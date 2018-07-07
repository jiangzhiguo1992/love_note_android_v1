package com.jiangzg.base.media;

import android.media.MediaMetadataRetriever;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.util.HashMap;

/**
 * Created by JZG on 2018/7/7.
 * VideoUtils
 */
public class VideoUtils {

    // getVideoDuration cursor有时候获取不到 就用这个
    public static String getVideoDuration(String vPath) {
        if (StringUtils.isEmpty(vPath)) return "0";
        String duration = null;
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        try {
            mmr.setDataSource(vPath);
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {
            LogUtils.e(VideoUtils.class, "getVideoDuration", e);
        } finally {
            mmr.release();
        }
        return duration;
    }

}
