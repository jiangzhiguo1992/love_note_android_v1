package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Post;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class PostCommentListActivity extends BaseActivity<PostCommentListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnTop)
    Button btnTop;
    @BindView(R.id.btnOfficial)
    Button btnOfficial;
    @BindView(R.id.btnWell)
    Button btnWell;
    @BindView(R.id.rv)
    RecyclerView rv;

    private Post post;
    private RecyclerHelper recyclerHelper;
    private int page;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), PostCommentListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long pid) {
        Intent intent = new Intent(from, PostCommentListActivity.class);
        intent.putExtra("pid", pid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_post_comment_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "post_comment_list", true);
        // init
        post = intent.getParcelableExtra("post");
        // head
        initHead();
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                //.initAdapter(new PostCommentAdapter(mActivity)) // TODO
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
                        // TODO
                        //PostCommentAdapter postCommentAdapter = (PostCommentAdapter) adapter;
                        //postCommentAdapter.goUserDetail(position);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        // TODO
                        //PostCommentAdapter postCommentAdapter = (PostCommentAdapter) adapter;
                        //postCommentAdapter.showDeleteDialog(position);
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        // TODO
                        //PostCommentAdapter postCommentAdapter = (PostCommentAdapter) adapter;
                        //switch (view.getId()) {
                        //    case R.id.tvTop:
                        //        postCommentAdapter.updateOfficial(position);
                        //        break;
                        //}
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        getListData(false);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.btnTop, R.id.btnOfficial, R.id.btnWell})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnTop:
                toggleTop();
                break;
            case R.id.btnOfficial:
                toggleOfficial();
                break;
            case R.id.btnWell:
                toggleWell();
                break;
        }
    }

    private void initHead() {
        // data
        if (post == null) return;
        boolean top = post.isTop();
        boolean official = post.isOfficial();
        boolean well = post.isWell();
        // set
        btnTop.setText(top ? "top-ing" : "top-no");
        btnOfficial.setText(official ? "official-ing" : "official-no");
        btnWell.setText(well ? "well-ing" : "well-no");
    }

    private void getListData(final boolean more) {
        if (post == null) return;
        page = more ? page + 1 : 0;
        // api TODO
        //Call<Result> call = new RetrofitHelper().call(API.class).topicPostCommentListGet(post.getId(), page);
        //RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
        //    @Override
        //    public void onResponse(int code, String message, Result.Data data) {
        //        if (recyclerHelper == null) return;
        //        recyclerHelper.viewEmptyShow(data.getShow());
        //        List<PostComment> postCommentList = data.getPostCommentList();
        //        recyclerHelper.dataOk(postCommentList, more);
        //    }
        //
        //    @Override
        //    public void onFailure(int code, String message, Result.Data data) {
        //        if (recyclerHelper == null) return;
        //        recyclerHelper.dataFail(more, message);
        //    }
        //});
    }

    private void toggleTop() {
        if (post == null) return;
        final boolean top = !post.isTop();
        DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content("修改为置顶-" + top + "？")
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (post == null) return;
                        post.setTop(top);
                        updatePost(post);
                    }
                })
                .show();
    }

    private void toggleOfficial() {
        if (post == null) return;
        final boolean official = !post.isOfficial();
        DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content("修改为官方-" + official + "？")
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (post == null) return;
                        post.setOfficial(official);
                        updatePost(post);
                    }
                })
                .show();
    }

    private void toggleWell() {
        if (post == null) return;
        final boolean well = !post.isWell();
        DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content("修改为精华-" + well + "？")
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (post == null) return;
                        post.setWell(well);
                        updatePost(post);
                    }
                })
                .show();
    }

    private void updatePost(Post body) {
        if (body == null) return;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).topicPostUpdate(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                post = data.getPost();
                initHead();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
