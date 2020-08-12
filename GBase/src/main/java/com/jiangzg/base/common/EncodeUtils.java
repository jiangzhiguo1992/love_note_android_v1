package com.jiangzg.base.common;

import android.text.Html;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by jiang on 2016/10/12
 * <p>
 * describe  编码解码相关工具类
 */
public class EncodeUtils {

    /**
     * URL编码
     * <p>若想自己指定字符集,可以使用{@link #urlEncode(String input, String charset)}方法</p>
     *
     * @param input 要编码的字符
     * @return 编码为UTF-8的字符串
     */
    public static String urlEncode(String input) {
        return urlEncode(input, "UTF-8");
    }

    /**
     * URL编码
     * <p>若系统不支持指定的编码字符集,则直接将input原样返回</p>
     *
     * @param input   要编码的字符
     * @param charset 字符集
     * @return 编码为字符集的字符串
     */
    public static String urlEncode(String input, String charset) {
        if (StringUtils.isEmpty(input)) {
            LogUtils.w(EncodeUtils.class, "urlEncode", "input == null");
            return "";
        }
        try {
            return URLEncoder.encode(input, charset);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(EncodeUtils.class, "urlEncode", e);
        }
        return input;
    }

    /**
     * URL解码
     * <p>若想自己指定字符集,可以使用 {@link #urlDecode(String input, String charset)}方法</p>
     *
     * @param input 要解码的字符串
     * @return URL解码后的字符串
     */
    public static String urlDecode(String input) {
        return urlDecode(input, "UTF-8");
    }

    /**
     * URL解码
     * <p>若系统不支持指定的解码字符集,则直接将input原样返回</p>
     *
     * @param input   要解码的字符串
     * @param charset 字符集
     * @return URL解码为指定字符集的字符串
     */
    public static String urlDecode(String input, String charset) {
        if (StringUtils.isEmpty(input)) {
            LogUtils.w(EncodeUtils.class, "urlDecode", "input == null");
            return "";
        }
        try {
            return URLDecoder.decode(input, charset);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(EncodeUtils.class, "urlDecode", e);
        }
        return input;
    }

    /**
     * Base64编码
     *
     * @param input 要编码的字符串
     * @return Base64编码后的字符串
     */
    public static byte[] base64Encode(String input) {
        return base64Encode(input.getBytes());
    }

    /**
     * Base64编码
     *
     * @param input 要编码的字节数组
     * @return Base64编码后的字符串
     */
    public static byte[] base64Encode(byte[] input) {
        if (input == null || input.length <= 0) {
            LogUtils.w(EncodeUtils.class, "base64Encode", "input == null");
            return new byte[]{};
        }
        return Base64.encode(input, Base64.NO_WRAP);
    }

    /**
     * Base64编码
     *
     * @param input 要编码的字节数组
     * @return Base64编码后的字符串
     */
    public static String base64Encode2String(byte[] input) {
        if (input == null || input.length <= 0) {
            LogUtils.w(EncodeUtils.class, "base64Encode2String", "input == null");
            return "";
        }
        return Base64.encodeToString(input, Base64.NO_WRAP);
    }

    /**
     * Base64解码
     *
     * @param input 要解码的字符串
     * @return Base64解码后的字符串
     */
    public static byte[] base64Decode(String input) {
        if (StringUtils.isEmpty(input)) {
            LogUtils.w(EncodeUtils.class, "base64Decode", "input == null");
            return new byte[]{};
        }
        return Base64.decode(input, Base64.NO_WRAP);
    }

    /**
     * Base64解码
     *
     * @param input 要解码的字符串
     * @return Base64解码后的字符串
     */
    public static byte[] base64Decode(byte[] input) {
        if (input == null || input.length <= 0) {
            LogUtils.w(EncodeUtils.class, "base64Decode", "input == null");
            return new byte[]{};
        }
        return Base64.decode(input, Base64.NO_WRAP);
    }

    /**
     * Base64URL安全编码
     * <p>将Base64中的URL非法字符�?,/=转为其他字符, 见RFC3548</p>
     *
     * @param input 要Base64URL安全编码的字符串
     * @return Base64URL安全编码后的字符串
     */
    public static byte[] base64UrlSafeEncode(String input) {
        if (StringUtils.isEmpty(input)) {
            LogUtils.w(EncodeUtils.class, "base64UrlSafeEncode", "input == null");
            return new byte[]{};
        }
        return Base64.encode(input.getBytes(), Base64.URL_SAFE);
    }

    /**
     * Html编码
     *
     * @param input 要Html编码的字符串
     * @return Html编码后的字符串
     */
    public static String htmlEncode(String input) {
        if (StringUtils.isEmpty(input)) {
            LogUtils.w(EncodeUtils.class, "htmlEncode", "input == null");
            return "";
        }
        return Html.escapeHtml(input);
    }

    /**
     * Html解码
     *
     * @param input 待解码的字符串
     * @return Html解码后的字符串
     */
    public static String htmlDecode(String input) {
        if (StringUtils.isEmpty(input)) {
            LogUtils.w(EncodeUtils.class, "htmlDecode", "input == null");
            return "";
        }
        return Html.fromHtml(input).toString();
    }

}
