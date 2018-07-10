package com.jiangzg.mianmian.activity.book;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.FragmentPagerAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.fragment.SouvenirListDoneFragment;
import com.jiangzg.mianmian.fragment.SouvenirListWishFragment;
import com.jiangzg.mianmian.helper.ViewHelper;

import butterknife.BindView;

public class SouvenirListActivity extends BaseActivity<SouvenirListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tl)
    TabLayout tl;
    @BindView(R.id.vpFragment)
    ViewPager vpFragment;

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
        // TODO 滚动隐藏tb
        // fragment
        SouvenirListDoneFragment souvenirListDoneFragment = SouvenirListDoneFragment.newFragment();
        SouvenirListWishFragment souvenirListWishFragment = SouvenirListWishFragment.newFragment();
        // adapter
        FragmentPagerAdapter<BaseFragment> adapter = new FragmentPagerAdapter<>(getSupportFragmentManager());
        adapter.addData(title, souvenirListDoneFragment);
        adapter.addData(getString(R.string.wish_list), souvenirListWishFragment);
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
                HelpActivity.goActivity(mActivity, Help.INDEX_BOOK_SOUVENIR_LIST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
