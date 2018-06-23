package com.jiangzg.base.system;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.common.LogUtils;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by jiang on 2016/10/12
 * 网络工具类
 */
public class NetInfo {

    private static NetInfo instance;
    private ConnectListener mListener;
    private NetReceiver receiver;

    public static NetInfo get() {
        if (instance == null) {
            synchronized (NetInfo.class) {
                if (instance == null) {
                    instance = new NetInfo();
                }
            }
        }
        return instance;
    }

    /**
     * 注册网络监听,listener监听回调（网络状态变化）
     */
    public void addListener(Context context, ConnectListener listener) {
        if (context == null || listener == null) {
            LogUtils.w(NetInfo.class, "addListener", "context == null || listener == null");
            return;
        }
        mListener = listener;
        context.registerReceiver(getReceiver(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * 注销网络监听
     */
    public void removeListener(Context context) {
        if (context == null) {
            LogUtils.w(NetInfo.class, "removeListener", "context == null");
            return;
        }
        mListener = null;
        context.unregisterReceiver(getReceiver());
    }

    private NetReceiver getReceiver() {
        if (receiver == null) {
            receiver = new NetReceiver();
        }
        return receiver;
    }

    /**
     * 广播,监听网络变化
     */
    private class NetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mListener == null) return;
            int type = getNetworkType();
            NetworkInfo.State state = getNetworkState();
            String operator = getNetworkOperator();
            LogUtils.i(NetInfo.class, "onReceive", type + ":" + state + ":" + operator);
            mListener.onStateChange(type, state, operator);
        }
    }

    /**
     * 网络状态监听器
     */
    public interface ConnectListener {
        void onStateChange(int type, NetworkInfo.State state, String operator);
    }

    /**
     * 网络是否可用
     */
    public static boolean isAvailable() {
        NetworkInfo networkInfo = getNetworkInfo();
        return (networkInfo != null && getNetworkInfo().isAvailable());
    }

    /**
     * 判断wifi是否连接状态
     */
    public static boolean isWifi() {
        ConnectivityManager cm = AppBase.getConnectivityManager();
        return cm != null && cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;
    }

    private static NetworkInfo getNetworkInfo() {
        return AppBase.getConnectivityManager().getActiveNetworkInfo();
    }

    /**
     * 获取当前网络类型
     *
     * @return {@link ConnectivityManager#TYPE_MOBILE}
     */
    public static int getNetworkType() {
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo == null) {
            LogUtils.w(NetInfo.class, "getNetworkType", "networkInfo == null");
            return -1;
        }
        return networkInfo.getType();
    }

    /**
     * 获取当前网络状态
     *
     * @return {@link NetworkInfo.State State}
     */
    public static NetworkInfo.State getNetworkState() {
        NetworkInfo networkInfo = getNetworkInfo();
        if (networkInfo == null) {
            LogUtils.w(NetInfo.class, "getNetworkState", "networkInfo == null");
            return NetworkInfo.State.UNKNOWN;
        }
        return networkInfo.getState();
    }

    /**
     * 获取移动网络运营商名称
     *
     * @return 如中国联通、中国移动、中国电信
     */
    public static String getNetworkOperator() {
        TelephonyManager tm = AppBase.getTelephonyManager();
        return tm != null ? tm.getNetworkOperatorName() : null;
    }

    /**
     * 获取IP地址 eg:127.168.x.x
     */
    public static String getIpAddress() {
        String ipAddress = "";
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    InetAddress ia = ias.nextElement();
                    if (ia instanceof Inet6Address) continue; // skip ipv6
                    String ip = ia.getHostAddress();
                    String host = "127.0.0.1";
                    if (!host.equals(ip)) {
                        ipAddress = ip;
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            LogUtils.e(NetInfo.class, "getIpAddress", e);
        }
        return ipAddress;
    }

}
