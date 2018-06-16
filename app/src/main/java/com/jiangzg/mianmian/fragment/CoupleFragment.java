package com.jiangzg.mianmian.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

import com.facebook.drawee.drawable.ScalingUtils;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.couple.CoupleInfoActivity;
import com.jiangzg.mianmian.activity.couple.CouplePairActivity;
import com.jiangzg.mianmian.activity.couple.CouplePlaceActivity;
import com.jiangzg.mianmian.activity.couple.CoupleWallPaperActivity;
import com.jiangzg.mianmian.activity.couple.CoupleWeatherActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.base.BasePagerFragment;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Place;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.domain.WallPaper;
import com.jiangzg.mianmian.domain.Weather;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.LocationHelper;
import com.jiangzg.mianmian.helper.OssResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GImageAvatarView;
import com.jiangzg.mianmian.view.GImageView;
import com.jiangzg.mianmian.view.GMarqueeText;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class CoupleFragment extends BasePagerFragment<CoupleFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvCoupleCountDown)
    TextView tvCoupleCountDown;
    @BindView(R.id.tvAddWallPaper)
    TextView tvAddWallPaper;
    @BindView(R.id.btnPair)
    Button btnPair;
    @BindView(R.id.vfWallPaper)
    ViewFlipper vfWallPaper;
    @BindView(R.id.ivHelp)
    ImageView ivHelp;
    @BindView(R.id.ivWallPaper)
    ImageView ivWallPaper;

    @BindView(R.id.llCoupleInfo)
    LinearLayout llCoupleInfo;
    @BindView(R.id.ivAvatarLeft)
    GImageAvatarView ivAvatarLeft;
    @BindView(R.id.ivAvatarRight)
    GImageAvatarView ivAvatarRight;
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

    private Call<Result> callHomeGet;
    private Call<Result> callPlaceGet;
    private Runnable coupleCountDownTask;
    private Observable<WallPaper> obWallPaperRefresh;
    private Observable<Couple> obCoupleRefresh;
    private Observable<LocationInfo> obLocationRefresh;
    private Place myPlace;
    private Place taPlace;

    public static CoupleFragment newFragment() {
        Bundle bundle = new Bundle();
        return BaseFragment.newInstance(CoupleFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_couple;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        // 沉浸式状态栏适配
        int statusBarHeight = BarUtils.getStatusBarHeight(mActivity);
        RelativeLayout.LayoutParams paramsHelp = (RelativeLayout.LayoutParams) ivHelp.getLayoutParams();
        paramsHelp.setMargins(paramsHelp.leftMargin, paramsHelp.topMargin + statusBarHeight, paramsHelp.rightMargin, paramsHelp.bottomMargin);
        ivHelp.setLayoutParams(paramsHelp);
        RelativeLayout.LayoutParams paramsSettings = (RelativeLayout.LayoutParams) ivWallPaper.getLayoutParams();
        paramsSettings.setMargins(paramsSettings.leftMargin, paramsSettings.topMargin + statusBarHeight, paramsSettings.rightMargin, paramsSettings.bottomMargin);
        ivWallPaper.setLayoutParams(paramsSettings);
        // listener
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 第一次进来不要地理位置，entry已经拿过了
                refreshPlaceDate();
                refreshData();
            }
        });
    }

    protected void loadData() {
        // event
        obWallPaperRefresh = RxBus.register(ConsHelper.EVENT_WALL_PAPER_REFRESH, new Action1<WallPaper>() {
            @Override
            public void call(WallPaper wallPaper) {
                refreshWallPaperView();
            }
        });
        obCoupleRefresh = RxBus.register(ConsHelper.EVENT_COUPLE_REFRESH, new Action1<Couple>() {
            @Override
            public void call(Couple couple) {
                refreshView();
            }
        });
        obLocationRefresh = RxBus.register(ConsHelper.EVENT_LOCATION_REFRESH, new Action1<LocationInfo>() {
            @Override
            public void call(LocationInfo locationInfo) {
                refreshPlaceView();
            }
        });
        // refresh
        refreshData();
    }

    @Override
    public void onStart() {
        super.onStart();
        // TODO ViewFlipper无动画
        // TODO ViewFlipper随机顺序
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callHomeGet);
        RetrofitHelper.cancel(callPlaceGet);
        stopCoupleCountDownTask();
        RxBus.unregister(ConsHelper.EVENT_COUPLE_REFRESH, obWallPaperRefresh);
        RxBus.unregister(ConsHelper.EVENT_COUPLE_REFRESH, obCoupleRefresh);
        RxBus.unregister(ConsHelper.EVENT_LOCATION_REFRESH, obLocationRefresh);
    }

    @OnClick({R.id.ivHelp, R.id.ivWallPaper, R.id.btnPair, R.id.llCoupleInfo, R.id.llPlace, R.id.llWeather})
    public void onViewClicked(View view) {
        Couple couple = SPHelper.getCouple();
        switch (view.getId()) {
            case R.id.ivHelp: // 帮助文档
                HelpActivity.goActivity(mActivity, Help.INDEX_COUPLE_HOME);
                break;
            case R.id.ivWallPaper:  // 背景图
                if (Couple.isBreak(couple)) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    CoupleWallPaperActivity.goActivity(mActivity);
                }
                break;
            case R.id.btnPair: // 配对
                CouplePairActivity.goActivity(mActivity);
                break;
            case R.id.llCoupleInfo: // cp信息
                if (Couple.isBreak(couple)) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    User ta = SPHelper.getTa();
                    if (ta == null) {
                        ToastUtils.show(getString(R.string.if_long_time_cant_show_please_down_refresh));
                    } else {
                        CoupleInfoActivity.goActivity(mActivity);
                    }
                }
                break;
            case R.id.llPlace: // 地址信息
                if (Couple.isBreak(couple)) {
                    CouplePairActivity.goActivity(mActivity);
                } else if (LocationHelper.checkLocationEnable(mActivity)) {
                    CouplePlaceActivity.goActivity(mActivity, taPlace);
                }
                break;
            case R.id.llWeather: // 天气信息
                if (Couple.isBreak(couple)) {
                    CouplePairActivity.goActivity(mActivity);
                } else if (LocationHelper.checkLocationEnable(mActivity)) {
                    CoupleWeatherActivity.goActivity(mActivity);
                }
                break;
        }
    }

    // 数据刷新
    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // 刷新的时候再把自己的位置推送上去
        Place body = ApiHelper.getPlaceBody();
        callHomeGet = new RetrofitHelper().call(API.class).coupleHomeGet(body);
        RetrofitHelper.enqueue(callHomeGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                SPHelper.setUser(data.getMe());
                SPHelper.setTa(data.getTa());
                SPHelper.setWallPaper(data.getWallPaper());
                myPlace = data.getMyPlace();
                taPlace = data.getTaPlace();
                refreshView();
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
            }
        });
    }

    // 获取自己的位置并上传
    private void refreshPlaceDate() {
        LocationHelper.startLocation(true, new LocationHelper.LocationCallBack() {
            @Override
            public void onSuccess(LocationInfo info) {
                Place body = ApiHelper.getPlaceBody();
                callPlaceGet = new RetrofitHelper().call(API.class).couplePlacePush(body);
                RetrofitHelper.enqueue(callPlaceGet, null, new RetrofitHelper.CallBack() {
                    @Override
                    public void onResponse(int code, String message, Result.Data data) {
                        LogUtils.i(LOG_TAG, "地理位置刷新");
                        myPlace = data.getMyPlace();
                        taPlace = data.getTaPlace();
                        refreshPlaceView();
                    }

                    @Override
                    public void onFailure(String errMsg) {
                    }
                });
            }

            @Override
            public void onFailed(String errMsg) {
                LogUtils.w(LOG_TAG, "refreshPlaceDate: " + errMsg);
                // 8.0 一个小时只允许获取几次地理位置
            }
        });
    }

    // 视图刷新 所有cp的更新都要放到sp里，集中存放
    private void refreshView() {
        tvCoupleCountDown.setVisibility(View.GONE);
        btnPair.setVisibility(View.GONE);
        vfWallPaper.setVisibility(View.GONE);
        tvAddWallPaper.setVisibility(View.GONE);

        User user = SPHelper.getUser();
        Couple couple = user.getCouple();
        if (Couple.isBreak(couple)) {
            // 已经分手，或者没有开始过
            btnPair.setVisibility(View.VISIBLE);
        } else {
            // 已经配对
            if (Couple.isBreaking(couple)) {
                // 正在分手
                tvCoupleCountDown.setVisibility(View.VISIBLE);
                MyApp.get().getHandler().post(getCoupleCountDownTask());
            } else {
                // 没分手
                vfWallPaper.setVisibility(View.VISIBLE);
                // 开始墙纸动画
                refreshWallPaperView();
            }
            // 本地文件刷新
            OssResHelper.refreshAvatarRes();
            // 头像 + 名称
            String myName = user.getMyNameInCp();
            String taName = user.getTaNameInCp();
            String myAvatar = user.getMyAvatarInCp();
            String taAvatar = user.getTaAvatarInCp();
            ivAvatarLeft.setData(taAvatar);
            ivAvatarRight.setData(myAvatar);
            tvNameLeft.setText(taName);
            tvNameRight.setText(myName);
            refreshPlaceView();
            refreshWeatherView();
        }
    }

    // 墙纸
    private void refreshWallPaperView() {
        // 本地文件刷新
        OssResHelper.refreshWallPaperRes();
        // 清除view
        vfWallPaper.removeAllViews();
        WallPaper wallPaper = SPHelper.getWallPaper();
        // 无图显示
        if (wallPaper == null || wallPaper.getImageList() == null || wallPaper.getImageList().size() <= 0) {
            tvAddWallPaper.setVisibility(View.VISIBLE);
            return;
        }
        tvAddWallPaper.setVisibility(View.GONE);
        // 单图显示
        List<String> imageList = wallPaper.getImageList();
        if (imageList.size() == 1) {
            GImageView image = getViewFlipperImage();
            String ossKey = imageList.get(0);
            image.setData(ossKey);
            vfWallPaper.addView(image);
            vfWallPaper.setAutoStart(false);
            vfWallPaper.stopFlipping();
            return;
        }
        // 多图显示
        for (String ossKey : imageList) {
            GImageView image = getViewFlipperImage();
            image.setData(ossKey);
            vfWallPaper.addView(image);
        }
        // anim
        Animation in = AnimationUtils.loadAnimation(mActivity, R.anim.alpha_we_bg_in);
        Animation out = AnimationUtils.loadAnimation(mActivity, R.anim.alpha_we_bg_out);
        in.setInterpolator(new DecelerateInterpolator());
        out.setInterpolator(new DecelerateInterpolator());
        in.setStartTime(AnimationUtils.currentAnimationTimeMillis());
        out.setStartTime(AnimationUtils.currentAnimationTimeMillis());
        // viewFilter
        vfWallPaper.setAnimateFirstView(true);
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
        int screenWidth = ScreenUtils.getScreenWidth(mActivity);
        int screenHeight = ScreenUtils.getScreenHeight(mActivity);
        image.setWidthAndHeight(screenWidth, screenHeight);
        image.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        image.getHierarchy().setFadeDuration(0);
        return image;
    }

    private void refreshPlaceView() {
        LocationInfo myInfo = LocationInfo.getInfo();
        // myAddress
        String myAddress;
        if (myPlace != null) {
            if (StringUtils.isEmpty(myInfo.getAddress())) {
                // 优先本地数据
                myInfo.setAddress(myPlace.getAddress());
            }
            if (myInfo.getLatitude() == 0 && myInfo.getLongitude() == 0) {
                myInfo.setLongitude(myPlace.getLongitude());
                myInfo.setLatitude(myPlace.getLatitude());
            }
        }
        myAddress = myInfo.getAddress();
        // taAddress
        LocationInfo taInfo = new LocationInfo();
        String taAddress;
        if (taPlace != null) {
            taInfo.setAddress(taPlace.getAddress());
            taInfo.setLongitude(taPlace.getLongitude());
            taInfo.setLatitude(taPlace.getLatitude());
        }
        taAddress = taInfo.getAddress();
        // creator
        tvPlaceLeft.setText(taAddress);
        tvPlaceRight.setText(myAddress);
        // distance
        float distance = LocationHelper.distance(myInfo, taInfo);
        String distanceShow;
        if (distance < 1000) {
            distanceShow = String.format(Locale.getDefault(), "%.0fm", distance);
        } else {
            float km = distance / 1000;
            distanceShow = String.format(Locale.getDefault(), "%.1fkm", km);
        }
        String format = String.format(Locale.getDefault(), getString(R.string.distance_holder), distanceShow);
        tvDistance.setText(format);
    }

    private void refreshWeatherView() {
        int colorPrimary = ViewHelper.getColorPrimary(mActivity);
        int colorIcon = ContextCompat.getColor(mActivity, colorPrimary);
        String myTemp = "";
        int myC = 0;
        Drawable myDrawable = null;
        String taTemp = "";
        int taC = 0;
        Drawable taDrawable = null;
        // myWeather
        if (myPlace != null) {
            Weather myWeather = myPlace.getWeather();
            if (myWeather != null) {
                Weather.Condition myCondition = myWeather.getCondition();
                if (myCondition != null && !StringUtils.isEmpty(myCondition.getTemp())) {
                    String icon = myCondition.getIcon();
                    String temp = myCondition.getTemp();
                    myC = Integer.parseInt(temp);
                    myTemp = temp + "℃ " + ConvertHelper.getWeatherShowByIcon(icon);
                    int myIcon = ConvertHelper.getWeatherIconById(icon);
                    myDrawable = ViewHelper.getDrawable(mActivity, myIcon);
                    if (myDrawable != null) {
                        myDrawable.setTint(colorIcon);
                    }
                }
            }
        }
        // taWeather
        if (taPlace != null) {
            Weather taWeather = taPlace.getWeather();
            if (taWeather != null) {
                Weather.Condition taCondition = taWeather.getCondition();
                if (taCondition != null && !StringUtils.isEmpty(taCondition.getTemp())) {
                    String icon = taCondition.getIcon();
                    String temp = taCondition.getTemp();
                    taC = Integer.parseInt(temp);
                    taTemp = temp + "℃ " + ConvertHelper.getWeatherShowByIcon(icon);
                    int taIcon = ConvertHelper.getWeatherIconById(icon);
                    taDrawable = ViewHelper.getDrawable(mActivity, taIcon);
                    if (taDrawable != null) {
                        taDrawable.setTint(colorIcon);
                    }
                }
            }
        }
        tvWeatherLeft.setText(taTemp);
        tvWeatherRight.setText(myTemp);
        tvWeatherLeft.setCompoundDrawables(taDrawable, null, null, null);
        tvWeatherRight.setCompoundDrawables(myDrawable, null, null, null);
        // diff
        int abs = Math.abs((myC - taC));
        String format = String.format(Locale.getDefault(), getString(R.string.differ_holder_c), abs);
        tvWeatherDiffer.setText(format);
    }

    // 分手倒计时
    private Runnable getCoupleCountDownTask() {
        if (coupleCountDownTask == null) {
            coupleCountDownTask = new Runnable() {
                @Override
                public void run() {
                    Couple couple = SPHelper.getCouple();
                    long breakCountDown = Couple.getBreakCountDown(couple);
                    if (breakCountDown <= 0) {
                        RxEvent<Couple> event = new RxEvent<>(ConsHelper.EVENT_COUPLE_REFRESH, new Couple());
                        RxBus.post(event);
                        MyApp.get().getHandler().removeCallbacks(this);
                    } else {
                        String breakCountDownShow = Couple.getBreakCountDownShow(couple);
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

}
