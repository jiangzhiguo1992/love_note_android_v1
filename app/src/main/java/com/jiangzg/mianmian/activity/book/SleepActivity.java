package com.jiangzg.mianmian.activity.book;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.FrescoView;

import butterknife.BindView;
import butterknife.OnClick;

public class SleepActivity extends BaseActivity<SleepActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.cvDate)
    CalendarView cvDate;
    @BindView(R.id.ivAvatarLeft)
    FrescoView ivAvatarLeft;
    @BindView(R.id.tvStateLeft)
    TextView tvStateLeft;
    @BindView(R.id.tvSleep)
    TextView tvSleep;
    @BindView(R.id.cvSleep)
    CardView cvSleep;
    @BindView(R.id.ivAvatarRight)
    FrescoView ivAvatarRight;
    @BindView(R.id.tvStateRight)
    TextView tvStateRight;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), SleepActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_sleep;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.sleep), true);

    }

    @Override
    protected void initData(Intent intent, Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

    @OnClick(R.id.tvSleep)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvSleep: // 睡眠
                // TODO
                break;
        }
    }

}
