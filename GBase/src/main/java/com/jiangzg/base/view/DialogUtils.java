package com.jiangzg.base.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.StyleRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by JZG on 2018/4/27.
 * 对话框工具类
 */
public class DialogUtils {

    public static void show(Dialog dialog) {
        if (dialog == null || dialog.isShowing()) return;
        dialog.show();
    }

    @SuppressLint("MissingPermission")
    public static void showInContext(Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        show(dialog);
    }

    public static void dismiss(Dialog dialog) {
        if (dialog == null || !dialog.isShowing()) return;
        dialog.dismiss();
    }

    /**
     * 设置动画，防止闪屏
     */
    public static void setAnim(Dialog dialog, @StyleRes int resId) {
        if (dialog == null || resId == 0) return;
        Window window = dialog.getWindow();
        if (window != null) {
            window.setWindowAnimations(resId);
        }
    }

    /**
     * 设置透明度
     */
    public static void setAlpha(Dialog dialog, float alpha) {
        Window window = dialog.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.alpha = alpha;
        window.setAttributes(lp);
    }

    /**
     * 设置暗黑背景层
     */
    public static void setBlackBg(Dialog dialog, float alpha) {
        Window window = dialog.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = alpha;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    /**
     * 自定义对话框
     *
     * @param theme R.style.DialogCustom
     */
    public static Dialog createCustom(Activity activity, int viewRes, int theme, int width, int height) {
        View view = LayoutInflater.from(activity).inflate(viewRes, null);
        return createCustom(activity, view, theme, width, height);
    }

    public static Dialog createCustom(Activity activity, View view, int theme, int width, int height) {
        final Dialog dialog = new Dialog(activity, theme);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        // 宽高
        if (width > 0) lp.width = width;
        if (height > 0) lp.height = height;
        dialog.setContentView(view, lp);
        // view
        return dialog;
    }

}
