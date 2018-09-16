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
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.UserAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.User;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class UserListActivity extends BaseActivity<UserListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnBlack)
    Button btnBlack;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnEnd)
    Button btnEnd;
    @BindView(R.id.btnBirth)
    Button btnBirth;
    @BindView(R.id.btnTotal)
    Button btnTotal;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;
    private long start, end;
    private boolean black;

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
        // time
        start = DateUtils.getCurrentLong() - ConstantUtils.DAY;
        end = DateUtils.getCurrentLong();
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
                        if (black) {
                            getBlackData(true);
                        } else {
                            getListData(true);
                        }
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
        getListData(false);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.btnBlack, R.id.btnSearch, R.id.btnStart, R.id.btnEnd, R.id.btnBirth, R.id.btnTotal})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnBlack:
                black = true;
                getBlackData(false);
                break;
            case R.id.btnSearch:
                black = false;
                getListData(false);
                break;
            case R.id.btnStart:
                showStartPicker();
                break;
            case R.id.btnEnd:
                showEndPicker();
                break;
            case R.id.btnBirth:
                getBirthData();
                break;
            case R.id.btnTotal:
                getTotalData();
                break;
        }
    }

    private void showStartPicker() {
        long time = (start == 0) ? DateUtils.getCurrentLong() : start;
        DialogHelper.showDateTimePicker(mActivity, time, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                start = time;
                refreshDateView();
            }
        });
    }

    private void showEndPicker() {
        long time = (end == 0) ? DateUtils.getCurrentLong() : end;
        DialogHelper.showDateTimePicker(mActivity, time, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                end = time;
                refreshDateView();
            }
        });
    }

    private void refreshDateView() {
        String startAt = "s:" + DateUtils.getString(start, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String endAt = "e:" + DateUtils.getString(end, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        btnStart.setText(startAt);
        btnEnd.setText(endAt);
    }

    private void getBlackData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).userBlackGet(page);
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

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).userListGet(page);
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

    private void getTotalData() {
        long startAt = start / 1000;
        long endAt = end / 1000;
        Call<Result> call = new RetrofitHelper().call(API.class).userTotalGet(startAt, endAt);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                btnTotal.setText("数量(" + data.getTotal() + ")");
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                btnTotal.setText("数量(fail)");
            }
        });
    }

    private void getBirthData() {
        long startAt = start / 1000;
        long endAt = end / 1000;
        Call<Result> call = new RetrofitHelper().call(API.class).userBirthGet(startAt, endAt);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                long birth = DateUtils.getCurrentLong() - (long) data.getBirth() * 1000;
                long year = birth / ConstantUtils.YEAR;
                long day = (birth % ConstantUtils.YEAR) / ConstantUtils.DAY;
                if (data.getBirth() == 0) {
                    year = 0;
                    day = 0;
                }
                btnBirth.setText("均龄(" + year + "岁" + day + "天)");
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                btnBirth.setText("均龄(fail)");
            }
        });
    }

}
