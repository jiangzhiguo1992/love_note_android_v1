package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.PromiseAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Promise;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
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

public class PromiseListActivity extends BaseActivity<PromiseListActivity> {

    private static final int FROM_BROWSE = 0;
    private static final int FROM_SELECT = 1;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tvSearch)
    TextView tvSearch;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private RecyclerHelper recyclerHelper;
    private Call<Result> call;
    private int page;
    private int searchType = ApiHelper.LIST_CP;
    private Observable<List<Promise>> obListRefresh;
    private Observable<Promise> obListItemRefresh;
    private Observable<Promise> obListItemDelete;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, PromiseListActivity.class);
        intent.putExtra("from", FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelect(Activity from) {
        Intent intent = new Intent(from, PromiseListActivity.class);
        intent.putExtra("from", FROM_SELECT);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
        return R.layout.activity_promise_list;
    }

    @Override
    protected void initView(Bundle state) {
        String title;
        if (isSelect()) {
            title = getString(R.string.please_select_promise);
        } else {
            title = getString(R.string.promise);
        }
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new PromiseAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_white, true, true)
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
                        PromiseAdapter promiseAdapter = (PromiseAdapter) adapter;
                        if (isSelect()) {
                            // 承诺选择
                            promiseAdapter.selectPromise(position);
                        } else {
                            // 承诺详情
                            promiseAdapter.goPromiseDetail(position);
                        }
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        obListRefresh = RxBus.register(ConsHelper.EVENT_PROMISE_LIST_REFRESH, new Action1<List<Promise>>() {
            @Override
            public void call(List<Promise> promiseList) {
                recyclerHelper.dataRefresh();
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_PROMISE_LIST_ITEM_DELETE, new Action1<Promise>() {
            @Override
            public void call(Promise promise) {
                ListHelper.removeIndexInAdapter(recyclerHelper.getAdapter(), promise);
            }
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_PROMISE_LIST_ITEM_REFRESH, new Action1<Promise>() {
            @Override
            public void call(Promise promise) {
                ListHelper.refreshIndexInAdapter(recyclerHelper.getAdapter(), promise);
            }
        });
        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isSelect()) {
            getMenuInflater().inflate(R.menu.help, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(call);
        RxBus.unregister(ConsHelper.EVENT_PROMISE_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_PROMISE_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(ConsHelper.EVENT_PROMISE_LIST_ITEM_REFRESH, obListItemRefresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_PROMISE_LIST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llSearch, R.id.llAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llSearch: // 搜索
                showSearchDialog();
                break;
            case R.id.llAdd: // 添加
                PromiseEditActivity.goActivity(mActivity);
                break;
        }
    }

    private boolean isSelect() {
        return getIntent().getIntExtra("from", FROM_BROWSE) == FROM_SELECT;
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        tvSearch.setText(ApiHelper.LIST_SHOW[searchType]);
        // api
        call = new RetrofitHelper().call(API.class).promiseListGet(searchType, page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Promise> promiseList = data.getPromiseList();
                recyclerHelper.dataOk(promiseList, more);
                // searchShow
                tvSearch.setText(ApiHelper.LIST_SHOW[searchType]);
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(more, errMsg);
            }
        });
    }

    private void showSearchDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.choose_search_type)
                .items(ApiHelper.LIST_SHOW)
                .itemsCallbackSingleChoice(searchType, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        searchType = which;
                        getData(false);
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

}
