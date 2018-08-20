package com.jiangzg.lovenote.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.FragmentPagerAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.base.BasePagerFragment;
import com.jiangzg.lovenote.domain.MatchPeriod;
import com.jiangzg.lovenote.fragment.more.MatchDiscussListFragment;
import com.jiangzg.lovenote.fragment.more.MatchPeriodListFragment;
import com.jiangzg.lovenote.helper.ViewHelper;

import butterknife.BindView;

public class MatchDiscussActivity extends BaseActivity<MatchDiscussActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tl)
    TabLayout tl;
    @BindView(R.id.vpFragment)
    ViewPager vpFragment;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), MatchDiscussActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, MatchDiscussActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_match_discuss;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_discuss), true);
        // fragment
        MatchPeriodListFragment periodListFragment = MatchPeriodListFragment.newFragment(MatchPeriod.MATCH_KIND_DISCUSS_MEET);
        MatchDiscussListFragment discussListFragment = MatchDiscussListFragment.newFragment();
        // adapter
        FragmentPagerAdapter<BasePagerFragment> adapter = new FragmentPagerAdapter<>(getSupportFragmentManager());
        adapter.addData(getString(R.string.old_period), periodListFragment);
        adapter.addData(getString(R.string.we_de), discussListFragment);
        // view
        vpFragment.setOffscreenPageLimit(1);
        vpFragment.setAdapter(adapter);
        tl.setupWithViewPager(vpFragment);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

}
