package com.android.base.time;

import com.android.base.R;
import com.android.base.component.application.AppContext;
import com.android.base.string.ConstantUtils;
import com.android.base.string.StringUtils;

import java.util.Calendar;

/**
 * Created by gg on 2017/4/7.
 * 时间单位类 也可参考Formatter
 */
public class UnitUtils {

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    public static UnitUtils get(long time) {
        Calendar c = TimeUtils.getCalendar(time);
        UnitUtils unit = new UnitUtils();
        unit.setYear(c.get(Calendar.YEAR));
        unit.setMonth(c.get(Calendar.MONTH));
        unit.setDay(c.get(Calendar.DAY_OF_MONTH));
        unit.setHour(c.get(Calendar.HOUR));
        unit.setMinute(c.get(Calendar.MINUTE));
        unit.setSecond(c.get(Calendar.SECOND));
        return unit;
    }

    /* between是时间间隔 */
    public static UnitUtils getBetween(long between) {
        UnitUtils unit = new UnitUtils();
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
        return unit;
    }

    /**
     * 获取间隔时间 eg: 1分钟，1小时，1天
     */
    public String getUnit() {
        if (year > 0) return year + AppContext.get().getString(R.string.year);
        if (month > 0) return month + AppContext.get().getString(R.string.month);
        if (day > 0) return day + AppContext.get().getString(R.string.day);
        if (hour > 0) return hour + AppContext.get().getString(R.string.hour);
        if (minute > 0) return minute + AppContext.get().getString(R.string.minute);
        if (second > 0) return second + AppContext.get().getString(R.string.second);
        return "";
    }

    /**
     * 获取时间戳
     */
    public String getFull(boolean y, boolean mo, boolean d, boolean h, boolean mi, boolean s) {
        String yea = "";
        if (year > 0 && y) {
            yea = year + AppContext.get().getString(R.string.year);
        }
        String mon = "";
        if (!StringUtils.isEmpty(yea) || (month > 0 && mo)) {
            mon = month + AppContext.get().getString(R.string.month);
        }
        String day = "";
        if (!StringUtils.isEmpty(mon) || (this.day > 0 && d)) {
            day = this.day + AppContext.get().getString(R.string.day);
        }
        String hou = "";
        if (!StringUtils.isEmpty(hou) || (hour > 0 && h)) {
            hou = hour + AppContext.get().getString(R.string.hour);
        }
        String min = "";
        if (!StringUtils.isEmpty(hou) || (minute > 0 && mi)) {
            min = minute + AppContext.get().getString(R.string.minute);
        }
        String sec = "";
        if (!StringUtils.isEmpty(hou) || (second > 0 && s)) {
            sec = second + AppContext.get().getString(R.string.second);
        }
        return yea + mon + day + hou + min + sec;
    }

    @Override
    public String toString() {
        return year + AppContext.get().getString(R.string.year) +
                month + AppContext.get().getString(R.string.month) +
                day + AppContext.get().getString(R.string.day) +
                hour + AppContext.get().getString(R.string.hour) +
                minute + AppContext.get().getString(R.string.minute) +
                second + AppContext.get().getString(R.string.second);
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
