package com.jiangzg.ita.utils;

import com.jiangzg.base.component.application.AppInfo;
import com.jiangzg.base.file.FileUtils;
import com.jiangzg.base.common.StringUtils;

import java.io.File;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 文件管理
 */
public class ResUtils {

    //public static boolean saveImgToWelcome(File img) {
    //    String resFilesDir = AppInfo.get().getResFilesDir();
    //
    //    return FileUtils.createFileByDeleteOldFile(img);
    //}

    public static File createJPGInFiles() {
        String fileName = StringUtils.getUUID(8) + ".jpg";
        //File jpgFile = new File(AppInfo.get().getFilesDir(), fileName);
        File jpgFile = new File("", fileName);
        FileUtils.createFileByDeleteOldFile(jpgFile);
        return jpgFile;
    }

    public static File createJPGInRes() {
        String fileName = StringUtils.getUUID(8) + ".jpg";
        //File jpgFile = new File(AppInfo.get().getResDir(), fileName);
        File jpgFile = new File("", fileName);
        FileUtils.createFileByDeleteOldFile(jpgFile);
        return jpgFile;
    }

    public static File createAPKInRes(String versionName) {
        String fileName = versionName + ".apk";
        //File apkFile = new File(AppInfo.get().getResDir(), fileName);
        File apkFile = new File("", fileName);
        FileUtils.createFileByDeleteOldFile(apkFile);
        return apkFile;
    }

}
