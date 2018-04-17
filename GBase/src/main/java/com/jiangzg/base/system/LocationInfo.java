package com.jiangzg.base.system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by gg on 2017/3/24.
 * 定位相关工具类
 */
public class LocationInfo {

    private static final String LOG_TAG = "LocationInfo";

    private static LocationInfo instance; // info

    private double longitude; // 经度
    private double latitude; // 纬度
    private String country; // 国
    private String province; // 省
    private String city; // 城/市
    private String district; // 区
    private String feature; // 详
    private String address;

    private OnLocationChangeListener mListener; // 外部实现的listener
    private MyLocationListener myLocationListener;  // 内部处理的listener

    /**
     * @return 定位到的地理位置信息(单例模式, 不要持久化)
     */
    public static LocationInfo getInfo() {
        if (instance == null) {
            synchronized (LocationInfo.class) {
                if (instance == null) {
                    instance = new LocationInfo();
                }
            }
        }
        return instance;
    }

    /**
     * 注册(permission)
     *
     * @param minTime     位置信息更新周期（单位：毫秒）
     * @param minDistance 位置变化最小距离：当位置距离变化超过此值时，将更新位置信息（单位：米）
     * @param listener    位置刷新的回调接口
     */
    public boolean addListener(final long minTime, final long minDistance, @Nullable final OnLocationChangeListener listener) {
        if (!isLocationEnabled()) {
            LogUtils.w(LOG_TAG, "addListener--->Fail");
            return false;
        }
        starLocation(minTime, minDistance, listener);
        return true;
    }

    /**
     * 注销
     */
    public void removeListener() {
        if (myLocationListener != null) {
            AppBase.getLocationManager().removeUpdates(myLocationListener);
            myLocationListener = null;
            mListener = null;
        }
    }

    @SuppressLint("MissingPermission")
    private void starLocation(long minTime, long minDistance, @Nullable OnLocationChangeListener listener) {
        mListener = listener;
        LocationManager mLocationManager = AppBase.getLocationManager();
        String provider = mLocationManager.getBestProvider(getCriteria(), true);
        // 获取上次定位的location
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location == null) { // gps有时候不行
            if (!provider.equals(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;
                location = mLocationManager.getLastKnownLocation(provider);
            } else {
                provider = LocationManager.NETWORK_PROVIDER;
                location = mLocationManager.getLastKnownLocation(provider);
            }
            if (location == null) { // 原来没有定位过，用最好的
                provider = mLocationManager.getBestProvider(getCriteria(), true);
            }
        }
        if (mListener != null && location != null) {
            mListener.onLocationFirst(location);
        }
        if (myLocationListener == null) {
            myLocationListener = new MyLocationListener();
        }
        mLocationManager.requestLocationUpdates(provider, minTime, minDistance, myLocationListener);
    }

    /**
     * 补充：计算两点之间真实距离(米)
     */
    public static double getDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        // 维度
        double lat1 = (Math.PI / 180) * latitude1;
        double lat2 = (Math.PI / 180) * latitude2;
        // 经度
        double lon1 = (Math.PI / 180) * longitude1;
        double lon2 = (Math.PI / 180) * longitude2;
        // 地球半径
        double R = 6371;
        // 两点间距离 km，如果想要米的话，结果*1000就可以了
        double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)) * R;
        return d * 1000;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getFeature() {
        return feature;
    }

    public String getCountry() {
        return country;
    }

    public String getAddress() {
        return address;
    }

    public String getDistrict() {
        return district;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    @Override
    public String toString() {
        return "LocationInfo{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", feature='" + feature + '\'' +
                ", address='" + address + '\'' +
                ", mListener=" + mListener +
                ", myLocationListener=" + myLocationListener +
                '}';
    }

    /* 设置本地缓存地址信息，注意这里的context不能是static，还有开线程 */
    public LocationInfo convertLoc2Info(Context context, Location location) {
        if (location == null) {
            LogUtils.w(LOG_TAG, "convertLoc2Info: location == null");
            return instance;
        }
        //经纬度
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        instance.latitude = latitude;
        instance.longitude = longitude;
        //address
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                instance.country = address.getCountryName();
                instance.province = address.getAdminArea();
                instance.city = address.getSubAdminArea();
                instance.district = address.getLocality();
                instance.feature = address.getFeatureName();
                instance.address = address.getAddressLine(0);
            }
        } catch (IOException e) { // 手动改location也会造成IOException
            LogUtils.e(LOG_TAG, "convertLoc2Info", e);
        }
        LogUtils.i(LOG_TAG, getInfo().toString());
        return instance;
    }

    /* 判断定位是否可用 */
    public static boolean isLocationEnabled() {
        LocationManager lm = AppBase.getLocationManager();
        boolean netProvide = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean gpsProvide = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return netProvide || gpsProvide;
    }

    /* 设置定位参数 */
    private static Criteria getCriteria() {
        Criteria criteria = new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置是否要求速度
        criteria.setSpeedRequired(false);
        // 设置是否允许运营商收费
        criteria.setCostAllowed(false);
        //设置是否需要方位信息
        criteria.setBearingRequired(false);
        //设置是否需要海拔信息
        criteria.setAltitudeRequired(false);
        // 设置对电源的需求
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        return criteria;
    }

    private class MyLocationListener implements LocationListener {
        /**
         * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         *
         * @param location 坐标
         */
        @Override
        public void onLocationChanged(Location location) {
            LogUtils.i(LOG_TAG, "onLocationChanged: " + location.getLongitude() + "-" + location.getLatitude());
            if (mListener != null) {
                mListener.onLocationChange(location);
            }
        }

        /**
         * provider的在可用、暂时不可用和无服务三个状态直接切换时触发此函数
         *
         * @param provider 提供者
         * @param status   状态
         * @param extras   provider可选包
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    LogUtils.i(LOG_TAG, "onStatusChanged: 当前为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    LogUtils.i(LOG_TAG, "onStatusChanged: 当前为服务区外状态");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    LogUtils.i(LOG_TAG, "onStatusChanged: 当前为暂停服务状态");
                    break;
            }
            if (mListener != null) {
                mListener.onStatusChange(provider, status, extras);
            }
        }

        /**
         * provider被enable时触发此函数，比如GPS被打开
         */
        @Override
        public void onProviderEnabled(String provider) {
            LogUtils.i(LOG_TAG, "onProviderEnabled: " + provider);
        }

        /**
         * provider被disable时触发此函数，比如GPS被关闭
         */
        @Override
        public void onProviderDisabled(String provider) {
            LogUtils.i(LOG_TAG, "onProviderDisabled: " + provider);
        }
    }

    /**
     * 回调的监听器
     */
    public interface OnLocationChangeListener {

        void onLocationFirst(Location location);

        /**
         * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         *
         * @param location 坐标
         */
        void onLocationChange(Location location);

        /**
         * provider的在可用、暂时不可用和无服务三个状态直接切换时触发此函数
         *
         * @param provider 提供者
         * @param status   状态
         * @param extras   provider可选包
         */
        void onStatusChange(String provider, int status, Bundle extras);
    }

}
