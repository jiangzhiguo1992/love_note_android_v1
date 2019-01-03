package com.jiangzg.lovenote.controller.activity.topic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.topic.PostAdapter;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.model.entity.PostSubKindInfo;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class PostSearchActivity extends BaseActivity<PostSearchActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.tvSearch)
    TextView tvSearch;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private PostKindInfo kindInfo;
    private PostSubKindInfo subKindInfo;
    private RecyclerHelper recyclerHelper;
    private Observable<Post> obListItemRefresh;
    private Observable<Post> obListItemDelete;
    private Call<Result> call;
    private int page;
    private long create;

    public static void goActivity(Activity from, PostKindInfo kindInfo, PostSubKindInfo subKindInfo) {
        Intent intent = new Intent(from, PostSearchActivity.class);
        intent.putExtra("kindInfo", kindInfo);
        intent.putExtra("subKindInfo", subKindInfo);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        kindInfo = intent.getParcelableExtra("kindInfo");
        subKindInfo = intent.getParcelableExtra("subKindInfo");
        return R.layout.activity_post_search;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "", true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new PostAdapter(mActivity, true, true))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
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
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // event
        obListItemDelete = RxBus.register(ConsHelper.EVENT_POST_LIST_ITEM_DELETE, post -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), post);
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_POST_LIST_ITEM_REFRESH, post -> {
            if (recyclerHelper == null) return;
            ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), post);
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RxBus.unregister(ConsHelper.EVENT_POST_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(ConsHelper.EVENT_POST_LIST_ITEM_REFRESH, obListItemRefresh);
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.tvSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvSearch: // 搜索
                if (recyclerHelper == null) return;
                recyclerHelper.dataRefresh();
                break;
        }
    }

    private void getData(final boolean more) {
        String title = etSearch.getText().toString().trim();
        if (StringUtils.isEmpty(title)) {
            ToastUtils.show(etSearch.getHint().toString());
            if (srl.isRefreshing()) srl.setRefreshing(false);
            return;
        }
        page = more ? page + 1 : 0;
        if (!more || create <= 0) {
            create = TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong());
        }
        // api
        int kindId = 0, subKindId = 0;
        if (kindInfo != null) {
            kindId = kindInfo.getKind();
            if (subKindInfo != null) {
                subKindId = subKindInfo.getKind();
            }
        }
        call = new RetrofitHelper().call(API.class).topicPostListGet(create, kindId, subKindId, title, false, false, page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Post> postList = data.getPostList();
                recyclerHelper.dataOk(postList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
