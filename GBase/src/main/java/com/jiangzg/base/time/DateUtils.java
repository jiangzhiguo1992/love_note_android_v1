package com.jiangzg.base.time;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jiangzg on 2014/03/30
 * describe 日期处理类
 * 1. date 和 long 都是记录的从1970到现在为止的刻度，是公用的
 * 2. string 和 calender 是用来描述刻度的单位，国家之间可能不一样
 * 3. CST 为北京时间 TimeZone.setDefault(TimeZone.getTimeZone("GMT+08:00"));
 */
public class DateUtils {

    public static final String FORMAT_CHINA_Y_M_D__H_M_S_S = "yyyyMMdd-HHmmssSSS"; // 一般用于流水号
    public static final String FORMAT_LINE_Y_M_D_H_M_S = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_CHINA_Y_M_D_H_M_S = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String FORMAT_LINE_Y_M_D_H_M = "yyyy-MM-dd HH:mm";
    public static final String FORMAT_CHINA_Y_M_D_H_M = "yyyy年MM月dd日 HH:mm";
    public static final String FORMAT_LINE_Y_M_D = "yyyy-MM-dd";
    public static final String FORMAT_POINT_Y_M_D = "yyyy.MM.dd";
    public static final String FORMAT_CHINA_Y_M_D = "yyyy年MM月dd日";
    public static final String FORMAT_LINE_Y_M = "yyyy-MM";
    public static final String FORMAT_POINT_M_D = "yyyy.MM";
    public static final String FORMAT_CHINA_M_D = "MM月dd日";
    public static final String FORMAT_LINE_M_D = "MM-dd";
    public static final String FORMAT_LINE_M_D_H_M = "MM-dd HH:mm";
    public static final String FORMAT_H_M_S = "HH:mm:ss";
    public static final String FORMAT_H_M = "HH:mm";
    public static final String FORMAT_M_S = "mm:ss";
    public static final String FORMAT_12_A = "hh:mm a"; // 12小时制

    /**
     * 获取当前时间
     */
    public static Date getCurrentDate() {
        return new Date();
    }

    public static Calendar getCurrentCal() {
        return Calendar.getInstance();
    }

    public static long getCurrentLong() {
        return getCurrentDate().getTime();
    }

    public static String getCurrentStr(String sFormat) {
        if (StringUtils.isEmpty(sFormat)) {
            LogUtils.w(DateUtils.class, "getCurrentStr", "sFormat == null");
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(sFormat, Locale.getDefault());
        return format.format(getCurrentDate());
    }

    /**
     * 获取Date类型时间
     */
    public static Date getDate(String time, String sFormat) {
        if (StringUtils.isEmpty(time) || StringUtils.isEmpty(sFormat)) {
            LogUtils.w(DateUtils.class, "getDate", "time == null || sFormat == null");
            return null;
        }
        SimpleDateFormat format = new SimpleDateFormat(sFormat, Locale.getDefault());
        try {
            return format.parse(time);
        } catch (ParseException e) {
            LogUtils.e(DateUtils.class, "getDate", e);
        }
        return null;
    }

    public static Date getDate(Calendar calendar) {
        if (calendar == null) {
            LogUtils.w(DateUtils.class, "getDate", "calendar == null");
            return null;
        }
        return calendar.getTime();
    }

    public static Date getDate(long time) {
        return new Date(time);
    }

    /**
     * 获取Calendar类型时间
     */
    public static Calendar getCal(Date date) {
        if (date == null) {
            LogUtils.w(DateUtils.class, "getCal", "date == null");
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar getCal(String time, String sFormat) {
        if (StringUtils.isEmpty(time) || StringUtils.isEmpty(sFormat)) {
            LogUtils.w(DateUtils.class, "getCal", "time == null || sFormat == null");
            return null;
        }
        Date date = getDate(time, sFormat);
        return getCal(date);
    }

    public static Calendar getCal(long time) {
        Date date = new Date(time);
        return getCal(date);
    }

    /**
     * 获取long类型的时间
     */
    public static long getLong(Date date) {
        if (date == null) {
            LogUtils.w(DateUtils.class, "getLong", "date == null");
            return 0;
        }
        return date.getTime();
    }

    public static long getLong(Calendar calendar) {
        Date date = calendar.getTime();
        return date.getTime();
    }

    public static long getLong(String time, String sFormat) {
        if (StringUtils.isEmpty(time) || StringUtils.isEmpty(sFormat)) {
            LogUtils.w(DateUtils.class, "getLong", "time == null || sFormat == null");
            return 0;
        }
        Date date = getDate(time, sFormat);
        if (date == null) {
            LogUtils.w(DateUtils.class, "getLong", "date == null");
            return 0;
        }
        return date.getTime();
    }

    /**
     * 获取String类型时间
     */
    public static String getStr(Date date, String sFormat) {
        if (date == null || StringUtils.isEmpty(sFormat)) {
            LogUtils.w(DateUtils.class, "getStr", "date == null || sFormat == null");
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(sFormat, Locale.getDefault());
        return format.format(date);
    }

    public static String getStr(long time, String sFormat) {
        if (StringUtils.isEmpty(sFormat)) {
            LogUtils.w(DateUtils.class, "getStr", "sFormat == null");
            return "";
        }
        Date date = new Date(time);
        return getStr(date, sFormat);
    }

    public static String getStr(Calendar calendar, String sFormat) {
        if (calendar == null || StringUtils.isEmpty(sFormat)) {
            LogUtils.w(DateUtils.class, "getStr", "calendar == null || sFormat == null");
            return "";
        }
        Date date = calendar.getTime();
        return getStr(date, sFormat);
    }

}
