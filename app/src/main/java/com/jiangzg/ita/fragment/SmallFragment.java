package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.jiangzg.base.component.fragment.FragmentTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseFragment;

import butterknife.BindView;

public class SmallFragment extends BaseFragment<SmallFragment> {

    @BindView(R.id.ivLauncher)
    ImageView ivLauncher;

    public static SmallFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return SmallFragment.newInstance(SmallFragment.class, bundle);
    }

    public BigFragment replace() {
        BigFragment bigFragment = BigFragment.newFragment();
        FragmentTrans.replace(mFragmentManager, bigFragment, R.id.rlFragment);
        return bigFragment;
    }

    @Override
    protected int initObj(Bundle data) {
        return R.layout.fragment_small;
    }

    @Override
    protected void initView(@Nullable Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }

}
