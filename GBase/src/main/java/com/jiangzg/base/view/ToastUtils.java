package com.jiangzg.base.view;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.widget.Toast;

import com.jiangzg.base.component.application.AppContext;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe toast工具类
 */
public class ToastUtils {

    private static Toast toast;

    public static void show(final CharSequence message) {
        if (TextUtils.isEmpty(message)) return;
        if (toast == null) {
            createToast();
        }
        toast.setText(message);
        toast.show();
    }

    public static void show(int resId) {
        if (resId == 0) return;
        String toast = AppContext.get().getString(resId);
        show(toast);
    }

    public static void cancel() {
        if (toast == null) return;
        toast.cancel();
    }

    /* 自定义Toast */
    @SuppressLint("ShowToast")
    private static void createToast() {
        if (toast != null) return;
        toast = Toast.makeText(AppContext.get(), "", Toast.LENGTH_SHORT);
    }

}