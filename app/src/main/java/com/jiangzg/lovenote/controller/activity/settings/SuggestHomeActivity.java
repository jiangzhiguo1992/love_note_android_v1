package com.jiangzg.lovenote.controller.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.settings.SuggestAdapter;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Suggest;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;

public class SuggestHomeActivity extends BaseActivity<SuggestHomeActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page = 0;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SuggestHomeActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_home;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new SuggestAdapter(mActivity))
                .viewHeader(mActivity, R.layout.list_head_suggest_home)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .viewAnim()
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
        // head
        initHead();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<List<Suggest>> obListRefresh = RxBus.register(RxBus.EVENT_SUGGEST_LIST_REFRESH, suggests -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_SUGGEST_LIST_REFRESH, obListRefresh);
        Observable<Suggest> obListItemDelete = RxBus.register(RxBus.EVENT_SUGGEST_LIST_ITEM_DELETE, suggest -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), suggest);
        });
        pushBus(RxBus.EVENT_SUGGEST_LIST_ITEM_DELETE, obListItemDelete);
        Observable<Suggest> obListItemRefresh = RxBus.register(RxBus.EVENT_SUGGEST_LIST_ITEM_REFRESH, suggest -> {
            if (recyclerHelper == null) return;
            ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), suggest);
        });
        pushBus(RxBus.EVENT_SUGGEST_LIST_ITEM_REFRESH, obListItemRefresh);
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_USER_SUGGEST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // head
    private void initHead() {
        if (recyclerHelper == null) return;
        View head = recyclerHelper.getViewHead();
        if (head == null) return;
        TextView tvMy = head.findViewById(R.id.tvMy);
        TextView tvFollow = head.findViewById(R.id.tvFollow);
        TextView tvAdd = head.findViewById(R.id.tvAdd);
        tvMy.setOnClickListener(v -> SuggestListActivity.goActivity(mActivity, SuggestListActivity.ENTRY_MINE));
        tvFollow.setOnClickListener(v -> SuggestListActivity.goActivity(mActivity, SuggestListActivity.ENTRY_FOLLOW));
        tvAdd.setOnClickListener(v -> SuggestAddActivity.goActivity(mActivity));
    }

    public void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).setSuggestListGet(Suggest.STATUS_REPLY_NO, Suggest.KIND_ALL, page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataOk(data.getShow(), data.getSuggestList(), more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
        pushApi(api);
    }

}
