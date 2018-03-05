package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.RelativeLayout;

import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.utils.UserPreference;

import butterknife.BindView;
import butterknife.OnClick;

public class WeFragment extends BasePagerFragment<WeFragment> {

    @BindView(R.id.rlNoCouple)
    RelativeLayout rlNoCouple;
    @BindView(R.id.rlLoading)
    RelativeLayout rlLoading;
    @BindView(R.id.nsvCouple)
    NestedScrollView nsvCouple;

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

    @OnClick({R.id.btnCouple})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnCouple: //登录

                break;
        }
    }

    public void refreshView() {
        if (UserPreference.noCouple()) {
            viewNoCouple();
        } else {
            viewIsCouple();
        }
    }

    private void viewNoCouple() {
        rlNoCouple.setVisibility(View.VISIBLE);
        rlLoading.setVisibility(View.GONE);
        nsvCouple.setVisibility(View.GONE);
    }

    private void viewIsCouple() {
        rlNoCouple.setVisibility(View.GONE);
        rlLoading.setVisibility(View.GONE);
        nsvCouple.setVisibility(View.VISIBLE);
    }

    private void viewLoading() {
        rlNoCouple.setVisibility(View.GONE);
        rlLoading.setVisibility(View.VISIBLE);
        nsvCouple.setVisibility(View.GONE);
    }

}
