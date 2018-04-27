package com.jiangzg.mianmian.helper;

import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.WallPaper;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * Created by JZG on 2018/4/27.
 * WallPaperHelper
 */
public class WallPaperHelper {

    private static final String LOG_TAG = "WallPaperHelper";

    // 刷新本地的wp
    public static void refreshWallPaper(WallPaper wallPaper) {
        final List<String> imageList = wallPaper.getImageList();
        if (imageList == null || imageList.size() <= 0) return;
        // sp保存
        SPHelper.setWallPaper(wallPaper);
        // file 开线程
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                // 删旧的
                List<File> wallPaperFileList = getWallPaperDirFiles();
                if (wallPaperFileList != null && wallPaperFileList.size() > 0) {
                    // 本地有wp的文件
                    for (File file : wallPaperFileList) {
                        // 文件不存在则直接检查下一个文件
                        if (file == null) continue;
                        boolean find = false;
                        // 检查是不是oss里对应的文件
                        for (String ossKey : imageList) {
                            String name = ConvertHelper.getNameByOssPath(ossKey);
                            if (file.getName().trim().equals(name)) {
                                // 是对应的文件，直接检查下一个文件
                                find = true;
                                break;
                            }
                        }
                        // 都检查完了，不是对应的文件则删除
                        if (!find) {
                            LogUtils.w(LOG_TAG, "refreshWallPaper: 删除了不匹配的wp文件 == " + file.getAbsolutePath());
                            ResHelper.deleteFileInBackground(file);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshWallPaper: 发现了匹配的wp文件 == " + file.getAbsolutePath());
                        }
                    }
                } else {
                    LogUtils.w(LOG_TAG, "refreshWallPaper: 没有发现WallPaper的存储目录");
                }
                // 加新的
                List<File> newFileList = getWallPaperDirFiles();
                for (String ossKey : imageList) {
                    if (newFileList != null && newFileList.size() > 0) {
                        // 本地有信息，则检查是都已下载
                        boolean find = isWallPaperExists(ossKey);
                        // 都检查完了，没下载过的直接下载
                        if (!find) {
                            LogUtils.w(LOG_TAG, "refreshWallPaper: 没发现匹配的oss对象 == " + ossKey);
                            OssHelper.downloadWall(ossKey, null);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshWallPaper: 发现了匹配的oss对象 == " + ossKey);
                        }
                    } else {
                        // 本地无信息，则直接下载
                        LogUtils.w(LOG_TAG, "refreshWallPaper: 没有发现WallPaper的新的存储目录");
                        OssHelper.downloadWall(ossKey, null);
                    }
                }
            }
        });
    }

    // 获取wp的本地文件
    public static File newWallPaperFile(String ossKey) {
        String name = ConvertHelper.getNameByOssPath(ossKey);
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
        List<File> fileList = getWallPaperDirFiles();
        for (File file : fileList) {
            // 文件不存在则直接检查下一个文件
            if (file == null) {
                LogUtils.i(LOG_TAG, "isWallPaperExists: file == null");
                continue;
            }
            // 检查本地是不是已下载
            String name = ConvertHelper.getNameByOssPath(ossKey);
            if (file.getName().trim().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
