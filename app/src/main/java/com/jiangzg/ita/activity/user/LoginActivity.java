package com.jiangzg.ita.activity.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.WebActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Sms;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.helper.ViewHelper;

import java.util.Stack;

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
    TextInputEditText etPhone;
    @BindView(R.id.etPwd)
    TextInputEditText etPwd;
    @BindView(R.id.etCode)
    TextInputEditText etCode;
    @BindView(R.id.btnSendCode)
    Button btnSendCode;
    @BindView(R.id.btnLoginType)
    Button btnLoginType;
    @BindView(R.id.btnLogin)
    Button btnLogin;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.tilPwd)
    TextInputLayout tilPwd;
    @BindView(R.id.llVerify)
    LinearLayout llVerify;
    @BindView(R.id.cbProtocol)
    CheckBox cbProtocol;
    @BindView(R.id.tvProtocol)
    TextView tvProtocol;

    private boolean isGo = false;
    private int logType = ApiHelper.LOG_PWD;
    private int countDownGo = -1;
    private Runnable countDownTask;

    public static void goActivity(Activity from) {
        // 顶部已经是LoginActivity时，不再跳转
        Activity top = ActivityStack.getTop();
        if (top != null) {
            ComponentName name = top.getComponentName();
            if (name.getClassName().equals("LoginActivity")) {
                return;
            }
        }
        Intent intent = new Intent(from, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.login), false);
        ViewHelper.setLineBottom(tvProtocol);
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuForget: // 忘记密码
                        ForgetActivity.goActivity(mActivity);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void initData(Bundle state) {

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

    @Override
    protected void onStop() {
        super.onStop();
        stopCountDownTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_forget, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @OnTextChanged({R.id.etPhone, R.id.etPwd, R.id.etCode})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.btnLoginType, R.id.btnSendCode, R.id.btnLogin, R.id.btnRegister, R.id.tvProtocol})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLoginType: // 登录类型
                toggleType();
                break;
            case R.id.btnSendCode: // 验证码
                sendCode();
                break;
            case R.id.btnLogin: // 登录
                login();
                break;
            case R.id.btnRegister: // 注册
                RegisterActivity.goActivity(mActivity);
                break;
            case R.id.tvProtocol: // 用户协议
                WebActivity.goActivity(mActivity, WebActivity.TYPE_USER_PROTOCOL);
                break;
        }
    }

    private void onInputChange() {
        boolean phone = etPhone.getText().toString().trim().length() > 0;
        boolean pwd = etPwd.getText().toString().trim().length() > 0;
        boolean code = etCode.getText().toString().trim().length() > 0;
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

    private void toggleType() {
        if (logType == ApiHelper.LOG_VER) {
            logType = ApiHelper.LOG_PWD;
        } else {
            logType = ApiHelper.LOG_VER;
        }
        onInputChange();
        switch (logType) {
            case ApiHelper.LOG_PWD: // 显示密码
                btnLoginType.setText(R.string.verify_login);
                tilPwd.setVisibility(View.VISIBLE);
                llVerify.setVisibility(View.GONE);
                break;
            case ApiHelper.LOG_VER: // 显示验证码
                btnLoginType.setText(R.string.pwd_login);
                tilPwd.setVisibility(View.GONE);
                llVerify.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void sendCode() {
        btnSendCode.setEnabled(false);
        String phone = etPhone.getText().toString().trim();
        // 发送验证码
        Sms body = ApiHelper.getSmsLoginBody(phone);
        final Call<Result> call = new RetrofitHelper().call(API.class).smsSend(body);
        MaterialDialog loading = getLoading(getString(R.string.are_send_validate_code), true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                countDownGo = 0;
                MyApp.get().getHandler().post(getCountDownTask());
            }

            @Override
            public void onFailure(String errMsg) {
                btnSendCode.setEnabled(true);
            }
        });
    }

    private Runnable getCountDownTask() {
        final int countDownSec = SPHelper.getLimit().getSmsLimitBetween();
        if (countDownTask == null) {
            countDownTask = new Runnable() {
                @SuppressLint("SetTextI18n")
                @Override
                public void run() {
                    if (!isGo) { // 防止跳转的时候，控件注销，空指针异常
                        if (countDownGo < countDownSec) {
                            ++countDownGo;
                            btnSendCode.setText(String.valueOf(countDownSec - countDownGo) + "s");
                            MyApp.get().getHandler().postDelayed(this, ConstantUtils.SEC);
                        } else {
                            btnSendCode.setText(R.string.send_validate_code);
                            countDownGo = -1;
                            onInputChange();
                            MyApp.get().getHandler().removeCallbacks(this);
                        }
                    }
                }
            };
        }
        return countDownTask;
    }

    private void stopCountDownTask() {
        if (countDownTask != null) {
            MyApp.get().getHandler().removeCallbacks(countDownTask);
            countDownTask = null;
        }
    }

    private void login() {
        if (!cbProtocol.isChecked()) {
            ToastUtils.show(getString(R.string.please_check_agree_protocol));
            return;
        }
        String phone = etPhone.getText().toString().trim();
        String password = etPwd.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        User user = ApiHelper.getUserLoginBody(phone, password, code, logType);
        // api调用
        final Call<Result> call = new RetrofitHelper().call(API.class).userLogin(user);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                isGo = true;
                stopCountDownTask();
                User user = data.getUser();
                SPHelper.setUser(user);
                ApiHelper.postEntry(mActivity);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}

