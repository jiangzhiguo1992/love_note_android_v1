package com.jiangzg.ita.utils;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.CalUtils;
import com.jiangzg.base.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by JZG on 2018/3/12.
 * 时间管理类
 */

public class TimeUtils {

    public static String getSuggestShowBySecond(long time) {
        Calendar cNow = Calendar.getInstance();
        Calendar cTime = Calendar.getInstance();
        cTime.setTime(new Date(time * 1000));
        boolean sameDay = CalUtils.isSameDay(cNow, cTime);
        String format;
        if (sameDay) {
            format = ConstantUtils.FORMAT_CHINA_Y_M_D;
        } else {
            format = ConstantUtils.FORMAT_H_M;
        }
        return DateUtils.getString(time * 1000, format);
    }
}
