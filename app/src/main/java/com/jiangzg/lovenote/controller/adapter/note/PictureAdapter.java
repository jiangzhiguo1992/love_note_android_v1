package com.jiangzg.lovenote.controller.adapter.note;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.BigImageActivity;
import com.jiangzg.lovenote.controller.activity.common.MapShowActivity;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Picture;
import com.jiangzg.lovenote.view.FrescoView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 照片列表适配器
 */
public class PictureAdapter extends BaseQuickAdapter<Picture, BaseViewHolder> {

    private static final int MODEL_IMAGE = 0;
    private static final int MODEL_DETAIL = 1;

    private final BaseActivity mActivity;
    private final int imageWidth, imageHeight, minHeight;
    private final int colorPrimary;
    private int mModel;
    //private int operationPosition;

    public PictureAdapter(BaseActivity activity) {
        super(R.layout.list_item_picture);
        mActivity = activity;
        mModel = MODEL_IMAGE;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        int dp5 = ConvertUtils.dp2px(5);
        imageWidth = imageHeight = (int) (screenWidth / 2) - dp5 * 2;
        minHeight = ConvertUtils.dp2px(100);
        //operationPosition = -1;
        colorPrimary = ContextCompat.getColor(activity, ViewUtils.getColorPrimary(mActivity));
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
        //helper.setVisible(R.id.llOperateHorizontal, false);
        //helper.setVisible(R.id.llOperateVertical, false);
        if (mModel != MODEL_DETAIL) {
            helper.setVisible(R.id.tvHappenAt, false);
            helper.setVisible(R.id.vLine, false);
            helper.setVisible(R.id.tvAddress, false);
        } else {
            helper.setVisible(R.id.tvHappenAt, true);
            helper.setVisible(R.id.vLine, !StringUtils.isEmpty(address));
            helper.setVisible(R.id.tvAddress, !StringUtils.isEmpty(address));
            helper.setText(R.id.tvHappenAt, happen);
            helper.setText(R.id.tvAddress, address);
        }
        FrescoView ivPicture = helper.getView(R.id.ivPicture);
        // 按版本来采样吧，低版本不能太大
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ivPicture.setWidthAndHeight(imageWidth / 2, imageHeight / 2);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ivPicture.setWidthAndHeight(imageWidth / 3, imageHeight / 3);
        } else {
            ivPicture.setWidthAndHeight(imageWidth / 4, imageHeight / 4);
        }
        // 主色值设置
        ivPicture.setBitmapListener(new FrescoView.BitmapListener() {
            @Override
            public void onBitmapSuccess(FrescoView iv, Bitmap bitmap) {
                Palette.from(bitmap).generate(palette -> {
                    int rgb = 0;
                    Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                    if (vibrantSwatch != null) {
                        rgb = vibrantSwatch.getRgb();
                    } else {
                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                        if (mutedSwatch != null) {
                            rgb = mutedSwatch.getRgb();
                        }
                    }
                    if (rgb != 0) {
                        helper.setBackgroundColor(R.id.llBottom, rgb);
                    } else {
                        helper.setBackgroundColor(R.id.llBottom, colorPrimary);
                    }
                });
            }

            @Override
            public void onBitmapFail(FrescoView iv) {
                helper.setBackgroundColor(R.id.llBottom, colorPrimary);
            }
        });
        // 为瀑布流定制不一样的宽高(这里起始高度一定不能wrap，否则会一开始就都加在)
        ivPicture.setLoadListener(new FrescoView.LoadListener() {
            @Override
            public void onLoadSuccess(FrescoView iv, ImageInfo imageInfo) {
                float width = imageInfo.getWidth();
                float height = imageInfo.getHeight();
                int finalHeight = (int) (height / width * imageWidth);
                if (finalHeight < minHeight) finalHeight = minHeight;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv.getLayoutParams();
                layoutParams.height = finalHeight;
                iv.setLayoutParams(layoutParams);
            }

            @Override
            public void onLoadFail(FrescoView iv) {
            }
        });
        // 点击全屏
        ivPicture.setClickListener(iv -> {
            List<Picture> data = PictureAdapter.this.getData();
            ArrayList<String> ossKeyList = PictureAdapter.getOssKeyListByPicture(data);
            int position = helper.getLayoutPosition();
            BigImageActivity.goActivityByOssList(mActivity, ossKeyList, position, iv);
        });
        ivPicture.setData(content);
        // listener
        helper.addOnClickListener(R.id.tvAddress);
        //helper.addOnClickListener(R.id.tvModifyVertical);
        //helper.addOnClickListener(R.id.tvDeleteVertical);
        //helper.addOnClickListener(R.id.tvCancelVertical);
        //helper.addOnClickListener(R.id.tvModifyHorizontal);
        //helper.addOnClickListener(R.id.tvDeleteHorizontal);
        //helper.addOnClickListener(R.id.tvCancelHorizontal);
    }

    // 点击跳转地图
    public void onLocationClick(int position) {
        Picture item = getItem(position);
        String address = item.getAddress();
        double longitude = item.getLongitude();
        double latitude = item.getLatitude();
        MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

    public void showDeleteDialog(final int position) {
        //hideOperation();
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_note)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> deleteApi(position))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi(int position) {
        final Picture item = getItem(position);
        Call<Result> api = new RetrofitHelper().call(API.class).notePictureDel(item.getId());
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_PICTURE_LIST_ITEM_DELETE, item));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

    //public void goEdit(int position, Album album) {
    //    hideOperation();
    //    if (album == null) return;
    //    Picture item = getItem(position);
    //    PictureEditActivity.goActivity(mActivity, album, item);
    //}
    //
    //public void showOperation(final int position) {
    //    hideOperation();
    //    // 用户检查
    //    Picture item = getItem(position);
    //    if (!item.isMine()) {
    //        ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_note));
    //        return;
    //    }
    //    // view
    //    LinearLayout llOperate;
    //    TextView tvModify, tvDelete, tvCancel;
    //    Animation modify, delete, cancel;
    //    FrescoView ivPicture = (FrescoView) getViewByPosition(position, R.id.ivPicture);
    //    if (ivPicture == null || ivPicture.getLayoutParams() == null) return;
    //    if (ivPicture.getLayoutParams().height < imageWidth) {
    //        // 展示横向
    //        llOperate = (LinearLayout) getViewByPosition(position, R.id.llOperateHorizontal);
    //        tvModify = (TextView) getViewByPosition(position, R.id.tvModifyHorizontal);
    //        tvDelete = (TextView) getViewByPosition(position, R.id.tvDeleteHorizontal);
    //        tvCancel = (TextView) getViewByPosition(position, R.id.tvCancelHorizontal);
    //        modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_in_100);
    //        delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_in_100);
    //        cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_in_100);
    //    } else {
    //        // 展示竖向
    //        llOperate = (LinearLayout) getViewByPosition(position, R.id.llOperateVertical);
    //        tvModify = (TextView) getViewByPosition(position, R.id.tvModifyVertical);
    //        tvDelete = (TextView) getViewByPosition(position, R.id.tvDeleteVertical);
    //        tvCancel = (TextView) getViewByPosition(position, R.id.tvCancelVertical);
    //        modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_right_in_100);
    //        delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_left_in_100);
    //        cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_right_in_100);
    //    }
    //    llOperate.setVisibility(View.VISIBLE);
    //    // anim
    //    modify.setInterpolator(new DecelerateInterpolator());
    //    modify.setFillAfter(true);
    //    delete.setInterpolator(new DecelerateInterpolator());
    //    delete.setFillAfter(true);
    //    delete.setStartOffset(50);
    //    cancel.setInterpolator(new DecelerateInterpolator());
    //    cancel.setFillAfter(true);
    //    cancel.setStartOffset(100);
    //    tvModify.startAnimation(modify);
    //    tvDelete.startAnimation(delete);
    //    tvCancel.startAnimation(cancel);
    //    // position
    //    operationPosition = position;
    //}
    //
    //public void hideOperation() {
    //    if (operationPosition < 0 || operationPosition >= getData().size()) return;
    //    // anim
    //    // view
    //    final LinearLayout llOperate;
    //    TextView tvModify, tvDelete, tvCancel;
    //    Animation modify, delete, cancel;
    //    FrescoView ivPicture = (FrescoView) getViewByPosition(operationPosition, R.id.ivPicture);
    //    if (ivPicture == null || ivPicture.getLayoutParams() == null) return;
    //    if (ivPicture.getLayoutParams().height < imageWidth) {
    //        // 展示横向
    //        llOperate = (LinearLayout) getViewByPosition(operationPosition, R.id.llOperateHorizontal);
    //        tvModify = (TextView) getViewByPosition(operationPosition, R.id.tvModifyHorizontal);
    //        tvDelete = (TextView) getViewByPosition(operationPosition, R.id.tvDeleteHorizontal);
    //        tvCancel = (TextView) getViewByPosition(operationPosition, R.id.tvCancelHorizontal);
    //        modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_out_100);
    //        delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_out_100);
    //        cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_bottom_out_100);
    //    } else {
    //        // 展示竖向
    //        llOperate = (LinearLayout) getViewByPosition(operationPosition, R.id.llOperateVertical);
    //        tvModify = (TextView) getViewByPosition(operationPosition, R.id.tvModifyVertical);
    //        tvDelete = (TextView) getViewByPosition(operationPosition, R.id.tvDeleteVertical);
    //        tvCancel = (TextView) getViewByPosition(operationPosition, R.id.tvCancelVertical);
    //        modify = AnimationUtils.loadAnimation(mActivity, R.anim.slide_left_out_100);
    //        delete = AnimationUtils.loadAnimation(mActivity, R.anim.slide_right_out_100);
    //        cancel = AnimationUtils.loadAnimation(mActivity, R.anim.slide_left_out_100);
    //    }
    //    modify.setInterpolator(new DecelerateInterpolator());
    //    modify.setFillAfter(true);
    //    delete.setInterpolator(new DecelerateInterpolator());
    //    delete.setFillAfter(true);
    //    delete.setStartOffset(50);
    //    cancel.setInterpolator(new DecelerateInterpolator());
    //    cancel.setFillAfter(true);
    //    cancel.setStartOffset(100);
    //    tvModify.startAnimation(modify);
    //    tvDelete.startAnimation(delete);
    //    tvCancel.startAnimation(cancel);
    //    // view
    //    cancel.setAnimationListener(new Animation.AnimationListener() {
    //        @Override
    //        public void onAnimationStart(Animation animation) {
    //        }
    //
    //        @Override
    //        public void onAnimationEnd(Animation animation) {
    //            llOperate.setVisibility(View.GONE);
    //        }
    //
    //        @Override
    //        public void onAnimationRepeat(Animation animation) {
    //        }
    //    });
    //    // position
    //    operationPosition = -1;
    //}

    // 集合类型转换(Picture -> ossKey)
    public static ArrayList<String> getOssKeyListByPicture(List<Picture> pictureList) {
        ArrayList<String> ossKeyList = new ArrayList<>();
        if (pictureList == null || pictureList.size() <= 0) return ossKeyList;
        for (Picture picture : pictureList) {
            if (picture == null || StringUtils.isEmpty(picture.getContentImage())) {
                continue;
            }
            ossKeyList.add(picture.getContentImage());
        }
        return ossKeyList;
    }

}
