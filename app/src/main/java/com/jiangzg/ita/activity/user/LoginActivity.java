package com.jiangzg.ita.activity.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.HomeActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.third.API;
import com.jiangzg.ita.third.RetroManager;
import com.jiangzg.ita.third.RxBus;
import com.jiangzg.ita.utils.Constants;
import com.jiangzg.ita.utils.UserPreference;
import com.jiangzg.ita.utils.ViewUtils;

import java.util.Timer;
import java.util.TimerTask;

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
    EditText etPhone;
    @BindView(R.id.etPwd)
    EditText etPwd;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.btnSendCode)
    Button btnSendCode;
    @BindView(R.id.tvLoginType)
    TextView tvLoginType;
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
    @BindView(R.id.cbProtocol)
    CheckBox cbProtocol;
    @BindView(R.id.tvProtocol)
    TextView tvProtocol;

    private int logType = User.LOG_PWD;
    private int countDownGo = -1;
    private Timer timer;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, LoginActivity.class);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(Bundle state) {
        ViewUtils.initToolbar(mActivity, tb, false);
    }

    @Override
    protected void initData(Bundle state) {

    }

    @OnTextChanged({R.id.etPhone, R.id.etPwd, R.id.etCode})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.tvLoginType, R.id.btnSendCode, R.id.btnLogin, R.id.btnRegister, R.id.tvForget})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvLoginType: //登录类型
                toggleType();
                break;
            case R.id.btnSendCode: //验证码
                sendCode();
                break;
            case R.id.btnLogin: //登录
                login();
                break;
            case R.id.btnRegister: //注册
                RegisterActivity.goActivity(mActivity);
                break;
            case R.id.tvForget: //忘记密码
                //todo
                break;
        }
    }

    private void onInputChange() {
        boolean phone = etPhone.getText().toString().trim().length() > 0;
        boolean pwd = etPwd.getText().toString().trim().length() > 0;
        boolean code = etCode.getText().toString().trim().length() > 0;
        switch (logType) {
            case User.LOG_PWD: // 密码
                btnLogin.setEnabled(phone && pwd);
                break;
            case User.LOG_VER: // 验证码
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
        if (logType == User.LOG_VER) {
            logType = User.LOG_PWD;
        } else {
            logType = User.LOG_VER;
        }
        onInputChange();
        switch (logType) {
            case User.LOG_PWD: // 显示密码
                tvLoginType.setText(R.string.verify_login);
                tilPwd.setVisibility(View.VISIBLE);
                llVerify.setVisibility(View.GONE);
                break;
            case User.LOG_VER: // 显示验证码
                tvLoginType.setText(R.string.pwd_login);
                tilPwd.setVisibility(View.GONE);
                llVerify.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void sendCode() {
        String phone = etPhone.getText().toString().trim();
        // todo 发送验证码
        validateCountDown(60);
        //User user = new User();
        //user.setPhone(phone);
        //
        //Call<Result> call = new RetroManager().call(API.class).validate(User.VALIDATE_REGISTER, user);
        //ProgressDialog loading = getLoading(getString(R.string.loading_sending), call, null);
        //RetroManager.enqueue(call, loading, new RetroManager.CallBack() {
        //    @Override
        //    public void onResponse(int code, Result.Data data) {
        //        validateCountDown();
        //    }
        //
        //    @Override
        //    public void onFailure() {
        //    }
        //});
    }

    private void validateCountDown(final int countDownSec) {
        btnSendCode.setEnabled(false);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MyApp.get().getHandler().post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        if (countDownGo < countDownSec) {
                            ++countDownGo;
                            btnSendCode.setText(String.valueOf(countDownSec - countDownGo) + "s");
                        } else {
                            timer.cancel();
                            btnSendCode.setText(R.string.send_validate_code);
                            countDownGo = -1;
                            onInputChange();
                        }
                    }
                });
            }
        }, 0, ConstantUtils.SEC);
    }

    private void login() {
        if (!cbProtocol.isChecked()) {
            ToastUtils.show(getString(R.string.please_check_agree_protocol));
            return;
        }
        // todo 登录处理
        HomeActivity.goActivity(mActivity);
        mActivity.finish();

        //String phone = etPhone.getText().toString().trim();
        //String password = etPwd.getText().toString().trim();
        //String validateCode = etCode.getText().toString().trim();
        //
        //User user = User.getLogin(phone, password, validateCode, logType);
        //
        //Call<Result> call = new RetroManager().call(API.class).userLogin(user);
        //ProgressDialog loading = getLoading(getString(R.string.loading_loging), call, null);
        //RetroManager.enqueue(call, loading, new RetroManager.CallBack() {
        //    @Override
        //    public void onResponse(int code, Result.Data data) {
        //        logSuc(data.getUser());
        //    }
        //
        //    @Override
        //    public void onFailure() {
        //    }
        //});
    }

    private void logSuc(User user) {
        // 检查user
        if (user == null) {
            ToastUtils.show(R.string.err_un_know_client);
            return;
        }
        UserPreference.setUser(user);
        RxBus.post(new RxEvent<>(Constants.EVENT_LOGIN_YES, user));
        // 检查couple
        Couple couple = user.getCouple();
        if (!UserPreference.noCouple(couple)) { // 有配对
            UserPreference.setCouple(couple);
        }
        if (timer != null) {
            timer.cancel();
        }
        HomeActivity.goActivity(mActivity);
        mActivity.finish();
    }

}

