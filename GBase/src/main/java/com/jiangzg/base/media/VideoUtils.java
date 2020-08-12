package com.jiangzg.base.media;

import android.media.MediaMetadataRetriever;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by JZG on 2018/7/7.
 * VideoUtils
 */
public class VideoUtils {

    // getVideoDuration cursor有时候获取不到 就用这个
    public static String getVideoDuration(String vPath) {
        if (StringUtils.isEmpty(vPath)) return "0";
        String duration = "0";
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(vPath);
            mmr.setDataSource(inputStream.getFD());
            duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        } catch (Exception e) {
            LogUtils.e(VideoUtils.class, "getVideoDuration", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mmr.release();
        }
        return duration;
    }

}
