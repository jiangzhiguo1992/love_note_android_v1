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

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.ita.R;
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

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class PhoneActivity extends BaseActivity<PhoneActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etPhone)
    TextInputEditText etPhone;
    @BindView(R.id.etCode)
    TextInputEditText etCode;
    @BindView(R.id.btnSendCode)
    Button btnSendCode;
    @BindView(R.id.btnChange)
    Button btnChange;

    private boolean isGo = false;
    private int countDownGo = -1;
    private Runnable countDownTask;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, PhoneActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_phone;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.change_phone), true);

    }

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCountDownTask();
    }

    @OnTextChanged({R.id.etPhone, R.id.etCode})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.btnSendCode, R.id.btnChange})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSendCode:
                sendCode();
                break;
            case R.id.btnChange:
                change();
                break;
        }
    }

    private void onInputChange() {
        boolean phone = etPhone.getText().toString().trim().length() > 0;
        boolean code = etCode.getText().toString().trim().length() > 0;

        btnChange.setEnabled(phone && code);
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
        Sms body = ApiHelper.getSmsPhoneBody(phone);
        final Call<Result> call = new RetrofitHelper().call(API.class).smsSend(body);
        MaterialDialog loading = getLoading(getString(R.string.are_send_validate_code), true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                countDownGo = 0;
                MyApp.get().getHandler().post(getCountDownTask());
            }

            @Override
            public void onFailure() {
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

    private void change() {
        String phone = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        // api调用
        User user = ApiHelper.getUserPhoneBody(phone, code);
        // api调用
        final Call<Result> call = new RetrofitHelper().call(API.class).userModify(user);
        MaterialDialog loading = getLoading("", true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                isGo = true;
                stopCountDownTask();
                User user = data.getUser();
                SPHelper.setUser(user);
                mActivity.finish();
            }

            @Override
            public void onFailure() {
            }
        });
    }

}
