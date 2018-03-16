package com.jiangzg.ita.third;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnimatorRes;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.BitmapTypeRequest;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.GifTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.animation.ViewPropertyAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.jiangzg.base.component.application.AppContext;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by JiangZhiGuo on 2016/7/25.
 * describe Glide工具类
 */
public class GlideManager {

    private static final int REQUEST_TYPE_DRAWABLE = 0;
    private static final int REQUEST_TYPE_BITMAP = 1;
    private static final int REQUEST_TYPE_GIF = 2;

    private RequestManager mRequestManager;
    private Object mData;
    private int mRequestType;
    // drawable
    private DrawableTypeRequest mDrawableTypeRequest;
    private DrawableRequestBuilder mDrawableRequestBuilder;
    // bitmap
    private BitmapTypeRequest mBitmapTypeRequest;
    private BitmapRequestBuilder mBitmapRequestBuilder;
    // gif
    private GifTypeRequest mGifTypeRequest;
    private GifRequestBuilder mGifRequestBuilder;
    // target
    private Target mTarget;
    private FutureTarget mFutureTarget;

    // 请求容器
    public GlideManager(Context context) {
        mRequestManager = Glide.with(context);
    }

    public GlideManager(Activity activity) {
        mRequestManager = Glide.with(activity);
    }

    public GlideManager(FragmentActivity activity) {
        mRequestManager = Glide.with(activity);
    }

    public GlideManager(Fragment fragment) {
        mRequestManager = Glide.with(fragment);
    }

    public GlideManager(android.app.Fragment fragment) {
        mRequestManager = Glide.with(fragment);
    }

    // 请求类型，不能加载gif
    public <T> GlideManager drawable(T data) {
        if (mRequestManager == null) return this;
        mData = data;
        mRequestType = REQUEST_TYPE_DRAWABLE;
        mDrawableTypeRequest = mRequestManager.load(data);
        return this;
    }

    // 请求类型，不能加载gif
    public <T> GlideManager bitmap(T data) {
        if (mRequestManager == null) return this;
        mData = data;
        mRequestType = REQUEST_TYPE_BITMAP;
        mBitmapTypeRequest = mRequestManager.load(data).asBitmap();
        return this;
    }

    // 请求类型，只能加载gif
    public <T> GlideManager gif(T data) {
        if (mRequestManager == null) return this;
        mData = data;
        mRequestType = REQUEST_TYPE_GIF;
        mGifTypeRequest = mRequestManager.load(data).asGif();
        return this;
    }

    // 内存缓存
    public GlideManager cacheMemory(boolean use) {
        boolean skip = !use;
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.skipMemoryCache(skip);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.skipMemoryCache(skip);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.skipMemoryCache(skip);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.skipMemoryCache(skip);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.skipMemoryCache(skip);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.skipMemoryCache(skip);
            }
        }
        return this;
    }

    // 磁盘缓存 可选 1.源分辨率大小 2.显示分辨率大小
    public GlideManager cacheDisk(DiskCacheStrategy type) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.diskCacheStrategy(type);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.diskCacheStrategy(type);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.diskCacheStrategy(type);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.diskCacheStrategy(type);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.diskCacheStrategy(type);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.diskCacheStrategy(type);
            }
        }
        return this;
    }

    //  占位图
    public GlideManager holder(@DrawableRes int resId) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.placeholder(resId);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.placeholder(resId);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.placeholder(resId);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.placeholder(resId);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.placeholder(resId);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.placeholder(resId);
            }
        }
        return this;
    }

    public GlideManager holder(Drawable drawable) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.placeholder(drawable);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.placeholder(drawable);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.placeholder(drawable);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.placeholder(drawable);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.placeholder(drawable);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.placeholder(drawable);
            }
        }
        return this;
    }

    // todo 进度条
    public GlideManager progress(boolean show) {
        return this;
    }

    // 错误图
    public GlideManager error(@DrawableRes int resId) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.error(resId);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.error(resId);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.error(resId);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.error(resId);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.error(resId);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.error(resId);
            }
        }
        return this;
    }

    public GlideManager error(Drawable drawable) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.error(drawable);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.error(drawable);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.error(drawable);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.error(drawable);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.error(drawable);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.error(drawable);
            }
        }
        return this;
    }

    // 缩略图 todo 还有两种方法,应该有和生成视频缩略图有关的api
    public GlideManager thumbnail(float percent) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.thumbnail(percent);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.thumbnail(percent);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.thumbnail(percent);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.thumbnail(percent);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.thumbnail(percent);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.thumbnail(percent);
            }
        }
        return this;
    }

    // 动画
    public GlideManager animate(@AnimatorRes int resId) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.animate(resId);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.animate(resId);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.animate(resId);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.animate(resId);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.animate(resId);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.animate(resId);
            }
        }
        return this;
    }

    public GlideManager animate(ViewPropertyAnimation.Animator animator) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.animate(animator);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.animate(animator);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.animate(animator);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.animate(animator);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.animate(animator);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.animate(animator);
            }
        }
        return this;
    }

    // 出场渐变
    public GlideManager fade(int millSecond) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.crossFade(millSecond);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.crossFade(millSecond);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            //if (mBitmapRequestBuilder != null) {
            //    mBitmapRequestBuilder = mBitmapRequestBuilder.crossFade(millSecond);
            //} else if (mBitmapTypeRequest != null) {
            //    mBitmapRequestBuilder = mBitmapTypeRequest.crossFade(millSecond);
            //}
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.crossFade(millSecond);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.crossFade(millSecond);
            }
        }
        return this;
    }

    public GlideManager fade(@AnimatorRes int animationId, int millSecond) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.crossFade(animationId, millSecond);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.crossFade(animationId, millSecond);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            //if (mBitmapRequestBuilder != null) {
            //    mBitmapRequestBuilder = mBitmapRequestBuilder.crossFade(animationId, millSecond);
            //} else if (mBitmapTypeRequest != null) {
            //    mBitmapRequestBuilder = mBitmapTypeRequest.crossFade(animationId, millSecond);
            //}
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.crossFade(animationId, millSecond);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.crossFade(animationId, millSecond);
            }
        }
        return this;
    }

    // 图片处理
    public GlideManager transform(Transformation transformation) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.transform(transformation);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.transform(transformation);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.transform(transformation);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.transform(transformation);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.transform(transformation);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.transform(transformation);
            }
        }
        return this;
    }

    // todo 点击重试 如果失败了直接重新加载
    public boolean retryLoad(GlideManager manager) {

        return false;
    }

    // 加载，到ImageVIew
    public GlideManager into(ImageView view) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mTarget = mGifRequestBuilder.into(view);
            } else if (mGifTypeRequest != null) {
                mTarget = mGifTypeRequest.into(view);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mTarget = mBitmapRequestBuilder.into(view);
            } else if (mBitmapTypeRequest != null) {
                mTarget = mBitmapTypeRequest.into(view);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mTarget = mDrawableRequestBuilder.into(view);
            } else if (mDrawableTypeRequest != null) {
                mTarget = mDrawableTypeRequest.into(view);
            }
        }
        return this;
    }

    // 加载，到自定义Target
    public GlideManager into(Target target) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mTarget = mGifRequestBuilder.into(target);
            } else if (mGifTypeRequest != null) {
                mTarget = mGifTypeRequest.into(target);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mTarget = mBitmapRequestBuilder.into(target);
            } else if (mBitmapTypeRequest != null) {
                mTarget = mBitmapTypeRequest.into(target);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mTarget = mDrawableRequestBuilder.into(target);
            } else if (mDrawableTypeRequest != null) {
                mTarget = mDrawableTypeRequest.into(target);
            }
        }
        return this;
    }

    // 生成资源，不会加载到view上，只会生成对应宽高的资源
    public GlideManager feature(int width, int height) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mFutureTarget = mGifRequestBuilder.into(width, height);
            } else if (mGifTypeRequest != null) {
                mFutureTarget = mGifTypeRequest.into(width, height);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mFutureTarget = mBitmapRequestBuilder.into(width, height);
            } else if (mBitmapTypeRequest != null) {
                mFutureTarget = mBitmapTypeRequest.into(width, height);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mFutureTarget = mDrawableRequestBuilder.into(width, height);
            } else if (mDrawableTypeRequest != null) {
                mFutureTarget = mDrawableTypeRequest.into(width, height);
            }
        }
        return this;
    }

    // 获取资源，子线程调用，drawable能转成GlideBitmapDrawable，bitmap才能转成bitmap
    public <T> T getFeature() {
        if (mFutureTarget != null) {
            try {
                return (T) mFutureTarget.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /* 图片加载完毕监听 */
    public interface TargetLifeListener {
        void onReady(Bitmap ready);

        void onComplete(Bitmap result);
    }

    public BitmapImageViewTarget getBitmapTarget(ImageView view, final TargetLifeListener listener) {
        return new BitmapImageViewTarget(view) {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                super.onResourceReady(resource, glideAnimation);
                if (listener != null) {
                    listener.onReady(resource);
                }
            }

            @Override
            protected void setResource(Bitmap resource) {
                super.setResource(resource);
                if (listener != null) {
                    listener.onComplete(resource);
                }
            }
        };
    }

    //request.sizeMultiplier();
    //request.downloadOnly();
    //request.clone();
    //request.clear();
    //request.listener();
    //request.override();
    //request.preload();
    //request.priority();
    //request.signature();

    // Glide.with(context).load(imageUrl).transform(new GlideRoundTransform(context)).into(imageView)
    // 注意：使用了transform以后，就不能使用centerCrop，fitCenter等方法
    public static class GlideRoundTransform extends BitmapTransformation {

        private static float radius = 0f;

        public GlideRoundTransform(Context context) {
            this(context, 4);
        }

        public GlideRoundTransform(Context context, int dp) {
            super(context);
            radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private static Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) return null;

            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName() + Math.round(radius);
        }
    }

    /* 获取缓存目录 */
    public static File getCacheFile() {
        return new File(AppContext.get().getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR);
    }

    /* 清除磁盘缓存 */
    public static void clearCache() {
        Glide.get(AppContext.get()).clearDiskCache();
    }

    /* 清除内存缓存 */
    public static void clearMemory() {
        Glide.get(AppContext.get()).clearMemory();
    }

}
