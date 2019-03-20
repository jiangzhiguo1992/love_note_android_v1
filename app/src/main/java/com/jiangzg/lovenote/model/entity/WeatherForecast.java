package com.jiangzg.lovenote.model.entity;

/**
 * Created by JZG on 2018/6/17.
 * WeatherForecast
 */
public class WeatherForecast {

    private long timeAt;
    private String timeShow;
    private String conditionDay;
    private String conditionNight;
    private String iconDay;
    private String iconNight;
    private String tempDay;
    private String tempNight;
    private String windDay;
    private String windNight;
    private long updateAt;

    public String getTimeShow() {
        return timeShow;
    }

    public void setTimeShow(String timeShow) {
        this.timeShow = timeShow;
    }

    public long getTimeAt() {
        return timeAt;
    }

    public void setTimeAt(long timeAt) {
        this.timeAt = timeAt;
    }

    public String getConditionDay() {
        return conditionDay;
    }

    public void setConditionDay(String conditionDay) {
        this.conditionDay = conditionDay;
    }

    public String getConditionNight() {
        return conditionNight;
    }

    public void setConditionNight(String conditionNight) {
        this.conditionNight = conditionNight;
    }

    public String getIconDay() {
        return iconDay;
    }

    public void setIconDay(String iconDay) {
        this.iconDay = iconDay;
    }

    public String getIconNight() {
        return iconNight;
    }

    public void setIconNight(String iconNight) {
        this.iconNight = iconNight;
    }

    public String getTempDay() {
        return tempDay;
    }

    public void setTempDay(String tempDay) {
        this.tempDay = tempDay;
    }

    public String getTempNight() {
        return tempNight;
    }

    public void setTempNight(String tempNight) {
        this.tempNight = tempNight;
    }

    public String getWindDay() {
        return windDay;
    }

    public void setWindDay(String windDay) {
        this.windDay = windDay;
    }

    public String getWindNight() {
        return windNight;
    }

    public void setWindNight(String windNight) {
        this.windNight = windNight;
    }

    public long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(long updateAt) {
        this.updateAt = updateAt;
    }
}
