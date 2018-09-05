package com.jiangzg.lovenote_admin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseFragment;

public class CoupleFragment extends BaseFragment<CoupleFragment> {

    public static CoupleFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(CoupleFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_couple;
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
