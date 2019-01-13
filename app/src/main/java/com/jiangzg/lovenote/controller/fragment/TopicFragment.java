package com.jiangzg.lovenote.controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostCollectActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostMineActivity;
import com.jiangzg.lovenote.controller.activity.topic.TopicMessageActivity;
import com.jiangzg.lovenote.controller.adapter.topic.HomeKindAdapter;
import com.jiangzg.lovenote.controller.fragment.base.BaseFragment;
import com.jiangzg.lovenote.controller.fragment.base.BasePagerFragment;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.model.entity.PostSubKindInfo;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class TopicFragment extends BasePagerFragment<TopicFragment> {

    public static List<PostKindInfo> postKindInfoList;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

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
        ViewHelper.initTopBar(mActivity, tb, mActivity.getString(R.string.nav_topic), false);
        fitToolBar(tb);
        // menu
        tb.inflateMenu(R.menu.help_notice);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new HomeKindAdapter(mActivity, mFragment))
                .viewEmpty(mActivity, R.layout.list_empty_grey, false, false)
                .viewHeader(mActivity, R.layout.list_head_topic)
                .setAdapter()
                .listenerRefresh(this::refreshData)
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        HomeKindAdapter homeKindAdapter = (HomeKindAdapter) adapter;
                        homeKindAdapter.goPostList(position);
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mFragment, HelpActivity.INDEX_TOPIC_HOME);
                return true;
            case R.id.menuNotice: // 消息
                TopicMessageActivity.goActivity(mFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initHead() {
        if (recyclerHelper == null) return;
        View head = recyclerHelper.getViewHead();
        LinearLayout llMyPush = head.findViewById(R.id.llMyPush);
        LinearLayout llMyCollect = head.findViewById(R.id.llMyCollect);
        // 发布
        llMyPush.setOnClickListener(v -> PostMineActivity.goActivity(mFragment));
        // 收藏
        llMyCollect.setOnClickListener(v -> PostCollectActivity.goActivity(mFragment));
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).topicHomeGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                srl.setRefreshing(false);
                postKindInfoList = getPostKindInfoEnableList(data.getPostKindInfoList());
                recyclerHelper.dataNew(postKindInfoList, 0);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                srl.setRefreshing(false);
                recyclerHelper.viewEmptyShow(message);
            }
        });
        pushApi(api);
    }

    // getPostKindInfoEnableList 获取可以显示的kind
    public ArrayList<PostKindInfo> getPostKindInfoEnableList(List<PostKindInfo> infoList) {
        ArrayList<PostKindInfo> returnList = new ArrayList<>();
        if (infoList == null || infoList.size() <= 0) {
            return returnList;
        }
        for (PostKindInfo info : infoList) {
            if (info != null && info.isEnable()) {
                ArrayList<PostSubKindInfo> returnSubList = new ArrayList<>();
                List<PostSubKindInfo> subKindInfoList = info.getPostSubKindInfoList();
                if (subKindInfoList != null && subKindInfoList.size() > 0) {
                    for (PostSubKindInfo subInfo : subKindInfoList) {
                        if (subInfo != null && subInfo.isEnable()) {
                            returnSubList.add(subInfo);
                        }
                    }
                }
                info.setPostSubKindInfoList(returnSubList);
                returnList.add(info);
            }
        }
        return returnList;
    }

}
