package com.jiangzg.ita.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import com.jiangzg.base.component.activity.ActivityStack;
import com.jiangzg.base.component.application.AppInfo;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Version;
import com.jiangzg.ita.service.UpdateService;
import com.jiangzg.ita.third.API;
import com.jiangzg.ita.third.RetroManager;

import retrofit2.Call;

/**
 * Created by gg on 2017/5/3.
 * 常用工具类
 */
public class Utils {

    public static String getImgUrl(String url) {
        String foreUrl = "";
        String imgUrl;
        if (url.startsWith("http")) {
            imgUrl = url;
        } else {
            imgUrl = foreUrl + url;
        }
        return imgUrl;
    }

    public static void checkUpdate(Dialog dialog) {
        Call<Result> call = new RetroManager()
                .call(API.class)
                .checkUpdate(AppInfo.get().getVersionCode());
        RetroManager.enqueue(call, dialog, new RetroManager.CallBack() {
            @Override
            public void onResponse(int code, Result.Data data) {
                if (data != null && data.getVersion() != null) {
                    showUpdateDialog(data.getVersion());
                }
            }

            @Override
            public void onFailure() {
            }
        });

    }

    private static void showUpdateDialog(Version version) {
        final Activity top = ActivityStack.getTop();
        if (top == null) return;
        String title = String.format(top.getString(R.string.find_new_version), version.getVersionName());
        String message = version.getUpdateLog();
        String positive = top.getString(R.string.update_now);
        String negative = top.getString(R.string.update_delay);
        AlertDialog dialog = DialogUtils.createAlert(top, title, message, positive, negative,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateService.goService(top);
                    }
                }, null);
        dialog.show();
    }

}
