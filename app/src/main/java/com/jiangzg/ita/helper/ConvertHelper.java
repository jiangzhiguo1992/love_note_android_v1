package com.jiangzg.ita.helper;

import android.net.Uri;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
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

    // url转oss路径
    public static String convertUrl2OssPath(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        // 先剔除http:// 和 https://
        if (url.startsWith("http")) {
            String[] split = url.trim().split("//");
            if (split.length >= 2) {
                url = split[1];
            }
        }
        // 再剔除get参数
        if (url.contains("?")) {
            String[] split = url.trim().split("\\?");
            if (split.length > 0) {
                url = split[0];
            }
        }
        // 再剔除oss的endpoint
        if (url.contains(OssHelper.endpoint + "/")) {
            String[] split = url.trim().split(OssHelper.endpoint + "/");
            if (split.length >= 2) {
                url = split[1];
            }
        }
        return url;
    }

}