package com.jiangzg.lovenote.helper;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.component.ProviderUtils;
import com.jiangzg.base.media.BitmapUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by JZG on 2018/11/24.
 * MediaPickHelper
 */
public class MediaPickHelper {

    public static void selectImage(Activity activity, int maxCount) {
        PermUtils.requestPermissions(activity, BaseActivity.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                MediaPickHelper.selectImageWithOutPermission(activity, maxCount);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(activity);
            }
        });
    }

    private static void selectImageWithOutPermission(Activity activity, int maxCount) {
        if (activity == null) return;
        Matisse.from(activity)
                .choose(MimeType.ofImage(), false) // 文件类型
                .showSingleMediaType(true) // 只显示选择类型
                .spanCount(3) // 行列数(默认3)
                .theme(R.style.Matisse_Dracula) // 样式(自带暗黑)
                .countable(maxCount > 1) // 是否显示数字
                .maxSelectable(maxCount) // 最大选择数
                //.capture(true) //使用拍照功能
                //.captureStrategy(new CaptureStrategy(true, FILE_PATH)) //是否拍照功能，并设置拍照后图片的保存路径
                //.gridExpectedSize(activity.getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED) // 文件方向(默认值)
                .thumbnailScale(0.75f) // 缩略图(0.75倍)
                .imageEngine(new GlideEngine()) // 构造器(glide)
                .forResult(BaseActivity.REQUEST_PICTURE); // 请求码
    }

    public static File getResultFile(Activity activity, Intent data) {
        if (activity == null || data == null) {
            LogUtils.w(MediaPickHelper.class, "getResultFile", "activity == null || data == null");
            return null;
        }
        boolean permissionOK = PermUtils.isPermissionOK(activity, PermUtils.appInfo);
        if (!permissionOK) {
            PermUtils.requestPermissions(activity, BaseActivity.REQUEST_APP_INFO, PermUtils.appInfo, null);
            return null;
        }
        List<Uri> uris = Matisse.obtainResult(data);
        if (uris == null || uris.size() <= 0) {
            LogUtils.w(MediaPickHelper.class, "getResultFile", "uris == null");
            return null;
        }
        Uri uri = uris.get(0);
        File file;
        if (uri != null) {
            file = ProviderUtils.getFileByUri(uri);
        } else {
            LogUtils.w(IntentResult.class, "getResultFile", "uri == null");
            long time = new Date().getTime();
            file = new File(AppInfo.get().getInCacheDir(), time + ".jpeg");
            FileUtils.createFileByDeleteOldFile(file);
            Bitmap picture = data.getParcelableExtra("data");
            BitmapUtils.saveBitmap(picture, file.getAbsolutePath(), Bitmap.CompressFormat.JPEG, true);
        }
        return file;
    }

    public static List<File> getResultFileList(Activity activity, Intent data) {
        if (activity == null || data == null) {
            LogUtils.w(MediaPickHelper.class, "getResultFileList", "activity == null || data == null");
            return null;
        }
        boolean permissionOK = PermUtils.isPermissionOK(activity, PermUtils.appInfo);
        if (!permissionOK) {
            PermUtils.requestPermissions(activity, BaseActivity.REQUEST_APP_INFO, PermUtils.appInfo, null);
            return null;
        }
        List<Uri> uris = Matisse.obtainResult(data);
        if (uris == null || uris.size() <= 0) {
            LogUtils.w(MediaPickHelper.class, "getResultFileList", "uris == null");
            return new ArrayList<>();
        }
        List<File> fileList = new ArrayList<>();
        for (Uri uri : uris) {
            File file;
            if (uri != null) {
                file = ProviderUtils.getFileByUri(uri);
            } else {
                LogUtils.w(IntentResult.class, "getResultFileList", "uri == null");
                long time = new Date().getTime();
                file = new File(AppInfo.get().getInCacheDir(), time + ".jpeg");
                FileUtils.createFileByDeleteOldFile(file);
                Bitmap picture = data.getParcelableExtra("data");
                BitmapUtils.saveBitmap(picture, file.getAbsolutePath(), Bitmap.CompressFormat.JPEG, true);
            }
            if (file != null) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    public static List<String> getResultFilePathList(Activity activity, Intent data) {
        List<File> fileList = getResultFileList(activity, data);
        if (fileList == null || fileList.size() <= 0) return new ArrayList<>();
        return ListHelper.getPathListByFile(fileList);
    }

}
