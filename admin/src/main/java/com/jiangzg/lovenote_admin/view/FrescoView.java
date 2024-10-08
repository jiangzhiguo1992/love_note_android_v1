package com.jiangzg.lovenote_admin.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.AbstractDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.MyApp;
import com.jiangzg.lovenote_admin.helper.FrescoHelper;
import com.jiangzg.lovenote_admin.helper.OssHelper;

import java.io.File;

/**
 * Created by JZG on 2018/3/21.
 * Fresco的图片控件封装
 */
public class FrescoView extends SimpleDraweeView {

    private int mWidth, mHeight;
    private ClickListener mClickListener;
    private LoadListener mLoadListener;
    private BitmapListener mBitmapListener;

    public FrescoView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        init(context, hierarchy);
    }

    public FrescoView(Context context) {
        super(context);
        init(context, null);
    }

    public FrescoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, null);
    }

    public FrescoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, null);
    }

    public FrescoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, null);
    }

    private void init(Context context, GenericDraweeHierarchy h) {
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
        //hierarchy.setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP); // 在xml里设置
        hierarchy.setPlaceholderImage(new ImageLoadingReactDrawable(), ScalingUtils.ScaleType.CENTER_CROP);
        hierarchy.setProgressBarImage(new ImageProgressReactDrawable(), ScalingUtils.ScaleType.CENTER_INSIDE);
        hierarchy.setRetryImage(new ImageRetryReactDrawable(), ScalingUtils.ScaleType.FIT_XY);
        hierarchy.setFailureImage(new ImageFailureReactDrawable(), ScalingUtils.ScaleType.FIT_XY);
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
        // bitmap (这里的参数最好不要往外复制，会有问题)
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline.fetchDecodedImage(imageRequest, this.getContext());
        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            protected void onNewResultImpl(Bitmap bitmap) {
                if (bitmap == null) {
                    LogUtils.w(FrescoView.class, "onNewResultImpl", "bitmap == null");
                    if (mBitmapListener != null) {
                        mBitmapListener.onBitmapFail(FrescoView.this);
                    }
                    return;
                }
                if (mBitmapListener != null) {
                    mBitmapListener.onBitmapSuccess(FrescoView.this, bitmap);
                }
            }

            @Override
            protected void onFailureImpl(DataSource<CloseableReference<CloseableImage>> dataSource) {
                LogUtils.w(FrescoView.class, "onFailureImpl", "");
                if (mBitmapListener != null) {
                    mBitmapListener.onBitmapFail(FrescoView.this);
                }
            }
        }, CallerThreadExecutor.getInstance());

        // controller
        PipelineDraweeControllerBuilder builder = FrescoHelper.getPipelineControllerBuilder(this, uri, imageRequest);
        builder.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null) {
                    LogUtils.w(FrescoView.class, "onFinalImageSet", "imageInfo == null");
                    return;
                }
                QualityInfo qualityInfo = imageInfo.getQualityInfo();
                LogUtils.d(FrescoView.class, "onFinalImageSet",
                        " width = " + imageInfo.getWidth() +
                                " height = " + imageInfo.getHeight() +
                                " quality = " + qualityInfo.getQuality() +
                                " goodEnoughQuality = " + qualityInfo.isOfGoodEnoughQuality() +
                                " fullQuality = " + qualityInfo.isOfFullQuality());
                // 加载成功事件
                if (mLoadListener != null) {
                    mLoadListener.onLoadSuccess(FrescoView.this, imageInfo);
                }
                // 点击事件
                if (mClickListener != null) {
                    FrescoView.this.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mClickListener.onSuccessClick(FrescoView.this);
                        }
                    });
                }
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
                super.onFailure(id, throwable);
                LogUtils.e(FrescoView.class, "onFailure", throwable);
                // 加载失败事件
                if (mLoadListener != null) {
                    mLoadListener.onLoadFail(FrescoView.this);
                }
            }
        });
        AbstractDraweeController controller = builder.build();
        this.setController(controller);
    }

    public interface ClickListener {
        void onSuccessClick(FrescoView iv);
    }

    public void setClickListener(ClickListener listener) {
        mClickListener = listener;
    }

    public interface LoadListener {
        void onLoadSuccess(FrescoView iv, ImageInfo imageInfo);

        void onLoadFail(FrescoView iv);
    }

    public void setLoadListener(LoadListener listener) {
        mLoadListener = listener;
    }

    public interface BitmapListener {
        void onBitmapSuccess(FrescoView iv, Bitmap bitmap);

        void onBitmapFail(FrescoView iv);
    }

    public void setBitmapListener(BitmapListener listener) {
        mBitmapListener = listener;
    }

    // GridList 和 match_parent 需要传，在设置数据源之前调用
    public void setWidthAndHeight(int width, int height) {
        mWidth = width;
        mHeight = height;
    }

    public void setData(String ossKey) {
        this.setDataOss(ossKey);
    }

    // file://
    public void setDataFile(File file) {
        if (file == null) return;
        Uri parse = Uri.fromFile(file);
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

}
