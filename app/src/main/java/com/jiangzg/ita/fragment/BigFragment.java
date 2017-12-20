package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.jiangzg.base.component.fragment.FragmentTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseFragment;

import butterknife.BindView;

public class BigFragment extends BaseFragment<BigFragment> {

    @BindView(R.id.ivLauncher)
    public ImageView ivLauncher;

    public static BigFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BigFragment.newInstance(BigFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_big;
    }

    @Override
    protected void initView(@Nullable Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {

    }
}
