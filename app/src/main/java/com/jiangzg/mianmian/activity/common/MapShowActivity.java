package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;

public class MapShowActivity extends BaseActivity<MapShowActivity> {

    // 当前我的位置
    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, MapShowActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    // 传入的位置
    public static void goActivity(Activity from, double lon, double lat) {
        Intent intent = new Intent(from, MapShowActivity.class);
        // intent.putExtra(); // TODO
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_map_show;
    }

    @Override
    protected void initView(Bundle state) {
        // TODO 只是查看位置的，顺带搜索？
    }

    @Override
    protected void initData(Bundle state) {

    }

}
