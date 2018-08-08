package com.jiangzg.lovenote.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;

public class VipActivity extends BaseActivity<VipActivity> {

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), VipActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, VipActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_vip;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        // TODO
    }

    @Override
    protected void initData(Intent intent, Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

}
