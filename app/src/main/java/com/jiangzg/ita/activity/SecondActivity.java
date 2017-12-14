package com.jiangzg.ita.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;

import butterknife.BindView;

public class SecondActivity extends BaseActivity<SecondActivity> {

    @BindView(R.id.ivLauncher)
    ImageView ivLauncher;

    public static void goActivity(HomeActivity from) {
        Intent intent = new Intent(from, SecondActivity.class);
        View ivMain = from.ivMain;
        Pair<View, String> pair = Pair.create(ivMain, "launcher");
        ActivityTrans.start(from, intent, pair);
    }

    @Override
    protected int initObj(Intent intent) {
        return R.layout.activity_second;
    }

    @Override
    protected void initView(Bundle state) {
        ActivityTrans.setShareElement(ivLauncher, "launcher");
    }

    @Override
    protected void initData(Bundle state) {

    }

}
