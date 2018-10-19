package com.jiangzg.lovenote.domain;

/**
 * Created by JZG on 2018/10/19.
 * PushInfo
 */
public class PushInfo {

    private String channelId;
    private String channelName;
    private String channelDesc;
    private int channelLevel; // 最高=NotificationManager.IMPORTANCE_HIGH=4
    private boolean noticeLight;
    private boolean noticeSound;
    private boolean noticeVibrate;
    private String miAppId; // 小米id
    private String miAppKey; // 小米key
    private boolean enableSystem;
    private boolean enableSocial;
    private boolean enableSecret;

    public boolean isEnableSystem() {
        return enableSystem;
    }

    public void setEnableSystem(boolean enableSystem) {
        this.enableSystem = enableSystem;
    }

    public boolean isEnableSocial() {
        return enableSocial;
    }

    public void setEnableSocial(boolean enableSocial) {
        this.enableSocial = enableSocial;
    }

    public boolean isEnableSecret() {
        return enableSecret;
    }

    public void setEnableSecret(boolean enableSecret) {
        this.enableSecret = enableSecret;
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

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public int getChannelLevel() {
        return channelLevel;
    }

    public void setChannelLevel(int channelLevel) {
        this.channelLevel = channelLevel;
    }

    public String getChannelDesc() {
        return channelDesc;
    }

    public void setChannelDesc(String channelDesc) {
        this.channelDesc = channelDesc;
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
