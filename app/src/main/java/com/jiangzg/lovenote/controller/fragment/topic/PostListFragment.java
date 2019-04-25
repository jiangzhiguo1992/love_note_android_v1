package com.jiangzg.lovenote.controller.fragment.topic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.adapter.topic.PostAdapter;
import com.jiangzg.lovenote.controller.fragment.base.BaseFragment;
import com.jiangzg.lovenote.controller.fragment.base.BasePagerFragment;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.model.entity.PostSubKindInfo;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;

public class PostListFragment extends BasePagerFragment<PostListFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private PostKindInfo kindInfo;
    private PostSubKindInfo subKindInfo;
    private RecyclerHelper recyclerHelper;
    private long create;
    private boolean official, well;
    private int page = 0;

    public static PostListFragment newFragment(PostKindInfo kindInfo, PostSubKindInfo subKindInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("kindInfo", kindInfo);
        bundle.putParcelable("subKindInfo", subKindInfo);
        return BaseFragment.newInstance(PostListFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        kindInfo = data.getParcelable("kindInfo");
        subKindInfo = data.getParcelable("subKindInfo");
        return R.layout.fragment_post_list;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new PostAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .viewAnim()
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        PostAdapter postAdapter = (PostAdapter) adapter;
                        postAdapter.goPostDetail(position);
                    }
                });
    }

    @Override
    protected void loadData() {
        official = false;
        well = false;
        // event
        Observable<Boolean> obSearchNormal = RxBus.register(RxBus.EVENT_POST_SEARCH_ALL, isTrue -> {
            if (!mFragment.getUserVisibleHint() || !isTrue) return;
            official = false;
            well = false;
            recyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_POST_SEARCH_ALL, obSearchNormal);
        Observable<Boolean> obSearchOfficial = RxBus.register(RxBus.EVENT_POST_SEARCH_OFFICIAL, isTrue -> {
            if (!mFragment.getUserVisibleHint() || !isTrue) return;
            official = true;
            well = false;
            recyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_POST_SEARCH_OFFICIAL, obSearchOfficial);
        Observable<Boolean> obSearchWell = RxBus.register(RxBus.EVENT_POST_SEARCH_WELL, isTrue -> {
            if (!mFragment.getUserVisibleHint() || !isTrue) return;
            official = false;
            well = true;
            recyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_POST_SEARCH_WELL, obSearchWell);
        Observable<Integer> obListRefresh = RxBus.register(RxBus.EVENT_POST_LIST_REFRESH, subKind -> {
            if (recyclerHelper == null || subKindInfo == null || (subKind > 0 && subKindInfo.getKind() != subKind)) {
                // 全部是可以更新的
                return;
            }
            recyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_POST_LIST_REFRESH, obListRefresh);
        Observable<Post> obListItemDelete = RxBus.register(RxBus.EVENT_POST_LIST_ITEM_DELETE, post -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), post);
        });
        pushBus(RxBus.EVENT_POST_LIST_ITEM_DELETE, obListItemDelete);
        Observable<Post> obListItemRefresh = RxBus.register(RxBus.EVENT_POST_LIST_ITEM_REFRESH, post -> {
            if (recyclerHelper == null) return;
            ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), post);
        });
        pushBus(RxBus.EVENT_POST_LIST_ITEM_REFRESH, obListItemRefresh);
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    public PostSubKindInfo getSubKindInfo() {
        return subKindInfo;
    }

    public int getSearchType() {
        if (official) return ApiHelper.LIST_TOPIC_TYPE_OFFICIAL;
        if (well) return ApiHelper.LIST_TOPIC_TYPE_WELL;
        return ApiHelper.LIST_TOPIC_TYPE_ALL;
    }

    private void getData(final boolean more) {
        if (subKindInfo == null) {
            if (srl.isRefreshing()) srl.setRefreshing(false);
            return;
        }
        page = more ? page + 1 : 0;
        if (!more || create <= 0) {
            create = TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong());
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostListGet(create, kindInfo.getKind(), subKindInfo.getKind(), official, well, page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataOk(data.getShow(), data.getPostList(), more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
        pushApi(api);
    }

}
