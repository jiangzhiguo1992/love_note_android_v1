//package com.jiangzg.ita.service;
//
//import android.app.Activity;
//import android.app.Service;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.os.IBinder;
//import android.support.v7.app.AlertDialog;
//
//import com.jiangzg.base.component.activity.ActivityStack;
//import com.jiangzg.base.component.activity.ActivityTrans;
//import com.jiangzg.base.component.application.AppInfo;
//import com.jiangzg.base.component.intent.IntentUtils;
//import com.jiangzg.base.file.FileUtils;
//import com.jiangzg.base.function.PermUtils;
//import com.jiangzg.base.view.DialogUtils;
//import com.jiangzg.base.view.ToastUtils;
//import com.jiangzg.ita.R;
//import com.jiangzg.ita.base.MyApp;
//import com.jiangzg.ita.domain.Result;
//import com.jiangzg.ita.domain.Version;
//import com.jiangzg.ita.third.RetroManager;
//import com.jiangzg.ita.utils.API;
//import com.jiangzg.ita.utils.Constants;
//import com.jiangzg.ita.utils.HttpUtils;
//import com.jiangzg.ita.utils.ResUtils;
//
//import java.io.File;
//
//import okhttp3.ResponseBody;
//import okhttp3.logging.HttpLoggingInterceptor;
//import retrofit2.Call;
//
//public class UpdateService extends Service {
//
//    private static final String EXTRA_VER = "version";
//
//    public static void goService(Context from) {
//        Intent intent = new Intent(from, UpdateService.class);
//        from.startService(intent);
//    }
//
//    public static void goService(Context from, Version version) {
//        Intent intent = new Intent(from, UpdateService.class);
//        intent.putExtra(EXTRA_VER, version);
//        from.startService(intent);
//    }
//
//    public UpdateService() {
//    }
//
//    @Override
//    public void onCreate() {
//        Activity top = ActivityStack.getTop();
//        PermUtils.requestPermissions(top, Constants.REQUEST_PERM_ALERT, PermUtils.alertWindow, new PermUtils.OnPermissionListener() {
//            @Override
//            public void onPermissionGranted(int requestCode, String[] permissions) {
//                if (requestCode == Constants.REQUEST_PERM_ALERT)
//                    checkUpdate();
//            }
//
//            @Override
//            public void onPermissionDenied(int requestCode, String[] permissions) {
//                ToastUtils.show(R.string.update_perm_alert);
//            }
//        });
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        throw null;
//    }
//
//    private void checkUpdate() {
//        final int code = AppInfo.get().getVersionCode();
//        Call<Result<Version>> call = new RetroManager(API.BASE_URL)
//                .log(HttpLoggingInterceptor.Level.BODY)
//                .head(HttpUtils.getHead())
//                .factory(RetroManager.Factory.gson)
//                .call(API.class)
//                .checkUpdate(code);
//        RetroManager.enqueue(call, null, new RetroManager.CallBack<Result<Version>>() {
//            @Override
//            public void onSuccess(Result<Version> result) {
//                ToastUtils.show(result.getMessage());
//                Version version = result.getData();
//                if (code < version.getVersionCode()) { // 小于 有新版本
//                    showNoticeDialog(version); //  提示对话框
//                } else {
//                    stopSelf(); // 停止服务
//                }
//            }
//
//            @Override
//            public void onFailure(int code, String error) {
//                HttpUtils.onResponseFail(code, error);
//            }
//        });
//    }
//
//    /* 提示更新 */
//    private void showNoticeDialog(final Version version) {
//        String title = String.format(getString(R.string.find_new_version), version.getVersionName());
//        String message = version.getUpdateLog();
//        String positive = getString(R.string.update_now);
//        String negative = getString(R.string.update_delay);
//        AlertDialog dialog = DialogUtils.createAlert(this, title, message, positive, negative,
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        newThreadDown(version);
//                    }
//                }, null);
//        DialogUtils.showInContext(dialog);
//    }
//
//    /* 子线程下载 */
//    private void newThreadDown(final Version version) {
//        MyApp.get().getThread().execute(new Runnable() {
//            @Override
//            public void run() {
//                downloadApk(version);
//            }
//        });
//    }
//
//    /* 下载apk */
//    private void downloadApk(final Version version) {
//        Call<ResponseBody> call = new RetroManager(API.BASE_URL)
//                .factory(RetroManager.Factory.empty)
//                .call(API.class)
//                .downloadLargeFile(version.getUpdateUrl());
//        RetroManager.enqueue(call, new RetroManager.CallBack<ResponseBody>() {
//            @Override
//            public void onSuccess(final ResponseBody body) { // 回调也是子线程
//                if (body == null || body.byteStream() == null) return;
//                MyApp.get().getThread().execute(new Runnable() {
//                    @Override
//                    public void run() {
//                        File apkFile = ResUtils.createAPKInRes(version.getVersionName());
//                        FileUtils.writeFileFromIS(apkFile, body.byteStream(), false);
//                        // 启动安装
//                        Intent installIntent = IntentUtils.getInstall(apkFile);
//                        ActivityTrans.start(UpdateService.this, installIntent);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(int httpCode, String errorMessage) {
//                HttpUtils.onResponseFail(httpCode, errorMessage);
//            }
//        });
//    }
//
//}
