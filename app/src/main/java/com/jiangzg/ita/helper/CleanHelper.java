package com.jiangzg.ita.helper;

import android.content.res.Configuration;
import android.util.Log;

import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;

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
                // System.gc();
                //Runtime.getRuntime().gc();
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
    public static List<String> getCacheFiles() {
        String appCacheDir = AppInfo.get().getAppCacheDir();
        String resCacheDir = AppInfo.get().getResCacheDir();
        List<String> filesList = new ArrayList<>();
        filesList.add(appCacheDir);
        filesList.add(resCacheDir);
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
     * 获取具有缓存的文件大小,带fmt
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
    public static List<String> getResFile() {
        String appFilesDir = AppInfo.get().getAppFilesDir();
        String resFilesDir = AppInfo.get().getResFilesDir();
        List<String> filesList = new ArrayList<>();
        filesList.add(appFilesDir);
        filesList.add(resFilesDir);
        return filesList;
    }

    /**
     * 获取资源的文件大小
     */
    public static long getResLength() {
        List<String> resFiles = getResFile();
        long size = 0;
        for (String res : resFiles) {
            size += new File(res).length();
        }
        return size;
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
        List<String> resFile = getResFile();
        for (String res : resFile) {
            FileUtils.deleteFilesAndDirInDir(new File(res));
        }
    }

}
