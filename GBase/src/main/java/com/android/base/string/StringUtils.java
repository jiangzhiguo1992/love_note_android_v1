package com.android.base.string;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.android.base.string.ConstantUtils.REGEX_CHZ;
import static com.android.base.string.ConstantUtils.REGEX_DATE;
import static com.android.base.string.ConstantUtils.REGEX_EMAIL;
import static com.android.base.string.ConstantUtils.REGEX_IDCARD15;
import static com.android.base.string.ConstantUtils.REGEX_IDCARD18;
import static com.android.base.string.ConstantUtils.REGEX_IP;
import static com.android.base.string.ConstantUtils.REGEX_MOBILE;
import static com.android.base.string.ConstantUtils.REGEX_NUMBER;
import static com.android.base.string.ConstantUtils.REGEX_PASSWORD;
import static com.android.base.string.ConstantUtils.REGEX_POST_CODE;
import static com.android.base.string.ConstantUtils.REGEX_TEL;
import static com.android.base.string.ConstantUtils.REGEX_URL;

/**
 * Created by jiang on 2016/10/13
 * <p/>
 * 字符串处理类
 */
public class StringUtils {

    /**
     * 获取uuid
     */
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取一定长度的uuid
     */
    public static String getUUID(int length) {
        String random = getUUID();
        return random.substring(random.length() - length, random.length());
    }

    /**
     * demo (num,4,3,*) 1860*********241
     */
    public static String replace(String old, int left, int right, CharSequence replace) {
        if (isEmpty(old)) return "";
        StringBuilder result = new StringBuilder(old);
        int length = result.length();
        if (length > left + right) { // 填充
            StringBuilder beReplace = new StringBuilder();
            for (int i = 0; i < length - left + right; i++) {
                beReplace.append(replace);
            }
            result.replace(left, result.length() - right, beReplace.toString());
            return result.toString();
        }
        return old;
    }

    /**
     * 获取字符串的长度，如果有中文，则每个中文字符计为2位
     */
    public static int getLength(String validateStr) {
        int valueLength = 0;
        if (validateStr == null) return valueLength;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < validateStr.length(); i++) {
            /* 获取一个字符 */
            String temp = validateStr.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese))
                valueLength += 2;  /* 中文字符长度为2 */
            else
                valueLength += 1; /* 其他字符长度为1 */
        }
        return valueLength;
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 判断是否为邮政编码
     */
    public static boolean isPostCode(String postCode){
        return isMatch(REGEX_POST_CODE, postCode);
    }

    /**
     * 判断是否为数字
     */
    public static boolean isNumber(String src) {
        return isMatch(REGEX_NUMBER, src);
    }

    /**
     * 验证密码 6-16位，数字和字母组合
     */
    public static boolean isPassword(String password) {
        return isMatch(REGEX_PASSWORD, password);
    }

    /**
     * 验证身份证号码15位
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isIDCard15(String string) {
        return isMatch(REGEX_IDCARD15, string);
    }

    /**
     * 验证身份证号码18位
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isIDCard18(String string) {
        return isMatch(REGEX_IDCARD18, string);
    }

    /**
     * 验证邮箱
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isEmail(String string) {
        return isMatch(REGEX_EMAIL, string);
    }

    /**
     * 验证yyyy-MM-dd格式的日期校验，已考虑平闰年
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isDate(String string) {
        return isMatch(REGEX_DATE, string);
    }

    /**
     * 验证汉字
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isCN(String string) {
        return isMatch(REGEX_CHZ, string);
    }

    /**
     * 验证URL
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isURL(String string) {
        return isMatch(REGEX_URL, string);
    }

    /**
     * 验证IP地址
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isIP(String string) {
        return isMatch(REGEX_IP, string);
    }

    /**
     * string是否匹配regex
     *
     * @param regex  正则表达式字符串
     * @param string 要匹配的字符串
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMatch(String regex, String string) {
        return !isEmpty(string) && Pattern.matches(regex, string);
    }

    /**
     * 验证手机号（精确）
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isMobile(String string) {
        return isMatch(REGEX_MOBILE, string);
    }

    /**
     * 验证电话号码
     *
     * @param string 待验证文本
     * @return {@code true}: 匹配<br>{@code false}: 不匹配
     */
    public static boolean isTel(String string) {
        return isMatch(REGEX_TEL, string);
    }

    /**
     * 转化为半角字符
     *
     * @param s 待转字符串
     * @return 半角字符串
     */
    public static String toDBC(String s) {
        if (isEmpty(s)) return s;
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288)
                chars[i] = ' ';
            else if (65281 <= chars[i] && chars[i] <= 65374)
                chars[i] = (char) (chars[i] - 65248);
            else
                chars[i] = chars[i];
        }
        return new String(chars);
    }

    /**
     * 转化为全角字符
     *
     * @param s 待转字符串
     * @return 全角字符串
     */
    public static String toSBC(String s) {
        if (isEmpty(s)) return s;
        char[] chars = s.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ')
                chars[i] = (char) 12288;
            else if (33 <= chars[i] && chars[i] <= 126)
                chars[i] = (char) (chars[i] + 65248);
            else
                chars[i] = chars[i];
        }
        return new String(chars);
    }

    /**
     * 首字母大写
     *
     * @param s 待转字符串
     * @return 首字母大写字符串
     */
    public static String upperFirstLetter(String s) {
        if (isEmpty(s) || !Character.isLowerCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param s 待转字符串
     * @return 首字母小写字符串
     */
    public static String lowerFirstLetter(String s) {
        if (isEmpty(s) || !Character.isUpperCase(s.charAt(0))) return s;
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * 反转字符串
     *
     * @param s 待反转字符串
     * @return 反转字符串
     */
    public static String reverse(String s) {
        int len = s.length();
        if (len <= 1) return s;
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * 单个汉字转成ASCII码
     *
     * @param s 单个汉字字符串
     * @return 如果字符串长度是1返回的是对应的ascii码，否则返回-1
     */
    private static int getASCII(String s) {
        if (s.length() != 1) return -1;
        int ascii = 0;
        try {
            byte[] bytes = s.getBytes("GB2312");
            if (bytes.length == 1) {
                ascii = bytes[0];
            } else if (bytes.length == 2) {
                int highByte = 256 + bytes[0];
                int lowByte = 256 + bytes[1];
                ascii = (256 * highByte + lowByte) - 256 * 256;
            } else {
                throw new IllegalArgumentException("不是单个汉字");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ascii;
    }

}
