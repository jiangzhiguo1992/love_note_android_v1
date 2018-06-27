package com.jiangzg.mianmian.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Notice;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import butterknife.BindView;

public class NoticeDetailActivity extends BaseActivity<NoticeDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvTime)
    TextView tvTime;
    @BindView(R.id.tvContent)
    TextView tvContent;

    public static void goActivity(Activity from, Notice notice) {
        Intent intent = new Intent(from, NoticeDetailActivity.class);
        intent.putExtra("notice", notice);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_notice_detail;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.new_notice), true);
    }

    @Override
    protected void initData(Bundle state) {
        Notice notice = getIntent().getParcelableExtra("notice");
        if (notice == null) return;
        // data
        String title = notice.getTitle();
        long createAt = notice.getCreateAt();
        String time = TimeHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(createAt);
        String contentText = notice.getContentText();
        // view
        tvTitle.setText(title);
        tvTime.setText(time);
        tvContent.setText(contentText);
    }

}
