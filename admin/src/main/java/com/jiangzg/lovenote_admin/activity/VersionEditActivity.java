package com.jiangzg.lovenote_admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Version;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.SPHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class VersionEditActivity extends BaseActivity<VersionEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.etPlatform)
    EditText etPlatform;
    @BindView(R.id.etUrl)
    EditText etUrl;
    @BindView(R.id.etLog)
    EditText etLog;
    @BindView(R.id.btnPush)
    Button btnPush;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), VersionEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_version_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "version_edit", true);
        etPlatform.setText("android");
        etUrl.setText(SPHelper.getOssInfo().getPathVersion() + "android/");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.btnPush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnPush: // 发布
                push();
                break;
        }
    }

    private void push() {
        Version version = new Version();
        String code = etCode.getText().toString();
        if (!StringUtils.isNumber(code)) {
            ToastUtils.show("code错误");
            return;
        }
        version.setPlatform(etPlatform.getText().toString());
        version.setVersionName(etName.getText().toString());
        version.setVersionCode(Integer.parseInt(code));
        version.setUpdateUrl(etUrl.getText().toString());
        version.setUpdateLog(etLog.getText().toString());
        MaterialDialog loading = getLoading(false);
        Call<Result> call = new RetrofitHelper().call(API.class).versionAdd(version);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
