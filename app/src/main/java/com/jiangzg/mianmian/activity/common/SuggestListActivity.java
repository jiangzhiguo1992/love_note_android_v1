package com.jiangzg.mianmian.activity.common;

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
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.adapter.SuggestAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.BaseObj;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.Suggest;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

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
    private Observable<List<Suggest>> obListRefresh;
    private Observable<Suggest> obListItemDelete;
    private Observable<Suggest> obListItemRefresh;
    private int page;

    public static void goActivity(Activity from, int entry) {
        Intent intent = new Intent(from, SuggestListActivity.class);
        intent.putExtra("entry", entry);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
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
                .initAdapter(new SuggestAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_grey, true, true)
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
                        SuggestAdapter suggestAdapter = (SuggestAdapter) adapter;
                        suggestAdapter.goSuggestDetail(position);
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        obListRefresh = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, new Action1<List<Suggest>>() {
            @Override
            public void call(List<Suggest> suggests) {
                recyclerHelper.dataRefresh();
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_ITEM_DELETE, new Action1<Suggest>() {
            @Override
            public void call(Suggest suggest) {
                ListHelper.removeIndexInAdapter(recyclerHelper.getAdapter(), suggest);
            }
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_ITEM_REFRESH, new Action1<Suggest>() {
            @Override
            public void call(Suggest suggest) {
                ListHelper.refreshIndexInAdapter(recyclerHelper.getAdapter(), suggest);
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
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_ITEM_REFRESH, obListItemRefresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                int helpType;
                if (entry == ENTRY_FOLLOW) {
                    helpType = Help.TYPE_SUGGEST_FOLLOW;
                } else {
                    helpType = Help.TYPE_SUGGEST_MINE;
                }
                HelpActivity.goActivity(mActivity, helpType);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                recyclerHelper.viewEmptyShow(data.getShow());
                long total = data.getTotal();
                List<Suggest> suggestList = data.getSuggestList();
                recyclerHelper.dataOk(suggestList, total, more);
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(more, errMsg);
            }
        });
    }

}
