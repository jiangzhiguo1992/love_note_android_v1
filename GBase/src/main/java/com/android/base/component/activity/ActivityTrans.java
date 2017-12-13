package com.android.base.component.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.View;

import com.android.base.common.StringUtils;

/**
 * Created by JiangZhiGuo on 2016/10/13.
 * describe Activity跳转工具类
 */
public class ActivityTrans {

    private static final String LOG_TAG = "ActivityTrans";

    /**
     * 分享元素过渡动画，一般在目标activity里的setContentView后调用
     */
    public static void setShareElement(View share, String tag) {
        if (share == null || StringUtils.isEmpty(tag)) {
            Log.e(LOG_TAG, "setShareElement: share == null || StringUtils.isEmpty(tag)");
            return;
        }
        ViewCompat.setTransitionName(share, tag);
    }

    /**
     * Context启动activity
     */
    @SafeVarargs
    public static void start(Context from, Intent intent, Pair<View, String>... sharedElements) {
        if (from == null || intent == null) {
            Log.e(LOG_TAG, "start: from == null || intent == null");
            return;
        }
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
    public static void start(Fragment from, Intent intent, Pair<View, String>... sharedElements) {
        if (from == null || intent == null) {
            Log.e(LOG_TAG, "start: from == null || intent == null");
            return;
        }
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
    public static void startResult(Activity from, Intent intent, int requestCode, Pair<View, String>... sharedElements) {
        if (from == null || intent == null) {
            Log.e(LOG_TAG, "startResult: from == null || intent == null");
            return;
        }
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
    public static void startResult(Fragment from, Intent intent, int requestCode, Pair<View, String>... sharedElements) {
        if (from == null || intent == null) {
            Log.e(LOG_TAG, "startResult: from == null || intent == null");
            return;
        }
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
    public static void startResult2Fragment(Fragment from, Intent intent, int requestCode, Pair<View, String>... sharedElements) {
        if (from == null || intent == null) {
            Log.e(LOG_TAG, "startResult2Fragment: from == null || intent == null");
            return;
        }
        FragmentActivity activity = from.getActivity();
        if (activity == null) {
            Log.e(LOG_TAG, "startResultFragment() from.getActivity() == null");
            return;
        }
        if (sharedElements == null || sharedElements.length < 1) {
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
    private static void startByContext(Context from, Intent intent) {
        if (from == null || intent == null) {
            Log.e(LOG_TAG, "startByContext: from == null || intent == null");
            return;
        }
        ActivityStack.changeTask(intent);
        from.startActivity(intent);
    }

    /* 5.0过渡跳转(有分享元素) */
    @SafeVarargs
    private static void startWithElement(Activity from, Intent intent, Pair<View, String>... sharedElements) throws Exception {
        if (from == null || intent == null) {
            Log.e(LOG_TAG, "startWithElement: from == null || intent == null");
            return;
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(from, sharedElements);
        from.startActivity(intent, options.toBundle());
    }

    /* 5.0过渡跳转(有分享元素) */
    @SafeVarargs
    private static void startWithElement(Activity from, Intent intent, int requestCode, Pair<View, String>... sharedElements) throws Exception {
        if (from == null || intent == null) {
            Log.e(LOG_TAG, "startWithElement: from == null || intent == null");
            return;
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(from, sharedElements);
        ActivityCompat.startActivityForResult(from, intent, requestCode, options.toBundle());
    }

}
