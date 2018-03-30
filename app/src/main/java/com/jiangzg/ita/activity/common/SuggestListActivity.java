package com.jiangzg.ita.activity.common;

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
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.SuggestListAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.third.RecyclerHelper;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

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

    public static void goActivity(Activity from, int entry) {
        Intent intent = new Intent(from, SuggestListActivity.class);
        intent.putExtra("entry", entry);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
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
                .initAdapter(new SuggestListAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_common, true, true)
                .viewLoadMore(new RecyclerHelper.RecyclerMoreView())
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

        // menu
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 评论
                        int helpType;
                        if (entry == ENTRY_FOLLOW) {
                            helpType = Help.TYPE_SUGGEST_FOLLOW;
                        } else {
                            helpType = Help.TYPE_SUGGEST_MINE;
                        }
                        HelpActivity.goActivity(mActivity, helpType);
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
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void getData(final boolean more) {
        // todo api searchType + searchStatus + limit + offset
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
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
                recyclerHelper.data(suggestList, 12, more);
                recyclerHelper.viewEmptyShow();
            }
        }, 1000);
    }

}
