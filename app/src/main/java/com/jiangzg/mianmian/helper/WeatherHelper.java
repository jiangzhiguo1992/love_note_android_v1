package com.jiangzg.mianmian.helper;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;

/**
 * Created by JZG on 2018/3/25.
 * 常用工具类
 */
public class WeatherHelper {

    // 天气显示文本
    public static String getWeatherShowByIcon(String icon) {
        if (StringUtils.isEmpty(icon)) return "";
        String iconTrim = icon.trim();
        switch (iconTrim) {
            case "0":
            case "30":
                return MyApp.get().getString(R.string.sunny);
            case "1":
            case "31":
                return MyApp.get().getString(R.string.cloudy);
            case "2":
                return MyApp.get().getString(R.string.over_cast);
            case "3":
            case "33":
                return MyApp.get().getString(R.string.showers);
            case "13":
            case "34":
                return MyApp.get().getString(R.string.snow_showers);
            case "18":
            case "32":
                return MyApp.get().getString(R.string.fog);
            case "20":
            case "36":
                return MyApp.get().getString(R.string.sandstorm);
            case "29":
            case "35":
                return MyApp.get().getString(R.string.dust);
            case "45":
            case "46":
                return MyApp.get().getString(R.string.haze);
            case "4":
                return MyApp.get().getString(R.string.thunder_shower);
            case "5":
                return MyApp.get().getString(R.string.hail);
            case "6":
                return MyApp.get().getString(R.string.sleet);
            case "7":
                return MyApp.get().getString(R.string.light_rain);
            case "8":
                return MyApp.get().getString(R.string.rain);
            case "9":
                return MyApp.get().getString(R.string.heavy_rain);
            case "10":
                return MyApp.get().getString(R.string.rain_storm);
            case "14":
                return MyApp.get().getString(R.string.light_snow);
            case "15":
                return MyApp.get().getString(R.string.snow);
            case "16":
                return MyApp.get().getString(R.string.heavy_snow);
            case "17":
                return MyApp.get().getString(R.string.blizzard);
            case "19":
                return MyApp.get().getString(R.string.freezing_rain);
            default:
                return "";
        }
    }

    // 天气显示icon
    public static int getWeatherIconById(String id) {
        if (StringUtils.isEmpty(id)) return 0;
        String idTrim = id.trim();
        int iconRes;
        switch (idTrim) {
            case "0":
                iconRes = R.mipmap.w0;
                break;
            case "1":
                iconRes = R.mipmap.w1;
                break;
            case "2":
                iconRes = R.mipmap.w2;
                break;
            case "3":
                iconRes = R.mipmap.w3;
                break;
            case "4":
                iconRes = R.mipmap.w4;
                break;
            case "5":
                iconRes = R.mipmap.w5;
                break;
            case "6":
                iconRes = R.mipmap.w6;
                break;
            case "7":
                iconRes = R.mipmap.w7;
                break;
            case "8":
                iconRes = R.mipmap.w8;
                break;
            case "9":
                iconRes = R.mipmap.w9;
                break;
            case "10":
                iconRes = R.mipmap.w10;
                break;
            case "13":
                iconRes = R.mipmap.w13;
                break;
            case "14":
                iconRes = R.mipmap.w14;
                break;
            case "15":
                iconRes = R.mipmap.w15;
                break;
            case "16":
                iconRes = R.mipmap.w16;
                break;
            case "17":
                iconRes = R.mipmap.w17;
                break;
            case "18":
                iconRes = R.mipmap.w18;
                break;
            case "19":
                iconRes = R.mipmap.w19;
                break;
            case "20":
                iconRes = R.mipmap.w20;
                break;
            case "29":
                iconRes = R.mipmap.w29;
                break;
            case "30":
                iconRes = R.mipmap.w30;
                break;
            case "31":
                iconRes = R.mipmap.w31;
                break;
            case "32":
                iconRes = R.mipmap.w32;
                break;
            case "33":
                iconRes = R.mipmap.w33;
                break;
            case "34":
                iconRes = R.mipmap.w34;
                break;
            case "35":
                iconRes = R.mipmap.w35;
                break;
            case "36":
                iconRes = R.mipmap.w36;
                break;
            case "44":
                iconRes = R.mipmap.w44;
                break;
            case "45":
                iconRes = R.mipmap.w45;
                break;
            case "46":
                iconRes = R.mipmap.w46;
                break;
            default:
                iconRes = 0;
        }
        return iconRes;
    }

}