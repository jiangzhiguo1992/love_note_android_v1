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

    private static final String LOG_TAG = "DateUtils";

    /**
     * 获取日期格式
     */
    public static SimpleDateFormat getFormat(String type) {
        if (StringUtils.isEmpty(type)) {
            LogUtils.w(LOG_TAG, "getFormat: type == null");
            return null;
        }
        return new SimpleDateFormat(type, Locale.CHINA);
    }

    /**
     * 获取当前时间
     */
    public static Calendar getCurrentCalendar() {
        return Calendar.getInstance();
    }

    public static Date getCurrentDate() {
        return new Date();
    }

    public static String getCurrentString(String sFormat) {
        if (StringUtils.isEmpty(sFormat)) {
            LogUtils.w(LOG_TAG, "getCurrentString: sFormat == null");
            return "";
        }
        SimpleDateFormat format = getFormat(sFormat);
        if (format == null) {
            LogUtils.w(LOG_TAG, "getCurrentString: format == null");
            return "";
        }
        return format.format(new Date());
    }

    public static long getCurrentLong() {
        Date date = new Date();
        return date.getTime();
    }

    /**
     * 获取Date类型时间
     */
    public static Date getDate(String time, String sFormat) {
        if (StringUtils.isEmpty(time) || StringUtils.isEmpty(sFormat)) {
            LogUtils.w(LOG_TAG, "getDate: time == null || sFormat == null");
            return null;
        }
        SimpleDateFormat format = getFormat(sFormat);
        try {
            if (format == null) {
                LogUtils.w(LOG_TAG, "getDate: format == null");
                return null;
            }
            return format.parse(time);
        } catch (ParseException e) {
            LogUtils.e(LOG_TAG, "getDate", e);
        }
        return null;
    }

    public static Date getDate(Calendar calendar) {
        if (calendar == null) {
            LogUtils.w(LOG_TAG, "getDate: calendar == null");
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
    public static Calendar getCalendar(Date date) {
        if (date == null) {
            LogUtils.w(LOG_TAG, "getCalendar: date == null");
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar getCalendar(String time, String sFormat) {
        if (StringUtils.isEmpty(time) || StringUtils.isEmpty(sFormat)) {
            LogUtils.w(LOG_TAG, "getCalendar: time == null || sFormat == null");
            return null;
        }
        Date date = getDate(time, sFormat);
        return getCalendar(date);
    }

    public static Calendar getCalendar(long time) {
        Date date = new Date(time);
        return getCalendar(date);
    }

    /**
     * 获取long类型的时间
     */
    public static long getLong(Date date) {
        if (date == null) {
            LogUtils.w(LOG_TAG, "getLong: date == null");
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
            LogUtils.w(LOG_TAG, "getLong: time == null || sFormat == null");
            return 0;
        }
        Date date = getDate(time, sFormat);
        if (date == null) {
            LogUtils.w(LOG_TAG, "getLong: date == null");
            return 0;
        }
        return date.getTime();
    }

    /**
     * 获取String类型时间
     */
    public static String getString(Date date, String sFormat) {
        if (date == null || StringUtils.isEmpty(sFormat)) {
            LogUtils.w(LOG_TAG, "getString: date == null || sFormat == null");
            return "";
        }
        SimpleDateFormat format = getFormat(sFormat);
        if (format == null) {
            LogUtils.w(LOG_TAG, "getString: format == null");
            return "";
        }
        return format.format(date);
    }

    public static String getString(long time, String sFormat) {
        if (StringUtils.isEmpty(sFormat)) {
            LogUtils.w(LOG_TAG, "getString: sFormat == null");
            return "";
        }
        Date date = new Date(time);
        return getString(date, sFormat);
    }

    public static String getString(Calendar calendar, String sFormat) {
        if (calendar == null || StringUtils.isEmpty(sFormat)) {
            LogUtils.w(LOG_TAG, "getString: calendar == null || sFormat == null");
            return "";
        }
        Date date = calendar.getTime();
        return getString(date, sFormat);
    }

}
