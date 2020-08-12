package com.jiangzg.lovenote.helper.media;

import android.content.Context;
import android.net.Uri;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DataSink;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSinkFactory;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.lovenote.controller.activity.common.VideoPlayActivity;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.ResHelper;

/**
 * Created by JZG on 2019/3/29.
 * 音视频播放
 */
public class PlayerHelper {

    public static ExoPlayer getAudioPlayer(Context context) {
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context, new DefaultTrackSelector());
        player.setRepeatMode(Player.REPEAT_MODE_ALL);
        player.setPlayWhenReady(true);
        return player;
    }

    public static ExoPlayer getVideoPlayer(Context context) {
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(context,
                new DefaultRenderersFactory(context), // 渲染处理器
                new DefaultTrackSelector(), // 轨道提取器
                new DefaultLoadControl());// 控制面板
        player.setPlayWhenReady(true);
        return player;
    }

    public static SimpleCache getSimpleCache() {
        return new SimpleCache(ResHelper.createExoCacheDir(), new LeastRecentlyUsedCacheEvictor(ResHelper.getMaxCacheSize()));
    }

    public static ExtractorMediaSource getDataSource(Context context, Cache cache, String ossKey) {
        String url = OssHelper.getUrl(ossKey);
        Uri uri = Uri.parse(url);

        DefaultDataSourceFactory sourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context, AppInfo.get().getName()));

        CacheKeyDataSourceFactory cacheDataSourceFactory = new CacheKeyDataSourceFactory(cache, sourceFactory, 0, ResHelper.getMaxCacheSize());
        //DefaultHttpDataSourceFactory defaultHttpDataSourceFactory = new DefaultHttpDataSourceFactory(AppInfo.get().getName());

        return new ExtractorMediaSource.Factory(cacheDataSourceFactory).createMediaSource(uri);
    }

    public static void play(ExoPlayer player, ExtractorMediaSource mediaSource) {
        // 播放
        if (player == null) {
            return;
        }
        player.prepare(mediaSource, true, true);
        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerError(ExoPlaybackException error) {
                LogUtils.e(VideoPlayActivity.class, "EventListener", error);
            }
        });
    }

    public static void release(ExoPlayer player, Cache cache, ExtractorMediaSource mediaSource) {
        if (player != null) {
            player.release();
            player = null;
        }
        if (mediaSource != null) {
            mediaSource.releaseSourceInternal();
            mediaSource = null;
        }
        if (cache != null) {
            cache.release();
            cache = null;
        }
    }

    // 带有缓存key的
    static class CacheKeyDataSourceFactory implements DataSource.Factory {

        private final Cache cache;
        private final DataSource.Factory upstreamFactory;
        private final DataSource.Factory cacheReadDataSourceFactory;
        private final DataSink.Factory cacheWriteDataSinkFactory;
        private final int flags;
        private final CacheDataSource.EventListener eventListener;

        /**
         * @see CacheDataSource#CacheDataSource(Cache, DataSource)
         */
        public CacheKeyDataSourceFactory(Cache cache, DataSource.Factory upstreamFactory) {
            this(cache, upstreamFactory, 0);
        }

        /**
         * @see CacheDataSource#CacheDataSource(Cache, DataSource, int)
         */
        public CacheKeyDataSourceFactory(Cache cache, DataSource.Factory upstreamFactory,
                                         @CacheDataSource.Flags int flags) {
            this(cache, upstreamFactory, flags, CacheDataSource.DEFAULT_MAX_CACHE_FILE_SIZE);
        }

        /**
         * @see CacheDataSource#CacheDataSource(Cache, DataSource, int, long)
         */
        public CacheKeyDataSourceFactory(Cache cache, DataSource.Factory upstreamFactory,
                                         @CacheDataSource.Flags int flags, long maxCacheFileSize) {
            this(cache, upstreamFactory, new FileDataSourceFactory(),
                    new CacheDataSinkFactory(cache, maxCacheFileSize), flags, null);
        }

        /**
         * @see CacheDataSource#CacheDataSource(Cache, DataSource, DataSource, DataSink, int,
         * CacheDataSource.EventListener)
         */
        public CacheKeyDataSourceFactory(Cache cache, DataSource.Factory upstreamFactory,
                                         DataSource.Factory cacheReadDataSourceFactory, DataSink.Factory cacheWriteDataSinkFactory,
                                         @CacheDataSource.Flags int flags, CacheDataSource.EventListener eventListener) {
            this.cache = cache;
            this.upstreamFactory = upstreamFactory;
            this.cacheReadDataSourceFactory = cacheReadDataSourceFactory;
            this.cacheWriteDataSinkFactory = cacheWriteDataSinkFactory;
            this.flags = flags;
            this.eventListener = eventListener;
        }

        @Override
        public CacheDataSource createDataSource() {
            return new CacheDataSource(cache, upstreamFactory.createDataSource(),
                    cacheReadDataSourceFactory.createDataSource(),
                    cacheWriteDataSinkFactory != null ? cacheWriteDataSinkFactory.createDataSink() : null,
                    flags, eventListener, dataSpec -> {
                Uri uri = dataSpec.uri;
                if (uri == null) return null;
                // uri转key
                return OssHelper.getOssKey(uri.toString());
            });
        }
    }
}
