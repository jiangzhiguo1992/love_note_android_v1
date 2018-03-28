package com.jiangzg.ita.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class CountDownService extends Service {

    public static void goService(Context from) {
        Intent intent = new Intent(from, CountDownService.class);
        from.startService(intent);
    }

    public CountDownService() {
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        goService(this);
    }
}
