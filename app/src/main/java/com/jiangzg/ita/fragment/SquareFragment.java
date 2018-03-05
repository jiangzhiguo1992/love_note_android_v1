package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.third.LogUtils;

public class SquareFragment extends BasePagerFragment<SquareFragment> {

    public static SquareFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(SquareFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_square;
    }

    @Override
    protected void initView(@Nullable Bundle state) {

    }

    protected void refreshData() {
        ToastUtils.show("Square加载数据");
    }

}
