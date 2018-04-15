package com.jiangzg.ita.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.activity.couple.CoupleInfoActivity;
import com.jiangzg.ita.activity.couple.CouplePairActivity;
import com.jiangzg.ita.activity.couple.WallPaperActivity;
import com.jiangzg.ita.activity.settings.SettingsActivity;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.domain.WallPaper;
import com.jiangzg.ita.helper.CheckHelper;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.RxBus;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.view.GImageView;
import com.jiangzg.ita.view.GMarqueeText;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;
import rx.functions.Action1;

public class WeFragment extends BasePagerFragment<WeFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvCoupleCountDown)
    TextView tvCoupleCountDown;
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

    @BindView(R.id.llPlace)
    LinearLayout llPlace;
    @BindView(R.id.tvPlaceLeft)
    GMarqueeText tvPlaceLeft;
    @BindView(R.id.tvDistance)
    TextView tvDistance;
    @BindView(R.id.tvPlaceRight)
    GMarqueeText tvPlaceRight;

    @BindView(R.id.llWeather)
    LinearLayout llWeather;
    @BindView(R.id.tvWeatherLeft)
    GMarqueeText tvWeatherLeft;
    @BindView(R.id.tvWeatherDiffer)
    TextView tvWeatherDiffer;
    @BindView(R.id.tvWeatherRight)
    GMarqueeText tvWeatherRight;

    @BindView(R.id.rlMenses)
    RelativeLayout rlMenses;
    @BindView(R.id.tvMenses)
    TextView tvMenses;
    @BindView(R.id.rlTrends)
    RelativeLayout rlTrends;
    @BindView(R.id.tvTrends)
    TextView tvTrends;
    @BindView(R.id.rlCoin)
    RelativeLayout rlCoin;
    @BindView(R.id.tvCoin)
    TextView tvCoin;

    private Observable<Couple> observable;
    private Runnable coupleCountDownTask;

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
        // event
        observable = RxBus.register(ConsHelper.EVENT_COUPLE_REFRESH, new Action1<Couple>() {
            @Override
            public void call(Couple couple) {
                refreshView();
            }
        });
    }

    protected void loadData() {
        refreshData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopCoupleCountDownTask();
        RxBus.unregister(ConsHelper.EVENT_COUPLE_REFRESH, observable);
    }

    @OnClick({R.id.ivHelp, R.id.ivSettings, R.id.btnPair, R.id.vfWallPaper, R.id.llCoupleInfo,
            R.id.llPlace, R.id.llWeather, R.id.rlMenses, R.id.rlTrends, R.id.rlCoin})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivHelp: // 帮助文档
                HelpActivity.goActivity(mActivity, Help.TYPE_COUPLE_HOME);
                break;
            case R.id.ivSettings: // 设置
                SettingsActivity.goActivity(mActivity);
                break;
            case R.id.btnPair: // 配对
                CouplePairActivity.goActivity(mActivity);
                break;
            case R.id.vfWallPaper: // todo 背景图
                if (CheckHelper.isCoupleBreak()) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    WallPaperActivity.goActivity(mActivity);
                }
                break;
            case R.id.llCoupleInfo: // cp信息
                if (CheckHelper.isCoupleBreak()) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    CoupleInfoActivity.goActivity(mActivity);
                }
                break;
            case R.id.llPlace: // todo 地理信息
                if (CheckHelper.isCoupleBreak()) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    ToastUtils.show("正在开发中");
                }
                break;
            case R.id.llWeather: // todo 天气信息
                if (CheckHelper.isCoupleBreak()) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    ToastUtils.show("正在开发中");
                }
                break;
            case R.id.rlMenses: // todo 姨妈
                if (CheckHelper.isCoupleBreak()) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    ToastUtils.show("正在开发中");
                }
                break;
            case R.id.rlTrends: // todo 动态
                if (CheckHelper.isCoupleBreak()) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    ToastUtils.show("正在开发中");
                }
                break;
            case R.id.rlCoin: // todo 金币
                if (CheckHelper.isCoupleBreak()) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    ToastUtils.show("正在开发中");
                }
                break;
        }
    }

    // 沉浸式状态栏
    private void initTransBar() {
        int statusBarHeight = BarUtils.getStatusBarHeight(mActivity);
        RelativeLayout.LayoutParams paramsHelp = (RelativeLayout.LayoutParams) ivHelp.getLayoutParams();
        paramsHelp.setMargins(paramsHelp.leftMargin, paramsHelp.topMargin + statusBarHeight, paramsHelp.rightMargin, paramsHelp.bottomMargin);
        ivHelp.setLayoutParams(paramsHelp);
        RelativeLayout.LayoutParams paramsSettings = (RelativeLayout.LayoutParams) ivSettings.getLayoutParams();
        paramsSettings.setMargins(paramsSettings.leftMargin, paramsSettings.topMargin + statusBarHeight, paramsSettings.rightMargin, paramsSettings.bottomMargin);
        ivSettings.setLayoutParams(paramsSettings);
    }

    // 数据刷新
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

    // 视图刷新 所有cp的更新都要放到sp里，集中存放
    private void refreshView() {
        tvCoupleCountDown.setVisibility(View.GONE);
        rlPair.setVisibility(View.GONE);
        vfWallPaper.setVisibility(View.GONE);

        User user = SPHelper.getUser();
        Couple couple = user.getCouple();
        if (CheckHelper.isCoupleBreak(couple)) {
            // 已经分手，或者没有开始过
            rlPair.setVisibility(View.VISIBLE);
        } else {
            // 已经配对
            if (CheckHelper.isCoupleBreaking(couple)) {
                // 正在分手
                tvCoupleCountDown.setVisibility(View.VISIBLE);
                MyApp.get().getHandler().post(getCoupleCountDownTask());
            } else {
                // 没分手
                vfWallPaper.setVisibility(View.VISIBLE);
                // 开始墙纸动画 todo 数据
                initViewFlipper(null);
            }
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

    // 分手倒计时
    private Runnable getCoupleCountDownTask() {
        if (coupleCountDownTask == null) {
            coupleCountDownTask = new Runnable() {
                @Override
                public void run() {
                    Couple couple = SPHelper.getCouple();
                    long breakCountDown = couple.getBreakCountDown();
                    if (breakCountDown <= 0) {
                        RxEvent<Couple> event = new RxEvent<>(ConsHelper.EVENT_COUPLE_REFRESH, new Couple());
                        RxBus.post(event);
                        MyApp.get().getHandler().removeCallbacks(this);
                    } else {
                        String breakCountDownShow = couple.getBreakCountDownShow();
                        tvCoupleCountDown.setText(breakCountDownShow);
                        MyApp.get().getHandler().postDelayed(this, ConstantUtils.SEC);
                    }
                }
            };
        }
        return coupleCountDownTask;
    }

    private void stopCoupleCountDownTask() {
        if (coupleCountDownTask != null) {
            MyApp.get().getHandler().removeCallbacks(coupleCountDownTask);
            coupleCountDownTask = null;
        }
    }

    // 墙纸
    private void initViewFlipper(WallPaper wallPaper) {
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
