package com.jiangzg.ita.adapter;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.WallPaper;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.ConvertHelper;
import com.jiangzg.ita.helper.DialogHelper;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.view.GImageView;

import java.util.ArrayList;
import java.util.Collections;
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
        float screenWidth = ScreenUtils.getScreenWidth(activity);
        float screenHeight = ScreenUtils.getScreenHeight(activity);
        ratio = screenWidth / screenHeight;
        imageWidth = (int) (screenWidth / 3);
        imageHeight = (int) (imageWidth / ratio);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        GImageView ivWallPaper = helper.getView(R.id.ivWallPaper);
        ivWallPaper.setAspectRatio(ratio);
        ivWallPaper.setWidthAndHeight(imageWidth, imageHeight);
        ivWallPaper.setDataOss(item);
    }

    public void goImgScreen(int position, GImageView view) {
        List<String> data = getData();
        ArrayList<Uri> uriList = ConvertHelper.convertListString2uri(data);
        // todo dialog形式
        //ImgScreenActivity.goActivity(mActivity, uriList, position, view);
    }

    public void showDeleteDialog(final int position) {
        MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
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
        DialogHelper.setAnim(dialog);
        DialogHelper.show(dialog);
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
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
