package com.jiangzg.mianmian.domain;

import java.util.List;

/**
 * Created by JZG on 2018/4/17.
 * Weather
 */
public class Weather {

    private City city;
    private Condition condition;
    private List<Forecast> forecastList;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public List<Forecast> getForecastList() {
        return forecastList;
    }

    public void setForecastList(List<Forecast> forecastList) {
        this.forecastList = forecastList;
    }

    public static class City {

        private int cityId;
        private String counname;
        private String name;
        private String pname;
        private String timezone;

        public int getCityId() {
            return cityId;
        }

        public void setCityId(int cityId) {
            this.cityId = cityId;
        }

        public String getCounname() {
            return counname;
        }

        public void setCounname(String counname) {
            this.counname = counname;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPname() {
            return pname;
        }

        public void setPname(String pname) {
            this.pname = pname;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
    }

    public static class Condition {

        private String condition;
        private String humidity;
        private String icon;
        private String temp;
        private String updatetime;
        private String windDir;
        private String windLevel;

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getHumidity() {
            return humidity;
        }

        public void setHumidity(String humidity) {
            this.humidity = humidity;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getTemp() {
            return temp;
        }

        public void setTemp(String temp) {
            this.temp = temp;
        }

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        public String getWindDir() {
            return windDir;
        }

        public void setWindDir(String windDir) {
            this.windDir = windDir;
        }

        public String getWindLevel() {
            return windLevel;
        }

        public void setWindLevel(String windLevel) {
            this.windLevel = windLevel;
        }
    }

    public static class Forecast {

        private String conditionDay;
        private String conditionIdDay;
        private String conditionIdNight;
        private String conditionNight;
        private String predictDate;
        private String tempDay;
        private String tempNight;
        private String updatetime;
        private String windDirDay;
        private String windDirNight;
        private String windLevelDay;
        private String windLevelNight;

        public String getConditionDay() {
            return conditionDay;
        }

        public void setConditionDay(String conditionDay) {
            this.conditionDay = conditionDay;
        }

        public String getConditionIdDay() {
            return conditionIdDay;
        }

        public void setConditionIdDay(String conditionIdDay) {
            this.conditionIdDay = conditionIdDay;
        }

        public String getConditionIdNight() {
            return conditionIdNight;
        }

        public void setConditionIdNight(String conditionIdNight) {
            this.conditionIdNight = conditionIdNight;
        }

        public String getConditionNight() {
            return conditionNight;
        }

        public void setConditionNight(String conditionNight) {
            this.conditionNight = conditionNight;
        }

        public String getPredictDate() {
            return predictDate;
        }

        public void setPredictDate(String predictDate) {
            this.predictDate = predictDate;
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

        public String getUpdatetime() {
            return updatetime;
        }

        public void setUpdatetime(String updatetime) {
            this.updatetime = updatetime;
        }

        public String getWindDirDay() {
            return windDirDay;
        }

        public void setWindDirDay(String windDirDay) {
            this.windDirDay = windDirDay;
        }

        public String getWindDirNight() {
            return windDirNight;
        }

        public void setWindDirNight(String windDirNight) {
            this.windDirNight = windDirNight;
        }

        public String getWindLevelDay() {
            return windLevelDay;
        }

        public void setWindLevelDay(String windLevelDay) {
            this.windLevelDay = windLevelDay;
        }

        public String getWindLevelNight() {
            return windLevelNight;
        }

        public void setWindLevelNight(String windLevelNight) {
            this.windLevelNight = windLevelNight;
        }
    }

}
