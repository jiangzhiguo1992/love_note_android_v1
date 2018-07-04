package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
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
import com.jiangzg.mianmian.activity.book.PictureListActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.FrescoView;

import java.util.Locale;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 相册适配器
 */
public class AlbumAdapter extends BaseQuickAdapter<Album, BaseViewHolder> {

    private final BaseActivity mActivity;
    private final int imageWidth;
    private final int imageHeight;
    private int operationPosition;
    private final String formatTime;

    public AlbumAdapter(BaseActivity activity) {
        super(R.layout.list_item_album);
        mActivity = activity;
        imageWidth = ScreenUtils.getScreenRealWidth(activity);
        imageHeight = ConvertUtils.dp2px(170);
        operationPosition = -1;

        formatTime = mActivity.getString(R.string.holder_space_line_space_holder);
    }

    @Override
    protected void convert(final BaseViewHolder helper, Album item) {
        // data
        String title = item.getTitle();
        long startAt = item.getStartAt();
        String startTime = (startAt == 0) ? "      " : TimeHelper.getTimeShowCn_MD_YMD_ByGo(startAt);
        long endAt = item.getEndAt();
        String endTime = (endAt == 0) ? "      " : TimeHelper.getTimeShowCn_MD_YMD_ByGo(endAt);
        String time = String.format(Locale.getDefault(), formatTime, startTime, endTime);
        String cover = item.getCover();
        // view
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvTime, time);
        FrescoView ivAlbum = helper.getView(R.id.ivAlbum);
        ivAlbum.setWidthAndHeight(imageWidth, imageHeight);
        if (StringUtils.isEmpty(cover)) {
            // 没有封面时，随机给个颜色
            int randomColor = ViewHelper.getRandomThemePrimaryRes();
            ivAlbum.setDataRes(randomColor);
        } else {
            ivAlbum.setData(cover);
        }
        // listener
        helper.addOnClickListener(R.id.tvModify);
        helper.addOnClickListener(R.id.tvDelete);
        helper.addOnClickListener(R.id.tvCancel);
    }

    public void selectAlbum(int position) {
        mActivity.finish(); // 必须先关闭
        Album item = getItem(position);
        RxEvent<Album> event = new RxEvent<>(ConsHelper.EVENT_ALBUM_SELECT, item);
        RxBus.post(event);
    }

    public void selectPicture(int position) {
        hideOperation();
        Album item = getItem(position);
        PictureListActivity.goActivityBySelect(mActivity, item);
    }

    public void goAlbumDetail(int position) {
        hideOperation();
        Album item = getItem(position);
        PictureListActivity.goActivity(mActivity, item);
    }

    public void showOperation(int position) {
        hideOperation();
        // 用户检查
        Album album = getItem(position);
        if (!album.isMine()) {
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
        Animation modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_in_100);
        modify.setInterpolator(new DecelerateInterpolator());
        modify.setFillAfter(true);
        Animation delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_in_100);
        delete.setInterpolator(new DecelerateInterpolator());
        delete.setFillAfter(true);
        delete.setStartOffset(50);
        Animation cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_in_100);
        cancel.setInterpolator(new DecelerateInterpolator());
        cancel.setFillAfter(true);
        cancel.setStartOffset(100);
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
        Animation modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_out_100);
        modify.setInterpolator(new DecelerateInterpolator());
        modify.setFillAfter(true);
        Animation delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_out_100);
        delete.setInterpolator(new DecelerateInterpolator());
        delete.setFillAfter(true);
        delete.setStartOffset(50);
        Animation cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_out_100);
        cancel.setInterpolator(new DecelerateInterpolator());
        cancel.setFillAfter(true);
        cancel.setStartOffset(100);
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
        Album item = getItem(position);
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
        Album album = getItem(position);
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

    public void showDeleteDialogNoApi(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_remove_this_album)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        remove(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

}
