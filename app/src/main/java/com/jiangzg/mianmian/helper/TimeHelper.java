package com.jiangzg.mianmian.helper;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.CalUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;

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

    // 时间显示
    public static String getTimeShowCn_MD_YMD_ByGo(long time) {
        return getTimeShowCn_MD_YMD_ByJava(getJavaTimeByGo(time));
    }

    // 时间显示
    public static String getTimeShowCn_MD_YMD_ByJava(long time) {
        Calendar cNow = DateUtils.getCurrentCalendar();
        Calendar cTime = DateUtils.getCurrentCalendar();
        cTime.setTime(new Date(time));
        String format;
        if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "MM" + month + "dd" + day;
        } else {
            // 不同年
            String year = MyApp.get().getString(R.string.year);
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "yyyy" + year + "MM" + month + "dd" + day;
        }
        return DateUtils.getString(time, format);
    }

    // 时间显示(有横线)
    public static String getTimeShowLine_HM_MD_YMD_ByGo(long time) {
        Calendar cNow = DateUtils.getCurrentCalendar();
        Calendar cTime = DateUtils.getCurrentCalendar();
        cTime.setTime(new Date(getJavaTimeByGo(time)));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = ConstantUtils.FORMAT_H_M;
        } else if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            format = ConstantUtils.FORMAT_LINE_M_D;
        } else {
            // 不同年
            format = ConstantUtils.FORMAT_LINE_Y_M_D;
        }
        return DateUtils.getString(getJavaTimeByGo(time), format);
    }

    // 时间显示(有横线)
    public static String getTimeShowLine_HM_MDHM_YMDHM_ByGo(long time) {
        Calendar cNow = DateUtils.getCurrentCalendar();
        Calendar cTime = DateUtils.getCurrentCalendar();
        cTime.setTime(new Date(getJavaTimeByGo(time)));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = ConstantUtils.FORMAT_H_M;
        } else if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            format = ConstantUtils.FORMAT_LINE_M_D_H_M;
        } else {
            // 不同年
            format = ConstantUtils.FORMAT_LINE_Y_M_D_H_M;
        }
        return DateUtils.getString(getJavaTimeByGo(time), format);
    }

    // 时间显示(有空格)
    public static String getTimeShowCnSpace_MD_YMD_ByGo(long time) {
        return getTimeShowCnSpace_MD_YMD_ByJava(getJavaTimeByGo(time));
    }

    // 时间显示(有空格)
    public static String getTimeShowCnSpace_MD_YMD_ByJava(long time) {
        Calendar cNow = DateUtils.getCurrentCalendar();
        Calendar cTime = DateUtils.getCurrentCalendar();
        cTime.setTime(new Date(time));
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
        return DateUtils.getString(time, format);
    }

    // 时间显示(有空格)
    public static String getTimeShowCnSpace_HM_MD_YMD_ByGo(long time) {
        return getTimeShowCnSpace_HM_MD_YMD_ByJava(getJavaTimeByGo(time));
    }

    // 时间显示(有空格)
    public static String getTimeShowCnSpace_HM_MD_YMD_ByJava(long time) {
        Calendar cNow = DateUtils.getCurrentCalendar();
        Calendar cTime = DateUtils.getCurrentCalendar();
        cTime.setTime(new Date(time));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = ConstantUtils.FORMAT_H_M;
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
        return DateUtils.getString(time, format);
    }

    // 时间显示(有空格)
    public static String getTimeShowCnSpace_HM_MDHM_YMDHM_ByGo(long time) {
        return getTimeShowCnSpace_HM_MDHM_YMDHM_ByJava(getJavaTimeByGo(time));
    }

    // 时间显示(有空格)
    public static String getTimeShowCnSpace_HM_MDHM_YMDHM_ByJava(long time) {
        Calendar cNow = DateUtils.getCurrentCalendar();
        Calendar cTime = DateUtils.getCurrentCalendar();
        cTime.setTime(new Date(time));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = ConstantUtils.FORMAT_H_M;
        } else if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "MM" + month + " dd" + day + " HH:mm";
        } else {
            // 不同年
            String year = MyApp.get().getString(R.string.year);
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "yyyy" + year + " MM" + month + " dd" + day + " HH:mm";
        }
        return DateUtils.getString(time, format);
    }
}
