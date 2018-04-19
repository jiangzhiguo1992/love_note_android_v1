package com.jiangzg.ita.domain;

/**
 * Created by JZG on 2017/12/26.
 * Entry
 */

public class Entry extends BaseObj {

    private String deviceId;
    private String deviceName;
    private String platform;
    private String osVersion;
    private int appVersion;
    private EntryPlace entryPlace;

    public EntryPlace getEntryPlace() {
        return entryPlace;
    }

    public void setEntryPlace(EntryPlace entryPlace) {
        this.entryPlace = entryPlace;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
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

    public static class EntryPlace extends BaseObj {

        private long entryId;
        private double longitude;
        private double latitude;
        private String address;
        private int cityId;

        public long getEntryId() {
            return entryId;
        }

        public void setEntryId(long entryId) {
            this.entryId = entryId;
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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getCityId() {
            return cityId;
        }

        public void setCityId(int cityId) {
            this.cityId = cityId;
        }
    }

}
