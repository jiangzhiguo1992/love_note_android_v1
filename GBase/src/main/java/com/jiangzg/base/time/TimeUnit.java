package com.jiangzg.base.time;

import android.support.annotation.StringRes;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;

import java.util.Calendar;

/**
 * Created by gg on 2017/4/7.
 * 时间单位类 也可参考Formatter
 */
public class TimeUnit {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public static TimeUnit getByDate(long date) {
        Calendar c = DateUtils.getCalendar(date);
        TimeUnit unit = new TimeUnit();
        unit.setYear(c.get(Calendar.YEAR));
        unit.setMonth(c.get(Calendar.MONTH));
        unit.setDay(c.get(Calendar.DAY_OF_MONTH));
        unit.setHour(c.get(Calendar.HOUR));
        unit.setMinute(c.get(Calendar.MINUTE));
        unit.setSecond(c.get(Calendar.SECOND));
        return unit;
    }

    /* between是时间间隔 */
    public static TimeUnit convertTime2Unit(long between) {
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
        LogUtils.d(TimeUnit.class, "convertTime2Unit", unit.toString());
        return unit;
    }

    /**
     * 获取时间单位 eg: 1分钟，1小时，1天
     */
    public ConstantUtils.TimeUnit getMaxUnit() {
        if (year > 0) return ConstantUtils.TimeUnit.YEAR;
        if (month > 0) return ConstantUtils.TimeUnit.MONTH;
        if (day > 0) return ConstantUtils.TimeUnit.DAY;
        if (hour > 0) return ConstantUtils.TimeUnit.HOUR;
        if (minute > 0) return ConstantUtils.TimeUnit.MIN;
        if (second > 0) return ConstantUtils.TimeUnit.SEC;
        return ConstantUtils.TimeUnit.MSEC;
    }

    /**
     * 获取所有有效时间的显示
     */
    public String getAllShow(boolean y, boolean mo, boolean d, boolean h, boolean mi, boolean s,
                             @StringRes int yearShow, @StringRes int monthShow, @StringRes int dayShow,
                             @StringRes int hourShow, @StringRes int minuteShow, @StringRes int secondShow) {
        String year = AppBase.getInstance().getString(yearShow);
        String month = AppBase.getInstance().getString(monthShow);
        String day = AppBase.getInstance().getString(dayShow);
        String hour = AppBase.getInstance().getString(hourShow);
        String minute = AppBase.getInstance().getString(minuteShow);
        String second = AppBase.getInstance().getString(secondShow);
        return getAllShow(y, mo, d, h, mi, s, year, month, day, hour, minute, second);
    }

    /**
     * 获取所有有效时间的显示
     */
    public String getAllShow(boolean y, boolean mo, boolean d, boolean h, boolean mi, boolean s,
                             String yearShow, String monthShow, String dayShow,
                             String hourShow, String minuteShow, String secondShow) {
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

    /**
     * 获取最大有效时间的显示
     */
    public String getMaxShow(boolean y, boolean mo, boolean d, boolean h, boolean mi, boolean s,
                             @StringRes int yearShow, @StringRes int monthShow, @StringRes int dayShow,
                             @StringRes int hourShow, @StringRes int minuteShow, @StringRes int secondShow) {
        String yea = "";
        if (year > 0 && y) {
            yea = year + AppBase.getInstance().getString(yearShow);
            return yea;
        }
        String mon = "";
        if (!StringUtils.isEmpty(yea) || (month > 0 && mo)) {
            mon = month + AppBase.getInstance().getString(monthShow);
            return mon;
        }
        String day = "";
        if (!StringUtils.isEmpty(mon) || (this.day > 0 && d)) {
            day = this.day + AppBase.getInstance().getString(dayShow);
            return day;
        }
        String hou = "";
        if (!StringUtils.isEmpty(day) || (hour > 0 && h)) {
            hou = hour + AppBase.getInstance().getString(hourShow);
            return hou;
        }
        String min = "";
        if (!StringUtils.isEmpty(hou) || (minute > 0 && mi)) {
            min = minute + AppBase.getInstance().getString(minuteShow);
            return min;
        }
        String sec;
        if (!StringUtils.isEmpty(min) || (second > 0 && s)) {
            sec = second + AppBase.getInstance().getString(secondShow);
            return sec;
        }
        return "";
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
