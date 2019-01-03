package com.jiangzg.base.common;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.application.AppUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.time.DateUtils;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Jiang on 2016/10/08
 * 日志管理工具类
 */
public class LogUtils {

    private static String logTagPrefix = "<>"; // 用来控制打印前缀
    private static boolean open; // 控制非异常级别的打印
    private static boolean warnFile, errFile; // 控制非异常级别的打印

    public static void initApp(boolean debug, boolean warnWrite, boolean errWrite) {
        logTagPrefix = "<" + AppInfo.get().getName() + ">";
        open = debug;
        warnFile = warnWrite;
        errFile = errWrite;
        // 全局异常捕获机制
        Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                // 记得先打印，要不系统拦截了异常之后，程序会退出
                LogUtils.e(LogUtils.class, "uncaughtException", e);
                // 获取系统默认的异常处理器
                Thread.UncaughtExceptionHandler defHandler = Thread.getDefaultUncaughtExceptionHandler();
                if (e != null && defHandler != null) {
                    // 如果用户没有处理，则让系统来处理，一般是弹对话框并退出
                    defHandler.uncaughtException(t, e);
                } else {
                    // 退出顶层activity
                    Stack<Activity> stack = ActivityStack.getStack();
                    if (stack == null || stack.size() <= 1) {
                        AppUtils.appExit();
                    } else {
                        Activity top = ActivityStack.getTop();
                        if (!ActivityStack.isActivityFinish(top)) {
                            top.finish();
                        }
                    }
                }
            }
        };
        Thread.setDefaultUncaughtExceptionHandler(handler);
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

    // 调试 不会保存到文件
    public static void d(Class cls, String method, String print) {
        if (!open) return;
        String tag = logTagPrefix + "-" + getLogTag(cls) + "-(" + method + ") ";
        Log.d(tag, print);
    }

    // 正常打印 不会保存到文件
    public static void i(Class cls, String method, String print) {
        if (!open) return;
        String tag = logTagPrefix + "-" + getLogTag(cls) + "-(" + method + ") ";
        Log.i(tag, print);
    }

    // 非err错误 会保存到文件
    public static void w(Class cls, String method, String print) {
        String tag = logTagPrefix + "-" + getLogTag(cls) + "-(" + method + ") ";
        String write = "\n" + tag + ": " + print;
        if (warnFile) writeLogFile("warn", write);
        if (!open) return;
        Log.w(tag, print);
    }

    // err打印 会保存到文件
    public static void e(Class cls, String method, Throwable t) {
        if (t == null) return;
        String tag = logTagPrefix + "-" + getLogTag(cls) + "-(" + method + ") ";
        String print = Log.getStackTraceString(t);
        String write = "\n" + tag + ": " + print;
        if (errFile) writeLogFile("err", write);
        if (!open) return;
        Log.e(tag, print);
    }

    // Log目录
    public static File getLogDir() {
        File logDir = new File(AppInfo.get().getOutFilesDir(), "log");
        FileUtils.createOrExistsDir(logDir); // 并创建
        return logDir;
    }

    // 日志写入
    private static void writeLogFile(String suffix, final String content) {
        if (TextUtils.isEmpty(content)) return;
        String dateTime = DateUtils.getCurrentStr(DateUtils.FORMAT_LINE_M_D); // 必须是天，不能是时分
        String logFileName = dateTime + "_" + suffix + ".txt";
        final File logFile = new File(getLogDir(), logFileName);
        if (logFile.length() >= FileUtils.KB * 100) {
            // 超过100KB就不要再写了
            return;
        }
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
