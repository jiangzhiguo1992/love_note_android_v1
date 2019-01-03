package com.jiangzg.lovenote.controller.activity.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.TimeUnit;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Sms;
import com.jiangzg.lovenote.model.entity.User;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class ForgetActivity extends BaseActivity<ForgetActivity> {

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
    @BindView(R.id.btnOk)
    Button btnOk;

    private Call<Result> callSms;
    private Call<Result> callModify;
    private int countDownGo = -1;
    private Runnable countDownTask;
    private boolean isGo = false;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, ForgetActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_forget;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.forget_pwd), true);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        stopCountDownTask();
        RetrofitHelper.cancel(callSms);
        RetrofitHelper.cancel(callModify);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isGo) {
            finish();
        }
    }

    @OnTextChanged({R.id.etPhone, R.id.etPwd, R.id.etPwdConfirm, R.id.etCode})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.btnSendCode, R.id.btnOk})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSendCode: // 验证码
                sendCode();
                break;
            case R.id.btnOk: // 完成
                forget();
                break;
        }
    }

    private void onInputChange() {
        boolean phone = etPhone.getText().toString().trim().length() > 0;
        boolean pwd = etPwd.getText().toString().length() > 0;
        boolean pwdConfirm = etPwdConfirm.getText().toString().length() > 0;
        boolean code = etCode.getText().toString().trim().length() == SPHelper.getLimit().getSmsCodeLength();

        btnOk.setEnabled(phone && pwd && pwdConfirm && code);
        if (countDownGo >= 0) {
            btnSendCode.setEnabled(false);
        } else {
            btnSendCode.setEnabled(phone);
        }
    }

    private void sendCode() {
        btnSendCode.setEnabled(false);
        String phone = etPhone.getText().toString().trim();
        // 发送验证码
        Sms body = ApiHelper.getSmsForgetBody(phone);
        callSms = new RetrofitHelper().call(API.class).smsSend(body);
        MaterialDialog loading = getLoading(getString(R.string.are_send_validate_code), true);
        RetrofitHelper.enqueue(callSms, loading, new RetrofitHelper.CallBack() {
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
                            btnSendCode.setText(String.valueOf(countDownSec - countDownGo) + "s");
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

    private void forget() {
        String pwd = etPwd.getText().toString();
        String pwdConfirm = etPwdConfirm.getText().toString();
        if (!pwd.equals(pwdConfirm)) {
            ToastUtils.show(getString(R.string.twice_pwd_no_equals));
            return;
        }
        String phone = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        User user = ApiHelper.getUserBody(phone, pwd);
        // api调用
        callModify = new RetrofitHelper().call(API.class).userModify(ApiHelper.MODIFY_FORGET, code, "", user);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callModify, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                isGo = true;
                stopCountDownTask();
                User user = data.getUser();
                SPHelper.setMe(user);
                ApiHelper.postEntry(mActivity);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
