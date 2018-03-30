package com.jiangzg.ita.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.jiangzg.base.component.application.AppContext;
import com.jiangzg.ita.base.MyApp;

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
