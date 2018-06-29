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
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.AwardAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Award;
import com.jiangzg.mianmian.domain.Help;
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

public class AwardListActivity extends BaseActivity<AwardListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tvScoreMe)
    TextView tvScoreMe;
    @BindView(R.id.tvScoreTa)
    TextView tvScoreTa;
    @BindView(R.id.tvSearch)
    TextView tvSearch;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.llRule)
    LinearLayout llRule;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private RecyclerHelper recyclerHelper;
    private Call<Result> callList;
    private Call<Result> callScore;
    private int page;
    private int searchType = ApiHelper.LIST_CP;
    private Observable<List<Award>> obListRefresh;
    private Observable<Award> obListItemDelete;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AwardListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
        return R.layout.activity_award_list;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.award), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new AwardAdapter(mActivity))
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
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        AwardAdapter awardAdapter = (AwardAdapter) adapter;
                        awardAdapter.showDeleteDialog(position);
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        obListRefresh = RxBus.register(ConsHelper.EVENT_AWARD_LIST_REFRESH, new Action1<List<Award>>() {
            @Override
            public void call(List<Award> awardList) {
                recyclerHelper.dataRefresh();
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_AWARD_LIST_ITEM_DELETE, new Action1<Award>() {
            @Override
            public void call(Award award) {
                ListHelper.removeIndexInAdapter(recyclerHelper.getAdapter(), award);
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
        RetrofitHelper.cancel(callList);
        RetrofitHelper.cancel(callScore);
        RxBus.unregister(ConsHelper.EVENT_AWARD_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_AWARD_LIST_ITEM_DELETE, obListItemDelete);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_AWARD_LIST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llSearch, R.id.llRule, R.id.llAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llSearch: // 搜索
                showSearchDialog();
                break;
            case R.id.llRule: // 搜索
                AwardRuleListActivity.goActivity(mActivity);
                break;
            case R.id.llAdd: // 添加
                AwardEditActivity.goActivity(mActivity);
                break;
        }
    }

    private void getData(final boolean more) {
        if (!more) getScore(); // 加载分数
        page = more ? page + 1 : 0;
        tvSearch.setText(ApiHelper.LIST_SHOW[searchType]);
        // api
        callList = new RetrofitHelper().call(API.class).awardListGet(searchType, page);
        RetrofitHelper.enqueue(callList, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Award> awardList = data.getAwardList();
                recyclerHelper.dataOk(awardList, more);
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

    private void getScore() {
        callScore = new RetrofitHelper().call(API.class).awardScoreGet();
        RetrofitHelper.enqueue(callScore, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                String myTotal = String.valueOf(data.getMyTotal());
                if (data.getMyTotal() > 0) {
                    myTotal = "+" + myTotal;
                }
                String taTotal = String.valueOf(data.getTaTotal());
                if (data.getTaTotal() > 0) {
                    taTotal = "+" + taTotal;
                }
                tvScoreMe.setText(myTotal);
                tvScoreTa.setText(taTotal);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
