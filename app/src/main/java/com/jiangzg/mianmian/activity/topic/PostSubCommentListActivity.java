package com.jiangzg.mianmian.activity.topic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.PostComment;

public class PostSubCommentListActivity extends BaseActivity<PostSubCommentListActivity> {

    public static void goActivity(Activity from, PostComment postComment) {
        if (postComment == null) return;
        Intent intent = new Intent(from, PostSubCommentListActivity.class);
        intent.putExtra("postComment", postComment);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_post_sub_comment_list;
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
