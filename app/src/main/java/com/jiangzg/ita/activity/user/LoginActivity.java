package com.jiangzg.ita.activity.user;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.activity.ActivityStack;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.HomeActivity;
import com.jiangzg.ita.activity.common.WebActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.third.RxBus;
import com.jiangzg.ita.utils.Constants;
import com.jiangzg.ita.utils.UserPreference;
import com.jiangzg.ita.utils.ViewUtils;

import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

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

    private int logType = User.LOG_PWD;
    private int countDownGo = -1;
    private Timer timer;

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
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_login;
    }

    @Override
    protected void initView(Bundle state) {
        ViewUtils.initTopBar(mActivity, tb, getString(R.string.login), false);
        ViewUtils.setLineBottom(tvProtocol);
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
        stopTimer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
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
                btnLoginType.setText(R.string.verify_login);
                tilPwd.setVisibility(View.VISIBLE);
                llVerify.setVisibility(View.GONE);
                break;
            case User.LOG_VER: // 显示验证码
                btnLoginType.setText(R.string.pwd_login);
                tilPwd.setVisibility(View.GONE);
                llVerify.setVisibility(View.VISIBLE);
                break;
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

    private void login() {
        if (!cbProtocol.isChecked()) {
            ToastUtils.show(getString(R.string.please_check_agree_protocol));
            return;
        }
        String phone = etPhone.getText().toString().trim();
        String password = etPwd.getText().toString().trim();
        String code = etCode.getText().toString().trim();

        User user = User.getLogin(phone, password, code, logType);

        // todo api调用
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
        stopTimer();
        HomeActivity.goActivity(mActivity);
        mActivity.finish();
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
        stopTimer();
        HomeActivity.goActivity(mActivity);
        mActivity.finish();
    }

}

