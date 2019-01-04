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

    private static DeviceInfo instance;

    private String deviceId; // GSM网络，返回IMEI；CDMA网络，返回MEID
    private String macAddress; // MAC地址
    private String model; // 设备型号
    private String manufacturer; // 设备厂商
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
        if (StringUtils.isEmpty(deviceId)) {
            if (isPhone()) {
                deviceId = AppBase.getTelephonyManager().getDeviceId();
            } else {
                ContentResolver contentResolver = AppBase.getInstance().getContentResolver();
                deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
            }
        }
        LogUtils.d(DeviceInfo.class, "getDeviceId", deviceId);
        return deviceId;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getPhoneNumber() {
        if (StringUtils.isEmpty(phoneNumber)) {
            phoneNumber = AppBase.getTelephonyManager().getLine1Number();
        }
        LogUtils.d(DeviceInfo.class, "getPhoneNumber", phoneNumber);
        return phoneNumber;
    }

    @SuppressLint({"HardwareIds", "MissingPermission"})
    public String getSimSerial() {
        if (StringUtils.isEmpty(simSerial)) {
            simSerial = AppBase.getTelephonyManager().getSimSerialNumber();
        }
        LogUtils.d(DeviceInfo.class, "getSimSerial", simSerial);
        return simSerial;
    }

    /**
     * 物理地址
     */
    @SuppressLint("HardwareIds")
    public String getMacAddress() {
        if (StringUtils.isEmpty(macAddress)) {
            WifiManager wifiManager = AppBase.getWifiManager();
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info != null) {
                macAddress = info.getMacAddress();
            }
        }
        LogUtils.d(DeviceInfo.class, "getMacAddress", macAddress);
        return macAddress;
    }

    public boolean isPhone() {
        isPhone = AppBase.getTelephonyManager().getPhoneType() != TelephonyManager.PHONE_TYPE_NONE;
        LogUtils.d(DeviceInfo.class, "isPhone", String.valueOf(isPhone));
        return isPhone;
    }

    public boolean isTable() {
        int screenLayout = AppBase.getInstance().getResources().getConfiguration().screenLayout;
        boolean xlarge = ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
        boolean large = ((screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
        isTable = (xlarge || large);
        LogUtils.d(DeviceInfo.class, "isTable", String.valueOf(isTable));
        return isTable;
    }

    public String getManufacturer() {
        if (StringUtils.isEmpty(manufacturer)) {
            manufacturer = Build.MANUFACTURER;
        }
        LogUtils.d(DeviceInfo.class, "getManufacturer", manufacturer);
        return manufacturer;
    }

    public String getModel() {
        if (StringUtils.isEmpty(model)) {
            model = Build.MODEL;
        }
        LogUtils.d(DeviceInfo.class, "getModel", model);
        return model;
    }

    public String getOsVersion() {
        if (StringUtils.isEmpty(osVersion)) {
            osVersion = Build.VERSION.RELEASE;
        }
        LogUtils.d(DeviceInfo.class, "getOsVersion", osVersion);
        return osVersion;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "deviceId='" + deviceId + '\'' +
                ", macAddress='" + macAddress + '\'' +
                ", model='" + model + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", isPhone=" + isPhone +
                ", isTable=" + isTable +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", simSerial='" + simSerial + '\'' +
                '}';
    }
}
