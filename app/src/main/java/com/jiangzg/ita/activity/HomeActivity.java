package com.jiangzg.ita.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.CommonFragmentPagerAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.domain.Version;
import com.jiangzg.ita.fragment.BookFragment;
import com.jiangzg.ita.fragment.SquareFragment;
import com.jiangzg.ita.fragment.TopicFragment;
import com.jiangzg.ita.fragment.WeFragment;
import com.jiangzg.ita.service.UpdateService;

import java.util.ArrayList;
import java.util.Stack;

import butterknife.BindView;

public class HomeActivity extends BaseActivity<HomeActivity> {

    private static final String LOG_TAG = "HomeActivity";

    @BindView(R.id.vpContent)
    ViewPager vpContent;
    @BindView(R.id.bnvBottom)
    BottomNavigationView bnvBottom;

    private int[] menuIdArray = new int[]{R.id.menuWe, R.id.menuBook, R.id.menuTopic, R.id.menuSquare};
    private CommonFragmentPagerAdapter<BasePagerFragment> pagerAdapter;
    private WeFragment weFragment;
    private BookFragment bookFragment;
    private TopicFragment topicFragment;
    private SquareFragment squareFragment;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, HomeActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 清除栈顶,类似于singTask
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, ArrayList<Version> versionList) {
        Intent intent = new Intent(from, HomeActivity.class);
        intent.putParcelableArrayListExtra("versionList", versionList);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        // we界面采用沉浸式，其余界面自己填充statusBar
        BarUtils.setStatusBarTrans(mActivity, true);
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Bundle state) {
        // viewPager
        initFragment();
        initViewPagerAdapter();
        initViewPagerListener();
        initViewPager();
        // navBottom
        initNavBottom();
    }

    @Override
    protected void initData(Bundle state) {
        checkUpdate();
    }

    // 关闭其他栈底activity，栈顶由singleTask来关闭
    @Override
    protected void onStart() {
        super.onStart();
        Stack<Activity> stack = ActivityStack.getStack();
        for (Activity activity : stack) {
            if (activity != mActivity) {
                activity.finish();
            }
        }
    }

    private void initFragment() {
        if (weFragment == null) {
            weFragment = WeFragment.newFragment();
        }
        if (bookFragment == null) {
            bookFragment = BookFragment.newFragment();
        }
        if (topicFragment == null) {
            topicFragment = TopicFragment.newFragment();
        }
        if (squareFragment == null) {
            squareFragment = SquareFragment.newFragment();
        }
    }

    private void initViewPagerAdapter() {
        if (pagerAdapter == null) {
            FragmentManager manager = mActivity.getSupportFragmentManager();
            pagerAdapter = new CommonFragmentPagerAdapter<>(manager);
            pagerAdapter.addData(0, null, weFragment);
            pagerAdapter.addData(1, null, bookFragment);
            pagerAdapter.addData(2, null, topicFragment);
            pagerAdapter.addData(3, null, squareFragment);
        }
        if (vpContent.getAdapter() == null) {
            vpContent.setAdapter(pagerAdapter);
        }
    }

    private void initViewPagerListener() {
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //LogUtils.i(LOG_TAG, "onPageScrolled: " + position);
            }

            @Override
            public void onPageSelected(int position) {
                LogUtils.i(LOG_TAG, "onPageSelected: " + position);
                bnvBottom.setSelectedItemId(menuIdArray[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                LogUtils.i(LOG_TAG, "onPageScrollStateChanged: " + state);
            }
        });
    }

    private void initViewPager() {
        // 预加载全部，防止刷新
        vpContent.setOffscreenPageLimit(menuIdArray.length - 1);
    }

    private void initNavBottom() {
        bnvBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                changeFragment(item.getItemId());
                return true;
            }
        });
        bnvBottom.setSelectedItemId(menuIdArray[0]);
    }

    private void changeFragment(int menuId) {
        int selectItemIndex = 0;
        for (int i = 0; i < menuIdArray.length; i++) {
            if (menuId == menuIdArray[i]) {
                selectItemIndex = i;
                break;
            }
        }
        vpContent.setCurrentItem(selectItemIndex, true);
    }

    private void checkUpdate() {
        ArrayList<Version> versionList = getIntent().getParcelableArrayListExtra("versionList");
        if (versionList == null || versionList.size() <= 0) return;
        UpdateService.showUpdateDialog(versionList);
    }

}
