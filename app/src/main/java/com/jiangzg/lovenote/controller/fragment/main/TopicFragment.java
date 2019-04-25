package com.jiangzg.lovenote.controller.fragment.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostAddActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostMyRelationActivity;
import com.jiangzg.lovenote.controller.activity.topic.PostSearchActivity;
import com.jiangzg.lovenote.controller.activity.topic.TopicMessageActivity;
import com.jiangzg.lovenote.controller.adapter.topic.HomeKindAdapter;
import com.jiangzg.lovenote.controller.adapter.topic.PostAdapter;
import com.jiangzg.lovenote.controller.fragment.base.BaseFragment;
import com.jiangzg.lovenote.controller.fragment.base.BasePagerFragment;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.model.entity.PostSubKindInfo;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class TopicFragment extends BasePagerFragment<TopicFragment> {

    public static List<PostKindInfo> postKindInfoList;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    private RecyclerHelper postRecyclerHelper, kindRecyclerHelper;
    private long create;
    private int page = 0;

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
        tb.inflateMenu(R.menu.help_mine_notice);
        // recycler
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        postRecyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(layoutManager)
                .initRefresh(srl, true)
                .initAdapter(new PostAdapter(mActivity))
                .viewHeader(mActivity, R.layout.list_head_topic_home)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .viewAnim()
                .setAdapter()
                .listenerRefresh(() -> refreshPostListData(false))
                .listenerMore(currentCount -> refreshPostListData(true))
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        PostAdapter postAdapter = (PostAdapter) adapter;
                        postAdapter.goPostDetail(position);
                    }
                });
        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 防止postItem里的rv抢焦点，触发srl下拉事件
                int position = layoutManager.findFirstCompletelyVisibleItemPosition();
                srl.setEnabled(position <= 0);
            }
        });
        // head
        initHead();
    }

    protected void loadData() {
        // event
        Observable<Post> obListRefresh = RxBus.register(RxBus.EVENT_POST_LIST_REFRESH, post -> {
            if (postRecyclerHelper == null) return;
            postRecyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_POST_LIST_REFRESH, obListRefresh);
        Observable<Post> obListItemDelete = RxBus.register(RxBus.EVENT_POST_LIST_ITEM_DELETE, post -> {
            if (postRecyclerHelper == null) return;
            ListHelper.removeObjInAdapter(postRecyclerHelper.getAdapter(), post);
        });
        pushBus(RxBus.EVENT_POST_LIST_ITEM_DELETE, obListItemDelete);
        Observable<Post> obListItemRefresh = RxBus.register(RxBus.EVENT_POST_LIST_ITEM_REFRESH, post -> {
            if (postRecyclerHelper == null) return;
            ListHelper.refreshObjInAdapter(postRecyclerHelper.getAdapter(), post);
        });
        pushBus(RxBus.EVENT_POST_LIST_ITEM_REFRESH, obListItemRefresh);
        // data
        refreshPostKindData();
        postRecyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(kindRecyclerHelper);
        RecyclerHelper.release(postRecyclerHelper);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mFragment, HelpActivity.INDEX_TOPIC_HOME);
                return true;
            case R.id.menuMine: // 我的
                PostMyRelationActivity.goActivity(mFragment);
                return true;
            case R.id.menuNotice: // 消息
                TopicMessageActivity.goActivity(mFragment);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd: // 添加
                if (postKindInfoList == null || postKindInfoList.size() <= 0) {
                    return;
                }
                PostKindInfo kindInfo = postKindInfoList.get(0);
                List<PostSubKindInfo> subKindInfoList = kindInfo.getPostSubKindInfoList();
                if (subKindInfoList == null || subKindInfoList.size() <= 0) {
                    return;
                }
                PostSubKindInfo subKindInfo = subKindInfoList.get(0);
                PostAddActivity.goActivity(mActivity, kindInfo, subKindInfo.getKind());
                break;
        }
    }

    private void initHead() {
        // view
        View head = postRecyclerHelper.getViewHead();
        if (head == null) return;
        // couple
        CardView cvSearch = head.findViewById(R.id.cvSearch);
        RecyclerView rvKind = head.findViewById(R.id.rv);
        // kind
        kindRecyclerHelper = new RecyclerHelper(rvKind)
                .initLayoutManager(new GridLayoutManager(mActivity, 4) {
                    @Override
                    public boolean canScrollVertically() {
                        return false;
                    }
                })
                .initAdapter(new HomeKindAdapter(mFragment))
                .viewAnim()
                .setAdapter()
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        HomeKindAdapter homeKindAdapter = (HomeKindAdapter) adapter;
                        homeKindAdapter.goPostList(position);
                    }
                });
        // search
        cvSearch.setOnClickListener(v -> PostSearchActivity.goActivity(mActivity));
    }

    private void refreshPostKindData() {
        Call<Result> api = new RetrofitHelper().call(API.class).topicHomeGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (kindRecyclerHelper == null) return;
                postKindInfoList = getPostKindInfoEnableList(data.getPostKindInfoList());
                kindRecyclerHelper.dataNew(postKindInfoList, 0);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void refreshPostListData(final boolean more) {
        page = more ? page + 1 : 0;
        if (!more || create <= 0) {
            create = TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong());
        }
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostListHomeGet(create, page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (postRecyclerHelper == null) return;
                postRecyclerHelper.dataOk(data.getShow(), data.getPostList(), more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (postRecyclerHelper == null) return;
                postRecyclerHelper.dataFail(more, message);
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
