package com.jiangzg.base.media;

import android.media.MediaRecorder;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.io.IOException;

/**
 * Created by JZG on 2018/7/5.
 * AudioUtils 音频工具类
 */
public class AudioUtils {

    private static MediaRecorder mRecorder;

    /**
     * 开始录音
     */
    public static MediaRecorder startRecord(String outFilePath) {
        if (StringUtils.isEmpty(outFilePath)) {
            LogUtils.w(AudioUtils.class, "startRecord", "outFilePath == null");
            return null;
        }
        MediaRecorder mRecorder = new MediaRecorder();
        mRecorder.reset();
        // 文件输出路径
        mRecorder.setOutputFile(outFilePath);
        // 音频为麦克风
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 音频格式  THREE_GPP????
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        // 音频编码
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
            mRecorder.start();
        } catch (IOException e) {
            LogUtils.e(AudioUtils.class, "startRecord", e);
        }
        return mRecorder;
    }

    /**
     * 停止录音
     */
    public static void stopRecord(MediaRecorder mRecorder) {
        if (mRecorder == null) {
            LogUtils.w(AudioUtils.class, "stopRecord", "mRecorder == null");
            return;
        }
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

}
