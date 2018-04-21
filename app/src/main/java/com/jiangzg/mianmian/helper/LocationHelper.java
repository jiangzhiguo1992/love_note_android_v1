package com.jiangzg.mianmian.helper;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.mianmian.base.MyApp;

/**
 * Created by JZG on 2018/4/18.
 * 高德地图
 */
public class LocationHelper {

    private static final String LOG_TAG = "LocationHelper";
    private static final int CODE_SUCCESS = 0; // 以后可能会变

    /* 定位 并 回调信息 */
    public interface LocationCallBack {
        void onSuccess(LocationInfo info);

        void onFailed(String errMsg);
    }

    /* 开启定位 */
    public static AMapLocationClient startLocation(boolean once, LocationCallBack callBack) {
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
                    LogUtils.i(LOG_TAG, "onLocationChanged: success:" + aMapLocation.getLatitude() + "-" + aMapLocation.getLongitude());
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
        DPoint point1 = new DPoint();
        point1.setLatitude(info1.getLatitude());
        point1.setLongitude(info1.getLongitude());
        DPoint point2 = new DPoint();
        point2.setLatitude(info2.getLatitude());
        point2.setLongitude(info2.getLongitude());
        return CoordinateConverter.calculateLineDistance(point1, point2);
    }

}
