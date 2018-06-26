package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Dream;

public class DreamDetailActivity extends BaseActivity<DreamDetailActivity> {

    private static final int FROM_NONE = 0;
    private static final int FROM_ID = 1;
    private static final int FROM_ALL = 2;

    public static void goActivity(Activity from, Dream dream) {
        Intent intent = new Intent(from, DreamDetailActivity.class);
        intent.putExtra("from", FROM_ALL);
        intent.putExtra("dream", dream);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long did) {
        Intent intent = new Intent(from, DiaryDetailActivity.class);
        intent.putExtra("from", FROM_ID);
        intent.putExtra("did", did);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_dream_detail;
    }

    @Override
    protected void initView(Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }

}
