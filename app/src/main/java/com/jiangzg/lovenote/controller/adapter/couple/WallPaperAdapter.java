package com.jiangzg.lovenote.controller.adapter.couple;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.BigImageActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.WallPaper;
import com.jiangzg.lovenote.view.FrescoView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 墙纸适配器
 */
public class WallPaperAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private final BaseActivity mActivity;
    private final float ratio;
    private final int imageWidth;
    private final int imageHeight;

    public WallPaperAdapter(BaseActivity activity) {
        super(R.layout.list_item_wall_paper);
        mActivity = activity;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        float screenHeight = ScreenUtils.getScreenRealHeight(activity) - BarUtils.getTopBarHeight(mActivity);
        ratio = screenWidth / screenHeight;
        imageWidth = (int) (screenWidth / 3);
        imageHeight = (int) (screenHeight / 3);
    }

    @Override
    protected void convert(final BaseViewHolder helper, String item) {
        FrescoView ivWallPaper = helper.getView(R.id.ivWallPaper);
        ivWallPaper.setAspectRatio(ratio);
        ivWallPaper.setWidthAndHeight(imageWidth / 2, imageHeight / 2);
        ivWallPaper.setClickListener(iv -> goImgScreen(helper.getLayoutPosition(), iv));
        // 优先加载本地文件
        ivWallPaper.setData(item);
    }

    private void goImgScreen(int position, FrescoView view) {
        List<String> data = getData();
        BigImageActivity.goActivityByOssList(mActivity, (ArrayList<String>) data, position, view);
    }

    public void showDeleteDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.confirm_delete_this_image)
                .positiveText(R.string.confirm)
                .onPositive((dialog1, which) -> delWallPaper(position))
                .negativeText(R.string.cancel)
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delWallPaper(final int position) {
        WallPaper body = SPHelper.getWallPaper();
        if (body.getContentImageList() == null) {
            body.setContentImageList(new ArrayList<>());
        }
        if (body.getContentImageList().size() > position) {
            body.getContentImageList().remove(position);
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).coupleWallPaperUpdate(body);
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                WallPaper wallPaper = data.getWallPaper();
                SPHelper.setWallPaper(wallPaper);
                if (WallPaperAdapter.this.getData().size() > position) {
                    WallPaperAdapter.this.remove(position);
                }
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_WALL_PAPER_REFRESH, wallPaper));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

}
