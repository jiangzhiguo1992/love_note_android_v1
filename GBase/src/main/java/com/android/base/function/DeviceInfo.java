package com.android.base.function;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.base.component.application.AppContext;
import com.android.base.common.StringUtils;

/**
 * Created by jiang on 2016/10/12
 * <p/>
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
            Log.d(LOG_TAG, "getDeviceId--->" + deviceId);
            return deviceId;
        }
        if (isPhone()) {
            deviceId = AppContext.getTelephonyManager().getDeviceId();
        } else {
            ContentResolver contentResolver = AppContext.get().getContentResolver();
            deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        }
        Log.d(LOG_TAG, "getDeviceId--->" + deviceId);
        return deviceId;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getPhoneNumber() {
        if (!StringUtils.isEmpty(phoneNumber)) {
            Log.d(LOG_TAG, "getPhoneNumber--->" + phoneNumber);
            return phoneNumber;
        }
        phoneNumber = AppContext.getTelephonyManager().getLine1Number();
        Log.d(LOG_TAG, "getPhoneNumber--->" + phoneNumber);
        return phoneNumber;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getSimSerial() {
        if (!StringUtils.isEmpty(simSerial)) {
            Log.d(LOG_TAG, "getSimSerial--->" + simSerial);
            return simSerial;
        }
        simSerial = AppContext.getTelephonyManager().getSimSerialNumber();
        Log.d(LOG_TAG, "getSimSerial--->" + simSerial);
        return simSerial;
    }

    /**
     * 物理地址
     */
    @SuppressLint("HardwareIds")
    public String getMacAddress() {
        if (!StringUtils.isEmpty(macAddress)) {
            Log.d(LOG_TAG, "getMacAddress--->" + macAddress);
            return macAddress;
        }
        WifiManager wifiManager = AppContext.getWifiManager();
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info != null) {
            macAddress = info.getMacAddress();
        }
        Log.d(LOG_TAG, "getMacAddress--->" + macAddress);
        return macAddress;
    }

    public boolean isPhone() {
        isPhone = AppContext.getTelephonyManager().getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
        Log.d(LOG_TAG, "isPhone--->" + isPhone);
        return isPhone;
    }

    public boolean isTable() {
        int screenLayout = AppContext.get().getResources().getConfiguration().screenLayout;
        boolean xlarge = ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        isTable = (xlarge || large);
        Log.d(LOG_TAG, "isTable--->" + isTable);
        return isTable;
    }

    public String getManufacturer() {
        if (!StringUtils.isEmpty(manufacturer)) {
            Log.d(LOG_TAG, "getManufacturer--->" + manufacturer);
            return manufacturer;
        }
        manufacturer = Build.MANUFACTURER;
        Log.d(LOG_TAG, "getManufacturer--->" + manufacturer);
        return manufacturer;
    }

    public String getModel() {
        if (!StringUtils.isEmpty(model)) {
            Log.d(LOG_TAG, "getModel--->" + model);
            return model;
        }
        model = Build.MODEL;
        Log.d(LOG_TAG, "getModel--->" + model);
        return model;
    }

    public String getPlatform() {
        if (!StringUtils.isEmpty(platform)) {
            Log.d(LOG_TAG, "getPlatform--->" + platform);
            return platform;
        }
        platform = "Android";
        Log.d(LOG_TAG, "getPlatform--->" + platform);
        return platform;
    }

    public String getOsVersion() {
        if (!StringUtils.isEmpty(osVersion)) {
            Log.d(LOG_TAG, "getOsVersion--->" + osVersion);
            return osVersion;
        }
        osVersion = Build.VERSION.RELEASE;
        Log.d(LOG_TAG, "getOsVersion--->" + osVersion);
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
