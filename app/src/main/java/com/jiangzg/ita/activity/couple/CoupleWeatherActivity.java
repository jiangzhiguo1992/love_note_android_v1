package com.jiangzg.ita.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;

public class CoupleWeatherActivity extends BaseActivity<CoupleWeatherActivity> {

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, CoupleWeatherActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_couple_weather;
    }

    @Override
    protected void initView(Bundle state) {
        // TODO
    }

    @Override
    protected void initData(Bundle state) {
        // TODO
    }

}
