package com.jiangzg.ita.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.EncryptUtils;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.third.API;
import com.jiangzg.ita.third.RetroManager;
import com.jiangzg.ita.utils.UserUtils;
import com.jiangzg.ita.utils.ViewUtils;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import retrofit2.Call;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity<LoginActivity> {

    private static final int COUNT_DOWN_SEC = 60;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.isLeft)
    ImageSwitcher isLeft;
    @BindView(R.id.isRight)
    ImageSwitcher isRight;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.etPwd)
    EditText etPwd;
    @BindView(R.id.etVerify)
    EditText etVerify;
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

    private int logType = User.LOG_PWD;
    private int countDownGo = 0;
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
        //左右输入框焦点图片
        isLeft.setFactory(getViewFactory(mActivity));
        isRight.setFactory(getViewFactory(mActivity));
    }

    @OnTextChanged({R.id.etPhone, R.id.etPwd, R.id.etVerify})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnFocusChange({R.id.etPwd})
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) onInputPwd();
        else onInputPhone();
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
                ForgetActivity.goActivity(mActivity);
                break;
        }
    }

    @Override
    protected void initData(Bundle state) {
        onInputPhone();
    }

    private void onInputChange() {
        boolean b1 = etPhone.getText().toString().trim().length() > 0;
        boolean b2 = etPwd.getText().toString().trim().length() > 0;
        boolean b3 = etVerify.getText().toString().trim().length() > 0;
        switch (logType) {
            case User.LOG_PWD: //密码
                btnLogin.setEnabled(b1 && b2);
                break;
            case User.LOG_VER: //验证码
                btnLogin.setEnabled(b1 && b3);
                if (countDownGo > 0) {
                    btnSendCode.setEnabled(false);
                } else {
                    btnSendCode.setEnabled(b1);
                }
                break;
        }
    }

    private void onInputPwd() {
        isLeft.setImageResource(R.mipmap.ic_22_hide);
        isRight.setImageResource(R.mipmap.ic_33_hide);
    }

    private void onInputPhone() {
        isLeft.setImageResource(R.mipmap.ic_22);
        isRight.setImageResource(R.mipmap.ic_33);
    }

    private void toggleType() {
        if (logType == User.LOG_VER) {
            logType = User.LOG_PWD;
        } else {
            logType = User.LOG_VER;
        }
        onInputChange();
        switch (logType) {
            case User.LOG_PWD: //密码
                tvLoginType.setText(R.string.pwd_login);
                tilPwd.setVisibility(View.VISIBLE);
                llVerify.setVisibility(View.GONE);
                break;
            case User.LOG_VER: //验证码
                tvLoginType.setText(R.string.verify_login);
                tilPwd.setVisibility(View.GONE);
                llVerify.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void sendCode() {
        String phone = etPhone.getText().toString().trim();

        User user = new User();
        user.setPhone(phone);

        Call<Result> call = new RetroManager().call(API.class).validate(User.VALIDATE_REGISTER, user);
        ProgressDialog loading = getLoading(getString(R.string.loading_sending), call, null);
        RetroManager.enqueue(call, loading, new RetroManager.CallBack() {
            @Override
            public void onResponse(int code, Result.Data data) {
                validateCountDown();
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private void validateCountDown() {
        btnSendCode.setEnabled(false);
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MyApp.get().getHandler().post(new Runnable() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void run() {
                        if (countDownGo < COUNT_DOWN_SEC) {
                            btnSendCode.setText(String.valueOf(COUNT_DOWN_SEC - countDownGo) + "s");
                            ++countDownGo;
                        } else {
                            timer.cancel();
                            btnSendCode.setText(R.string.send_validate_code);
                            countDownGo = 0;
                            onInputChange();
                        }
                    }
                });
            }
        }, 0, ConstantUtils.SEC);
    }

    private void login() {
        String phone = etPhone.getText().toString().trim();
        String password = etPwd.getText().toString().trim();
        String validateCode = etVerify.getText().toString().trim();
        String md5Pwd = EncryptUtils.encryptMD5ToString(password);

        User user = new User();
        user.setPhone(phone);
        user.setPassword(md5Pwd);
        user.setValidateCode(validateCode);
        user.setType(logType);

        Call<Result> call = new RetroManager().call(API.class).userLogin(user);
        ProgressDialog loading = getLoading(getString(R.string.loading_loging), call, null);
        RetroManager.enqueue(call, loading, new RetroManager.CallBack() {
            @Override
            public void onResponse(int code, Result.Data data) {
                User user = data.getUser();
                UserUtils.setUser(user);
                switch (code) {
                    case Result.ResultStatusSuc: //home
                        UserUtils.setCouple(user.getCouple());
                        HomeActivity.goActivity(mActivity);
                        break;
                    case Result.ResultStatusNoCp: //couple
                        NoCpActivity.goActivity(mActivity);
                        break;
                }
            }

            @Override
            public void onFailure() {
            }
        });
    }

    public ViewSwitcher.ViewFactory getViewFactory(final Context context) {
        return new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new ImageView(context);
            }
        };
    }

}

