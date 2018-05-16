package com.jiangzg.mianmian.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;

import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.DrawableUtils;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ProviderUtils;
import com.jiangzg.mianmian.helper.FrescoHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.OssResHelper;

import java.io.File;

/**
 * Created by JZG on 2018/3/21.
 * Fresco的图片控件封装
 */
public class GImageAvatarView extends SimpleDraweeView {

    private static final String LOG_TAG = "GImageAvatarView";

    private int mWidth, mHeight;

    public GImageAvatarView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init(context, null, hierarchy);
    }

    public GImageAvatarView(Context context) {
        super(context);
        init(context, null, null);
    }

    public GImageAvatarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, null);
    }

    public GImageAvatarView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, null);
    }

    public GImageAvatarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
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
        hierarchy.setFadeDuration(300);
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        hierarchy.setRoundingParams(RoundingParams.asCircle());
        hierarchy.setPlaceholderImage(new ImageLoadingCircleDrawable(), ScalingUtils.ScaleType.CENTER_CROP);
        hierarchy.setProgressBarImage(new ImageProgressCircleDrawable(), ScalingUtils.ScaleType.CENTER_INSIDE);
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
            }
        }
        // request
        ImageRequestBuilder requestBuilder = FrescoHelper.getImageRequestBuilder(uri, mWidth, mHeight);
        ImageRequest imageRequest = requestBuilder.build();
        // controller
        PipelineDraweeControllerBuilder builder = FrescoHelper.getPipelineControllerBuilder(this, uri, imageRequest);
        builder = builder.setTapToRetryEnabled(false); // 不支持重新加载
        builder.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null) {
                    LogUtils.i(LOG_TAG, "controllerListener: onFinalImageSet: imageInfo == null");
                    return;
                }
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                LogUtils.i(LOG_TAG, "setControllerListener: onFinalImageSet: " +
                        " width = " + imageInfo.getWidth() +
                        " height = " + imageInfo.getHeight() +
                        " quality = " + qualityInfo.getQuality() +
                        " goodEnoughQuality = " + qualityInfo.isOfGoodEnoughQuality() +
                        " fullQuality = " + qualityInfo.isOfFullQuality());
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                LogUtils.e(LOG_TAG, "controllerListener: onFailure: ", throwable);
            }
        });
        AbstractDraweeController controller = builder.build();
        this.setController(controller);
    }

    // GridList 和 match_parent 需要传，在设置数据源之前调用
    public void setWidthAndHeight(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void setData(String ossKey) {
        if (OssResHelper.isKeyFileExists(ossKey)) {
            File file = OssResHelper.newKeyFile(ossKey);
            if (!FileUtils.isFileEmpty(file)) {
                this.setDataFile(file);
            } else {
                this.setDataOss(ossKey);
            }
        } else {
            this.setDataOss(ossKey);
        }
    }

    // http:// https:// 需要现场获取oss的url
    private void setDataOss(String objPath) {
        String url = OssHelper.getUrl(objPath);
        Uri parse;
        if (StringUtils.isEmpty(url)) {
            parse = Uri.parse("");
        } else {
            parse = Uri.parse(url);
        }
        setController(parse);
    }

    // file://
    public void setDataFile(File file) {
        Uri parse = ProviderUtils.getUriByFile(file);
        setController(parse);
    }

    public class ImageLoadingCircleDrawable extends Drawable {

        private Paint mFillPaint;

        public ImageLoadingCircleDrawable() {
            mFillPaint = new Paint();
            mFillPaint.setAntiAlias(true);
            mFillPaint.setColor(Color.parseColor("#FF000000"));
            mFillPaint.setStyle(Paint.Style.FILL);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            drawBar(canvas, mFillPaint);
        }

        private void drawBar(Canvas canvas, Paint paint) {
            Rect bound = getBounds();
            int mXCenter = bound.centerX();
            int mYCenter = bound.centerY();
            int radius = getWidth() / 2;
            canvas.drawCircle(mXCenter, mYCenter, radius, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            mFillPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mFillPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    public class ImageProgressCircleDrawable extends Drawable {

        // 画背景的画笔
        private Paint mBackgroundPaint;
        // 画圆心的画笔
        private Paint mFillPaint;
        // 画圆环的画笔
        //private Paint mRingPaint;
        // 总进度
        private int mTotalProgress = 10000;
        // 当前进度
        private int mCurrentProgress;

        public ImageProgressCircleDrawable() {
            mBackgroundPaint = new Paint();
            mBackgroundPaint.setAntiAlias(true);
            mBackgroundPaint.setColor(Color.parseColor("#FF000000"));
            mBackgroundPaint.setStyle(Paint.Style.FILL);

            mFillPaint = new Paint();
            mFillPaint.setAntiAlias(true);
            mFillPaint.setColor(Color.parseColor("#77FFFFFF"));
            mFillPaint.setStyle(Paint.Style.FILL);

            //mRingPaint = new Paint();
            //mRingPaint.setAntiAlias(true);
            //mRingPaint.setColor(Color.parseColor("#77FFFFFF"));
            //mRingPaint.setStyle(Paint.Style.STROKE);
            //mRingPaint.setStrokeWidth(ConvertUtils.dp2px(2));
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            Rect bound = getBounds();
            int mXCenter = bound.centerX();
            int mYCenter = bound.centerY();
            int radius = getWidth() / 2;
            canvas.drawCircle(mXCenter, mYCenter, radius, mBackgroundPaint);

            //drawBar(canvas, mTotalProgress, mRingPaint);
            drawBar(canvas, mCurrentProgress, mFillPaint);
        }

        private void drawBar(Canvas canvas, int level, Paint paint) {
            if (level > 0) {
                float mRingRadius = getWidth() / 2;
                Rect bound = getBounds();
                int mXCenter = bound.centerX();
                int mYCenter = bound.centerY();

                RectF oval = new RectF();
                oval.left = (mXCenter - mRingRadius);
                oval.top = (mYCenter - mRingRadius);
                oval.right = mRingRadius * 2 + (mXCenter - mRingRadius);
                oval.bottom = mRingRadius * 2 + (mYCenter - mRingRadius);
                canvas.drawArc(oval, -90, ((float) level / mTotalProgress) * 360, true, paint);
            }
        }

        @Override
        protected boolean onLevelChange(int level) {
            mCurrentProgress = level;
            if (level > 0 && level < mTotalProgress) {
                invalidateSelf();
                return true;
            }
            return false;
        }

        @Override
        public void setAlpha(int alpha) {
            mFillPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mFillPaint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return DrawableUtils.getOpacityFromColor(this.mFillPaint.getColor());
        }
    }

}
