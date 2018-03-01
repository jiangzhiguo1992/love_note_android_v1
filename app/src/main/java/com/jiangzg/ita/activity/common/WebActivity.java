package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;

public class WebActivity extends BaseActivity<WebActivity> {

    public static final int TYPE_USER_PROTOCOL = 1; // 用户协议

    public static void goActivity(Activity from, int type) {
        Intent intent = new Intent(from, WebActivity.class);
        intent.putExtra("type", type);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_web;
    }

    @Override
    protected void initView(Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {
        // todo 先获取type，在api获取url和title，再加载
    }

}
