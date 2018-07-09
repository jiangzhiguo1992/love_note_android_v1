package com.jiangzg.mianmian.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.PictureEditActivity;
import com.jiangzg.mianmian.activity.common.BigImageActivity;
import com.jiangzg.mianmian.activity.common.MapShowActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Album;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.FrescoView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 图片列表适配器
 */
public class PictureAdapter extends BaseQuickAdapter<Picture, BaseViewHolder> {

    private static final int MODEL_IMAGE = 0;
    private static final int MODEL_DETAIL = 1;

    private final BaseActivity mActivity;
    private int mModel;
    private final int imageWidth, imageHeight;
    private final int colorPrimary;
    private int operationPosition;

    public PictureAdapter(BaseActivity activity) {
        super(R.layout.list_item_picture);
        mActivity = activity;
        mModel = MODEL_IMAGE;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        int dp5 = ConvertUtils.dp2px(5);
        imageWidth = imageHeight = (int) (screenWidth / 2) - dp5 * 2;
        operationPosition = -1;
        colorPrimary = ContextCompat.getColor(activity, ViewHelper.getColorPrimary(mActivity));
    }

    // 切换显示模式
    public void toggleModel() {
        mModel = (mModel == MODEL_DETAIL) ? MODEL_IMAGE : MODEL_DETAIL;
        this.notifyDataSetChanged();
    }

    @Override
    protected void convert(final BaseViewHolder helper, Picture item) {
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String address = item.getAddress();
        String content = item.getContentImage();
        // view
        if (mModel != MODEL_DETAIL) {
            helper.setVisible(R.id.tvHappenAt, false);
            helper.setVisible(R.id.tvAddress, false);
        } else {
            helper.setVisible(R.id.tvHappenAt, item.getHappenAt() != 0);
            helper.setVisible(R.id.tvAddress, !StringUtils.isEmpty(address));
        }
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvAddress, address);
        FrescoView ivPicture = helper.getView(R.id.ivPicture);
        ivPicture.setWidthAndHeight(imageWidth / 3, imageHeight / 3); // 注意了啊，太高了会崩
        // 主色值设置
        ivPicture.setBitmapListener(new FrescoView.BitmapListener() {
            @Override
            public void onBitmapSuccess(FrescoView iv, Bitmap bitmap) {
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@NonNull Palette palette) {
                        int rgb = 0;
                        if (palette != null) {
                            Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                            if (vibrantSwatch != null) {
                                rgb = vibrantSwatch.getRgb();
                            } else {
                                Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                                if (mutedSwatch != null) {
                                    rgb = mutedSwatch.getRgb();
                                }
                            }
                        }
                        if (rgb != 0) {
                            helper.setBackgroundColor(R.id.tvHappenAt, rgb);
                            helper.setBackgroundColor(R.id.tvAddress, rgb);
                        } else {
                            helper.setBackgroundColor(R.id.tvHappenAt, colorPrimary);
                            helper.setBackgroundColor(R.id.tvAddress, colorPrimary);
                        }
                    }
                });
            }

            @Override
            public void onBitmapFail(FrescoView iv) {
                helper.setBackgroundColor(R.id.tvHappenAt, colorPrimary);
                helper.setBackgroundColor(R.id.tvAddress, colorPrimary);
            }
        });
        // 为瀑布流定制不一样的宽高(这里起始高度一定不能wrap，否则会一开始就都加在)
        ivPicture.setLoadListener(new FrescoView.LoadListener() {
            @Override
            public void onLoadSuccess(FrescoView iv, ImageInfo imageInfo) {
                float width = imageInfo.getWidth();
                float height = imageInfo.getHeight();
                int finalHeight = (int) (height / width * imageWidth);
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) iv.getLayoutParams();
                layoutParams.height = finalHeight;
                iv.setLayoutParams(layoutParams);
            }

            @Override
            public void onLoadFail(FrescoView iv) {
            }
        });
        // 点击全屏
        ivPicture.setClickListener(new FrescoView.ClickListener() {
            @Override
            public void onSuccessClick(FrescoView iv) {
                List<Picture> data = PictureAdapter.this.getData();
                ArrayList<String> ossKeyList = ListHelper.getOssKeyListByPicture(data);
                if (ossKeyList == null || ossKeyList.size() <= 0) return;
                int position = helper.getLayoutPosition();
                BigImageActivity.goActivityByOssList(mActivity, ossKeyList, position, iv);
            }
        });
        ivPicture.setData(content);
        // listener
        helper.addOnClickListener(R.id.tvAddress);
        helper.addOnClickListener(R.id.tvModifyVertical);
        helper.addOnClickListener(R.id.tvDeleteVertical);
        helper.addOnClickListener(R.id.tvCancelVertical);
        helper.addOnClickListener(R.id.tvModifyHorizontal);
        helper.addOnClickListener(R.id.tvDeleteHorizontal);
        helper.addOnClickListener(R.id.tvCancelHorizontal);
    }

    public void selectPicture(int position) {
        mActivity.finish(); // 必须先关闭
        Picture item = getItem(position);
        RxEvent<Picture> event = new RxEvent<>(ConsHelper.EVENT_PICTURE_SELECT, item);
        RxBus.post(event);
    }

    // 点击跳转地图
    public void onLocationClick(int position) {
        hideOperation();
        Picture item = getItem(position);
        String address = item.getAddress();
        double longitude = item.getLongitude();
        double latitude = item.getLatitude();
        MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

    public void showOperation(final int position) {
        hideOperation();
        // 用户检查
        Picture item = getItem(position);
        if (!item.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_picture));
            return;
        }
        // view
        LinearLayout llOperate;
        TextView tvModify, tvDelete, tvCancel;
        Animation modify, delete, cancel;
        FrescoView ivPicture = (FrescoView) getViewByPosition(position, R.id.ivPicture);
        if (ivPicture.getLayoutParams().height < imageWidth) {
            // 展示横向
            llOperate = (LinearLayout) getViewByPosition(position, R.id.llOperateHorizontal);
            tvModify = (TextView) getViewByPosition(position, R.id.tvModifyHorizontal);
            tvDelete = (TextView) getViewByPosition(position, R.id.tvDeleteHorizontal);
            tvCancel = (TextView) getViewByPosition(position, R.id.tvCancelHorizontal);
            modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_in_100);
            delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_in_100);
            cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_in_100);
        } else {
            // 展示竖向
            llOperate = (LinearLayout) getViewByPosition(position, R.id.llOperateVertical);
            tvModify = (TextView) getViewByPosition(position, R.id.tvModifyVertical);
            tvDelete = (TextView) getViewByPosition(position, R.id.tvDeleteVertical);
            tvCancel = (TextView) getViewByPosition(position, R.id.tvCancelVertical);
            modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_right_in_100);
            delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_left_in_100);
            cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_right_in_100);
        }
        llOperate.setVisibility(View.VISIBLE);
        // anim
        modify.setInterpolator(new DecelerateInterpolator());
        modify.setFillAfter(true);
        delete.setInterpolator(new DecelerateInterpolator());
        delete.setFillAfter(true);
        delete.setStartOffset(50);
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
        // view
        final LinearLayout llOperate;
        TextView tvModify, tvDelete, tvCancel;
        Animation modify, delete, cancel;
        FrescoView ivPicture = (FrescoView) getViewByPosition(operationPosition, R.id.ivPicture);
        if (ivPicture.getLayoutParams().height < imageWidth) {
            // 展示横向
            llOperate = (LinearLayout) getViewByPosition(operationPosition, R.id.llOperateHorizontal);
            tvModify = (TextView) getViewByPosition(operationPosition, R.id.tvModifyHorizontal);
            tvDelete = (TextView) getViewByPosition(operationPosition, R.id.tvDeleteHorizontal);
            tvCancel = (TextView) getViewByPosition(operationPosition, R.id.tvCancelHorizontal);
            modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_out_100);
            delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_out_100);
            cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_out_100);
        } else {
            // 展示竖向
            llOperate = (LinearLayout) getViewByPosition(operationPosition, R.id.llOperateVertical);
            tvModify = (TextView) getViewByPosition(operationPosition, R.id.tvModifyVertical);
            tvDelete = (TextView) getViewByPosition(operationPosition, R.id.tvDeleteVertical);
            tvCancel = (TextView) getViewByPosition(operationPosition, R.id.tvCancelVertical);
            modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_left_out_100);
            delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_right_out_100);
            cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_left_out_100);
        }
        modify.setInterpolator(new DecelerateInterpolator());
        modify.setFillAfter(true);
        delete.setInterpolator(new DecelerateInterpolator());
        delete.setFillAfter(true);
        delete.setStartOffset(50);
        cancel.setInterpolator(new DecelerateInterpolator());
        cancel.setFillAfter(true);
        cancel.setStartOffset(100);
        tvModify.startAnimation(modify);
        tvDelete.startAnimation(delete);
        tvCancel.startAnimation(cancel);
        // view
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

    public void goEdit(int position, Album album) {
        hideOperation();
        if (album == null) return;
        Picture item = getItem(position);
        PictureEditActivity.goActivity(mActivity, album, item);
    }

    public void showDeleteDialog(final int position) {
        hideOperation();
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_picture)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteApi(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi(int position) {
        final Picture item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).pictureDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Picture> event = new RxEvent<>(ConsHelper.EVENT_PICTURE_LIST_ITEM_DELETE, item);
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
