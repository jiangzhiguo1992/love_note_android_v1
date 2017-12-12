package com.jiangzg.project.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.RelativeLayout;

import com.android.base.component.activity.ActivityTrans;
import com.android.base.component.application.AppInfo;
import com.android.base.component.fragment.FragmentTrans;
import com.android.base.component.fragment.FragmentUtils;
import com.jiangzg.project.R;
import com.jiangzg.project.base.BaseActivity;
import com.jiangzg.project.fragment.BigFragment;
import com.jiangzg.project.fragment.SmallFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class FragActivity extends BaseActivity<FragActivity> {

    @BindView(R.id.rlFragment)
    RelativeLayout rlFragment;

    private BigFragment bigFragment;
    private SmallFragment smallFragment;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, FragActivity.class);
        // intent.putExtra();
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int initObj(Intent intent) {
        return R.layout.activity_frag;
    }

    @Override
    protected void initView(Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {
        bigFragment = BigFragment.newFragment();
        smallFragment = SmallFragment.newFragment();
        FragmentTrans.add(mFragmentManager, smallFragment, R.id.rlFragment);
        FragmentTrans.add(mFragmentManager, bigFragment, R.id.rlFragment);


    }

    @OnClick(R.id.btnTrans)
    public void onViewClicked() {
        FragmentManager m = getSupportFragmentManager();
        if (bigFragment.isVisible()) {
            FragmentTrans.show(m, smallFragment, false);
            //FragmentTrans.hide(m, bigFragment, false);
            //smallFragment = bigFragment.replace();
        } else {
            FragmentTrans.show(m, bigFragment, false);
            //FragmentTrans.hide(m, smallFragment, false);
            //bigFragment = smallFragment.replace();
        }
    }
}
