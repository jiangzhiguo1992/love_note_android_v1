package com.android.base.function;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.base.component.application.AppContext;
import com.android.base.component.intent.IntentCons;

/**
 * Created by gg on 2017/4/3.
 * 电量工具类
 */
public class BatteryInfo {

    private static final String LOG_TAG = "BatteryInfo";

    private static BatteryInfo instance;
    private BatteryListener mListener;

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
    public void addListener(@NonNull Context context, @NonNull BatteryListener listener) {
        mListener = listener;
        Log.d(LOG_TAG, "addListener");
        IntentFilter intentFilter = new IntentFilter(IntentCons.action_battery);
        context.registerReceiver(batteryReceiver, intentFilter);
    }

    /**
     * 注销
     */
    public void removeListener(@NonNull Context context) {
        mListener = null;
        Log.d(LOG_TAG, "removeListener");
        context.unregisterReceiver(batteryReceiver);
    }

    /**
     * 广播,监听电量变化
     */
    private BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mListener == null) return;
            int voltage = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            switch (voltage) {
                case BatteryManager.BATTERY_STATUS_CHARGING:
                    Log.d(LOG_TAG, "onReceive-->.up");
                    mListener.up();
                    break;
                case BatteryManager.BATTERY_STATUS_DISCHARGING:
                    Log.d(LOG_TAG, "onReceive-->.down");
                    mListener.down();
                    break;
                case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                    Log.d(LOG_TAG, "onReceive-->.normal");
                    mListener.normal();
                    break;
                case BatteryManager.BATTERY_STATUS_FULL:
                    Log.d(LOG_TAG, "onReceive-->.full");
                    mListener.full();
                    break;
            }
            int percent = getPercent();
            if (percent > 0) {
                Log.d(LOG_TAG, "onReceive-->.percent:" + percent);
                mListener.percent(percent);
            }
        }
    };

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

    /**
     * 主动-->获取电量百分比, BatteryInfo.FALSE 为失败
     */
    public static int getPercent() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent intent = AppContext.get().registerReceiver(null, filter);
        if (intent == null) return 0;
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
        if (level > 0 && scale > 0) {
            int percent = (level * 100) / scale;
            Log.d(LOG_TAG, "mListener.percent:" + percent);
            return percent;
        }
        return 0;
    }

}
