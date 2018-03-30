package com.jiangzg.ita.domain;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.provider.Settings;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.system.DeviceInfo;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.helper.SPHelper;

/**
 * Created by JZG on 2017/12/26.
 * Entry
 */

public class Entry extends BaseObj {

    private long userId;
    private String userToken;
    private String deviceId;
    private String deviceName;
    private String platform;
    private String osVersion;
    private int appVersion;

    @SuppressLint("HardwareIds")
    public static Entry getEntry() {
        Entry entry = new Entry();
        User user = SPHelper.getUser();
        entry.setUserId(user.getId());
        entry.setUserToken(user.getUserToken());
        ContentResolver contentResolver = MyApp.get().getContentResolver();
        String deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        if (StringUtils.isEmpty(deviceId)) {
            deviceId = DeviceInfo.get().getMacAddress();
        }
        entry.setDeviceId(deviceId);
        String manufacturer = DeviceInfo.get().getManufacturer();
        String model = DeviceInfo.get().getModel();
        entry.setDeviceName(manufacturer + " : " + model);
        entry.setPlatform(DeviceInfo.get().getPlatform());
        entry.setOsVersion(DeviceInfo.get().getOsVersion());
        entry.setAppVersion(AppInfo.get().getVersionCode());
        return entry;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

}
