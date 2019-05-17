package com.jiangzg.lovenote.helper.common;

import android.app.Activity;
import android.content.pm.ProviderInfo;
import android.content.res.Configuration;
import android.os.Environment;

import com.jiangzg.base.application.AppListener;
import com.jiangzg.base.application.AppUtils;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.BroadcastUtils;
import com.jiangzg.base.system.DeviceInfo;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.media.FrescoHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.main.MyApp;

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

    public static String getFileProviderAuth() {
        String auth = MyApp.get().getPackageName() + ".fileprovider";
        ProviderInfo providerInfo = AppUtils.getAppProviderInfo(MyApp.get(), "android.support.v4.content.FileProvider");
        if (providerInfo == null) return auth;
        if (StringUtils.isEmpty(providerInfo.authority)) return auth;
        return providerInfo.authority;
    }

    public static void deleteFileInBackground(final File file) {
        final Activity top = ActivityStack.getTop();
        if (top == null) return;
        //if (!FileUtils.isFileExists(file)) return; // 有时候存在文件也会返回false
        if (file == null) return;
        PermUtils.requestPermissions(top, BaseActivity.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                MyApp.get().getThread().execute(() -> {
                    LogUtils.d(ResHelper.class, "deleteFileInBackground", file.getAbsolutePath());
                    FileUtils.deleteFile(file);
                    // 发送删除广播
                    BroadcastUtils.refreshMediaFileDelete(ResHelper.getFileProviderAuth(), file);
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
        PermUtils.requestPermissions(top, BaseActivity.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                MyApp.get().getThread().execute(() -> {
                    for (File file : fileList) {
                        if (!FileUtils.isFileExists(file)) return;
                        LogUtils.d(ResHelper.class, "deleteFileListInBackground", file.getAbsolutePath());
                        FileUtils.deleteFile(file);
                        // 发送删除广播
                        BroadcastUtils.refreshMediaFileDelete(ResHelper.getFileProviderAuth(), file);
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
     * ****************************************Cache****************************************
     */
    // 获取具有缓存的文件夹
    private static List<String> getCaches() {
        List<String> filesList = new ArrayList<>();
        String inCacheDir = DeviceInfo.get().getInCacheDir();
        String outCacheDir = DeviceInfo.get().getOutCacheDir();
        String imgCache = createImgCacheDir().getAbsolutePath();
        String apkCache = newApkDir().getAbsolutePath();
        filesList.add(inCacheDir);
        filesList.add(outCacheDir);
        filesList.add(imgCache);
        filesList.add(apkCache);
        return filesList;
    }

    public static long getMaxCacheSize() {
        long maxSize = Environment.getExternalStorageDirectory().getTotalSpace() / 2;
        if (maxSize <= 0) maxSize = 10L * FileUtils.GB;
        return maxSize;
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

    /**
     * ****************************************Oss****************************************
     */
    public static final long FILE_DOWNLOAD_WAIT = TimeUnit.MIN * 10;
    public static final int TYPE_COUPLE_AVATAR = 100;
    public static final int TYPE_COUPLE_WALL = 110;
    //public static final int TYPE_NOTE_AUDIO = 200;
    //public static final int TYPE_NOTE_VIDEO_THUMB = 210;
    //public static final int TYPE_NOTE_VIDEO = 220;
    //public static final int TYPE_NOTE_ALBUM = 230;
    //public static final int TYPE_NOTE_PICTURE = 240;
    //public static final int TYPE_NOTE_WHISPER = 250;
    //public static final int TYPE_NOTE_DIARY = 260;
    //public static final int TYPE_NOTE_GIFT = 270;
    //public static final int TYPE_NOTE_FOOD = 280;
    //public static final int TYPE_NOTE_MOVIE = 290;

    // 获取ossKey的文件
    public static File newKeyFile(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(ResHelper.class, "newOutFile", "objectKey == null");
            return null;
        }
        return new File(getOssResDirPath(), objectKey);
    }

    // ossKey的文件是否存在
    public static boolean isKeyFileExists(String objectKey) {
        File file = newKeyFile(objectKey);
        boolean fileExists = FileUtils.isFileExists(file);
        LogUtils.d(ResHelper.class, "isKeyFileExists", "oss文件存在性: " + objectKey + " = " + fileExists);
        return fileExists;
    }

    // ossKey的文件名
    static String getKeyFileName(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(ResHelper.class, "getKeyFileName", "objectKey == null");
            return "";
        }
        String[] split = objectKey.split("/");
        return split[split.length - 1].trim();
    }

    // 获取具体oss类型的dir
    private static File getResDir(int ossType) {
        String dirPath = "";
        switch (ossType) {
            case TYPE_COUPLE_AVATAR: // 头像
                dirPath = SPHelper.getOssInfo().getPathCoupleAvatar();
                break;
            case TYPE_COUPLE_WALL: // 墙纸
                dirPath = SPHelper.getOssInfo().getPathCoupleWall();
                break;
            //case TYPE_NOTE_WHISPER: // 耳语
            //    dirPath = SPHelper.getOssInfo().getPathNoteWhisper();
            //    break;
            //case TYPE_NOTE_DIARY: // 日记
            //    dirPath = SPHelper.getOssInfo().getPathNoteDiary();
            //    break;
            //case TYPE_NOTE_ALBUM: // 相册
            //    dirPath = SPHelper.getOssInfo().getPathNoteAlbum();
            //    break;
            //case TYPE_NOTE_PICTURE: // 照片
            //    dirPath = SPHelper.getOssInfo().getPathNotePicture();
            //    break;
            //case TYPE_NOTE_AUDIO: // 音频
            //    dirPath = SPHelper.getOssInfo().getPathNoteAudio();
            //    break;
            //case TYPE_NOTE_VIDEO_THUMB: // 视频封面
            //    dirPath = SPHelper.getOssInfo().getPathNoteVideoThumb();
            //    break;
            //case TYPE_NOTE_VIDEO: // 视频
            //    dirPath = SPHelper.getOssInfo().getPathNoteVideo();
            //    break;
            //case TYPE_NOTE_FOOD: // 美食
            //    dirPath = SPHelper.getOssInfo().getPathNoteFood();
            //    break;
            //case TYPE_NOTE_GIFT: // 礼物
            //    dirPath = SPHelper.getOssInfo().getPathNoteGift();
            //    break;
            //case TYPE_NOTE_MOVIE: // 电影
            //    dirPath = SPHelper.getOssInfo().getPathNoteMovie();
            //    break;
        }
        if (StringUtils.isEmpty(dirPath)) {
            LogUtils.w(ResHelper.class, "getResDir", ossType + ": dirPath == null ");
            return null;
        }
        return new File(getOssResDirPath(), dirPath);
    }

    // 刷新type类型的资源，删除没有的key的文件 (一般用于一次性获取到的数据)
    public static void refreshResWithDelNoExist(int type, List<String> ossKeyList) {
        File resDir = getResDir(type);
        FileUtils.createOrExistsDir(resDir);
        refreshOssResWithDelNoExists(resDir, ossKeyList);
    }

    // 刷新本地oss资源，删除不存在的ossKey
    private static void refreshOssResWithDelNoExists(final File dir, final List<String> ossKeyList) {
        if (!FileUtils.isDir(dir)) {
            LogUtils.w(ResHelper.class, "refreshOssResWithDelNoExists", "目录不存在");
            return;
        }
        // 开线程
        MyApp.get().getThread().execute(() -> {
            List<File> existsFileList = FileUtils.listFilesAndDirInDir(dir, true);
            if (ossKeyList == null || ossKeyList.size() <= 0) {
                // 没有oss 直接删除对应的目录
                //LogUtils.d(ResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 删除全部文件");
                //if (existsFileList == null || existsFileList.size() <= 0) {
                //    LogUtils.d(ResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 没有文件");
                //} else {
                //    ResHelper.deleteFileListInBackground(existsFileList);
                //}
                return;
            }
            // 删旧的(放在上面可以少判断点)
            if (existsFileList != null && existsFileList.size() > 0) {
                // 本地有oss的文件
                for (File file : existsFileList) {
                    // 文件不存在则直接检查下一个文件
                    if (file == null) continue;
                    if (file.length() <= 0) {
                        // 空文件
                        deleteFileInBackground(file);
                        continue;
                    }
                    boolean find = false;
                    // 检查是不是oss里对应的文件
                    for (String ossKey : ossKeyList) {
                        String name = getKeyFileName(ossKey);
                        if (file.getName().trim().equals(name)) {
                            // 是对应的文件，直接走下面的流程
                            find = true;
                            break;
                        }
                    }
                    // 都检查完了，不是对应的文件则删除
                    if (!find) {
                        LogUtils.i(ResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 删不匹配的文件 == " + file.getName());
                        deleteFileInBackground(file);
                    } else {
                        LogUtils.i(ResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 留下匹配的文件 == " + file.getName());
                    }
                }
            } else {
                LogUtils.d(ResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 无文件");
            }
            // 加新的
            for (String ossKey : ossKeyList) {
                if (StringUtils.isEmpty(ossKey)) return;
                if (!isKeyFileExists(ossKey)) {
                    // 不存在的的直接下载
                    LogUtils.i(ResHelper.class, "refreshOssResWithDelNoExists", "目录(" + dir.getName() + "): 下载匹配的文件 == " + ossKey);
                    File target = newKeyFile(ossKey);
                    OssHelper.downloadFileInBackground(ossKey, target, false, null);
                }
            }
        });
    }

    // 刷新type类型的资源，删除过期的文件 (一般用于有分页加载的数据)
    //public static void refreshResWithDelExpire(int type, List<String> ossKeyList) {
    //    File resDir = getResDir(type);
    //    FileUtils.createOrExistsDir(resDir);
    //    long currentLong = DateUtils.getCurrentLong();
    //    long expireAt = currentLong - SPHelper.getLimit().getNoteResExpireSec() * 1000;
    //    refreshOssResWithDelExpire(resDir, ossKeyList, expireAt);
    //}
    //
    //// 刷新本地oss资源，删除过时的ossKey
    //private static void refreshOssResWithDelExpire(final File dir, final List<String> ossKeyList, final long expireAt) {
    //    if (!FileUtils.isDir(dir)) {
    //        LogUtils.d(ResHelper.class, "refreshOssResWithDelExpire", "目录不存在");
    //        return;
    //    }
    //    final String expireTime = DateUtils.getStr(expireAt, DateUtils.FORMAT_CHINA_Y_M_D_H_M);
    //    // 开线程
    //    MyApp.get().getThread().execute(() -> {
    //        List<File> existsFileList = FileUtils.listFilesAndDirInDir(dir, true);
    //        if (ossKeyList == null || ossKeyList.size() <= 0) {
    //            // 没有oss 直接删除对应的目录下超时的
    //            //    LogUtils.d(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 删除全部过期文件");
    //            //    if (existsFileList == null || existsFileList.size() <= 0) {
    //            //        LogUtils.d(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 没有文件");
    //            //    } else {
    //            //        for (File file : existsFileList) {
    //            //            if (file == null) continue;
    //            //            long lastModified = file.lastModified();
    //            //            String lastModifyTime = DateUtils.getStr(lastModified, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M);
    //            //            if (lastModified > expireAt) {
    //            //                LogUtils.i(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 留下没过期文件(" + file.getName() + "): 修改时间 == " + lastModifyTime + " , 过期时间 == " + expireTime);
    //            //            } else {
    //            //                LogUtils.i(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 删除已过期文件(" + file.getName() + "): 修改时间 == " + lastModifyTime + " , 过期时间 == " + expireTime);
    //            //                ResHelper.deleteFileInBackground(file);
    //            //            }
    //            //        }
    //            //    }
    //            return;
    //        }
    //        // 过期的文件 (也就是ossKeyList里不存在的文件)
    //        List<File> expireFileList = new ArrayList<>();
    //        // 获取过期的文件(放在上面可以少判断点)
    //        if (existsFileList != null && existsFileList.size() > 0) {
    //            // 本地有oss的文件
    //            for (File file : existsFileList) {
    //                // 文件不存在则直接检查下一个文件
    //                if (file == null) continue;
    //                if (file.length() <= 0) {
    //                    // 空文件
    //                    ResHelper.deleteFileInBackground(file);
    //                    continue;
    //                }
    //                boolean find = false;
    //                // 检查是不是oss里对应的文件
    //                for (String ossKey : ossKeyList) {
    //                    String keyFileName = ResHelper.getKeyFileName(ossKey);
    //                    if (file.getName().trim().equals(keyFileName.trim())) {
    //                        // 发现文件，直接走下面的流程
    //                        find = true;
    //                        break;
    //                    }
    //                }
    //                if (!find) {
    //                    // 没发现的文件，再检查一下过期时间
    //                    long lastModified = file.lastModified();
    //                    String lastModifyTime = DateUtils.getStr(lastModified, DateUtils.FORMAT_CHINA_Y_M_D_H_M);
    //                    if (lastModified > expireAt) {
    //                        LogUtils.i(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 留下没过期文件(" + file.getName() + "): 修改时间 == " + lastModifyTime + " , 过期时间 == " + expireTime);
    //                    } else {
    //                        //LogUtils.i(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 删除已过期文件(" + file.getName() + "): 修改时间 == " + lastModifyTime + " , 过期时间 == " + expireTime);
    //                        expireFileList.add(file);
    //                    }
    //                }
    //                //else {
    //                // 发现的文件 暂不更新修改时间，下面会统一修改
    //                //boolean success = file.setLastModified(DateUtils.getCurrentLong());
    //                //if (success) {
    //                //    long lastModified = file.lastModified();
    //                //    String lastModifyTime = DateUtils.getStr(lastModified, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M);
    //                //    LogUtils.i(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 更新文件(" + file.getName() + "): 过期时间 == " + lastModifyTime);
    //                //} else {
    //                //    LogUtils.w(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 更新文件(" + file.getName() + "): 过期时间 == 失败");
    //                //}
    //                //}
    //            }
    //        } else {
    //            LogUtils.d(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + ") 无文件");
    //        }
    //        // 加新的 (也就是expireFileList里不存在的文件)
    //        for (String ossKey : ossKeyList) {
    //            if (StringUtils.isEmpty(ossKey)) return;
    //            if (!ResHelper.isKeyFileExists(ossKey)) {
    //                // 不存在的的直接下载
    //                LogUtils.i(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 下载匹配的文件 == " + ossKey);
    //                File target = ResHelper.newKeyFile(ossKey);
    //                OssHelper.downloadFileInBackground(ossKey, target, false, null);
    //            } else {
    //                // 更新文件修改时间(不要在删旧的循环里更新，会重复)
    //                File file = ResHelper.newKeyFile(ossKey);
    //                if (file == null) continue;
    //                // 注意了，这里的修改不能修改成当前时间，FrescoView中会用到
    //                long modifyTime = DateUtils.getCurrentLong() - ResHelper.FILE_DOWNLOAD_WAIT;
    //                long lastModified = file.lastModified();
    //                if (lastModified < modifyTime) {
    //                    // 只能修改存放一天以上的文件，防止没下载完就开始欺骗fresco下载完了
    //                    boolean success = file.setLastModified(modifyTime);
    //                    LogUtils.i(ResHelper.class, "refreshOssResWithDelExpire", "文件(" + file.getName() + ")开始更新 " + success);
    //                } else {
    //                    LogUtils.i(ResHelper.class, "refreshOssResWithDelExpire", "文件(" + file.getName() + ")还没到可更新时间");
    //                }
    //                // 是不是在过期里有
    //                if (expireFileList.size() > 0) {
    //                    ListIterator<File> iterator = expireFileList.listIterator();
    //                    while (iterator.hasNext()) {
    //                        File expireFile = iterator.next();
    //                        if (expireFile.getName().trim().equals(file.getName().trim())) {
    //                            // 过期文件里有oss文件，则从过期里删除
    //                            LogUtils.d(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 取消删除过期但刚更新的文件(" + file.getName() + ")");
    //                            iterator.remove();
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //        // 最后删除过期文件
    //        if (expireFileList.size() > 0) {
    //            for (File file : expireFileList) {
    //                if (file == null) continue;
    //                long lastModified = file.lastModified();
    //                String lastModifyTime = DateUtils.getStr(lastModified, DateUtils.FORMAT_CHINA_Y_M_D_H_M);
    //                LogUtils.i(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 删除已过期文件(" + file.getName() + "): 修改时间 == " + lastModifyTime + " , 过期时间 == " + expireTime);
    //            }
    //            ResHelper.deleteFileListInBackground(expireFileList);
    //        } else {
    //            LogUtils.i(ResHelper.class, "refreshOssResWithDelExpire", "目录(" + dir.getName() + "): 没有发现过期文件");
    //        }
    //    });
    //}

    /**
     * ****************************************File****************************************
     */
    // web-=缓存目录
    public static File createWebCacheDir() {
        File file = new File(DeviceInfo.get().getInCacheDir(), "web_cache");
        FileUtils.createOrExistsDir(file);
        return file;
    }

    // 图片-oss目录
    public static String getOssResDirPath() {
        String dir = DeviceInfo.get().getOutFilesDir();
        return dir + File.separator + "oss" + File.separator;
    }

    // 图片-fresco缓存目录
    public static File createFrescoCacheDir() {
        File file = new File(DeviceInfo.get().getOutCacheDir(), "fresco");
        FileUtils.createOrExistsDir(file);
        return file;
    }

    // 音视频-exo缓存目录
    public static File createExoCacheDir() {
        File file = new File(DeviceInfo.get().getOutCacheDir(), "exo");
        FileUtils.createOrExistsDir(file);
        return file;
    }

    // downLoad目录
    private static File createSdCardDir() {
        String dirName = "YuSheng";
        File dir = new File(DeviceInfo.get().getSdCardDir(), dirName);
        FileUtils.createOrExistsDir(dir);
        return dir;
    }

    // downLoad文件
    public static File newDownLoadFile(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(ResHelper.class, "newDownLoadFile", "objectKey == null");
            return null;
        }
        String[] split = objectKey.split("/");
        if (split.length <= 0) {
            LogUtils.w(ResHelper.class, "newDownLoadFile", "split == null");
            return null;
        }
        String fileName = split[split.length - 1];
        File file = new File(createSdCardDir(), fileName);
        LogUtils.d(ResHelper.class, "newDownLoadFile", file.getAbsolutePath());
        return file;
    }

    // apk-目录
    public static File newApkDir() {
        return new File(DeviceInfo.get().getOutFilesDir(), "apk");
    }

    // apk-文件
    public static File newApkFile(String versionName) {
        File apkFile = new File(newApkDir(), versionName + ".apk");
        LogUtils.d(ResHelper.class, "newApkFile", apkFile.getAbsolutePath());
        return apkFile;
    }

    // 图片-缓存目录
    public static File createImgCacheDir() {
        File dir = new File(DeviceInfo.get().getOutFilesDir(), "mm_img_cache");
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

}
