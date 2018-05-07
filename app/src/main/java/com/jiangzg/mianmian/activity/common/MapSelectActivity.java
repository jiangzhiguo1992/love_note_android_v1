package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;

public class MapSelectActivity extends BaseActivity<MapSelectActivity> {

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, MapSelectActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, String address, double longitude, double latitude) {
        Intent intent = new Intent(from, MapSelectActivity.class);
        intent.putExtra("address", address);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_map_select;
    }

    @Override
    protected void initView(Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }

}
