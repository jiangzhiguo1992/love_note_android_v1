package com.jiangzg.lovenote.controller.activity.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;

import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
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

public class PhoneActivity extends BaseActivity<PhoneActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etPhone)
    AppCompatEditText etPhone;
    @BindView(R.id.etCode)
    AppCompatEditText etCode;
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
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.change_phone), true);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
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
        boolean code = etCode.getText().toString().trim().length() == SPHelper.getLimit().getSmsCodeLength();

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
        Sms body = ApiHelper.getSmsPhoneBody(phone);
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

    private void change() {
        String phone = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        User user = ApiHelper.getUserBody(phone, "");
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).userModify(ApiHelper.MODIFY_PHONE, code, "", user);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                isGo = true;
                stopCountDownTask();
                SPHelper.setMe(data.getUser());
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
