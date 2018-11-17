package com.jiangzg.lovenote.helper;

import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.MyApp;

import java.util.Locale;

/**
 * Created by JZG on 2018/8/6.
 * CountHelper
 */
public class CountHelper {

    // getShowCount
    public static String getShowCount2Thousand(long count) {
        int unit = 10000; // 万
        if (Math.abs(count) < unit) {
            return String.valueOf(count);
        }
        return String.format(Locale.getDefault(), MyApp.get().getString(R.string.holder_thousand_point1), (double) count / (double) unit);
    }

    // getShowDistance
    public static String getShowDistance(float distance) {
        String distanceShow = "-m";
        if (distance >= 1000 * 100) {
            float km = distance / 1000;
            distanceShow = String.format(Locale.getDefault(), "%.0fkm", km);
        } else if (distance >= 1000) {
            float km = distance / 1000;
            distanceShow = String.format(Locale.getDefault(), "%.1fkm", km);
        } else if (distance > 0) {
            distanceShow = String.format(Locale.getDefault(), "%.0fm", distance);
        }
        return distanceShow;
    }

}
