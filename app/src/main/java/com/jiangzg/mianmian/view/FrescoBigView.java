package com.jiangzg.mianmian.view;

import android.app.Activity;
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
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.DrawableUtils;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ProviderUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.helper.FrescoHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.OssResHelper;

import java.io.File;

import me.relex.photodraweeview.PhotoDraweeView;

/**
 * Created by JZG on 2018/3/21.
 * Fresco的图片控件封装
 */
public class FrescoBigView extends PhotoDraweeView {

    public FrescoBigView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init(context, null, hierarchy);
    }

    public FrescoBigView(Context context) {
        super(context);
        init(context, null, null);
    }

    public FrescoBigView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, null);
    }

    public FrescoBigView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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
        hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
        hierarchy.setPlaceholderImage(android.R.color.black, ScalingUtils.ScaleType.CENTER_CROP);
        hierarchy.setProgressBarImage(new ImageProgressFullDrawable(), ScalingUtils.ScaleType.CENTER_INSIDE);
        hierarchy.setRetryImage(new ImageRetryFullDrawable(), ScalingUtils.ScaleType.FIT_XY);
        hierarchy.setFailureImage(new ImageFailureFullDrawable(), ScalingUtils.ScaleType.FIT_XY);
    }

    private void setController(Uri uri) {
        // request
        Activity top = ActivityStack.getTop();
        int screenWidth;
        int screenHeight;
        if (top != null) {
            screenWidth = ScreenUtils.getScreenRealWidth(top);
            screenHeight = ScreenUtils.getScreenRealHeight(top);
        } else {
            screenWidth = ScreenUtils.getScreenWidth(MyApp.get());
            screenHeight = ScreenUtils.getScreenHeight(MyApp.get());
        }
        ImageRequestBuilder requestBuilder = FrescoHelper.getImageRequestBuilder(uri, screenWidth, screenHeight); // 不需要采样了
        ImageRequest imageRequest = requestBuilder.build();
        // controller
        PipelineDraweeControllerBuilder builder = FrescoHelper.getPipelineControllerBuilder(this, uri, imageRequest);
        builder.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null) {
                    LogUtils.w(FrescoBigView.class, "onFinalImageSet", "imageInfo == null");
                    return;
                }
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                LogUtils.d(FrescoBigView.class, "onFinalImageSet",
                        " width = " + imageInfo.getWidth() +
                                " height = " + imageInfo.getHeight() +
                                " quality = " + qualityInfo.getQuality() +
                                " goodEnoughQuality = " + qualityInfo.isOfGoodEnoughQuality() +
                                " fullQuality = " + qualityInfo.isOfFullQuality());
                FrescoBigView.this.update(imageInfo.getWidth(), imageInfo.getHeight());
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                LogUtils.e(FrescoBigView.class, "onFailure", throwable);
            }
        });
        AbstractDraweeController controller = builder.build();
        this.setController(controller);
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

    // file://
    public void setDataFile(File file) {
        Uri parse = ProviderUtils.getUriByFile(file);
        setController(parse);
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
