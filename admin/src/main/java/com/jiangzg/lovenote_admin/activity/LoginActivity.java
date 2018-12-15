package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.EncryptUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.base.MyApp;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.User;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ResHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.SPHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class LoginActivity extends BaseActivity<LoginActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etPhone)
    TextInputEditText etPhone;
    @BindView(R.id.etPwd)
    TextInputEditText etPwd;
    @BindView(R.id.btnLogin)
    Button btnLogin;

    public static void goActivity(Activity from) {
        // 顶部已经是LoginActivity时，不再跳转
        Activity top = ActivityStack.getTop();
        if (top != null) {
            ComponentName name = top.getComponentName();
            if (name.getClassName().equals(LoginActivity.class.getSimpleName())) {
                return;
            }
        }
        Intent intent = new Intent(from, LoginActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.login), false);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        User user = SPHelper.getUser();
        String phone = user.getPhone();
        String password = user.getPassword();
        etPhone.setText(phone);
        etPwd.setText(password);
        // cache
        //MyApp.get().getThread().execute(new Runnable() {
        //    @Override
        //    public void run() {
        //        String cachesSizeFmt = ResHelper.getCachesSizeFmt();
        //        ResHelper.clearCaches();
        //        ToastUtils.show("清理缓存大小：" + cachesSizeFmt);
        //    }
        //});
        // login
        if (!StringUtils.isEmpty(phone) && !StringUtils.isEmpty(password)) {
            login();
        }
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.btnLogin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin: // 登录
                login();
                break;
        }
    }

    private void login() {
        String phone = etPhone.getText().toString().trim();
        final String password = etPwd.getText().toString();
        String pwd = EncryptUtils.encryptMD5ToString(password);
        // api调用
        Call<Result> callLogin = new RetrofitHelper().call(API.class).userLogin(phone, pwd);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callLogin, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                User user = data.getUser();
                user.setPassword(password);
                SPHelper.setUser(user);
                HomeActivity.goActivity(mActivity);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
