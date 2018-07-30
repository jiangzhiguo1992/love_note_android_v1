package com.jiangzg.mianmian.activity.topic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.FragmentPagerAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.TopicMessageInfo;
import com.jiangzg.mianmian.fragment.TopicFragment;
import com.jiangzg.mianmian.fragment.TopicMessageFragment;
import com.jiangzg.mianmian.helper.ViewHelper;

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
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.my_collect), true);
        // fragment
        List<String> titleList = new ArrayList<>();
        List<TopicMessageFragment> fragmentList = new ArrayList<>();
        if (TopicFragment.topicMessageInfoList != null && TopicFragment.topicMessageInfoList.size() > 0) {
            for (TopicMessageInfo info : TopicFragment.topicMessageInfoList) {
                if (info == null) continue;
                titleList.add(info.getName());
                TopicMessageFragment fragment = TopicMessageFragment.newFragment(info);
                fragmentList.add(fragment);
            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_TOPIC_MESSAGE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
