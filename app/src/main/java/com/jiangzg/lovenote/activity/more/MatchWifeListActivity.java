package com.jiangzg.lovenote.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.MatchWifeAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.MatchPeriod;
import com.jiangzg.lovenote.domain.MatchWork;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class MatchWifeListActivity extends BaseActivity<MatchWifeListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.llTop)
    LinearLayout llTop;
    @BindView(R.id.llOrder)
    LinearLayout llOrder;
    @BindView(R.id.tvOrder)
    TextView tvOrder;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private MatchPeriod period;
    private RecyclerHelper recyclerHelper;
    private Call<Result> call;
    private int page;
    private int orderIndex;

    public static void goActivity(Fragment from, MatchPeriod period) {
        Intent intent = new Intent(from.getActivity(), MatchWifeListActivity.class);
        intent.putExtra("period", period);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_match_wife_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_wife), true);
        // search
        orderIndex = 0;
        tvOrder.setText(ApiHelper.LIST_MATCH_ORDER_SHOW[orderIndex]);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new GridLayoutManager(mActivity, 2))
                .initRefresh(srl, true)
                .initAdapter(new MatchWifeAdapter(mActivity))
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
                        MatchWifeAdapter wifeAdapter = (MatchWifeAdapter) adapter;
                        wifeAdapter.goWifeDetail(position);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        MatchWifeAdapter wifeAdapter = (MatchWifeAdapter) adapter;
                        wifeAdapter.showDeleteDialog(position);
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        switch (view.getId()) {
                            case R.id.llReport: // 举报
                                // TODO
                                break;
                            case R.id.llCoin: // 金币
                                // TODO
                                break;
                            case R.id.llPoint: // 点赞
                                // TODO
                                break;
                        }
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        period = intent.getParcelableExtra("period");
        // head
        initHead();
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.llTop, R.id.llOrder, R.id.llAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llTop: // 置顶
                if (rv != null) rv.smoothScrollToPosition(0);
                break;
            case R.id.llOrder: // 搜索
                showSearchDialog();
                break;
            case R.id.llAdd: // 添加
                // TODO
                break;
        }
    }

    private void initHead() {
        if (period == null) {
            mActivity.finish();
            return;
        }
        // TODO 本期介绍
        // TODO 往期在这里
        //MatchPeriodListActivity.goActivity(mActivity, MatchPeriod.MATCH_KIND_WIFE_PICTURE);
        // TODO 我们的
    }

    private void getData(final boolean more) {
        if (period == null) {
            srl.setRefreshing(false);
        }
        page = more ? page + 1 : 0;
        // api
        int orderType = ApiHelper.LIST_MATCH_ORDER_TYPE[orderIndex];
        call = new RetrofitHelper().call(API.class).moreMatchWordPeriodListGet(period.getId(), orderType, page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<MatchWork> matchWorkList = data.getMatchWorkList();
                recyclerHelper.dataOk(matchWorkList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

    private void showSearchDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.LIST_MATCH_ORDER_SHOW)
                .itemsCallbackSingleChoice(orderIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (recyclerHelper == null) return true;
                        if (which < 0 || which >= ApiHelper.LIST_MATCH_ORDER_TYPE.length
                                || which >= ApiHelper.LIST_MATCH_ORDER_SHOW.length) {
                            return true;
                        }
                        orderIndex = which;
                        tvOrder.setText(ApiHelper.LIST_MATCH_ORDER_SHOW[orderIndex]);
                        recyclerHelper.dataRefresh();
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

}
