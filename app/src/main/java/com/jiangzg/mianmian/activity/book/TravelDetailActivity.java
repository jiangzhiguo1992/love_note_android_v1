package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Travel;

public class TravelDetailActivity extends BaseActivity<TravelDetailActivity> {

    private static final int FROM_NONE = 0;
    private static final int FROM_ID = 1;
    private static final int FROM_ALL = 2;

    public static void goActivity(Activity from, Travel travel) {
        Intent intent = new Intent(from, TravelDetailActivity.class);
        intent.putExtra("from", FROM_ALL);
        intent.putExtra("travel", travel);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long tid) {
        Intent intent = new Intent(from, TravelDetailActivity.class);
        intent.putExtra("from", FROM_ID);
        intent.putExtra("tid", tid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_travel_detail;
    }

    @Override
    protected void initView(Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }

}
