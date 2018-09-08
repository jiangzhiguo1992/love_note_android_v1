package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.SuggestCommentAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Suggest;
import com.jiangzg.lovenote_admin.domain.SuggestComment;
import com.jiangzg.lovenote_admin.domain.SuggestInfo;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;
import com.jiangzg.lovenote_admin.view.FrescoView;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;

public class SuggestActivity extends BaseActivity<SuggestActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rv)
    RecyclerView rv;

    private Suggest suggest;
    private RecyclerHelper recyclerHelper;
    private int page;

    public static void goActivity(Activity from, Suggest suggest) {
        Intent intent = new Intent(from, SuggestActivity.class);
        intent.putExtra("suggest", suggest);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // init
        suggest = intent.getParcelableExtra("suggest");
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new SuggestCommentAdapter(mActivity))
                .viewHeader(mActivity, R.layout.list_head_suggest_comment)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getCommentData(true);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        SuggestCommentAdapter commentAdapter = (SuggestCommentAdapter) adapter;
                        commentAdapter.showDeleteDialog(position);
                    }
                });
        // head
        initHead();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // refresh
        getCommentData(false);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    private void initHead() {
        // data
        if (suggest == null || recyclerHelper == null) return;
        boolean top = suggest.isTop();
        boolean official = suggest.isOfficial();
        boolean mine = suggest.isMine();
        String statusShow = SuggestInfo.getStatusShow(suggest.getStatus());
        String kindShow = SuggestInfo.getKindShow(suggest.getKind());
        String title = suggest.getTitle();
        String create = DateUtils.getString(suggest.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String createShow = String.format(Locale.getDefault(), getString(R.string.create_at_colon_space_holder), create);
        final String contentImgUrl = suggest.getContentImage();
        String contentText = suggest.getContentText();
        // view
        View head = recyclerHelper.getViewHead();
        TextView tvTitle = head.findViewById(R.id.tvTitle);
        TextView tvCreateAt = head.findViewById(R.id.tvCreateAt);
        TextView tvContent = head.findViewById(R.id.tvContent);
        // imageView
        FrescoView ivContent = head.findViewById(R.id.ivContent);
        ViewGroup.LayoutParams layoutParams = ivContent.getLayoutParams();
        ivContent.setWidthAndHeight(ScreenUtils.getScreenWidth(mActivity), layoutParams.height);
        // otherView
        tvTitle.setText(title);
        tvCreateAt.setText(createShow);
        tvContent.setText(contentText);
        if (StringUtils.isEmpty(contentImgUrl)) {
            ivContent.setVisibility(View.GONE);
            ivContent.setClickListener(null);
        } else {
            ivContent.setVisibility(View.VISIBLE);
            ivContent.setData(contentImgUrl);
        }
    }

    private void getCommentData(final boolean more) {
        if (suggest == null) return;
        page = more ? page + 1 : 0;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).setSuggestCommentListGet(suggest.getId(), page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<SuggestComment> suggestCommentList = data.getSuggestCommentList();
                recyclerHelper.dataOk(suggestCommentList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
