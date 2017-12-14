package com.jiangzg.ita.service;

import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;

import com.jiangzg.base.component.application.AppInfo;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.ita.third.RetroManager;
import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.Version;
import com.jiangzg.ita.utils.API;
import com.jiangzg.ita.utils.HttpUtils;

import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;

public class UpdateService extends Service {

    public static void goService(Context from) {
        Intent intent = new Intent(from, UpdateService.class);
        from.startService(intent);
    }

    public UpdateService() {
    }

    @Override
    public void onCreate() {
        // TODO: 2017/5/16  permission
        checkUpdate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    private void checkUpdate() {
        final int code = AppInfo.get().getVersionCode();
        Call<Version> versionCall = new RetroManager(API.BASE_URL)
                .log(HttpLoggingInterceptor.Level.BODY)
                .head(HttpUtils.getHead())
                .factory(RetroManager.Factory.empty)
                .call(API.class)
                .checkUpdate(code);
        RetroManager.enqueue(versionCall, null, new RetroManager.CallBack<Version>() {
            @Override
            public void onSuccess(Version result) {
                if (result == null) {
                    stopSelf(); // 停止服务
                    return;
                }
                if (code < result.getVersionCode()) { // 小于 有新版本
                    showNoticeDialog(result); //  提示对话框
                } else {
                    stopSelf(); // 停止服务
                }
            }

            @Override
            public void onFailure(int code, String error) {
                HttpUtils.onFailure(code, error);
            }
        });
    }

    /* 提示更新 */
    private void showNoticeDialog(final Version version) {
        String title = String.format(getString(R.string.find_new_version), version.getVersionName());
        String message = version.getChangeLog();
        String positive = getString(R.string.update_now);
        String negative = getString(R.string.update_delay);
        AlertDialog dialog = DialogUtils.createAlert(this, title, message, positive, negative,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newThreadDown(version);
                    }
                }, null);
        DialogUtils.showInContext(dialog);
    }

    /* 子线程下载 */
    private void newThreadDown(final Version version) {
//        MyApp.get().getThread().execute(new Runnable() {
//            @Override
//            public void run() {
//                downloadApk(version);
//            }
//        });
    }

    /* 下载apk */
    private void downloadApk(final Version version) {
        Call<ResponseBody> call = new RetroManager(API.BASE_URL)
                .factory(RetroManager.Factory.empty)
                .call(API.class)
                .downloadLargeFile(version.getUpdateUrl());
        RetroManager.enqueue(call, new RetroManager.CallBack<ResponseBody>() {
            @Override
            public void onSuccess(final ResponseBody body) { // 回调也是子线程
                if (body == null || body.byteStream() == null) return;
//                MyApp.get().getThread().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        File apkFile = ResUtils.createAPKInRes(version.getVersionName());
//                        FileUtils.writeFileFromIS(apkFile, body.byteStream(), false);
//                        // 启动安装
//                        Intent installIntent = IntentUtils.getInstall(apkFile);
//                        ActivityTrans.startActivity(UpdateService.this, installIntent);
//                    }
//                });
            }

            @Override
            public void onFailure(int httpCode, String errorMessage) {
                HttpUtils.onFailure(httpCode, errorMessage);
            }
        });
    }

}
