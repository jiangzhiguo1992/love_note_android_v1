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
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.MatchWorkAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.MatchWork;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class MatchWorkListActivity extends BaseActivity<MatchWorkListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnReport)
    Button btnReport;
    @BindView(R.id.etUid)
    EditText etUid;
    @BindView(R.id.etMpid)
    EditText etMpid;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;
    private boolean report;

    public static void goActivity(Activity from, long uid, long mpid) {
        Intent intent = new Intent(from, MatchWorkListActivity.class);
        intent.putExtra("uid", uid);
        intent.putExtra("mpid", mpid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_match_work_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "match_work_list", true);
        // init
        long uid = intent.getLongExtra("uid", 0);
        long mpid = intent.getLongExtra("mpid", 0);
        if (uid > 0) {
            etUid.setText(String.valueOf(uid));
        }
        if (mpid > 0) {
            etMpid.setText(String.valueOf(mpid));
        }
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new MatchWorkAdapter(mActivity))
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
                        MatchWorkAdapter matchWorkAdapter = (MatchWorkAdapter) adapter;
                        matchWorkAdapter.goUserDetail(position);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        MatchWorkAdapter matchWorkAdapter = (MatchWorkAdapter) adapter;
                        matchWorkAdapter.showDeleteDialog(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        report = false;
        getListData(false);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.btnReport, R.id.btnSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnReport:
                report = true;
                getListData(false);
                break;
            case R.id.btnSearch:
                report = false;
                getListData(false);
                break;
        }
    }

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> call;
        if (report) {
            call = new RetrofitHelper().call(API.class).moreMatchWorkReportListGet(page);
        } else {
            long uid = 0, mpid = 0;
            String sUid = etUid.getText().toString().trim();
            if (StringUtils.isNumber(sUid)) {
                uid = Long.parseLong(sUid);
            }
            String sMpid = etMpid.getText().toString().trim();
            if (StringUtils.isNumber(sMpid)) {
                mpid = Long.parseLong(sMpid);
            }
            call = new RetrofitHelper().call(API.class).moreMatchWorkListGet(uid, mpid, page);
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
                List<MatchWork> matchWorkList = data.getMatchWorkList();
                recyclerHelper.dataOk(matchWorkList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
