package com.jiangzg.mianmian.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.SettingsActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.activity.topic.PostCollectActivity;
import com.jiangzg.mianmian.activity.topic.PostMineActivity;
import com.jiangzg.mianmian.activity.topic.TopicMessageActivity;
import com.jiangzg.mianmian.adapter.TopicHomeKindAdapter;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.base.BasePagerFragment;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.PostKindInfo;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.TopicMessageInfo;
import com.jiangzg.mianmian.domain.Version;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class TopicFragment extends BasePagerFragment<TopicFragment> {

    public static List<TopicMessageInfo> topicMessageInfoList;
    public static List<PostKindInfo> postKindInfoList;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private Call<Result> call;
    private RecyclerHelper recyclerHelper;

    public static TopicFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(TopicFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_topic;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_topic), false);
        fitToolBar(tb);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new TopicHomeKindAdapter(mActivity, mFragment))
                .viewEmpty(mActivity, R.layout.list_empty_grey, false, false)
                .viewHeader(mActivity, R.layout.list_head_topic)
                .setAdapter()
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshData();
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        TopicHomeKindAdapter homeKindAdapter = (TopicHomeKindAdapter) adapter;
                        homeKindAdapter.goAngryDetail(position);
                    }
                });
        // head
        initHead();
    }

    protected void loadData() {
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
    }

    @Override
    public void onStart() {
        super.onStart();
        // menu
        refreshMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mFragment, Help.INDEX_TOPIC_HOME);
                return true;
            case R.id.menuSettings: // 设置
                SettingsActivity.goActivity(mFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshMenu() {
        long noticeNoReadCount = SPHelper.getNoticeNoReadCount();
        Version version = SPHelper.getVersion();
        boolean redPoint = (noticeNoReadCount > 0) || (version != null);
        tb.getMenu().clear();
        tb.inflateMenu(redPoint ? R.menu.help_settings_point : R.menu.help_settings);
    }

    private void initHead() {
        if (recyclerHelper == null) return;
        View head = recyclerHelper.getViewHead();
        Button btnMyPush = head.findViewById(R.id.btnMyPush);
        Button btnMyCollect = head.findViewById(R.id.btnMyCollect);
        Button btnMyMessage = head.findViewById(R.id.btnMyMessage);
        // 发布
        btnMyPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostMineActivity.goActivity(mFragment);
            }
        });
        // 收藏
        btnMyCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PostCollectActivity.goActivity(mFragment);
            }
        });
        // 消息
        btnMyMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TopicMessageActivity.goActivity(mFragment);
            }
        });
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        call = new RetrofitHelper().call(API.class).topicHomeGet();
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                topicMessageInfoList = data.getTopicMessageInfoList();
                postKindInfoList = ListHelper.getPostKindInfoEnableList(data.getPostKindInfoList());
                recyclerHelper.dataNew(postKindInfoList, 0);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                recyclerHelper.viewEmptyShow(message);
            }
        });
    }

}
