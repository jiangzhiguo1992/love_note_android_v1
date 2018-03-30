package com.jiangzg.base.common;

import android.text.TextUtils;
import android.util.Log;

import com.jiangzg.base.application.AppInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Jiang on 2016/10/08
 * 日志管理工具类 todo 对应级别上线打印开关 打印堆栈 打印线程 日志文件读写 全局异常补货 json打印
 */
public class LogUtils {
    private static String logDir; // SDCard/包名/log/
    private static String LOG_TAG_PREFIX = ">";

    public static void initApp() {
        LOG_TAG_PREFIX = "<" + AppInfo.get().getName() + ">";
    }

    // 调试 不会保存到文件，正式关掉
    public static void d(String tag, String print) {
        Log.d(LOG_TAG_PREFIX + tag, print);
    }

    // 正常打印 不会保存到文件，正式也开启
    public static void i(String tag, String print) {
        Log.i(LOG_TAG_PREFIX + tag, print);
    }

    // 非err错误 会保存到文件，正式也开启
    public static void w(String tag, String print) {
        Log.w(LOG_TAG_PREFIX + tag, print);
    }

    // err打印 会保存到文件，正式也开启
    public static void e(String tag, String print, Exception e) {
        //Log.e(LOG_TAG_PREFIX + tag, print);
        e.printStackTrace();
    }

    /**
     * 记录日志
     */
    public static void writeFile(String content) {
        if (TextUtils.isEmpty(content)) return;
        String logDir = getLogDir();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA);
        String format = simpleDateFormat.format(new Date());
        String logFileName = format + ".txt";
        File logFile = new File(logDir, logFileName);
        FileUtils.createFileByDeleteOldFile(logFile);
        FileUtils.writeFileFromString(logFile, content, true);
    }

    /**
     * 自定义Log路径
     */
    public static String getLogDir() {
        //logDir = AppInfo.getInstance().getResDir() + "log" + File.separator;
        logDir = "";
        FileUtils.createOrExistsDir(logDir); // 并创建
        return logDir;
    }

}
