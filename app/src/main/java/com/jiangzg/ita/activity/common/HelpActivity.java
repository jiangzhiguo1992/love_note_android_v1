package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;

public class HelpActivity extends BaseActivity<HelpActivity> {

    public static final int TYPE_USER_INFO_SET = 1;

    public static void goActivity(Activity from, int type) {
        Intent intent = new Intent(from, HelpActivity.class);
        intent.putExtra("type", type);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_help;
    }

    @Override
    protected void initView(Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }

}
