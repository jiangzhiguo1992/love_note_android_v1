package com.jiangzg.project.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.View;
import android.widget.ImageView;

import com.android.base.component.fragment.FragmentTrans;
import com.jiangzg.project.R;
import com.jiangzg.project.base.BaseFragment;

import butterknife.BindView;

public class BigFragment extends BaseFragment<BigFragment> {

    @BindView(R.id.ivLauncher)
    public ImageView ivLauncher;

    public static BigFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BigFragment.newInstance(BigFragment.class, bundle);
    }

    public SmallFragment replace() {
        SmallFragment smallFragment = new SmallFragment();
        FragmentTrans.replace(mFragmentManager, smallFragment, R.id.rlFragment);
        return smallFragment;
    }

    @Override
    protected int initObj(Bundle data) {
        return R.layout.fragment_big;
    }

    @Override
    protected void initView(@Nullable Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }
}
