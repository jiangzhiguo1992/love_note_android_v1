package com.jiangzg.lovenote.controller.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import butterknife.OnClick;

public class VideoPlayActivity extends BaseActivity<VideoPlayActivity> {

    @BindView(R.id.vPlayer)
    PlayerView vPlayer;
    @BindView(R.id.llTopInfo)
    LinearLayout llTopInfo;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.tvTitle)
    TextView tvTitle;

    private String ossKey;

    private ExoPlayer player;
    private SimpleCache simpleCache;
    private ExtractorMediaSource mediaSource;

    public static void goActivity(Activity from, String title, String ossKey) {
        if (StringUtils.isEmpty(ossKey)) {
            return;
        }
        Intent intent = new Intent(from, VideoPlayActivity.class);
        intent.putExtra("title", title);
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
        player = PlayerHelper.getVideoPlayer(mActivity);
        simpleCache = PlayerHelper.getSimpleCache();
        vPlayer.setPlayer(player);
        // view
        tvTitle.setText(intent.getStringExtra("title"));
        // listener
        vPlayer.setControllerVisibilityListener(visibility -> {
            llTopInfo.setVisibility(visibility);
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        PlayerHelper.release(null, null, mediaSource);
        mediaSource = PlayerHelper.getDataSource(mActivity, simpleCache, ossKey);
        PlayerHelper.play(player, mediaSource);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            PlayerHelper.release(player, simpleCache, mediaSource);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        PlayerHelper.release(player, simpleCache, mediaSource);
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.ivBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivBack: // 返回
                mActivity.finish();
                break;
        }
    }

}
