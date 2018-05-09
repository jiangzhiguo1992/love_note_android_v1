package com.jiangzg.mianmian.helper;

import android.content.res.Configuration;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.BroadcastUtils;
import com.jiangzg.mianmian.base.MyApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 文件管理
 */
public class ResHelper {

    private static final String LOG_TAG = "ResHelper";

    public static void initApp() {
        AppListener.addComponentListener(LOG_TAG, new AppListener.ComponentListener() {
            @Override
            public void onTrimMemory(int level) {
                LogUtils.d(LOG_TAG, "清理内存，级别:" + level);
            }

            @Override
            public void onLowMemory() {
                LogUtils.w(LOG_TAG, "内存警告!");
                FrescoHelper.clearMemoryCaches();
                // System.gc();
                // Runtime.getRuntime().gc();
            }

            @Override
            public void onConfigurationChanged(Configuration cfg) {
            }
        });
    }

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

    public static void deleteFileInBackground(final File file) {
        if (!FileUtils.isFileExists(file)) return;
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                LogUtils.i(LOG_TAG, "deleteFileInBackground: " + file.getAbsolutePath());
                FileUtils.deleteFile(file);
                // 发送删除广播
                BroadcastUtils.refreshMediaFile(file);
            }
        });
    }

    public static void deleteFileListInBackground(final List<File> fileList) {
        if (fileList == null || fileList.size() <= 0) return;
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                for (File file : fileList) {
                    if (!FileUtils.isFileExists(file)) return;
                    LogUtils.i(LOG_TAG, "deleteFileListInBackground: " + file.getAbsolutePath());
                    FileUtils.deleteFile(file);
                    // 发送删除广播
                    BroadcastUtils.refreshMediaFile(file);
                }
            }
        });
    }

    /**
     * ****************************************File****************************************
     */
    public static File newImageOutCache() {
        String fileName = StringUtils.getUUID(10) + ".jpeg";
        File file = new File(getImgCacheDir(), fileName);
        LogUtils.i(LOG_TAG, "newImageOutCache: " + file.getAbsolutePath());
        return file;
    }

    public static File getImgCacheDir() {
        File dir = new File(AppInfo.get().getOutCacheDir(), "image");
        FileUtils.createOrExistsDir(dir);
        return dir;
    }

    public static File getFrescoCacheDir() {
        File file = new File(AppInfo.get().getOutCacheDir(), "fresco");
        FileUtils.createOrExistsDir(file);
        return file;
    }

    public static File getAvatarDir() {
        File dir = new File(AppInfo.get().getOutFilesDir(), "avatar");
        FileUtils.createOrExistsDir(dir);
        return dir;
    }

    public static File getWallPaperDir() {
        File dir = new File(AppInfo.get().getOutFilesDir(), "wallpaper");
        FileUtils.createOrExistsDir(dir);
        return dir;
    }

    public static File createApkFile(String fileName) {
        File file = new File(AppInfo.get().getOutCacheDir() + File.separator + "apk", fileName);
        FileUtils.createFileByDeleteOldFile(file);
        LogUtils.i(LOG_TAG, "createApkFile: " + file.getAbsolutePath());
        return file;
    }

    /**
     * ****************************************Avatar****************************************
     */
    // 获取avatar的本地文件
    public static File newAvatarFile(String ossKey) {
        String name = ConvertHelper.getFileNameByOssPath(ossKey);
        if (StringUtils.isEmpty(name)) {
            LogUtils.w(LOG_TAG, "newAvatarFile: name == null");
            return null;
        }
        return new File(ResHelper.getAvatarDir(), name);
    }

    // 获取avatar目录里的所有文件
    public static List<File> getAvatarDirFiles() {
        File dir = ResHelper.getAvatarDir();
        return FileUtils.listFilesAndDirInDir(dir, true);
    }

    /**
     * ****************************************WallPaper****************************************
     */
    // 获取wp的本地文件
    public static File newWallPaperFile(String ossKey) {
        String name = ConvertHelper.getFileNameByOssPath(ossKey);
        if (StringUtils.isEmpty(name)) {
            LogUtils.w(LOG_TAG, "newWallPaperFile: name == null");
            return null;
        }
        return new File(ResHelper.getWallPaperDir(), name);
    }

    // 获取wp目录里的所有文件
    public static List<File> getWallPaperDirFiles() {
        File dir = ResHelper.getWallPaperDir();
        return FileUtils.listFilesAndDirInDir(dir, true);
    }

    // 获取随机的wp
    public static File getWallPaperRandom() {
        List<File> fileList = getWallPaperDirFiles();
        if (fileList == null || fileList.size() <= 0) {
            LogUtils.i(LOG_TAG, "getWallPaperRandom: 没有发现WallPaper的存储目录");
            return null;
        }
        Random random = new Random();
        int nextInt = random.nextInt(fileList.size());
        return fileList.get(nextInt);
    }

}
