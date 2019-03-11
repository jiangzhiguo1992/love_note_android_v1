package com.jiangzg.base.system;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.res.Configuration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.io.File;

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
    private String inCacheDir; // 内部缓存目录 /data/user/0/packageName/cache，拍照裁剪没权限
    private String inFilesDir; // 内部文件目录 /data/user/0/packageName/files，拍照裁剪没权限
    private String outCacheDir; // sd卡缓存目录 /storage/emulated/0/Android/data/packageName/cache
    private String outFilesDir; // sd卡文件目录 /storage/emulated/0/Android/data/packageName/files
    private String sdCardDir; // sd卡路径 /storage/emulated/0/ 或 /

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

    public String getInCacheDir() {
        if (StringUtils.isEmpty(inCacheDir)) {
            File cacheDir = AppBase.getInstance().getCacheDir();
            inCacheDir = cacheDir.getAbsolutePath();
        }
        LogUtils.d(AppInfo.class, "getInCacheDir", inCacheDir);
        return inCacheDir;
    }

    public String getInFilesDir() {
        if (StringUtils.isEmpty(inFilesDir)) {
            File filesDir = AppBase.getInstance().getFilesDir();
            inFilesDir = filesDir.getAbsolutePath();
        }
        LogUtils.d(AppInfo.class, "getInFilesDir", inFilesDir);
        return inFilesDir;
    }

    public String getOutCacheDir() {
        if (StringUtils.isEmpty(outCacheDir)) {
            File externalCacheDir = AppBase.getInstance().getExternalCacheDir();
            if (isSDCardExits() && externalCacheDir != null && !StringUtils.isEmpty(externalCacheDir.getAbsolutePath())) {
                outCacheDir = externalCacheDir.getAbsolutePath();
            } else {
                outCacheDir = getInCacheDir();
            }
        }
        LogUtils.d(AppInfo.class, "getOutCacheDir", outCacheDir);
        return outCacheDir;
    }

    public String getOutFilesDir() {
        if (StringUtils.isEmpty(outFilesDir)) {
            File externalFilesDir = AppBase.getInstance().getExternalFilesDir("");
            if (isSDCardExits() && externalFilesDir != null && !StringUtils.isEmpty(externalFilesDir.getAbsolutePath())) {
                outFilesDir = externalFilesDir.getAbsolutePath();
            } else {
                outFilesDir = getInFilesDir();
            }
        }
        LogUtils.d(AppInfo.class, "getOutFilesDir", outFilesDir);
        return outFilesDir;
    }

    /**
     * 获取可用的SD卡路径，需要动态权限
     */
    @SuppressLint("MissingPermission")
    public String getSdCardDir() {
        if (StringUtils.isEmpty(sdCardDir)) {
            if (isSDCardExits()) { // 有sd卡 == /storage/emulated/0/
                this.sdCardDir = Environment.getExternalStorageDirectory().getPath() + File.separator;
            } else { // 没sd卡 == /
                this.sdCardDir = Environment.getRootDirectory() + File.separator;
            }
        }
        LogUtils.d(AppInfo.class, "getSDCardDir", sdCardDir);
        return this.sdCardDir;
    }

    public static boolean isSDCardExits() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
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
                ", inCacheDir='" + inCacheDir + '\'' +
                ", inFilesDir='" + inFilesDir + '\'' +
                ", outCacheDir='" + outCacheDir + '\'' +
                ", outFilesDir='" + outFilesDir + '\'' +
                ", sdCardDir='" + sdCardDir + '\'' +
                '}';
    }
}
