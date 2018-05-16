package com.jiangzg.base.view;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.jiangzg.base.application.AppBase;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.application.AppUtils;
import com.jiangzg.base.common.LogUtils;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe toast工具类
 */
public class ToastUtils {

    private static final String LOG_TAG = "ToastUtils";

    private static Toast toast;
    private static Handler handler;

    public static void show(final CharSequence message) {
        if (TextUtils.isEmpty(message)) {
            LogUtils.w(LOG_TAG, "show: message == null");
            return;
        }
        if (!AppUtils.isAppForeground(AppInfo.get().getPackageName())) {
            LogUtils.w(LOG_TAG, "show: isAppForeground == false");
            return;
        }
        if (toast == null) {
            toast = createToast(message);
        } else {
            toast.setText(message);
        }
        if (handler == null) {
            handler = new Handler(Looper.getMainLooper());
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                toast.show();
            }
        });
    }

    public static void cancel() {
        if (toast == null) return;
        toast.cancel();
    }

    /* 自定义Toast */
    @SuppressLint("ShowToast")
    private static Toast createToast(CharSequence meg) {
        return Toast.makeText(AppBase.getInstance(), meg, Toast.LENGTH_SHORT);
    }

}