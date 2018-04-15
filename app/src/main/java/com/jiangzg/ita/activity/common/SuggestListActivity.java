package com.jiangzg.ita.activity.common;

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
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.SuggestListAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.BaseObj;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.RecyclerHelper;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.helper.RxBus;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class SuggestListActivity extends BaseActivity<SuggestListActivity> {

    public static final int ENTRY_MINE = 0;
    public static final int ENTRY_FOLLOW = 1;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private int entry;
    private RecyclerHelper recyclerHelper;
    private Observable<List<Suggest>> observableListRefresh;
    private Observable<Suggest> observableListItemDelete;
    private Observable<Suggest> observableListItemRefresh;
    private int page = 0;

    public static void goActivity(Activity from, int entry) {
        Intent intent = new Intent(from, SuggestListActivity.class);
        intent.putExtra("entry", entry);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_list;
    }

    @Override
    protected void initView(Bundle state) {
        entry = getIntent().getIntExtra("entry", ENTRY_MINE);
        String title;
        if (entry == ENTRY_FOLLOW) {
            title = getString(R.string.my_follow);
        } else {
            title = getString(R.string.my_push);
        }
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new SuggestListAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_common, true, true)
                .viewLoadMore(new RecyclerHelper.RecyclerMoreView())
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
                        SuggestListAdapter suggestListAdapter = (SuggestListAdapter) adapter;
                        suggestListAdapter.goSuggestDetail(position);
                    }
                });
        // menu
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 评论
                        int helpType;
                        if (entry == ENTRY_FOLLOW) {
                            helpType = Help.TYPE_SUGGEST_FOLLOW;
                        } else {
                            helpType = Help.TYPE_SUGGEST_MINE;
                        }
                        HelpActivity.goActivity(mActivity, helpType);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void initData(Bundle state) {
        observableListRefresh = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, new Action1<List<Suggest>>() {
            @Override
            public void call(List<Suggest> suggests) {
                recyclerHelper.dataRefresh();
            }
        });
        observableListItemDelete = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_ITEM_DELETE, new Action1<Suggest>() {
            @Override
            public void call(Suggest suggest) {
                SuggestListAdapter adapter = recyclerHelper.getAdapter();
                if (adapter == null) return;
                List<Suggest> data = adapter.getData();
                int index = SuggestListActivity.getIndexInSuggestList(data, suggest);
                if (index < 0) return;
                adapter.remove(index);
            }
        });
        observableListItemRefresh = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_ITEM_REFRESH, new Action1<Suggest>() {
            @Override
            public void call(Suggest suggest) {
                SuggestListAdapter adapter = recyclerHelper.getAdapter();
                if (adapter == null) return;
                List<Suggest> data = adapter.getData();
                int index = SuggestListActivity.getIndexInSuggestList(data, suggest);
                if (index < 0) return;
                adapter.setData(index, suggest);
            }
        });
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
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, observableListRefresh);
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, observableListItemDelete);
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, observableListItemRefresh);
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> call;
        if (entry == ENTRY_MINE) {
            call = new RetrofitHelper().call(API.class).suggestListMineGet(page);
        } else if (entry == ENTRY_FOLLOW) {
            call = new RetrofitHelper().call(API.class).suggestListFollowGet(page);
        } else {
            call = new RetrofitHelper().call(API.class).suggestListHomeGet(BaseObj.STATUS_NOL, BaseObj.STATUS_NOL, page);
        }
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                long total = data.getTotal();
                List<Suggest> suggestList = data.getSuggestList();
                recyclerHelper.data(suggestList, total, more);
                recyclerHelper.viewEmptyShow(data.getShow());
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
                recyclerHelper.viewEmptyShow(errMsg);
            }
        });
    }

    public static int getIndexInSuggestList(List<Suggest> suggestList, Suggest suggest) {
        if (suggestList == null || suggestList.size() <= 0) return -1;
        if (suggest == null || suggest.getId() <= 0) return -1;
        for (int i = 0; i < suggestList.size(); i++) {
            Suggest item = suggestList.get(i);
            if (item.getId() == suggest.getId()) {
                return i;
            }
        }
        return -1;
    }

}
