package com.android.base.time;

import android.util.Log;

import com.android.base.common.ConstantUtils;
import com.android.base.common.StringUtils;

import java.util.Calendar;

/**
 * Created by gg on 2017/4/7.
 * 时间单位类 也可参考Formatter
 */
public class TimeUnit {

    private static final String LOG_TAG = "TimeUnit";

    public static final int TYPE_SECOND = 1;
    public static final int TYPE_MINUTE = 2;
    public static final int TYPE_HOUR = 3;
    public static final int TYPE_DAY = 4;
    public static final int TYPE_MONTH = 5;
    public static final int TYPE_YEAR = 6;

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public static TimeUnit get(long time) {
        Calendar c = DateUtils.getCalendar(time);
        TimeUnit unit = new TimeUnit();
        unit.setYear(c.get(Calendar.YEAR));
        unit.setMonth(c.get(Calendar.MONTH));
        unit.setDay(c.get(Calendar.DAY_OF_MONTH));
        unit.setHour(c.get(Calendar.HOUR));
        unit.setMinute(c.get(Calendar.MINUTE));
        unit.setSecond(c.get(Calendar.SECOND));
        Log.d(LOG_TAG, unit.toString());
        return unit;
    }

    /* between是时间间隔 */
    public static TimeUnit getBetween(long between) {
        TimeUnit unit = new TimeUnit();
        // year
        int year = (int) (between / ConstantUtils.YEAR);
        unit.setYear(year);
        // month
        int mouth;
        long monthB;
        if (year > 0) {
            long yearB = between % year;
            mouth = (int) (yearB / ConstantUtils.MONTH);
            monthB = yearB % ConstantUtils.MONTH;
        } else {
            mouth = (int) (between / ConstantUtils.MONTH);
            monthB = between % ConstantUtils.MONTH;
        }
        unit.setMonth(mouth);
        // day
        int day;
        long dayB;
        if (mouth > 0) {
            day = (int) (monthB / ConstantUtils.DAY);
            dayB = monthB % ConstantUtils.DAY;
        } else {
            day = (int) (between / ConstantUtils.DAY);
            dayB = between % ConstantUtils.DAY;
        }
        unit.setDay(day);
        // hour
        int hour;
        long hourB;
        if (day > 0) {
            hour = (int) (dayB / ConstantUtils.HOUR);
            hourB = dayB % ConstantUtils.HOUR;
        } else {
            hour = (int) (between / ConstantUtils.HOUR);
            hourB = between % ConstantUtils.HOUR;
        }
        unit.setHour(hour);
        // minute
        int minute;
        long minuteB;
        if (hour > 0) {
            minute = (int) (hourB / ConstantUtils.MIN);
            minuteB = hourB % ConstantUtils.MIN;
        } else {
            minute = (int) (between / ConstantUtils.MIN);
            minuteB = between % ConstantUtils.MIN;
        }
        unit.setMinute(minute);
        // minute
        int second;
        if (minute > 0) {
            second = (int) (minuteB / ConstantUtils.SEC);
        } else {
            second = (int) (between / ConstantUtils.SEC);
        }
        unit.setSecond(second);
        Log.d(LOG_TAG, unit.toString());
        return unit;
    }

    /**
     * 获取时间单位 eg: 1分钟，1小时，1天
     */
    public int getUnitType() {
        if (year > 0) return TYPE_YEAR;
        if (month > 0) return TYPE_MONTH;
        if (day > 0) return TYPE_DAY;
        if (hour > 0) return TYPE_HOUR;
        if (minute > 0) return TYPE_MINUTE;
        if (second > 0) return TYPE_SECOND;
        return TYPE_SECOND;
    }

    /**
     * 获取时间的所有单位
     */
    public String getUnitFull(boolean y, boolean mo, boolean d, boolean h, boolean mi, boolean s,
                              String yearShow, String monthShow, String dayShow, String hourShow, String minuteShow, String secondShow) {
        String yea = "";
        if (year > 0 && y) {
            yea = year + yearShow;
        }
        String mon = "";
        if (!StringUtils.isEmpty(yea) || (month > 0 && mo)) {
            mon = month + monthShow;
        }
        String day = "";
        if (!StringUtils.isEmpty(mon) || (this.day > 0 && d)) {
            day = this.day + dayShow;
        }
        String hou = "";
        if (!StringUtils.isEmpty(hou) || (hour > 0 && h)) {
            hou = hour + hourShow;
        }
        String min = "";
        if (!StringUtils.isEmpty(hou) || (minute > 0 && mi)) {
            min = minute + minuteShow;
        }
        String sec = "";
        if (!StringUtils.isEmpty(hou) || (second > 0 && s)) {
            sec = second + secondShow;
        }
        return yea + mon + day + hou + min + sec;
    }

    @Override
    public String toString() {
        return "TimeUnit{" +
                "year=" + year +
                ", month=" + month +
                ", day=" + day +
                ", hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                '}';
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
