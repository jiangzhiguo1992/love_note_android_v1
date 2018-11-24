package com.jiangzg.lovenote.fragment;

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
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.activity.settings.SettingsActivity;
import com.jiangzg.lovenote.activity.topic.PostCollectActivity;
import com.jiangzg.lovenote.activity.topic.PostMineActivity;
import com.jiangzg.lovenote.activity.topic.TopicMessageActivity;
import com.jiangzg.lovenote.adapter.TopicHomeKindAdapter;
import com.jiangzg.lovenote.base.BaseFragment;
import com.jiangzg.lovenote.base.BasePagerFragment;
import com.jiangzg.lovenote.domain.CommonCount;
import com.jiangzg.lovenote.domain.Help;
import com.jiangzg.lovenote.domain.PostKindInfo;
import com.jiangzg.lovenote.domain.PostSubKindInfo;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
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
        ViewHelper.initTopBar(mActivity, tb, mActivity.getString(R.string.nav_topic), false);
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
        CommonCount commonCount = SPHelper.getCommonCount();
        boolean redPoint = (commonCount.getNoticeNewCount() > 0) || (commonCount.getVersionNewCount() > 0);
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
