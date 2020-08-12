package com.jiangzg.lovenote.helper.common;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.main.MyApp;

/**
 * Created by JZG on 2018/3/25.
 * 常用工具类
 */
public class WeatherHelper {

    public static String getIconByAMap(String show) {
        if (StringUtils.isEmpty(show)) return "";
        String showTrim = show.trim();
        switch (showTrim) {
            case "晴天":
            case "晴":
                return "0";
            case "多云":
            case "少云":
            case "晴间多云":
                return "1";
            case "阴":
                return "2";
            case "阵雨":
            case "强阵雨":
                return "3";
            case "雷阵雨":
            case "强雷阵雨":
                return "4";
            case "冰雹":
            case "雷阵雨并伴有冰雹":
                return "5";
            case "雨夹雪":
            case "雨雪天气":
            case "阵雨夹雪":
                return "6";
            case "雨":
            case "小雨":
            case "毛毛雨/细雨":
            case "小雨-中雨":
                return "7";
            case "中雨":
            case "中雨-大雨":
                return "8";
            case "大雨":
            case "大雨-暴雨":
                return "9";
            case "暴雨":
            case "大暴雨":
            case "特大暴雨":
            case "极端降雨":
            case "暴雨-大暴雨":
            case "大暴雨-特大暴雨":
                return "10";
            case "雪":
            case "阵雪":
                return "13";
            case "小雪":
            case "小雪-中雪":
                return "14";
            case "中雪":
            case "中雪-大雪":
                return "15";
            case "大雪":
            case "大雪-暴雪":
                return "16";
            case "暴雪":
                return "17";
            case "雾":
            case "浓雾":
            case "强浓雾":
            case "轻雾":
            case "大雾":
            case "特强浓雾":
                return "18";
            case "冻雨":
                return "19";
            case "沙尘暴":
            case "强沙尘暴":
            case "龙卷风":
                return "20";
            case "扬沙":
            case "浮尘":
                return "29";
            case "霾":
            case "中度霾":
            case "重度霾":
            case "严重霾":
                return "45";
            case "有风":
            case "平静":
            case "微风":
            case "和风":
            case "清风":
            case "强风/劲风":
            case "疾风":
            case "大风":
            case "烈风":
            case "风暴":
            case "狂爆风":
            case "飓风":
            case "热带风暴":
            case "热":
            case "冷":
            case "未知":
            default:
                return "";
        }
    }

    public static String getShowById(String id) {
        if (StringUtils.isEmpty(id)) return "";
        String idTrim = id.trim();
        switch (idTrim) {
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

    public static int getIconById(String id) {
        if (StringUtils.isEmpty(id)) return R.mipmap.w0;
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
                iconRes = R.mipmap.w0;
        }
        return iconRes;
    }

}