package com.jiangzg.ita.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Diary;
import com.jiangzg.ita.helper.ConvertHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

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
        String happenAt = ConvertHelper.ConvertTimeGo2DiaryShow(diary.getHappenAt());
        ViewHelper.initTopBar(mActivity, tb, happenAt, true);

    }

    @Override
    protected void initData(Bundle state) {

    }

}
