package com.jiangzg.mianmian.activity.user;

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
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class PasswordActivity extends BaseActivity<PasswordActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etOldPwd)
    TextInputEditText etOldPwd;
    @BindView(R.id.etNewPwd)
    TextInputEditText etNewPwd;
    @BindView(R.id.etNewPwdConfirm)
    TextInputEditText etNewPwdConfirm;
    @BindView(R.id.btnModify)
    Button btnModify;

    private Call<Result> call;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, PasswordActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_password;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.modify_password), true);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
    }

    @OnTextChanged({R.id.etOldPwd, R.id.etNewPwd, R.id.etNewPwdConfirm})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.btnModify})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnModify:
                modify();
                break;
        }
    }

    private void onInputChange() {
        boolean oldPwd = etOldPwd.getText().toString().trim().length() > 0;
        boolean newPwd = etNewPwd.getText().toString().trim().length() > 0;
        boolean newPwdConfirm = etNewPwdConfirm.getText().toString().trim().length() > 0;

        btnModify.setEnabled(oldPwd && newPwd && newPwdConfirm);
    }

    private void modify() {
        String oldPwd = etOldPwd.getText().toString().trim();
        String newPwd = etNewPwd.getText().toString().trim();
        String newPwdConfirm = etNewPwdConfirm.getText().toString().trim();
        if (!newPwd.equals(newPwdConfirm)) {
            ToastUtils.show(getString(R.string.twice_pwd_no_equals));
            return;
        }
        // api调用
        User user = ApiHelper.getUserPasswordBody(oldPwd, newPwd);
        // api调用
        call = new RetrofitHelper().call(API.class).userModify(user);
        MaterialDialog loading = getLoading("", true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                User user = data.getUser();
                SPHelper.setMe(user);
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
