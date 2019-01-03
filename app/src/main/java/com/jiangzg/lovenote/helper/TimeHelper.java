package com.jiangzg.lovenote.helper;

import com.jiangzg.base.time.CalUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.main.MyApp;

import java.util.Calendar;
import java.util.Date;

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

    // 时间显示(有横线)(一般不用)
    public static String getTimeShowLine_MD_YMD_ByGo(long time) {
        Calendar cNow = DateUtils.getCurrentCal();
        Calendar cTime = DateUtils.getCurrentCal();
        cTime.setTime(new Date(getJavaTimeByGo(time)));
        String format;
        if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            format = DateUtils.FORMAT_LINE_M_D;
        } else {
            // 不同年
            format = DateUtils.FORMAT_LINE_Y_M_D;
        }
        return DateUtils.getStr(getJavaTimeByGo(time), format);
    }

    // 时间显示(有横线)
    public static String getTimeShowLine_HM_MD_YMD_ByGo(long time) {
        Calendar cNow = DateUtils.getCurrentCal();
        Calendar cTime = DateUtils.getCurrentCal();
        cTime.setTime(new Date(getJavaTimeByGo(time)));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = DateUtils.FORMAT_H_M;
        } else if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            format = DateUtils.FORMAT_LINE_M_D;
        } else {
            // 不同年
            format = DateUtils.FORMAT_LINE_Y_M_D;
        }
        return DateUtils.getStr(getJavaTimeByGo(time), format);
    }

    // 时间显示(有横线)
    public static String getTimeShowLine_HM_MDHM_YMDHM_ByGo(long time) {
        Calendar cNow = DateUtils.getCurrentCal();
        Calendar cTime = DateUtils.getCurrentCal();
        cTime.setTime(new Date(getJavaTimeByGo(time)));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = DateUtils.FORMAT_H_M;
        } else if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            format = DateUtils.FORMAT_LINE_M_D_H_M;
        } else {
            // 不同年
            format = DateUtils.FORMAT_LINE_Y_M_D_H_M;
        }
        return DateUtils.getStr(getJavaTimeByGo(time), format);
    }

    // 时间显示(y年 M月 d日)(一般不用)
    public static String getTimeShowLocal_MD_YMD_ByGo(long time) {
        Calendar cNow = DateUtils.getCurrentCal();
        Calendar cTime = DateUtils.getCurrentCal();
        cTime.setTime(new Date(getJavaTimeByGo(time)));
        String format;
        if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "MM" + month + " dd" + day;
        } else {
            // 不同年
            String year = MyApp.get().getString(R.string.year);
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "yyyy" + year + " MM" + month + " dd" + day;
        }
        return DateUtils.getStr(getJavaTimeByGo(time), format);
    }

    // 时间显示(y年 M月 d日 / HH:mm)
    public static String getTimeShowLocal_HM_MD_YMD_ByGo(long time) {
        Calendar cNow = DateUtils.getCurrentCal();
        Calendar cTime = DateUtils.getCurrentCal();
        cTime.setTime(new Date(getJavaTimeByGo(time)));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = DateUtils.FORMAT_H_M;
        } else if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "MM" + month + " dd" + day;
        } else {
            // 不同年
            String year = MyApp.get().getString(R.string.year);
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "yyyy" + year + " MM" + month + " dd" + day;
        }
        return DateUtils.getStr(getJavaTimeByGo(time), format);
    }

    // 时间显示(y年M月d日 HH:mm)
    public static String getTimeShowLocal_HM_MDHM_YMDHM_ByGo(long time) {
        Calendar cNow = DateUtils.getCurrentCal();
        Calendar cTime = DateUtils.getCurrentCal();
        cTime.setTime(new Date(getJavaTimeByGo(time)));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = DateUtils.FORMAT_H_M;
        } else if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "MM" + month + "dd" + day + " HH:mm";
        } else {
            // 不同年
            String year = MyApp.get().getString(R.string.year);
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "yyyy" + year + "MM" + month + "dd" + day + " HH:mm";
        }
        return DateUtils.getStr(getJavaTimeByGo(time), format);
    }

}
