package com.jiangzg.lovenote.controller.activity.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.common.CommonFragmentAdapter;
import com.jiangzg.lovenote.controller.fragment.base.BasePagerFragment;
import com.jiangzg.lovenote.controller.fragment.main.CoupleFragment;
import com.jiangzg.lovenote.controller.fragment.main.MoreFragment;
import com.jiangzg.lovenote.controller.fragment.main.NoteFragment;
import com.jiangzg.lovenote.controller.fragment.main.TopicFragment;
import com.jiangzg.lovenote.controller.service.UpdateService;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.model.entity.ModelShow;
import com.jiangzg.lovenote.model.entity.Version;
import com.jiangzg.lovenote.view.HomePaper;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;

public class HomeActivity extends BaseActivity<HomeActivity> {

    @BindView(R.id.vpContent)
    HomePaper vpContent;
    @BindView(R.id.bnvBottom)
    BottomNavigationView bnvBottom;

    private ModelShow modelShow;
    private int[] menuIdArray;
    private CommonFragmentAdapter<BasePagerFragment> pagerAdapter;
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
        modelShow = SPHelper.getModelShow();
        return R.layout.activity_home;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        // menu
        menuIdArray = getMenuIdArray();
        Menu menu = bnvBottom.getMenu();
        if (!modelShow.isCouple()) {
            menu.removeItem(R.id.menuCouple);
        }
        if (!modelShow.isNote()) {
            menu.removeItem(R.id.menuNote);
        }
        if (!modelShow.isTopic()) {
            menu.removeItem(R.id.menuTopic);
        }
        if (!modelShow.isMore()) {
            menu.removeItem(R.id.menuMore);
        }
        // fragment
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
        // 更新
        Version version = SPHelper.getVersion();
        if (version != null) {
            ArrayList<Version> list = new ArrayList<>();
            list.add(version);
            UpdateService.showUpdateDialog(list);
        }
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
            boolean couple = modelShow.isCouple();
            boolean note = modelShow.isNote();
            boolean topic = modelShow.isTopic();
            boolean more = modelShow.isMore();
            FragmentManager manager = mActivity.getSupportFragmentManager();
            pagerAdapter = new CommonFragmentAdapter<>(manager);
            int index = -1;
            if (couple) {
                ++index;
                pagerAdapter.addData(index, null, coupleFragment);
            }
            if (note) {
                ++index;
                pagerAdapter.addData(index, null, noteFragment);
            }
            if (topic) {
                ++index;
                pagerAdapter.addData(index, null, topicFragment);
            }
            if (more) {
                ++index;
                pagerAdapter.addData(index, null, moreFragment);
            }
        }
    }

    private void initViewPagerListener() {
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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
        bnvBottom.setOnNavigationItemSelectedListener(item -> {
            changeFragment(item.getItemId());
            return true;
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

    private int[] getMenuIdArray() {
        boolean couple = modelShow.isCouple();
        boolean note = modelShow.isNote();
        boolean topic = modelShow.isTopic();
        boolean more = modelShow.isMore();
        // 开始计算
        int length = 0;
        List<Integer> menuList = new ArrayList<>();
        if (couple) {
            ++length;
            menuList.add(R.id.menuCouple);
        }
        if (note) {
            ++length;
            menuList.add(R.id.menuNote);
        }
        if (topic) {
            ++length;
            menuList.add(R.id.menuTopic);
        }
        if (more) {
            ++length;
            menuList.add(R.id.menuMore);
        }
        int[] menuIdArray = new int[length];
        for (int i = 0; i < length; i++) {
            menuIdArray[i] = menuList.get(i);
        }
        return menuIdArray;
    }

}
