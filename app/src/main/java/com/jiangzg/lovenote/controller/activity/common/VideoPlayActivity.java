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
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.media.PlayerHelper;

import butterknife.BindView;

public class VideoPlayActivity extends BaseActivity<VideoPlayActivity> {

    @BindView(R.id.vPlayer)
    PlayerView vPlayer;

    private String ossKey;

    private ExoPlayer player;
    private ExtractorMediaSource mediaSource;
    private SimpleCache simpleCache;

    public static void goActivity(Activity from, String ossKey) {
        if (StringUtils.isEmpty(ossKey)) {
            return;
        }
        Intent intent = new Intent(from, VideoPlayActivity.class);
        intent.putExtra("ossKey", ossKey);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        ScreenUtils.requestNoOrientation(mActivity); // 可以横屏
        BarUtils.setAllBarHide(mActivity); // 隐藏bar
        return R.layout.activity_video_play;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ossKey = intent.getStringExtra("ossKey");
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
        // 缓存
        simpleCache = PlayerHelper.getSimpleCache();
        // 资源
        mediaSource = PlayerHelper.getDataSource(mActivity, simpleCache, ossKey);
        // 播放
        PlayerHelper.play(player, mediaSource);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PlayerHelper.release(player, simpleCache, mediaSource);
    }

    @Override
    protected void onFinish(Bundle state) {
    }

}
