package com.jiangzg.lovenote.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.MatchPeriod;

public class MatchLetterListActivity extends BaseActivity<MatchLetterListActivity> {

    public static void goActivity(Fragment from, MatchPeriod period) {
        Intent intent = new Intent(from.getActivity(), MatchLetterListActivity.class);
        intent.putExtra("period", period);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_match_letter_list;
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
