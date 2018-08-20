package com.jiangzg.lovenote.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.MatchPeriod;

public class MatchDiscussListActivity extends BaseActivity<MatchDiscussListActivity> {

    public static void goActivity(Fragment from, MatchPeriod period) {
        Intent intent = new Intent(from.getActivity(), MatchDiscussListActivity.class);
        intent.putExtra("period", period);
        // TODO
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, MatchPeriod period) {
        Intent intent = new Intent(from, MatchDiscussListActivity.class);
        intent.putExtra("period", period);
        // TODO
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_match_discuss_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {

    }

    @Override
    protected void initData(Intent intent, Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

}
