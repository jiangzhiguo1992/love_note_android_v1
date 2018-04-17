package com.jiangzg.ita.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;

public class CoupleMapActivity extends BaseActivity<CoupleMapActivity> {

    // todo 先检查是否初始化
    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, CoupleMapActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_couple_map;
    }

    @Override
    protected void initView(Bundle state) {
        // todo
    }

    @Override
    protected void initData(Bundle state) {
        // todo
    }

}
