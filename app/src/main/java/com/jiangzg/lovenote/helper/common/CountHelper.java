package com.jiangzg.lovenote.helper.common;

import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.main.MyApp;

import java.util.Locale;

/**
 * Created by JZG on 2018/8/6.
 * CountHelper
 */
public class CountHelper {

    public static String getShowCount2Thousand(int count) {
        int unit = 10000; // 万
        if (Math.abs(count) < unit) {
            return String.valueOf(count);
        }
        return String.format(Locale.getDefault(), MyApp.get().getString(R.string.holder_thousand_point1), (double) count / (double) unit);
    }

    public static String getShowDistance(float distance) {
        String distanceShow = "-m";
        if (distance >= 1000 * 100) {
            float km = distance / 1000;
            distanceShow = String.format(Locale.getDefault(), "%.0fkm", km);
        } else if (distance >= 1000) {
            float km = distance / 1000;
            distanceShow = String.format(Locale.getDefault(), "%.1fkm", km);
        } else if (distance >= 0) {
            distanceShow = String.format(Locale.getDefault(), "%.0fm", distance);
        }
        return distanceShow;
    }

    public static String getDurationShow(int duration) {
        String secShow;
        long sec = duration % (TimeUnit.MIN / TimeUnit.SEC);
        if (sec < 1) {
            secShow = "00";
        } else if (sec < 10) {
            secShow = "0" + sec;
        } else {
            secShow = String.valueOf(sec);
        }
        String minShow;
        long min = duration / (TimeUnit.MIN / TimeUnit.SEC);
        if (min < 1) {
            minShow = "00";
        } else if (min < 10) {
            minShow = "0" + min;
        } else {
            minShow = String.valueOf(min);
        }
        return minShow + ":" + secShow;
    }

    public static String getBetweenTimeGoneShow(long between) {
        String time = MyApp.get().getString(R.string.just_now);
        if (between >= TimeUnit.YEAR) {
            time = (between / TimeUnit.YEAR) + MyApp.get().getString(R.string.year) + MyApp.get().getString(R.string.before);
        } else if (between >= TimeUnit.MONTH) {
            time = (between / TimeUnit.MONTH) + MyApp.get().getString(R.string.month) + MyApp.get().getString(R.string.before);
        } else if (between >= TimeUnit.DAY) {
            time = (between / TimeUnit.DAY) + MyApp.get().getString(R.string.dayT) + MyApp.get().getString(R.string.before);
        } else if (between >= TimeUnit.HOUR) {
            time = (between / TimeUnit.HOUR) + MyApp.get().getString(R.string.hour) + MyApp.get().getString(R.string.before);
        } else if (between >= TimeUnit.MIN * 5) {
            time = (between / TimeUnit.MIN) + MyApp.get().getString(R.string.minute) + MyApp.get().getString(R.string.before);
        }
        return time;
    }

}
