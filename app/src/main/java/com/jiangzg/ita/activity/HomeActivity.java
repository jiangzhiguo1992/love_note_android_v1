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
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.GFragmentPagerAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.fragment.BookFragment;
import com.jiangzg.ita.fragment.PairFragment;
import com.jiangzg.ita.fragment.SquareFragment;
import com.jiangzg.ita.fragment.TopicFragment;
import com.jiangzg.ita.fragment.WeFragment;
import com.jiangzg.ita.third.RxBus;
import com.jiangzg.ita.utils.Constants;

import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import rx.Observable;
import rx.functions.Action1;

public class HomeActivity extends BaseActivity<HomeActivity> {

    @BindView(R.id.vpContent)
    ViewPager vpContent;
    @BindView(R.id.bnvBottom)
    BottomNavigationView bnvBottom;

    private int[] menuIdArray = new int[]{R.id.menuWe, R.id.menuBook, R.id.menuTopic, R.id.menuSquare};
    private Observable<Couple> coupleObservable;
    private GFragmentPagerAdapter<BasePagerFragment> pagerAdapter;

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
        changePagerAdapter();
        vpContent.setOffscreenPageLimit(menuIdArray.length - 1);
        // listener
        vpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //LogUtils.e(position + "-->" + positionOffset + "<--" + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                bnvBottom.setSelectedItemId(menuIdArray[position]);
                onTabChange();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bnvBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                changeFragment(item.getItemId());
                return true;
            }
        });
        // firstFragment
        bnvBottom.setSelectedItemId(menuIdArray[0]);
        onTabChange();
        // event
        coupleObservable = RxBus.register(Constants.EVENT_COUPLE, new Action1<Couple>() {
            @Override
            public void call(Couple couple) {
                changePagerAdapter();
            }
        });
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.unregister(Constants.EVENT_COUPLE, coupleObservable);
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

    public void changePagerAdapter() {
        if (pagerAdapter == null) {
            FragmentManager manager = mActivity.getSupportFragmentManager();
            pagerAdapter = new GFragmentPagerAdapter<>(manager);
        }
        // fragmentList的size是0或4
        List<BasePagerFragment> fragmentList = pagerAdapter.getFragmentList();
        if (fragmentList == null || fragmentList.size() <= 0) {
            pagerAdapter.addData(null, BookFragment.newFragment());
            pagerAdapter.addData(null, TopicFragment.newFragment());
            pagerAdapter.addData(null, SquareFragment.newFragment());
        } else {
            pagerAdapter.removeData(0);
        }
        // 这里之后fragmentList的size是3
        // todo if (PreferenceUser.noCouple()) {
        if (false) {
            pagerAdapter.addData(0, null, PairFragment.newFragment());
        } else {
            pagerAdapter.addData(0, null, WeFragment.newFragment());
        }
        // 底下一个都不能少
        pagerAdapter.notifyDataSetChanged();
        vpContent.setAdapter(pagerAdapter);
    }

    private void onTabChange() {
        boolean trans = (vpContent.getCurrentItem() == 0); // todo pairFragment不要忘记屏蔽
        BarUtils.setStatusBarTrans(mActivity, trans);
    }

}
