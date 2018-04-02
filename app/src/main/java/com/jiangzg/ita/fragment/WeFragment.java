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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.activity.couple.CoupleInviteeActivity;
import com.jiangzg.ita.activity.couple.WallPaperActivity;
import com.jiangzg.ita.activity.settings.SettingsActivity;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.view.GImageView;
import com.jiangzg.ita.view.GMarqueeText;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class WeFragment extends BasePagerFragment<WeFragment> {

    private static final String LOG_TAG = "WeFragment";

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rlPair)
    RelativeLayout rlPair;
    @BindView(R.id.btnPair)
    Button btnPair;
    @BindView(R.id.vfWallPaper)
    ViewFlipper vfWallPaper;
    @BindView(R.id.ivHelp)
    ImageView ivHelp;
    @BindView(R.id.ivSettings)
    ImageView ivSettings;

    @BindView(R.id.llCoupleInfo)
    LinearLayout llCoupleInfo;
    @BindView(R.id.ivAvatarLeft)
    GImageView ivAvatarLeft;
    @BindView(R.id.ivAvatarRight)
    GImageView ivAvatarRight;
    @BindView(R.id.tvNameLeft)
    TextView tvNameLeft;
    @BindView(R.id.tvNameRight)
    TextView tvNameRight;

    @BindView(R.id.cardPlace)
    CardView cardPlace;
    @BindView(R.id.tvPlaceLeft)
    GMarqueeText tvPlaceLeft;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvPlaceRight)
    GMarqueeText tvPlaceRight;

    @BindView(R.id.cardWeather)
    CardView cardWeather;
    @BindView(R.id.tvWeatherLeft)
    GMarqueeText tvWeatherLeft;
    @BindView(R.id.tvWeatherDiffer)
    TextView tvWeatherDiffer;
    @BindView(R.id.tvWeatherRight)
    GMarqueeText tvWeatherRight;

    @BindView(R.id.cardMenses)
    CardView cardMenses;
    @BindView(R.id.tvMenses)
    TextView tvMenses;
    @BindView(R.id.cardTrends)
    CardView cardTrends;
    @BindView(R.id.tvTrends)
    TextView tvTrends;
    @BindView(R.id.cardCoin)
    CardView cardCoin;
    @BindView(R.id.tvCoin)
    TextView tvCoin;

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
        // 沉浸式状态栏适配
        initTransBar();
        // listener
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
    }

    protected void loadData() {
        refreshData();
    }

    @OnClick({R.id.ivHelp, R.id.ivSettings, R.id.btnPair, R.id.vfWallPaper, R.id.llCoupleInfo,
            R.id.cardPlace, R.id.cardWeather, R.id.cardMenses, R.id.cardTrends, R.id.cardCoin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivHelp: // 帮助文档
                HelpActivity.goActivity(mActivity, Help.TYPE_COUPLE_HOME);
                break;
            case R.id.ivSettings: // 设置
                SettingsActivity.goActivity(mActivity);
                break;
            case R.id.btnPair: // 配对
                CoupleInviteeActivity.goActivity(mActivity);
                break;
            case R.id.vfWallPaper: // 背景图
                WallPaperActivity.goActivity(mActivity);
                break;
            case R.id.llCoupleInfo: // todo cp信息
                break;
            case R.id.cardPlace: // todo 地理信息
                break;
            case R.id.cardWeather: // todo 天气信息
                break;
            case R.id.cardMenses: // todo 姨妈
                break;
            case R.id.cardTrends: // todo 动态
                break;
            case R.id.cardCoin: // todo 金币
                break;
        }
    }

    private void initTransBar() {
        int statusBarHeight = BarUtils.getStatusBarHeight(mActivity);
        RelativeLayout.LayoutParams paramsHelp = (RelativeLayout.LayoutParams) ivHelp.getLayoutParams();
        paramsHelp.setMargins(paramsHelp.leftMargin, paramsHelp.topMargin + statusBarHeight, paramsHelp.rightMargin, paramsHelp.bottomMargin);
        ivHelp.setLayoutParams(paramsHelp);
        RelativeLayout.LayoutParams paramsSettings = (RelativeLayout.LayoutParams) ivSettings.getLayoutParams();
        paramsSettings.setMargins(paramsSettings.leftMargin, paramsSettings.topMargin + statusBarHeight, paramsSettings.rightMargin, paramsSettings.bottomMargin);
        ivSettings.setLayoutParams(paramsSettings);
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // todo api
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);
                refreshView();

            }
        }, 1000);
    }

    private void refreshView() {
        User user = SPHelper.getUser();
        Couple couple = user.getCouple();
        if (SPHelper.noCouple(couple)) {
            rlPair.setVisibility(View.VISIBLE);
            vfWallPaper.setVisibility(View.GONE);
            // 默认头像
            if (user.getSex() == User.SEX_BOY) {
                ivAvatarLeft.setDataRes(R.mipmap.ic_boy_circle);
                ivAvatarRight.setDataRes(R.mipmap.ic_girl_circle);
            } else {
                ivAvatarLeft.setDataRes(R.mipmap.ic_girl_circle);
                ivAvatarRight.setDataRes(R.mipmap.ic_boy_circle);
            }
        } else {
            rlPair.setVisibility(View.GONE);
            vfWallPaper.setVisibility(View.VISIBLE);
            // 开始墙纸动画 todo 数据
            initViewFlipper();
            // 头像 + 名称
            String myAvatar = user.getMyAvatarInCp();
            String taAvatar = user.getTaAvatarInCp();
            String myName = user.getMyNameInCp();
            String taName = user.getTaNameInCp();
            ivAvatarLeft.setDataOss(myAvatar);
            ivAvatarRight.setDataOss(taAvatar);
            tvNameLeft.setText(myName);
            tvNameRight.setText(taName);
        }
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
            image.setDataRes(resId);
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
        return image;
    }

}
