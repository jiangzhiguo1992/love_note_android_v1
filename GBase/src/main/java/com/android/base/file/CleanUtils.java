package com.android.base.file;

import android.content.res.Configuration;
import android.util.Log;

import com.android.base.component.application.AppContext;
import com.android.base.component.application.AppInfo;
import com.android.base.component.application.AppListener;
import com.android.base.component.application.AppBase;
import com.android.base.function.ConfigUtils;
import com.android.base.string.ConvertUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gg on 2017/4/9.
 * 清理工具类 todo wait test
 */
public class CleanUtils {

    private static final String LOG_TAG = "CleanUtils";

    public static void initApp() {
        AppListener.addComponentListener("CleanUtils", new AppListener.ComponentListener() {
            @Override
            public void onTrimMemory(int level) {
                Log.d(LOG_TAG, "清理内存，级别:" + level);
            }

            @Override
            public void onLowMemory() {
                Log.e(LOG_TAG, "内存警告!");
                // System.gc();
                Runtime.getRuntime().gc();
            }

            @Override
            public void onConfigurationChanged(Configuration cfg) {
                String log = ConfigUtils.getLog(cfg);
                Log.d(LOG_TAG, "App配置发生变化:" + log);
            }
        });
    }

    /**
     * **********************************缓存**********************************
     * 获取具有缓存的文件夹
     */
    public static List<String> getCacheFiles() {
        String filesDir = AppInfo.get().getFilesDir();
        String cacheDir = AppInfo.get().getCacheDir();
        File internalFilesDir = AppContext.get().getFilesDir();
        File internalCacheDir = AppContext.get().getCacheDir();

        List<String> filesList = new ArrayList<>();
        filesList.add(filesDir);
        filesList.add(cacheDir);
        filesList.add(internalFilesDir.getAbsolutePath());
        filesList.add(internalCacheDir.getAbsolutePath());
        return filesList;
    }

    /**
     * 获取具有缓存的文件大小
     */
    public static long getCacheLength() {
        List<String> cacheFiles = getCacheFiles();
        long size = 0;
        for (String cache : cacheFiles) {
            size += new File(cache).length();
        }
        return size;
    }

    /**
     * 获取具有缓存的文件大小
     */
    public static String getCacheSize() {
        long cacheLength = getCacheLength();
        return ConvertUtils.byte2FitSize(cacheLength);
    }

    /**
     * 清除缓存
     */
    public static void clearCache() {
        List<String> cacheFiles = getCacheFiles();
        for (String cache : cacheFiles) {
            FileUtils.deleteFilesAndDirInDir(new File(cache));
        }
    }

    /**
     * **********************************资源**********************************
     * 获取资源文件夹
     */
    public static String getResFile() {
        return AppInfo.get().getResDir();
    }

    /**
     * 获取资源的文件大小
     */
    public static long getResLength() {
        File resDir = new File(getResFile());
        return resDir.length();
    }

    /**
     * 获取资源的文件大小
     */
    public static String getResSize() {
        long resLength = getResLength();
        return ConvertUtils.byte2FitSize(resLength);
    }

    /**
     * 清除所有资源
     */
    public static void clearRes() {
        String resDir = getResFile();
        FileUtils.deleteFilesAndDirInDir(resDir);
    }

}
