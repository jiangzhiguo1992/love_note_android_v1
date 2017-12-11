package com.jiangzg.project.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.android.base.component.activity.ActivityTrans;
import com.android.base.component.application.AppInfo;
import com.android.base.component.fragment.FragmentTrans;
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
        FragmentTrans.replace(mFragmentManager, smallFragment, R.id.rlFragment);

        AppInfo appInfo = AppInfo.get();
        String packageName = appInfo.getPackageName();
        String cacheDir = appInfo.getCacheDir();
        String filesDir = appInfo.getFilesDir("");
        String resDir = appInfo.getResDir();
        String logDir = appInfo.getLogDir();
    }

    @OnClick(R.id.btnTrans)
    public void onViewClicked() {
        if (bigFragment.isVisible()) {
            smallFragment = bigFragment.replace();
        } else {
            bigFragment = smallFragment.replace();
        }
    }
}
