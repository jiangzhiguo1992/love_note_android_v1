package com.jiangzg.mianmian.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentSend;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.Version;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;

import java.io.File;
import java.util.List;

import retrofit2.Call;

public class UpdateService extends Service {

    public static void checkUpdate(BaseActivity activity) {
        MaterialDialog loading = null;
        if (activity != null) {
            loading = activity.getLoading(activity.getString(R.string.are_update_check), true);
        }
        int versionCode = AppInfo.get().getVersionCode();
        Call<Result> call = new RetrofitHelper().call(API.class).checkUpdate(versionCode);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                List<Version> versionList = data.getVersionList();
                if (versionList == null || versionList.size() <= 0) return;
                showUpdateDialog(versionList);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    public static void showUpdateDialog(final List<Version> versionList) {
        if (versionList == null || versionList.size() <= 0) return;
        final Activity top = ActivityStack.getTop();
        if (top == null) return;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < versionList.size(); i++) {
            Version version = versionList.get(i);
            String versionName = version.getVersionName();
            long createdAt = version.getCreateAt();
            String create = DateUtils.getCSTString(ConvertHelper.convertTimeGo2Java(createdAt), ConstantUtils.FORMAT_CHINA_M_D);
            String updateLog = version.getUpdateLog();
            builder.append(MyApp.get().getString(R.string.version_number_colon)).append(versionName)
                    .append("  (").append(create).append(")\n")
                    .append(updateLog).append("\n\n");
        }
        String content = builder.toString();
        MaterialDialog dialog = DialogHelper.getBuild(top)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .title(R.string.have_new_version)
                .content(content)
                .positiveText(R.string.update_now)
                .negativeText(R.string.update_delay)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        UpdateService.goService(top, versionList.get(0));
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    public static void goService(Context from, Version version) {
        Intent intent = new Intent(from, UpdateService.class);
        intent.putExtra("version", version);
        from.startService(intent);
    }

    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    // 多次创建只会调用一次
    @Override
    public void onCreate() {
    }

    // 多次创建会调用多次
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Version version = intent.getParcelableExtra("version");
        checkPerm(version);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // 检查权限
    private void checkPerm(final Version version) {
        final Activity top = ActivityStack.getTop();
        if (top == null || !(top instanceof BaseActivity)) {
            UpdateService.this.stopSelf();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 8.0需要允许未知应用安装
            boolean canInstall = MyApp.get().getPackageManager().canRequestPackageInstalls();
            if (canInstall) {
                ossDownloadApk(version);
            } else {
                PermUtils.requestPermissions(top, ConsHelper.REQUEST_INSTALL, PermUtils.installApk, new PermUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted(int requestCode, String[] permissions) {
                        ossDownloadApk(version);
                    }

                    @SuppressLint("InlinedApi")
                    @Override
                    public void onPermissionDenied(int requestCode, String[] permissions) {
                        MaterialDialog dialog = DialogHelper.getBuild(top)
                                .cancelable(false)
                                .canceledOnTouchOutside(false)
                                .content(R.string.need_check_some_perm_can_install)
                                .positiveText(R.string.go_now)
                                .negativeText(R.string.say_after)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                                        ActivityTrans.start(MyApp.get(), intent);
                                    }
                                })
                                .dismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        UpdateService.this.stopSelf();
                                    }
                                })
                                .build();
                        DialogHelper.showWithAnim(dialog);
                    }
                });
            }
        } else {
            // 8.0以下直接下载
            ossDownloadApk(version);
        }
    }

    // 开始下载
    private void ossDownloadApk(Version version) {
        if (version == null || version.getVersionCode() <= 0) {
            UpdateService.this.stopSelf();
            return;
        }
        final Activity top = ActivityStack.getTop();
        if (top == null || !(top instanceof BaseActivity)) {
            UpdateService.this.stopSelf();
            return;
        }
        // 获取下载地址
        String updateUrl = version.getUpdateUrl().trim();
        // 生成apk文件
        final File apkFile = ResHelper.createFileInCache(version.getVersionName() + ".apk");
        // 开始下载
        OssHelper.downloadApk(top, updateUrl, apkFile, new OssHelper.OssDownloadCallBack() {
            @Override
            public void success(String ossPath) {
                installApk(apkFile);
            }

            @Override
            public void failure(String ossPath) {
                UpdateService.this.stopSelf();
            }
        });
    }

    // 启动安装
    private void installApk(File apkFile) {
        Intent installIntent = IntentSend.getInstall(apkFile);
        ActivityTrans.start(UpdateService.this, installIntent);
        UpdateService.this.stopSelf();
    }

}
