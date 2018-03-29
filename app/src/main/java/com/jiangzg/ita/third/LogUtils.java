package com.jiangzg.ita.third;

import android.text.TextUtils;
import android.util.Log;

import com.jiangzg.base.component.application.AppInfo;
import com.jiangzg.base.file.FileUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.ita.base.MyApp;

import java.io.File;

/**
 * Created by Jiang on 2016/10/08
 * <p/>
 * 日志管理工具类 todo 应该转移到GBase中
 */
public class LogUtils {
    private static String logDir; // SDCard/包名/log/
    private static String LOG_TAG_PREFIX = ">";

    public static void initApp() {
        LOG_TAG_PREFIX = AppInfo.get().getName() + ">";

        //String logTag = AppContext.get().getString(R.string.app_name);
        //Logger.init(logTag) // 打印tag
        //        .methodCount(0)// 3以上才能显示调用方法
        //        .hideThreadInfo() // 隐藏线程显示
        //        .logLevel(LogLevel.FULL);// 打印开关
    }

    public static void d(String tag, String print) {
        Log.d(LOG_TAG_PREFIX + tag, print);
        //Logger.d(tag, print);
    }

    public static void i(String tag, String print) {
        Log.i(LOG_TAG_PREFIX + tag, print);
    }

    public static void w(String tag, String print) {
        Log.w(LOG_TAG_PREFIX + tag, print);
    }

    public static void e(String tag, String print) {
        Log.e(LOG_TAG_PREFIX + tag, print);
    }

    /**
     * 记录日志
     */
    public static void writeLog2File(String content) {
        if (TextUtils.isEmpty(content)) return;
        String logDir = getLogDir();
        String logFileName = DateUtils.genBillTime() + ".txt";
        File logFile = new File(logDir, logFileName);
        FileUtils.createFileByDeleteOldFile(logFile);
        FileUtils.writeFileFromString(logFile, content, true);
    }

    /**
     * 自定义Log路径
     */
    public static String getLogDir() {
        //logDir = AppInfo.get().getResDir() + "log" + File.separator;
        logDir = "";
        FileUtils.createOrExistsDir(logDir); // 并创建
        return logDir;
    }

}
