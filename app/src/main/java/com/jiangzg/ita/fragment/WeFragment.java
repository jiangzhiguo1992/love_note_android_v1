package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiangzg.base.view.BarUtils;
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
    @BindView(R.id.tvWeatherLeft)
    GMarqueeText tvWeatherLeft;
    @BindView(R.id.tvWeatherForecast)
    TextView tvWeatherForecast;
    @BindView(R.id.tvWeatherRight)
    GMarqueeText tvWeatherRight;
    @BindView(R.id.cardWeather)
    CardView cardWeather;
    @BindView(R.id.tvMenses)
    TextView tvMenses;
    @BindView(R.id.cardMenses)
    CardView cardMenses;
    @BindView(R.id.tvSleep)
    TextView tvSleep;
    @BindView(R.id.cardSleep)
    CardView cardSleep;
    @BindView(R.id.tvTrends)
    TextView tvTrends;
    @BindView(R.id.cardTrends)
    CardView cardTrends;
    @BindView(R.id.tvSign)
    TextView tvSign;
    @BindView(R.id.cardSign)
    CardView cardSign;
    @BindView(R.id.tvCoin)
    TextView tvCoin;
    @BindView(R.id.cardCoin)
    CardView cardCoin;
    @BindView(R.id.tvVip)
    TextView tvVip;
    @BindView(R.id.cardVip)
    CardView cardVip;

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

    @OnClick({R.id.ivHelp, R.id.ivSettings, R.id.civAvatarLeft, R.id.civAvatarRight,
            R.id.cardPlace, R.id.cardWeather, R.id.cardMenses, R.id.cardSleep, R.id.cardTrends,
            R.id.cardSign, R.id.cardCoin, R.id.cardVip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivHelp: // 帮助文档
                break;
            case R.id.ivSettings: // 设置
                break;
            case R.id.civAvatarLeft: // 我的头像
                break;
            case R.id.civAvatarRight: // ta的头像
                break;
            case R.id.cardPlace: // 地理信息
                break;
            case R.id.cardWeather: // 天气信息
                break;
            case R.id.cardMenses: // 姨妈
                break;
            case R.id.cardSleep: // 睡觉
                break;
            case R.id.cardTrends: // 动态
                break;
            case R.id.cardSign: // 签到
                break;
            case R.id.cardCoin: // 金币
                break;
            case R.id.cardVip: // 会员
                break;
        }
    }

    public void refreshView() {
        root.setVisibility(View.VISIBLE);
        // 沉浸式状态栏适配
        int statusBarHeight = BarUtils.getStatusBarHeight(mActivity);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) ivHelp.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin + statusBarHeight, params.rightMargin, params.bottomMargin);
        ivHelp.setLayoutParams(params);
        RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) ivSettings.getLayoutParams();
        params2.setMargins(params2.leftMargin, params2.topMargin + statusBarHeight, params2.rightMargin, params2.bottomMargin);
        ivSettings.setLayoutParams(params2);
    }

}
