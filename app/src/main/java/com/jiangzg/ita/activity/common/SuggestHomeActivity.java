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
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.SuggestListAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.third.RecyclerManager;
import com.jiangzg.ita.third.RecyclerMoreView;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SuggestHomeActivity extends BaseActivity<SuggestHomeActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerManager recyclerManager;
    private int searchType = 0; // 0是所有
    private int searchStatus = 0; // 0是所有

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SuggestHomeActivity.class);
        // intent.putExtra();
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
        srl.setEnabled(false);
        recyclerManager = new RecyclerManager(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new SuggestListAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_common, true, true)
                .viewHeader(R.layout.list_head_suggest_home)
                .viewLoadMore(new RecyclerMoreView())
                .setAdapter()
                .listenerRefresh(new RecyclerManager.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData(false);
                    }
                })
                .listenerMore(new RecyclerManager.MoreListener() {
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
        recyclerManager.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // head
    private void initHead() {
        View head = recyclerManager.getViewHead();
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
                        searchType = Suggest.TYPE_BUG;
                        break;
                    case R.id.rbTypeFunction:
                        searchType = Suggest.TYPE_FUNCTION;
                        break;
                    case R.id.rbTypeExperience:
                        searchType = Suggest.TYPE_EXPERIENCE;
                        break;
                    case R.id.rbTypeOther:
                        searchType = Suggest.TYPE_OTHER;
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
                        searchStatus = Suggest.STATUE_REPLY_NO;
                        break;
                    case R.id.rbStatusReplyYes:
                        searchStatus = Suggest.STATUE_REPLY_YES;
                        break;
                    case R.id.rbStatusAcceptNo:
                        searchStatus = Suggest.STATUE_ACCEPT_NO;
                        break;
                    case R.id.rbStatusAcceptYes:
                        searchStatus = Suggest.STATUE_ACCEPT_YES;
                        break;
                    case R.id.rbStatusHandleIng:
                        searchStatus = Suggest.STATUE_HANDLE_ING;
                        break;
                    case R.id.rbStatusHandleOver:
                        searchStatus = Suggest.STATUE_HANDLE_OVER;
                        break;
                }
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerManager.dataRefresh();
            }
        });
    }

    public void getData(final boolean more) {
        // todo api searchType + searchStatus + limit + offset
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);
                List<Suggest> suggestList = new ArrayList<>();
                if (more) {
                    Suggest s0 = new Suggest();
                    s0.setTitle("上拉加载出来的！");
                    s0.setCreatedAt(1520866299);
                    s0.setUpdatedAt(1520866299);
                    s0.setFollow(false);
                    s0.setComment(false);
                    s0.setFollowCount(0);
                    s0.setCommentCount(0);
                    s0.setContentType(Suggest.TYPE_OTHER);
                    s0.setOfficial(false);
                    s0.setTop(false);
                    suggestList.add(s0);
                } else {
                    Suggest s1 = new Suggest();
                    s1.setTitle("我发现了一个bug！");
                    s1.setStatus(Suggest.STATUE_ACCEPT_NO);
                    s1.setCreatedAt(1520866299);
                    s1.setUpdatedAt(1520866299);
                    s1.setFollow(false);
                    s1.setComment(false);
                    s1.setFollowCount(0);
                    s1.setCommentCount(0);
                    s1.setContentType(Suggest.TYPE_BUG);
                    s1.setOfficial(true);
                    s1.setTop(true);
                    Suggest s2 = new Suggest();
                    s2.setTitle("我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！");
                    s2.setStatus(Suggest.STATUE_HANDLE_OVER);
                    s2.setCreatedAt(1520010299);
                    s2.setUpdatedAt(1520866299);
                    s2.setFollow(true);
                    s2.setComment(false);
                    s2.setFollowCount(111111111);
                    s2.setCommentCount(0);
                    s2.setContentType(Suggest.TYPE_FUNCTION);
                    s2.setOfficial(true);
                    s2.setTop(false);
                    Suggest s3 = new Suggest();
                    s3.setTitle("我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！");
                    s3.setStatus(Suggest.STATUE_REPLY_YES);
                    s3.setCreatedAt(1520010299);
                    s3.setUpdatedAt(1520010299);
                    s3.setFollow(true);
                    s3.setComment(true);
                    s3.setFollowCount(111111111);
                    s3.setCommentCount(2);
                    s3.setContentType(Suggest.TYPE_EXPERIENCE);
                    s3.setOfficial(false);
                    s3.setTop(true);
                    suggestList.add(s1);
                    suggestList.add(s2);
                    suggestList.add(s3);
                    suggestList.add(s1);
                    suggestList.add(s2);
                    suggestList.add(s3);
                    suggestList.add(s1);
                    suggestList.add(s2);
                    suggestList.add(s3);
                }
                recyclerManager.data(suggestList, 12, more);
                recyclerManager.viewEmptyShow();
            }
        }, 1000);
    }

}
