package com.android.base.string;

/**
 * Created by JiangZhiGuo on 2016/10/11.
 * describe 常量工具类
 */
public class ConstantUtils {

    /**
     * ****************************** 时间 **********************************
     */
    public static final String FORMAT_CHINA_Y_M_D_H_M = "yyyy年MM月dd日 HH:mm";
    public static final String FORMAT_LINE_Y_M_D_H_M_S = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_LINE_M_D_H_M = "MM-dd HH:mm";
    public static final String FORMAT_LINE_Y_M_D = "yyyy-MM-dd";
    public static final String FORMAT_POINT_Y_M_D = "yyyy.MM.dd";
    public static final String FORMAT_CHINA_Y_M_D = "yyyy年MM月dd日";
    public static final String FORMAT_LINE_M_D = "MM-dd";
    public static final String FORMAT_POINT_M_D = "yyyy.MM";
    public static final String FORMAT_H_M_S = "HH:mm:ss";
    public static final String FORMAT_H_M = "HH:mm";
    public static final String FORMAT_M_S = "mm:ss";
    public static final String FORMAT_12_A = "hh:mm a"; // 12小时制

    /**
     * ****************************** 存储 **********************************
     */
    /* Byte与Byte的倍数 */
    public static final int BYTE = 1;
    /* KB与Byte的倍数 */
    public static final int KB = 1024;
    /* MB与Byte的倍数 */
    public static final int MB = 1048576;
    /* GB与Byte的倍数 */
    public static final int GB = 1073741824;

    public enum MemoryUnit {
        BYTE,
        KB,
        MB,
        GB
    }

    /**
     * ****************************** 时间 *********************************
     */
    /* 毫秒与毫秒的倍数 */
    public static final long MSEC = 1;
    /* 秒与毫秒的倍数 */
    public static final long SEC = MSEC * 1000;
    /* 分与毫秒的倍数 */
    public static final long MIN = SEC * 60;
    /* 时与毫秒的倍数 */
    public static final long HOUR = MIN * 60;
    /* 天与毫秒的倍数 */
    public static final long DAY = HOUR * 24;
    /* 天与毫秒的倍数 */
    public static final long MONTH = DAY * 30;
    /* 天与毫秒的倍数 */
    public static final long YEAR = MONTH * 12;

    public enum TimeUnit {
        MSEC,
        SEC,
        MIN,
        HOUR,
        DAY
    }

    /**
     * ******************************正则 ****************************
     */
     /* 正则：数字 */
    public static final String REGEX_NUMBER = "[0-9]*";
    /* 正则：邮政编码 */
    public static final String REGEX_POST_CODE = "[1-9]\\d{5}";
    /* 正则：密码  6-16位，数字和字母组合 */
    public static final String REGEX_PASSWORD = "^(?=.*?[a-zA-Z])(?=.*?[0-9])[a-zA-Z0-9]{6,16}$";
    /* 正则：手机号（精确） */
    public static final String REGEX_MOBILE = "^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$";
    /* 正则：电话号码 */
    public static final String REGEX_TEL = "^0\\d{2,3}[- ]?\\d{7,8}";
    /* 正则：身份证号码15位 */
    public static final String REGEX_IDCARD15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
    /*  正则：身份证号码18位 */
    public static final String REGEX_IDCARD18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$";
    /* 正则：邮箱 */
    public static final String REGEX_EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    /* 正则：URL */
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?";
    /* 正则：汉字 */
    public static final String REGEX_CHZ = "^[\\u4e00-\\u9fa5]+$";
    /* 正则：用户名，取值范围为a-z,A-Z,0-9,"_",汉字，不能以"_"结尾,用户名必须是6-20位 */
    public static final String REGEX_USERNAME = "^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$";
    /* 正则：yyyy-MM-dd格式的日期校验，已考虑平闰年 */
    public static final String REGEX_DATE = "^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$";
    /* 正则：IP地址 */
    public static final String REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

}
