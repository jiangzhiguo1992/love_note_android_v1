package com.jiangzg.mianmian.helper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.mianmian.R;

/**
 * Created by JZG on 2018/3/30.
 * 对话框
 * TODO 错误对话框中，左下角加意见反馈
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
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        Intent permission = IntentFactory.getPermission();
                        ActivityTrans.start(activity, permission);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
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
