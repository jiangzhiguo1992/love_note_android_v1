package com.jiangzg.base.function;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.jiangzg.base.component.application.AppContext;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by gg on 2017/3/24.
 * 定位相关工具类
 */
public class LocationInfo {

    private static final String LOG_TAG = "LocationInfo";

    // info
    private static LocationInfo instance;
    private double longitude; // 经度
    private double latitude; // 纬度
    private String country; // 国
    private String province; // 省
    private String city; // 城/市
    private String district; // 区
    private String feature; // 详
    private String address;

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

    private OnLocationChangeListener mListener; // 外部实现的listener
    private MyLocationListener myLocationListener;  // 内部处理的listener

    /**
     * 注册(permission)
     *
     * @param minTime     位置信息更新周期（单位：毫秒）
     * @param minDistance 位置变化最小距离：当位置距离变化超过此值时，将更新位置信息（单位：米）
     * @param listener    位置刷新的回调接口
     */
    public boolean addListener(final long minTime, final long minDistance, @Nullable final OnLocationChangeListener listener) {
        if (!isLocationEnabled()) {
            Log.d(LOG_TAG, "addListener--->Fail");
            return false;
        }
        Log.d(LOG_TAG, "addListener--->begin");
        starLocation(minTime, minDistance, listener);
        return true;
    }

    /**
     * 注销
     */
    public void removeListener() {
        if (myLocationListener != null) {
            Log.d(LOG_TAG, "removeListener");
            AppContext.getLocationManager().removeUpdates(myLocationListener);
            myLocationListener = null;
            mListener = null;
        }
    }

    @SuppressLint("MissingPermission")
    private void starLocation(long minTime, long minDistance, @Nullable OnLocationChangeListener listener) {
        mListener = listener;
        LocationManager mLocationManager = AppContext.getLocationManager();
        String provider = mLocationManager.getBestProvider(getCriteria(), true);
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location == null) { //gps有时候不行
            provider = LocationManager.NETWORK_PROVIDER;
            location = mLocationManager.getLastKnownLocation(provider);
        }
        setNativeInfo(location);
        if (mListener != null) {
            mListener.onLocation(location);
        }
        if (myLocationListener == null) {
            myLocationListener = new MyLocationListener();
        }
        mLocationManager.requestLocationUpdates(provider, minTime, minDistance, myLocationListener);
    }

    /* 设置本地缓存地址信息 */
    private static void setNativeInfo(Location location) {
        if (location == null) {
            Log.e(LOG_TAG, "setNativeInfo: location == null");
            return;
        }
        //经纬度
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        instance.latitude = latitude;
        instance.longitude = longitude;
        //address
        Geocoder geocoder = new Geocoder(AppContext.get(), Locale.getDefault());
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
        } catch (IOException e) { //手动改location也会造成IOException
            e.printStackTrace();
        }
        Log.d(LOG_TAG, getInfo().toString());
    }

    /* 判断定位是否可用 */
    private static boolean isLocationEnabled() {
        LocationManager lm = AppContext.getLocationManager();
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

    /**
     * 回调的监听器
     */
    public interface OnLocationChangeListener {

        /**
         * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         *
         * @param location 坐标
         */
        void onLocation(Location location);

        /**
         * provider的在可用、暂时不可用和无服务三个状态直接切换时触发此函数
         *
         * @param provider 提供者
         * @param status   状态
         * @param extras   provider可选包
         */
        void onStatus(String provider, int status, Bundle extras);
    }

    private class MyLocationListener implements LocationListener {
        /**
         * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         *
         * @param location 坐标
         */
        @Override
        public void onLocationChanged(Location location) {
            setNativeInfo(location);
            if (mListener != null) {
                mListener.onLocation(location);
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
                    Log.d(LOG_TAG, "onStatusChanged-->当前GPS状态为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d(LOG_TAG, "onStatusChanged-->当前GPS状态为服务区外状态");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d(LOG_TAG, "onStatusChanged-->当前GPS状态为暂停服务状态");
                    break;
            }
            if (mListener != null) {
                mListener.onStatus(provider, status, extras);
            }
        }

        /**
         * provider被enable时触发此函数，比如GPS被打开
         */
        @Override
        public void onProviderEnabled(String provider) {
            Log.d(LOG_TAG, "onProviderEnabled-->" + provider);
        }

        /**
         * provider被disable时触发此函数，比如GPS被关闭
         */
        @Override
        public void onProviderDisabled(String provider) {
            Log.d(LOG_TAG, "onProviderDisabled-->" + provider);
        }
    }

}
