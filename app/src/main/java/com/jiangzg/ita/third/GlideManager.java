package com.jiangzg.ita.third;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
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
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.jiangzg.base.component.application.AppContext;
import com.jiangzg.ita.base.MyApp;

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

    //request.clone();
    //request.clear();
    //request.signature();

    public static <T> void loadNative(GlideManager manager, T data, ImageView view) {
        manager = manager.drawable(data)
                .cacheDisk(DiskCacheStrategy.NONE)
                .cacheMemory(false)
                .priority(Priority.IMMEDIATE) // 优先级
                .thumbnail(0.5f) // 开始缩略图
                .fade(100) // 渐进时间
                .intoView(view);
    }

    public static <T> void loadAvatar(GlideManager manager, T data, ImageView view) {
        //if (data instanceof String) {
        //    // todo 先获取标识，用于获取缓存
        //}
        //// todo 判断图片是否是gif，用于后续加载
        //manager = manager.drawable(data)
        //        .cacheDisk(DiskCacheStrategy.ALL)
        //        .cacheMemory(true)
        //        //todo 优先根据标识加载缓存
        //        .priority(Priority.NORMAL) // 优先级
        //        .holder() // 占位图
        //        .progress(true) // 圆形进度条
        //        .error(a) // 错误图
        //        .transform(a) // 圆型+白边
        //        .thumbnail(0.5f) // 开始缩略图
        //        .fade(100) // 渐进时间
        //        .listener(new RequestListener() {
        //            @Override
        //            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
        //                // 网络不好不处理，交给点击重新加载，oss过期问题，在这里处理
        //                return false;
        //            }
        //
        //            @Override
        //            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
        //                return false;
        //            }
        //        })
        //        .intoView(view);
        //manager.reLoad(a); // 点击重新加载
    }

    public static <T> void loadView(GlideManager manager, T data, ImageView view) {
        if (data instanceof String) {
            // todo 先获取标识，用于获取缓存
        }
        // todo 判断图片是否是gif，用于后续加载
        manager = manager.drawable(data)
                .cacheDisk(DiskCacheStrategy.ALL)
                .cacheMemory(true)
                //todo 优先根据标识加载缓存
                .priority(Priority.LOW) // 优先级
                //.holder() // 占位图
                .progress(true) // 圆形进度条
                //.error(a) // 错误图
                .thumbnail(0.5f) // 开始缩略图
                .fade(100) // 渐进时间
                .listener(new RequestListener() {
                    @Override
                    public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                        // 网络不好不处理，交给点击重新加载，oss过期问题，在这里处理
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                        return false;
                    }
                })
                .intoView(view);
        //manager.reLoad(a); // 点击重新加载
    }

    public static <T> void loadTarget(GlideManager manager, T data, Target target) {
        //if (data instanceof String) {
        //    // todo 先获取标识，用于获取缓存
        //}
        //// todo 判断图片是否是gif，用于后续加载
        //manager = manager.bitmap(data)
        //        .cacheDisk(DiskCacheStrategy.ALL)
        //        .cacheMemory(true)
        //        //todo 优先根据标识加载缓存
        //        .priority(Priority.LOW) // 优先级
        //        .holder() // 占位图
        //        .progress(true) // 圆形进度条
        //        .error(a) // 错误图
        //        .thumbnail(0.5f) // 开始缩略图
        //        .fade(100) // 渐进时间
        //        .listener(new RequestListener() {
        //            @Override
        //            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
        //                // 网络不好不处理，交给点击重新加载，oss过期问题，在这里处理
        //                return false;
        //            }
        //
        //            @Override
        //            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
        //                return false;
        //            }
        //        })
        //        .intoView(target);
        //manager.reLoad(a); // 点击重新加载
    }

    public static <T> void loadScreen(GlideManager manager, T data, ImageView view) {
        //if (data instanceof String) {
        //    // todo 先获取标识，用于获取缓存
        //}
        //// todo 判断图片是否是gif，用于后续加载
        //manager = manager.drawable(data)
        //        .cacheDisk(DiskCacheStrategy.ALL)
        //        .cacheMemory(true)
        //        //todo 优先根据标识加载缓存
        //        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
        //        .priority(Priority.HIGH) // 优先级
        //        .holder() // 占位图
        //        .progress(true) // 圆形进度条
        //        .error(a) // 错误图
        //        .thumbnail(0.5f) // 开始缩略图
        //        .listener(new RequestListener<T, GlideDrawable>() {
        //            @Override
        //            public boolean onException(Exception e, T model, Target<GlideDrawable> target, boolean isFirstResource) {
        //                return false;
        //            }
        //
        //            @Override
        //            public boolean onResourceReady(GlideDrawable resource, T model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
        //                return false;
        //            }
        //        })
        //        .intoView(view);
        //manager.reLoad(a); // 点击重新加载
    }

    public static <T> void getBitmap(T data, boolean tag, ImageView view) {
        //Bitmap bitmap = new GlideManager(AppContext.get())
        //        .bitmap(data)
        //        .cacheDisk(DiskCacheStrategy.ALL)
        //        .cacheMemory(true)
        //        .priority(Priority.IMMEDIATE)
        //        .listener()// todo
        //        .intoGet(); // 原始尺寸
    }

    public static <T> void getFile(T data, boolean tag, ImageView view) {
        //File file = new GlideManager(AppContext.get())
        //        .drawable(data)
        //        .cacheDisk(DiskCacheStrategy.ALL)
        //        .cacheMemory(true)
        //        .priority(Priority.IMMEDIATE)
        //        .transform(tag) // 水印
        //        .listener()// todo
        //        .intoDownload(); // 原始尺寸
    }

    private RequestManager mRequestManager;
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

    // 请求类型，不能加载gif的第一帧
    public <T> GlideManager drawable(T data) {
        if (mRequestManager == null) return this;
        mRequestType = REQUEST_TYPE_DRAWABLE;
        mDrawableTypeRequest = mRequestManager.load(data);
        return this;
    }

    // 请求类型，不能加载gif的第一帧
    public <T> GlideManager bitmap(T data) {
        if (mRequestManager == null) return this;
        mRequestType = REQUEST_TYPE_BITMAP;
        mBitmapTypeRequest = mRequestManager.load(data).asBitmap();
        return this;
    }

    // 请求类型，只能加载gif
    public <T> GlideManager gif(T data) {
        if (mRequestManager == null) return this;
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

    // 重新设置尺寸，默认为Target.SIZE_ORIGINAL
    public GlideManager override(int width, int height) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.override(width, height);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.override(width, height);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.override(width, height);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.override(width, height);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.override(width, height);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.override(width, height);
            }
        }
        return this;
    }

    // 重新设置显示画质百分比
    public GlideManager sizeMultiplier(float sizeMultiplier) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.sizeMultiplier(sizeMultiplier);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.sizeMultiplier(sizeMultiplier);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.sizeMultiplier(sizeMultiplier);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.sizeMultiplier(sizeMultiplier);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.sizeMultiplier(sizeMultiplier);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.sizeMultiplier(sizeMultiplier);
            }
        }
        return this;
    }

    // 优先级，默认NORMAL
    public GlideManager priority(Priority priority) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.priority(priority);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.priority(priority);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.priority(priority);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.priority(priority);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.priority(priority);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.priority(priority);
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

    // 进度条
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

    // 缩略图，图片资源已经得到时，且缩略图比全尺寸图先加载完，就显示缩略图
    public GlideManager thumbnail(float sizeMultiplier) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.thumbnail(sizeMultiplier);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.thumbnail(sizeMultiplier);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.thumbnail(sizeMultiplier);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.thumbnail(sizeMultiplier);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.thumbnail(sizeMultiplier);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.thumbnail(sizeMultiplier);
            }
        }
        return this;
    }

    // 动画
    public GlideManager animator(@AnimatorRes int resId) {
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

    // 监听
    public GlideManager listener(RequestListener listener) {
        if (mRequestType == REQUEST_TYPE_GIF) {
            if (mGifRequestBuilder != null) {
                mGifRequestBuilder = mGifRequestBuilder.listener(listener);
            } else if (mGifTypeRequest != null) {
                mGifRequestBuilder = mGifTypeRequest.listener(listener);
            }
        } else if (mRequestType == REQUEST_TYPE_BITMAP) {
            if (mBitmapRequestBuilder != null) {
                mBitmapRequestBuilder = mBitmapRequestBuilder.listener(listener);
            } else if (mBitmapTypeRequest != null) {
                mBitmapRequestBuilder = mBitmapTypeRequest.listener(listener);
            }
        } else {
            if (mDrawableRequestBuilder != null) {
                mDrawableRequestBuilder = mDrawableRequestBuilder.listener(listener);
            } else if (mDrawableTypeRequest != null) {
                mDrawableRequestBuilder = mDrawableTypeRequest.listener(listener);
            }
        }
        return this;
    }

    // 点击重试
    public boolean reLoad(GlideManager manager) {

        return false;
    }

    // 加载，到ImageVIew
    public GlideManager intoView(ImageView view) {
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
    public GlideManager intoTarget(Target target) {
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

    // 获取指定宽高的资源，后台执行的，对应的type生成对应的T
    public <T> T intoGet(int width, int height) {
        try {
            if (mRequestType == REQUEST_TYPE_GIF) {
                if (mGifRequestBuilder != null) {
                    return (T) mGifRequestBuilder.into(width, height).get();
                } else if (mGifTypeRequest != null) {
                    return (T) mGifTypeRequest.into(width, height).get();
                }
            } else if (mRequestType == REQUEST_TYPE_BITMAP) {
                if (mBitmapRequestBuilder != null) {
                    return (T) mBitmapRequestBuilder.into(width, height).get();
                } else if (mBitmapTypeRequest != null) {
                    return (T) mBitmapTypeRequest.into(width, height).get();
                }
            } else {
                if (mDrawableRequestBuilder != null) {
                    return (T) mDrawableRequestBuilder.into(width, height).get();
                } else if (mDrawableTypeRequest != null) {
                    return (T) mDrawableTypeRequest.into(width, height).get();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T intoGet() {
        return intoGet(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
    }

    // 获取文件，后台执行的，且只有drawable可以调用
    public <T> T intoDownload(int width, int height) {
        try {
            if (mDrawableTypeRequest != null) {
                return (T) mDrawableTypeRequest.downloadOnly(width, height).get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public <T> T intoDownload() {
        return intoDownload(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
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

    /* 清除内存缓存，主线程调用 */
    public static void clearMemory() {
        MyApp.get().getHandler().post(new Runnable() {
            @Override
            public void run() {
                Glide.get(AppContext.get()).clearMemory();
            }
        });
    }

    /* 获取缓存目录 */
    public static File getCacheFile() {
        return new File(AppContext.get().getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR);
    }

    /* 清除磁盘缓存，后台线程中调用 */
    public static void clearCache() {
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(AppContext.get()).clearDiskCache();
            }
        });
    }

}
