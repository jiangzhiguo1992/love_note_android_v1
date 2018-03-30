package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;

public class WebActivity extends BaseActivity<WebActivity> {

    public static final int TYPE_USER_PROTOCOL = 1; // 用户协议
    public static final int TYPE_CONTACT_US = 2; // 联系我们
    private int type;

    public static void goActivity(Activity from, int type) {
        Intent intent = new Intent(from, WebActivity.class);
        intent.putExtra("type", type);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_web;
    }

    @Override
    protected void initView(Bundle state) {
        type = getIntent().getIntExtra("type", TYPE_USER_PROTOCOL);
    }

    @Override
    protected void initData(Bundle state) {
        // todo 先获取type，在api获取url和title，再加载
    }

}
