package com.jiangzg.lovenote.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.NoticeAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Notice;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class NoticeListActivity extends BaseActivity<NoticeListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private Call<Result> call;
    private int page;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, NoticeListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_notice_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.new_notice), true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new NoticeAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_white, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData(false);
                    }
                })
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        NoticeAdapter noticeAdapter = (NoticeAdapter) adapter;
                        noticeAdapter.goNoticeDetail(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RecyclerHelper.release(recyclerHelper);
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        call = new RetrofitHelper().call(API.class).setNoticeListGet(page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                int noticeNoReadCount = data.getNoticeNoReadCount();
                SPHelper.setNoticeNoReadCount(noticeNoReadCount);
                recyclerHelper.viewEmptyShow(data.getShow());
                long total = data.getTotal();
                List<Notice> noticeList = data.getNoticeList();
                recyclerHelper.dataOk(noticeList, total, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
