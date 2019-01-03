package com.jiangzg.lovenote.activity.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.base.BaseActivity;
import com.jiangzg.lovenote.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.adapter.FragmentPagerAdapter;
import com.jiangzg.lovenote.fragment.topic.PostCollectFragment;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class PostCollectActivity extends BaseActivity<PostCollectActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tl)
    TabLayout tl;
    @BindView(R.id.vpFragment)
    ViewPager vpFragment;

    public static void goActivity(Fragment from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), PostCollectActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from, PostCollectActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_post_collect;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.our_collect), true);
        // fragment
        List<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.me_de));
        titleList.add(getString(R.string.ta_de));
        List<PostCollectFragment> fragmentList = new ArrayList<>();
        PostCollectFragment meCollectFragment = PostCollectFragment.newFragment(true);
        PostCollectFragment taCollectFragment = PostCollectFragment.newFragment(false);
        fragmentList.add(meCollectFragment);
        fragmentList.add(taCollectFragment);
        // adapter
        FragmentPagerAdapter<PostCollectFragment> adapter = new FragmentPagerAdapter<>(getSupportFragmentManager());
        adapter.newData(titleList, fragmentList);
        // view
        vpFragment.setOffscreenPageLimit(fragmentList.size());
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
