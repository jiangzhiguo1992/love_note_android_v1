package com.jiangzg.mianmian.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;

public class CoupleMensesActivity extends BaseActivity<CoupleMensesActivity> {

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, CoupleMensesActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_couple_menses;
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
