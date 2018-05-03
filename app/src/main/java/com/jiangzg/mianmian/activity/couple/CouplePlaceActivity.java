package com.jiangzg.mianmian.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.CouplePlaceAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Entry;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Place;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class CouplePlaceActivity extends BaseActivity<CouplePlaceActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private Entry topEntry;
    private RecyclerHelper recyclerHelper;
    private Call<Result> call;
    private int page;

    public static void goActivity(Activity from, Place taPlace) {
        Intent intent = new Intent(from, CouplePlaceActivity.class);
        intent.putExtra("taPlace", taPlace);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
        return R.layout.activity_couple_place;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.ta_entry_info), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new CouplePlaceAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_white, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
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
                        CouplePlaceAdapter placeAdapter = (CouplePlaceAdapter) adapter;
                        placeAdapter.goDiaryDetail(position);
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        Place taPlace = getIntent().getParcelableExtra("taPlace");
        if (taPlace != null && !StringUtils.isEmpty(taPlace.getAddress())) {
            Entry.EntryPlace entryPlace = new Entry.EntryPlace();
            entryPlace.setAddress(taPlace.getAddress());
            entryPlace.setLongitude(taPlace.getLongitude());
            entryPlace.setLatitude(taPlace.getLatitude());
            topEntry = new Entry();
            topEntry.setEntryPlace(entryPlace);
        }
        addTopEntry();
        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(call);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.TYPE_ENTRY_LIST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addTopEntry() {
        if (topEntry == null) return;
        CouplePlaceAdapter adapter = recyclerHelper.getAdapter();
        adapter.addData(0, topEntry);
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        call = new RetrofitHelper().call(API.class).entryListGet(page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                recyclerHelper.viewEmptyShow(data.getShow());
                long total = data.getTotal();
                List<Entry> entryList = data.getEntryList();
                recyclerHelper.dataOk(entryList, total, more);
                addTopEntry();
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(more, errMsg);
            }
        });
    }

}
