package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.SuggestCommentAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.domain.SuggestComment;
import com.jiangzg.ita.third.GlideUtils;
import com.jiangzg.ita.third.RecyclerManager;
import com.jiangzg.ita.third.RecyclerMoreView;
import com.jiangzg.ita.utils.TimeUtils;
import com.jiangzg.ita.utils.ViewUtils;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SuggestDetailActivity extends BaseActivity<SuggestDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fabComment)
    FloatingActionButton fabComment;

    private Suggest suggest;
    private RecyclerManager recyclerManager;

    public static void goActivity(Activity from, Suggest suggest) {
        Intent intent = new Intent(from, SuggestDetailActivity.class);
        intent.putExtra("suggest", suggest);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_detail;
    }

    @Override
    protected void initView(Bundle state) {
        suggest = (Suggest) getIntent().getSerializableExtra("suggest");
        String title = (suggest == null) ? getString(R.string.suggest_feedback) : suggest.getTitle();
        ViewUtils.initTopBar(mActivity, tb, title, true);
        // recycler
        recyclerManager = new RecyclerManager(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl)
                .initAdapter(new SuggestCommentAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_common, true, true)
                .viewLoadMore(new RecyclerMoreView())
                .setAdapter()
                .listenerRefresh(new RecyclerManager.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData(false);
                    }
                })
                .listenerMore(new RecyclerManager.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getData(true);
                    }
                });
        // menu
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 评论
                        HelpActivity.goActivity(mActivity, Help.TYPE_SUGGEST_DETAIL);
                        break;
                    case R.id.menuTop: // 回到顶部
                        rv.smoothScrollToPosition(0);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void initData(Bundle state) {
        recyclerManager.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @OnClick({R.id.fabComment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabComment: // todo 评论
                ToastUtils.show("点击");
                break;
        }
    }

    private void getData(final boolean more) {
        // todo tpi
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                rv.setVisibility(View.VISIBLE);

                suggest.setContentText("这是一个很不好的消息，你们的产品太差了，真的不好。这是一个很不好的消息，你们的产品太差了，真的不好。这是一个很不好的消息，你们的产品太差了，真的不好。这是一个很不好的消息，你们的产品太差了，真的不好。");
                suggest.setContentImgUrl("https://timgsa.baidu.com/timg?image");
                if (!more) { // 在请求成功里执行
                    fabComment.setVisibility(View.VISIBLE);
                    recyclerManager.viewHeader(R.layout.list_head_suggest_comment);
                    initHead();
                }

                List<SuggestComment> commentList = new ArrayList<>();
                if (more) {
                    SuggestComment c1 = new SuggestComment();
                    c1.setCreatedAt(1520866299);
                    c1.setOfficial(false);
                    c1.setMine(false);
                    c1.setContentText("上拉刷新出来的");
                    commentList.add(c1);
                } else {
                    SuggestComment c1 = new SuggestComment();
                    c1.setCreatedAt(1521104791);
                    c1.setOfficial(true);
                    c1.setMine(false);
                    c1.setContentText("官方已采纳，多谢亲的一片心意！");
                    SuggestComment c2 = new SuggestComment();
                    c2.setCreatedAt(1520866299);
                    c2.setOfficial(false);
                    c2.setMine(true);
                    c2.setContentText("我觉得可以接受");
                    SuggestComment c3 = new SuggestComment();
                    c3.setCreatedAt(1520866299);
                    c3.setOfficial(false);
                    c3.setMine(false);
                    c3.setContentText("我觉得是个不错的注意，我觉得是个不错的注意，我觉得");
                    commentList.add(c1);
                    commentList.add(c2);
                    commentList.add(c3);
                    commentList.add(c3);
                    commentList.add(c3);
                    commentList.add(c3);
                    commentList.add(c3);
                    commentList.add(c3);
                    commentList.add(c3);
                    commentList.add(c3);
                }
                suggest.setCommentList(commentList);
                recyclerManager.data(commentList, 13, more);
                recyclerManager.viewEmptyShow();
            }
        }, 1000);
    }

    private void initHead() {
        // data
        String title = suggest.getTitle();
        String create = TimeUtils.getDiffDayShowBySecond(suggest.getCreatedAt());
        String createShow = String.format(getString(R.string.create_at_colon_holder), create);
        String contentImgUrl = suggest.getContentImgUrl();
        String contentText = suggest.getContentText();
        String commentTotal = String.format(getString(R.string.comment_colon_holder), suggest.getCommentCount());
        // view todo 图片加载框架 图片显示控件+点击放大
        View head = recyclerManager.getViewHead();
        TextView tvTitle = head.findViewById(R.id.tvTitle);
        TextView tvCreateAt = head.findViewById(R.id.tvCreateAt);
        ImageView ivContent = head.findViewById(R.id.ivContent);
        TextView tvContent = head.findViewById(R.id.tvContent);
        TextView tvCommentTotal = head.findViewById(R.id.tvCommentTotal);
        tvTitle.setText(title);
        tvCreateAt.setText(createShow);
        GlideUtils.load(mActivity, contentImgUrl, ivContent);
        tvContent.setText(contentText);
        tvCommentTotal.setText(commentTotal);
    }

}
