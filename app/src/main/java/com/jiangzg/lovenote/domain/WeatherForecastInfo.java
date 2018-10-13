package com.jiangzg.lovenote.domain;

import java.util.List;

/**
 * Created by JZG on 2018/10/13.
 * WeatherForecastInfo
 */
public class WeatherForecastInfo {

    private String show;
    private List<WeatherForecast> weatherForecastList;

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public List<WeatherForecast> getWeatherForecastList() {
        return weatherForecastList;
    }

    public void setWeatherForecastList(List<WeatherForecast> weatherForecastList) {
        this.weatherForecastList = weatherForecastList;
    }
}
