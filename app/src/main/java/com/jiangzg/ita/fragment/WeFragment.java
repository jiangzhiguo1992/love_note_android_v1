package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.activity.couple.WallPaperActivity;
import com.jiangzg.ita.activity.settings.SettingsActivity;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.view.GImageView;
import com.jiangzg.ita.view.GMarqueeText;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;

public class WeFragment extends BasePagerFragment<WeFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.vfWallPaper)
    ViewFlipper vfWallPaper;
    @BindView(R.id.ivHelp)
    ImageView ivHelp;
    @BindView(R.id.ivSettings)
    ImageView ivSettings;
    @BindView(R.id.ivAvatarLeft)
    GImageView ivAvatarLeft;
    @BindView(R.id.ivAvatarRight)
    GImageView ivAvatarRight;
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
        root.setVisibility(View.VISIBLE);
        // 沉浸式状态栏适配
        initBar();
        // 开始背景动画
        initViewFlipper();
        // listener
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    protected void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        getData();
    }

    @OnClick({R.id.ivHelp, R.id.ivSettings, R.id.ivAvatarLeft, R.id.ivAvatarRight,
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
            case R.id.ivAvatarLeft: // 我的头像
                break;
            case R.id.ivAvatarRight: // ta的头像
                break;
            case R.id.cardPlace: // 地理信息
                break;
            case R.id.cardWeather: // 天气信息
                break;
            case R.id.cardMenses: // 姨妈
                break;
            case R.id.cardSleep: // todo 去掉睡觉 添加戳ta
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

    @OnLongClick({R.id.vfWallPaper})
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.vfWallPaper: // 背景图
                WallPaperActivity.goActivity(mActivity);
                return true;
        }
        return false;
    }

    private void initBar() {
        int statusBarHeight = BarUtils.getStatusBarHeight(mActivity);
        RelativeLayout.LayoutParams paramsHelp = (RelativeLayout.LayoutParams) ivHelp.getLayoutParams();
        paramsHelp.setMargins(paramsHelp.leftMargin, paramsHelp.topMargin + statusBarHeight, paramsHelp.rightMargin, paramsHelp.bottomMargin);
        ivHelp.setLayoutParams(paramsHelp);
        RelativeLayout.LayoutParams paramsSettings = (RelativeLayout.LayoutParams) ivSettings.getLayoutParams();
        paramsSettings.setMargins(paramsSettings.leftMargin, paramsSettings.topMargin + statusBarHeight, paramsSettings.rightMargin, paramsSettings.bottomMargin);
        ivSettings.setLayoutParams(paramsSettings);
    }

    private void initViewFlipper() {
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.mipmap.test_bg_01);
        imageList.add(R.mipmap.test_bg_02);
        imageList.add(R.mipmap.test_bg_03);
        imageList.add(R.mipmap.test_bg_04);
        imageList.add(R.mipmap.test_bg_05);
        imageList.add(R.mipmap.test_bg_06);
        imageList.add(R.mipmap.test_bg_07);
        imageList.add(R.mipmap.test_bg_08);
        imageList.add(R.mipmap.test_bg_09);
        for (Integer resId : imageList) {
            GImageView image = getViewFlipperImage();
            image.setRes(resId);
            vfWallPaper.addView(image);
        }

        Animation in = AnimationUtils.loadAnimation(mActivity, R.anim.alpha_we_bg_in);
        Animation out = AnimationUtils.loadAnimation(mActivity, R.anim.alpha_we_bg_out);
        in.setInterpolator(new DecelerateInterpolator());
        out.setInterpolator(new DecelerateInterpolator());
        in.setStartTime(AnimationUtils.currentAnimationTimeMillis());
        out.setStartTime(AnimationUtils.currentAnimationTimeMillis());

        vfWallPaper.setInAnimation(in);
        vfWallPaper.setOutAnimation(out);
        vfWallPaper.setAutoStart(true);
        vfWallPaper.setFlipInterval(10000);
        vfWallPaper.startFlipping();
    }

    private GImageView getViewFlipperImage() {
        GImageView image = new GImageView(mActivity);
        ViewFlipper.LayoutParams paramsImage = new ViewFlipper.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        image.setLayoutParams(paramsImage);
        image.setCircleAndFull(false, false);
        return image;
    }

    private void getData() {
        srl.setRefreshing(false);

        // todo api

        ivAvatarLeft.setRes(R.mipmap.ic_boy_circle);
        ivAvatarRight.setRes(R.mipmap.ic_girl_circle);
    }

}
