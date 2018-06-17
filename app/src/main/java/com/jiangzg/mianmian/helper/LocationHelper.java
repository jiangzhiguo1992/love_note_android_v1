package com.jiangzg.mianmian.helper;

import android.app.Activity;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.mianmian.base.MyApp;

/**
 * Created by JZG on 2018/4/18.
 * 高德地图
 */
public class LocationHelper {

    private static final String LOG_TAG = "LocationHelper";
    private static final int CODE_SUCCESS = 0; // 以后可能会变

    // 检查位置服务是否可用
    //public static boolean isLocationEnable() {
    //    boolean permission = PermUtils.isPermissionOK(MyApp.get(), PermUtils.location);
    //    boolean enabled = LocationInfo.isLocationEnabled();
    //    return permission && enabled;
    //}

    // 检查并请求位置服务 TODO 用到地图的都要检查
    public static boolean checkLocationEnable(final Activity activity) {
        if (!PermUtils.isPermissionOK(activity, PermUtils.location)) {
            // 权限不过关
            PermUtils.requestPermissions(activity, ConsHelper.REQUEST_LOCATION, PermUtils.location, new PermUtils.OnPermissionListener() {
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
        //            .title(R.string.location_func_limit)
        //            .content(R.string.find_location_func_cant_use_normal_look_gps_is_open)
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
    public static AMapLocationClient startLocation(boolean once, LocationCallBack callBack) {
        if (!PermUtils.isPermissionOK(MyApp.get(), PermUtils.location)) return null;
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
        locationOption.setHttpTimeOut(20000); // 单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒
        locationOption.setLocationCacheEnable(true); // 开启缓存机制，无变化时会返回缓存地址

        AMapLocationClientOption.AMapLocationMode locationMode;
        if (once) {
            locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy; // 高精度
            locationOption.setMockEnable(true); // 可以通过外界第三方软件对GPS位置进行模拟，低耗电无效
        } else {
            locationMode = AMapLocationClientOption.AMapLocationMode.Battery_Saving; // 低耗电
            locationOption.setInterval(1000); // 设置定位间隔,单位毫秒,默认为2000ms，最低1000ms
        }
        locationOption.setLocationMode(locationMode);
        locationOption.setOnceLocation(once); // 获取一次定位结果
        locationOption.setOnceLocationLatest(once); // 获取最近3s内精度最高的一次定位结果
        locationOption.setNeedAddress(true); // 设置是否返回地址信息（默认返回地址信息）
        return locationOption;
    }

    private static AMapLocationListener getLocationListener(final LocationCallBack callBack) {
        return new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation == null) return;
                if (aMapLocation.getErrorCode() == CODE_SUCCESS) {
                    // 定位成功回调信息，设置相关消息
                    LogUtils.i(LOG_TAG, "onLocationChanged: success:" + aMapLocation.getLongitude() + " - " + aMapLocation.getLatitude());
                    LocationInfo info = LocationInfo.getInfo();
                    info.setLatitude(aMapLocation.getLatitude()); // 纬度
                    info.setLongitude(aMapLocation.getLongitude()); // 经度
                    info.setCountry(aMapLocation.getCountry()); // 国家
                    info.setProvince(aMapLocation.getProvince()); // 省份
                    info.setCity(aMapLocation.getCity()); // 城市
                    info.setDistrict(aMapLocation.getDistrict()); // 城区
                    info.setStreet(aMapLocation.getStreet()); // 街道
                    //只有wifi会返回此字段
                    String address = aMapLocation.getAddress();
                    if (StringUtils.isEmpty(address)) {
                        address = info.getProvince() + info.getCity() + info.getDistrict() + info.getStreet();
                    }
                    info.setAddress(address);
                    if (callBack != null) {
                        callBack.onSuccess(info);
                    }
                } else {
                    // 定位失败，打印相关信息
                    LogUtils.w(LOG_TAG, "onLocationChanged: ErrCode:" + aMapLocation.getErrorCode()
                            + ", errInfo:" + aMapLocation.getErrorInfo());
                    if (callBack != null) {
                        callBack.onFailed(aMapLocation.getErrorInfo());
                    }
                }
            }
        };
    }

    // 两点距离
    public static float distance(LocationInfo info1, LocationInfo info2) {
        LatLng latLng1 = new LatLng(info1.getLatitude(), info2.getLongitude());
        LatLng latLng2 = new LatLng(info2.getLatitude(), info2.getLongitude());
        return AMapUtils.calculateLineDistance(latLng1, latLng2);
        //DPoint point1 = new DPoint();
        //point1.setLatitude(info1.getLatitude());
        //point1.setLongitude(info1.getLongitude());
        //DPoint point2 = new DPoint();
        //point2.setLatitude(info2.getLatitude());
        //point2.setLongitude(info2.getLongitude());
        //return CoordinateConverter.calculateLineDistance(point1, point2);
    }

}
