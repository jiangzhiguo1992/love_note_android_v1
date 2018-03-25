package com.jiangzg.ita.third;

import android.text.TextUtils;
import android.util.Log;

import com.jiangzg.base.file.FileUtils;
import com.jiangzg.base.time.DateUtils;

import java.io.File;

/**
 * Created by Jiang on 2016/10/08
 * <p/>
 * 日志管理工具类
 */
public class LogUtils {
    private static String logDir; // SDCard/包名/log/

    public static void initApp() {
        //String logTag = AppContext.get().getString(R.string.app_name);
        //Logger.init(logTag) // 打印tag
        //        .methodCount(0)// 3以上才能显示调用方法
        //        .hideThreadInfo() // 隐藏线程显示
        //        .logLevel(LogLevel.FULL);// 打印开关
    }

    /**
     * 调试
     */
    public static void d(String print) {
        Log.d("", print);
        //Logger.d(print);
    }

    public static void d(String tag, String print) {
        Log.d("", print);
        //Logger.d(tag, print);
    }

    public static void d(String print, int methodCount) {
        Log.d("", print);
        //Logger.d(print, methodCount);
    }

    /**
     * 警告
     */
    public static void e(String print) {
        Log.e("", print);
        //Logger.e(print);
    }

    public static void e(String tag, String print) {
        Log.e("", print);
        //Logger.e(tag, print);
    }

    public static void e(String print, int methodCount) {
        Log.e("", print);
        //Logger.e(print, methodCount);
    }

    /**
     * 实体类
     */
    public static void json(String json) {
        Log.e("", json);
        //Logger.json(json);
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
