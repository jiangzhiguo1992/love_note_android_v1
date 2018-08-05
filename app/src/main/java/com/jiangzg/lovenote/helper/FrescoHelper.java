package com.jiangzg.lovenote.helper;

import android.app.Application;
import android.graphics.Bitmap;
import android.net.Uri;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.cache.disk.FileCache;
import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.view.DraweeView;
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
import com.jiangzg.base.common.StringUtils;

/**
 * Created by JZG on 2018/4/28.
 * FrescoHelper
 */
public class FrescoHelper {

    private static final String LOG_TAG = "FrescoHelper";

    // 初始化
    public static void init(Application app, boolean debug) {
        // 网络图的缓存key
        CacheKeyFactory keyFactory = new DefaultCacheKeyFactory() {
            @Override
            protected Uri getCacheKeySourceUri(Uri sourceUri) {
                if (sourceUri == null) return null;
                String key = sourceUri.toString();
                String cacheKey = getOssPathByUrl(key);
                return Uri.parse(cacheKey);
            }
        };
        // 设置缓存目录
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(app)
                .setBaseDirectoryPath(ResHelper.createFrescoCacheDir())
                .build();
        // 初始化配置
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(app)
                .setCacheKeyFactory(keyFactory)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig()) // 渐进式实现
                .setDownsampleEnabled(true) // 向下采样
                .setBitmapsConfig(Bitmap.Config.ARGB_8888) // 小图RGB_565 大图ARGB8888
                .build();
        // 开始初始化
        Fresco.initialize(app, config);
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
        imagePipeline.clearDiskCaches();
    }

    // 获取磁盘大小
    public static long getDiskCachesSize() {
        FileCache mainFileCache = Fresco.getImagePipelineFactory().getMainFileCache();
        mainFileCache.trimToMinimum(); // 防止大小为-1
        return mainFileCache.getSize();
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
                .setAutoPlayAnimations(false); // gif不支持自动播放，(此app不支持gif播放)
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

    // url转oss路径
    private static String getOssPathByUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        // 先剔除http:// 和 https://
        if (url.startsWith("http")) {
            String[] split = url.trim().split("//");
            if (split.length >= 2) {
                url = split[1];
            }
        }
        // 再剔除get参数
        if (url.contains("?")) {
            String[] split = url.trim().split("\\?");
            if (split.length > 0) {
                url = split[0];
            }
        }
        // 再剔除oss的endpoint
        String domain = SPHelper.getOssInfo().getDomain();
        if (url.contains(domain + "/")) {
            String[] split = url.trim().split(domain + "/");
            if (split.length >= 2) {
                url = split[1];
            }
        }
        return url;
    }

}
