package com.jiangzg.lovenote.controller.fragment.topic;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.adapter.topic.PostAdapter;
import com.jiangzg.lovenote.controller.fragment.base.BaseFragment;
import com.jiangzg.lovenote.controller.fragment.base.BasePagerFragment;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;

public class PostCollectFragment extends BasePagerFragment<PostCollectFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private boolean me;
    private RecyclerHelper recyclerHelper;
    private int page = 0;

    public static PostCollectFragment newFragment(boolean me) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("me", me);
        return BaseFragment.newInstance(PostCollectFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        me = data.getBoolean("me");
        return R.layout.fragment_post_collect;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new PostAdapter(mActivity, false))
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
        if (me) {
            recyclerHelper.listenerClick(new OnItemLongClickListener() {
                @Override
                public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                    PostAdapter postAdapter = (PostAdapter) adapter;
                    postAdapter.showCollectDeleteDialog(position);
                }
            });
        }
    }

    @Override
    protected void loadData() {
        // event
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

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).topicPostCollectListGet(me, page);
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
