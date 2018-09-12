package com.jiangzg.lovenote_admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.CoupleAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Couple;
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

public class CoupleListActivity extends BaseActivity<CoupleListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etUid)
    EditText etUid;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnEnd)
    Button btnEnd;
    @BindView(R.id.btnStateGroup)
    Button btnStateGroup;
    @BindView(R.id.btnCount)
    Button btnCount;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;
    private long start, end;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), CoupleListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_couple_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "couple_list", true);
        // phone
        long uid = intent.getLongExtra("uid", 0);
        etUid.setText(String.valueOf(uid));
        // time
        start = DateUtils.getCurrentLong() - ConstantUtils.DAY;
        end = DateUtils.getCurrentLong();
        refreshDateView();
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new CoupleAdapter(mActivity))
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
                        CoupleAdapter coupleAdapter = (CoupleAdapter) adapter;
                        coupleAdapter.goCouple(position);
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

    @OnClick({R.id.btnSearch, R.id.btnStart, R.id.btnEnd, R.id.btnStateGroup, R.id.btnCount})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                getListData(false);
                break;
            case R.id.btnStart:
                showStartPicker();
                break;
            case R.id.btnEnd:
                showEndPicker();
                break;
            case R.id.btnStateGroup:
                getStateGroup();
                break;
            case R.id.btnCount:
                getCount();
                break;
        }
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

    private void refreshDateView() {
        String startAt = DateUtils.getString(start, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String endAt = DateUtils.getString(end, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        btnStart.setText("s: " + startAt);
        btnEnd.setText("e: " + endAt);
    }

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        long uid = 0;
        if (StringUtils.isNumber(etUid.getText().toString().trim())) {
            uid = Long.parseLong(etUid.getText().toString().trim());
        }
        Call<Result> call = new RetrofitHelper().call(API.class).coupleListGet(uid, page);
        MaterialDialog loading = null;
        if (!more) {
            loading = getLoading(true);
        }
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Couple> coupleList = data.getCoupleList();
                recyclerHelper.dataOk(coupleList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

    private void getCount() {
        long startAt = start / 1000;
        long endAt = end / 1000;
        Call<Result> call = new RetrofitHelper().call(API.class).coupleTotalGet(startAt, endAt);
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

    private void getStateGroup() {
        long startAt = start / 1000;
        long endAt = end / 1000;
        Call<Result> call = new RetrofitHelper().call(API.class).coupleStateTotalGet(startAt, endAt);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                List<FiledInfo> infoList = data.getInfoList();
                DialogHelper.showFiledInfoDialog(mActivity, infoList);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
