package com.jiangzg.project.utils;

import com.android.base.component.application.AppInfo;
import com.android.base.file.FileUtils;
import com.android.base.string.StringUtils;

import java.io.File;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 文件管理
 */
public class ResUtils {

    public static File createJPGInFiles() {
        String fileName = StringUtils.getUUID(8) + ".jpg";
        File jpgFile = new File(AppInfo.get().getFilesDir(""), fileName);
        FileUtils.createFileByDeleteOldFile(jpgFile);
        return jpgFile;
    }

    public static File createJPGInRes() {
        String fileName = StringUtils.getUUID(8) + ".jpg";
        File jpgFile = new File(AppInfo.get().getResDir(), fileName);
        FileUtils.createFileByDeleteOldFile(jpgFile);
        return jpgFile;
    }

    public static File createAPKInRes(String versionName) {
        String fileName = versionName + ".apk";
        File apkFile = new File(AppInfo.get().getResDir(), fileName);
        FileUtils.createFileByDeleteOldFile(apkFile);
        return apkFile;
    }

}
