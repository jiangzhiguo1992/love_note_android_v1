package com.jiangzg.lovenote.controller.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.media.PlayerHelper;
import com.jiangzg.lovenote.model.entity.Video;

import butterknife.BindView;

public class VideoPlayActivity extends BaseActivity<VideoPlayActivity> {

    @BindView(R.id.vPlayer)
    PlayerView vPlayer;

    private Video video;

    private ExoPlayer player;
    private ExtractorMediaSource mediaSource;
    private SimpleCache simpleCache;

    public static void goActivity(Activity from, Video video) {
        if (video == null || StringUtils.isEmpty(video.getContentVideo())) {
            return;
        }
        Intent intent = new Intent(from, VideoPlayActivity.class);
        intent.putExtra("video", video);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_video_play;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        video = intent.getParcelableExtra("video");
        // init
        player = PlayerHelper.getPlayer(mActivity);
        vPlayer.setPlayer(player);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        initSource();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releasePlayer();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    private void initSource() {
        if (video == null) return;
        // 缓存
        simpleCache = PlayerHelper.getSimpleCache();

        // 资源
        mediaSource = PlayerHelper.getDataSource(mActivity, simpleCache, video.getContentVideo());

        // 播放
        PlayerHelper.play(player, mediaSource);
    }

    private void releasePlayer() {
        PlayerHelper.release(player, simpleCache, mediaSource);
    }

}
