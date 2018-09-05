package com.jiangzg.lovenote_admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.UserAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
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

public class UserListActivity extends BaseActivity<UserListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnCreate)
    Button btnCreate;
    @BindView(R.id.btnSex)
    Button btnSex;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnEnd)
    Button btnEnd;
    @BindView(R.id.btnCount)
    Button btnCount;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;
    private int sexIndex;
    private long create, start, end;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), UserListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_user_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "user_list", true);
        // sex
        sexIndex = 0;
        btnSex.setText(ApiHelper.LIST_USER_SEX_SHOW[sexIndex]);
        // time
        create = DateUtils.getCurrentLong() - ConstantUtils.DAY;
        start = end = 0;
        refreshDateView();
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new UserAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getListData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        UserAdapter userAdapter = (UserAdapter) adapter;
                        userAdapter.goUser(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.btnCreate, R.id.btnSex, R.id.btnStart, R.id.btnEnd, R.id.btnCount, R.id.btnSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCreate:
                showCreatePicker();
                break;
            case R.id.btnSex:
                showSexSelectDialog();
                break;
            case R.id.btnStart:
                showStartPicker();
                break;
            case R.id.btnEnd:
                showEndPicker();
                break;
            case R.id.btnCount:
                getCountData();
                break;
            case R.id.btnSearch:
                getListData(false);
                break;
        }
    }

    @OnLongClick({R.id.btnStart, R.id.btnEnd,})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.btnStart:
                start = 0;
                refreshDateView();
                return true;
            case R.id.btnEnd:
                end = 0;
                refreshDateView();
                return true;
        }
        return false;
    }

    private void showSexSelectDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.LIST_USER_SEX_SHOW)
                .itemsCallbackSingleChoice(sexIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (recyclerHelper == null) return true;
                        sexIndex = which;
                        btnSex.setText(ApiHelper.LIST_USER_SEX_SHOW[sexIndex]);
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void showCreatePicker() {
        DialogHelper.showDatePicker(mActivity, create, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                create = time;
                refreshDateView();
            }
        });
    }

    private void showStartPicker() {
        long time = (start == 0) ? DateUtils.getCurrentLong() : start;
        DialogHelper.showDatePicker(mActivity, time, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                start = time;
                refreshDateView();
            }
        });
    }

    private void showEndPicker() {
        long time = (end == 0) ? DateUtils.getCurrentLong() : end;
        DialogHelper.showDatePicker(mActivity, time, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                end = time;
                refreshDateView();
            }
        });
    }

    private void refreshDateView() {
        String createAt = DateUtils.getString(create, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String startAt = start == 0 ? "未知" : DateUtils.getString(start, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String endAt = end == 0 ? "未知" : DateUtils.getString(end, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        btnCreate.setText("c: " + createAt);
        btnStart.setText("s: " + startAt);
        btnEnd.setText("e: " + endAt);
    }

    private void getCountData() {
        long createAt = create / 1000;
        long startAt = start / 1000;
        long endAt = end / 1000;
        int sex = ApiHelper.LIST_USER_SEX_TYPE[sexIndex];
        Call<Result> call = new RetrofitHelper().call(API.class).userCountGet(createAt, sex, startAt, endAt);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                btnCount.setText("数量(" + data.getTotal() + ")");
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                btnCount.setText("数量(fail)");
            }
        });
    }

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        long createAt = create / 1000;
        long startAt = start / 1000;
        long endAt = end / 1000;
        int sex = ApiHelper.LIST_USER_SEX_TYPE[sexIndex];
        Call<Result> call = new RetrofitHelper().call(API.class).userListGet(createAt, sex, startAt, endAt, page);
        MaterialDialog loading = null;
        if (!more) {
            loading = getLoading(true);
        }
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<User> userList = data.getUserList();
                recyclerHelper.dataOk(userList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
