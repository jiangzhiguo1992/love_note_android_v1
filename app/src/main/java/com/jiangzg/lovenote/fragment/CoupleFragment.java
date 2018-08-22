package com.jiangzg.lovenote.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.couple.CoupleInfoActivity;
import com.jiangzg.lovenote.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.activity.couple.CouplePlaceActivity;
import com.jiangzg.lovenote.activity.couple.CoupleWallPaperActivity;
import com.jiangzg.lovenote.activity.couple.CoupleWeatherActivity;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.adapter.CoupleHomeWallPagerAdapter;
import com.jiangzg.lovenote.base.BaseFragment;
import com.jiangzg.lovenote.base.BasePagerFragment;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Help;
import com.jiangzg.lovenote.domain.Place;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.RxEvent;
import com.jiangzg.lovenote.domain.User;
import com.jiangzg.lovenote.domain.WallPaper;
import com.jiangzg.lovenote.domain.WeatherToday;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.LocationHelper;
import com.jiangzg.lovenote.helper.OssResHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.helper.WeatherHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GMarqueeText;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;
import com.jiangzg.lovenote.view.WallPaperPager;

import java.util.ArrayList;
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
    @BindView(R.id.ivHelp)
    ImageView ivHelp;
    @BindView(R.id.ivWallPaper)
    ImageView ivWallPaper;
    @BindView(R.id.vpWallPaper)
    WallPaperPager vpWallPaper;

    @BindView(R.id.tvCoupleCountDown)
    TextView tvCoupleCountDown;
    @BindView(R.id.tvAddWallPaper)
    TextView tvAddWallPaper;
    @BindView(R.id.btnPair)
    Button btnPair;

    @BindView(R.id.llBottom)
    LinearLayout llBottom;
    @BindView(R.id.llCoupleInfo)
    LinearLayout llCoupleInfo;
    @BindView(R.id.ivAvatarLeft)
    FrescoAvatarView ivAvatarLeft;
    @BindView(R.id.ivAvatarRight)
    FrescoAvatarView ivAvatarRight;
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
    private Place myPlace;
    private Place taPlace;
    private WeatherToday myWeatherToday;
    private WeatherToday taWeatherToday;

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
        // refresh
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callHomeGet);
        RetrofitHelper.cancel(callPlaceGet);
        RxBus.unregister(ConsHelper.EVENT_COUPLE_REFRESH, obWallPaperRefresh);
        RxBus.unregister(ConsHelper.EVENT_COUPLE_REFRESH, obCoupleRefresh);
        stopCoupleCountDownTask();
    }

    @OnClick({R.id.ivHelp, R.id.ivWallPaper, R.id.btnPair,
            R.id.llCoupleInfo, R.id.llPlace, R.id.llWeather})
    public void onViewClicked(View view) {
        Couple couple = SPHelper.getCouple();
        switch (view.getId()) {
            case R.id.ivHelp: // 帮助文档
                HelpActivity.goActivity(mFragment, Help.INDEX_COUPLE_HOME);
                break;
            case R.id.ivWallPaper:  // 背景图
                if (Couple.isBreak(couple)) {
                    CouplePairActivity.goActivity(mFragment);
                } else {
                    CoupleWallPaperActivity.goActivity(mFragment);
                }
                break;
            case R.id.btnPair: // 配对
                CouplePairActivity.goActivity(mFragment);
                break;
            case R.id.llCoupleInfo: // cp信息
                if (Couple.isBreak(couple)) {
                    CouplePairActivity.goActivity(mFragment);
                } else {
                    CoupleInfoActivity.goActivity(mFragment);
                }
                break;
            case R.id.llPlace: // 地址信息
                if (Couple.isBreak(couple)) {
                    CouplePairActivity.goActivity(mFragment);
                } else if (LocationHelper.checkLocationEnable(mActivity)) {
                    CouplePlaceActivity.goActivity(mFragment);
                }
                break;
            case R.id.llWeather: // 天气信息
                if (Couple.isBreak(couple)) {
                    CouplePairActivity.goActivity(mFragment);
                } else if (LocationHelper.checkLocationEnable(mActivity)) {
                    CoupleWeatherActivity.goActivity(mFragment);
                }
                break;
        }
    }

    // 数据刷新
    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callHomeGet = new RetrofitHelper().call(API.class).coupleHomeGet();
        RetrofitHelper.enqueue(callHomeGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                SPHelper.setMe(data.getUserMe());
                SPHelper.setTa(data.getUserTa());
                SPHelper.setTogetherDay(data.getTogetherDay());
                SPHelper.setWallPaper(data.getWallPaper());
                myPlace = data.getPlaceMe();
                taPlace = data.getPlaceTa();
                myWeatherToday = data.getWeatherTodayMe();
                taWeatherToday = data.getWeatherTodayTa();
                refreshView();
                // 刷新地址+天气
                refreshPlaceWeatherDate();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    // 获取自己的位置并上传
    private void refreshPlaceWeatherDate() {
        if (Couple.isBreak(SPHelper.getCouple())) return; // 还是要检查的，防止后台返回错误
        if (!LocationHelper.checkLocationEnable(mActivity)) return;
        LocationHelper.startLocation(mActivity, true, new LocationHelper.LocationCallBack() {
            @Override
            public void onSuccess(LocationInfo info) {
                // 设备位置信息获取成功
                Place body = ApiHelper.getPlaceBody(info);
                if (body == null || (body.getLongitude() == 0 && body.getLatitude() == 0)) return;
                callPlaceGet = new RetrofitHelper().call(API.class).couplePlacePush(body);
                RetrofitHelper.enqueue(callPlaceGet, null, new RetrofitHelper.CallBack() {
                    @Override
                    public void onResponse(int code, String message, Result.Data data) {
                        myPlace = data.getPlaceMe();
                        taPlace = data.getPlaceTa();
                        myWeatherToday = data.getWeatherTodayMe();
                        taWeatherToday = data.getWeatherTodayTa();
                        refreshPlaceView();
                        refreshWeatherView();
                    }

                    @Override
                    public void onFailure(int code, String message, Result.Data data) {
                    }
                });
            }

            @Override
            public void onFailed(String errMsg) {
                LogUtils.w(CoupleFragment.class, "startLocation", errMsg);
                ToastUtils.show(String.format(Locale.getDefault(), getString(R.string.address_get_fail_reason_colon_holder), errMsg));
            }
        });
    }

    // 视图刷新 所有cp的更新都要放到sp里，集中存放
    private void refreshView() {
        vpWallPaper.setVisibility(View.GONE);
        btnPair.setVisibility(View.GONE);
        tvAddWallPaper.setVisibility(View.GONE);
        tvCoupleCountDown.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);

        User user = SPHelper.getMe();
        Couple couple = user.getCouple();
        if (Couple.isBreak(couple)) {
            // 已经分手，或者没有开始过
            btnPair.setVisibility(View.VISIBLE);
        } else {
            // 已经配对
            llBottom.setVisibility(View.VISIBLE);
            if (Couple.isBreaking(couple)) {
                // 正在分手
                tvCoupleCountDown.setVisibility(View.VISIBLE);
                MyApp.get().getHandler().post(getCoupleCountDownTask());
            } else {
                // 没分手
                vpWallPaper.setVisibility(View.VISIBLE);
                // 开始墙纸动画
                refreshWallPaperView();
            }
            // 头像文件刷新
            List<String> imageList = new ArrayList<>();
            imageList.add(couple.getCreatorAvatar());
            imageList.add(couple.getInviteeAvatar());
            OssResHelper.refreshResWithDelNoExist(OssResHelper.TYPE_COUPLE_AVATAR, imageList);
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
        if (vpWallPaper.getVisibility() != View.VISIBLE) return;
        //if (vfWallPaper.getVisibility() != View.VISIBLE) return;
        WallPaper wallPaper = SPHelper.getWallPaper();
        List<String> imageList;
        // 清除view
        //vfWallPaper.removeAllViews();
        // 无图显示
        if (wallPaper == null || wallPaper.getContentImageList() == null || wallPaper.getContentImageList().size() <= 0) {
            if (vpWallPaper.getAdapter() == null || vpWallPaper.getAdapter().getCount() <= 0) {
                tvAddWallPaper.setVisibility(View.VISIBLE);
            } else {
                tvAddWallPaper.setVisibility(View.GONE);
            }
            // 删除本地文件
            OssResHelper.refreshResWithDelNoExist(OssResHelper.TYPE_COUPLE_WALL, new ArrayList<String>());
            return;
        } else {
            tvAddWallPaper.setVisibility(View.GONE);
            // 本地文件刷新
            imageList = wallPaper.getContentImageList();
            OssResHelper.refreshResWithDelNoExist(OssResHelper.TYPE_COUPLE_WALL, imageList);
        }
        // vp适配器
        CoupleHomeWallPagerAdapter adapter = (CoupleHomeWallPagerAdapter) vpWallPaper.getAdapter();
        if (adapter == null) {
            adapter = new CoupleHomeWallPagerAdapter(mActivity, vpWallPaper);
            vpWallPaper.setAdapter(adapter);
        }
        // 单图显示
        if (imageList.size() == 1) {
            String ossKey = imageList.get(0);
            adapter.newData(ossKey);
            return;
        }
        // 多图显示，随机顺序
        adapter.newData(imageList);
    }

    private void refreshPlaceView() {
        String addressDef = getString(R.string.now_no_address_info);
        // myAddress
        String myAddress = (myPlace == null) ? "" : myPlace.getAddress();
        myAddress = StringUtils.isEmpty(myAddress) ? addressDef : myAddress;
        // taAddress
        String taAddress = (taPlace == null) ? "" : taPlace.getAddress();
        taAddress = StringUtils.isEmpty(taAddress) ? addressDef : taAddress;
        // distance
        float distance = 0F;
        if (myPlace != null && taPlace != null) {
            distance = LocationHelper.distance(myPlace.getLongitude(), myPlace.getLatitude(), taPlace.getLongitude(), taPlace.getLatitude());
        }
        String distanceShow;
        if (distance >= 1000 * 100) {
            float km = distance / 1000;
            distanceShow = String.format(Locale.getDefault(), "%.0fkm", km);
        } else if (distance >= 1000) {
            float km = distance / 1000;
            distanceShow = String.format(Locale.getDefault(), "%.1fkm", km);
        } else if (distance > 0) {
            distanceShow = String.format(Locale.getDefault(), "%.0fm", distance);
        } else {
            distanceShow = "-m";
        }
        String format = String.format(Locale.getDefault(), getString(R.string.distance_space_holder), distanceShow);
        // view
        tvPlaceRight.setText(myAddress);
        tvPlaceLeft.setText(taAddress);
        tvDistance.setText(format);
    }

    private void refreshWeatherView() {
        int colorIcon = ContextCompat.getColor(mActivity, ViewHelper.getColorPrimary(mActivity));
        String weatherDef = getString(R.string.now_no_weather_info);
        // myWeather
        int myTemp = 520;
        String myWeatherShow = "";
        Drawable myIcon = null;
        if (myWeatherToday != null && !StringUtils.isEmpty(myWeatherToday.getTemp())) {
            int weatherIcon = WeatherHelper.getWeatherIconById(myWeatherToday.getIcon());
            myIcon = ViewHelper.getDrawable(mActivity, weatherIcon);
            if (myIcon != null) {
                myIcon.setTint(colorIcon);
            }
            String temp = myWeatherToday.getTemp();
            if (StringUtils.isNumber(temp)) {
                myTemp = Integer.parseInt(temp);
            }
            myWeatherShow = temp + "℃ " + myWeatherToday.getCondition() + " " + myWeatherToday.getHumidity();
        }
        myWeatherShow = StringUtils.isEmpty(myWeatherShow) ? weatherDef : myWeatherShow;
        // taWeather
        int taTemp = 520;
        String taWeatherShow = "";
        Drawable taIcon = null;
        if (taWeatherToday != null && !StringUtils.isEmpty(taWeatherToday.getTemp())) {
            int weatherIcon = WeatherHelper.getWeatherIconById(taWeatherToday.getIcon());
            taIcon = ViewHelper.getDrawable(mActivity, weatherIcon);
            if (taIcon != null) {
                taIcon.setTint(colorIcon);
            }
            String temp = taWeatherToday.getTemp();
            if (StringUtils.isNumber(temp)) {
                taTemp = Integer.parseInt(temp);
            }
            taWeatherShow = temp + "℃ " + taWeatherToday.getCondition() + " " + taWeatherToday.getHumidity();
        }
        taWeatherShow = StringUtils.isEmpty(taWeatherShow) ? weatherDef : taWeatherShow;
        // diff
        String diff;
        if (myTemp == 520 || taTemp == 520) {
            diff = String.format(Locale.getDefault(), getString(R.string.differ_space_holder), "-℃");
        } else {
            String abs = Math.abs((myTemp - taTemp)) + "℃";
            diff = String.format(Locale.getDefault(), getString(R.string.differ_space_holder), abs);
        }
        // view
        tvWeatherRight.setText(myWeatherShow);
        tvWeatherRight.setCompoundDrawables(myIcon, null, null, null);
        tvWeatherLeft.setText(taWeatherShow);
        tvWeatherLeft.setCompoundDrawables(taIcon, null, null, null);
        tvWeatherDiffer.setText(diff);
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
                        stopCoupleCountDownTask();
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
