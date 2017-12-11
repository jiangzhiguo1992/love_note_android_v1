package com.android.base.time;

import com.android.base.R;
import com.android.base.component.application.AppContext;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by gg on 2017/4/9.
 * 日期管理类
 */
public class CalendarUtils {

    /**
     * ********************************天*****************************
     * 判断是否是同一天
     * int year = c.get(Calendar.YEAR);
     * int month = c.get(Calendar.MONTH);
     * int day = c.get(Calendar.DAY_OF_MONTH);
     */
    public static boolean isSameDay(Calendar c1, Calendar c2) {
        boolean isSameYear = c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
        boolean isSameMonth = c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
        boolean isSameDay = c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
        return isSameYear && isSameMonth && isSameDay;
    }

    /**
     * ***********************************周******************************
     * 获取星期 <p>注意：周日的Index才是1，周六为7</p>
     */
    public static int getWeekIndex(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取星期
     */
    public static String getWeek(Date time) {
        return new SimpleDateFormat("EEEE", Locale.getDefault()).format(time);
    }

    /**
     * 根据Index获取星期
     */
    public static String getWeek(int index) {
        switch (index) {
            case Calendar.MONDAY:
                return AppContext.get().getString(R.string.day1);
            case Calendar.TUESDAY:
                return AppContext.get().getString(R.string.day2);
            case Calendar.WEDNESDAY:
                return AppContext.get().getString(R.string.day3);
            case Calendar.THURSDAY:
                return AppContext.get().getString(R.string.day4);
            case Calendar.FRIDAY:
                return AppContext.get().getString(R.string.day5);
            case Calendar.SATURDAY:
                return AppContext.get().getString(R.string.day6);
            case Calendar.SUNDAY:
                return AppContext.get().getString(R.string.day7);
            default:
                return "";
        }
    }

    /**
     * 获取本周第一天的日期
     */
    public static Calendar getFirstDayOfWeek() {
        Calendar calendar = Calendar.getInstance();
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, -1);
        }
        return calendar;
    }

    /**
     * 获取传入日期的周第一天
     */
    public static Calendar getFirstDayOfWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.setFirstDayOfWeek(Calendar.SUNDAY);//设置一星期的第一天是哪一天
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);//指示一个星期中的某天
        cal.set(Calendar.HOUR_OF_DAY, 0);//指示一天中的小时。HOUR_OF_DAY 用于 24 小时制时钟。例如，在 10:04:15.250 PM 这一时刻，HOUR_OF_DAY 为 22。
        cal.set(Calendar.MINUTE, 0);//指示一小时中的分钟。例如，在 10:04:15.250 PM 这一时刻，MINUTE 为 4。
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * 得到某一天的该星期的第一日
     *
     * @param date           当前日期
     * @param firstDayOfWeek 一个星期的第一天为星期几
     */
    public static Calendar getFirstDayOfWeek(Date date, int firstDayOfWeek) {
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.setFirstDayOfWeek(firstDayOfWeek);//设置一星期的第一天是哪一天
        cal.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);//指示一个星期中的某天
        cal.set(Calendar.HOUR_OF_DAY, 0);//指示一天中的小时。HOUR_OF_DAY 用于 24 小时制时钟。例如，在 10:04:15.250 PM 这一时刻，HOUR_OF_DAY 为 22。
        cal.set(Calendar.MINUTE, 0);//指示一小时中的分钟。例如，在 10:04:15.250 PM 这一时刻，MINUTE 为 4。
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    /**
     * 获取上周周日
     */
    public static Calendar getSundayInLastWeek(int value) {
        Calendar cal = Calendar.getInstance();
        cal.setFirstDayOfWeek(Calendar.SUNDAY);
        cal.set(Calendar.DAY_OF_WEEK, value);
        return cal;
    }

    /**
     * ***********************************月********************************8
     * 传入月份的天数
     */
    public static int getDaysNumOfMonth(Calendar c) {
        Calendar tmp = Calendar.getInstance();
        tmp.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), 1);
        tmp.roll(Calendar.DATE, false); // 将临时Calendar设置成当月最后一天
        return tmp.get(Calendar.DATE); // 获取最后一天
    }

    /**
     * 获取月份中的第几周
     * <p>注意：国外周日才是新的一周的开始</p>
     *
     * @param time Date类型时间
     * @return 1...5
     */
    public static int getWeekOfMonth(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return cal.get(Calendar.WEEK_OF_MONTH);
    }

    /**
     * 两段时间的月数差
     */
    public static int getMonthsBetween(Calendar d1, Calendar d2) {
        boolean swaped = false;
        if (d1.after(d2)) {  // swap dates so that d1 is start and d2 is end
            Calendar swap = d1;
            d1 = d2;
            d2 = swap;
            swaped = true;
        }
        int months = d2.get(Calendar.MONTH) - d1.get(Calendar.MONTH);
        int y2 = d2.get(Calendar.YEAR);
        if (d1.get(Calendar.YEAR) != y2) {
            d1 = (Calendar) d1.clone();
            do {
                months += 12;
                d1.add(Calendar.YEAR, 1);
            } while (d1.get(Calendar.YEAR) != y2);
        }
        if (swaped) {
            months = -months;
        }
        return months;
    }

    /**
     * ***********************************年**********************************
     * 判断闰年
     */
    public static boolean isLeapYear(int year) {
        return year % 4 == 0 && year % 100 != 0 || year % 400 == 0;
    }

    /**
     * 获取年份中的第几周
     * <p>注意：国外周日才是新的一周的开始</p>
     *
     * @param time Date类型时间
     * @return 1...54
     */
    public static int getWeekOfYear(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }
}
