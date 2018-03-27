package com.jiangzg.ita.helper;

import android.net.Uri;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.CalUtils;
import com.jiangzg.base.time.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by JZG on 2018/3/25.
 * 常用工具类
 */
public class ConvertHelper {

    public static ArrayList<Uri> convertListString2uri(List<String> strings) {
        ArrayList<Uri> uriList = new ArrayList<>();
        if (strings == null || strings.size() <= 0) return uriList;
        for (String s : strings) {
            uriList.add(Uri.parse(s));
        }
        return uriList;
    }

    public static String ConvertSecond2DiffDay(long time) {
        Calendar cNow = Calendar.getInstance();
        Calendar cTime = Calendar.getInstance();
        cTime.setTime(new Date(time * 1000));
        boolean sameDay = CalUtils.isSameDay(cNow, cTime);
        String format;
        if (sameDay) {
            format = ConstantUtils.FORMAT_H_M;
        } else {
            format = ConstantUtils.FORMAT_LINE_Y_M_D;
        }
        return DateUtils.getString(time * 1000, format);
    }

}