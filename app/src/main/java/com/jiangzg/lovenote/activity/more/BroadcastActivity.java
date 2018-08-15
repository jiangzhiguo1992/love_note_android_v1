package com.jiangzg.lovenote.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Broadcast;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;

import java.util.Locale;

import butterknife.BindView;

public class BroadcastActivity extends BaseActivity<BroadcastActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvStart)
    TextView tvStart;
    @BindView(R.id.tvEnd)
    TextView tvEnd;
    @BindView(R.id.tvContent)
    TextView tvContent;

    public static void goActivity(Activity from, Broadcast broadcast) {
        Intent intent = new Intent(from, BroadcastActivity.class);
        intent.putExtra("broadcast", broadcast);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_broadcast;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.broadcast), true);
        // init
        Broadcast broadcast = intent.getParcelableExtra("broadcast");
        if (broadcast == null) return;
        // data
        String title = broadcast.getTitle();
        String start = broadcast.getStartAt() == 0 ? getString(R.string.nil) : TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(broadcast.getStartAt());
        String startShow = String.format(Locale.getDefault(), getString(R.string.start_time_colon_holder), start);
        String end = broadcast.getEndAt() == 0 ? getString(R.string.nil) : TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(broadcast.getEndAt());
        String endShow = String.format(Locale.getDefault(), getString(R.string.end_time_colon_holder), end);
        String content = broadcast.getContentText().replace("\\n", "\n"); // 换行问题
        // view
        tvTitle.setText(title);
        tvStart.setText(startShow);
        tvEnd.setText(endShow);
        tvContent.setText(content);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

}
