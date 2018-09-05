package com.jiangzg.lovenote_admin.helper;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 文件管理
 */
public class ResHelper {

    // 获取具有缓存的文件夹
    private static List<String> getCaches() {
        List<String> filesList = new ArrayList<>();
        String inCacheDir = AppInfo.get().getInCacheDir();
        String outCacheDir = AppInfo.get().getOutCacheDir();
        filesList.add(inCacheDir);
        filesList.add(outCacheDir);
        return filesList;
    }

    // 获取具有缓存的文件大小
    private static long getCachesSize() {
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

    // 获取具有缓存的文件大小,带fmt
    public static String getCachesSizeFmt() {
        long cacheLength = getCachesSize();
        return ConvertUtils.byte2FitSize(cacheLength);
    }

    // 清除缓存
    public static void clearCaches() {
        // app
        List<String> cacheFiles = getCaches();
        for (String cache : cacheFiles) {
            FileUtils.deleteFilesAndDirInDir(new File(cache));
        }
        // fresco
        FrescoHelper.clearDiskCaches();
    }

    /**
     * ****************************************File****************************************
     */
    // 图片-fresco缓存目录
    public static File createFrescoCacheDir() {
        File file = new File(AppInfo.get().getOutCacheDir(), "fresco");
        FileUtils.createOrExistsDir(file);
        return file;
    }

}
