package com.android.base.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jiangzg on 2014/03/30
 * describe 日期处理类
 */
public class TimeUtils {

    /**
     * 流水号
     */
    public static String genBillTime() {
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 获取日期格式
     */
    public static SimpleDateFormat getFormat(String type) {
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
        Date date = new Date();
        SimpleDateFormat format = getFormat(sFormat);
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
        Date date = new Date();
        try {
            SimpleDateFormat format = getFormat(sFormat);
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDate(Calendar calendar) {
        return calendar.getTime();
    }

    public static Date getDate(long time) {
        return new Date(time);
    }

    /**
     * 获取Calendar类型时间
     */
    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    public static Calendar getCalendar(String time, String sFormat) {
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
        return date.getTime();
    }

    public static long getLong(Calendar calendar) {
        Date date = calendar.getTime();
        return date.getTime();
    }

    public static long getLong(String time, String sFormat) {
        Date date = getDate(time, sFormat);
        return date.getTime();
    }

    /**
     * 获取String类型时间
     */
    public static String getString(Date date, String sFormat) {
        SimpleDateFormat format = getFormat(sFormat);
        return format.format(date);
    }

    public static String getString(long time, String sFormat) {
        Date date = new Date(time);
        return getString(date, sFormat);
    }

    public static String getString(Calendar calendar, String sFormat) {
        Date date = calendar.getTime();
        return getString(date, sFormat);
    }

}
