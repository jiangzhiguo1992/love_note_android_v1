package com.jiangzg.ita.helper;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.FileUtils;

import java.io.File;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 文件管理
 * todo 搞一搞 注意权限
 */
public class ResHelper {

    public static File createJPEGInCache() {
        String fileName = StringUtils.getUUID(8) + ".jpeg";
        File jpgFile = new File(AppInfo.get().getCacheDir(), fileName);
        FileUtils.createFileByDeleteOldFile(jpgFile);
        return jpgFile;
    }

    //public static File createJPEGInFiles() {
    //    String fileName = StringUtils.getUUID(8) + ".jpeg";
    //    File jpgFile = new File(AppInfo.get().getResFilesDir(), fileName);
    //    FileUtils.createFileByDeleteOldFile(jpgFile);
    //    return jpgFile;
    //}

    //public static File createAPKInRes(String versionName) {
    //    String fileName = versionName + ".apk";
    //    //File apkFile = new File(AppInfo.get().getResDir(), fileName);
    //    File apkFile = new File("", fileName);
    //    FileUtils.createFileByDeleteOldFile(apkFile);
    //    return apkFile;
    //}

    /**
     * 获取外存使用情况: getRealSDCardFile().getXXXXSpace()
     */
    //public static File getRealSDCardFile() {
    //    File dir = new File(AppInfo.getSDCardPath());
    //    FileUtils.createOrExistsDir(dir);
    //    return dir;
    //}

    //public static boolean saveImgToWelcome(File img) {
    //    String resFilesDir = AppInfo.get().getResFilesDir();
    //
    //    return FileUtils.createFileByDeleteOldFile(img);
    //}

}
