package com.jiangzg.base.component;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;

import java.io.File;

/**
 * Created by JZG on 2018/4/26.
 * 广播工具类
 */
public class BroadcastUtils {

    // 多媒体文件删除操作
    @SuppressLint("MissingPermission")
    public static void refreshMediaFileDelete(String providerAuth, File file) {
        if (file == null) {
            LogUtils.w(BroadcastUtils.class, "refreshMediaFileDelete", "file == null");
            return;
        }
        AppBase app = AppBase.getInstance();
        // 获取多媒体文件的标识
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver mContentResolver = app.getContentResolver();
        String where = MediaStore.Images.Media.DATA + "='" + file.getAbsolutePath() + "'";
        LogUtils.i(BroadcastUtils.class, "refreshMediaFileDelete", "where = " + where);
        mContentResolver.delete(uri, where, null);
        // 发送广播通知已删除，重新扫描
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(ProviderUtils.getUriByFile(providerAuth, file));
        app.sendBroadcast(intent);
    }

    // 多媒体图片增加操作
    @SuppressLint("MissingPermission")
    public static void refreshMediaImageInsert(String providerAuth, File file) {
        if (file == null) {
            LogUtils.w(BroadcastUtils.class, "insertImage", "file == null");
            return;
        }
        AppBase app = AppBase.getInstance();
        // 就不往照片里插入了
        //try {
        //    MediaStore.Images.Media.insertImage(app.getContentResolver(), file.getAbsolutePath(), file.getName(), null);
        //} catch (FileNotFoundException e) {
        //    LogUtils.e(BroadcastUtils.class, "insertImage", e);
        //}
        // 发送广播通知已添加，重新扫描
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(ProviderUtils.getUriByFile(providerAuth, file));
        app.sendBroadcast(intent);
        // 二次扫描，利用MediaScannerConnection来做
        MediaScannerConnection.scanFile(app, new String[]{file.getAbsolutePath()}, null, null);
    }

}
