package com.jiangzg.base.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.io.IOException;

/**
 * author cipherGG
 * Created by Administrator on 2016/5/25.
 * describe 播放类
 */
public class PlayerUtils {

    /**
     * Idle状态
     */
    public static MediaPlayer getMediaPlayer() {
        return new MediaPlayer();
    }

    /**
     * 直接返回init状态的MediaPlayer
     */
    public static MediaPlayer getMediaPlayer(Context context, int resID) {
        if (context == null || resID == 0) {
            LogUtils.w(PlayerUtils.class, "getMediaPlayer", "context == null || resID == 0");
            return null;
        }
        return MediaPlayer.create(context, resID);
    }

    /**
     * init状态
     */
    public static void setData(MediaPlayer player, String path) {
        if (player == null || StringUtils.isEmpty(path)) {
            LogUtils.w(PlayerUtils.class, "setData", "player == null || path == null");
            return;
        }
        // 先清空资源
        player.reset();
        try {
            // 再设置新的资源
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(path);
        } catch (IOException e) {
            LogUtils.e(PlayerUtils.class, "setData", e);
        }
    }

    /**
     * 准备状态,只有在接口中知道什么时候准备好
     */
    public static void prepare(final MediaPlayer player,
                               MediaPlayer.OnCompletionListener completionListener,
                               MediaPlayer.OnSeekCompleteListener seekCompleteListener,
                               MediaPlayer.OnErrorListener errorListener) {
        if (player == null) {
            LogUtils.w(PlayerUtils.class, "prepare", "player == null");
            return;
        }
        // 准备之后直接开始
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                player.start();
            }
        });
        // 结束,恢复init状态
        player.setOnCompletionListener(completionListener);
        // 定位完成,没设置直接开始
        player.setOnSeekCompleteListener(seekCompleteListener);
        // 错误,恢复init状态
        player.setOnErrorListener(errorListener);
        // 开始异步准备
        try {
            player.prepareAsync();
        } catch (IllegalStateException e) {
            LogUtils.e(PlayerUtils.class, "prepare", e);
        }
    }

    /**
     * 播放/暂停
     */
    public static void toggle(MediaPlayer player,
                              MediaPlayer.OnCompletionListener completionListener,
                              MediaPlayer.OnSeekCompleteListener seekCompleteListener,
                              MediaPlayer.OnErrorListener errorListener) {
        if (player == null) {
            LogUtils.w(PlayerUtils.class, "toggle", "player == null");
            return;
        }
        if (player.isPlaying()) {
            stop(player);
        } else {
            play(player, completionListener, seekCompleteListener, errorListener);
        }
    }

    public static void play(MediaPlayer player,
                            MediaPlayer.OnCompletionListener completionListener,
                            MediaPlayer.OnSeekCompleteListener seekCompleteListener,
                            MediaPlayer.OnErrorListener errorListener) {
        if (player == null) {
            LogUtils.w(PlayerUtils.class, "play", "player == null");
            return;
        }
        prepare(player, completionListener, seekCompleteListener, errorListener);
    }

    /**
     * 停止状态,需prepare再start
     */
    public static void stop(MediaPlayer player) {
        if (player == null) {
            LogUtils.w(PlayerUtils.class, "stop", "player == null");
            return;
        }
        if (player.isPlaying()) {
            player.stop();
        }
    }

    /**
     * 停止状态,需prepare再start
     */
    public static void destroy(MediaPlayer player) {
        if (player == null) {
            LogUtils.w(PlayerUtils.class, "destroy", "player == null");
            return;
        }
        player.stop();
        player.release();
        player = null;
    }

}
