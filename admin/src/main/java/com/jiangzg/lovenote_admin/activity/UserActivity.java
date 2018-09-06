package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.BaseObj;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.User;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ApiHelper;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import retrofit2.Call;

public class UserActivity extends BaseActivity<UserActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etId)
    EditText etId;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.btnStatus)
    Button btnStatus;
    @BindView(R.id.tvCreate)
    TextView tvCreate;
    @BindView(R.id.tvUpdate)
    TextView tvUpdate;
    @BindView(R.id.btnSex)
    Button btnSex;
    @BindView(R.id.btnBirthday)
    Button btnBirthday;
    @BindView(R.id.rv)
    RecyclerView rv;

    private User user;

    public static void goActivity(Activity from, long uid) {
        Intent intent = new Intent(from, UserActivity.class);
        intent.putExtra("uid", uid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, String phone) {
        Intent intent = new Intent(from, UserActivity.class);
        intent.putExtra("phone", phone);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_user;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "user", true);
        // id
        long uid = intent.getLongExtra("uid", 0);
        etId.setText(String.valueOf(uid));
        // phone
        String phone = intent.getStringExtra("phone");
        etPhone.setText(phone);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        getUserData();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.btnSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                getUserData();
                break;
        }
    }

    @OnLongClick({R.id.btnStatus, R.id.btnSex, R.id.btnBirthday,})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.btnStatus:
                modifyUser(ApiHelper.MODIFY_ADMIN_UPDATE_STATUS, user);
                return true;
            case R.id.btnSex:
                modifySex();
                return true;
            case R.id.btnBirthday:
                showBirthDayPicker();
                return true;
        }
        return false;
    }

    private void getUserData() {
        long uid = 0;
        if (StringUtils.isNumber(etId.getText().toString().trim())) {
            uid = Long.parseLong(etId.getText().toString().trim());
        }
        String phone = etPhone.getText().toString().trim();
        Call<Result> call = new RetrofitHelper().call(API.class).userGet(uid, phone);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                user = data.getUser();
                refreshView();
                getApiData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void refreshView() {
        if (user == null) {
            ToastUtils.show("user为空");
            return;
        }
        etId.setText(String.valueOf(user.getId()));
        etPhone.setText(user.getPhone());
        btnStatus.setText(user.getStatus() == BaseObj.STATUS_VISIBLE ? "正常ing" : "拉黑ing");
        tvCreate.setText(DateUtils.getString(user.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M));
        tvUpdate.setText(DateUtils.getString(user.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M));
        btnSex.setText(User.getSexShow(user.getSex()));
        btnBirthday.setText(DateUtils.getString(user.getBirthday() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D));
    }

    private void modifySex() {
        if (user == null) {
            ToastUtils.show("user为空");
            return;
        }
        User modify = new User();
        modify.setId(user.getId());
        modify.setStatus(user.getStatus());
        modify.setSex(user.getSex() == User.SEX_GIRL ? User.SEX_BOY : User.SEX_GIRL);
        modify.setBirthday(user.getBirthday());
        modifyUser(ApiHelper.MODIFY_ADMIN_UPDATE_INFO, modify);
    }

    private void showBirthDayPicker() {
        if (user == null) {
            ToastUtils.show("user为空");
            return;
        }
        DialogHelper.showDatePicker(mActivity, user.getBirthday() * 1000, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                User modify = new User();
                modify.setId(user.getId());
                modify.setStatus(user.getStatus());
                modify.setSex(user.getSex());
                modify.setBirthday(time / 1000);
                modifyUser(ApiHelper.MODIFY_ADMIN_UPDATE_INFO, modify);
            }
        });
    }

    private void modifyUser(int type, User modify) {
        if (modify == null) {
            ToastUtils.show("user为空");
            return;
        }
        Call<Result> call = new RetrofitHelper().call(API.class).userModify(type, modify);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                user = data.getUser();
                refreshView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void getApiData() {
        // TODO rv
    }

}
