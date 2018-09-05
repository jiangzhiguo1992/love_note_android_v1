package com.jiangzg.lovenote_admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.SmsAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Sms;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ApiHelper;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class SmsActivity extends BaseActivity<SmsActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etPhone)
    EditText etPhone;
    @BindView(R.id.tvType)
    TextView tvType;
    @BindView(R.id.cvType)
    CardView cvType;
    @BindView(R.id.tvStart)
    TextView tvStart;
    @BindView(R.id.cvStart)
    CardView cvStart;
    @BindView(R.id.tvEnd)
    TextView tvEnd;
    @BindView(R.id.cvEnd)
    CardView cvEnd;
    @BindView(R.id.btnCount)
    Button btnCount;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;
    private int searchIndex;
    private long start, end;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), SmsActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_sms;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "sms", true);
        // phone
        String phone = intent.getStringExtra("phone");
        etPhone.setText(phone);
        // search
        searchIndex = 0;
        tvType.setText(ApiHelper.LIST_SMS_SHOW[searchIndex]);
        // time
        start = DateUtils.getCurrentLong() - ConstantUtils.DAY;
        end = DateUtils.getCurrentLong();
        refreshDateView();
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new SmsAdapter(mActivity))
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
                        SmsAdapter smsAdapter = (SmsAdapter) adapter;
                        smsAdapter.goUser(position);
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

    @OnClick({R.id.cvType, R.id.cvStart, R.id.cvEnd, R.id.btnCount, R.id.btnSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvType:
                showTypeSelectDialog();
                break;
            case R.id.cvStart:
                showStartPicker();
                break;
            case R.id.cvEnd:
                showEndPicker();
                break;
            case R.id.btnCount:
                getCount();
                break;
            case R.id.btnSearch:
                getListData(false);
                break;
        }
    }

    private void showTypeSelectDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.LIST_SMS_SHOW)
                .itemsCallbackSingleChoice(searchIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (recyclerHelper == null) return true;
                        searchIndex = which;
                        tvType.setText(ApiHelper.LIST_SMS_SHOW[searchIndex]);
                        recyclerHelper.dataRefresh();
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void showStartPicker() {
        DialogHelper.showDatePicker(mActivity, start, new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                start = time;
                refreshDateView();
            }
        });
    }

    private void showEndPicker() {
        DialogHelper.showDatePicker(mActivity, end, new DialogHelper.OnPickListener() {
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
        tvStart.setText("s: " + startAt);
        tvEnd.setText("e: " + endAt);
    }

    private void getCount() {
        long startAt = start / 1000;
        long endAt = end / 1000;
        String phone = etPhone.getText().toString();
        int sendType = ApiHelper.LIST_SMS_TYPE[searchIndex];
        Call<Result> call = new RetrofitHelper().call(API.class).smsCountGet(startAt, endAt, phone, sendType);
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
        long startAt = start / 1000;
        long endAt = end / 1000;
        String phone = etPhone.getText().toString();
        int sendType = ApiHelper.LIST_SMS_TYPE[searchIndex];
        Call<Result> call = new RetrofitHelper().call(API.class).smsListGet(startAt, endAt, phone, sendType, page);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Sms> smsList = data.getSmsList();
                recyclerHelper.dataOk(smsList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
