package com.jiangzg.lovenote.activity.user;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;

import java.util.Stack;

import butterknife.BindView;
import butterknife.OnClick;

public class SplashActivity extends BaseActivity<SplashActivity> {

    @BindView(R.id.vp)
    ViewPager vp;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.btnRegister)
    Button btnRegister;

    public static void goActivity(Activity from) {
        // 顶部已经是SplashActivity时，不再跳转
        Activity top = ActivityStack.getTop();
        if (top != null) {
            ComponentName name = top.getComponentName();
            if (name.getClassName().equals(SplashActivity.class.getSimpleName())) {
                return;
            }
        }
        Intent intent = new Intent(from, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {

    }

    @Override
    protected void initData(Intent intent, Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

    // 关闭其他activity
    @Override
    protected void onStart() {
        super.onStart();
        Stack<Activity> stack = ActivityStack.getStack();
        for (Activity activity : stack) {
            if (activity != mActivity) {
                activity.finish();
            }
        }
    }

    @OnClick({R.id.btnLogin, R.id.btnRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnLogin:
                LoginActivity.goActivity(mActivity);
                break;
            case R.id.btnRegister:
                RegisterActivity.goActivity(mActivity);
                break;
        }
    }
}
