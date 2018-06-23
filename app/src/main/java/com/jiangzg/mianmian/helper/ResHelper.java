package com.jiangzg.mianmian.helper;

import android.app.Activity;
import android.content.res.Configuration;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.BroadcastUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.mianmian.base.MyApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 文件管理
 */
public class ResHelper {

    public static void initApp() {
        AppListener.addComponentListener("ResHelper", new AppListener.ComponentListener() {
            @Override
            public void onTrimMemory(int level) {
            }

            @Override
            public void onLowMemory() {
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
        final Activity top = ActivityStack.getTop();
        if (top == null) return;
        //if (!FileUtils.isFileExists(file)) return; // 有时候存在文件也会返回false
        if (file == null) return;
        PermUtils.requestPermissions(top, ConsHelper.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                MyApp.get().getThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.d(ResHelper.class, "deleteFileInBackground", file.getAbsolutePath());
                        FileUtils.deleteFile(file);
                        // 发送删除广播
                        BroadcastUtils.refreshMediaFile(file);
                    }
                });
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(top);
            }
        });
    }

    public static void deleteFileListInBackground(final List<File> fileList) {
        final Activity top = ActivityStack.getTop();
        if (top == null) return;
        if (fileList == null || fileList.size() <= 0) return;
        PermUtils.requestPermissions(top, ConsHelper.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                MyApp.get().getThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        for (File file : fileList) {
                            if (!FileUtils.isFileExists(file)) return;
                            LogUtils.d(ResHelper.class, "deleteFileListInBackground", file.getAbsolutePath());
                            FileUtils.deleteFile(file);
                            // 发送删除广播
                            BroadcastUtils.refreshMediaFile(file);
                        }
                    }
                });
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(top);
            }
        });
    }

    /**
     * ****************************************File****************************************
     */
    // 图片
    public static File getImgCacheDir() {
        File dir = new File(AppInfo.get().getOutCacheDir(), "img");
        FileUtils.createOrExistsDir(dir);
        return dir;
    }

    public static File newImageOutCacheFile() {
        String fileName = StringUtils.getUUID(10) + ".jpeg";
        File file = new File(getImgCacheDir(), fileName);
        LogUtils.d(ResHelper.class, "newImageOutCacheFile", file.getAbsolutePath());
        return file;
    }

    // apk
    public static File newApkDir() {
        return new File(AppInfo.get().getOutFilesDir(), "apk");
    }

    public static File newApkFile(String versionName) {
        File apkFile = new File(newApkDir(), versionName + ".apk");
        LogUtils.d(ResHelper.class, "newApkFile", apkFile.getAbsolutePath());
        return apkFile;
    }

    // fresco
    public static File getFrescoCacheDir() {
        File file = new File(AppInfo.get().getOutCacheDir(), "fresco");
        FileUtils.createOrExistsDir(file);
        return file;
    }

    // oss
    public static String getOssResDirPath() {
        return AppInfo.get().getOutFilesDir() + File.separator + "oss" + File.separator;
    }

}
