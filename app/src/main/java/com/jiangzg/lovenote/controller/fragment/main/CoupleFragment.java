package com.jiangzg.lovenote.controller.fragment.main;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.couple.CoupleInfoActivity;
import com.jiangzg.lovenote.controller.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.controller.activity.couple.CouplePlaceActivity;
import com.jiangzg.lovenote.controller.activity.couple.CoupleWallPaperActivity;
import com.jiangzg.lovenote.controller.activity.couple.CoupleWeatherActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.adapter.couple.HomeWallPagerAdapter;
import com.jiangzg.lovenote.controller.fragment.base.BaseFragment;
import com.jiangzg.lovenote.controller.fragment.base.BasePagerFragment;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.CountHelper;
import com.jiangzg.lovenote.helper.common.OssResHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.common.WeatherHelper;
import com.jiangzg.lovenote.helper.system.LocationHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.ModelShow;
import com.jiangzg.lovenote.model.entity.Place;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.model.entity.WallPaper;
import com.jiangzg.lovenote.model.entity.WeatherToday;
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

public class CoupleFragment extends BasePagerFragment<CoupleFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.ivHelp)
    ImageView ivHelp;
    @BindView(R.id.ivWallPaper)
    ImageView ivWallPaper;
    @BindView(R.id.ivBg)
    ImageView ivBg;
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
    @BindView(R.id.tvTogether)
    TextView tvTogether;

    @BindView(R.id.cvPlaceWeather)
    CardView cvPlaceWeather;
    @BindView(R.id.vLinePlaceWeather)
    View vLinePlaceWeather;
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

    private Place myPlace;
    private Place taPlace;
    private WeatherToday myWeatherToday;
    private WeatherToday taWeatherToday;
    private Runnable coupleCountDownTask;

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
        // show
        ModelShow modelShow = SPHelper.getModelShow();
        llPlace.setVisibility(modelShow.isCouplePlace() ? View.VISIBLE : View.GONE);
        llWeather.setVerticalGravity(modelShow.isCoupleWeather() ? View.VISIBLE : View.GONE);
        vLinePlaceWeather.setVisibility((modelShow.isCouplePlace() && modelShow.isCoupleWeather()) ? View.VISIBLE : View.GONE);
        cvPlaceWeather.setVisibility((modelShow.isCouplePlace() || modelShow.isCoupleWeather()) ? View.VISIBLE : View.GONE);
        // listener
        srl.setOnRefreshListener(this::refreshData);
    }

    protected void loadData() {
        // event
        Observable<Couple> obCoupleRefresh = RxBus.register(RxBus.EVENT_COUPLE_REFRESH, couple -> refreshData());
        pushBus(RxBus.EVENT_COUPLE_REFRESH, obCoupleRefresh);
        Observable<WallPaper> obWallPaperRefresh = RxBus.register(RxBus.EVENT_WALL_PAPER_REFRESH, wallPaper -> refreshWallPaperView());
        pushBus(RxBus.EVENT_WALL_PAPER_REFRESH, obWallPaperRefresh);
        // refresh
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
        stopCoupleCountDownTask();
    }

    @OnClick({R.id.ivHelp, R.id.ivWallPaper, R.id.btnPair, R.id.llCoupleInfo, R.id.llPlace, R.id.llWeather})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivHelp: // 帮助文档
                HelpActivity.goActivity(mFragment, HelpActivity.INDEX_COUPLE_HOME);
                break;
            case R.id.ivWallPaper:  // 背景图
                CoupleWallPaperActivity.goActivity(mFragment);
                break;
            case R.id.btnPair: // 配对
                CouplePairActivity.goActivity(mFragment);
                break;
            case R.id.llCoupleInfo: // cp信息
                CoupleInfoActivity.goActivity(mFragment);
                break;
            case R.id.llPlace: // 地址信息
                CouplePlaceActivity.goActivity(mFragment);
                break;
            case R.id.llWeather: // 天气信息
                CoupleWeatherActivity.goActivity(mFragment, myPlace, taPlace);
                break;
        }
    }

    // 数据刷新
    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // oss 最好是刷新一次，防止状态变化 oss不同步
        ApiHelper.ossInfoUpdate();
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).coupleHomeGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                SPHelper.setMe(data.getUser());
                SPHelper.setTa(data.getTa());
                SPHelper.setWallPaper(data.getWallPaper());
                refreshView();
                // 刷新地址
                refreshPlaceDate();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    // 获取自己的位置并上传
    private void refreshPlaceDate() {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) return; // 还是要检查的，防止后台返回错误
        if (!LocationHelper.isLocationPermissionOK(mActivity)) return; // 没同意不要弹
        LocationHelper.startLocation(mActivity, true, new LocationHelper.LocationCallBack() {
            @Override
            public void onSuccess(LocationInfo info) {
                if (info == null || (info.getLongitude() == 0 && info.getLatitude() == 0)) {
                    return;
                }
                // 设备位置信息获取成功
                Place body = new Place();
                body.setLongitude(info.getLongitude());
                body.setLatitude(info.getLatitude());
                body.setCountry(info.getCountry());
                body.setProvince(info.getProvince());
                body.setCity(info.getCity());
                body.setDistrict(info.getDistrict());
                body.setStreet(info.getStreet());
                body.setAddress(info.getAddress());
                body.setCityId(info.getCityId());
                // api
                Call<Result> api = new RetrofitHelper().call(API.class).couplePlacePush(body);
                RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
                    @Override
                    public void onResponse(int code, String message, Result.Data data) {
                        myPlace = data.getPlaceMe();
                        taPlace = data.getPlaceTa();
                        refreshPlaceView();
                        // myWeatherToday = data.getWeatherTodayMe();
                        // taWeatherToday = data.getWeatherTodayTa();
                        // refreshWeatherView();
                        refreshWeatherData();
                    }

                    @Override
                    public void onFailure(int code, String message, Result.Data data) {
                    }
                });
                pushApi(api);
            }

            @Override
            public void onFailed(String errMsg) {
                LogUtils.w(CoupleFragment.class, "startLocation", errMsg);
                ToastUtils.show(String.format(Locale.getDefault(), mActivity.getString(R.string.address_get_fail_reason_colon_holder), errMsg));
            }
        });
    }

    // 刷新天气数据
    private void refreshWeatherData() {
        if (myPlace != null) {
            LocationHelper.getWeatherToday(mActivity, myPlace.getCity(), new LocationHelper.WeatherTodayCallBack() {
                @Override
                public void onSuccess(WeatherToday weather) {
                    myWeatherToday = weather;
                    refreshWeatherView();
                }

                @Override
                public void onFailed(String errMsg) {
                }
            });
        }
        if (taPlace != null) {
            LocationHelper.getWeatherToday(mActivity, taPlace.getCity(), new LocationHelper.WeatherTodayCallBack() {
                @Override
                public void onSuccess(WeatherToday weather) {
                    taWeatherToday = weather;
                    refreshWeatherView();
                }

                @Override
                public void onFailed(String errMsg) {
                }
            });
        }
    }

    private void refreshView() {
        if (mActivity == null || !mFragment.isAdded()) return; // 防止已经脱离后加载
        ivBg.setVisibility(View.GONE);
        vpWallPaper.setVisibility(View.GONE);
        btnPair.setVisibility(View.GONE);
        tvAddWallPaper.setVisibility(View.GONE);
        tvCoupleCountDown.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);
        // 开始判断
        Couple couple = SPHelper.getCouple();
        if (UserHelper.isCoupleBreak(couple)) {
            // 已经分手，或者没有开始过
            ivBg.setVisibility(View.VISIBLE);
            btnPair.setVisibility(View.VISIBLE);
        } else {
            // 已经配对
            llBottom.setVisibility(View.VISIBLE);
            if (UserHelper.isCoupleBreaking(couple)) {
                // 正在分手
                tvCoupleCountDown.setVisibility(View.VISIBLE);
                MyApp.get().getHandler().post(getCoupleCountDownTask());
            } else {
                // 在一起
                refreshWallPaperView();
            }
            // 头像文件刷新
            List<String> imageList = new ArrayList<>();
            if (couple != null) imageList.add(couple.getCreatorAvatar());
            if (couple != null) imageList.add(couple.getInviteeAvatar());
            OssResHelper.refreshResWithDelNoExist(OssResHelper.TYPE_COUPLE_AVATAR, imageList);
            // 头像 + 名称
            User me = SPHelper.getMe();
            User ta = SPHelper.getTa();
            String myName = UserHelper.getMyName(me);
            String taName = UserHelper.getTaName(me);
            String myAvatar = UserHelper.getMyAvatar(me);
            String taAvatar = UserHelper.getTaAvatar(me);
            int togetherDay = UserHelper.getCoupleTogetherDay(couple);
            // view
            ivAvatarLeft.setData(taAvatar, ta);
            ivAvatarRight.setData(myAvatar, me);
            tvNameLeft.setText(taName);
            tvNameRight.setText(myName);
            tvTogether.setText(String.valueOf(togetherDay));
            // 刷新位置+天气
            refreshPlaceView();
            refreshWeatherView();
        }
    }

    // 墙纸
    private void refreshWallPaperView() {
        if (mActivity == null || !mFragment.isAdded()) return; // 防止已经脱离后加载
        List<String> imageList = SPHelper.getWallPaper().getContentImageList();
        // 无图显示
        if (imageList == null || imageList.size() <= 0) {
            tvAddWallPaper.setVisibility(View.VISIBLE);
            vpWallPaper.setVisibility(View.GONE);
            // 删除本地文件
            OssResHelper.refreshResWithDelNoExist(OssResHelper.TYPE_COUPLE_WALL, new ArrayList<>());
            return;
        }
        // 有图显示
        tvAddWallPaper.setVisibility(View.GONE);
        vpWallPaper.setVisibility(View.VISIBLE);
        // 本地文件刷新
        OssResHelper.refreshResWithDelNoExist(OssResHelper.TYPE_COUPLE_WALL, imageList);
        // vp适配器
        HomeWallPagerAdapter adapter = (HomeWallPagerAdapter) vpWallPaper.getAdapter();
        if (adapter == null) {
            adapter = new HomeWallPagerAdapter(mActivity, vpWallPaper);
            vpWallPaper.setAdapter(adapter);
        }
        // 单图显示
        if (imageList.size() <= 1) {
            String ossKey = imageList.get(0);
            adapter.newData(ossKey);
            return;
        }
        // 多图显示，随机顺序
        adapter.newData(imageList);
    }

    private void refreshPlaceView() {
        if (mActivity == null || !mFragment.isAdded()) return; // 防止已经脱离后加载
        // address
        String addressDef = mActivity.getString(R.string.now_no_address_info);
        String myAddress = (myPlace == null) ? "" : myPlace.getAddress();
        myAddress = StringUtils.isEmpty(myAddress) ? addressDef : myAddress;
        String taAddress = (taPlace == null) ? "" : taPlace.getAddress();
        taAddress = StringUtils.isEmpty(taAddress) ? addressDef : taAddress;
        // distance
        float distance = 0F;
        if (myPlace != null && taPlace != null) {
            distance = LocationHelper.distance(myPlace.getLongitude(), myPlace.getLatitude(), taPlace.getLongitude(), taPlace.getLatitude());
        }
        String distanceShow = CountHelper.getShowDistance(distance);
        String format = String.format(Locale.getDefault(), mActivity.getString(R.string.distance_space_holder), distanceShow);
        // view
        if (tvPlaceRight != null) {
            tvPlaceRight.setText(myAddress);
        }
        if (tvPlaceLeft != null) {
            tvPlaceLeft.setText(taAddress);
        }
        if (tvDistance != null) {
            tvDistance.setText(format);
        }
    }

    private void refreshWeatherView() {
        if (mActivity == null || !mFragment.isAdded()) return; // 防止已经脱离后加载
        int colorIcon = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
        String weatherDef = mActivity.getString(R.string.now_no_weather_info);
        // myWeather
        int myTemp = 520;
        String myWeatherShow = weatherDef;
        Drawable myIcon = null;
        if (myWeatherToday != null && !StringUtils.isEmpty(myWeatherToday.getTemp())) {
            int weatherIcon = WeatherHelper.getIconById(myWeatherToday.getIcon());
            myIcon = ViewUtils.getDrawable(mActivity, weatherIcon, tvWeatherRight.getHeight(), tvWeatherRight.getHeight());
            if (myIcon != null) {
                myIcon.setTint(colorIcon);
            }
            String temp = myWeatherToday.getTemp();
            if (StringUtils.isNumber(temp)) {
                myTemp = Integer.parseInt(temp);
            }
            myWeatherShow = temp + "℃ " + myWeatherToday.getCondition();
        }
        // taWeather
        int taTemp = 520;
        String taWeatherShow = weatherDef;
        Drawable taIcon = null;
        if (taWeatherToday != null && !StringUtils.isEmpty(taWeatherToday.getTemp())) {
            int weatherIcon = WeatherHelper.getIconById(taWeatherToday.getIcon());
            taIcon = ViewUtils.getDrawable(mActivity, weatherIcon, tvWeatherLeft.getHeight(), tvWeatherLeft.getHeight());
            if (taIcon != null) {
                taIcon.setTint(colorIcon);
            }
            String temp = taWeatherToday.getTemp();
            if (StringUtils.isNumber(temp)) {
                taTemp = Integer.parseInt(temp);
            }
            taWeatherShow = temp + "℃ " + taWeatherToday.getCondition();
        }
        // diff
        String diff;
        if (myTemp == 520 || taTemp == 520) {
            diff = String.format(Locale.getDefault(), mActivity.getString(R.string.differ_space_holder), "-℃");
        } else {
            String abs = Math.abs((myTemp - taTemp)) + "℃";
            diff = String.format(Locale.getDefault(), mActivity.getString(R.string.differ_space_holder), abs);
        }
        // view
        if (tvWeatherRight != null) {
            tvWeatherRight.setText(myWeatherShow);
            tvWeatherRight.setCompoundDrawables(myIcon, null, null, null);
        }
        if (tvWeatherLeft != null) {
            tvWeatherLeft.setText(taWeatherShow);
            tvWeatherLeft.setCompoundDrawables(taIcon, null, null, null);
        }
        if (tvWeatherDiffer != null) {
            tvWeatherDiffer.setText(diff);
        }
    }

    // 分手倒计时
    private Runnable getCoupleCountDownTask() {
        if (coupleCountDownTask == null) {
            coupleCountDownTask = new Runnable() {
                @Override
                public void run() {
                    Couple couple = SPHelper.getCouple();
                    long breakCountDown = UserHelper.getCoupleBreakCountDown(couple);
                    if (breakCountDown <= 0) {
                        RxBus.post(new RxBus.Event<>(RxBus.EVENT_COUPLE_REFRESH, couple));
                        stopCoupleCountDownTask();
                    } else {
                        String breakCountDownShow = getBreakCountDownShow(breakCountDown);
                        tvCoupleCountDown.setText(breakCountDownShow);
                        MyApp.get().getHandler().postDelayed(this, TimeUnit.SEC);
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

    private String getBreakCountDownShow(long breakCountDown) {
        long hour = breakCountDown / (TimeUnit.HOUR / TimeUnit.SEC);
        String hourF = hour >= 10 ? "" : "0";
        long min = (breakCountDown - hour * (TimeUnit.HOUR / TimeUnit.SEC)) / (TimeUnit.MIN / TimeUnit.SEC);
        String minF = min >= 10 ? ":" : ":0";
        long sec = breakCountDown - hour * (TimeUnit.HOUR / TimeUnit.SEC) - min * (TimeUnit.MIN / TimeUnit.SEC);
        String secF = sec >= 10 ? ":" : ":0";
        return hourF + hour + minF + min + secF + sec;
    }

}
