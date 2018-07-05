package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;

public class VideoListActivity extends BaseActivity<VideoListActivity> {

    private static final int FROM_BROWSE = 0;
    private static final int FROM_SELECT = 1;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), VideoListActivity.class);
        intent.putExtra("from", FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelect(Activity from) {
        Intent intent = new Intent(from, VideoListActivity.class);
        intent.putExtra("from", FROM_SELECT);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_video_list;
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
