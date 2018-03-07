package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.utils.ViewUtils;
import com.jiangzg.ita.view.GCircleImageView;
import com.jiangzg.ita.view.GMarqueeText;
import com.jiangzg.ita.view.GVerticalSwipeRefresh;

import butterknife.BindView;
import butterknife.OnClick;

public class WeFragment extends BasePagerFragment<WeFragment> {

    @BindView(R.id.vsr)
    GVerticalSwipeRefresh vsr;
    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.ivTopBg)
    ImageView ivTopBg;
    @BindView(R.id.ivHelp)
    ImageView ivHelp;
    @BindView(R.id.ivSettings)
    ImageView ivSettings;
    @BindView(R.id.civAvatarLeft)
    GCircleImageView civAvatarLeft;
    @BindView(R.id.civAvatarRight)
    GCircleImageView civAvatarRight;
    @BindView(R.id.tvNameLeft)
    TextView tvNameLeft;
    @BindView(R.id.tvNameRight)
    TextView tvNameRight;
    @BindView(R.id.tvPlaceLeft)
    GMarqueeText tvPlaceLeft;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvPlaceRight)
    GMarqueeText tvPlaceRight;
    @BindView(R.id.cardPlace)
    CardView cardPlace;

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
    }

    @OnClick({R.id.cardPlace})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cardPlace:
                ToastUtils.show("点击");
                break;
        }
    }

    public void refreshView() {
        root.setVisibility(View.VISIBLE);
        int colorDark = ViewUtils.getColorDark(mActivity);
        int colorPrimary = ViewUtils.getColorPrimary(mActivity);
        int colorAccent = ViewUtils.getColorAccent(mActivity);
        int colorLight = ViewUtils.getColorLight(mActivity);
        vsr.setColorSchemeResources(colorDark, colorPrimary, colorAccent, colorLight);
    }

}
