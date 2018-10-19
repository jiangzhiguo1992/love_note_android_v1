package com.jiangzg.lovenote.domain;

/**
 * Created by JZG on 2018/10/19.
 * NotificationInfo
 */
public class NotificationInfo {

    private String channelId;
    private String channelName;
    private int channelLevel; // 最高=NotificationManager.IMPORTANCE_HIGH=4
    private String channelDesc;
    private boolean light;
    private boolean vibrate;

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

    public boolean isLight() {
        return light;
    }

    public void setLight(boolean light) {
        this.light = light;
    }

    public boolean isVibrate() {
        return vibrate;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }
}
