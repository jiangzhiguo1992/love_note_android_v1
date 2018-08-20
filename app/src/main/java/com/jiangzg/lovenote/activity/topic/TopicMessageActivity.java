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
import com.jiangzg.lovenote.fragment.topic.MessageListFragment;
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
        List<MessageListFragment> fragmentList = new ArrayList<>();
        MessageListFragment all = MessageListFragment.newFragment(TopicMessage.KIND_ALL);
        MessageListFragment official = MessageListFragment.newFragment(TopicMessage.KIND_OFFICIAL_TEXT);
        MessageListFragment jabPost = MessageListFragment.newFragment(TopicMessage.KIND_JAB_IN_POST);
        MessageListFragment jabComment = MessageListFragment.newFragment(TopicMessage.KIND_JAB_IN_COMMENT);
        MessageListFragment postReport = MessageListFragment.newFragment(TopicMessage.KIND_POST_BE_REPORT);
        MessageListFragment postPoint = MessageListFragment.newFragment(TopicMessage.KIND_POST_BE_POINT);
        MessageListFragment postCollect = MessageListFragment.newFragment(TopicMessage.KIND_POST_BE_COLLECT);
        MessageListFragment postComment = MessageListFragment.newFragment(TopicMessage.KIND_POST_BE_COMMENT);
        MessageListFragment commentReply = MessageListFragment.newFragment(TopicMessage.KIND_COMMENT_BE_REPLY);
        MessageListFragment commentReport = MessageListFragment.newFragment(TopicMessage.KIND_COMMENT_BE_REPORT);
        MessageListFragment commentPoint = MessageListFragment.newFragment(TopicMessage.KIND_COMMENT_BE_POINT);
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
        FragmentPagerAdapter<MessageListFragment> adapter = new FragmentPagerAdapter<>(getSupportFragmentManager());
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
