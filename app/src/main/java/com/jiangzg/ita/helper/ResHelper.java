package com.jiangzg.ita.helper;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;

import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.ita.base.MyApp;

import java.io.File;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 文件管理
 * todo 搞一搞 注意权限
 */
public class ResHelper {

    public static void deleteFileInBackground(final File file, final boolean media) {
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                if (FileUtils.isFileExists(file) && media) {
                    FileUtils.deleteFile(file);
                    // 多媒体文件删除操作
                    Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    ContentResolver mContentResolver = MyApp.get().getContentResolver();
                    String where = MediaStore.Images.Media.DATA + "='" + file.getAbsolutePath() + "'";
                    mContentResolver.delete(uri, where, null);
                    // 发送广播通知已删除，重新扫描
                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(ConvertUtils.file2Uri(file));
                    MyApp.get().sendBroadcast(intent);
                } else {
                    // 普通文件删除操作
                    FileUtils.deleteFile(file);
                }
            }
        });
    }

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
