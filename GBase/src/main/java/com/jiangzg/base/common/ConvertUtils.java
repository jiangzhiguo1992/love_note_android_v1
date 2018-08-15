package com.jiangzg.base.common;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.jiangzg.base.application.AppBase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

/**
 * Created by JiangZhiGuo on 2016/10/12.
 * describe 转换工具类
 */
public class ConvertUtils {

    private static final char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * byteArr转hexString
     * <p>例如：</p>
     * bytes2HexString(new byte[] { 0, (byte) 0xa8 }) returns 00A8
     *
     * @param bytes byte数组
     * @return 16进制大写字符串
     */
    public static String bytes2HexString(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            LogUtils.w(ConvertUtils.class, "", "bytes2HexString: bytes == null || bytes.length <= 0");
            return "";
        }
        int len = bytes.length;
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >>> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    /**
     * hexString转byteArr
     * <p>例如：</p>
     * hexString2Bytes("00A8") returns { 0, (byte) 0xA8 }
     *
     * @param hexString 十六进制字符串
     * @return 字节数组
     */
    public static byte[] hexString2Bytes(String hexString) {
        if (StringUtils.isEmpty(hexString)) {
            LogUtils.w(ConvertUtils.class, "hexString2Bytes", "hexString == null");
            return new byte[]{};
        }
        int len = hexString.length();
        if (len % 2 != 0) {
            LogUtils.e(ConvertUtils.class, "hexString2Bytes", new IllegalArgumentException("长度不是偶数"));
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret = new byte[len >>> 1];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) (hex2Dec(hexBytes[i]) << 4 | hex2Dec(hexBytes[i + 1]));
        }
        return ret;
    }

    /**
     * hexChar转int
     *
     * @param hexChar hex单个字节
     * @return 0..15
     */
    private static int hex2Dec(char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        } else if (hexChar >= 'A' && hexChar <= 'F') {
            return hexChar - 'A' + 10;
        } else {
            return 0;
        }
    }

    /**
     * charArr转byteArr
     *
     * @param chars 字符数组
     * @return 字节数组
     */
    public static byte[] chars2Bytes(char[] chars) {
        if (chars == null || chars.length <= 0) {
            LogUtils.w(ConvertUtils.class, "chars2Bytes", "chars == null");
            return new byte[]{};
        }
        int len = chars.length;
        byte[] bytes = new byte[len];
        for (int i = 0; i < len; i++) {
            bytes[i] = (byte) (chars[i]);
        }
        return bytes;
    }

    /**
     * byteArr转charArr
     *
     * @param bytes 字节数组
     * @return 字符数组
     */
    public static char[] bytes2Chars(byte[] bytes) {
        if (bytes == null || bytes.length <= 0) {
            LogUtils.w(ConvertUtils.class, "bytes2Chars", "bytes == null");
            return new char[]{};
        }
        int len = bytes.length;
        char[] chars = new char[len];
        for (int i = 0; i < len; i++) {
            chars[i] = (char) (bytes[i] & 0xff);
        }
        return chars;
    }

    /**
     * 字节数转以unit为单位的size
     */
    public static double byte2Size(long byteNum, ConstantUtils.MemoryUnit unit) {
        switch (unit) {
            default:
            case BYTE:
                return (double) byteNum / ConstantUtils.BYTE;
            case KB:
                return (double) byteNum / ConstantUtils.KB;
            case MB:
                return (double) byteNum / ConstantUtils.MB;
            case GB:
                return (double) byteNum / ConstantUtils.GB;
        }
    }

    /**
     * 以unit为单位的size转字节数 也可以Formatter.formatFileSize
     *
     * @param size 大小
     */
    public static long size2Byte(long size, ConstantUtils.MemoryUnit unit) {
        switch (unit) {
            default:
            case BYTE:
                return size * ConstantUtils.BYTE;
            case KB:
                return size * ConstantUtils.KB;
            case MB:
                return size * ConstantUtils.MB;
            case GB:
                return size * ConstantUtils.GB;
        }
    }

    /**
     * 字节数转合适大小 也可以Formatter.formatFileSize
     * <p>保留3位小数</p>
     *
     * @param byteNum 字节数
     * @return 1...1024 unit
     */
    public static String byte2FitSize(long byteNum) {
        if (byteNum < 0) {
            return "";
        } else if (byteNum < ConstantUtils.KB) {
            return String.format(Locale.getDefault(), "%.1fB", (double) byteNum);
        } else if (byteNum < ConstantUtils.MB) {
            return String.format(Locale.getDefault(), "%.1fKB", (double) byteNum / ConstantUtils.KB);
        } else if (byteNum < ConstantUtils.GB) {
            return String.format(Locale.getDefault(), "%.1fMB", (double) byteNum / ConstantUtils.MB);
        } else {
            return String.format(Locale.getDefault(), "%.1fGB", (double) byteNum / ConstantUtils.GB);
        }
    }

    /**
     * inputStream转outputStream
     *
     * @param is 输入流
     * @return outputStream子类
     */
    public static ByteArrayOutputStream input2OutputStream(InputStream is) {
        if (is == null) {
            LogUtils.w(ConvertUtils.class, "input2OutputStream", "is == null");
            return null;
        }
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            byte[] b = new byte[ConstantUtils.KB];
            int len;
            while ((len = is.read(b, 0, ConstantUtils.KB)) != -1) {
                os.write(b, 0, len);
            }
            return os;
        } catch (IOException e) {
            LogUtils.e(ConvertUtils.class, "input2OutputStream", e);
        } finally {
            FileUtils.closeIO(is);
        }
        return null;
    }

    /**
     * outputStream转inputStream
     *
     * @param out 输出流
     * @return inputStream子类
     */
    public ByteArrayInputStream output2InputStream(OutputStream out) {
        if (out == null) {
            LogUtils.w(ConvertUtils.class, "output2InputStream", "out == null");
            return null;
        }
        return new ByteArrayInputStream(((ByteArrayOutputStream) out).toByteArray());
    }

    /**
     * inputStream转byteArr
     *
     * @param is 输入流
     * @return 字节数组
     */
    public static byte[] inputStream2Bytes(InputStream is) {
        if (is == null) {
            LogUtils.w(ConvertUtils.class, "inputStream2Bytes", "is == null");
            return new byte[]{};
        }
        ByteArrayOutputStream stream = input2OutputStream(is);
        if (stream == null) {
            return new byte[]{};
        }
        return stream.toByteArray();
    }

    /**
     * byteArr转inputStream
     *
     * @param bytes 字节数组
     * @return 输入流
     */
    public static InputStream bytes2InputStream(byte[] bytes) {
        if (bytes == null) {
            LogUtils.w(ConvertUtils.class, "bytes2InputStream", "bytes == null");
            return null;
        }
        return new ByteArrayInputStream(bytes);
    }

    /**
     * outputStream转byteArr
     *
     * @param out 输出流
     * @return 字节数组
     */
    public static byte[] outputStream2Bytes(OutputStream out) {
        if (out == null) {
            LogUtils.w(ConvertUtils.class, "outputStream2Bytes", "out == null");
            return new byte[]{};
        }
        return ((ByteArrayOutputStream) out).toByteArray();
    }

    /**
     * outputStream转byteArr
     *
     * @param bytes 字节数组
     * @return 字节数组
     */
    public static OutputStream bytes2OutputStream(byte[] bytes) {
        if (bytes == null) {
            LogUtils.w(ConvertUtils.class, "bytes2OutputStream", "bytes == null");
            return null;
        }
        ByteArrayOutputStream os = null;
        try {
            os = new ByteArrayOutputStream();
            os.write(bytes);
            return os;
        } catch (IOException e) {
            LogUtils.e(ConvertUtils.class, "bytes2OutputStream", e);
        } finally {
            FileUtils.closeIO(os);
        }
        return null;
    }

    /**
     * inputStream转string按编码
     *
     * @param is          输入流
     * @param charsetName 编码格式
     * @return 字符串
     */
    public static String inputStream2String(InputStream is, String charsetName) {
        if (is == null || TextUtils.isEmpty(charsetName)) {
            LogUtils.w(ConvertUtils.class, "inputStream2String", "bytes == null");
            return "";
        }
        try {
            return new String(inputStream2Bytes(is), charsetName);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(ConvertUtils.class, "inputStream2String", e);
            return "";
        }
    }

    /**
     * string转inputStream按编码
     *
     * @param string      字符串
     * @param charsetName 编码格式
     * @return 输入流
     */
    public static InputStream string2InputStream(String string, String charsetName) {
        if (TextUtils.isEmpty(charsetName)) {
            LogUtils.w(ConvertUtils.class, "string2InputStream", "charsetName == null");
            return null;
        }
        try {
            return new ByteArrayInputStream(string.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(ConvertUtils.class, "string2InputStream", e);
            return null;
        }
    }

    /**
     * outputStream转string按编码
     *
     * @param out         输出流
     * @param charsetName 编码格式
     * @return 字符串
     */
    public static String outputStream2String(OutputStream out, String charsetName) {
        if (out == null) {
            LogUtils.w(ConvertUtils.class, "outputStream2String", "out == null");
            return "";
        }
        try {
            return new String(outputStream2Bytes(out), charsetName);
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(ConvertUtils.class, "outputStream2String", e);
            return "";
        }
    }

    /**
     * string转outputStream按编码
     *
     * @param string      字符串
     * @param charsetName 编码格式
     * @return 输入流
     */
    public static OutputStream string2OutputStream(String string, String charsetName) {
        if (StringUtils.isEmpty(string)) {
            LogUtils.w(ConvertUtils.class, "string2OutputStream", "string == null");
            return null;
        }
        try {
            return bytes2OutputStream(string.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            LogUtils.e(ConvertUtils.class, "string2OutputStream", e);
            return null;
        }
    }

    /**
     * bitmap转byteArr
     *
     * @param bitmap bitmap对象
     * @param format 格式
     * @return 字节数组
     */
    public static byte[] bitmap2Bytes(Bitmap bitmap, Bitmap.CompressFormat format) {
        if (bitmap == null) {
            LogUtils.w(ConvertUtils.class, "bitmap2Bytes", "bitmap == null");
            return new byte[]{};
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        return baos.toByteArray();
    }

    /**
     * byteArr转bitmap
     *
     * @param bytes 字节数组
     * @return bitmap对象
     */
    public static Bitmap bytes2Bitmap(byte[] bytes) {
        return (bytes == null || bytes.length == 0) ? null : BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * drawable转bitmap
     *
     * @param drawable drawable对象
     * @return bitmap对象
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable == null) {
            LogUtils.w(ConvertUtils.class, "drawable2Bitmap", "drawable == null");
            return null;
        }
        Bitmap bitmap = Bitmap.createBitmap(
                drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * bitmap转drawable
     *
     * @param res    resources对象
     * @param bitmap bitmap对象
     * @return drawable对象
     */
    public static Drawable bitmap2Drawable(Resources res, Bitmap bitmap) {
        return bitmap == null ? null : new BitmapDrawable(res, bitmap);
    }

    /**
     * drawable转byteArr
     *
     * @param drawable drawable对象
     * @param format   格式
     * @return 字节数组
     */
    public static byte[] drawable2Bytes(Drawable drawable, Bitmap.CompressFormat format) {
        return bitmap2Bytes(drawable2Bitmap(drawable), format);
    }

    /**
     * byteArr转drawable
     *
     * @param res   resources对象
     * @param bytes 字节数组
     * @return drawable对象
     */
    public static Drawable bytes2Drawable(Resources res, byte[] bytes) {
        return bitmap2Drawable(res, bytes2Bitmap(bytes));
    }

    /**
     * 将value转化成px(没算四舍五入)
     *
     * @param type eg: TypedValue.COMPLEX_UNIT_DIP
     */
    public static int getpx(int type, float value) {
        DisplayMetrics metrics = AppBase.getInstance().getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(type, value, metrics);
    }

    /**
     * dp转px
     *
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(float dpValue) {
        final float scale = AppBase.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     *
     * @param pxValue px值
     * @return dp值
     */
    public static int px2dp(float pxValue) {
        final float scale = AppBase.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param spValue sp值
     * @return px值
     */
    public static int sp2px(float spValue) {
        final float fontScale = AppBase.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * px转sp
     *
     * @param pxValue px值
     * @return sp值
     */
    public static int px2sp(float pxValue) {
        final float fontScale = AppBase.getInstance().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

}
