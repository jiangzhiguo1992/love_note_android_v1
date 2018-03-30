package com.jiangzg.base.system;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

/**
 * Created by jiang on 2016/10/12
 * describe  设备相关工具类
 */
public class DeviceInfo {

    private static final String LOG_TAG = "DeviceInfo";

    private static DeviceInfo instance;

    private String deviceId; // GSM网络，返回IMEI；CDMA网络，返回MEID
    private String macAddress; // MAC地址
    private String model; // 设备型号
    private String manufacturer; // 设备厂商
    private String platform; // 平台(Android)
    private String osVersion; // Android版本号
    private boolean isPhone; // 是否是手机
    private boolean isTable; // 是否是手表
    private String phoneNumber; // 手机号
    private String simSerial; // sim卡序号

    /* 获取当前Device信息(Permission) */
    public static DeviceInfo get() {
        if (instance != null) return instance;
        instance = new DeviceInfo();
        return instance;
    }

    /**
     * 获取手机的IMIE(DeviceId)
     */
    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getDeviceId() {
        if (!StringUtils.isEmpty(deviceId)) {
            LogUtils.i(LOG_TAG, "getDeviceId: " + deviceId);
            return deviceId;
        }
        if (isPhone()) {
            deviceId = AppBase.getTelephonyManager().getDeviceId();
        } else {
            ContentResolver contentResolver = AppBase.get().getContentResolver();
            deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        }
        LogUtils.i(LOG_TAG, "getDeviceId: " + deviceId);
        return deviceId;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getPhoneNumber() {
        if (!StringUtils.isEmpty(phoneNumber)) {
            LogUtils.i(LOG_TAG, "getPhoneNumber: " + phoneNumber);
            return phoneNumber;
        }
        phoneNumber = AppBase.getTelephonyManager().getLine1Number();
        LogUtils.i(LOG_TAG, "getPhoneNumber: " + phoneNumber);
        return phoneNumber;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getSimSerial() {
        if (!StringUtils.isEmpty(simSerial)) {
            LogUtils.i(LOG_TAG, "getSimSerial: " + simSerial);
            return simSerial;
        }
        simSerial = AppBase.getTelephonyManager().getSimSerialNumber();
        LogUtils.i(LOG_TAG, "getSimSerial: " + simSerial);
        return simSerial;
    }

    /**
     * 物理地址
     */
    @SuppressLint("HardwareIds")
    public String getMacAddress() {
        if (!StringUtils.isEmpty(macAddress)) {
            LogUtils.i(LOG_TAG, "getMacAddress: " + macAddress);
            return macAddress;
        }
        WifiManager wifiManager = AppBase.getWifiManager();
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            macAddress = info.getMacAddress();
        }
        LogUtils.i(LOG_TAG, "getMacAddress: " + macAddress);
        return macAddress;
    }

    public boolean isPhone() {
        isPhone = AppBase.getTelephonyManager().getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
        LogUtils.i(LOG_TAG, "isPhone: " + isPhone);
        return isPhone;
    }

    public boolean isTable() {
        int screenLayout = AppBase.get().getResources().getConfiguration().screenLayout;
        boolean xlarge = ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        isTable = (xlarge || large);
        LogUtils.i(LOG_TAG, "isTable: " + isTable);
        return isTable;
    }

    public String getManufacturer() {
        if (!StringUtils.isEmpty(manufacturer)) {
            LogUtils.i(LOG_TAG, "getManufacturer: " + manufacturer);
            return manufacturer;
        }
        manufacturer = Build.MANUFACTURER;
        LogUtils.i(LOG_TAG, "getManufacturer: " + manufacturer);
        return manufacturer;
    }

    public String getModel() {
        if (!StringUtils.isEmpty(model)) {
            LogUtils.i(LOG_TAG, "getModel: " + model);
            return model;
        }
        model = Build.MODEL;
        LogUtils.i(LOG_TAG, "getModel: " + model);
        return model;
    }

    public String getPlatform() {
        if (!StringUtils.isEmpty(platform)) {
            LogUtils.i(LOG_TAG, "getPlatform: " + platform);
            return platform;
        }
        platform = "Android";
        LogUtils.i(LOG_TAG, "getPlatform: " + platform);
        return platform;
    }

    public String getOsVersion() {
        if (!StringUtils.isEmpty(osVersion)) {
            LogUtils.i(LOG_TAG, "getOsVersion: " + osVersion);
            return osVersion;
        }
        osVersion = Build.VERSION.RELEASE;
        LogUtils.i(LOG_TAG, "getOsVersion: " + osVersion);
        return osVersion;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "deviceId='" + deviceId + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", platform='" + platform + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", isPhone=" + isPhone +
                ", isTable=" + isTable +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", simSerial='" + simSerial + '\'' +
                '}';
    }
}
