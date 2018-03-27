package com.jiangzg.ita.third;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.activity.ActivityStack;
import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.component.intent.IntentUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.MyApp;

/**
 * Created by JZG on 2018/3/27.
 * 对话框
 */
public class DialogHelper {

    //public static void showDialog(@StringRes int resId) {
    //    String content = MyApp.get().getString(resId);
    //    showDialog(content);
    //}
    //
    //public static void showDialog(String content,boolean cancel) {
    //    Activity top = ActivityStack.getTop();
    //    if (top == null) return;
    //    new MaterialDialog.Builder(top)
    //            .content(content)
    //            .cancelable(cancel)
    //            .canceledOnTouchOutside(cancel)
    //            .autoDismiss(true)
    //            .positiveText(top.getString(R.string.go_to_setting))
    //            .onPositive(new MaterialDialog.SingleButtonCallback() {
    //                @Override
    //                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
    //                    Intent netSettings = IntentUtils.getNetSettings();
    //                    ActivityTrans.start(top, netSettings);
    //                }
    //            })
    //            .negativeText(top.getString(R.string.i_know))
    //            .build()
    //            .show();
    //}
}
