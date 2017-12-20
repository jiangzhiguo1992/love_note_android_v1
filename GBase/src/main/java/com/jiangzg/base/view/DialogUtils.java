package com.jiangzg.base.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.Calendar;

/**
 * Created by Jiang on 2016/10/13
 * DialogUtils: 对话框管理工具类
 */
public class DialogUtils {

    /**
     * @param height 百分比
     * @param width  百分比
     */
    public static Dialog createCustom(Activity activity, View view, int theme, float height, float width) {
        DisplayMetrics d = activity.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        int rHeight = (int) (d.heightPixels * height) - BarUtils.getStatusBarHeight(activity); // 高度设置为屏幕的0.x（减去statusBar高度）
        int rWidth = (int) (d.widthPixels * width);  // 宽度设置为屏幕的0.x
        return createCustom(activity, view, theme, rHeight, rWidth);
    }

    /**
     * 自定义对话框
     *
     * @param view  LayoutInflater.from(activity).inflate(layoutId, null);
     * @param theme R.style.DialogCustom
     */
    public static Dialog createCustom(Activity activity, View view, int theme, int height, int width) {
        final Dialog dialog = new Dialog(activity, theme);
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        if (height != 0)   // 高度设置为屏幕的0.x（减去statusBar高度）
            lp.height = height;
        if (width != 0)   // 宽度设置为屏幕的0.x
            lp.width = width;
        dialog.setContentView(view, lp);
        return dialog;
    }

    /**
     * 警告对话框
     */
    public static AlertDialog createAlert(Context context, String title, String message,
                                          String positive, String negative,
                                          DialogInterface.OnClickListener positiveListener,
                                          DialogInterface.OnClickListener negativeListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positive, positiveListener);
        builder.setNegativeButton(negative, negativeListener);
        return builder.create();
    }

    /**
     * 等待对话框
     */
    public static ProgressDialog createLoading(Context context, int theme, String title,
                                               String message, boolean cancel) {
        ProgressDialog loading;
        if (theme == 0) {
            loading = new ProgressDialog(context);
        } else {
            loading = new ProgressDialog(context, theme);
        }
        if (!TextUtils.isEmpty(title))
            loading.setTitle(title);
        if (!TextUtils.isEmpty(message))
            loading.setMessage(message);
        loading.setCanceledOnTouchOutside(false);
        loading.setCancelable(cancel);
        return loading;
    }

    /**
     * 进度对话框(没有message)
     */
    public static ProgressDialog createProgress(Context context, int theme, String title,
                                                boolean cancel, int max, int start,
                                                DialogInterface.OnCancelListener listener) {
        ProgressDialog progress;
        if (theme == 0) {
            progress = new ProgressDialog(context);
        } else {
            progress = new ProgressDialog(context, theme);
        }
        if (!TextUtils.isEmpty(title)) {
            progress.setTitle(title);
        }
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setMax(max);
        // setProgress与setMessage冲突
        progress.setProgress(start);
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(cancel);
        progress.setOnCancelListener(listener);
        return progress;
    }

    /**
     * 单选对话框
     *
     * @param context          Activity必须是AppCompat的
     * @param items            数据源
     * @param checkedIndex     首次选中下标
     * @param positive         确定提示
     * @param choiceListener   选择回调(这里的witch有用)
     * @param positiveListener 确定回调(这里的witch没用)
     * @return dialog（没有show）
     */
    public static AlertDialog createSingle(Context context, String title, String[] items,
                                           int checkedIndex, String positive, String negative,
                                           DialogInterface.OnClickListener choiceListener,
                                           DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        // setSingleChoiceItems与setMessage冲突
        builder.setSingleChoiceItems(items, checkedIndex, choiceListener);
        builder.setPositiveButton(positive, positiveListener);
        builder.setNegativeButton(negative, null);
        return builder.create();
    }

    /**
     * 多选对话框
     */
    public static AlertDialog createMulti(Context context, String title, String[] items,
                                          final boolean[] checkedState, String positive, String negative,
                                          DialogInterface.OnMultiChoiceClickListener choiceListener,
                                          DialogInterface.OnClickListener positiveListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        // setMultiChoiceItems与setMessage冲突
        builder.setMultiChoiceItems(items, checkedState, choiceListener);
        builder.setPositiveButton(positive, positiveListener);
        builder.setNegativeButton(negative, null);
        return builder.create();
    }

    /**
     * 创建系统日期选择对话框 picker.show();
     */
    public static DatePickerDialog createDatePicker(Context context, Calendar calendar, DatePickerDialog.OnDateSetListener onDateSetListener) {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(context, onDateSetListener, year, month, day);
    }

    /**
     * 创建系统时间选择对话框 picker.show();
     */
    public static TimePickerDialog createTimePicker(Context context, Calendar calendar, boolean is24, TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(context, onTimeSetListener, hour, minute, is24);
    }

    public static void setAnim(Dialog dialog, @StyleRes int resId) {
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
    public static void setDimamount(Dialog dialog, float alpha) {
        Window window = dialog.getWindow();
        if (window == null) return;
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = alpha;
        window.setAttributes(lp);
        window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
    }

    /**
     * 后台弹框
     */
    @SuppressLint("MissingPermission")
    public static void showInContext(Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        dialog.show();
    }

}
