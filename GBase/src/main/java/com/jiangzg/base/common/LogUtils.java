package com.jiangzg.base.common;

import android.text.TextUtils;
import android.util.Log;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.time.DateUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by Jiang on 2016/10/08
 * 日志管理工具类
 */
public class LogUtils {

    private static String logTagPrefix = "<>"; // 用来控制打印前缀
    private static boolean open; // 控制非异常级别的打印

    public static void initApp(boolean debug) {
        logTagPrefix = "<" + AppInfo.get().getName() + ">";
        open = debug;
    }

    // LogTag
    private static <T> String getLogTag(Class<T> cls) {
        if (cls == null) return "[]";
        return "[" + cls.getSimpleName() + "]";
    }

    // 线程信息
    private static String getThreadLog() {
        int activeCount = Thread.activeCount();
        Thread thread = Thread.currentThread();
        long id = thread.getId();
        String name = thread.getName();
        int priority = thread.getPriority();
        String stateName = thread.getState().name();
        return "活跃线程数:" + activeCount + " 当前线程:" + id + "-名称:" + name + "-优先:" + priority + "-状态:" + stateName;
    }

    public static <T> String getArrayLog(T[] arr) {
        return Arrays.toString(arr);
    }

    // list打印
    public static <T> String getListLog(List<T> list) {
        if (list == null || list.size() <= 0) {
            return "list == null || list.size() <= 0";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            builder.append("\n").append(i).append(":");
            T o = list.get(i);
            if (o == null) {
                builder.append("null");
                continue;
            }
            String str = o.toString();
            builder.append(str);
        }
        return builder.toString();
    }

    // map打印
    public static <K, V> String getMapLog(Map<K, V> map) {
        if (map == null || map.size() <= 0) {
            return "map == null || map.size() <= 0";
        }
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<K, V> entry : map.entrySet()) {
            builder.append("\n").append(entry.getKey()).append(":").append(entry.getValue());
        }
        return builder.toString();
    }

    // 调试 不会保存到文件，正式关掉
    public static void d(Class cls, String method, String print) {
        if (!open) return;
        String tag = logTagPrefix + "-" + getLogTag(cls) + "-(" + method + ") ";
        Log.d(tag, print);
    }

    // 正常打印 不会保存到文件，正式也开启
    public static void i(Class cls, String method, String print) {
        String tag = logTagPrefix + "-" + getLogTag(cls) + "-(" + method + ") ";
        Log.i(tag, print);
    }

    // 非err错误 会保存到文件，正式也开启
    public static void w(Class cls, String method, String print) {
        String tag = logTagPrefix + "-" + getLogTag(cls) + "-(" + method + ") ";
        Log.w(tag, print);
        String write = "\n" + tag + ": " + print;
        writeLogFile("warn", write);
    }

    // err打印 会保存到文件，正式也开启
    public static void e(Class cls, String method, Throwable t) {
        if (t == null) return;
        String tag = logTagPrefix + "-" + getLogTag(cls) + "-(" + method + ") ";
        String print = Log.getStackTraceString(t);
        Log.e(tag, print);
        String write = "\n" + tag + ": " + print;
        writeLogFile("err", write);
    }

    // TODO 全局异常捕获
    public static void cache() {

    }

    // Log目录
    public static File getLogDir() {
        File logDir = new File(AppInfo.get().getOutFilesDir(), "log");
        FileUtils.createOrExistsDir(logDir); // 并创建
        return logDir;
    }

    // 日志写入
    private static void writeLogFile(String prefix, final String content) {
        if (TextUtils.isEmpty(content)) return;
        String dateTime = DateUtils.getCurrentString(ConstantUtils.FORMAT_LINE_Y_M_D);
        String logFileName = prefix + "_" + dateTime + ".txt";
        final File logFile = new File(getLogDir(), logFileName);
        // 开线程
        AppBase.getInstance().getThread().execute(new Runnable() {
            @Override
            public void run() {
                // 已存在文件的话 就追加写进去
                FileUtils.createOrExistsFile(logFile);
                FileUtils.writeFileFromString(logFile, content, true);
            }
        });
    }

}
