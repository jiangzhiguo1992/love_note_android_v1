package com.jiangzg.lovenote.activity.more;

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
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.adapter.FragmentPagerAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.base.BasePagerFragment;
import com.jiangzg.lovenote.domain.Help;
import com.jiangzg.lovenote.domain.MatchPeriod;
import com.jiangzg.lovenote.fragment.more.MatchLetterListFragment;
import com.jiangzg.lovenote.fragment.more.MatchPeriodListFragment;
import com.jiangzg.lovenote.helper.ViewHelper;

import butterknife.BindView;

public class MatchLetterActivity extends BaseActivity<MatchLetterActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tl)
    TabLayout tl;
    @BindView(R.id.vpFragment)
    ViewPager vpFragment;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), MatchLetterActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, MatchLetterActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_match_letter;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_letter), true);
        // fragment
        MatchPeriodListFragment periodListFragment = MatchPeriodListFragment.newFragment(MatchPeriod.MATCH_KIND_LETTER_SHOW);
        MatchLetterListFragment letterListFragment = MatchLetterListFragment.newFragment();
        // adapter
        FragmentPagerAdapter<BasePagerFragment> adapter = new FragmentPagerAdapter<>(getSupportFragmentManager());
        adapter.addData(getString(R.string.old_period), periodListFragment);
        adapter.addData(getString(R.string.we_de), letterListFragment);
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
                HelpActivity.goActivity(mActivity, Help.INDEX_MORE_MATCH);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
