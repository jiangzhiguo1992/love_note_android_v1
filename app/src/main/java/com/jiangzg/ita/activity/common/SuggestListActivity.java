package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.SuggestAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.third.RecyclerManager;
import com.jiangzg.ita.utils.ViewUtils;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SuggestListActivity extends BaseActivity<SuggestListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    private RecyclerManager recyclerManager;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SuggestListActivity.class);
        // intent.putExtra();
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_list;
    }

    @Override
    protected void initView(Bundle state) {
        ViewUtils.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // recycler
        //rv.setLayoutManager(new LinearLayoutManager(mActivity));
        //SuggestAdapter adapter = new SuggestAdapter(mActivity);
        //rv.setAdapter(adapter);
        // refresh
        //srl.setProgressViewEndTarget();
        //srl.onInterceptTouchEvent();
        //srl.offsetTopAndBottom();
        //srl.setOnDragListener(new View.OnDragListener() {
        //    @Override
        //    public boolean onDrag(View v, DragEvent event) {
        //        return false;
        //    }
        //});
        //srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        //    @Override
        //    public void onRefresh() {
        //        getData(true);
        //    }
        //});

        recyclerManager = new RecyclerManager(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl)
                .setAdapter(new SuggestAdapter(mActivity))
                .viewHeader(R.layout.list_head_suggest) // todo head
                //.viewLoading() // todo load
                //.viewEmpty() // todo empty
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
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        SuggestAdapter suggestAdapter = (SuggestAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.llTitle:
                                suggestAdapter.goSuggestDetail(position);
                                break;
                            case R.id.llWatch:
                                suggestAdapter.toggleWatch(position);
                                break;
                            case R.id.llComment:
                                suggestAdapter.comment(position);
                                break;
                        }
                    }
                });
        // todo behavior
    }

    @Override
    protected void initData(Bundle state) {
        recyclerManager.dataRefresh();
    }


    public void getData(final boolean more) {
        // todo api
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);
                if (more) {
                    Suggest s0 = new Suggest();
                    s0.setTitle("下拉出来的！");
                    s0.setCreatedAt(1520866299);
                    s0.setUpdatedAt(1520866299);
                    //s1.setContent();
                    s0.setWatch(false);
                    s0.setComment(false);
                    s0.setWatchCount(0);
                    s0.setCommentCount(0);
                    List<Suggest> list = new ArrayList<>();
                    list.add(s0);
                    recyclerManager.data(list, more);
                } else {
                    List<Suggest> suggestList = new ArrayList<>();
                    Suggest s1 = new Suggest();
                    s1.setTitle("我发现了一个bug！");
                    s1.setCreatedAt(1520866299);
                    s1.setUpdatedAt(1520866299);
                    //s1.setContent();
                    s1.setWatch(false);
                    s1.setComment(false);
                    s1.setWatchCount(0);
                    s1.setCommentCount(0);
                    Suggest s2 = new Suggest();
                    s2.setTitle("我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！");
                    s2.setCreatedAt(1520010299);
                    s2.setUpdatedAt(1520866299);
                    //s2.setContent();
                    s2.setWatch(true);
                    s2.setComment(false);
                    s2.setWatchCount(111111111);
                    s2.setCommentCount(0);
                    Suggest s3 = new Suggest();
                    s3.setTitle("我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！我发现了一个bug！");
                    s3.setCreatedAt(1520010299);
                    s3.setUpdatedAt(1520010299);
                    //s3.setContent();
                    s3.setWatch(true);
                    s3.setComment(true);
                    s3.setWatchCount(111111111);
                    s3.setCommentCount(2);
                    suggestList.add(s1);
                    suggestList.add(s2);
                    suggestList.add(s3);
                    suggestList.add(s1);
                    suggestList.add(s2);
                    suggestList.add(s3);
                    suggestList.add(s1);
                    suggestList.add(s2);
                    suggestList.add(s3);
                    recyclerManager.data(suggestList, more);
                }
            }
        }, 1000);
    }

}
