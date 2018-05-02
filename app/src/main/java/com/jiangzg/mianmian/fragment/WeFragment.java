package com.jiangzg.mianmian.fragment;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.drawable.ScalingUtils;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.HelpActivity;
import com.jiangzg.mianmian.activity.couple.CoupleInfoActivity;
import com.jiangzg.mianmian.activity.couple.CouplePairActivity;
import com.jiangzg.mianmian.activity.couple.CoupleWallPaperActivity;
import com.jiangzg.mianmian.activity.settings.SettingsActivity;
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
import com.jiangzg.mianmian.helper.CheckHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.LocationHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GImageAvatarView;
import com.jiangzg.mianmian.view.GImageView;
import com.jiangzg.mianmian.view.GMarqueeText;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class WeFragment extends BasePagerFragment<WeFragment> {

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
    @BindView(R.id.ivSettings)
    ImageView ivSettings;

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

    private Call<Result> callHomeGet;
    private Call<Result> callPlaceGet;
    private Runnable coupleCountDownTask;
    private Observable<WallPaper> obWallPaperCountRefresh;
    private Observable<Couple> obCoupleRefresh;
    private Observable<LocationInfo> obLocationRefresh;
    private Place myPlace;
    private Place taPlace;

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
        int statusBarHeight = BarUtils.getStatusBarHeight(mActivity);
        RelativeLayout.LayoutParams paramsHelp = (RelativeLayout.LayoutParams) ivHelp.getLayoutParams();
        paramsHelp.setMargins(paramsHelp.leftMargin, paramsHelp.topMargin + statusBarHeight, paramsHelp.rightMargin, paramsHelp.bottomMargin);
        ivHelp.setLayoutParams(paramsHelp);
        RelativeLayout.LayoutParams paramsSettings = (RelativeLayout.LayoutParams) ivSettings.getLayoutParams();
        paramsSettings.setMargins(paramsSettings.leftMargin, paramsSettings.topMargin + statusBarHeight, paramsSettings.rightMargin, paramsSettings.bottomMargin);
        ivSettings.setLayoutParams(paramsSettings);
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
        obWallPaperCountRefresh = RxBus.register(ConsHelper.EVENT_WALL_PAPER_COUNT_REFRESH, new Action1<WallPaper>() {
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
    public void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callHomeGet);
        RetrofitHelper.cancel(callPlaceGet);
        stopCoupleCountDownTask();
        RxBus.unregister(ConsHelper.EVENT_COUPLE_REFRESH, obWallPaperCountRefresh);
        RxBus.unregister(ConsHelper.EVENT_COUPLE_REFRESH, obCoupleRefresh);
        RxBus.unregister(ConsHelper.EVENT_LOCATION_REFRESH, obLocationRefresh);
    }

    @OnClick({R.id.ivHelp, R.id.ivSettings, R.id.btnPair, R.id.vfWallPaper, R.id.llCoupleInfo,
            R.id.llPlace, R.id.llWeather, R.id.rlMenses, R.id.rlTrends, R.id.rlCoin})
    public void onViewClicked(View view) {
        Couple couple = SPHelper.getCouple();
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
            case R.id.vfWallPaper: // 背景图
                if (CheckHelper.isCoupleBreak(couple)) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    CoupleWallPaperActivity.goActivity(mActivity);
                }
                break;
            case R.id.llCoupleInfo: // cp信息
                if (CheckHelper.isCoupleBreak(couple)) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    CoupleInfoActivity.goActivity(mActivity);
                }
                break;
            case R.id.llPlace: // 地理信息
                if (CheckHelper.isCoupleBreak(couple)) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    if (PermUtils.isPermissionOK(mActivity, PermUtils.location)) {
                        if (LocationInfo.isLocationEnabled()) {
                            // TODO 等有两步手机了再说
                            //CoupleMapActivity.goActivity(mActivity);
                        } else {
                            showLocationEnableDialog();
                        }
                    } else {
                        requestLocationPermission();
                    }
                }
                break;
            case R.id.llWeather: // 天气信息
                if (CheckHelper.isCoupleBreak(couple)) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    if (PermUtils.isPermissionOK(mActivity, PermUtils.location)) {
                        if (LocationInfo.isLocationEnabled()) {
                            // TODO 等有两步手机了再说
                            //CoupleWeatherActivity.goActivity(mActivity);
                        } else {
                            showLocationEnableDialog();
                        }
                    } else {
                        requestLocationPermission();
                    }
                }
                break;
            case R.id.rlMenses: // 姨妈
                if (CheckHelper.isCoupleBreak(couple)) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    // TODO 等研究好了生理机制再说
                    //CoupleMensesActivity.goActivity(mActivity);
                }
                break;
            case R.id.rlTrends: // 动态
                if (CheckHelper.isCoupleBreak(couple)) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    // TODO 等book和topic做完了再说
                    //CoupleTrendsActivity.goActivity(mActivity);
                }
                break;
            case R.id.rlCoin: // 金币
                if (CheckHelper.isCoupleBreak(couple)) {
                    CouplePairActivity.goActivity(mActivity);
                } else {
                    // TODO 在topic做之前再说
                    //CoupleCoinActivity.goActivity(mActivity);
                }
                break;
        }
    }

    // 先检查位置权限 获取位置并推送，并在成功后会刷新本地view 再点一次才能进入地图
    private void requestLocationPermission() {
        PermUtils.requestPermissions(mActivity, ConsHelper.REQUEST_LOCATION, PermUtils.location, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                if (LocationInfo.isLocationEnabled()) {
                    ApiHelper.pushEntryPlace(mActivity);
                } else {
                    showLocationEnableDialog();
                }
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
            }
        });
    }

    // 再检查位置开关
    private void showLocationEnableDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .title(R.string.location_func_limit)
                .content(R.string.find_location_func_cant_use_normal_look_gps_is_open)
                .positiveText(R.string.go_to_setting)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent gps = IntentFactory.getGps();
                        ActivityTrans.start(mActivity, gps);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
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
                SPHelper.setUser(data.getUser());
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
        boolean permissionOK = PermUtils.isPermissionOK(mActivity, PermUtils.location);
        if (!permissionOK) return;
        LocationHelper.startLocation(true, new LocationHelper.LocationCallBack() {
            @Override
            public void onSuccess(LocationInfo info) {
                Place body = ApiHelper.getPlaceBody();
                callPlaceGet = new RetrofitHelper().call(API.class).coupleHomeGet(body);
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
                LogUtils.w(LOG_TAG, "不能获取地理位置");
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
        if (CheckHelper.isCoupleBreak(couple)) {
            // 已经分手，或者没有开始过
            btnPair.setVisibility(View.VISIBLE);
        } else {
            // 已经配对
            if (CheckHelper.isCoupleBreaking(couple)) {
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
            refreshAvatarRes();
            // 头像 + 名称
            String creatorAvatar = couple.getCreatorAvatar();
            String inviteeAvatar = couple.getInviteeAvatar();
            String creatorName = couple.getCreatorName();
            String inviteeName = couple.getInviteeName();
            ivAvatarLeft.setDateAvatar(creatorAvatar);
            ivAvatarRight.setDateAvatar(inviteeAvatar);
            tvNameLeft.setText(creatorName);
            tvNameRight.setText(inviteeName);
            refreshPlaceView();
            refreshWeatherView();
        }
    }

    // 墙纸
    private void refreshWallPaperView() {
        // 本地文件刷新
        refreshWallPaperRes();
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
            image.setDateWallPaper(ossKey);
            vfWallPaper.addView(image);
            vfWallPaper.setAutoStart(false);
            vfWallPaper.stopFlipping();
            return;
        }
        // 多图显示
        for (String ossKey : imageList) {
            GImageView image = getViewFlipperImage();
            image.setDateWallPaper(ossKey);
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
        User user = SPHelper.getUser();
        String left;
        String right;
        if (user.isCoupleCreator()) {
            left = myAddress;
            right = taAddress;
        } else {
            left = taAddress;
            right = myAddress;
        }
        tvPlaceLeft.setText(left);
        tvPlaceRight.setText(right);
        // distance
        float distance = LocationHelper.distance(myInfo, taInfo);
        String distanceShow = ConvertHelper.getDistanceShow(distance);
        String format = String.format(Locale.getDefault(), getString(R.string.distance_holder), distanceShow);
        tvDistance.setText(format);
    }

    private void refreshWeatherView() {
        String myTemp = "";
        int myC = 0;
        int myIcon = 0;
        String taTemp = "";
        int taC = 0;
        int taIcon = 0;
        // myWeather
        if (myPlace != null) {
            Weather myWeather = myPlace.getWeather();
            if (myWeather != null) {
                Weather.Condition myCondition = myWeather.getCondition();
                if (myCondition != null && !StringUtils.isEmpty(myCondition.getTemp())) {
                    String icon = myCondition.getIcon();
                    String temp = myCondition.getTemp();
                    myTemp = temp + "℃ " + ConvertHelper.getWeatherShowByIcon(icon);
                    myIcon = ConvertHelper.getWeatherIconByRes(icon);
                    myC = Integer.parseInt(temp);
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
                    taTemp = temp + "℃ " + ConvertHelper.getWeatherShowByIcon(icon);
                    taIcon = ConvertHelper.getWeatherIconByRes(icon);
                    taC = Integer.parseInt(temp);
                }
            }
        }
        // creator
        User user = SPHelper.getUser();
        String left;
        int leftIcon;
        String right;
        int rightIcon;
        if (user.isCoupleCreator()) {
            left = myTemp;
            leftIcon = myIcon;
            right = taTemp;
            rightIcon = taIcon;
        } else {
            left = taTemp;
            leftIcon = taIcon;
            right = myTemp;
            rightIcon = myIcon;
        }
        int colorPrimary = ViewHelper.getColorPrimary(mActivity);
        int colorIcon = ContextCompat.getColor(mActivity, colorPrimary);
        // left
        tvWeatherLeft.setText(left);
        Drawable myDrawable = ViewHelper.getDrawable(mActivity, leftIcon);
        if (myDrawable != null) {
            myDrawable.setTint(colorIcon);
        }
        tvWeatherLeft.setCompoundDrawables(myDrawable, null, null, null);
        // right
        tvWeatherRight.setText(right);
        Drawable taDrawable = ViewHelper.getDrawable(mActivity, rightIcon);
        if (taDrawable != null) {
            taDrawable.setTint(colorIcon);
        }
        tvWeatherRight.setCompoundDrawables(taDrawable, null, null, null);
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

    // 刷新本地的avatar
    private void refreshAvatarRes() {
        final Couple couple = SPHelper.getCouple();
        String creatorAvatar = couple.getCreatorAvatar();
        String inviteeAvatar = couple.getInviteeAvatar();
        final List<String> imageList = new ArrayList<>();
        imageList.add(creatorAvatar);
        imageList.add(inviteeAvatar);
        // file 开线程
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                // 删旧的
                List<File> fileList = ResHelper.getAvatarDirFiles();
                if (fileList != null && fileList.size() > 0) {
                    // 本地有avatar的文件
                    for (File file : fileList) {
                        // 文件不存在则直接检查下一个文件
                        if (file == null) continue;
                        boolean find = false;
                        // 检查是不是oss里对应的文件
                        for (String ossKey : imageList) {
                            String name = ConvertHelper.getNameByOssPath(ossKey);
                            if (file.getName().trim().equals(name)) {
                                // 是对应的文件，直接检查下一个文件
                                find = true;
                                break;
                            }
                        }
                        // 都检查完了，不是对应的文件则删除
                        if (!find) {
                            LogUtils.w(LOG_TAG, "refreshAvatarRes: 删除了不匹配的avatar文件 == " + file.getAbsolutePath());
                            ResHelper.deleteFileInBackground(file);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshAvatarRes: 发现了匹配的avatar文件 == " + file.getAbsolutePath());
                        }
                    }
                } else {
                    LogUtils.w(LOG_TAG, "refreshAvatarRes: 没有发现avatar的存储目录");
                }
                // 加新的
                List<File> newFileList = ResHelper.getAvatarDirFiles();
                for (String ossKey : imageList) {
                    if (newFileList != null && newFileList.size() > 0) {
                        // 本地有信息，则检查是都已下载
                        boolean find = CheckHelper.isAvatarExists(ossKey);
                        // 都检查完了，没下载过的直接下载
                        if (!find) {
                            LogUtils.w(LOG_TAG, "refreshAvatarRes: 没发现匹配的oss对象 == " + ossKey);
                            OssHelper.downloadAvatar(ossKey);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshAvatarRes: 发现了匹配的oss对象 == " + ossKey);
                        }
                    } else {
                        // 本地无信息，则直接下载
                        LogUtils.w(LOG_TAG, "refreshAvatarRes: 没有发现avatar的新的存储目录");
                        OssHelper.downloadAvatar(ossKey);
                    }
                }
            }
        });
    }

    // 刷新本地的wp
    private void refreshWallPaperRes() {
        WallPaper wallPaper = SPHelper.getWallPaper();
        final List<String> imageList = wallPaper.getImageList();
        if (imageList == null || imageList.size() <= 0) return;
        // file 开线程
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                // 删旧的
                List<File> fileList = ResHelper.getWallPaperDirFiles();
                if (fileList != null && fileList.size() > 0) {
                    // 本地有wp的文件
                    for (File file : fileList) {
                        // 文件不存在则直接检查下一个文件
                        if (file == null) continue;
                        boolean find = false;
                        // 检查是不是oss里对应的文件
                        for (String ossKey : imageList) {
                            String name = ConvertHelper.getNameByOssPath(ossKey);
                            if (file.getName().trim().equals(name)) {
                                // 是对应的文件，直接检查下一个文件
                                find = true;
                                break;
                            }
                        }
                        // 都检查完了，不是对应的文件则删除
                        if (!find) {
                            LogUtils.w(LOG_TAG, "refreshWallPaperRes: 删除了不匹配的wp文件 == " + file.getAbsolutePath());
                            ResHelper.deleteFileInBackground(file);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshWallPaperRes: 发现了匹配的wp文件 == " + file.getAbsolutePath());
                        }
                    }
                } else {
                    LogUtils.w(LOG_TAG, "refreshWallPaperRes: 没有发现WallPaper的存储目录");
                }
                // 加新的
                List<File> newFileList = ResHelper.getWallPaperDirFiles();
                for (String ossKey : imageList) {
                    if (newFileList != null && newFileList.size() > 0) {
                        // 本地有信息，则检查是都已下载
                        boolean find = CheckHelper.isWallPaperExists(ossKey);
                        // 都检查完了，没下载过的直接下载
                        if (!find) {
                            LogUtils.w(LOG_TAG, "refreshWallPaperRes: 没发现匹配的oss对象 == " + ossKey);
                            OssHelper.downloadWall(ossKey);
                        } else {
                            LogUtils.i(LOG_TAG, "refreshWallPaperRes: 发现了匹配的oss对象 == " + ossKey);
                        }
                    } else {
                        // 本地无信息，则直接下载
                        LogUtils.w(LOG_TAG, "refreshWallPaperRes: 没有发现WallPaper的新的存储目录");
                        OssHelper.downloadWall(ossKey);
                    }
                }
            }
        });
    }

}
