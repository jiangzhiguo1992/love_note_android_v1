package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GWebView;

import butterknife.BindView;

public class WebActivity extends BaseActivity<WebActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.wv)
    GWebView wv;

    public static void goActivity(Activity from, String url) {
        Intent intent = new Intent(from, WebActivity.class);
        intent.putExtra("url", url);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_web;
    }

    @Override
    protected void initView(Bundle state) {
        // TODO tb名称
        ViewHelper.initTopBar(mActivity, tb, "", true);
    }

    @Override
    protected void initData(Bundle state) {
        String url = getIntent().getStringExtra("url");
        wv.load(url);
    }

}
