package com.jiangzg.ita.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

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

    @BindView(R.id.ivWelcome)
    ImageView ivWelcome;

    @Override
    protected int getView(Intent intent) {
        //BarUtils.hideStatusBar(this);
        BarUtils.setStatusColor(mActivity, Color.TRANSPARENT);
        BarUtils.setBarTrans(mActivity, true);
        return R.layout.activity_welcome;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        // todo ...非网络性init操作
        //goNext();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish(); //记得关闭欢迎页
    }

    private void goNext() {
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (UserUtils.noLogin()) {
                    LoginActivity.goActivity(mActivity);
                } else {
                    HomeActivity.goActivity(mActivity);
                }
            }
        }, 500); // todo 欢迎页展示时间

    }

}
