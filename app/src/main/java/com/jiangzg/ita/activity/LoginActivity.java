package com.jiangzg.ita.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageSwitcher;

import com.jiangzg.base.common.EncryptUtils;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.BaseObj;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.third.API;
import com.jiangzg.ita.third.RetroManager;
import com.jiangzg.ita.utils.ViewUtils;

import butterknife.BindView;
import retrofit2.Call;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity<LoginActivity> {

    //用户登录类型
    private static final int LOG_PWD = 1;
    private static final int LOG_VER = 2;

    @BindView(R.id.isLeft)
    ImageSwitcher isLeft;
    @BindView(R.id.isRight)
    ImageSwitcher isRight;
    @BindView(R.id.etPhone)
    AppCompatEditText etPhone;
    @BindView(R.id.etPwd)
    AppCompatEditText etPwd;
    @BindView(R.id.btnLogin)
    Button btnLogin;

    Call<Result> callLogin;

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
        //左右输入框焦点图片
        isLeft.setFactory(ViewUtils.getViewFactory(mActivity));
        isRight.setFactory(ViewUtils.getViewFactory(mActivity));
        //输入框文本监听
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onInputChange();
            }
        });
        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onInputChange();
            }
        });
        //密码框焦点监听
        etPwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) onInputPwd();
                else onInputPhone();
            }
        });
        //登录
        btnLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }

    @Override
    protected void initData(Bundle state) {
        onInputPhone();
    }

    private void onInputChange() {
        boolean b1 = etPhone.getText().toString().trim().length() > 0;
        boolean b2 = etPwd.getText().toString().trim().length() > 0;
        btnLogin.setEnabled(b1 && b2);
    }

    private void onInputPwd() {
        isLeft.setImageResource(R.mipmap.ic_22_hide);
        isRight.setImageResource(R.mipmap.ic_33_hide);
    }

    private void onInputPhone() {
        isLeft.setImageResource(R.mipmap.ic_22);
        isRight.setImageResource(R.mipmap.ic_33);
    }

    private void attemptLogin() {

        String phone = etPhone.getText().toString().trim();
        String password = etPwd.getText().toString().trim();
        String md5Pwd = EncryptUtils.encryptMD5ToString(password);

        User user = new User();
        user.setPhone(phone);
        user.setPassword(md5Pwd);
        user.setValidateCode("");//todo
        user.setType(LOG_PWD);//todo

        callLogin = new RetroManager().call(API.class).userLogin(user);
        ProgressDialog loading = getLoading("正在登录...", callLogin, null);
        RetroManager.enqueue(callLogin, loading, new RetroManager.CallBack() {
            @Override
            public void onResponse(int code, Result.Data data) {
                if (code == BaseObj.CODE_OK && data != null && data.getUser() != null) {
                    User user = data.getUser();
                    ToastUtils.show(user.toString());
                }
            }

            @Override
            public void onFailure() {
            }
        });
    }

}

