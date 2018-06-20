package com.jiangzg.base.application;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.content.ClipboardManager;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.telephony.TelephonyManager;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by JiangZhiGuo on 2016-12-2.
 * describe Application的基类
 */
public class AppBase extends MultiDexApplication {

    private static AppBase instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this); // 大项目需要分包
    }

    @Override
    public void onCreate() {
        instance = this;
        super.onCreate();
    }

    public static AppBase getInstance() {
        return instance;
    }

    //这个获取的是GBase=的res，注意不是app的
    //    public static Resources getResources() {
    //        return getInstance().getResources();
    //    }

    public static ActivityManager getActivityManager() {
        return (ActivityManager) getInstance().getSystemService(Context.ACTIVITY_SERVICE);
    }

    public static WifiManager getWifiManager() {
        return (WifiManager) getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static WindowManager getWindowManager() {
        return (WindowManager) getInstance().getSystemService(Context.WINDOW_SERVICE);
    }

    public static ConnectivityManager getConnectivityManager() {
        return (ConnectivityManager) getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public static LocationManager getLocationManager() {
        return (LocationManager) getInstance().getSystemService(Context.LOCATION_SERVICE);
    }

    public static TelephonyManager getTelephonyManager() {
        return (TelephonyManager) getInstance().getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static InputMethodManager getInputManager() {
        return (InputMethodManager) getInstance().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public static ClipboardManager getClipboardManager() {
        return (ClipboardManager) getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public static KeyguardManager getKeyguardManager() {
        return (KeyguardManager) getInstance().getSystemService(Context.KEYGUARD_SERVICE);
    }

    public static PowerManager getPowerManager() {
        return (PowerManager) getInstance().getSystemService(Context.POWER_SERVICE);
    }

    public static NotificationManager getNotificationManager() {
        return (NotificationManager) getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static AlarmManager getAlarmManager() {
        return (AlarmManager) getInstance().getSystemService(Context.ALARM_SERVICE);
    }

    public static DownloadManager getDownloadManager() {
        return (DownloadManager) getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
    }

}
