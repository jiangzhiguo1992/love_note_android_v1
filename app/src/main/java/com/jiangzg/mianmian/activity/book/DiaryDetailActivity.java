package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;

public class DiaryDetailActivity extends BaseActivity<DiaryDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvAuthor)
    TextView tvAuthor;
    @BindView(R.id.tvUpdateAt)
    TextView tvUpdateAt;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tvContent)
    TextView tvContent;

    private Diary diary;

    public static void goActivity(Activity from, Diary diary) {
        Intent intent = new Intent(from, DiaryDetailActivity.class);
        intent.putExtra("diary", diary);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_diary_detail;
    }

    @Override
    protected void initView(Bundle state) {
        diary = getIntent().getParcelableExtra("diary");
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.diary), true);
        // view
        refreshView();
        // TODO menu
    }

    @Override
    protected void initData(Bundle state) {
        // TODO event
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO
        return super.onCreateOptionsMenu(menu);
    }

    private void refreshData() {
        // TODO
    }

    private void refreshView() {
        User user = SPHelper.getUser();
        Couple couple = SPHelper.getCouple();
        long userId = diary.getUserId();
        long updateAt = diary.getUpdateAt();

        // happen
        String happenAt = ConvertHelper.ConvertTimeGo2DiaryShow(diary.getHappenAt());
        tb.setTitle(happenAt);
        // author
        String authorName = ConvertHelper.convertCp2Name(couple, userId);
        String authorShow = String.format(Locale.getDefault(), getString(R.string.author_space_colon_space_holder), authorName);
        tvAuthor.setText(authorShow);
        // updateAt
        String update = ConvertHelper.ConvertTimeGo2DiaryShow(updateAt);
        String updateShow = String.format(Locale.getDefault(), getString(R.string.update_at_colon_space_holder), update);
        tvUpdateAt.setText(updateShow);
        // TODO imageList
        List<String> imageList = diary.getImageList();
        if (imageList != null && imageList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
        } else {
            rv.setVisibility(View.GONE);
        }
        // content
        tvContent.setText(diary.getContent());
    }

    private void showDeleteDialog() {
        // TODO
    }

    private void goEdit() {
        // TODO
    }

}
