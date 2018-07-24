package com.jiangzg.mianmian.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.domain.PostSubKindInfo;

public class PostListFragment extends BaseFragment<PostListFragment> {

    private PostSubKindInfo subKindInfo;

    public static PostListFragment newFragment(PostSubKindInfo subKindInfo) {
        Bundle bundle = new Bundle();
        bundle.putParcelable("subKindInfo", subKindInfo);
        return BaseFragment.newInstance(PostListFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        subKindInfo = data.getParcelable("subKindInfo");
        return R.layout.fragment_post_list;
    }

    @Override
    protected void initView(@Nullable Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

}
