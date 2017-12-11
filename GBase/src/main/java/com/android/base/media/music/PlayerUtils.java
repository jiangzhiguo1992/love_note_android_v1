package com.android.base.media.music;

/**
 * Created by gg on 2017/4/3.
 * 多媒体播放
 */
public class PlayerUtils {
//
//    public static boolean isInit = false;
//    public static boolean isPrepare = false;
//    public static boolean isStart = false;
//
//    /**
//     * 在setData之后调用
//     */
//    public static void setPlayerListener(MediaPlayer player,
//                                         final PlayerListener listener) {
//        if (listener == null)
//            return;
//
//        // 准备之后直接开始
//        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mp) {
//                isInit = false;
//                isPrepare = true;
//                isStart = false;
//                listener.start(mp);
//                start(mp);
//            }
//        });
//        // 结束,恢复init状态
//        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                listener.complete(mp);
//                reset(mp);
//                isInit = true;
//                isPrepare = false;
//                isStart = false;
//            }
//        });
//        // 错误,恢复init状态
//        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer mp, int what, int extra) {
//                listener.error(mp, what, extra);
//                reset(mp);
//                isInit = true;
//                isPrepare = false;
//                isStart = false;
//                return true; // return false;
//            }
//        });
//        // 定位完成,没设置直接开始
//        player.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
//            @Override
//            public void onSeekComplete(MediaPlayer mp) {
//                listener.seekComplete(mp);
//            }
//        });
//    }
//
//    /**
//     * 监听器
//     */
//    public interface PlayerListener {
//        /**
//         * 之后会执行start()
//         */
//        void start(MediaPlayer mp);
//
//        /**
//         * 播放完成
//         */
//        void complete(MediaPlayer mp);
//
//        /**
//         * 发生错误
//         */
//        void error(MediaPlayer mp, int what, int extra);
//
//        /**
//         * 定位完成
//         */
//        void seekComplete(MediaPlayer mp);
//    }
//
//    /**
//     * 播放和暂停，万能
//     */
//    public static void toggle(MediaPlayer player) {
//        if (isStart) { // 正在播放
//            pause(player); // 暂停
//        } else { // 不在播放
//            if (isPrepare) { // 准备好
//                start(player); // 开始
//            } else { // 没准备好
//                prepare(player); // 那就去准备，并播放
//            }
//        }
//    }
//
//    /**
//     * Idle状态
//     */
//    public static MediaPlayer getMediaPlayer() {
//        return new MediaPlayer();
//    }
//
//    /**
//     * 直接返回init状态的MediaPlayer
//     */
//    public static MediaPlayer getMediaPlayer(Context context, int resID) {
//        MediaPlayer player = MediaPlayer.create(context, resID);
//        isInit = true;
//        isPrepare = false;
//        isStart = false;
//        return player;
//    }
//
//    /**
//     * init状态
//     */
//    public static void setData(MediaPlayer player, String filePath) {
//        try {
//            player.setDataSource(filePath);
//            isInit = true;
//            isPrepare = false;
//            isStart = false;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * init状态
//     */
//    public static void setData(MediaPlayer player, Context context, Uri uri) {
//        try {
//            player.setDataSource(context, uri);
//            isInit = true;
//            isPrepare = false;
//            isStart = false;
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 准备状态,只有在接口中知道什么时候准备好
//     */
//    public static void prepare(MediaPlayer player) {
//        if (isInit) {
//            player.prepareAsync();
//        }
//    }
//
//    /**
//     * 开始状态
//     */
//    public static void start(MediaPlayer player) {
//        if (isPrepare) {
//            player.start();
//            isInit = false;
//            isPrepare = false;
//            isStart = true;
//        } else {
//            Log.e("PlayerUtils", "start--->isPrepare = false");
//        }
//    }
//
//    /**
//     * 暂停状态，可直接start恢复播放
//     */
//    public static void pause(MediaPlayer player) {
//        if (isStart) {
//            player.pause();
//            isInit = false;
//            isStart = false;
//            isPrepare = true;
//        } else {
//            Log.e("PlayerUtils", "pause--->isStart = false");
//        }
//    }
//
//    /**
//     * 停止状态,需prepare再start
//     */
//    public static void stop(MediaPlayer player) {
//        if (isStart) {
//            player.stop();
//            isInit = true;
//            isStart = false;
//            isPrepare = false;
//        } else {
//            Log.e("PlayerUtils", "stop--->isStart = false");
//        }
//    }
//
//    /**
//     * 定位, 在哪个状态，seek之后还是哪个状态
//     */
//    public static void seekTo(MediaPlayer player, int milliseconds) {
//        if (isInit) {
//            Log.e("PlayerUtils", "seekTo--->isInit = true");
//            return;
//        }
//        player.seekTo(milliseconds);
//    }
//
//    /**
//     * 返回至init状态 , onDestory中先调用reset后调用relese
//     */
//    public static void reset(MediaPlayer player) {
//        player.reset();
//        isInit = true;
//        isPrepare = false;
//        isStart = false;
//    }
//
//    /**
//     * 释放，无状态 , onDestory中先调用reset后调用relese
//     */
//    public static void relese(MediaPlayer player) {
//        player.release();
//        isInit = false;
//        isPrepare = false;
//        isStart = false;
//    }
//
//    /**
//     * 时长 , setMax
//     */
//    public static void getDuration(MediaPlayer player) {
//        player.getDuration();
//    }
//
//    /**
//     * 当前播放位置 , setProgress
//     */
//    public static int getPosition(MediaPlayer player) {
//        return player.getCurrentPosition();
//    }
//
//    /**
//     * 是否在播放 , while(isPlaying)
//     */
//    public static boolean isPlaying(MediaPlayer player) {
//        return player.isPlaying();
//    }
//
//    /**
//     * 设置循环 ,单曲循环
//     */
//    public static void setLoop(MediaPlayer player, boolean loop) {
//        player.setLooping(loop);
//    }
//
//    /**
//     * 是否循环
//     */
//    public static boolean isLoop(MediaPlayer player) {
//        return player.isLooping();
//    }
//
//    /**
//     * 调音量
//     */
//    public static void setVolume(MediaPlayer player, float volume) {
//        player.setVolume(volume, volume);
//    }

}
