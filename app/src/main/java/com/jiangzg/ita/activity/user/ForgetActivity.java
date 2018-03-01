package com.jiangzg.ita.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;

public class ForgetActivity extends BaseActivity<ForgetActivity> {

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, ForgetActivity.class);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_forget;
    }

    @Override
    protected void initView(Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
    }
}
