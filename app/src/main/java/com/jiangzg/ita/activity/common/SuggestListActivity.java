package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;

public class SuggestListActivity extends BaseActivity<SuggestListActivity> {

    public static final int ENTRY_MINE = 0;
    public static final int ENTRY_FOLLOW = 1;
    private int entry;

    public static void goActivity(Activity from, int entry) {
        Intent intent = new Intent(from, SuggestListActivity.class);
        intent.putExtra("entry", entry);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_list;
    }

    @Override
    protected void initView(Bundle state) {
        entry = getIntent().getIntExtra("entry", ENTRY_MINE);
    }

    @Override
    protected void initData(Bundle state) {

    }

}
