package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.AlbumEditActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GImageView;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 帮助列表适配器
 */
public class AlbumAdapter extends BaseQuickAdapter<Album, BaseViewHolder> {

    private final BaseActivity mActivity;
    private final int imageWidth;
    private final int imageHeight;

    public AlbumAdapter(BaseActivity activity) {
        super(R.layout.list_item_album);
        mActivity = activity;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        imageWidth = (int) (screenWidth / 2);
        imageHeight = ConvertUtils.dp2px(250);
    }

    @Override
    protected void convert(final BaseViewHolder helper, Album item) {
        // data
        String title = item.getTitle();
        String cover = item.getCover();
        // view
        helper.setText(R.id.tvTitle, title);
        GImageView ivAlbum = helper.getView(R.id.ivAlbum);
        ivAlbum.setWidthAndHeight(imageWidth, imageHeight);
        if (StringUtils.isEmpty(cover)) {
            // 没有封面时，随机给个颜色
            int randomColor = ViewHelper.getRandomThemePrimaryRes();
            ivAlbum.setDataRes(randomColor);
        } else {
            ivAlbum.setDataOss(cover);
        }
    }

    public void onLongPress(int position) {
        // TODO
        //goEdit(position);
        //showDeleteDialog(position);
    }

    public void goEdit(int position) {
        Album item = getData().get(position);
        AlbumEditActivity.goActivity(mActivity, item);
    }

    public void showDeleteDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.confirm_delete_this_album)
                .positiveText(R.string.confirm)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delAlbumApi(position);
                    }
                })
                .negativeText(R.string.cancel)
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delAlbumApi(final int position) {
        Album album = getData().get(position);
        Call<Result> call = new RetrofitHelper().call(API.class).AlbumDel(album.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                AlbumAdapter.this.remove(position);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
