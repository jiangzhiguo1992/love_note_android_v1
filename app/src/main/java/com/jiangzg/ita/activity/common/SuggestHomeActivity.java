package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.SuggestListAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.helper.RecyclerHelper;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class SuggestHomeActivity extends BaseActivity<SuggestHomeActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page = 0;
    private int searchType = 0; // 0是所有
    private int searchStatus = 0; // 0是所有

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
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new SuggestListAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_common, true, true)
                .viewHeader(R.layout.list_head_suggest_home)
                .viewLoadMore(new RecyclerHelper.RecyclerMoreView())
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
                        SuggestListAdapter suggestListAdapter = (SuggestListAdapter) adapter;
                        suggestListAdapter.goSuggestDetail(position);
                    }
                });
        // head
        initHead();
        // menu
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 帮助
                        HelpActivity.goActivity(mActivity, Help.TYPE_SUGGEST_HOME);
                        break;
                    case R.id.menuTop: // 返回顶部
                        rv.smoothScrollToPosition(0);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void initData(Bundle state) {
        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // head
    private void initHead() {
        View head = recyclerHelper.getViewHead();
        CardView cvMy = head.findViewById(R.id.cvMy);
        CardView cvFollow = head.findViewById(R.id.cvFollow);
        CardView cvAdd = head.findViewById(R.id.cvAdd);
        RadioGroup rgType = head.findViewById(R.id.rgType);
        RadioGroup rgStatus = head.findViewById(R.id.rgStatus);
        Button btnSearch = head.findViewById(R.id.btnSearch);
        cvMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestListActivity.goActivity(mActivity, SuggestListActivity.ENTRY_MINE);
            }
        });
        cvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestListActivity.goActivity(mActivity, SuggestListActivity.ENTRY_FOLLOW);
            }
        });
        cvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestAddActivity.goActivity(mActivity);
            }
        });
        rgType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbTypeAll:
                        searchType = 0;
                        break;
                    case R.id.rbTypeBug:
                        //searchType = Suggest.TYPE_BUG;
                        break;
                    case R.id.rbTypeFunction:
                        //searchType = Suggest.TYPE_FUNC;
                        break;
                    case R.id.rbTypeExperience:
                        //searchType = Suggest.TYPE_TASTE;
                        break;
                    case R.id.rbTypeOther:
                        //searchType = Suggest.TYPE_OTHER;
                        break;
                }
            }
        });
        rgStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbStatusAll:
                        searchStatus = 0;
                        break;
                    case R.id.rbStatusReplyNo:
                        //searchStatus = Suggest.STATUE_REPLY_NO;
                        break;
                    case R.id.rbStatusReplyYes:
                        //searchStatus = Suggest.STATUE_REPLY_YES;
                        break;
                    case R.id.rbStatusAcceptNo:
                        //searchStatus = Suggest.STATUE_ACCEPT_NO;
                        break;
                    case R.id.rbStatusAcceptYes:
                        //searchStatus = Suggest.STATUE_ACCEPT_YES;
                        break;
                    case R.id.rbStatusHandleIng:
                        //searchStatus = Suggest.STATUE_HANDLE_ING;
                        break;
                    case R.id.rbStatusHandleOver:
                        //searchStatus = Suggest.STATUE_HANDLE_OVER;
                        break;
                }
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerHelper.dataRefresh();
            }
        });
    }

    public void getData(final boolean more) {
        page = more ? page + 1 : 0;

        // todo api searchType + searchStatus

        Call<Result> call = new RetrofitHelper().call(API.class).suggestListGet(page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                long total = data.getTotal();
                List<Suggest> suggestList = data.getSuggestList();
                recyclerHelper.data(suggestList, total, more);
                recyclerHelper.viewEmptyShow(data.getShow());
            }

            @Override
            public void onFailure() {
                srl.setRefreshing(false);
            }
        });
    }

}
