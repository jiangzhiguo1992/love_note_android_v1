package com.android.base.function;

import android.annotation.SuppressLint;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

import com.android.base.R;
import com.android.base.component.application.AppContext;
import com.android.base.string.StringUtils;
import com.android.base.view.widget.ToastUtils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by gg on 2017/3/24.
 * 定位相关工具类
 */
public class LocationUtils {

    private static LocationUtils instance;
    private static OnLocationChangeListener mListener; // 外部实现的listener
    private static MyLocationListener myLocationListener;  // 内部处理的listener
    // info
    private double longitude; // 经度
    private double latitude; // 纬度
    private String address; // 详细地址
    private String province; // 省信息
    private String city; // 城市信息
    private String district; // 区

    /**
     * 注册(permission)
     *
     * @param minTime     位置信息更新周期（单位：毫秒）
     * @param minDistance 位置变化最小距离：当位置距离变化超过此值时，将更新位置信息（单位：米）
     * @param listener    位置刷新的回调接口
     */
    public static void register(final long minTime, final long minDistance,
                                final OnLocationChangeListener listener) {
        if (!isLocationEnabled()) {
            ToastUtils.show(R.string.cannot_location_please_open_service);
            return;
        }
        starLocation(minTime, minDistance, listener);
    }

    @SuppressLint("MissingPermission")
    private static boolean starLocation(long minTime, long minDistance,
                                        OnLocationChangeListener listener) {
        mListener = listener;
        LocationManager mLocationManager = AppContext.getLocationManager();
        String provider = mLocationManager.getBestProvider(getCriteria(), true);
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            setNativeLocation(location);
            if (mListener != null) {
                mListener.getLastKnownLocation(location);
            }
        }
        if (myLocationListener == null) {
            myLocationListener = new MyLocationListener();
        }
        mLocationManager.requestLocationUpdates(provider,
                minTime, minDistance, myLocationListener);
        return true;
    }

    /**
     * 注销
     */
    public static void unregister() {
        if (myLocationListener != null) {
            AppContext.getLocationManager().removeUpdates(myLocationListener);
            myLocationListener = null;
        }
    }

    /**
     * @return 定位到的地理位置信息(单例模式, 不要持久化)
     */
    public static LocationUtils getInfo() {
        if (instance == null) {
            synchronized (LocationUtils.class) {
                if (instance == null) {
                    instance = new LocationUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 根据经纬度获取地理位置
     *
     * @param latitude  纬度
     * @param longitude 经度
     * @return {@link Address}
     */
    public static Address getAddress(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(AppContext.get(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) return addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getAddress() {
        if (StringUtils.isEmpty(address)) {
            address = "";
        }
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDistrict() {
        if (StringUtils.isEmpty(district)) {
            district = "";
        }
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        if (StringUtils.isEmpty(province)) {
            province = "";
        }
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        if (StringUtils.isEmpty(city)) {
            city = "";
        }
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /* 判断定位是否可用 */
    private static boolean isLocationEnabled() {
        LocationManager lm = AppContext.getLocationManager();
        return lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                || lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
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

    /* 设置本地缓存地址信息 */
    private static void setNativeLocation(Location location) {
        if (location == null) return;
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        getInfo().setLatitude(latitude);
        getInfo().setLongitude(longitude);
    }

    /**
     * 回调的监听器
     */
    public interface OnLocationChangeListener {

        /**
         * 获取最后一次保留的坐标
         *
         * @param location 坐标
         */
        void getLastKnownLocation(Location location);

        /**
         * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         *
         * @param location 坐标
         */
        void onLocationChanged(Location location);

        /**
         * provider的在可用、暂时不可用和无服务三个状态直接切换时触发此函数
         *
         * @param provider 提供者
         * @param status   状态
         * @param extras   provider可选包
         */
        void onStatusChanged(String provider, int status, Bundle extras);
    }

    private static class MyLocationListener implements LocationListener {
        /**
         * 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
         *
         * @param location 坐标
         */
        @Override
        public void onLocationChanged(Location location) {
            setNativeLocation(location);
            if (mListener != null) {
                mListener.onLocationChanged(location);
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
//                    LogUtils.d("onStatusChanged", "当前GPS状态为可见状态");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
//                    LogUtils.d("onStatusChanged", "当前GPS状态为服务区外状态");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                    LogUtils.d("onStatusChanged", "当前GPS状态为暂停服务状态");
                    break;
            }
            if (mListener != null) {
                mListener.onStatusChanged(provider, status, extras);
            }
        }

        /**
         * provider被enable时触发此函数，比如GPS被打开
         */
        @Override
        public void onProviderEnabled(String provider) {
        }

        /**
         * provider被disable时触发此函数，比如GPS被关闭
         */
        @Override
        public void onProviderDisabled(String provider) {
        }
    }

}
