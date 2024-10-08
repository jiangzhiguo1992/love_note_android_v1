package com.jiangzg.lovenote.controller.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.GsonHelper;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.ResHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Version;

import java.io.File;
import java.util.List;

import retrofit2.Call;

/**
 * apk下载service
 */
public class UpdateService extends Service {

    public static void checkUpdate(BaseActivity activity) {
        MaterialDialog loading = null;
        if (activity != null) {
            loading = activity.getLoading(true);
        }
        int versionCode = AppInfo.get().getVersionCode();
        Call<Result> call = new RetrofitHelper().call(API.class).setVersionNewListGet(versionCode);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                List<Version> versionList = data.getVersionList();
                if (versionList == null || versionList.size() <= 0) {
                    SPHelper.clearVersion();
                } else {
                    SPHelper.setVersion(versionList.get(0));
                    showUpdateDialog(versionList);
                }
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    public static void showUpdateDialog(final List<Version> versionList) {
        if (versionList == null || versionList.size() <= 0) return;
        final Activity top = ActivityStack.getTop();
        if (ActivityStack.isActivityFinish(top)) return;
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < versionList.size(); i++) {
            Version version = versionList.get(i);
            String versionName = version.getVersionName();
            long createdAt = version.getCreateAt();
            String create = DateUtils.getStr(TimeHelper.getJavaTimeByGo(createdAt), DateUtils.FORMAT_LINE_M_D);
            String updateLog = version.getUpdateLog();
            updateLog = updateLog.replace("\\n", "\n"); // 换行问题
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
                .negativeText(R.string.after_say)
                .onPositive((dialog1, which) -> UpdateService.goService(top, versionList.get(0)))
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
        if (ActivityStack.isActivityFinish(top) || !(top instanceof BaseActivity)) {
            UpdateService.this.stopSelf();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 8.0需要允许未知应用安装
            boolean canInstall = MyApp.get().getPackageManager().canRequestPackageInstalls();
            if (canInstall) {
                ossDownloadApk(version);
            } else {
                PermUtils.requestPermissions(top, BaseActivity.REQUEST_INSTALL, PermUtils.installApk, new PermUtils.OnPermissionListener() {
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
                                .negativeText(R.string.brutal_refuse)
                                .onPositive((dialog1, which) -> {
                                    Intent intent = IntentFactory.getInstallSettings(AppInfo.get().getPackageName());
                                    ActivityTrans.start(MyApp.get(), intent);
                                })
                                .dismissListener(dialog12 -> UpdateService.this.stopSelf())
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
        final Activity top = ActivityStack.getTop();
        if (ActivityStack.isActivityFinish(top) || !(top instanceof BaseActivity)) {
            LogUtils.w(UpdateService.class, "ossDownloadApk", "top = null");
            UpdateService.this.stopSelf();
            return;
        }
        if (version == null || version.getVersionCode() <= 0) {
            LogUtils.w(UpdateService.class, "ossDownloadApk", "version = null");
            UpdateService.this.stopSelf();
            return;
        }
        LogUtils.i(UpdateService.class, "ossDownloadApk", GsonHelper.get().toJson(version));
        // 获取下载地址
        String updateUrl = version.getUpdateUrl().trim();
        // 生成apk文件
        final File apkFile = ResHelper.newApkFile(version.getVersionName());
        // 开始下载
        OssHelper.downloadFileInForeground(top, updateUrl, apkFile, new OssHelper.OssDownloadCallBack() {
            @Override
            public void success(String ossKey, File target) {
                installApk(apkFile);
            }

            @Override
            public void failure(String ossKey, String errMsg) {
                UpdateService.this.stopSelf();
            }
        });
    }

    // 启动安装
    private void installApk(File apkFile) {
        LogUtils.i(UpdateService.class, "installApk", "apkFile = " + apkFile.getAbsolutePath());
        Intent installIntent = IntentFactory.getInstall(ResHelper.getFileProviderAuth(), apkFile);
        ActivityTrans.start(UpdateService.this, installIntent);
        UpdateService.this.stopSelf();
    }

}
