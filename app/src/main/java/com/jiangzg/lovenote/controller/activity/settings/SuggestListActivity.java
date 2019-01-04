package com.jiangzg.lovenote.controller.activity.settings;

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
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.settings.SuggestAdapter;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.engine.SuggestInfo;
import com.jiangzg.lovenote.model.entity.Suggest;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;

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
    private Call<Result> call;
    private int page;

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
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new SuggestAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        SuggestAdapter suggestAdapter = (SuggestAdapter) adapter;
                        suggestAdapter.goSuggestDetail(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // tb
        entry = intent.getIntExtra("entry", ENTRY_MINE);
        if (entry == ENTRY_FOLLOW) {
            ViewHelper.initTopBar(mActivity, tb, getString(R.string.my_follow), true);
        } else if (entry == ENTRY_MINE) {
            ViewHelper.initTopBar(mActivity, tb, getString(R.string.my_push), true);
        }
        // event
        obListRefresh = RxBus.register(RxBus.EVENT_SUGGEST_LIST_REFRESH, suggests -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        obListItemDelete = RxBus.register(RxBus.EVENT_SUGGEST_LIST_ITEM_DELETE, suggest -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), suggest);
        });
        obListItemRefresh = RxBus.register(RxBus.EVENT_SUGGEST_LIST_ITEM_REFRESH, suggest -> {
            if (recyclerHelper == null) return;
            ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), suggest);
        });
        // api
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RxBus.unregister(RxBus.EVENT_SUGGEST_LIST_REFRESH, obListRefresh);
        RxBus.unregister(RxBus.EVENT_SUGGEST_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(RxBus.EVENT_SUGGEST_LIST_ITEM_REFRESH, obListItemRefresh);
        RecyclerHelper.release(recyclerHelper);
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        if (entry == ENTRY_MINE) {
            call = new RetrofitHelper().call(API.class).setSuggestListMineGet(page);
        } else if (entry == ENTRY_FOLLOW) {
            call = new RetrofitHelper().call(API.class).setSuggestListFollowGet(page);
        } else {
            SuggestInfo suggestInfo = SuggestInfo.getInstance();
            int status = suggestInfo.getStatusList().get(0).getStatus();
            int kind = suggestInfo.getKindList().get(0).getKind();
            call = new RetrofitHelper().call(API.class).setSuggestListGet(status, kind, page);
        }
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Suggest> suggestList = data.getSuggestList();
                recyclerHelper.dataOk(suggestList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
