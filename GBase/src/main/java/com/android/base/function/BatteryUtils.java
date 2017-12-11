package com.android.base.function;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.android.base.component.application.AppContext;
import com.android.base.component.intent.IntentCons;

/**
 * Created by gg on 2017/4/3.
 * 电量工具类
 */
public class BatteryUtils {

    public static final int ERROR = -1;

    private static BatteryUtils instance;
    private BatteryListener mListener;

    public static BatteryUtils get() {
        if (instance == null) {
            synchronized (BatteryUtils.class) {
                if (instance == null) {
                    instance = new BatteryUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 注册 设置电量广播监听器
     */
    public void registerReceiver(Context context, BatteryListener listener) {
        mListener = listener;
        IntentFilter intentFilter = new IntentFilter(IntentCons.action_battery);
        context.registerReceiver(batteryReceiver, intentFilter);
    }

    /**
     * 注销
     */
    public void unregisterReceiver(Context context) {
        context.unregisterReceiver(batteryReceiver);
    }

    /**
     * 广播,监听电量变化
     */
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mListener == null) return;
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_STATUS, ERROR);
            switch (voltage) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    mListener.up();
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    mListener.down();
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    mListener.full();
                    break;
            }
            int percent = getPercent();
            if (percent == ERROR) return;
            mListener.percent(percent);
        }
    };

    /**
     * 监听器
     */
    public interface BatteryListener {

        void percent(int percent);

        void up();

        void down();

        void full();
    }

    /**
     * 主动-->获取电量百分比, BatteryUtils.FALSE 为失败
     */
    public static int getPercent() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = AppContext.get().registerReceiver(null, filter);
        if (intent == null) return ERROR;
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, ERROR);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, ERROR);
        if (level == ERROR || scale == ERROR) {
            return ERROR;
        } else {
            return (level * 100) / scale;
        }
    }

}
