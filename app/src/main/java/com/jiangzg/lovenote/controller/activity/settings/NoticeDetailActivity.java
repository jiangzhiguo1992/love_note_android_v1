package com.jiangzg.lovenote.controller.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.entity.Notice;

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
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.new_notice), true);
        // init
        Notice notice = intent.getParcelableExtra("notice");
        if (notice == null) return;
        // view
        tvTitle.setText(notice.getTitle());
        tvTime.setText(TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(notice.getCreateAt()));
        String content = notice.getContentText().replace("\\n", "\n"); // 换行问题
        tvContent.setText(content);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

}
