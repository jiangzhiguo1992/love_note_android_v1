package com.android.base.component.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

/**
 * Created by JiangZhiGuo on 2016/10/13.
 * describe Activity跳转工具类
 */
public class ActivityTrans {

    private static final String LOG_TAG = "AppBase";

    /**
     * 分享元素过渡动画，一般在目标activity里的setContentView后调用
     */
    public static void setShareElement(@NonNull View share, @NonNull String tag) {
        ViewCompat.setTransitionName(share, tag);
    }

    /**
     * Context启动activity
     */
    @SafeVarargs
    public static void start(@NonNull Context from, @NonNull Intent intent, @Nullable Pair<View, String>... sharedElements) {
        if (from instanceof Activity) {
            Activity activity = (Activity) from;
            if (sharedElements == null || sharedElements.length < 1) {
                activity.startActivity(intent);
            } else {
                try {
                    startWithElement(activity, intent, sharedElements);
                } catch (Exception e) {
                    e.printStackTrace();
                    activity.startActivity(intent);
                }
            }
        } else {
            startByContext(from, intent);
        }
    }

    /**
     * fragment启动activity
     */
    @SafeVarargs
    public static void start(@NonNull Fragment from, @NonNull Intent intent, @Nullable Pair<View, String>... sharedElements) {
        if (sharedElements == null || sharedElements.length < 1) {
            from.startActivity(intent);
        } else {
            try {
                FragmentActivity activity = from.getActivity();
                if (activity != null) {
                    startWithElement(activity, intent, sharedElements);
                } else {
                    from.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                from.startActivity(intent);
            }
        }
    }

    /**
     * activity启动activity，setResult设置回传的resultCode和intent
     */
    @SafeVarargs
    public static void startResult(@NonNull Activity from, @NonNull Intent intent, int requestCode, @Nullable Pair<View, String>... sharedElements) {
        if (sharedElements == null || sharedElements.length < 1) {
            from.startActivityForResult(intent, requestCode);
        } else {
            try {
                startWithElement(from, intent, requestCode, sharedElements);
            } catch (Exception e) {
                e.printStackTrace();
                from.startActivityForResult(intent, requestCode);
            }
        }
    }

    /**
     * Fragment启动activity，setResult设置回传的resultCode和intent
     */
    @SafeVarargs
    public static void startResult(@NonNull Fragment from, @NonNull Intent intent, int requestCode, @Nullable Pair<View, String>... sharedElements) {
        if (sharedElements == null || sharedElements.length < 1) {
            from.startActivityForResult(intent, requestCode);
        } else {
            try {
                FragmentActivity activity = from.getActivity();
                if (activity != null) {
                    startWithElement(activity, intent, requestCode, sharedElements);
                } else {
                    from.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
                from.startActivityForResult(intent, requestCode);
            }
        }
    }

    /**
     * 多层fragment时，第二级fragment是无法在startActivityForResult上时候收到回传intent的
     */
    @SafeVarargs
    public static void startResult2Fragment(@NonNull Fragment from, @NonNull Intent intent, int requestCode, @Nullable Pair<View, String>... sharedElements) {
        FragmentActivity activity = from.getActivity();
        if (activity == null) {
            Log.e(LOG_TAG, "startResultFragment() from.getActivity() == null");
            return;
        }
        if (sharedElements == null || sharedElements.length < 1) {
//                startElement(activity, intent, requestCode);
            activity.startActivityFromFragment(from, intent, requestCode);
        } else {
            try {
                startWithElement(activity, intent, requestCode, sharedElements);
            } catch (Exception e) {
                e.printStackTrace();
                activity.startActivityFromFragment(from, intent, requestCode);
            }
        }
    }

    /* context启动activity */
    private static void startByContext(@NonNull Context from, @NonNull Intent intent) {
        ActivityStack.changeTask(intent);
        from.startActivity(intent);
    }

    /* 5.0过渡跳转(有分享元素) */
    @SafeVarargs
    private static void startWithElement(@NonNull Activity from, @NonNull Intent intent, Pair<View, String>... sharedElements) throws Exception {
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(from, sharedElements);
        from.startActivity(intent, options.toBundle());
    }

    /* 5.0过渡跳转(有分享元素) */
    @SafeVarargs
    private static void startWithElement(@NonNull Activity from, @NonNull Intent intent, int requestCode, Pair<View, String>... sharedElements) throws Exception {
        ActivityOptionsCompat options = ActivityOptionsCompat
                .makeSceneTransitionAnimation(from, sharedElements);
        ActivityCompat.startActivityForResult(from, intent, requestCode, options.toBundle());
    }

}
