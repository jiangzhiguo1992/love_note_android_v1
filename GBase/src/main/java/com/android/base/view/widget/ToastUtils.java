package com.android.base.view.widget;

import android.text.TextUtils;
import android.widget.Toast;

import com.android.base.component.application.AppBase;
import com.android.base.component.application.AppContext;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe toast工具类
 */
public class ToastUtils {

    private static Toast toast;

    public static void show(final CharSequence message) {
        if (TextUtils.isEmpty(message)) return;
        //AppBase.get().getHandler().post(new Runnable() {
        //    @Override
        //    public void run() {
        //        if (toast == null) {
        //            createToast();
        //        }
        //        toast.setText(message);
        //        toast.show();
        //    }
        //});
    }

    public static void show(int resId) {
        if (resId == 0) return;
        String toast = AppContext.get().getString(resId);
        show(toast);
    }

    public static void cancel() {
//        AppNative.get().getHandler().post(new Runnable() {
//            @Override
//            public void run() {
//                if (toast == null) return;
//                toast.cancel();
//            }
//        });
    }

    /* 自定义Toast */
    private static void createToast() {
        if (toast != null) return;
        toast = Toast.makeText(AppContext.get(), "", Toast.LENGTH_SHORT);
    }

}