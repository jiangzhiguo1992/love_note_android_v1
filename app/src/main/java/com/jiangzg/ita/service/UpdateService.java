package com.jiangzg.ita.service;

import android.app.Activity;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;

import com.jiangzg.base.component.activity.ActivityStack;
import com.jiangzg.base.component.application.AppInfo;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Version;
import com.jiangzg.ita.third.API;
import com.jiangzg.ita.third.RetroManager;

import retrofit2.Call;

public class UpdateService extends Service {

    private static final String EXTRA_VER = "version";

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

    public static void showUpdateDialog(Version version) {
        final Activity top = ActivityStack.getTop();
        if (top == null) return;
        String title = String.format(top.getString(R.string.find_new_version_colon_holder), version.getVersionName());
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

    public static void goService(Context from) {
        Intent intent = new Intent(from, UpdateService.class);
        from.startService(intent);
    }

    public static void goService(Context from, Version version) {
        Intent intent = new Intent(from, UpdateService.class);
        intent.putExtra(EXTRA_VER, version);
        from.startService(intent);
    }

    public UpdateService() {
    }

    @Override
    public void onCreate() {
        //newThreadDown(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    /* 子线程下载 */
    private void newThreadDown(final Version version) {
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                //downloadApk(version);
            }
        });
    }

    /* 下载apk */
    //private void downloadApk(final Version version) {
    //    Call<ResponseBody> call = new RetroManager(API.BASE_URL)
    //            .factory(RetroManager.Factory.empty)
    //            .call(API.class)
    //            .downloadLargeFile(version.getUpdateUrl());
    //    RetroManager.enqueue(call, new RetroManager.CallBack<ResponseBody>() {
    //        @Override
    //        public void onSuccess(final ResponseBody body) { // 回调也是子线程
    //            if (body == null || body.byteStream() == null) return;
    //            MyApp.get().getThread().execute(new Runnable() {
    //                @Override
    //                public void run() {
    //                    File apkFile = ResHelper.createAPKInRes(version.getVersionName());
    //                    FileUtils.writeFileFromIS(apkFile, body.byteStream(), false);
    //                    // 启动安装
    //                    Intent installIntent = IntentUtils.getInstall(apkFile);
    //                    ActivityTrans.start(UpdateService.this, installIntent);
    //                }
    //            });
    //        }
    //
    //        @Override
    //        public void onFailure(int httpCode, String errorMessage) {
    //            HttpUtils.onResponseFail(httpCode, errorMessage);
    //        }
    //    });
    //}

}
