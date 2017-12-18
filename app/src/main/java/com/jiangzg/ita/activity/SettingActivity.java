package com.jiangzg.ita.activity;

import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;

public class SettingActivity extends BaseActivity<SettingActivity> {

    @Override
    protected int initObj(Intent intent) {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView(Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }

    //private void checkUpdate() {
    //    new RetroManager(API.BASE_URL)
    //            .head(HttpUtils.getHead())
    //}

}
