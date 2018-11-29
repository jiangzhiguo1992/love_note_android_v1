package com.jiangzg.lovenote.helper;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.BroadcastUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.lovenote.main.MyApp;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 文件管理
 */
public class ResHelper {

    // 要和manifest中的一致
    public static String PROVIDER_AUTH;

    public static void initApp() {
        PROVIDER_AUTH = MyApp.get().getPackageName() + ".fileprovider";
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
                if (cache == null) continue;
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
                        BroadcastUtils.refreshMediaFileDelete(ResHelper.PROVIDER_AUTH, file);
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
                            BroadcastUtils.refreshMediaFileDelete(ResHelper.PROVIDER_AUTH, file);
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
    // 图片-oss目录
    public static String getOssResDirPath() {
        String dir; // 7.0以下不能分享文件给外部应用(主要是音视频播放)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dir = AppInfo.get().getInFilesDir();
        } else {
            dir = AppInfo.get().getOutFilesDir();
        }
        return dir + File.separator + "oss" + File.separator;
    }

    // apk-目录
    public static File newApkDir() {
        return new File(AppInfo.get().getOutFilesDir(), "apk");
    }

    // 图片-缓存目录
    public static File createImgCacheDir() {
        File dir = new File(AppInfo.get().getOutFilesDir(), "mm_img_cache");
        FileUtils.createOrExistsDir(dir);
        return dir;
    }

    // 图片-缓存文件
    public static File newImageCacheFile() {
        String fileName = StringUtils.getUUID(10) + ".jpeg";
        File file = new File(createImgCacheDir(), fileName);
        LogUtils.d(ResHelper.class, "newImageCacheFile", file.getAbsolutePath());
        return file;
    }

    // apk-文件
    public static File newApkFile(String versionName) {
        File apkFile = new File(newApkDir(), versionName + ".apk");
        LogUtils.d(ResHelper.class, "newApkFile", apkFile.getAbsolutePath());
        return apkFile;
    }

    // 图片-fresco缓存目录
    public static File createFrescoCacheDir() {
        File file = new File(AppInfo.get().getOutCacheDir(), "fresco");
        FileUtils.createOrExistsDir(file);
        return file;
    }

    // 图片-downLoad目录
    public static File createSdCardImageDir() {
        String dirName = "YuSheng";
        File dir = new File(AppInfo.get().getSdCardDir(), dirName);
        FileUtils.createOrExistsDir(dir);
        return dir;
    }

    // 图片-downLoad文件
    public static File newSdCardImageFile(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(ResHelper.class, "newSdCardImageFile", "objectKey == null");
            return null;
        }
        String[] split = objectKey.split("/");
        if (split.length <= 0) {
            LogUtils.w(ResHelper.class, "newSdCardImageFile", "split == null");
            return null;
        }
        String fileName = split[split.length - 1];
        File file = new File(createSdCardImageDir(), fileName);
        LogUtils.d(ResHelper.class, "newSdCardImageFile", file.getAbsolutePath());
        return file;
    }

}
