package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.PostAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Post;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class PostListActivity extends BaseActivity<PostListActivity> {

    private static final int SEARCH_LIST = 0;
    private static final int SEARCH_COLLECT = 1;
    private static final int SEARCH_REPORT = 2;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnCollect)
    Button btnCollect;
    @BindView(R.id.btnReport)
    Button btnReport;
    @BindView(R.id.etUid)
    EditText etUid;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;
    private int searchType;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), PostListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long uid) {
        Intent intent = new Intent(from, PostListActivity.class);
        intent.putExtra("uid", uid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_post_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "post_list", true);
        long uid = intent.getLongExtra("uid", 0);
        if (uid > 0) {
            etUid.setText(String.valueOf(uid));
        }
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new PostAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getListData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        PostAdapter postAdapter = (PostAdapter) adapter;
                        postAdapter.goPostCommentList(position);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        PostAdapter postAdapter = (PostAdapter) adapter;
                        postAdapter.showDeleteDialog(position);
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        PostAdapter postAdapter = (PostAdapter) adapter;
                        switch (view.getId()) {
                            // TODO
                        }
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        searchType = SEARCH_REPORT;
        getListData(false);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.btnCollect, R.id.btnReport, R.id.btnSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnCollect:
                searchType = SEARCH_COLLECT;
                getListData(false);
                break;
            case R.id.btnReport:
                searchType = SEARCH_REPORT;
                getListData(false);
                break;
            case R.id.btnSearch:
                searchType = SEARCH_LIST;
                getListData(false);
                break;
        }
    }

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> call;

        if (searchType == SEARCH_COLLECT) {
            call = new RetrofitHelper().call(API.class).topicPostCollectListGet(page);
        } else if (searchType == SEARCH_REPORT) {
            call = new RetrofitHelper().call(API.class).topicPostReportListGet(page);
        } else {
            long uid = 0;
            String sUid = etUid.getText().toString().trim();
            if (StringUtils.isNumber(sUid)) {
                uid = Long.parseLong(sUid);
            }
            if (uid <= 0) return;
            call = new RetrofitHelper().call(API.class).topicPostUserListGet(uid, page);
        }
        MaterialDialog loading = null;
        if (!more) {
            loading = getLoading(true);
        }
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
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
