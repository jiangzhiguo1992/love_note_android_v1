package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;

public class TopicFragment extends BasePagerFragment<TopicFragment> {

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

    }

    protected void refreshData() {
        ToastUtils.show("Topic加载数据");
    }

}
