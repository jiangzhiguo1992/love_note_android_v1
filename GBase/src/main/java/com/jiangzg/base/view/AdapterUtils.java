package com.jiangzg.base.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.WindowManager;

import com.jiangzg.base.application.AppListener;

/**
 * Created by JZG on 2019-05-15.
 */
public class AdapterUtils {

    public static void initApp(Application app, final float width, final float height) {
        AppListener.addActivityListener("AdapterUtils", new AppListener.ActivityListener() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                resetDensity(activity, width, height);
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }
        });
        resetDensity(app, width, height);
    }

    /**
     * dp适配getResources().getDisplayMetrics().density
     * <p>
     * sp适配getResources().getDisplayMetrics().scaledDensity
     * <p>
     * pt适配getResources().getDisplayMetrics().xdpi
     */
    private static void resetDensity(Context context, float width, float height) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return;
        //获取屏幕的数值
        Point point = new Point();
        windowManager.getDefaultDisplay().getSize(point);
        //dp适配getResources().getDisplayMetrics().density
        context.getResources().getDisplayMetrics().density = point.x / width * 2f;
        context.getResources().getDisplayMetrics().density = point.y / height * 2f;
        //sp适配getResources().getDisplayMetrics().scaledDensity
        context.getResources().getDisplayMetrics().scaledDensity = point.x / width * 2f;
        context.getResources().getDisplayMetrics().scaledDensity = point.y / height * 2f;
    }

}
