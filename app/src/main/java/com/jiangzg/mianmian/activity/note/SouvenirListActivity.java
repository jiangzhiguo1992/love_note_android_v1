package com.jiangzg.mianmian.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.FragmentPagerAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.fragment.SouvenirListFragment;
import com.jiangzg.mianmian.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class SouvenirListActivity extends BaseActivity<SouvenirListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tl)
    TabLayout tl;
    @BindView(R.id.vpFragment)
    ViewPager vpFragment;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SouvenirListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), SouvenirListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_souvenir_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        String title = getString(R.string.souvenir);
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // fragment
        SouvenirListFragment souvenirDoneFragment = SouvenirListFragment.newFragment(true);
        SouvenirListFragment souvenirWishFragment = SouvenirListFragment.newFragment(false);
        // adapter
        FragmentPagerAdapter<SouvenirListFragment> adapter = new FragmentPagerAdapter<>(getSupportFragmentManager());
        adapter.addData(title, souvenirDoneFragment);
        adapter.addData(getString(R.string.wish_list), souvenirWishFragment);
        // view
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
                HelpActivity.goActivity(mActivity, Help.INDEX_NOTE_SOUVENIR);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd: // 添加
                SouvenirEditActivity.goActivity(mActivity);
                break;
        }
    }

}
