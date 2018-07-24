package com.jiangzg.mianmian.activity.topic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.PostKindInfo;

public class PostAddActivity extends BaseActivity<PostAddActivity> {

    private PostKindInfo kindInfo;

    public static void goActivity(Activity from, PostKindInfo kindInfo) {
        if (kindInfo == null) {
            LogUtils.w(PostAddActivity.class, "goActivity", "kindInfo == null");
            return;
        }
        Intent intent = new Intent(from, PostAddActivity.class);
        intent.putExtra("kindInfo", kindInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        kindInfo = intent.getParcelableExtra("kindInfo");
        return R.layout.activity_post_add;
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
