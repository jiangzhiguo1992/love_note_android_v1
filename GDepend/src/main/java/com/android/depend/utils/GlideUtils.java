package com.android.depend.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;

import com.android.base.component.application.AppContext;
import com.android.base.string.ConvertUtils;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by JiangZhiGuo on 2016/7/25.
 * describe Glide工具类
 */
public class GlideUtils {

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

    /* 图片加载完毕监听 */
    public interface CompleteListener {
        void complete(Bitmap result);
    }

    /* 获取网络图片 */
    public static <T> Bitmap getBitmap(T data, int width, int height) {
        try {
            return Glide.with(AppContext.get())
                    .load(data)
                    .asBitmap()
                    .into(width, height)
                    .get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static <T> void load(RequestManager requestManager, T data, int errorRes,
                                 final CompleteListener completeListener, ImageView view) {
        DrawableTypeRequest load = requestManager.load(data);
        if (errorRes != 0) { // 设置错误图片
            load.error(errorRes);
        }
        load.crossFade(100); // 设置淡入淡出效果，默认300ms(load.dontAnimate();取消效果)
        load.skipMemoryCache(false); // 不跳过内存缓存
        load.diskCacheStrategy(DiskCacheStrategy.SOURCE); // 磁盘缓存
        load.into(new GlideDrawableImageViewTarget(view) {  //.listener() 也是监听异常和准备状态
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
            }

            @Override
            protected void setResource(GlideDrawable resource) {
                super.setResource(resource);
                if (completeListener != null) {
                    Bitmap bitmap = ConvertUtils.drawable2Bitmap(resource);
                    completeListener.complete(bitmap);
                }
            }
        });
    }

    /* 错误图片,回调监听 */
    public static <T> void load(Context context, T data, int errorRes,
                                CompleteListener completeListener, ImageView view) {
        RequestManager with = Glide.with(context);
        load(with, data, errorRes, completeListener, view);
    }

    public static <T> void load(FragmentActivity activity, T data, int errorRes,
                                CompleteListener completeListener, ImageView view) {
        RequestManager with = Glide.with(activity);
        load(with, data, errorRes, completeListener, view);
    }

    public static <T> void load(Fragment fragment, T data, int errorRes,
                                CompleteListener completeListener, ImageView view) {
        RequestManager with = Glide.with(fragment);
        load(with, data, errorRes, completeListener, view);
    }

    /* 错误图片 */
    public static <T> void load(Context context, T data, int errorRes, ImageView view) {
        load(context, data, errorRes, null, view);
    }

    public static <T> void load(FragmentActivity activity, T data, int errorRes, ImageView view) {
        load(activity, data, errorRes, null, view);
    }

    public static <T> void load(Fragment fragment, T data, int errorRes, ImageView view) {
        load(fragment, data, errorRes, null, view);
    }

    /* 回调监听 */
    public static <T> void load(Context context, T data,
                                CompleteListener completeListener, ImageView view) {
        load(context, data, 0, completeListener, view);
    }

    public static <T> void load(FragmentActivity activity, T data,
                                CompleteListener completeListener, ImageView view) {
        load(activity, data, 0, completeListener, view);
    }

    public static <T> void load(Fragment fragment, T data,
                                CompleteListener completeListener, ImageView view) {
        load(fragment, data, 0, completeListener, view);
    }

    /* 加载图片 */
    public static <T> void load(Context context, T data, ImageView view) {
        load(context, data, 0, null, view);
    }

    public static <T> void load(FragmentActivity activity, T data, ImageView view) {
        load(activity, data, 0, null, view);
    }

    public static <T> void load(Fragment fragment, T data, ImageView view) {
        load(fragment, data, 0, null, view);
    }
}
