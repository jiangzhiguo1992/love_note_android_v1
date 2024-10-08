package com.jiangzg.lovenote.helper.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.cache.disk.FileCache;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.NoOpMemoryTrimmableRegistry;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.view.DraweeView;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.CacheKeyFactory;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.common.RotationOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.ResHelper;

import okhttp3.OkHttpClient;

/**
 * Created by JZG on 2018/4/28.
 * FrescoHelper
 */
public class FrescoHelper {

    // 初始化
    public static void initApp(Context ctx, boolean debug) {
        // 网络图的缓存key
        CacheKeyFactory keyFactory = new DefaultCacheKeyFactory() {
            @Override
            protected Uri getCacheKeySourceUri(Uri sourceUri) {
                if (sourceUri == null) return null;
                String key = sourceUri.toString();
                String cacheKey = OssHelper.getOssKey(key);
                return Uri.parse(cacheKey);
            }
        };
        // 设置缓存目录
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(ctx)
                .setBaseDirectoryPath(ResHelper.createFrescoCacheDir()) // 路径
                .setMaxCacheSize(ResHelper.getMaxCacheSize()) // 大小
                .setCacheErrorLogger((category, clazz, message, throwable) -> LogUtils.e(FrescoHelper.class, "initApp", throwable))
                .build();
        // 初始化配置，使用okHttp的，会有准确的加载进度
        ImagePipelineConfig config = OkHttpImagePipelineConfigFactory.newBuilder(ctx, new OkHttpClient())
                .setCacheKeyFactory(keyFactory)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig()) // 渐进式实现
                .setBitmapsConfig(Bitmap.Config.ARGB_8888) // 小图RGB_565 大图ARGB8888
                //.setBitmapMemoryCacheParamsSupplier() // 配置缓存策略
                .setMainDiskCacheConfig(diskCacheConfig)
                .setMemoryTrimmableRegistry(NoOpMemoryTrimmableRegistry.getInstance()) // 注册内存调用器，在需要回收内存的时候进行回收
                .setResizeAndRotateEnabledForNetwork(true) // 对网络图片进行resize处理，减少内存消耗
                .setDownsampleEnabled(true) // 向下采样，必须和ImageRequest的ResizeOptions一起使用
                .build();
        // 开始初始化
        Fresco.initialize(ctx, config);
        if (debug) {
            FLog.setMinimumLoggingLevel(FLog.VERBOSE);
        }
    }

    // 清除内存
    public static void clearMemoryCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
    }

    // 清除磁盘
    public static void clearDiskCaches() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
        imagePipeline.clearDiskCaches();
    }

    // 获取磁盘大小
    public static long getDiskCachesSize() {
        FileCache mainFileCache = Fresco.getImagePipelineFactory().getMainFileCache();
        mainFileCache.trimToMinimum(); // 防止大小为-1
        return mainFileCache.getSize();
    }

    public static void imagePause() {
        Fresco.getImagePipeline().pause();
    }

    public static void imageResume() {
        Fresco.getImagePipeline().resume();
    }

    // ImageRequest构造
    public static ImageRequestBuilder getImageRequestBuilder(Uri uri, int width, int height) {
        if (uri == null) return null;
        ImageRequestBuilder builder = ImageRequestBuilder.newBuilderWithSource(uri)
                .setCacheChoice(ImageRequest.CacheChoice.DEFAULT)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH) // 优先加载内存->磁盘->文件->网络
                .setProgressiveRenderingEnabled(true) // 网络图渐进式jpeg
                .setLocalThumbnailPreviewsEnabled(true) // 本地图缩略图
                .setRotationOptions(RotationOptions.autoRotate()); // 摆正图片
        if (width > 0 && height > 0) {
            builder.setResizeOptions(new ResizeOptions(width, height)); // 采样大小
        } else {
            LogUtils.w(FrescoHelper.class, "getImageRequestBuilder", "width | height == 0");
        }
        return builder;
    }

    // PipelineController构造
    public static PipelineDraweeControllerBuilder getPipelineControllerBuilder(DraweeView view, Uri uri, ImageRequest imageRequest) {
        PipelineDraweeControllerBuilder builder = Fresco.newDraweeControllerBuilder()
                .setOldController(view.getController()) // 减少内存消耗
                .setAutoPlayAnimations(true); // gif支持自动播放
        if (imageRequest != null) {
            builder = builder.setImageRequest(imageRequest);
        }
        if (uri != null && uri.toString().startsWith("http")) {
            builder = builder.setTapToRetryEnabled(true); // 网络图点击重新加载
        } else {
            builder = builder.setTapToRetryEnabled(false); // 非网络图不支持重新加载
        }
        return builder;
    }

}
