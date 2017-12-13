package com.android.base.time;

import android.util.Log;

import com.android.base.common.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jiangzg on 2014/03/30
 * describe 日期处理类
 */
public class DateUtils {

    private static final String LOG_TAG = "DateUtils";

    /**
     * 流水号
     */
    public static String genBillTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 获取日期格式
     */
    public static SimpleDateFormat getFormat(String type) {
        if (StringUtils.isEmpty(type)) {
            Log.e(LOG_TAG, "getFormat: type == null");
            return null;
        }
        return new SimpleDateFormat(type, Locale.getDefault());
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
            Log.e(LOG_TAG, "getCurrentString: sFormat == null");
            return null;
        }
        Date date = new Date();
        SimpleDateFormat format = getFormat(sFormat);
        if (format != null) {
            return format.format(date);
        } else {
            Log.e(LOG_TAG, "getCurrentString: format == null");
            return "";
        }
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
            Log.e(LOG_TAG, "getDate: time == null || sFormat == null");
            return null;
        }
        Date date = new Date();
        try {
            SimpleDateFormat format = getFormat(sFormat);
            if (format != null) {
                date = format.parse(time);
            } else {
                Log.e(LOG_TAG, "getDate: format == null");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDate(Calendar calendar) {
        if (calendar == null) {
            Log.e(LOG_TAG, "getDate: calendar == null");
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
            Log.e(LOG_TAG, "getCalendar: date == null");
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar getCalendar(String time, String sFormat) {
        if (StringUtils.isEmpty(time) || StringUtils.isEmpty(sFormat)) {
            Log.e(LOG_TAG, "getCalendar: time == null || sFormat == null");
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
            Log.e(LOG_TAG, "getLong: date == null");
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
            Log.e(LOG_TAG, "getLong: time == null || sFormat == null");
            return 0;
        }
        Date date = getDate(time, sFormat);
        if (date != null) {
            return date.getTime();
        } else {
            Log.e(LOG_TAG, "getLong: date == null");
            return 0;
        }
    }

    /**
     * 获取String类型时间
     */
    public static String getString(Date date, String sFormat) {
        if (date == null || StringUtils.isEmpty(sFormat)) {
            Log.e(LOG_TAG, "getString: date == null || sFormat == null");
            return "";
        }
        SimpleDateFormat format = getFormat(sFormat);
        if (format != null) {
            return format.format(date);
        } else {
            Log.e(LOG_TAG, "getString: format == null");
            return "";
        }
    }

    public static String getString(long time, String sFormat) {
        if (StringUtils.isEmpty(sFormat)) {
            Log.e(LOG_TAG, "getString: sFormat == null");
            return "";
        }
        Date date = new Date(time);
        return getString(date, sFormat);
    }

    public static String getString(Calendar calendar, String sFormat) {
        if (calendar == null || StringUtils.isEmpty(sFormat)) {
            Log.e(LOG_TAG, "getString: calendar == null || sFormat == null");
            return "";
        }
        Date date = calendar.getTime();
        return getString(date, sFormat);
    }

}
