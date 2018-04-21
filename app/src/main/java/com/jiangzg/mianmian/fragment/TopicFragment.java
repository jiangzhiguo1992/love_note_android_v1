package com.jiangzg.mianmian.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;

import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.base.BasePagerFragment;
import com.jiangzg.mianmian.helper.ViewHelper;

import butterknife.BindView;

public class TopicFragment extends BasePagerFragment<TopicFragment> {

    @BindView(R.id.tb)
    Toolbar tb;

    public static TopicFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(TopicFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_topic;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "话题", false);
        fitToolBar(tb);
    }

    protected void loadData() {
        ToastUtils.show("Topic加载数据");
    }

}
