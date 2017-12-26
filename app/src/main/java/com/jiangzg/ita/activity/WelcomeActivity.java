package com.jiangzg.ita.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.utils.UserUtils;

import butterknife.BindView;

/**
 * Created by JiangZhiGuo on 2016/06/01
 * describe 启动界面
 */
public class WelcomeActivity extends BaseActivity<WelcomeActivity> {

    @Override
    protected int getView(Intent intent) {
        BarUtils.setBarTrans(mActivity, true);
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        // todo ...非网络性init操作
        httpEntry();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish(); //记得关闭欢迎页
    }

    private void httpEntry() {
        Long waitTime = ConstantUtils.SEC; //todo 等待时间
        long entryTime = DateUtils.getCurrentLong();
        // todo ...httpEntry
        long between = DateUtils.getCurrentLong() - entryTime;
        if (between >= waitTime) {
            goNext();
        } else {
            long newWait = waitTime - between;
            MyApp.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goNext();
                }
            }, newWait);
        }
    }

    private void goNext() {
        if (UserUtils.noLogin()) {
            LoginActivity.goActivity(mActivity);
        } else {
            HomeActivity.goActivity(mActivity);
        }
    }

}
