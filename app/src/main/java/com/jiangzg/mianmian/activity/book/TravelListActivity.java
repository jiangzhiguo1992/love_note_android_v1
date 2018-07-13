package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
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
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.TravelAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.Travel;
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
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class TravelListActivity extends BaseActivity<TravelListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    private RecyclerHelper recyclerHelper;
    private Observable<List<Travel>> obListRefresh;
    private Observable<Travel> obListItemDelete;
    private Observable<Travel> obListItemRefresh;
    private Call<Result> call;
    private int page;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, TravelListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_LIST_FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), TravelListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_LIST_FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelect(Activity from) {
        Intent intent = new Intent(from, TravelListActivity.class);
        intent.putExtra("from", ConsHelper.ACT_LIST_FROM_SELECT);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_travel_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        String title;
        if (isFromSelect()) {
            title = getString(R.string.please_select_travel);
        } else {
            title = getString(R.string.travel);
        }
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new TravelAdapter(mActivity))
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
                        TravelAdapter travelAdapter = (TravelAdapter) adapter;
                        if (isFromSelect()) {
                            // 规则选择
                            travelAdapter.selectTravel(position);
                        } else {
                            travelAdapter.goTravelDetail(position);
                        }
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // event
        obListRefresh = RxBus.register(ConsHelper.EVENT_TRAVEL_LIST_REFRESH, new Action1<List<Travel>>() {
            @Override
            public void call(List<Travel> travelList) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataRefresh();
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_TRAVEL_LIST_ITEM_DELETE, new Action1<Travel>() {
            @Override
            public void call(Travel travel) {
                if (recyclerHelper == null) return;
                ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), travel);
            }
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_TRAVEL_LIST_ITEM_REFRESH, new Action1<Travel>() {
            @Override
            public void call(Travel travel) {
                if (recyclerHelper == null) return;
                ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), travel);
            }
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
        RxBus.unregister(ConsHelper.EVENT_TRAVEL_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_TRAVEL_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(ConsHelper.EVENT_TRAVEL_LIST_ITEM_REFRESH, obListItemRefresh);
        RetrofitHelper.cancel(call);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isFromSelect()) {
            getMenuInflater().inflate(R.menu.help, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_BOOK_TRAVEL_LIST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd: // 添加
                TravelEditActivity.goActivity(mActivity);
                break;
        }
    }

    private boolean isFromSelect() {
        return getIntent().getIntExtra("from", ConsHelper.ACT_LIST_FROM_BROWSE) == ConsHelper.ACT_LIST_FROM_SELECT;
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        call = new RetrofitHelper().call(API.class).travelListGet(page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Travel> travelList = data.getTravelList();
                recyclerHelper.dataOk(travelList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
