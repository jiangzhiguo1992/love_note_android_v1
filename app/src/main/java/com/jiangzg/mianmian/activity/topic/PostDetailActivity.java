package com.jiangzg.mianmian.activity.topic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Post;
import com.jiangzg.mianmian.helper.ConsHelper;

public class PostDetailActivity extends BaseActivity<PostDetailActivity> {

    public static void goActivity(Activity from, Post post) {
        Intent intent = new Intent(from, PostDetailActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_OBJ);
        intent.putExtra("post", post);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long pid) {
        Intent intent = new Intent(from, PostDetailActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
        intent.putExtra("pid", pid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_post_detail;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        // TODO post详情页图片竖排
        // TODO 举报在详情里才会有、官方的post/comment不能举报和屏蔽
        // TODO comment有楼主的标识 有几楼的标识 有@TA的选项 置顶
    }

    @Override
    protected void initData(Intent intent, Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

}
