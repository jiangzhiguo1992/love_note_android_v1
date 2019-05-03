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
import com.jiangzg.lovenote.controller.fragment.topic.MessageListFragment;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.entity.TopicMessage;

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
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), TopicMessageActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from, TopicMessageActivity.class);
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
        titleList.add(getString(R.string.jab_in_post));
        titleList.add(getString(R.string.jab_in_comment));
        titleList.add(getString(R.string.post_comment));
        titleList.add(getString(R.string.comment_reply));
        List<MessageListFragment> fragmentList = new ArrayList<>();
        MessageListFragment all = MessageListFragment.newFragment(TopicMessage.KIND_ALL);
        MessageListFragment jabPost = MessageListFragment.newFragment(TopicMessage.KIND_JAB_IN_POST);
        MessageListFragment jabComment = MessageListFragment.newFragment(TopicMessage.KIND_JAB_IN_COMMENT);
        MessageListFragment postComment = MessageListFragment.newFragment(TopicMessage.KIND_POST_BE_COMMENT);
        MessageListFragment commentReply = MessageListFragment.newFragment(TopicMessage.KIND_COMMENT_BE_REPLY);
        fragmentList.add(all);
        fragmentList.add(jabPost);
        fragmentList.add(jabComment);
        fragmentList.add(postComment);
        fragmentList.add(commentReply);
        // adapter
        CommonFragmentAdapter<MessageListFragment> adapter = new CommonFragmentAdapter<>(getSupportFragmentManager());
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
