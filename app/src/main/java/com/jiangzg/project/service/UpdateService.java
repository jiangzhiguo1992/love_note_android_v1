package com.jiangzg.project.service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;

import com.android.base.component.application.AppInfo;
import com.android.base.view.widget.DialogUtils;
import com.android.depend.utils.RetroUtils;
import com.jiangzg.project.R;
import com.jiangzg.project.domain.Version;
import com.jiangzg.project.utils.API;
import com.jiangzg.project.utils.HttpUtils;

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
        Call<Version> versionCall = new RetroUtils(API.BASE_URL)
                .log(HttpLoggingInterceptor.Level.BODY)
                .head(HttpUtils.getHead())
                .factory(RetroUtils.Factory.empty)
                .call(API.class)
                .checkUpdate(code);
        RetroUtils.enqueue(versionCall, null, new RetroUtils.CallBack<Version>() {
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
                });
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
        Call<ResponseBody> call = new RetroUtils(API.BASE_URL)
                .factory(RetroUtils.Factory.empty)
                .call(API.class)
                .downloadLargeFile(version.getUpdateUrl());
        RetroUtils.enqueue(call, new RetroUtils.CallBack<ResponseBody>() {
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
