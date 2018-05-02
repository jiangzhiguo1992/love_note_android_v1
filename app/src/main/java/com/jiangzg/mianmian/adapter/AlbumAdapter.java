package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.AlbumEditActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.SPHelper;
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
    private int operationPosition;

    public AlbumAdapter(BaseActivity activity) {
        super(R.layout.list_item_album);
        mActivity = activity;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        imageWidth = (int) (screenWidth / 2);
        imageHeight = ConvertUtils.dp2px(250);
        operationPosition = -1;
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
        // listener
        helper.addOnClickListener(R.id.tvModify);
        helper.addOnClickListener(R.id.tvDelete);
        helper.addOnClickListener(R.id.tvCancel);
    }

    public void goDetail(int position) {
        hideOperation();
        // TODO 进入照片列表页
        ToastUtils.show("详情页 " + position);
    }

    public void showOperation(int position) {
        hideOperation();
        // 用户检查
        Album album = getData().get(position);
        if (album.getUserId() != SPHelper.getUser().getId()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_album));
            return;
        }
        // view
        LinearLayout llOperate = (LinearLayout) getViewByPosition(position, R.id.llOperate);
        llOperate.setVisibility(View.VISIBLE);
        // anim
        TextView tvModify = (TextView) getViewByPosition(position, R.id.tvModify);
        TextView tvDelete = (TextView) getViewByPosition(position, R.id.tvDelete);
        TextView tvCancel = (TextView) getViewByPosition(position, R.id.tvCancel);
        Animation modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_right_in_300);
        modify.setFillAfter(true);
        Animation delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_left_in_300);
        delete.setFillAfter(true);
        delete.setStartOffset(100);
        Animation cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_right_in_300);
        cancel.setFillAfter(true);
        cancel.setStartOffset(200);
        tvModify.startAnimation(modify);
        tvDelete.startAnimation(delete);
        tvCancel.startAnimation(cancel);
        // position
        operationPosition = position;
    }

    public void hideOperation() {
        if (operationPosition < 0 || operationPosition >= getData().size()) return;
        // anim
        TextView tvModify = (TextView) getViewByPosition(operationPosition, R.id.tvModify);
        TextView tvDelete = (TextView) getViewByPosition(operationPosition, R.id.tvDelete);
        TextView tvCancel = (TextView) getViewByPosition(operationPosition, R.id.tvCancel);
        Animation modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_left_out_300);
        modify.setFillAfter(true);
        Animation delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_right_out_300);
        delete.setFillAfter(true);
        delete.setStartOffset(100);
        Animation cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_left_out_300);
        cancel.setFillAfter(true);
        cancel.setStartOffset(200);
        tvModify.startAnimation(modify);
        tvDelete.startAnimation(delete);
        tvCancel.startAnimation(cancel);
        // view
        final LinearLayout llOperate = (LinearLayout) getViewByPosition(operationPosition, R.id.llOperate);
        cancel.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                llOperate.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        // position
        operationPosition = -1;
    }

    public void goEdit(int position) {
        hideOperation();
        Album item = getData().get(position);
        AlbumEditActivity.goActivity(mActivity, item);
    }

    public void showDeleteDialog(final int position) {
        hideOperation();
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
        hideOperation();
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
