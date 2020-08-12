package com.jiangzg.lovenote.controller.activity.topic;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.controller.adapter.common.CommonFragmentAdapter;
import com.jiangzg.lovenote.controller.fragment.base.BaseFragment;
import com.jiangzg.lovenote.controller.fragment.topic.PostCollectFragment;
import com.jiangzg.lovenote.controller.fragment.topic.PostMineFragment;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class PostMineActivity extends BaseActivity<PostMineActivity> {

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
        Intent intent = new Intent(from.getActivity(), PostMineActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from, PostMineActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_post_mine;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.my_relation), true);
        // fragment
        List<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.my_push));
        titleList.add(getString(R.string.my_collect));
        titleList.add(getString(R.string.ta_collect));
        List<BaseFragment> fragmentList = new ArrayList<>();
        PostMineFragment postMineFragment = PostMineFragment.newFragment();
        PostCollectFragment meCollectFragment = PostCollectFragment.newFragment(true);
        PostCollectFragment taCollectFragment = PostCollectFragment.newFragment(false);
        fragmentList.add(postMineFragment);
        fragmentList.add(meCollectFragment);
        fragmentList.add(taCollectFragment);
        // adapter
        CommonFragmentAdapter<BaseFragment> adapter = new CommonFragmentAdapter<>(getSupportFragmentManager());
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
