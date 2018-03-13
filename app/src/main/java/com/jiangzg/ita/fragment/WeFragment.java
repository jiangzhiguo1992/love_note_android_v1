package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.activity.settings.SettingsActivity;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.view.GMarqueeText;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

public class WeFragment extends BasePagerFragment<WeFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.vfTopBg)
    ViewFlipper vfTopBg;
    @BindView(R.id.ivHelp)
    ImageView ivHelp;
    @BindView(R.id.ivSettings)
    ImageView ivSettings;
    @BindView(R.id.civAvatarLeft)
    CircleImageView civAvatarLeft;
    @BindView(R.id.civAvatarRight)
    CircleImageView civAvatarRight;
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
                HelpActivity.goActivity(mActivity, Help.TYPE_COUPLE_HOME);
                break;
            case R.id.ivSettings: // 设置
                SettingsActivity.goActivity(mActivity);
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
        RelativeLayout.LayoutParams paramsHelp = (RelativeLayout.LayoutParams) ivHelp.getLayoutParams();
        paramsHelp.setMargins(paramsHelp.leftMargin, paramsHelp.topMargin + statusBarHeight, paramsHelp.rightMargin, paramsHelp.bottomMargin);
        ivHelp.setLayoutParams(paramsHelp);
        RelativeLayout.LayoutParams paramsSettings = (RelativeLayout.LayoutParams) ivSettings.getLayoutParams();
        paramsSettings.setMargins(paramsSettings.leftMargin, paramsSettings.topMargin + statusBarHeight, paramsSettings.rightMargin, paramsSettings.bottomMargin);
        ivSettings.setLayoutParams(paramsSettings);
        // 开始背景动画
        ViewFlipper.LayoutParams paramsImage = new ViewFlipper.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        ImageView image1 = new ImageView(mActivity);
        image1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image1.setImageResource(R.mipmap.test_bg_01);
        image1.setLayoutParams(paramsImage);
        vfTopBg.addView(image1);
        ImageView image2 = new ImageView(mActivity);
        image2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image2.setImageResource(R.mipmap.test_bg_02);
        image2.setLayoutParams(paramsImage);
        vfTopBg.addView(image2);
        ImageView image3 = new ImageView(mActivity);
        image3.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image3.setImageResource(R.mipmap.test_bg_03);
        image3.setLayoutParams(paramsImage);
        vfTopBg.addView(image3);
        ImageView image4 = new ImageView(mActivity);
        image4.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image4.setImageResource(R.mipmap.test_bg_04);
        image4.setLayoutParams(paramsImage);
        vfTopBg.addView(image4);
        ImageView image5 = new ImageView(mActivity);
        image5.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image5.setImageResource(R.mipmap.test_bg_05);
        image5.setLayoutParams(paramsImage);
        vfTopBg.addView(image5);
        ImageView image6 = new ImageView(mActivity);
        image6.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image6.setImageResource(R.mipmap.test_bg_06);
        image6.setLayoutParams(paramsImage);
        vfTopBg.addView(image6);
        ImageView image7 = new ImageView(mActivity);
        image7.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image7.setImageResource(R.mipmap.test_bg_07);
        image7.setLayoutParams(paramsImage);
        vfTopBg.addView(image7);
        ImageView image8 = new ImageView(mActivity);
        image8.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image8.setImageResource(R.mipmap.test_bg_08);
        image8.setLayoutParams(paramsImage);
        vfTopBg.addView(image8);
        ImageView image9 = new ImageView(mActivity);
        image9.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image9.setImageResource(R.mipmap.test_bg_09);
        image9.setLayoutParams(paramsImage);
        vfTopBg.addView(image9);
        vfTopBg.setInAnimation(AnimationUtils.makeInAnimation(mActivity, false));
        vfTopBg.setOutAnimation(AnimationUtils.makeOutAnimation(mActivity, false));
        vfTopBg.setAutoStart(true);
        vfTopBg.setFlipInterval(3000);
        vfTopBg.startFlipping();
    }

}
