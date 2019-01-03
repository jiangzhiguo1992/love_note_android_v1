package com.jiangzg.lovenote.helper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote.R;

import java.util.Calendar;

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

    // 跳转权限dialog
    public static void showGoPermDialog(final Activity activity) {
        MaterialDialog dialog = DialogHelper.getBuild(activity)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .content(R.string.need_check_some_perm)
                .positiveText(R.string.go_now)
                .negativeText(R.string.brutal_refuse)
                .onPositive((dialog1, which) -> {
                    Intent permission = IntentFactory.getPermission();
                    ActivityTrans.start(activity, permission);
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    public interface OnPickListener {
        void onPick(long time);
    }

    // 日期+时钟选择器
    public static void showDateTimePicker(final Context context, long time, final OnPickListener listener) {
        if (context == null) return;
        showDatePicker(context, time, time1 -> showTimePicker(context, time1, listener));
    }

    // 日期选择器
    public static void showDatePicker(Context context, long time, final OnPickListener listener) {
        if (context == null) return;
        final Calendar calendar = DateUtils.getCal(time);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog picker = new DatePickerDialog(context, (view, year1, month1, dayOfMonth) -> {
            if (listener != null) {
                calendar.set(year1, month1, dayOfMonth);
                long timeInMillis = calendar.getTimeInMillis();
                listener.onPick(timeInMillis);
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
        TimePickerDialog picker = new TimePickerDialog(context, (view, hourOfDay, minute1) -> {
            if (listener != null) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute1);
                long timeInMillis = calendar.getTimeInMillis();
                listener.onPick(timeInMillis);
            }
        }, hour, minute, true);
        picker.show();
    }

//public static Dialog showPickerFromPictureCamera(final Activity activity, final File cameraFile) {
//    View view = LayoutInflater.from(activity).inflate(R.layout.pop_select_img_from_picture_camera, null);
//    int screenWidth = ScreenUtils.getScreenRealWidth(activity);
//    int screenHeight = ScreenUtils.getScreenHeight(activity);
//    final Dialog dialog = DialogUtils.createCustom(activity, view, R.style.DialogCustom, screenWidth, screenHeight);
//    View.OnClickListener listener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.root:
//                    DialogUtils.dismiss(dialog);
//                    ResHelper.deleteFileInBackground(cameraFile);
//                    break;
//                case R.id.llPicture:
//                    DialogUtils.dismiss(dialog);
//                    ResHelper.deleteFileInBackground(cameraFile);
//                    PermUtils.requestPermissions(activity, ConsHelper.REQUEST_APP_INFO, PermUtils.picture, new PermUtils.OnPermissionListener() {
//                        @Override
//                        public void onPermissionGranted(int requestCode, String[] permissions) {
//                            Intent picture = IntentFactory.getPicture();
//                            ActivityTrans.startResult(activity, picture, ConsHelper.REQUEST_PICTURE);
//                        }
//
//                        @Override
//                        public void onPermissionDenied(int requestCode, String[] permissions) {
//
//                        }
//                    });
//                    break;
//                case R.id.llCamera:
//                    DialogUtils.dismiss(dialog);
//                    PermUtils.requestPermissions(activity, ConsHelper.REQUEST_CAMERA, PermUtils.camera, new PermUtils.OnPermissionListener() {
//                        @Override
//                        public void onPermissionGranted(int requestCode, String[] permissions) {
//                            Intent camera = IntentFactory.getCamera(cameraFile);
//                            ActivityTrans.startResult(activity, camera, ConsHelper.REQUEST_CAMERA);
//                        }
//
//                        @Override
//                        public void onPermissionDenied(int requestCode, String[] permissions) {
//
//                        }
//                    });
//                    break;
//                case R.id.llCancel:
//                    DialogUtils.dismiss(dialog);
//                    ResHelper.deleteFileInBackground(cameraFile);
//                    break;
//            }
//        }
//    };
//    RelativeLayout root = view.findViewById(R.id.root);
//    LinearLayout llAlbum = view.findViewById(R.id.llPicture);
//    LinearLayout llCamera = view.findViewById(R.id.llCamera);
//    LinearLayout llCancel = view.findViewById(R.id.llCancel);
//    root.setOnClickListener(listener);
//    llAlbum.setOnClickListener(listener);
//    llCamera.setOnClickListener(listener);
//    llCancel.setOnClickListener(listener);
//    // showWithAnim
//    DialogUtils.setAnim(dialog, R.style.DialogAnimScale);
//    DialogUtils.show(dialog);
//    return dialog;
//}
}
