package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.ApiAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Api;
import com.jiangzg.lovenote_admin.domain.BaseObj;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.User;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ApiHelper;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import retrofit2.Call;

public class UserDetailActivity extends BaseActivity<UserDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etId)
    EditText etId;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.btnSearch)
    Button btnSearch;

    @BindView(R.id.btnSms)
    Button btnSms;
    @BindView(R.id.btnEntry)
    Button btnEntry;
    @BindView(R.id.btnPlace)
    Button btnPlace;
    @BindView(R.id.btnSuggest)
    Button btnSuggest;
    @BindView(R.id.btnSuggestComment)
    Button btnSuggestComment;
    @BindView(R.id.btnCouple)
    Button btnCouple;

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
    private RecyclerHelper recyclerHelper;
    private int page;

    public static void goActivity(Activity from, long uid) {
        Intent intent = new Intent(from, UserDetailActivity.class);
        intent.putExtra("uid", uid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, String phone) {
        Intent intent = new Intent(from, UserDetailActivity.class);
        intent.putExtra("phone", phone);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_user_detail;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "user_detail", true);
        // id
        long uid = intent.getLongExtra("uid", 0);
        etId.setText(String.valueOf(uid));
        // phone
        String phone = intent.getStringExtra("phone");
        etPhone.setText(phone);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new ApiAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getApiData(true);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        getUserData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.btnSearch, R.id.btnSms, R.id.btnEntry, R.id.btnPlace,
            R.id.btnSuggest, R.id.btnSuggestComment, R.id.btnCouple})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                getUserData();
                break;
            case R.id.btnSms:
                if (user == null) return;
                SmsListActivity.goActivity(mActivity, user.getPhone());
                break;
            case R.id.btnPlace:
                if (user == null) return;
                PlaceListActivity.goActivity(mActivity, user.getId());
                break;
            case R.id.btnEntry:
                if (user == null) return;
                EntryListActivity.goActivity(mActivity, user.getId());
                break;
            case R.id.btnSuggest:
                if (user == null) return;
                SuggestListActivity.goActivity(mActivity, user.getId());
                break;
            case R.id.btnSuggestComment:
                if (user == null) return;
                SuggestCommentListActivity.goActivity(mActivity, user.getId(), 0);
                break;
            case R.id.btnCouple:
                if (user == null) return;
                CoupleDetailActivity.goActivity(mActivity, 0, user.getId());
                break;
        }
    }

    @OnLongClick({R.id.btnStatus, R.id.btnSex, R.id.btnBirthday})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.btnStatus:
                showStatusDialog();
                return true;
            case R.id.btnSex:
                showSexDialog();
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
                getApiData(false);
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
        tvCreate.setText("c:" + DateUtils.getStr(user.getCreateAt() * 1000, DateUtils.FORMAT_LINE_Y_M_D_H_M));
        tvUpdate.setText("u:" + DateUtils.getStr(user.getUpdateAt() * 1000, DateUtils.FORMAT_LINE_Y_M_D_H_M));
        btnSex.setText("性别:" + User.getSexShow(user.getSex()));
        btnBirthday.setText("生日:" + DateUtils.getStr(user.getBirthday() * 1000, DateUtils.FORMAT_LINE_Y_M_D));
    }

    private void showStatusDialog() {
        DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title("修改状态？")
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        modifyUser(ApiHelper.MODIFY_ADMIN_UPDATE_STATUS, user);
                    }
                })
                .show();
    }

    private void showSexDialog() {
        DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title("修改性别？")
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        modifySex();
                    }
                })
                .show();
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

    private void getApiData(final boolean more) {
        if (user == null) {
            ToastUtils.show("user为空");
            return;
        }
        page = more ? page + 1 : 0;
        Call<Result> call = new RetrofitHelper().call(API.class).apiListGet(0, 0, user.getId(), page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Api> apiList = data.getApiList();
                recyclerHelper.dataOk(apiList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
