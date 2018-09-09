package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
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
import com.jiangzg.lovenote_admin.adapter.ApiAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Api;
import com.jiangzg.lovenote_admin.domain.FiledInfo;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class ApiActivity extends BaseActivity<ApiActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnCreate)
    Button btnCreate;
    @BindView(R.id.btnUri)
    Button btnUri;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnEnd)
    Button btnEnd;
    @BindView(R.id.btnCount)
    Button btnCount;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;
    private long create, start, end;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), ApiActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long uid) {
        Intent intent = new Intent(from, ApiActivity.class);
        intent.putExtra("uid", uid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_api;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "api", true);
        // time
        create = start = DateUtils.getCurrentLong() - ConstantUtils.HOUR;
        end = DateUtils.getCurrentLong();
        refreshDateView();
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
                        getListData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        ApiAdapter apiAdapter = (ApiAdapter) adapter;
                        apiAdapter.goUser(position);
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

    @OnClick({R.id.btnCreate, R.id.btnUri, R.id.btnSearch, R.id.btnStart, R.id.btnEnd, R.id.btnCount})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCreate:
                showCreatePicker();
                break;
            case R.id.btnUri:
                getUriData();
                break;
            case R.id.btnSearch:
                getListData(false);
                break;
            case R.id.btnStart:
                showStartPicker();
                break;
            case R.id.btnEnd:
                showEndPicker();
                break;
            case R.id.btnCount:
                getTotalData();
                break;
        }
    }

    private void refreshDateView() {
        String createAt = DateUtils.getString(create, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String startAt = DateUtils.getString(start, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String endAt = DateUtils.getString(end, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        btnCreate.setText("c: " + createAt);
        btnStart.setText("s: " + startAt);
        btnEnd.setText("e: " + endAt);
    }

    private void showCreatePicker() {
        DialogHelper.showDateTimePicker(mActivity, create, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                create = time;
                refreshDateView();
            }
        });
    }

    private void getUriData() {
        Call<Result> call = new RetrofitHelper().call(API.class).apiUriGet(create / 1000);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                List<FiledInfo> infoList = data.getInfoList();
                showUriResultDialog(infoList);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void showUriResultDialog(List<FiledInfo> infoList) {
        StringBuilder builder = new StringBuilder();
        if (infoList == null || infoList.size() <= 0) {
            builder.append("没有信息");
        } else {
            for (FiledInfo info : infoList) {
                if (info == null) continue;
                builder.append(info.getCount())
                        .append(" == ")
                        .append(info.getName())
                        .append("\n");
            }
        }
        String show = builder.toString();
        DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(show)
                .show();
    }

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).apiListGet(create / 1000, page);
        MaterialDialog loading = null;
        if (!more) {
            loading = getLoading(true);
        }
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
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

    private void showStartPicker() {
        DialogHelper.showDateTimePicker(mActivity, start, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                start = time;
                refreshDateView();
            }
        });
    }

    private void showEndPicker() {
        DialogHelper.showDateTimePicker(mActivity, end, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                end = time;
                refreshDateView();
            }
        });
    }

    private void getTotalData() {
        Call<Result> call = new RetrofitHelper().call(API.class).apiTotalGet(start / 1000, end / 1000);
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

}
