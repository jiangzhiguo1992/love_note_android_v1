package com.jiangzg.lovenote.controller.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.adapter.common.CommonFragmentAdapter;
import com.jiangzg.lovenote.controller.fragment.base.BasePagerFragment;
import com.jiangzg.lovenote.controller.fragment.more.MatchPeriodListFragment;
import com.jiangzg.lovenote.controller.fragment.more.MatchWifeListFragment;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.entity.MatchPeriod;

import butterknife.BindView;

public class MatchWifeActivity extends BaseActivity<MatchWifeActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tl)
    TabLayout tl;
    @BindView(R.id.vpFragment)
    ViewPager vpFragment;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), MatchWifeActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, MatchWifeActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_match_wife;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_wife), true);
        // fragment
        MatchPeriodListFragment periodListFragment = MatchPeriodListFragment.newFragment(MatchPeriod.MATCH_KIND_WIFE_PICTURE);
        MatchWifeListFragment wifeListFragment = MatchWifeListFragment.newFragment();
        // adapter
        CommonFragmentAdapter<BasePagerFragment> adapter = new CommonFragmentAdapter<>(getSupportFragmentManager());
        adapter.addData(getString(R.string.old_period), periodListFragment);
        adapter.addData(getString(R.string.we_de), wifeListFragment);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_MORE_MATCH);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
