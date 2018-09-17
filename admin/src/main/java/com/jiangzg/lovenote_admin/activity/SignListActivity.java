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
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.SignAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Sign;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class SignListActivity extends BaseActivity<SignListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etUid)
    EditText etUid;
    @BindView(R.id.etCid)
    EditText etCid;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.etYear)
    EditText etYear;
    @BindView(R.id.etMonth)
    EditText etMonth;
    @BindView(R.id.etDay)
    EditText etDay;
    @BindView(R.id.btnTotal)
    Button btnTotal;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), SignListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long cid) {
        Intent intent = new Intent(from, SignListActivity.class);
        intent.putExtra("cid", cid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_sign_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "sign_list", true);
        long uid = intent.getLongExtra("uid", 0);
        long cid = intent.getLongExtra("cid", 0);
        if (uid > 0) {
            etUid.setText(String.valueOf(uid));
        }
        if (cid > 0) {
            etCid.setText(String.valueOf(cid));
        }
        // time
        int year = DateUtils.getCurrentCalendar().get(Calendar.YEAR);
        etYear.setText(String.valueOf(year));
        int month = DateUtils.getCurrentCalendar().get(Calendar.MONTH) + 1;
        etMonth.setText(String.valueOf(month));
        int day = DateUtils.getCurrentCalendar().get(Calendar.DAY_OF_MONTH);
        etDay.setText(String.valueOf(day));
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new SignAdapter(mActivity))
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
                        SignAdapter signAdapter = (SignAdapter) adapter;
                        signAdapter.goCouple(position);
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

    @OnClick({R.id.btnTotal, R.id.btnSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnTotal:
                getTotalData();
                break;
            case R.id.btnSearch:
                getListData(false);
                break;
        }
    }

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        long uid = 0, cid = 0;
        String sUid = etUid.getText().toString().trim();
        if (StringUtils.isNumber(sUid)) {
            uid = Long.parseLong(sUid);
        }
        String sCid = etCid.getText().toString().trim();
        if (StringUtils.isNumber(sCid)) {
            cid = Long.parseLong(sCid);
        }
        Call<Result> call = new RetrofitHelper().call(API.class).moreSignListGet(uid, cid, page);
        MaterialDialog loading = null;
        if (!more) {
            loading = getLoading(true);
        }
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Sign> signList = data.getSignList();
                recyclerHelper.dataOk(signList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

    private void getTotalData() {
        int year = 0;
        String sYear = etYear.getText().toString().trim();
        if (StringUtils.isNumber(sYear)) {
            year = Integer.parseInt(sYear);
        }
        int month = 0;
        String sMonth = etMonth.getText().toString().trim();
        if (StringUtils.isNumber(sMonth)) {
            month = Integer.parseInt(sMonth);
        }
        int day = 0;
        String sDay = etDay.getText().toString().trim();
        if (StringUtils.isNumber(sDay)) {
            day = Integer.parseInt(sDay);
        }
        Call<Result> call = new RetrofitHelper().call(API.class).moreSignTotalGet(year, month, day);
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

}
