package com.android.base.media.data;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.android.base.component.application.AppContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by gg on 2017/4/11.
 * 多媒体管理类
 */
public class MediaUtils {

    /**
     * 获取设备里的所有图片信息
     */
    public static List<Map<String, String>> getImage() {
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

    /**
     * 获取设备里的所有音频信息
     */
    public static List<Map<String, String>> getAudio() {
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

    /**
     * 获取设备里的所有视频信息
     */
    public static List<Map<String, String>> getVideo() {
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
        Cursor cursor = AppContext.get().getContentResolver()
                .query(uri, projection, selection, selectionArgs, orderBy);
        if (null == cursor) return list;
        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            for (int i = 0; i < projection.length; i++) {
                map.put(projection[i], cursor.getString(i));
            }
            list.add(map);
        }
        cursor.close();
        return list;
    }

}
