package com.jiangzg.ita.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.jiangzg.base.component.activity.ActivityStack;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.BaseFragmentPagerAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.fragment.BookFragment;
import com.jiangzg.ita.fragment.SquareFragment;
import com.jiangzg.ita.fragment.TopicFragment;
import com.jiangzg.ita.fragment.WeFragment;
import com.jiangzg.ita.utils.UserPreference;

import java.util.Stack;

import butterknife.BindView;

public class HomeActivity extends BaseActivity<HomeActivity> {

    @BindView(R.id.vpContent)
    ViewPager vpContent;
    @BindView(R.id.bnvBottom)
    BottomNavigationView bnvBottom;

    private int[] menuIdArray = new int[]{R.id.menu_we, R.id.menu_book, R.id.menu_topic, R.id.menu_square};

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, HomeActivity.class);
        // intent.putExtra();
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        //BarUtils.setBarTrans(mActivity, false);
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Bundle state) {
        // viewPager
        BaseFragmentPagerAdapter<BasePagerFragment> viewPagerAdapter = getViewPagerAdapter();
        vpContent.setAdapter(viewPagerAdapter);
        vpContent.setOffscreenPageLimit(0);
        // listener
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //LogUtils.e(position + "-->" + positionOffset + "<--" + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                bnvBottom.setSelectedItemId(menuIdArray[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bnvBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                changeFragment2(item.getItemId());
                return true;
            }
        });
        // firstFragment
        bnvBottom.setSelectedItemId(menuIdArray[0]);

    }

    @Override
    protected void initData(Bundle state) {
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
        initUser();
    }

    public BaseFragmentPagerAdapter<BasePagerFragment> getViewPagerAdapter() {
        FragmentManager manager = mActivity.getSupportFragmentManager();
        BaseFragmentPagerAdapter<BasePagerFragment> fragmentAdapter = new BaseFragmentPagerAdapter<>(manager);

        WeFragment weFragment = WeFragment.newFragment();
        BookFragment bookFragment = BookFragment.newFragment();
        TopicFragment topicFragment = TopicFragment.newFragment();
        SquareFragment squareFragment = SquareFragment.newFragment();

        fragmentAdapter.addData("", weFragment);
        fragmentAdapter.addData("", bookFragment);
        fragmentAdapter.addData("", topicFragment);
        fragmentAdapter.addData("", squareFragment);
        return fragmentAdapter;
    }

    private void changeFragment2(int menuId) {
        int selectItemIndex = 0;
        for (int i = 0; i < menuIdArray.length; i++) {
            if (menuId == menuIdArray[i]) {
                selectItemIndex = i;
                break;
            }
        }
        vpContent.setCurrentItem(selectItemIndex, true);
    }

    private void initUser() {
        if (UserPreference.noLogin()) {
            //LoginActivity.goActivity(mActivity);
        }
    }

}
