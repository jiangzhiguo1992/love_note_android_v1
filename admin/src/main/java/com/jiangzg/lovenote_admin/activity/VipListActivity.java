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
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.VipAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.FiledInfo;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Vip;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ApiHelper;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class VipListActivity extends BaseActivity<VipListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etUid)
    EditText etUid;
    @BindView(R.id.etCid)
    EditText etCid;
    @BindView(R.id.etBid)
    EditText etBid;
    @BindView(R.id.btnType)
    Button btnType;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnEnd)
    Button btnEnd;
    @BindView(R.id.btnGroup)
    Button btnGroup;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private long start, end;
    private int page, typeIndex;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), VipListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long uid, long cid) {
        Intent intent = new Intent(from, VipListActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("cid", cid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_vip_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "vip_list", true);
        long uid = intent.getLongExtra("uid", 0);
        long cid = intent.getLongExtra("cid", 0);
        long bid = intent.getLongExtra("bid", 0);
        if (uid > 0) {
            etUid.setText(String.valueOf(uid));
        }
        if (cid > 0) {
            etCid.setText(String.valueOf(cid));
        }
        if (bid > 0) {
            etBid.setText(String.valueOf(bid));
        }
        typeIndex = 0;
        btnType.setText(ApiHelper.VIP_FROM_SHOW[typeIndex]);
        // time
        start = DateUtils.getCurrentLong() - TimeUnit.DAY;
        end = DateUtils.getCurrentLong();
        refreshDateView();
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new VipAdapter(mActivity))
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
                        VipAdapter vipAdapter = (VipAdapter) adapter;
                        vipAdapter.goCouple(position);
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

    @OnClick({R.id.btnType, R.id.btnSearch, R.id.btnStart, R.id.btnEnd, R.id.btnGroup})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnType:
                showTypeSelectDialog();
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
            case R.id.btnGroup:
                getGroupData();
                break;
        }
    }

    private void showTypeSelectDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.VIP_FROM_SHOW)
                .itemsCallbackSingleChoice(typeIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (recyclerHelper == null) return true;
                        typeIndex = which;
                        btnType.setText(ApiHelper.VIP_FROM_SHOW[typeIndex]);
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
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
        String startAt = "s: " + DateUtils.getStr(start, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        String endAt = "e: " + DateUtils.getStr(end, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        btnStart.setText(startAt);
        btnEnd.setText(endAt);
    }

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        long uid = 0, cid = 0, bid = 0;
        String sUid = etUid.getText().toString().trim();
        if (StringUtils.isNumber(sUid)) {
            uid = Long.parseLong(sUid);
        }
        String sCid = etCid.getText().toString().trim();
        if (StringUtils.isNumber(sCid)) {
            cid = Long.parseLong(sCid);
        }
        String sBid = etBid.getText().toString().trim();
        if (StringUtils.isNumber(sBid)) {
            bid = Long.parseLong(sBid);
        }
        int fromType = ApiHelper.VIP_FROM_TYPE[typeIndex];
        Call<Result> call = new RetrofitHelper().call(API.class).moreVipListGet(uid, cid, bid, fromType, page);
        MaterialDialog loading = null;
        if (!more) {
            loading = getLoading(true);
        }
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Vip> vipList = data.getVipList();
                recyclerHelper.dataOk(vipList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

    private void getGroupData() {
        Call<Result> call = new RetrofitHelper().call(API.class).moreVipExpireDaysListGet(start / 1000, end / 1000);
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
