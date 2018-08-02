package com.jiangzg.mianmian.activity;

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
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.adapter.FragmentPagerAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.base.BasePagerFragment;
import com.jiangzg.mianmian.fragment.NoteFragment;
import com.jiangzg.mianmian.fragment.CoupleFragment;
import com.jiangzg.mianmian.fragment.MoreFragment;
import com.jiangzg.mianmian.fragment.TopicFragment;
import com.jiangzg.mianmian.view.HomePaper;

import java.util.Stack;

import butterknife.BindView;

public class HomeActivity extends BaseActivity<HomeActivity> {

    @BindView(R.id.vpContent)
    HomePaper vpContent;
    @BindView(R.id.bnvBottom)
    BottomNavigationView bnvBottom;

    private int[] menuIdArray = new int[]{R.id.menuCouple, R.id.menuNote, R.id.menuTopic, R.id.menuMore};
    private FragmentPagerAdapter<BasePagerFragment> pagerAdapter;
    private CoupleFragment coupleFragment;
    private NoteFragment noteFragment;
    private TopicFragment topicFragment;
    private MoreFragment moreFragment;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, HomeActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // 清除栈顶,类似于singTask
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        // we界面采用沉浸式，其余界面自己填充statusBar
        BarUtils.setStatusBarTrans(mActivity, true);
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        initFragment();
        // viewPager
        initViewPagerAdapter();
        initViewPagerListener();
        initViewPager();
        // navBottom
        initNavBottom();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
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
        if (coupleFragment == null) {
            coupleFragment = CoupleFragment.newFragment();
        }
        if (noteFragment == null) {
            noteFragment = NoteFragment.newFragment();
        }
        if (topicFragment == null) {
            topicFragment = TopicFragment.newFragment();
        }
        if (moreFragment == null) {
            moreFragment = MoreFragment.newFragment();
        }
    }

    private void initViewPagerAdapter() {
        if (pagerAdapter == null) {
            FragmentManager manager = mActivity.getSupportFragmentManager();
            pagerAdapter = new FragmentPagerAdapter<>(manager);
            pagerAdapter.addData(0, null, coupleFragment);
            pagerAdapter.addData(1, null, noteFragment);
            pagerAdapter.addData(2, null, topicFragment);
            pagerAdapter.addData(3, null, moreFragment);
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
                LogUtils.i(HomeActivity.class, "onPageSelected", String.valueOf(position));
                bnvBottom.setSelectedItemId(menuIdArray[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                LogUtils.i(HomeActivity.class, "onPageScrollStateChanged", String.valueOf(state));
            }
        });
    }

    private void initViewPager() {
        if (vpContent.getAdapter() == null) {
            vpContent.setAdapter(pagerAdapter);
        }
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

}
