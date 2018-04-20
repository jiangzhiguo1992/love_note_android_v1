package com.jiangzg.base.time;

import com.jiangzg.base.common.ConstantUtils;
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

    public static String getCurrentCSTString(String sFormat) {
        if (StringUtils.isEmpty(sFormat)) {
            LogUtils.w(LOG_TAG, "getCurrentString: sFormat == null");
            return "";
        }
        SimpleDateFormat format = getFormat(sFormat);
        if (format == null) {
            LogUtils.w(LOG_TAG, "getCurrentString: format == null");
            return "";
        }
        Date date = new Date();
        // UTC比SCT早8个小时
        date.setTime(date.getTime() + ConstantUtils.HOUR * 8);
        return format.format(date);
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
    public static String getCSTString(Date date, String sFormat) {
        if (date == null || StringUtils.isEmpty(sFormat)) {
            LogUtils.w(LOG_TAG, "getCSTString: date == null || sFormat == null");
            return "";
        }
        // UTC比SCT早8个小时
        date.setTime(date.getTime() + ConstantUtils.HOUR * 8);
        SimpleDateFormat format = getFormat(sFormat);
        if (format == null) {
            LogUtils.w(LOG_TAG, "getCSTString: format == null");
            return "";
        }
        return format.format(date);
    }

    public static String getCSTString(long time, String sFormat) {
        if (StringUtils.isEmpty(sFormat)) {
            LogUtils.w(LOG_TAG, "getCSTString: sFormat == null");
            return "";
        }
        Date date = new Date(time);
        return getCSTString(date, sFormat);
    }

    public static String getCSTString(Calendar calendar, String sFormat) {
        if (calendar == null || StringUtils.isEmpty(sFormat)) {
            LogUtils.w(LOG_TAG, "getCSTString: calendar == null || sFormat == null");
            return "";
        }
        Date date = calendar.getTime();
        return getCSTString(date, sFormat);
    }

}
