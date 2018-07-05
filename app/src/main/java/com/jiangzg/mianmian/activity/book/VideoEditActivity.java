package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Video;

public class VideoEditActivity extends BaseActivity<VideoEditActivity> {

    private static final int TYPE_ADD = 0;
    private static final int TYPE_UPDATE = 1;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, VideoEditActivity.class);
        intent.putExtra("type", TYPE_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Video video) {
        if (video == null) {
            goActivity(from);
            return;
        } else if (!video.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_video));
            return;
        }
        Intent intent = new Intent(from, VideoEditActivity.class);
        intent.putExtra("type", TYPE_UPDATE);
        intent.putExtra("video", video);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_video_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {

    }

    @Override
    protected void initData(Intent intent, Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

}
