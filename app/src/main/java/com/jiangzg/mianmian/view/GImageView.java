package com.jiangzg.mianmian.view;


import android.app.Application;
import android.content.Context;
import android.content.res.TypedArray;
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
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.cache.disk.FileCache;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.DrawableUtils;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.ResHelper;

import java.io.File;

/**
 * Created by JZG on 2018/3/21.
 * Fresco的图片控件封装
 */
public class GImageView extends SimpleDraweeView {

    private static final String LOG_TAG = "GImageView";

    // 初始化
    public static void init(Application app, boolean debug) {
        // 网络图的缓存key
        CacheKeyFactory keyFactory = new DefaultCacheKeyFactory() {
            @Override
            protected Uri getCacheKeySourceUri(Uri sourceUri) {
                if (sourceUri == null) return null;
                String key = sourceUri.toString();
                String cacheKey = ConvertHelper.convertUrl2OssPath(key);
                return Uri.parse(cacheKey);
            }
        };
        // 设置缓存目录
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(app)
                .setBaseDirectoryPath(ResHelper.getFrescoCacheDir())
                .build();
        // 初始化配置
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(app)
                .setCacheKeyFactory(keyFactory)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig()) // 渐进式实现
                .setDownsampleEnabled(true) // 向下采样
                .build();
        // 开始初始化
        Fresco.initialize(app, config);
        if (debug) {
            FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        }
    }

    public static void clearMemoryCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
    }

    public static void clearDiskCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearDiskCaches();
    }

    public static long getDiskCachesSize() {
        FileCache mainFileCache = Fresco.getImagePipelineFactory().getMainFileCache();
        mainFileCache.trimToMinimum(); // 防止大小为-1
        return mainFileCache.getSize();
    }

    private int mWidth, mHeight;
    private boolean isNetCircle, isNetFull, isNetNormal;
    private onSuccessClickListener mSuccessClickListener;

    public GImageView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init(context, null, hierarchy);
    }

    public GImageView(Context context) {
        super(context);
        init(context, null, null);
    }

    public GImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, null);
    }

    public GImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, null);
    }

    public GImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, null);
    }

    private void init(Context context, AttributeSet attrs, GenericDraweeHierarchy h) {
        initAttrs(context, attrs);
        initHierarchy(h);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GImageView);
        isNetCircle = a.getBoolean(R.styleable.GImageView_is_net_circle, false);
        isNetFull = a.getBoolean(R.styleable.GImageView_is_net_full, false);
        isNetNormal = a.getBoolean(R.styleable.GImageView_is_net_normal, false);
        a.recycle();
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
        if (isNetCircle) { // 网络头像
            hierarchy.setFadeDuration(300);
            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            hierarchy.setRoundingParams(RoundingParams.asCircle());
            hierarchy.setPlaceholderImage(new ImageLoadingCircleDrawable(), ScalingUtils.ScaleType.CENTER_CROP);
            hierarchy.setProgressBarImage(new ImageProgressCircleDrawable(), ScalingUtils.ScaleType.CENTER_INSIDE);
        } else if (isNetFull) { // 网络全屏
            hierarchy.setFadeDuration(0);
            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_INSIDE);
            hierarchy.setPlaceholderImage(android.R.color.black, ScalingUtils.ScaleType.CENTER_CROP);
            hierarchy.setProgressBarImage(new ImageProgressFullDrawable(), ScalingUtils.ScaleType.CENTER_INSIDE);
            hierarchy.setRetryImage(new ImageRetryFullDrawable(), ScalingUtils.ScaleType.FIT_XY);
            hierarchy.setFailureImage(new ImageFailureFullDrawable(), ScalingUtils.ScaleType.FIT_XY);
        } else if (isNetNormal) { // 网络正常网络
            hierarchy.setFadeDuration(300);
            hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
            hierarchy.setPlaceholderImage(new ImageLoadingReactDrawable(), ScalingUtils.ScaleType.CENTER_CROP);
            hierarchy.setProgressBarImage(new ImageProgressReactDrawable(), ScalingUtils.ScaleType.CENTER_INSIDE);
            hierarchy.setRetryImage(new ImageRetryReactDrawable(), ScalingUtils.ScaleType.FIT_XY);
            hierarchy.setFailureImage(new ImageFailureReactDrawable(), ScalingUtils.ScaleType.FIT_XY);
        } else {
            // 自己在xml设置
        }
    }

    private void setController(Uri uri) {
        ImageRequest imageRequest = createImageRequest(uri);

        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setOldController(this.getController()) // 减少内存消耗
                .setImageRequest(imageRequest)
                .setAutoPlayAnimations(false);// gif自动播放
        if (uri != null && uri.toString().startsWith("http") && (isNetNormal || isNetFull)) {
            builder = builder.setTapToRetryEnabled(true); // 网络图非头像点击重新加载
        } else {
            builder = builder.setTapToRetryEnabled(false); // 非网络图不支持重新加载
        }
        // 加载回调
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
                        "\n\t width = " + imageInfo.getWidth() +
                        "\n\t height = " + imageInfo.getHeight() +
                        "\n\t quality = " + qualityInfo.getQuality() +
                        "\n\t goodEnoughQuality = " + qualityInfo.isOfGoodEnoughQuality() +
                        "\n\t fullQuality = " + qualityInfo.isOfFullQuality());
                // 点击事件
                if (mSuccessClickListener != null) {
                    GImageView.this.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSuccessClickListener.onClick(GImageView.this);
                        }
                    });
                }
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

    private ImageRequest createImageRequest(Uri uri) {
        ImageRequestBuilder requestBuilder = ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH) // 优先加载内存->磁盘->文件->网络
                .setProgressiveRenderingEnabled(true) // 网络图渐进式jpeg
                .setLocalThumbnailPreviewsEnabled(true) // 本地图缩略图
                .setRotationOptions(RotationOptions.autoRotate());
        // 非全屏小尺寸加载
        if (!isNetFull) {
            if (mWidth <= 0) {
                mWidth = getWidth();
            }
            if (mHeight <= 0) {
                mHeight = getHeight();
            }
            if (mWidth > 0 && mHeight > 0) {
                requestBuilder.setResizeOptions(new ResizeOptions(mWidth, mHeight));
            } else {
                LogUtils.w(LOG_TAG, "createImageRequest: mWidth | mHeight <= 0");
            }
        }
        return requestBuilder.build();
    }

    public interface onSuccessClickListener {
        void onClick(GImageView iv);
    }

    public void setSuccessClickListener(onSuccessClickListener listener) {
        mSuccessClickListener = listener;
    }

    // 设置图片类型
    public void setShowAs(boolean normal, boolean full, boolean circle) {
        isNetNormal = normal;
        isNetCircle = circle;
        isNetFull = full;
        initHierarchy(null);
    }

    // 一般只有grid需要传，在设置数据源之前调用
    public void setWidthAndHeight(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    // http:// https:// 需要现场获取oss的url
    public void setDataOss(String objPath) {
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
        Uri parse = ConvertUtils.file2Uri(file);
        setController(parse);
    }

    // "res://" + AppInfo.get().getPackageName() + "/" + id
    public void setDataRes(@AnyRes int id) {
        setImageResource(id);
    }

    public class ImageLoadingReactDrawable extends Drawable {

        private int mBackgroundColor;
        private Paint mTextPaint;
        private String mText;

        public ImageLoadingReactDrawable() {
            mBackgroundColor = ContextCompat.getColor(MyApp.get(), R.color.img_grey);
            int mTextColor = ContextCompat.getColor(MyApp.get(), R.color.font_grey);

            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setColor(mTextColor);

            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(ConvertUtils.sp2px(11));

            mText = getContext().getString(R.string.are_loading);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawColor(mBackgroundColor);
            //StaticLayout myStaticLayout = new StaticLayout(mText, getWidth() / 2, getHeight() / 2,mTextPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            //myStaticLayout.draw(canvas);

            canvas.drawText(mText, getWidth() / 2, getHeight() / 2, mTextPaint);
        }

        @Override
        public void setAlpha(int alpha) {
            mTextPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mTextPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
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

    public class ImageProgressReactDrawable extends Drawable {

        private int mBackgroundColor;
        private Paint mTextTopPaint;
        private Paint mTextBottomPaint;
        private String mTextTop;
        private String mProgressText;

        public ImageProgressReactDrawable() {
            mBackgroundColor = ContextCompat.getColor(MyApp.get(), R.color.img_grey);
            int mTextColor = ContextCompat.getColor(MyApp.get(), R.color.font_grey);

            mTextTopPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextTopPaint.setAntiAlias(true);
            mTextTopPaint.setStyle(Paint.Style.FILL);
            mTextTopPaint.setColor(mTextColor);
            mTextTopPaint.setTextAlign(Paint.Align.CENTER);
            mTextTopPaint.setTextSize(ConvertUtils.sp2px(11));

            mTextBottomPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextBottomPaint.setAntiAlias(true);
            mTextBottomPaint.setStyle(Paint.Style.FILL);
            mTextBottomPaint.setColor(mTextColor);
            mTextBottomPaint.setTextAlign(Paint.Align.CENTER);
            mTextBottomPaint.setTextSize(ConvertUtils.sp2px(11));

            mTextTop = getContext().getString(R.string.are_loading);
            mProgressText = "0%";
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawColor(mBackgroundColor);
            canvas.drawText(mTextTop, getWidth() / 2, getHeight() / 2, mTextTopPaint);
            canvas.drawText(mProgressText, getWidth() / 2, getHeight() / 4 * 3, mTextBottomPaint);
        }

        @Override
        protected boolean onLevelChange(int level) {
            if (level > 0 && level < 10000) {
                mProgressText = String.valueOf(level / 100) + "%";
                invalidateSelf();
                return true;
            }
            return false;
        }

        @Override
        public void setAlpha(int alpha) {
            mTextTopPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mTextTopPaint.setColorFilter(colorFilter);
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

    public class ImageProgressFullDrawable extends Drawable {

        // 画圆心的画笔
        private Paint mFillPaint;
        // 画圆环的画笔
        private Paint mRingPaint;
        // 总进度
        private int mTotalProgress = 10000;
        // 当前进度
        private int mCurrentProgress;

        public ImageProgressFullDrawable() {

            mFillPaint = new Paint();
            mFillPaint.setAntiAlias(true);
            mFillPaint.setColor(Color.parseColor("#77FFFFFF"));
            mFillPaint.setStyle(Paint.Style.FILL);

            mRingPaint = new Paint();
            mRingPaint.setAntiAlias(true);
            mRingPaint.setColor(Color.parseColor("#77FFFFFF"));
            mRingPaint.setStyle(Paint.Style.STROKE);
            mRingPaint.setStrokeWidth(ConvertUtils.dp2px(2));
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawColor(Color.parseColor("#FF000000"));
            drawBar(canvas, mTotalProgress, mRingPaint);
            drawBar(canvas, mCurrentProgress, mFillPaint);
        }

        private void drawBar(Canvas canvas, int level, Paint paint) {
            if (level > 0) {
                float mRingRadius = ConvertUtils.dp2px(20);
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
            mRingPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mRingPaint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return DrawableUtils.getOpacityFromColor(this.mRingPaint.getColor());
        }
    }

    public class ImageRetryReactDrawable extends Drawable {

        private int mBackgroundColor;
        private Paint mTextPaint;
        private String mText;

        public ImageRetryReactDrawable() {
            mBackgroundColor = ContextCompat.getColor(MyApp.get(), R.color.img_grey);
            int mTextColor = ContextCompat.getColor(MyApp.get(), R.color.font_grey);

            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setColor(mTextColor);

            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(ConvertUtils.sp2px(11));

            mText = getContext().getString(R.string.click_retry_load);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawColor(mBackgroundColor);
            //StaticLayout myStaticLayout = new StaticLayout(mText, getWidth() / 2, getHeight() / 2,mTextPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            //myStaticLayout.draw(canvas);

            canvas.drawText(mText, getWidth() / 2, getHeight() / 2, mTextPaint);
        }

        @Override
        public void setAlpha(int alpha) {
            mTextPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mTextPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    public class ImageRetryFullDrawable extends Drawable {

        private int mBackgroundColor;
        private Paint mTextPaint;
        private String mText;

        public ImageRetryFullDrawable() {
            mBackgroundColor = ContextCompat.getColor(MyApp.get(), android.R.color.black);
            int mTextColor = ContextCompat.getColor(MyApp.get(), android.R.color.white);

            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setColor(mTextColor);

            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(ConvertUtils.sp2px(11));

            mText = getContext().getString(R.string.click_retry_load);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawColor(mBackgroundColor);
            //StaticLayout myStaticLayout = new StaticLayout(mText, getWidth() / 2, getHeight() / 2,mTextPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            //myStaticLayout.draw(canvas);

            canvas.drawText(mText, getWidth() / 2, getHeight() / 2, mTextPaint);
        }

        @Override
        public void setAlpha(int alpha) {
            mTextPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mTextPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    public class ImageFailureReactDrawable extends Drawable {

        private int mBackgroundColor;
        private Paint mTextPaint;
        private String mText;

        public ImageFailureReactDrawable() {
            mBackgroundColor = ContextCompat.getColor(MyApp.get(), R.color.img_grey);
            int mTextColor = ContextCompat.getColor(MyApp.get(), R.color.font_grey);

            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setColor(mTextColor);

            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(ConvertUtils.sp2px(11));

            mText = getContext().getString(R.string.image_load_fail);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawColor(mBackgroundColor);
            //StaticLayout myStaticLayout = new StaticLayout(mText, getWidth() / 2, getHeight() / 2,mTextPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            //myStaticLayout.draw(canvas);

            canvas.drawText(mText, getWidth() / 2, getHeight() / 2, mTextPaint);
        }

        @Override
        public void setAlpha(int alpha) {
            mTextPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mTextPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

    public class ImageFailureFullDrawable extends Drawable {

        private int mBackgroundColor;
        private Paint mTextPaint;
        private String mText;

        public ImageFailureFullDrawable() {
            mBackgroundColor = ContextCompat.getColor(MyApp.get(), android.R.color.black);
            int mTextColor = ContextCompat.getColor(MyApp.get(), android.R.color.white);

            mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mTextPaint.setAntiAlias(true);
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setColor(mTextColor);

            mTextPaint.setTextAlign(Paint.Align.CENTER);
            mTextPaint.setTextSize(ConvertUtils.sp2px(11));

            mText = getContext().getString(R.string.image_load_fail);
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            canvas.drawColor(mBackgroundColor);
            //StaticLayout myStaticLayout = new StaticLayout(mText, getWidth() / 2, getHeight() / 2,mTextPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, false);
            //myStaticLayout.draw(canvas);

            canvas.drawText(mText, getWidth() / 2, getHeight() / 2, mTextPaint);
        }

        @Override
        public void setAlpha(int alpha) {
            mTextPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            mTextPaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }

}
