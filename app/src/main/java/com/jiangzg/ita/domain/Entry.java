package com.jiangzg.ita.domain;

import com.jiangzg.base.component.application.AppInfo;
import com.jiangzg.base.function.DeviceInfo;
import com.jiangzg.ita.utils.UserPreference;

/**
 * Created by JZG on 2017/12/26.
 * Entry
 */

public class Entry extends BaseObj {

    private long userId;
    private String userToken;
    private String deviceName;
    private String platform;
    private String osVersion;
    private int appVersion;

    public static Entry getEntry() {
        Entry entry = new Entry();
        User user = UserPreference.getUser();
        entry.setUserId(user.getId());
        entry.setUserToken(user.getUserToken());
        String manufacturer = DeviceInfo.get().getManufacturer();
        String model = DeviceInfo.get().getModel();
        entry.setDeviceName(manufacturer + " : " + model);
        entry.setPlatform(DeviceInfo.get().getPlatform());
        entry.setOsVersion(DeviceInfo.get().getOsVersion());
        entry.setAppVersion(AppInfo.get().getVersionCode());
        return entry;
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
