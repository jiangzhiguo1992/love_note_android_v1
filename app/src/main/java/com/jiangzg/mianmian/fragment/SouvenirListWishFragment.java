package com.jiangzg.mianmian.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseFragment;

public class SouvenirListWishFragment extends BaseFragment<SouvenirListWishFragment> {

    public static SouvenirListWishFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(SouvenirListWishFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_souvenir_list_wish;
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
