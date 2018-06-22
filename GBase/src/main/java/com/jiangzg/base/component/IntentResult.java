package com.jiangzg.base.component;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.media.BitmapUtils;

import java.io.File;
import java.util.Date;


/**
 * Created by JZG on 2018/3/30.
 * intent的result
 */
public class IntentResult {

    private static final String LOG_TAG = "IntentResult";

    /**
     * IntentFactory.getContacts返回时调用
     * 在onActivityResult中调用，获取选中的号码
     */
    public static String getContactSelect(Intent data) {
        if (data == null) {
            LogUtils.w(LOG_TAG, "getContactSelect: data == null");
            return "";
        }
        Uri uri = data.getData();
        Cursor cursor = null;
        if (uri != null) {
            ContentResolver contentResolver = AppBase.getInstance().getContentResolver();
            cursor = contentResolver.query(uri, null, null, null, null);
        }
        if (cursor == null) return "";
        String num = "";
        while (cursor.moveToNext()) {
            num = cursor.getString(cursor.getColumnIndex("data1"));
        }
        cursor.close();
        num = num.replaceAll("-", "");//替换的操作,555-6 -> 5556
        num = num.replaceAll(" ", "");//替换的操作,555-6 -> 5556
        return num;
    }

    /**
     * 相册文件获取(拍照文件外部指定)
     */
    public static File getPictureFile(Intent data) {
        if (data == null) {
            LogUtils.w(LOG_TAG, "getPictureFile: data == null");
            return null;
        }
        Uri uri = getPictureUri(data);
        File file;
        if (uri != null) {
            file = ProviderUtils.getImgFileByUri(uri);
        } else {
            long time = new Date().getTime();
            file = new File(AppInfo.get().getInCacheDir(), time + ".jpeg");
            FileUtils.createFileByDeleteOldFile(file);
            Bitmap picture = data.getParcelableExtra("data");
            BitmapUtils.saveBitmap(picture, file.getAbsolutePath(), Bitmap.CompressFormat.JPEG, true);
        }
        return file;
    }

    /**
     * 获取相册图片: 在onActivityResult中执行 data.getData()
     */
    public static Uri getPictureUri(Intent data) {
        if (data == null || data.getData() == null) {
            LogUtils.w(LOG_TAG, "getPictureUri: data == null");
            return null;
        }
        Uri uri = data.getData();
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE) && (data.getType() != null && data.getType().contains("image/"))) {
            //小米的type不是null 其他的是
            String path = uri.getEncodedPath();
            if (path != null) {
                path = Uri.decode(path);
                ContentResolver cr = AppBase.getInstance().getContentResolver();
                String buff = "(" + MediaStore.Images.ImageColumns.DATA + "=" +
                        "'" + path + "'" + ")";
                Cursor cur = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Images.ImageColumns._ID},
                        buff, null, null);
                int index = 0;
                if (cur != null) {
                    for (cur.moveToFirst(); !cur.isAfterLast(); cur.moveToNext()) {
                        index = cur.getColumnIndex(MediaStore.Images.ImageColumns._ID);
                        index = cur.getInt(index);
                    }
                    cur.close();
                }
                if (index > 0) {
                    Uri uri_temp = Uri.parse("content://media/external/images/media/" + index);
                    if (uri_temp != null) {
                        return uri_temp;
                    }
                }
            }
        }
        return uri;
    }

}
