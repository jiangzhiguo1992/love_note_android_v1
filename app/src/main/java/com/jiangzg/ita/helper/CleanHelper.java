package com.jiangzg.ita.helper;

import android.content.res.Configuration;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.ita.view.GImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gg on 2017/4/9.
 * 清理工具类
 */
public class CleanHelper {

    private static final String LOG_TAG = "CleanUtils";

    public static void initApp() {
        AppListener.addComponentListener(LOG_TAG, new AppListener.ComponentListener() {
            @Override
            public void onTrimMemory(int level) {
                LogUtils.d(LOG_TAG, "清理内存，级别:" + level);
            }

            @Override
            public void onLowMemory() {
                LogUtils.w(LOG_TAG, "内存警告!");
                GImageView.clearMemoryCaches();
                // System.gc();
                // Runtime.getRuntime().gc();
            }

            @Override
            public void onConfigurationChanged(Configuration cfg) {
            }
        });
    }

    /**
     * **********************************缓存**********************************
     * 获取具有缓存的文件夹
     */
    private static List<String> getCaches() {
        List<String> filesList = new ArrayList<>();
        // app
        String appCacheDir = AppInfo.get().getCacheDir();
        filesList.add(appCacheDir);
        // ...
        //String resCacheDir = AppInfo.get().getResCacheDir();
        //filesList.add(resCacheDir);
        return filesList;
    }

    /**
     * 获取具有缓存的文件大小
     */
    private static long getCachesSize() {
        // app
        List<String> cacheFiles = getCaches();
        long size = 0;
        for (String path : cacheFiles) {
            List<File> files = FileUtils.listFilesAndDirInDir(path, true);
            for (File cache : files) {
                size += cache.length();
            }
        }
        return size;
        // fresco 已加入appCache
        //long frescoSize = GImageView.getDiskCachesSize();
        //return size + frescoSize;
    }

    /**
     * 获取具有缓存的文件大小,带fmt
     */
    public static String getCachesSizeFmt() {
        long cacheLength = getCachesSize();
        return ConvertUtils.byte2FitSize(cacheLength);
    }

    /**
     * 清除缓存
     */
    public static void clearCaches() {
        // app
        List<String> cacheFiles = getCaches();
        for (String cache : cacheFiles) {
            FileUtils.deleteFilesAndDirInDir(new File(cache));
        }
        // fresco
        GImageView.clearDiskCaches();
    }

    /**
     * **********************************资源**********************************
     * 获取资源文件夹
     */
    public static List<String> getFiles() {
        List<String> filesList = new ArrayList<>();
        // app
        String appFilesDir = AppInfo.get().getFilesDir();
        filesList.add(appFilesDir);
        // ...
        //String resFilesDir = AppInfo.get().getResFilesDir();
        //filesList.add(resFilesDir);
        return filesList;
    }

    /**
     * 获取资源的文件大小
     */
    public static long getFilesSize() {
        List<String> resFiles = getFiles();
        long size = 0;
        for (String res : resFiles) {
            size += new File(res).length();
        }
        return size;
    }

    /**
     * 获取资源的文件大小
     */
    public static String getFilesSizeFmt() {
        long resLength = getFilesSize();
        return ConvertUtils.byte2FitSize(resLength);
    }

    /**
     * 清除所有资源
     */
    public static void clearFiles() {
        List<String> resFile = getFiles();
        for (String res : resFile) {
            FileUtils.deleteFilesAndDirInDir(new File(res));
        }
    }

}
