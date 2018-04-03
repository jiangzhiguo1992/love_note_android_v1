package com.jiangzg.ita.service;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Version;
import com.jiangzg.ita.helper.DialogHelper;
import com.jiangzg.ita.third.API;
import com.jiangzg.ita.third.RetrofitHelper;

import java.util.List;

import retrofit2.Call;

public class UpdateService extends Service {

    private Version version;

    public static void checkUpdate(BaseActivity activity) {
        MaterialDialog loading = null;
        if (activity != null) {
            loading = activity.getLoading(activity.getString(R.string.are_update_check), true);
        }
        Call<Result> call = new RetrofitHelper().call(API.class).checkUpdate(AppInfo.get().getVersionCode());
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                List<Version> versionList = data.getVersionList();
                if (versionList == null || versionList.size() <= 0) return;
                showUpdateDialog(versionList);
            }

            @Override
            public void onFailure() {
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
            String create = DateUtils.getString(createdAt * 1000, ConstantUtils.FORMAT_CHINA_M_D);
            String updateLog = version.getUpdateLog();
            builder.append(MyApp.get().getString(R.string.version_colon)).append(versionName)
                    .append("(").append(create).append(")\n")
                    .append(updateLog).append("\n\n");
        }
        String content = builder.toString();
        MaterialDialog dialog = new MaterialDialog.Builder(top)
                .title(R.string.have_new_version)
                .content(content)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .autoDismiss(true)
                .positiveText(R.string.update_now)
                .negativeText(R.string.update_delay)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        UpdateService.goService(top, versionList.get(0));
                    }
                })
                .build();
        DialogHelper.setAnim(dialog);
        DialogHelper.show(dialog);
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        version = intent.getParcelableExtra("version");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        // todo oss下载
        newThreadDown(version);
    }

    /* 子线程下载 */
    private void newThreadDown(final Version version) {
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                downloadApk(version);
            }
        });
    }

    /* 下载apk */
    private void downloadApk(final Version version) {
        //Call<ResponseBody> call = new RetrofitHelper(API.BASE_URL)
        //        .factory(RetrofitHelper.Factory.empty)
        //        .call(API.class)
        //        .downloadLargeFile(version.getUpdateUrl());
        //RetrofitHelper.enqueue(call, new RetrofitHelper.CallBack<ResponseBody>() {
        //    @Override
        //    public void onSuccess(final ResponseBody body) { // 回调也是子线程
        //        if (body == null || body.byteStream() == null) return;
        //        MyApp.get().getThread().execute(new Runnable() {
        //            @Override
        //            public void run() {
        //                File apkFile = ResHelper.createAPKInRes(version.getVersionName());
        //                FileUtils.writeFileFromIS(apkFile, body.byteStream(), false);
        //                // 启动安装
        //                Intent installIntent = IntentSend.getInstall(apkFile);
        //                ActivityTrans.start(UpdateService.this, installIntent);
        //            }
        //        });
        //    }
        //
        //    @Override
        //    public void onFailure(int httpCode, String errorMessage) {
        //        HttpUtils.onResponseFail(httpCode, errorMessage);
        //    }
        //});
    }

}
