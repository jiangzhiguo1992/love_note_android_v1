package com.jiangzg.lovenote.model.entity;

/**
 * Created by JZG on 2018/10/19.
 * PushInfo
 */
public class PushInfo {

    private String aliAppKey;
    private String aliAppSecret;
    private String miAppId;
    private String miAppKey;
    private String channelId;
    private boolean noticeLight;
    private boolean noticeSound;
    private boolean noticeVibrate;
    private int noStartHour;
    private int noEndHour;

    public int getNoStartHour() {
        return noStartHour;
    }

    public void setNoStartHour(int noStartHour) {
        this.noStartHour = noStartHour;
    }

    public int getNoEndHour() {
        return noEndHour;
    }

    public void setNoEndHour(int noEndHour) {
        this.noEndHour = noEndHour;
    }

    public String getAliAppKey() {
        return aliAppKey;
    }

    public void setAliAppKey(String aliAppKey) {
        this.aliAppKey = aliAppKey;
    }

    public String getAliAppSecret() {
        return aliAppSecret;
    }

    public void setAliAppSecret(String aliAppSecret) {
        this.aliAppSecret = aliAppSecret;
    }

    public boolean isNoticeSound() {
        return noticeSound;
    }

    public void setNoticeSound(boolean noticeSound) {
        this.noticeSound = noticeSound;
    }

    public String getMiAppId() {
        return miAppId;
    }

    public void setMiAppId(String miAppId) {
        this.miAppId = miAppId;
    }

    public String getMiAppKey() {
        return miAppKey;
    }

    public void setMiAppKey(String miAppKey) {
        this.miAppKey = miAppKey;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public boolean isNoticeLight() {
        return noticeLight;
    }

    public void setNoticeLight(boolean noticeLight) {
        this.noticeLight = noticeLight;
    }

    public boolean isNoticeVibrate() {
        return noticeVibrate;
    }

    public void setNoticeVibrate(boolean noticeVibrate) {
        this.noticeVibrate = noticeVibrate;
    }
}
