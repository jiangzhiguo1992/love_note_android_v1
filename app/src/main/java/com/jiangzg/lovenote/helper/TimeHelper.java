package com.jiangzg.lovenote.helper;

import com.jiangzg.base.common.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.main.MyApp;

/**
 * Created by JZG on 2018/6/26.
 * TimeHelper
 */
public class TimeHelper {

    // 时间转换(java -> go)
    public static long getGoTimeByJava(long time) {
        return time / 1000;
    }

    // 时间转换(go -> java)
    public static long getJavaTimeByGo(long time) {
        return time * 1000;
    }

    public static String getTimeShowLine_MD_YMD_ByGo(long time) {
        long jTime = getJavaTimeByGo(time);
        String format;
        if (DateUtils.isSameYear(DateUtils.getCurrentLong(), jTime)) {
            // 同一年
            format = DateUtils.FORMAT_LINE_M_D;
        } else {
            // 不同年
            format = DateUtils.FORMAT_LINE_Y_M_D;
        }
        return DateUtils.getStr(jTime, format);
    }

    public static String getTimeShowLine_HM_MD_YMD_ByGo(long time) {
        long jTime = getJavaTimeByGo(time);
        String format;
        if (DateUtils.isSameDay(DateUtils.getCurrentLong(), jTime)) {
            // 同一天
            format = DateUtils.FORMAT_H_M;
        } else if (DateUtils.isSameYear(DateUtils.getCurrentLong(), jTime)) {
            // 同一年
            format = DateUtils.FORMAT_LINE_M_D;
        } else {
            // 不同年
            format = DateUtils.FORMAT_LINE_Y_M_D;
        }
        return DateUtils.getStr(jTime, format);
    }

    public static String getTimeShowLine_HM_MDHM_YMDHM_ByGo(long time) {
        long jTime = getJavaTimeByGo(time);
        String format;
        if (DateUtils.isSameDay(DateUtils.getCurrentLong(), jTime)) {
            // 同一天
            format = DateUtils.FORMAT_H_M;
        } else if (DateUtils.isSameYear(DateUtils.getCurrentLong(), jTime)) {
            // 同一年
            format = DateUtils.FORMAT_LINE_M_D_H_M;
        } else {
            // 不同年
            format = DateUtils.FORMAT_LINE_Y_M_D_H_M;
        }
        return DateUtils.getStr(jTime, format);
    }

    public static String getTimeShowLocal_MD_YMD_ByGo(long time) {
        long jTime = getJavaTimeByGo(time);
        String year = MyApp.get().getString(R.string.year);
        String month = MyApp.get().getString(R.string.month);
        String day = MyApp.get().getString(R.string.dayR);
        String format;
        if (DateUtils.isSameYear(DateUtils.getCurrentLong(), jTime)) {
            // 同一年
            format = "MM" + month + " dd" + day;
        } else {
            // 不同年
            format = "yyyy" + year + " MM" + month + " dd" + day;
        }
        return DateUtils.getStr(jTime, format);
    }

    public static String getTimeShowLocal_HM_MD_YMD_ByGo(long time) {
        long jTime = getJavaTimeByGo(time);
        String year = MyApp.get().getString(R.string.year);
        String month = MyApp.get().getString(R.string.month);
        String day = MyApp.get().getString(R.string.dayR);
        String format;
        if (DateUtils.isSameDay(DateUtils.getCurrentLong(), jTime)) {
            // 同一天
            format = DateUtils.FORMAT_H_M;
        } else if (DateUtils.isSameYear(DateUtils.getCurrentLong(), jTime)) {
            // 同一年
            format = "MM" + month + " dd" + day;
        } else {
            // 不同年
            format = "yyyy" + year + " MM" + month + " dd" + day;
        }
        return DateUtils.getStr(jTime, format);
    }

    public static String getTimeShowLocal_HM_MDHM_YMDHM_ByGo(long time) {
        long jTime = getJavaTimeByGo(time);
        String year = MyApp.get().getString(R.string.year);
        String month = MyApp.get().getString(R.string.month);
        String day = MyApp.get().getString(R.string.dayR);
        String format;
        if (DateUtils.isSameDay(DateUtils.getCurrentLong(), jTime)) {
            // 同一天
            format = DateUtils.FORMAT_H_M;
        } else if (DateUtils.isSameYear(DateUtils.getCurrentLong(), jTime)) {
            // 同一年
            format = "MM" + month + "dd" + day + " HH:mm";
        } else {
            // 不同年
            format = "yyyy" + year + "MM" + month + "dd" + day + " HH:mm";
        }
        return DateUtils.getStr(jTime, format);
    }

}
