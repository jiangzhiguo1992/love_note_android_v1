package com.jiangzg.lovenote.controller.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;

import com.jiangzg.base.common.EncryptUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.User;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class PasswordActivity extends BaseActivity<PasswordActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etOldPwd)
    AppCompatEditText etOldPwd;
    @BindView(R.id.etNewPwd)
    AppCompatEditText etNewPwd;
    @BindView(R.id.etNewPwdConfirm)
    AppCompatEditText etNewPwdConfirm;
    @BindView(R.id.btnModify)
    Button btnModify;

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
        boolean newPwd = etNewPwd.getText().toString().length() > 0;
        boolean newPwdConfirm = etNewPwdConfirm.getText().toString().length() > 0;
        btnModify.setEnabled(newPwd && newPwdConfirm);
    }

    private void modify() {
        String oldPwd = etOldPwd.getText().toString();
        String newPwd = etNewPwd.getText().toString();
        String newPwdConfirm = etNewPwdConfirm.getText().toString();
        if (!newPwd.equals(newPwdConfirm)) {
            ToastUtils.show(getString(R.string.twice_pwd_no_equals));
            return;
        }
        String md5OldPwd = EncryptUtils.encryptMD5ToString(oldPwd);
        User user = ApiHelper.getUserBody("", newPwd);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).userModify(ApiHelper.MODIFY_PASSWORD, "", md5OldPwd, user);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
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
