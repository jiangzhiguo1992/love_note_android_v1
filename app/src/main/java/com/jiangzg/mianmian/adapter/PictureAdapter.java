package com.jiangzg.mianmian.adapter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.BigImageActivity;
import com.jiangzg.mianmian.activity.common.MapShowActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GImageView;

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

    public PictureAdapter(BaseActivity activity) {
        super(R.layout.list_item_picture);
        mActivity = activity;
        mModel = MODEL_IMAGE;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        int dp5 = ConvertUtils.dp2px(5);
        imageWidth = imageHeight = (int) (screenWidth / 2) - dp5 * 2;
        colorPrimary = ContextCompat.getColor(activity, ViewHelper.getColorPrimary(mActivity));
    }

    // 切换显示模式
    public void toggleModel() {
        mModel = (mModel == MODEL_DETAIL) ? MODEL_IMAGE : MODEL_DETAIL;
        this.notifyDataSetChanged();
    }

    @Override
    protected void convert(final BaseViewHolder helper, Picture item) {
        String happen = ConvertHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(item.getHappenAt());
        String address = item.getAddress();
        String content = item.getContent();
        // view
        helper.setVisible(R.id.tvHappenAt, mModel == MODEL_DETAIL);
        helper.setVisible(R.id.tvLocation, mModel == MODEL_DETAIL);
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvLocation, address);
        GImageView ivPicture = helper.getView(R.id.ivPicture);
        ivPicture.setWidthAndHeight(imageWidth, imageHeight);
        // 主色值设置
        ivPicture.setBitmapListener(new GImageView.BitmapListener() {
            @Override
            public void onBitmapSuccess(GImageView iv, Bitmap bitmap) {
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(@NonNull Palette palette) {
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
                            helper.setBackgroundColor(R.id.rlBg, rgb);
                        } else {
                            helper.setBackgroundColor(R.id.rlBg, colorPrimary);
                        }
                    }
                });
            }

            @Override
            public void onBitmapFail(GImageView iv) {
                helper.setBackgroundColor(R.id.rlBg, colorPrimary);
            }
        });
        // 为瀑布流定制不一样的宽高
        ivPicture.setLoadListener(new GImageView.LoadListener() {
            @Override
            public void onLoadSuccess(GImageView iv, ImageInfo imageInfo) {
                float height = imageInfo.getHeight();
                float width = imageInfo.getWidth();
                int finalHeight = (int) (height / width * imageWidth);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv.getLayoutParams();
                layoutParams.height = finalHeight;
                iv.setLayoutParams(layoutParams);
            }

            @Override
            public void onLoadFail(GImageView iv) {
            }
        });
        // 点击全屏
        ivPicture.setClickListener(new GImageView.ClickListener() {
            @Override
            public void onSuccessClick(GImageView iv) {
                List<Picture> data = PictureAdapter.this.getData();
                ArrayList<String> pathList = ConvertHelper.getStrListByPicture(data);
                if (pathList == null || pathList.size() <= 0) return;
                int position = helper.getLayoutPosition();
                BigImageActivity.goActivityByOssList(mActivity, pathList, position, iv);
            }
        });
        ivPicture.setDataOss(content);
        helper.addOnClickListener(R.id.tvLocation);
    }

    // 点击跳转地图
    public void onLocationClick(int position) {
        Picture item = getItem(position);
        String address = item.getAddress();
        double latitude = item.getLatitude();
        double longitude = item.getLongitude();
        MapShowActivity.goActivity(mActivity, address, latitude, longitude);
    }

    public void showDeleteDialog(final int position) {
        Picture item = getItem(position);
        if (!item.isMine()) return;
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
            public void onFailure(String errMsg) {
            }
        });
    }

}
