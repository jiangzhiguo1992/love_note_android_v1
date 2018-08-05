package com.jiangzg.lovenote.activity.topic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.FragmentPagerAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.TopicMessage;
import com.jiangzg.lovenote.fragment.TopicMessageFragment;
import com.jiangzg.lovenote.helper.ViewHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class TopicMessageActivity extends BaseActivity<TopicMessageActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tl)
    TabLayout tl;
    @BindView(R.id.vpFragment)
    ViewPager vpFragment;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), TopicMessageActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_topic_message;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.my_message), true);
        // fragment
        List<String> titleList = new ArrayList<>();
        titleList.add(getString(R.string.all));
        titleList.add(getString(R.string.official));
        titleList.add(getString(R.string.jab_in_post));
        titleList.add(getString(R.string.jab_in_comment));
        titleList.add(getString(R.string.post_report));
        titleList.add(getString(R.string.post_point));
        titleList.add(getString(R.string.post_collect));
        titleList.add(getString(R.string.post_comment));
        titleList.add(getString(R.string.comment_reply));
        titleList.add(getString(R.string.comment_report));
        titleList.add(getString(R.string.comment_point));
        List<TopicMessageFragment> fragmentList = new ArrayList<>();
        TopicMessageFragment all = TopicMessageFragment.newFragment(TopicMessage.KIND_ALL);
        TopicMessageFragment official = TopicMessageFragment.newFragment(TopicMessage.KIND_OFFICIAL_TEXT);
        TopicMessageFragment jabPost = TopicMessageFragment.newFragment(TopicMessage.KIND_JAB_IN_POST);
        TopicMessageFragment jabComment = TopicMessageFragment.newFragment(TopicMessage.KIND_JAB_IN_COMMENT);
        TopicMessageFragment postReport = TopicMessageFragment.newFragment(TopicMessage.KIND_POST_BE_REPORT);
        TopicMessageFragment postPoint = TopicMessageFragment.newFragment(TopicMessage.KIND_POST_BE_POINT);
        TopicMessageFragment postCollect = TopicMessageFragment.newFragment(TopicMessage.KIND_POST_BE_COLLECT);
        TopicMessageFragment postComment = TopicMessageFragment.newFragment(TopicMessage.KIND_POST_BE_COMMENT);
        TopicMessageFragment commentReply = TopicMessageFragment.newFragment(TopicMessage.KIND_COMMENT_BE_REPLY);
        TopicMessageFragment commentReport = TopicMessageFragment.newFragment(TopicMessage.KIND_COMMENT_BE_REPORT);
        TopicMessageFragment commentPoint = TopicMessageFragment.newFragment(TopicMessage.KIND_COMMENT_BE_POINT);
        fragmentList.add(all);
        fragmentList.add(official);
        fragmentList.add(jabPost);
        fragmentList.add(jabComment);
        fragmentList.add(postReport);
        fragmentList.add(postPoint);
        fragmentList.add(postCollect);
        fragmentList.add(postComment);
        fragmentList.add(commentReply);
        fragmentList.add(commentReport);
        fragmentList.add(commentPoint);
        // adapter
        FragmentPagerAdapter<TopicMessageFragment> adapter = new FragmentPagerAdapter<>(getSupportFragmentManager());
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
