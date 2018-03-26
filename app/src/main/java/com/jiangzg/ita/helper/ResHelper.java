package com.jiangzg.ita.helper;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.application.AppInfo;
import com.jiangzg.base.file.FileUtils;

import java.io.File;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 文件管理
 */
public class ResHelper {

    public static boolean saveImgToWelcome(File img) {
        String resFilesDir = AppInfo.get().getResFilesDir();

        return FileUtils.createFileByDeleteOldFile(img);
    }

    // 不能暴露给外部，缓存清理给清掉
    public static File createJPEGInFiles() {
        String fileName = StringUtils.getUUID(8) + ".jpeg";
        File jpgFile = new File(AppInfo.get().getResFilesDir(), fileName);
        FileUtils.createFileByDeleteOldFile(jpgFile);
        return jpgFile;
    }

    //public static File createJPEGInRes() {
    //    String fileName = StringUtils.getUUID(8) + ".jpeg";
    //    //File jpgFile = new File(AppInfo.get().getResDir(), fileName);
    //    File jpgFile = new File("", fileName);
    //    FileUtils.createFileByDeleteOldFile(jpgFile);
    //    return jpgFile;
    //}

    public static File createAPKInRes(String versionName) {
        String fileName = versionName + ".apk";
        //File apkFile = new File(AppInfo.get().getResDir(), fileName);
        File apkFile = new File("", fileName);
        FileUtils.createFileByDeleteOldFile(apkFile);
        return apkFile;
    }

}
