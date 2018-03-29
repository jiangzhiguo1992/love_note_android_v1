package com.jiangzg.ita.service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.component.application.AppContext;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.OssInfo;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.PrefHelper;
import com.jiangzg.ita.third.API;
import com.jiangzg.ita.third.OssHelper;
import com.jiangzg.ita.third.RetrofitHelper;

import retrofit2.Call;

public class TimerService extends Service {


    public static void goService(Context from) {
        Intent intent = new Intent(from, TimerService.class);
        from.startService(intent);
    }

    public TimerService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {

        Runnable ossRun = new Runnable() {
            @Override
            public void run() {

            }
        };

        MyApp.get().getThread().execute(ossRun);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        goService(this);
    }

}
