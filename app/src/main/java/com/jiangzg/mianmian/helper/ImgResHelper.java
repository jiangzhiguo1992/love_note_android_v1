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
 * ImgResHelper
 */
public class ImgResHelper {

    private static final String LOG_TAG = "ImgResHelper";

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

    // 检查avatar是否已下载
    public static boolean isAvatarExists(String ossKey) {
        // 获取avatar目录里的所有文件
        List<File> fileList = ImgResHelper.getAvatarDirFiles();
        for (File file : fileList) {
            // 文件不存在则直接检查下一个文件
            if (file == null) {
                LogUtils.i(LOG_TAG, "isAvatarExists: file == null");
                continue;
            }
            // 检查本地是不是已下载
            String name = ConvertHelper.getFileNameByOssPath(ossKey);
            if (file.getName().trim().equals(name)) {
                return true;
            }
        }
        return false;
    }

    // 刷新本地的avatar
    public static void refreshAvatarRes() {
        final Couple couple = SPHelper.getCouple();
        String creatorAvatar = couple.getCreatorAvatar();
        String inviteeAvatar = couple.getInviteeAvatar();
        final List<String> imageList = new ArrayList<>();
        imageList.add(creatorAvatar);
        imageList.add(inviteeAvatar);
        // file 开线程
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                // 删旧的
                List<File> fileList = ImgResHelper.getAvatarDirFiles();
                if (fileList != null && fileList.size() > 0) {
                    // 本地有avatar的文件
                    for (File file : fileList) {
                        // 文件不存在则直接检查下一个文件
                        if (file == null) continue;
                        boolean find = false;
                        // 检查是不是oss里对应的文件
                        for (String ossKey : imageList) {
                            String name = ConvertHelper.getFileNameByOssPath(ossKey);
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
                List<File> newFileList = ImgResHelper.getAvatarDirFiles();
                for (String ossKey : imageList) {
                    if (newFileList != null && newFileList.size() > 0) {
                        // 本地有信息，则检查是都已下载
                        boolean find = ImgResHelper.isAvatarExists(ossKey);
                        // 都检查完了，没下载过的直接下载
                        if (!find) {
                            LogUtils.w(LOG_TAG, "refreshAvatarRes: 没发现匹配的oss对象 == " + ossKey);
                            OssHelper.downloadAvatar(ossKey);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshAvatarRes: 发现了匹配的oss对象 == " + ossKey);
                        }
                    } else {
                        // 本地无信息，则直接下载
                        LogUtils.w(LOG_TAG, "refreshAvatarRes: 没有发现avatar的新的存储目录");
                        OssHelper.downloadAvatar(ossKey);
                    }
                }
            }
        });
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

    // 检查wp是否已下载
    public static boolean isWallPaperExists(String ossKey) {
        // 获取wp目录里的所有文件
        List<File> fileList = ImgResHelper.getWallPaperDirFiles();
        for (File file : fileList) {
            // 文件不存在则直接检查下一个文件
            if (file == null) {
                LogUtils.i(LOG_TAG, "isWallPaperExists: file == null");
                continue;
            }
            // 检查本地是不是已下载
            String name = ConvertHelper.getFileNameByOssPath(ossKey);
            if (file.getName().trim().equals(name)) {
                return true;
            }
        }
        return false;
    }

    // 刷新本地的wp
    public static void refreshWallPaperRes() {
        WallPaper wallPaper = SPHelper.getWallPaper();
        final List<String> imageList = wallPaper.getImageList();
        // file 开线程
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                // 没有wallpaper
                if (imageList == null || imageList.size() <= 0) {
                    List<File> fileList = ImgResHelper.getWallPaperDirFiles();
                    ResHelper.deleteFileListInBackground(fileList);
                    return;
                }
                // 删旧的
                List<File> fileList = ImgResHelper.getWallPaperDirFiles();
                if (fileList != null && fileList.size() > 0) {
                    // 本地有wp的文件
                    for (File file : fileList) {
                        // 文件不存在则直接检查下一个文件
                        if (file == null) continue;
                        boolean find = false;
                        // 检查是不是oss里对应的文件
                        for (String ossKey : imageList) {
                            String name = ConvertHelper.getFileNameByOssPath(ossKey);
                            if (file.getName().trim().equals(name)) {
                                // 是对应的文件，直接检查下一个文件
                                find = true;
                                break;
                            }
                        }
                        // 都检查完了，不是对应的文件则删除
                        if (!find) {
                            LogUtils.w(LOG_TAG, "refreshWallPaperRes: 删除了不匹配的wp文件 == " + file.getAbsolutePath());
                            ResHelper.deleteFileInBackground(file);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshWallPaperRes: 发现了匹配的wp文件 == " + file.getAbsolutePath());
                        }
                    }
                } else {
                    LogUtils.w(LOG_TAG, "refreshWallPaperRes: 没有发现WallPaper的存储目录");
                }
                // 加新的
                List<File> newFileList = ImgResHelper.getWallPaperDirFiles();
                for (String ossKey : imageList) {
                    if (newFileList != null && newFileList.size() > 0) {
                        // 本地有信息，则检查是都已下载
                        boolean find = ImgResHelper.isWallPaperExists(ossKey);
                        // 都检查完了，没下载过的直接下载
                        if (!find) {
                            LogUtils.w(LOG_TAG, "refreshWallPaperRes: 没发现匹配的oss对象 == " + ossKey);
                            OssHelper.downloadWall(ossKey);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshWallPaperRes: 发现了匹配的oss对象 == " + ossKey);
                        }
                    } else {
                        // 本地无信息，则直接下载
                        LogUtils.w(LOG_TAG, "refreshWallPaperRes: 没有发现WallPaper的新的存储目录");
                        OssHelper.downloadWall(ossKey);
                    }
                }
            }
        });
    }

}
