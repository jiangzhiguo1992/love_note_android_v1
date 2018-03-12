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

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.utils.ViewUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

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

    private int countDownGo = -1;
    private Timer timer;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, PhoneActivity.class);
        // intent.putExtra();
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_phone;
    }

    @Override
    protected void initView(Bundle state) {
        ViewUtils.initTopBar(mActivity, tb, getString(R.string.change_phone), true);

    }

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTimer();
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
        String phone = etPhone.getText().toString().trim();
        User user = new User();
        user.setPhone(phone);
        // todo 发送验证码
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
        // todo 发送成功执行 validateCountDown 失败误操作
        validateCountDown(60);
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

    private void change() {
        String phone = etPhone.getText().toString().trim();
        String code = etCode.getText().toString().trim();
        // todo api调用
        stopTimer();
        mActivity.finish();
    }

}
