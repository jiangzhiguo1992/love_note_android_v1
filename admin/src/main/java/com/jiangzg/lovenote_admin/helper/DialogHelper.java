package com.jiangzg.lovenote_admin.helper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.domain.FiledInfo;

import java.util.Calendar;
import java.util.List;

/**
 * Created by JZG on 2018/3/30.
 * 对话框
 */
public class DialogHelper {

    public static MaterialDialog.Builder getBuild(Context context) {
        return new MaterialDialog.Builder(context)
                .autoDismiss(true)
                .theme(Theme.LIGHT)
                // color
                .titleColorRes(R.color.font_black)
                .contentColorRes(R.color.font_black)
                .positiveColorAttr(R.attr.colorPrimaryDark)
                .negativeColorAttr(R.attr.colorPrimary)
                .neutralColorAttr(R.attr.colorAccent)
                //.linkColorAttr(R.attr.colorAccent) // 默认的就好
                //.dividerColorAttr(R.attr.colorAccent) // 默认的就好
                //.backgroundColorRes(R.color.material_blue_grey_800) // 默认的就好
                //.widgetColorRes(R.color.material_red_500) // 默认的就好
                //.buttonRippleColorRes(R.color.material_red_500) // 默认的就好
                // selector 有ripple就不要这个了
                //.btnSelector(R.drawable.custom_btn_selector)
                //.btnSelector(R.drawable.custom_btn_selector_primary, DialogAction.POSITIVE)
                //.btnSelectorStacked(R.drawable.custom_btn_selector_stacked)
                //.listSelector(R.drawable.custom_list_and_stackedbtn_selector)
                // Gravity
                .titleGravity(GravityEnum.START)
                .contentGravity(GravityEnum.START)
                .btnStackedGravity(GravityEnum.END)
                .itemsGravity(GravityEnum.START)
                .buttonsGravity(GravityEnum.START);
    }

    public static void showWithAnim(Dialog dialog) {
        setAnim(dialog);
        DialogUtils.show(dialog);
    }

    public static void setAnim(Dialog dialog) {
        DialogUtils.setAnim(dialog, R.style.DialogAnimAlpha);
    }

    public interface OnPickListener {
        void onPick(long time);
    }

    // 日期+时钟选择器
    public static void showDateTimePicker(final Context context, long time, final OnPickListener listener) {
        if (context == null) return;
        showDatePicker(context, time, new OnPickListener() {
            @Override
            public void onPick(long time) {
                showTimePicker(context, time, listener);
            }
        });
    }

    // 日期选择器
    public static void showDatePicker(Context context, long time, final OnPickListener listener) {
        if (context == null) return;
        final Calendar calendar = DateUtils.getCal(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog picker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                if (listener != null) {
                    calendar.set(year, month, dayOfMonth);
                    long timeInMillis = calendar.getTimeInMillis();
                    listener.onPick(timeInMillis);
                }
            }
        }, year, month, day);
        picker.show();
    }

    // 时钟选择器
    public static void showTimePicker(Context context, long time, final OnPickListener listener) {
        if (context == null) return;
        final Calendar calendar = DateUtils.getCal(time);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog picker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (listener != null) {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    long timeInMillis = calendar.getTimeInMillis();
                    listener.onPick(timeInMillis);
                }
            }
        }, hour, minute, true);
        picker.show();
    }

    public static void showFiledInfoDialog(Activity activity, List<FiledInfo> infoList) {
        StringBuilder builder = new StringBuilder();
        if (infoList == null || infoList.size() <= 0) {
            builder.append("没有信息");
        } else {
            for (FiledInfo info : infoList) {
                if (info == null) continue;
                builder.append(info.getCount())
                        .append(" == ")
                        .append(info.getName())
                        .append("\n");
            }
        }
        String show = builder.toString();
        MaterialDialog dialog = DialogHelper.getBuild(activity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(show)
                .build();
        showWithAnim(dialog);
    }

}
