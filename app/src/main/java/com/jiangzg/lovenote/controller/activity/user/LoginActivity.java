package com.jiangzg.lovenote.controller.activity.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.settings.UserProtocolActivity;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Sms;
import com.jiangzg.lovenote.model.entity.User;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity<LoginActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etPhone)
    AppCompatEditText etPhone;
    @BindView(R.id.etPwd)
    AppCompatEditText etPwd;
    @BindView(R.id.etCode)
    AppCompatEditText etCode;
    @BindView(R.id.btnSendCode)
    Button btnSendCode;
    @BindView(R.id.rgLoginType)
    RadioGroup rgLoginType;
    @BindView(R.id.rbLoginPwd)
    RadioButton rbLoginPwd;
    @BindView(R.id.rbLoginVerify)
    RadioButton rbLoginVerify;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.tilPwd)
    TextInputLayout tilPwd;
    @BindView(R.id.llVerify)
    LinearLayout llVerify;
    @BindView(R.id.tvForget)
    TextView tvForget;

    private boolean isGo = false;
    private int logType;
    private int countDownGo = -1;
    private Runnable countDownTask;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.login), true);
        ViewUtils.setLineBottom(tvForget);
        // loginType
        rgLoginType.setOnCheckedChangeListener((group, checkedId) -> toggleLoginType());
        // 默认密码登录
        rbLoginPwd.setChecked(true);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        stopCountDownTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_protocol, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuProtocol: // 用户协议
                UserProtocolActivity.goActivity(mActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etPhone, R.id.etPwd, R.id.etCode})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.btnSendCode, R.id.tvForget, R.id.btnLogin, R.id.btnRegister})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSendCode: // 验证码
                sendCode();
                break;
            case R.id.tvForget: // 忘记密码
                ForgetActivity.goActivity(mActivity);
                break;
            case R.id.btnLogin: // 登录
                login();
                break;
            case R.id.btnRegister: // 注册
                RegisterActivity.goActivity(mActivity);
                break;
        }
    }

    private void onInputChange() {
        boolean phone = etPhone.getText().toString().trim().length() > 0;
        boolean pwd = etPwd.getText().toString().length() > 0;
        boolean code = etCode.getText().toString().trim().length() == SPHelper.getLimit().getSmsCodeLength();
        switch (logType) {
            case ApiHelper.LOG_PWD: // 密码
                btnLogin.setEnabled(phone && pwd);
                break;
            case ApiHelper.LOG_VER: // 验证码
                btnLogin.setEnabled(phone && code);
                if (countDownGo >= 0) {
                    btnSendCode.setEnabled(false);
                } else {
                    btnSendCode.setEnabled(phone);
                }
                break;
        }
    }

    private void toggleLoginType() {
        if (rbLoginVerify.isChecked()) {
            logType = ApiHelper.LOG_VER;
        } else {
            logType = ApiHelper.LOG_PWD;
        }
        onInputChange();
        switch (logType) {
            case ApiHelper.LOG_PWD: // 显示密码
                tilPwd.setVisibility(View.VISIBLE);
                llVerify.setVisibility(View.GONE);
                break;
            case ApiHelper.LOG_VER: // 显示验证码
                tilPwd.setVisibility(View.GONE);
                llVerify.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void sendCode() {
        btnSendCode.setEnabled(false);
        String phone = etPhone.getText().toString().trim();
        Sms body = ApiHelper.getSmsLoginBody(phone);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).smsSend(body);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                countDownGo = 0;
                MyApp.get().getHandler().post(getCountDownTask());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                btnSendCode.setEnabled(true);
            }
        });
        pushApi(api);
    }

    private Runnable getCountDownTask() {
        final int countDownSec = SPHelper.getLimit().getSmsBetweenSec();
        if (countDownTask == null) {
            countDownTask = new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    if (!isGo) { // 防止跳转的时候，控件注销，空指针异常
                        if (countDownGo < countDownSec) {
                            ++countDownGo;
                            btnSendCode.setText((countDownSec - countDownGo) + "s");
                            MyApp.get().getHandler().postDelayed(this, TimeUnit.SEC);
                        } else {
                            stopCountDownTask();
                        }
                    }
                }
            };
        }
        return countDownTask;
    }

    private void stopCountDownTask() {
        countDownGo = -1;
        btnSendCode.setText(R.string.send_validate_code);
        onInputChange();
        if (countDownTask != null) {
            MyApp.get().getHandler().removeCallbacks(countDownTask);
            countDownTask = null;
        }
    }

    private void login() {
        String phone = etPhone.getText().toString().trim();
        String password = etPwd.getText().toString();
        String code = etCode.getText().toString().trim();
        User user = ApiHelper.getUserBody(phone, password);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).userLogin(logType, code, user);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                isGo = true;
                stopCountDownTask();
                SPHelper.setMe(data.getUser());
                ApiHelper.postEntry(mActivity);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}

