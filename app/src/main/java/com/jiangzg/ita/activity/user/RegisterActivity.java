package com.jiangzg.ita.activity.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.HomeActivity;
import com.jiangzg.ita.activity.common.WebActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Sms;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.PrefHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.third.API;
import com.jiangzg.ita.third.RetrofitHelper;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class RegisterActivity extends BaseActivity<RegisterActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etPhone)
    TextInputEditText etPhone;
    @BindView(R.id.etPwd)
    TextInputEditText etPwd;
    @BindView(R.id.etPwdConfirm)
    TextInputEditText etPwdConfirm;
    @BindView(R.id.etCode)
    TextInputEditText etCode;
    @BindView(R.id.btnSendCode)
    Button btnSendCode;
    @BindView(R.id.btnRegister)
    Button btnRegister;
    @BindView(R.id.cbProtocol)
    CheckBox cbProtocol;
    @BindView(R.id.tvProtocol)
    TextView tvProtocol;

    private int countDownGo = -1;
    private Timer timer;
    private boolean autoFinish = false;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, RegisterActivity.class);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_register;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.register), true);
        ViewHelper.setLineBottom(tvProtocol);
    }

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
        if (autoFinish) {
            finish();
        }
    }

    @OnTextChanged({R.id.etPhone, R.id.etPwd, R.id.etPwdConfirm, R.id.etCode})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.btnSendCode, R.id.btnRegister, R.id.tvProtocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSendCode: // 验证码
                sendCode();
                break;
            case R.id.btnRegister: // 注册
                register();
                break;
            case R.id.tvProtocol: // 用户协议
                WebActivity.goActivity(mActivity, WebActivity.TYPE_USER_PROTOCOL);
                break;
        }
    }

    private void onInputChange() {
        boolean phone = etPhone.getText().toString().trim().length() > 0;
        boolean pwd = etPwd.getText().toString().trim().length() > 0;
        boolean pwdConfirm = etPwdConfirm.getText().toString().trim().length() > 0;
        boolean code = etCode.getText().toString().trim().length() > 0;

        btnRegister.setEnabled(phone && pwd && pwdConfirm && code);
        if (countDownGo >= 0) {
            btnSendCode.setEnabled(false);
        } else {
            btnSendCode.setEnabled(phone);
        }
    }

    private void sendCode() {
        String phone = etPhone.getText().toString().trim();
        // 发送验证码
        Sms body = Sms.getRegisterBody(phone);
        final Call<Result> call = new RetrofitHelper().call(API.class).smsSend(body);
        MaterialDialog loading = getLoading(getString(R.string.are_send_validate_code), true);
        RetrofitHelper.enqueueLoading(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                validateCountDown(data.getCountDownSec());
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private void validateCountDown(final int countDownSec) {
        countDownGo = 0;
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
                            btnSendCode.setText(R.string.send_validate_code);
                            countDownGo = -1;
                            onInputChange();
                            stopTimer();
                        }
                    }
                });
            }
        }, 0, ConstantUtils.SEC);
    }

    private void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void register() {
        if (!cbProtocol.isChecked()) {
            ToastUtils.show(getString(R.string.please_check_agree_protocol));
            return;
        }
        String pwd = etPwd.getText().toString().trim();
        String pwdConfirm = etPwdConfirm.getText().toString().trim();
        if (!pwd.equals(pwdConfirm)) {
            ToastUtils.show(getString(R.string.twice_pwd_no_equals));
            return;
        }
        String phone = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        User user = User.getRegisterBody(phone, pwd, code);
        // api调用
        final Call<Result> call = new RetrofitHelper().call(API.class).userRegister(user);
        MaterialDialog loading = getLoading("", true);
        RetrofitHelper.enqueueLoading(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                autoFinish = true;
                stopTimer();
                User user = data.getUser();
                PrefHelper.setUser(user);
                if (code == Result.ResultCodeNoUserInfo) {
                    UserInfoActivity.goActivity(mActivity);
                } else {
                    ApiHelper.onEntryFinish(mActivity, code, data);
                }
            }

            @Override
            public void onFailure() {
            }
        });
    }

}
