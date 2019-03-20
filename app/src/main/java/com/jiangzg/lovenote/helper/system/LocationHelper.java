package com.jiangzg.lovenote.helper.system;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.WeatherHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.Place;
import com.jiangzg.lovenote.model.entity.WeatherToday;

/**
 * Created by JZG on 2018/4/18.
 * 高德地图
 */
public class LocationHelper {

    public static boolean checkLocationEnable(final Fragment fragment) {
        return !(fragment == null || fragment.getActivity() == null) && checkLocationEnable(fragment.getActivity());
    }

    // 检查并请求位置服务
    public static boolean checkLocationEnable(final Activity activity) {
        if (!PermUtils.isPermissionOK(activity, PermUtils.location)) {
            // 权限不过关
            PermUtils.requestPermissions(activity, BaseActivity.REQUEST_LOCATION, PermUtils.location, new PermUtils.OnPermissionListener() {
                @Override
                public void onPermissionGranted(int requestCode, String[] permissions) {
                    // 通过之后 用户再点击一次来进行后面的判断
                }

                @Override
                public void onPermissionDenied(int requestCode, String[] permissions) {
                    DialogHelper.showGoPermDialog(activity);
                }
            });
            return false;
        }
        //else if (!LocationInfo.isLocationEnabled()) {
        //    // GPS不过关 废弃 高德可以进行wifi和基站定位
        //    MaterialDialog dialog = DialogHelper.getBuild(activity)
        //            .cancelable(true)
        //            .canceledOnTouchOutside(false)
        //            .title(“位置功能受限!“)
        //            .content(“发现位置功能不能正常使用，查看是不是GPS没有开启“)
        //            .positiveText(R.string.go_to_setting)
        //            .negativeText(R.string.i_think_again)
        //            .onPositive(new MaterialDialog.SingleButtonCallback() {
        //                @Override
        //                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        //                    Intent gps = IntentFactory.getGps();
        //                    ActivityTrans.start(activity, gps);
        //                }
        //            })
        //            .build();
        //    DialogHelper.showWithAnim(dialog);
        //    return false;
        //}
        return true;
    }

    /* 定位 并 回调信息 */
    public interface LocationCallBack {
        void onSuccess(LocationInfo info);

        void onFailed(String errMsg);
    }

    /* 开启定位 */
    public static AMapLocationClient startLocation(Activity activity, boolean once, LocationCallBack callBack) {
        if (!LocationHelper.checkLocationEnable(activity)) return null;
        //初始化定位
        AMapLocationClient mLocationClient = new AMapLocationClient(MyApp.get());
        //给定位客户端对象设置定位参数
        AMapLocationClientOption locationOption = getLocationOption(once);
        mLocationClient.setLocationOption(locationOption);
        //设置定位回调监听
        AMapLocationListener locationListener = getLocationListener(callBack);
        mLocationClient.setLocationListener(locationListener);
        //启动定位
        mLocationClient.startLocation();
        return mLocationClient;
    }

    /* 关闭定位 */
    public static void stopLocation(AMapLocationClient client) {
        if (client != null) {
            client.stopLocation();
            client.onDestroy();
            client = null;
        }
    }

    private static AMapLocationClientOption getLocationOption(boolean once) {
        AMapLocationClientOption locationOption = new AMapLocationClientOption();
        locationOption.setWifiScan(true); // 获取最新的wifi列表，从而获取更精准的定位结果
        locationOption.setHttpTimeOut(30000); // 单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒
        locationOption.setLocationCacheEnable(true); // 开启缓存机制，无变化时会返回缓存地址

        AMapLocationClientOption.AMapLocationMode locationMode;
        if (once) {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy; // 高精度
            locationOption.setMockEnable(true); // 可以通过外界第三方软件对GPS位置进行模拟，低耗电无效
        } else {
            locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving; // 低耗电
            locationOption.setInterval(2000); // 设置定位间隔,单位毫秒,默认为2000ms，最低1000ms
        }
        locationOption.setLocationMode(locationMode);
        locationOption.setOnceLocation(once); // 获取一次定位结果
        locationOption.setOnceLocationLatest(once); // 获取最近3s内精度最高的一次定位结果
        locationOption.setNeedAddress(true); // 设置是否返回地址信息（默认返回地址信息）
        return locationOption;
    }

    private static AMapLocationListener getLocationListener(final LocationCallBack callBack) {
        return aMapLocation -> {
            if (aMapLocation == null) {
                if (callBack != null) {
                    callBack.onFailed("");
                }
                return;
            }
            if (aMapLocation.getErrorCode() != 0) {
                // 定位失败，打印相关信息
                LogUtils.w(LocationHelper.class, "onLocationChanged", "ErrCode: " + aMapLocation.getErrorCode() + ", errInfo:" + aMapLocation.getErrorInfo());
                if (callBack != null) {
                    callBack.onFailed(aMapLocation.getErrorInfo());
                }
                return;
            }
            // 定位成功回调信息，设置相关消息
            LogUtils.d(LocationHelper.class, "onLocationChanged", aMapLocation.getLongitude() + " - " + aMapLocation.getLatitude() + " - " + aMapLocation.getAddress());
            LocationInfo info = LocationInfo.getInfo(); // 用来保存设备位置的对象
            info.setLongitude(aMapLocation.getLongitude()); // 经度
            info.setLatitude(aMapLocation.getLatitude()); // 纬度
            info.setCountry(aMapLocation.getCountry()); // 国家
            info.setProvince(aMapLocation.getProvince()); // 省份
            info.setCity(aMapLocation.getCity()); // 城市
            info.setDistrict(aMapLocation.getDistrict()); // 城区
            info.setStreet(aMapLocation.getStreet()); // 街道
            info.setCityId(aMapLocation.getAdCode()); // 城市编号
            // 只有wifi会返回此字段
            String address = aMapLocation.getAddress();
            if (StringUtils.isEmpty(address)) {
                address = info.getProvince() + info.getCity() + info.getDistrict() + info.getStreet();
            }
            info.setAddress(address);
            if (callBack != null) {
                callBack.onSuccess(info);
            }
        };
    }

    // 两点距离
    public static float distance(double lon1, double lat1, double lon2, double lat2) {
        // 用高德的距离测量
        LatLng latLng1 = new LatLng(lat1, lon1);
        LatLng latLng2 = new LatLng(lat2, lon2);
        return AMapUtils.calculateLineDistance(latLng1, latLng2);
        //DPoint point1 = new DPoint();
        //point1.setLatitude(info1.getLatitude());
        //point1.setLongitude(info1.getLongitude());
        //DPoint point2 = new DPoint();
        //point2.setLatitude(info2.getLatitude());
        //point2.setLongitude(info2.getLongitude());
        //return CoordinateConverter.calculateLineDistance(point1, point2);
    }

    /* 定位 并 回调信息 */
    public interface WeatherCallBack {
        void onSuccess(WeatherToday weather);

        void onFailed(String errMsg);
    }

    // 天气查询(实况)
    public static void getWeatherToday(Context context, Place place, WeatherCallBack callBack) {
        if (place == null) return;
        WeatherSearch search = new WeatherSearch(context);
        search.setQuery(new WeatherSearchQuery(place.getCity(), WeatherSearchQuery.WEATHER_TYPE_LIVE));
        search.setOnWeatherSearchListener(new WeatherSearch.OnWeatherSearchListener() {
            @Override
            public void onWeatherLiveSearched(LocalWeatherLiveResult localWeatherLiveResult, int i) {
                if (i != 1000) {
                    LogUtils.w(LocationHelper.class, "getWeatherToday", "ErrCode: " + i);
                    if (callBack != null) {
                        callBack.onFailed("");
                    }
                    return;
                }
                if (localWeatherLiveResult == null || localWeatherLiveResult.getLiveResult() == null) {
                    if (callBack != null) {
                        callBack.onFailed("");
                    }
                    return;
                }
                LocalWeatherLive result = localWeatherLiveResult.getLiveResult();
                WeatherToday weather = new WeatherToday();
                weather.setCondition(result.getWeather());
                weather.setIcon(WeatherHelper.getIcondByAmap(result.getWeather()));
                weather.setTemp(result.getTemperature());
                weather.setWindDir(result.getWindDirection());
                weather.setWindLevel(result.getWindPower());
                weather.setHumidity(result.getHumidity());
                // weather.setUpdateAt();
                if (callBack != null) {
                    callBack.onSuccess(weather);
                }
            }

            @Override
            public void onWeatherForecastSearched(LocalWeatherForecastResult localWeatherForecastResult, int i) {
            }
        });
        search.searchWeatherAsyn();
    }

}
