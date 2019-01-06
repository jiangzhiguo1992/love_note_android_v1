package com.jiangzg.lovenote.view;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ProviderUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.helper.common.ResHelper;
import com.jiangzg.lovenote.helper.view.FrescoHelper;
import com.jiangzg.lovenote.main.MyApp;

import java.io.File;

/**
 * Created by JZG on 2018/3/21.
 * Fresco的图片控件封装
 */
public class FrescoNativeView extends SimpleDraweeView {

    private int mWidth, mHeight;
    private onSuccessClickListener mSuccessClickListener;

    public FrescoNativeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init(context, null, hierarchy);
    }

    public FrescoNativeView(Context context) {
        super(context);
        init(context, null, null);
    }

    public FrescoNativeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, null);
    }

    public FrescoNativeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, null);
    }

    public FrescoNativeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, null);
    }

    private void init(Context context, AttributeSet attrs, GenericDraweeHierarchy h) {
        initHierarchy(h);
    }

    private void initHierarchy(GenericDraweeHierarchy h) {
        GenericDraweeHierarchy hierarchy;
        if (h != null) {
            hierarchy = h;
        } else {
            hierarchy = this.getHierarchy();
            if (hierarchy == null) {
                hierarchy = new GenericDraweeHierarchyBuilder(getResources()).build();
                this.setHierarchy(hierarchy);
            }
        }
        hierarchy.setFadeDuration(0);
        //hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP); // 在xml里设置
    }

    private void setController(Uri uri) {
        if (mWidth <= 0 || mHeight <= 0) {
            // 获取宽高
            mWidth = getWidth();
            mHeight = getHeight();
            if (mWidth <= 0 || mHeight <= 0) {
                ViewGroup.LayoutParams params = getLayoutParams();
                if (params != null) {
                    mWidth = params.width;
                    mHeight = params.height;
                }
                if (mWidth <= 0 || mHeight <= 0) {
                    mWidth = ScreenUtils.getScreenWidth(MyApp.get()) / 10;
                    mHeight = ScreenUtils.getScreenHeight(MyApp.get()) / 15;
                }
            }
        }
        // request
        ImageRequestBuilder requestBuilder = FrescoHelper.getImageRequestBuilder(uri, mWidth, mHeight);
        ImageRequest imageRequest = requestBuilder.build();
        // controller
        PipelineDraweeControllerBuilder builder = FrescoHelper.getPipelineControllerBuilder(this, uri, imageRequest);
        builder = builder.setTapToRetryEnabled(false); // 非网络图不支持重新加载
        builder.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null) {
                    LogUtils.w(FrescoNativeView.class, "onFinalImageSet", "imageInfo == null");
                    return;
                }
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                LogUtils.d(FrescoNativeView.class, "onFinalImageSet",
                        " width = " + imageInfo.getWidth() +
                                " height = " + imageInfo.getHeight() +
                                " quality = " + qualityInfo.getQuality() +
                                " goodEnoughQuality = " + qualityInfo.isOfGoodEnoughQuality() +
                                " fullQuality = " + qualityInfo.isOfFullQuality());
                // 点击事件
                if (mSuccessClickListener != null) {
                    FrescoNativeView.this.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSuccessClickListener.onClick(FrescoNativeView.this);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                LogUtils.w(FrescoNativeView.class, "onFailure", throwable.getMessage());
                ToastUtils.show(MyApp.get().getString(R.string.image_load_fail));
            }
        });
        AbstractDraweeController controller = builder.build();
        this.setController(controller);
    }

    public interface onSuccessClickListener {
        void onClick(FrescoNativeView iv);
    }

    public void setSuccessClickListener(onSuccessClickListener listener) {
        mSuccessClickListener = listener;
    }

    // GridList 和 match_parent 需要传，在设置数据源之前调用
    public void setWidthAndHeight(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    // file://
    public void setDataFile(File file) {
        if (file == null) return;
        Uri parse = ProviderUtils.getUriByFile(ResHelper.PROVIDER_AUTH, file);
        setController(parse);
    }

    // "res://" + AppInfo.get().getPackageName() + "/" + id
    public void setDataRes(@AnyRes int id) {
        setImageResource(id);
    }

}
