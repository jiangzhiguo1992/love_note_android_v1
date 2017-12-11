package com.android.base.media.music;

import android.media.MediaRecorder;

import java.io.IOException;

/**
 * Created by gg on 2017/4/9.
 * 录音管理类
 */
public class RecordUtils {

    /**
     * 开始录音
     */
    public static MediaRecorder startRecord(String outFilePath) {
        MediaRecorder recorder = new MediaRecorder();
        // 文件输出路径
        recorder.setOutputFile(outFilePath);
        // 音频为麦克风
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 音频格式  THREE_GPP????
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        // 音频编码
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recorder;
    }

    /**
     * 停止录音
     */
    public static void stopRecord(MediaRecorder recorder) {
        if (recorder == null)
            return;
        recorder.stop();
        recorder.release();
        recorder = null;
    }
}
