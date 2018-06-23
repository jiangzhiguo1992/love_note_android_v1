package com.jiangzg.base.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;

/**
 * Created by gg on 2017/4/3.
 * 电量工具类
 */
public class BatteryInfo {

    private static BatteryInfo instance;
    private BatteryListener mListener;
    private BatteryReceiver batteryReceiver;

    /**
     * 主动-->获取电量百分比, 0 为失败
     */
    public static int getPercent() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = AppBase.getInstance().registerReceiver(null, filter);
        if (intent == null) return 0;
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        if (level > 0 && scale > 0) {
            return (level * 100) / scale;
        }
        return 0;
    }

    public static BatteryInfo get() {
        if (instance == null) {
            synchronized (BatteryInfo.class) {
                if (instance == null) {
                    instance = new BatteryInfo();
                }
            }
        }
        return instance;
    }

    /**
     * 注册 设置电量广播监听器
     */
    public void addListener(Context context, BatteryListener listener) {
        if (context == null || listener == null) {
            LogUtils.w(BatteryInfo.class, "addListener", "context == null || listener == null");
            return;
        }
        mListener = listener;
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(getBatteryReceiver(), intentFilter);
    }

    /**
     * 注销
     */
    public void removeListener(Context context) {
        if (context == null) {
            LogUtils.w(BatteryInfo.class, "removeListener", "context == null");
            return;
        }
        mListener = null;
        context.unregisterReceiver(getBatteryReceiver());
    }

    private BatteryReceiver getBatteryReceiver() {
        if (batteryReceiver == null) {
            batteryReceiver = new BatteryReceiver();
        }
        return batteryReceiver;
    }

    /**
     * 广播,监听电量变化
     */
    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mListener == null) return;
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            switch (voltage) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    LogUtils.d(BatteryInfo.class, "onReceive", "up");
                    mListener.up();
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    LogUtils.d(BatteryInfo.class, "onReceive", "down");
                    mListener.down();
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    LogUtils.d(BatteryInfo.class, "onReceive", "noChange");
                    mListener.normal();
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    LogUtils.d(BatteryInfo.class, "onReceive", "full");
                    mListener.full();
                    break;
            }
            int percent = getPercent();
            if (percent > 0) {
                LogUtils.d(BatteryInfo.class, "onReceive", "percent = " + percent);
                mListener.percent(percent);
            }
        }
    }

    /**
     * 监听器
     */
    public interface BatteryListener {

        void percent(int percent);

        void normal();

        void up();

        void down();

        void full();
    }

}
