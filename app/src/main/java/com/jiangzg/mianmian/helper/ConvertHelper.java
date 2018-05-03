package com.jiangzg.mianmian.helper;

import android.net.Uri;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.CalUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.User;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/3/25.
 * 常用工具类
 */
public class ConvertHelper {

    // 全路径截取name
    public static String getNameByOssPath(String objectKey) {
        if (StringUtils.isEmpty(objectKey)) {
            return "";
        }
        String[] split = objectKey.split("/");
        return split[split.length - 1].trim();
    }

    // url转oss路径
    public static String getOssPathByUrl(String url) {
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
        String endpoint = SPHelper.getOssInfo().getDomain();
        if (url.contains(endpoint + "/")) {
            String[] split = url.trim().split(endpoint + "/");
            if (split.length >= 2) {
                url = split[1];
            }
        }
        return url;
    }

    // 两点距离
    public static String getDistanceShow(float distance) {
        String show = "";
        if (distance <= 0) return show;
        if (distance < 1000) {
            show = String.format(Locale.getDefault(), "%.0fm", distance);
        } else {
            float km = distance / 1000;
            show = String.format(Locale.getDefault(), "%.1fkm", km);
        }
        return show;
    }

    // 获取ta的id
    public static long getTaIdByCp(Couple couple, long mid) {
        if (CheckHelper.isNullCouple(couple)) return 0;
        if (mid == couple.getCreatorId()) {
            return couple.getInviteeId();
        } else {
            return couple.getCreatorId();
        }
    }

    // 获取在cp中的头像
    public static String getAvatarByCp(Couple couple, long uid) {
        if (CheckHelper.isNullCouple(couple)) return "";
        if (uid == couple.getCreatorId()) {
            return couple.getCreatorAvatar();
        } else {
            return couple.getInviteeAvatar();
        }
    }

    // 获取在cp中的昵称
    public static String getNameByCp(Couple couple, long uid) {
        if (CheckHelper.isNullCouple(couple)) return "";
        if (uid == couple.getCreatorId()) {
            return couple.getCreatorName();
        } else {
            return couple.getInviteeName();
        }
    }

    // 性别显示
    public static String getSexShow(int sex) {
        if (sex == User.SEX_GIRL) {
            return MyApp.get().getString(R.string.girl);
        } else if (sex == User.SEX_BOY) {
            return MyApp.get().getString(R.string.boy);
        }
        return "";
    }

    // 性别小圆图显示
    public static int getSexResCircleSmall(int sex) {
        if (sex == User.SEX_BOY) {
            return R.mipmap.ic_sex_boy_circle;
        } else if (sex == User.SEX_GIRL) {
            return R.mipmap.ic_sex_girl_circle;
        }
        return 0;
    }

    /**
     * **************************************时间**************************************
     */

    // 时间转换(java -> go)
    public static long getGoTimeByJava(long time) {
        return time / 1000;
    }

    // 时间转换(go -> java)
    public static long getJavaTimeByGo(long time) {
        return time * 1000;
    }

    // 时间显示(有横线)
    public static String getTimeShowLine_HM_MD_YMD_ByGo(long time) {
        Calendar cNow = DateUtils.getCurrentCalendar();
        Calendar cTime = DateUtils.getCurrentCalendar();
        cTime.setTime(new Date(getJavaTimeByGo(time)));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = ConstantUtils.FORMAT_H_M;
        } else if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            format = ConstantUtils.FORMAT_LINE_M_D;
        } else {
            // 不同年
            format = ConstantUtils.FORMAT_LINE_Y_M_D;
        }
        return DateUtils.getString(getJavaTimeByGo(time), format);
    }

    // 时间显示(有横线)
    public static String getTimeShowLine_HM_MDHM_YMDHM_ByGo(long time) {
        Calendar cNow = DateUtils.getCurrentCalendar();
        Calendar cTime = DateUtils.getCurrentCalendar();
        cTime.setTime(new Date(getJavaTimeByGo(time)));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = ConstantUtils.FORMAT_H_M;
        } else if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            format = ConstantUtils.FORMAT_LINE_M_D_H_M;
        } else {
            // 不同年
            format = ConstantUtils.FORMAT_LINE_Y_M_D_H_M;
        }
        return DateUtils.getString(getJavaTimeByGo(time), format);
    }

    // 时间显示(有空格)
    public static String getTimeShowCnSpace_HM_MD_YMD_ByGo(long time) {
        return getTimeShowCnSpace_HM_MD_YMD_ByJava(getJavaTimeByGo(time));
    }

    // 时间显示(有空格)
    public static String getTimeShowCnSpace_HM_MD_YMD_ByJava(long time) {
        Calendar cNow = DateUtils.getCurrentCalendar();
        Calendar cTime = DateUtils.getCurrentCalendar();
        cTime.setTime(new Date(time));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = ConstantUtils.FORMAT_H_M;
        } else if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "MM" + month + " dd" + day;
        } else {
            // 不同年
            String year = MyApp.get().getString(R.string.year);
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "yyyy" + year + " MM" + month + " dd" + day;
        }
        return DateUtils.getString(time, format);
    }

    // 时间显示(有空格)
    public static String getTimeShowCnSpace_HM_MDHM_YMDHM_ByGo(long time) {
        return getTimeShowCnSpace_HM_MDHM_YMDHM_ByJava(getJavaTimeByGo(time));
    }

    // 时间显示(有空格)
    public static String getTimeShowCnSpace_HM_MDHM_YMDHM_ByJava(long time) {
        Calendar cNow = DateUtils.getCurrentCalendar();
        Calendar cTime = DateUtils.getCurrentCalendar();
        cTime.setTime(new Date(time));
        String format;
        if (CalUtils.isSameDay(cNow, cTime)) {
            // 同一天
            format = ConstantUtils.FORMAT_H_M;
        } else if (cNow.get(Calendar.YEAR) == cTime.get(Calendar.YEAR)) {
            // 同一年
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "MM" + month + " dd" + day + " HH:mm";
        } else {
            // 不同年
            String year = MyApp.get().getString(R.string.year);
            String month = MyApp.get().getString(R.string.month);
            String day = MyApp.get().getString(R.string.dayR);
            format = "yyyy" + year + " MM" + month + " dd" + day + " HH:mm";
        }
        return DateUtils.getString(time, format);
    }

    /**
     * **************************************列表**************************************
     */

    // 集合类型转换(string -> uri)
    public static ArrayList<Uri> getUriListByString(List<String> strings) {
        ArrayList<Uri> uriList = new ArrayList<>();
        if (strings == null || strings.size() <= 0) return uriList;
        for (String s : strings) {
            uriList.add(Uri.parse(s));
        }
        return uriList;
    }

    // 集合类型转换(string -> file)
    public static List<File> getFileListByPath(List<String> pathList) {
        List<File> fileList = new ArrayList<>();
        if (pathList == null || pathList.size() <= 0) return fileList;
        for (String path : pathList) {
            fileList.add(new File(path));
        }
        return fileList;
    }

    /**
     * **************************************三方资源**************************************
     */

    // 天气显示文本
    public static String getWeatherShowByIcon(String icon) {
        if (StringUtils.isEmpty(icon)) return "";
        String iconTrim = icon.trim();
        switch (iconTrim) {
            case "0":
            case "30":
                return MyApp.get().getString(R.string.sunny);
            case "1":
            case "31":
                return MyApp.get().getString(R.string.cloudy);
            case "2":
                return MyApp.get().getString(R.string.over_cast);
            case "3":
            case "33":
                return MyApp.get().getString(R.string.showers);
            case "13":
            case "34":
                return MyApp.get().getString(R.string.snow_showers);
            case "18":
            case "32":
                return MyApp.get().getString(R.string.fog);
            case "20":
            case "36":
                return MyApp.get().getString(R.string.sandstorm);
            case "29":
            case "35":
                return MyApp.get().getString(R.string.dust);
            case "45":
            case "46":
                return MyApp.get().getString(R.string.haze);
            case "4":
                return MyApp.get().getString(R.string.thunder_shower);
            case "5":
                return MyApp.get().getString(R.string.hail);
            case "6":
                return MyApp.get().getString(R.string.sleet);
            case "7":
                return MyApp.get().getString(R.string.light_rain);
            case "8":
                return MyApp.get().getString(R.string.rain);
            case "9":
                return MyApp.get().getString(R.string.heavy_rain);
            case "10":
                return MyApp.get().getString(R.string.rain_storm);
            case "14":
                return MyApp.get().getString(R.string.light_snow);
            case "15":
                return MyApp.get().getString(R.string.snow);
            case "16":
                return MyApp.get().getString(R.string.heavy_snow);
            case "17":
                return MyApp.get().getString(R.string.blizzard);
            case "19":
                return MyApp.get().getString(R.string.freezing_rain);
            default:
                return "";
        }
    }

    // 天气显示icon
    public static int getWeatherIconByRes(String icon) {
        if (StringUtils.isEmpty(icon)) return 0;
        String iconTrim = icon.trim();
        int iconRes;
        switch (iconTrim) {
            case "0":
                iconRes = R.mipmap.w0;
                break;
            case "1":
                iconRes = R.mipmap.w1;
                break;
            case "2":
                iconRes = R.mipmap.w2;
                break;
            case "3":
                iconRes = R.mipmap.w3;
                break;
            case "4":
                iconRes = R.mipmap.w4;
                break;
            case "5":
                iconRes = R.mipmap.w5;
                break;
            case "6":
                iconRes = R.mipmap.w6;
                break;
            case "7":
                iconRes = R.mipmap.w7;
                break;
            case "8":
                iconRes = R.mipmap.w8;
                break;
            case "9":
                iconRes = R.mipmap.w9;
                break;
            case "10":
                iconRes = R.mipmap.w10;
                break;
            case "13":
                iconRes = R.mipmap.w13;
                break;
            case "14":
                iconRes = R.mipmap.w14;
                break;
            case "15":
                iconRes = R.mipmap.w15;
                break;
            case "16":
                iconRes = R.mipmap.w16;
                break;
            case "17":
                iconRes = R.mipmap.w17;
                break;
            case "18":
                iconRes = R.mipmap.w18;
                break;
            case "19":
                iconRes = R.mipmap.w19;
                break;
            case "20":
                iconRes = R.mipmap.w20;
                break;
            case "29":
                iconRes = R.mipmap.w29;
                break;
            case "30":
                iconRes = R.mipmap.w30;
                break;
            case "31":
                iconRes = R.mipmap.w31;
                break;
            case "32":
                iconRes = R.mipmap.w32;
                break;
            case "33":
                iconRes = R.mipmap.w33;
                break;
            case "34":
                iconRes = R.mipmap.w34;
                break;
            case "35":
                iconRes = R.mipmap.w35;
                break;
            case "36":
                iconRes = R.mipmap.w36;
                break;
            case "44":
                iconRes = R.mipmap.w44;
                break;
            case "45":
                iconRes = R.mipmap.w45;
                break;
            case "46":
                iconRes = R.mipmap.w46;
                break;
            default:
                iconRes = 0;
        }
        return iconRes;
    }

}