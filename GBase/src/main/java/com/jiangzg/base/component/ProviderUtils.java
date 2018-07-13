package com.jiangzg.base.component;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.ArrayMap;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by JZG on 2018/5/13.
 * ProviderUtils
 */
public class ProviderUtils {

    /**
     * uri转file
     */
    public static File getFileByUri(Uri uri) {
        if (uri == null) {
            LogUtils.w(ProviderUtils.class, "getFileByUri", "uri == null");
            return null;
        }
        String[] project = new String[]{MediaStore.Images.ImageColumns.DATA}; // 字段名
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            data = uri.getPath();
        } else if (DocumentsContract.isDocumentUri(AppBase.getInstance(), uri)) { // KITKAT
            String docId = DocumentsContract.getDocumentId(uri);
            String[] split = docId.split(":");
            String type = split[0];
            if ("com.android.externalstorage.documents".equals(uri.getAuthority())) {
                // ExternalStorageProvider
                if ("primary".equalsIgnoreCase(type)) {
                    data = Environment.getExternalStorageDirectory() + "/" + split[1];
                } else {
                    LogUtils.w(ProviderUtils.class, "getFileByUri", "primary != externalstorage");
                    return null;
                }
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                // DownloadsProvider
                Long aLong = 0L;
                try {
                    aLong = Long.valueOf(docId);
                } catch (Exception e) {
                    LogUtils.e(ProviderUtils.class, "getFileByUri", e);
                    return null;
                }
                if (aLong != 0) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), aLong);
                    data = getProviderColumnTop(contentUri, project, null, null, null);
                } else {
                    return null;
                }
            } else if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                // MediaProvider
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                data = getProviderColumnTop(contentUri, project, selection, selectionArgs, null);
            }
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) { // File
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) { // MediaStore (and general)
            if ("com.google.android.apps.photos.content".equals(uri.getAuthority())) {
                data = uri.getLastPathSegment();
            } else {
                data = getProviderColumnTop(uri, project, null, null, null);
            }
        }
        if (data != null) return new File(data);
        return null;
    }

    private static String getProviderColumnTop(Uri uri, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        Cursor cursor = AppBase.getInstance().getContentResolver()
                .query(uri, projection, selection, selectionArgs, orderBy);

        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(projection[0]);
            if (index > -1) return cursor.getString(index);
            cursor.close();
        }
        return null;
    }

    /**
     * file转uri
     */
    public static Uri getUriByFile(String authorities, File file) {
        if (file == null) {
            LogUtils.w(ProviderUtils.class, "getUriByFile", "file == null");
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return FileProvider.getUriForFile(AppBase.getInstance(), authorities, file);
        } else {
            return Uri.fromFile(file);
        }
    }

    /**
     * 获取设备里的所有图片信息
     */
    public static List<Map<String, String>> getImageInfoList() {
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE};
        String orderBy = MediaStore.Images.Media.DISPLAY_NAME;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        return getProviderColumn(uri, projection, null, null, orderBy);
    }

    public static Map<String, String> getImageInfo(Uri uri) {
        if (uri == null) {
            LogUtils.w(ProviderUtils.class, "getImageInfo", "uri == null");
            return new HashMap<>();
        }
        String[] projection = {MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE};
        return getProviderColumn(uri, projection);
    }

    /**
     * 获取设备里的所有音频信息
     */
    public static List<Map<String, String>> getAudioInfoList() {
        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE};
        String orderBy = MediaStore.Audio.Media.DISPLAY_NAME;
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        return getProviderColumn(uri, projection, null, null, orderBy);
    }

    public static Map<String, String> getAudioInfo(Uri uri) {
        if (uri == null) {
            LogUtils.w(ProviderUtils.class, "getAudioInfo", "uri == null");
            return new HashMap<>();
        }
        String[] projection = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.MIME_TYPE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.SIZE};
        return getProviderColumn(uri, projection);
    }

    /**
     * 获取设备里的所有视频信息
     */
    public static List<Map<String, String>> getVideoInfoList() {
        String[] projection = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE};
        String orderBy = MediaStore.Video.Media.DISPLAY_NAME;
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        return getProviderColumn(uri, projection, null, null, orderBy);
    }

    public static Map<String, String> getVideoInfo(Uri uri) {
        if (uri == null) {
            LogUtils.w(ProviderUtils.class, "getVideoInfo", "uri == null");
            return new HashMap<>();
        }
        String[] projection = {MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE};
        return getProviderColumn(uri, projection);
    }

    /**
     * @param uri        查询的uri
     * @param projection map里的key
     * @param orderBy    排序
     * @return 查询到的数据
     */
    public static List<Map<String, String>> getProviderColumn(Uri uri, String[] projection,
                                                              String selection,
                                                              String[] selectionArgs,
                                                              String orderBy) {
        List<Map<String, String>> list = new ArrayList<>();
        Cursor cursor = AppBase.getInstance().getContentResolver()
                .query(uri, projection, selection, selectionArgs, orderBy);
        if (null == cursor) return list;
        while (cursor.moveToNext()) {
            Map<String, String> map = new ArrayMap<>();
            for (int i = 0; i < projection.length; i++) {
                String key = projection[i];
                String value = cursor.getString(i);
                LogUtils.d(ProviderUtils.class, "getProviderColumn", "key = " + key + " | value = " + value);
                map.put(key, value);
            }
            list.add(map);
        }
        cursor.close();
        return list;
    }

    public static Map<String, String> getProviderColumn(Uri uri, String[] projection) {
        Map<String, String> map = new HashMap<>();
        Cursor cursor = AppBase.getInstance().getContentResolver()
                .query(uri, projection, null, null, null);
        if (null == cursor) return map;
        if (cursor.moveToNext()) {
            for (int i = 0; i < projection.length; i++) {
                String key = projection[i];
                String value = cursor.getString(i);
                LogUtils.d(ProviderUtils.class, "getProviderColumn", "key = " + key + " | value = " + value);
                map.put(key, value);
            }
        }
        cursor.close();
        return map;
    }

}
