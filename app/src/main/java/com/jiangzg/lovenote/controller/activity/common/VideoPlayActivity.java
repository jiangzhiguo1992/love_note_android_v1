package com.jiangzg.lovenote.controller.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.OssResHelper;

import java.io.File;

import butterknife.BindView;

public class VideoPlayActivity extends BaseActivity<VideoPlayActivity> {

    @BindView(R.id.vv)
    VideoView vv;

    private String ossKey;

    public static void goActivity(Activity from, String ossKey) {
        if (StringUtils.isEmpty(ossKey)) return;
        Intent intent = new Intent(from, VideoPlayActivity.class);
        intent.putExtra("ossKey", ossKey);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_video_play;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ossKey = intent.getStringExtra("ossKey");

        // video配置
        MediaController controller = new MediaController(mActivity);
        vv.setMediaController(controller);
        controller.setMediaPlayer(vv);
        controller.show();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // 检查是否下载完毕
        if (OssResHelper.isKeyFileExists(ossKey)) {
            File file = OssResHelper.newKeyFile(ossKey);
            if (file != null && !FileUtils.isFileEmpty(file)) {
                long lastModified = file.lastModified();
                long maxOldTime = DateUtils.getCurrentLong() - OssResHelper.FILE_DOWNLOAD_WAIT;
                if (lastModified > 0 && lastModified <= maxOldTime) {
                    // 有文件 已下载完
                    playByPath();
                } else {
                    // 有文件 可能没下载完
                    playByUrl();
                }
            } else {
                // 下载过，但是空文件
                playByUrl();
            }
        } else {
            // 没下载过
            playByUrl();
        }
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    private void playByPath() {
        File file = OssResHelper.newKeyFile(ossKey);
        if (file == null || FileUtils.isFileEmpty(file)) {
            finish();
            return;
        }
        vv.setVideoPath(file.getAbsolutePath());
        vv.start();
    }

    private void playByUrl() {
        String url = OssHelper.getUrl(ossKey);
        if (StringUtils.isEmpty(url)) {
            finish();
            return;
        }
        vv.setVideoURI(Uri.parse(url));
        vv.start();
    }

}
