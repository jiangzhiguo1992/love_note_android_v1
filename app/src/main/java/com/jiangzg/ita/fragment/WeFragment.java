package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.view.View;
import android.widget.RelativeLayout;

import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.user.LoginActivity;
import com.jiangzg.ita.activity.user.RegisterActivity;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.utils.UserPreference;

import butterknife.BindView;
import butterknife.OnClick;

public class WeFragment extends BaseFragment<WeFragment> {

    @BindView(R.id.rlNoUser)
    RelativeLayout rlNoUser;
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

    @Override
    protected void initData(Bundle state) {

    }

    @OnClick({R.id.btnLogin, R.id.btnRegister, R.id.btnCouple})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnLogin: //登录类型
                LoginActivity.goActivity(mActivity);
                break;
            case R.id.btnRegister: //验证码
                RegisterActivity.goActivity(mActivity);
                break;
            case R.id.btnCouple: //登录

                break;
        }
    }

    public void refreshView() {
        if (UserPreference.noLogin()) {
            viewNoLogin();
        } else {
            if (UserPreference.noCouple()) {
                viewNoCouple();
            } else {
                viewIsCouple();
            }
        }
    }

    private void viewNoLogin() {
        rlNoUser.setVisibility(View.VISIBLE);
        rlNoCouple.setVisibility(View.GONE);
        rlLoading.setVisibility(View.GONE);
        nsvCouple.setVisibility(View.GONE);
    }

    private void viewNoCouple() {
        rlNoUser.setVisibility(View.GONE);
        rlNoCouple.setVisibility(View.VISIBLE);
        rlLoading.setVisibility(View.GONE);
        nsvCouple.setVisibility(View.GONE);
    }

    private void viewIsCouple() {
        rlNoUser.setVisibility(View.GONE);
        rlNoCouple.setVisibility(View.GONE);
        rlLoading.setVisibility(View.GONE);
        nsvCouple.setVisibility(View.VISIBLE);
    }

    private void viewLoading() {
        rlNoUser.setVisibility(View.GONE);
        rlNoCouple.setVisibility(View.GONE);
        rlLoading.setVisibility(View.VISIBLE);
        nsvCouple.setVisibility(View.GONE);
    }

}
