package com.jiangzg.base.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.annotation.LayoutRes;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jiangzg.base.common.LogUtils;

/**
 * Created by JZG on 2018/4/27.
 * 对话框工具类
 */
public class DialogUtils {

    public static void show(Dialog dialog) {
        if (dialog == null) {
            LogUtils.i(DialogUtils.class, "show", "dialog == null");
            return;
        }
        if (!dialog.isShowing()) {
            Context context = ((ContextWrapper) dialog.getContext()).getBaseContext();
            if (context instanceof Activity) {
                if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    dialog.show();
                }
            } else {
                dialog.show();
            }
        }
    }

    @SuppressLint("MissingPermission")
    public static void showInContext(Dialog dialog) {
        if (dialog == null) {
            LogUtils.d(DialogUtils.class, "showInContext", "dialog == null");
            return;
        }
        Window window = dialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        show(dialog);
    }

    public static void dismiss(Dialog dialog) {
        if (dialog == null) {
            LogUtils.i(DialogUtils.class, "dismiss", "dialog == null");
            return;
        }
        if (dialog.isShowing()) {
            Context context = ((ContextWrapper) dialog.getContext()).getBaseContext();
            if (context instanceof Activity) {
                if (!((Activity) context).isFinishing() && !((Activity) context).isDestroyed()) {
                    dialog.dismiss();
                }
            } else {
                dialog.dismiss();
            }
        }
    }

    /**
     * 设置动画，防止闪屏
     */
    public static void setAnim(Dialog dialog, @StyleRes int resId) {
        if (dialog == null || resId == 0) {
            LogUtils.i(DialogUtils.class, "setAnim", "dialog == null || resId == 0");
            return;
        }
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(resId);
        }
    }

    /**
     * 设置背景透明度
     */
    public static void setBgAlpha(Dialog dialog, float alpha) {
        if (dialog == null) {
            LogUtils.w(DialogUtils.class, "setBgAlpha", "dialog == null");
            return;
        }
        Window window = dialog.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = alpha;
        window.setAttributes(lp);
    }

    /**
     * 设置背景暗黑
     */
    public static void setBgBlack(Dialog dialog, float black) {
        if (dialog == null) {
            LogUtils.w(DialogUtils.class, "setBgBlack", "dialog == null");
            return;
        }
        Window window = dialog.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = black;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    /**
     * 自定义对话框
     *
     * @param theme R.style.DialogCustom
     */
    public static Dialog createCustom(Activity activity, @LayoutRes int viewRes, @StyleRes int theme, int width, int height) {
        if (activity == null || viewRes == 0) {
            LogUtils.w(DialogUtils.class, "createCustom", "activity == null || viewRes == 0");
            return null;
        }
        View view = LayoutInflater.from(activity).inflate(viewRes, null);
        return createCustom(activity, view, theme, width, height);
    }

    public static Dialog createCustom(Activity activity, View view, @StyleRes int theme, int width, int height) {
        if (activity == null || view == null || theme == 0) {
            LogUtils.w(DialogUtils.class, "createCustom", "activity == null || view == null || theme == 0");
            return null;
        }
        Dialog dialog = new Dialog(activity, theme);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        // 宽高
        if (width > 0) lp.width = width;
        if (height > 0) lp.height = height;
        dialog.setContentView(view, lp);
        // view
        return dialog;
    }

}
