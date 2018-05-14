package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.BigImageActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.WallPaper;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.view.GImageView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 帮助列表适配器
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
        float screenHeight = ScreenUtils.getScreenRealHeight(activity);
        ratio = screenWidth / screenHeight;
        imageWidth = (int) (screenWidth / 3);
        imageHeight = (int) (imageWidth / ratio);
    }

    @Override
    protected void convert(final BaseViewHolder helper, String item) {
        GImageView ivWallPaper = helper.getView(R.id.ivWallPaper);
        ivWallPaper.setAspectRatio(ratio);
        ivWallPaper.setWidthAndHeight(imageWidth, imageHeight);
        ivWallPaper.setClickListener(new GImageView.ClickListener() {
            @Override
            public void onSuccessClick(GImageView iv) {
                goImgScreen(helper.getLayoutPosition(), iv);
            }
        });
        // 优先加载本地文件
        ivWallPaper.setDateWallPaper(item);
    }

    private void goImgScreen(int position, GImageView view) {
        List<String> data = getData();
        BigImageActivity.goActivityByOssList(mActivity, (ArrayList<String>) data, position, view);
    }

    public void showDeleteDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.confirm_delete_this_image)
                .positiveText(R.string.confirm)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delImgApi(position);
                    }
                })
                .negativeText(R.string.cancel)
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delImgApi(final int position) {
        List<String> data = getData();
        if (data.size() <= position) return;
        List<String> objects = new ArrayList<>();
        objects.addAll(data);
        objects.remove(position);
        WallPaper body = ApiHelper.getWallPaperUpdateBody(objects);
        Call<Result> call = new RetrofitHelper().call(API.class).coupleWallPaperUpdate(body);
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                WallPaperAdapter.this.remove(position);
                SPHelper.setWallPaper(data.getWallPaper());
                // event
                RxEvent<WallPaper> event = new RxEvent<>(ConsHelper.EVENT_WALL_PAPER_REFRESH, data.getWallPaper());
                RxBus.post(event);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
