package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.AwardRuleAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.AwardRule;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
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

public class AwardRuleListActivity extends BaseActivity<AwardRuleListActivity> {

    private static final int FROM_BROWSE = 0;
    private static final int FROM_SELECT = 1;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    private RecyclerHelper recyclerHelper;
    private Observable<List<AwardRule>> obListRefresh;
    private Observable<AwardRule> obListItemDelete;
    private Call<Result> call;
    private int page;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AwardRuleListActivity.class);
        intent.putExtra("from", FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelect(Activity from) {
        Intent intent = new Intent(from, AwardRuleListActivity.class);
        intent.putExtra("from", FROM_SELECT);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
        return R.layout.activity_award_rule_list;
    }

    @Override
    protected void initView(Bundle state) {
        String title;
        if (isSelect()) {
            title = getString(R.string.please_select_rule);
        } else {
            title = getString(R.string.award_rule);
        }
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new AwardRuleAdapter(mActivity))
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
                        AwardRuleAdapter awardRuleAdapter = (AwardRuleAdapter) adapter;
                        if (isSelect()) {
                            // 规则选择
                            awardRuleAdapter.selectAwardRule(position);
                        }
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        AwardRuleAdapter awardRuleAdapter = (AwardRuleAdapter) adapter;
                        awardRuleAdapter.showDeleteDialog(position);
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        obListRefresh = RxBus.register(ConsHelper.EVENT_AWARD_RULE_LIST_REFRESH, new Action1<List<AwardRule>>() {
            @Override
            public void call(List<AwardRule> awardRuleList) {
                recyclerHelper.dataRefresh();
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_AWARD_RULE_LIST_ITEM_DELETE, new Action1<AwardRule>() {
            @Override
            public void call(AwardRule awardRule) {
                ListHelper.removeIndexInAdapter(recyclerHelper.getAdapter(), awardRule);
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
        RxBus.unregister(ConsHelper.EVENT_AWARD_RULE_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_AWARD_RULE_LIST_ITEM_DELETE, obListItemDelete);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_AWARD_RULE_LIST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd: // 添加
                AwardRuleEditActivity.goActivity(mActivity);
                break;
        }
    }

    private boolean isSelect() {
        return getIntent().getIntExtra("from", FROM_BROWSE) == FROM_SELECT;
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        call = new RetrofitHelper().call(API.class).awardRuleListGet(page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                recyclerHelper.viewEmptyShow(data.getShow());
                List<AwardRule> awardRuleList = data.getAwardRuleList();
                recyclerHelper.dataOk(awardRuleList, more);
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(more, errMsg);
            }
        });
    }

}
