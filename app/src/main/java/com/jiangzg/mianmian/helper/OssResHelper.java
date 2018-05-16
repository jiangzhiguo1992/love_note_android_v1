package com.jiangzg.mianmian.helper;

import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.WallPaper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by JZG on 2018/5/14.
 * OssResHelper
 */
public class OssResHelper {

    private static final String LOG_TAG = "OssResHelper";

    // 获取ossKey的文件
    public static File newKeyFile(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(LOG_TAG, "newOutFile: objectKey == null");
            return null;
        }
        LogUtils.i(LOG_TAG, "newOutFile: objectKey == " + objectKey);
        return new File(ResHelper.getOssOutDirPath(), objectKey);
    }

    // 获取ossKey的文件所在目录的所有文件
    public static List<File> getKeyParentFiles(String objectKey) {
        File file = newKeyFile(objectKey);
        if (file == null) {
            LogUtils.w(LOG_TAG, "getDirFilesByKey: file == null");
            return new ArrayList<>();
        }
        //StringBuilder builder = new StringBuilder();
        //String[] split = objectKey.split("/");
        //for (int i = 0; i < split.length - 1; i++) {
        //    // 不要最后一个
        //    builder.append(split[i]).append("/");
        //}
        //String diaPath = builder.toString();
        //File dir = new File(AppInfo.get().getOutFilesDir(), diaPath);
        File dir = file.getParentFile();
        FileUtils.createOrExistsDir(dir);
        return FileUtils.listFilesAndDirInDir(dir, true);
    }

    // ossKey的文件是否存在的文件名
    public static String getKeyFileName(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            LogUtils.w(LOG_TAG, "getKeyFileName: objectKey == null");
            return "";
        }
        String[] split = objectKey.split("/");
        return split[split.length - 1].trim();
    }

    // ossKey的文件是否存在
    public static boolean isKeyFileExists(String objectKey) {
        File file = newKeyFile(objectKey);
        boolean fileExists = FileUtils.isFileExists(file);
        LogUtils.i(LOG_TAG, "isKeyFileExists: " + objectKey + " == " + fileExists);
        return fileExists;
    }

    /**
     * ****************************************Apk****************************************
     */
    public static File newApkDir() {
        return new File(ResHelper.getOssOutDirPath(), "apk");
    }

    public static File newApkFile(String versionName) {
        File apkFile = new File(OssResHelper.newApkDir(), versionName + ".apk");
        LogUtils.i(LOG_TAG, "createApkFile: " + apkFile.getAbsolutePath());
        return apkFile;
    }

    /**
     * ****************************************Avatar****************************************
     */
    // 存放avatar的目录
    public static File newAvatarDir() {
        String pathCoupleAvatar = SPHelper.getOssInfo().getPathCoupleAvatar();
        if (StringUtils.isEmpty(pathCoupleAvatar)) {
            LogUtils.w(LOG_TAG, "getAvatarDir: pathCoupleAvatar == null");
            return null;
        }
        return new File(ResHelper.getOssOutDirPath(), pathCoupleAvatar);
    }

    // 刷新本地的avatar
    public static void refreshAvatarRes() {
        // 开线程
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                final Couple couple = SPHelper.getCouple();
                String creatorAvatar = couple.getCreatorAvatar();
                String inviteeAvatar = couple.getInviteeAvatar();
                final List<String> imageList = new ArrayList<>();
                imageList.add(creatorAvatar);
                imageList.add(inviteeAvatar);
                // 删旧的
                File avatarDir = OssResHelper.newAvatarDir();
                List<File> existsFileList = FileUtils.listFilesAndDirInDir(avatarDir, true);
                if (existsFileList != null && existsFileList.size() > 0) {
                    // 本地有avatar的文件
                    for (File file : existsFileList) {
                        // 文件不存在则直接检查下一个文件
                        if (file == null) continue;
                        boolean find = false;
                        // 检查是不是oss里对应的文件
                        for (String ossKey : imageList) {
                            String name = OssResHelper.getKeyFileName(ossKey);
                            if (file.getName().trim().equals(name)) {
                                // 是对应的文件，直接检查下一个文件
                                find = true;
                                break;
                            }
                        }
                        // 都检查完了，不是对应的文件则删除
                        if (!find) {
                            LogUtils.w(LOG_TAG, "refreshAvatarRes: 删除了不匹配的avatar文件 == " + file.getAbsolutePath());
                            ResHelper.deleteFileInBackground(file);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshAvatarRes: 发现了匹配的avatar文件 == " + file.getAbsolutePath());
                        }
                    }
                } else {
                    LogUtils.w(LOG_TAG, "refreshAvatarRes: 没有发现avatar的存储目录");
                }
                // 加新的
                for (String ossKey : imageList) {
                    if (!OssResHelper.isKeyFileExists(ossKey)) {
                        // 不存在的直接下载
                        LogUtils.w(LOG_TAG, "refreshAvatarRes: 没发现匹配的oss对象 == " + ossKey);
                        OssHelper.downloadAvatar(ossKey);
                    } else {
                        LogUtils.i(LOG_TAG, "refreshAvatarRes: 发现了匹配的oss对象 == " + ossKey);
                    }
                }
            }
        });
    }

    /**
     * ****************************************WallPaper****************************************
     */
    // 获取wp目录里的所有文件
    public static File getWallPaperDir() {
        String pathCoupleWall = SPHelper.getOssInfo().getPathCoupleWall();
        if (StringUtils.isEmpty(pathCoupleWall)) {
            LogUtils.w(LOG_TAG, "getWallPaperDir: pathCoupleWall == null");
            return null;
        }
        return new File(ResHelper.getOssOutDirPath(), pathCoupleWall);
    }

    // 获取随机的wp
    public static File getWallPaperRandom() {
        File wallPaperDir = getWallPaperDir();
        List<File> fileList = FileUtils.listFilesAndDirInDir(wallPaperDir, true);
        if (fileList == null || fileList.size() <= 0) {
            LogUtils.i(LOG_TAG, "getWallPaperRandom: 没有发现WallPaper的存储目录");
            return null;
        }
        Random random = new Random();
        int nextInt = random.nextInt(fileList.size());
        return fileList.get(nextInt);
    }

    // 刷新本地的wp
    public static void refreshWallPaperRes() {
        // file 开线程
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                WallPaper wallPaper = SPHelper.getWallPaper();
                final List<String> imageList = wallPaper.getImageList();
                File wallPaperDir = OssResHelper.getWallPaperDir();
                List<File> existsFileList = FileUtils.listFilesAndDirInDir(wallPaperDir, true);
                if (imageList == null || imageList.size() <= 0) {
                    // 没有wallpaper 直接删除对应的目录
                    ResHelper.deleteFileListInBackground(existsFileList);
                    return;
                }
                // 删旧的
                if (existsFileList != null && existsFileList.size() > 0) {
                    // 本地有wp的文件
                    for (File file : existsFileList) {
                        // 文件不存在则直接检查下一个文件
                        if (file == null) continue;
                        boolean find = false;
                        // 检查是不是oss里对应的文件
                        for (String ossKey : imageList) {
                            String name = OssResHelper.getKeyFileName(ossKey);
                            if (file.getName().trim().equals(name)) {
                                // 是对应的文件，直接检查下一个文件
                                find = true;
                                break;
                            }
                        }
                        // 都检查完了，不是对应的文件则删除
                        if (!find) {
                            LogUtils.w(LOG_TAG, "refreshWallPaperRes: 删除了不匹配的WallPaper文件 == " + file.getAbsolutePath());
                            ResHelper.deleteFileInBackground(file);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshWallPaperRes: 发现了匹配的WallPaper文件 == " + file.getAbsolutePath());
                        }
                    }
                } else {
                    LogUtils.w(LOG_TAG, "refreshWallPaperRes: 没有发现WallPaper的存储目录");
                }
                // 加新的
                for (String ossKey : imageList) {
                    if (!OssResHelper.isKeyFileExists(ossKey)) {
                        // 不存在的的直接下载
                        LogUtils.w(LOG_TAG, "refreshWallPaperRes: 没发现匹配的oss对象 == " + ossKey);
                        OssHelper.downloadWall(ossKey);
                    } else {
                        LogUtils.i(LOG_TAG, "refreshWallPaperRes: 发现了匹配的oss对象 == " + ossKey);
                    }
                }
            }
        });
    }

}
