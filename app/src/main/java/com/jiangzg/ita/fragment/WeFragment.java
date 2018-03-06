package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.View;

import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class WeFragment extends BasePagerFragment<WeFragment> {

    @BindView(R.id.root)
    NestedScrollView root;

    public static WeFragment newFragment() {
        Bundle bundle = new Bundle();
        return BaseFragment.newInstance(WeFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_we;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        refreshView();
    }

    protected void refreshData() {
        ToastUtils.show("We加载数据");
    }

    //@OnClick({R.id.btnCouple})
    //public void onClick(View view) {
    //    switch (view.getId()) {
    //        case R.id.btnCouple: //登录
    //
    //            break;
    //    }
    //}

    public void refreshView() {
        root.setVisibility(View.VISIBLE);
    }

}
