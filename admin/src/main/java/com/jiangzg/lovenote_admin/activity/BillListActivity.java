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
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.BillAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Bill;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ApiHelper;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class BillListActivity extends BaseActivity<BillListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etUid)
    EditText etUid;
    @BindView(R.id.etCid)
    EditText etCid;
    @BindView(R.id.etTradeNo)
    EditText etTradeNo;
    @BindView(R.id.btnSearchSync)
    Button btnSearchSync;
    @BindView(R.id.btnSearch)
    Button btnSearch;

    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnEnd)
    Button btnEnd;
    @BindView(R.id.btnPlatformOs)
    Button btnPlatformOs;
    @BindView(R.id.btnPlatformPay)
    Button btnPlatformPay;
    @BindView(R.id.btnGoodsType)
    Button btnGoodsType;
    @BindView(R.id.btnTotal)
    Button btnTotal;
    @BindView(R.id.btnAmount)
    Button btnAmount;

    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private long start, end;
    private int page, platformOsIndex, platformPayIndex, goodsTypeIndex;
    private boolean sync;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), BillListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long uid, long cid) {
        Intent intent = new Intent(from, BillListActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("cid", cid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_bill_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "bill_list", true);
        long uid = intent.getLongExtra("uid", 0);
        long cid = intent.getLongExtra("cid", 0);
        if (uid > 0) {
            etUid.setText(String.valueOf(uid));
        }
        if (cid > 0) {
            etCid.setText(String.valueOf(cid));
        }
        // time
        start = DateUtils.getCurrentLong() - ConstantUtils.DAY;
        end = DateUtils.getCurrentLong();
        refreshDateView();
        // total
        platformOsIndex = platformPayIndex = goodsTypeIndex = 0;
        btnPlatformOs.setText(ApiHelper.BILL_PLATFORM_OS_SHOW[platformOsIndex]);
        btnPlatformPay.setText(ApiHelper.BILL_PLATFORM_PAY_SHOW[platformPayIndex]);
        btnGoodsType.setText(ApiHelper.BILL_GOODS_TYPE_SHOW[goodsTypeIndex]);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new BillAdapter(mActivity))
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
                        BillAdapter billAdapter = (BillAdapter) adapter;
                        billAdapter.goCouple(position);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        BillAdapter billAdapter = (BillAdapter) adapter;
                        billAdapter.checkBill(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        sync = false;
        getListData(false);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.btnSearchSync, R.id.btnSearch, R.id.btnStart, R.id.btnEnd,
            R.id.btnPlatformOs, R.id.btnPlatformPay, R.id.btnGoodsType,
            R.id.btnTotal, R.id.btnAmount})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSearchSync:
                sync = true;
                getListData(false);
                break;
            case R.id.btnSearch:
                sync = false;
                getListData(false);
                break;
            case R.id.btnStart:
                showStartPicker();
                break;
            case R.id.btnEnd:
                showEndPicker();
                break;
            case R.id.btnPlatformOs:
                showPlatformOsDialog();
                break;
            case R.id.btnPlatformPay:
                showPlatformPayDialog();
                break;
            case R.id.btnGoodsType:
                showGoodsTypeDialog();
                break;
            case R.id.btnAmount:
                getAmountData();
                break;
            case R.id.btnTotal:
                getTotalData();
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

    private void showPlatformOsDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.BILL_PLATFORM_OS_SHOW)
                .itemsCallbackSingleChoice(platformOsIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (recyclerHelper == null) return true;
                        platformOsIndex = which;
                        btnPlatformOs.setText(ApiHelper.BILL_PLATFORM_OS_SHOW[platformOsIndex]);
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void showPlatformPayDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.BILL_PLATFORM_PAY_SHOW)
                .itemsCallbackSingleChoice(platformPayIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (recyclerHelper == null) return true;
                        platformPayIndex = which;
                        btnPlatformPay.setText(ApiHelper.BILL_PLATFORM_PAY_SHOW[platformPayIndex]);
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void showGoodsTypeDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.BILL_GOODS_TYPE_SHOW)
                .itemsCallbackSingleChoice(goodsTypeIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (recyclerHelper == null) return true;
                        goodsTypeIndex = which;
                        btnGoodsType.setText(ApiHelper.BILL_GOODS_TYPE_SHOW[goodsTypeIndex]);
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void refreshDateView() {
        String startAt = "s: " + DateUtils.getStr(start, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String endAt = "e: " + DateUtils.getStr(end, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        btnStart.setText(startAt);
        btnEnd.setText(endAt);
    }

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> call;
        if (sync) {
            call = new RetrofitHelper().call(API.class).moreBillSyncListGet(page);
        } else {
            long uid = 0, cid = 0;
            String sUid = etUid.getText().toString().trim();
            if (StringUtils.isNumber(sUid)) {
                uid = Long.parseLong(sUid);
            }
            String sCid = etCid.getText().toString().trim();
            if (StringUtils.isNumber(sCid)) {
                cid = Long.parseLong(sCid);
            }
            String tradeNo = etTradeNo.getText().toString().trim();
            call = new RetrofitHelper().call(API.class).moreBillListGet(uid, cid, tradeNo, page);
        }
        MaterialDialog loading = null;
        if (!more) {
            loading = getLoading(true);
        }
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Bill> billList = data.getBillList();
                recyclerHelper.dataOk(billList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

    private void getAmountData() {
        String platformOs = platformOsIndex <= 0 ? "" : ApiHelper.BILL_PLATFORM_OS_SHOW[platformOsIndex];
        int platformPay = ApiHelper.BILL_PLATFORM_PAY_TYPE[platformPayIndex];
        int payType = Bill.BILL_PAY_TYPE_APP;
        int goodsType = ApiHelper.BILL_GOODS_TYPE_TYPE[goodsTypeIndex];
        Call<Result> call = new RetrofitHelper().call(API.class).moreBillAmountGet(start / 1000, end / 1000, platformOs, platformPay, payType, goodsType);
        RetrofitHelper.enqueue(call, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                String amount = String.format(Locale.getDefault(), "%.1f", data.getAmount());
                btnAmount.setText("金额(" + amount + ")");
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                btnAmount.setText("金额(fail)");
            }
        });
    }

    private void getTotalData() {
        String platformOs = platformOsIndex <= 0 ? "" : ApiHelper.BILL_PLATFORM_OS_SHOW[platformOsIndex];
        int platformPay = ApiHelper.BILL_PLATFORM_PAY_TYPE[platformPayIndex];
        int payType = Bill.BILL_PAY_TYPE_APP;
        int goodsType = ApiHelper.BILL_GOODS_TYPE_TYPE[goodsTypeIndex];
        Call<Result> call = new RetrofitHelper().call(API.class).moreBillTotalGet(start / 1000, end / 1000, platformOs, platformPay, payType, goodsType);
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
