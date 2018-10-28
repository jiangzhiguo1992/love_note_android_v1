package com.jiangzg.base.component;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.Pair;
import android.view.View;

import com.jiangzg.base.common.LogUtils;

/**
 * Created by JiangZhiGuo on 2016/10/13.
 * describe Activity跳转工具类
 */
public class ActivityTrans {

    /**
     * Context启动activity
     * 要分享元素过渡动画 目标activity调用 ViewCompat.setTransitionName(share, tag);
     */
    @SafeVarargs
    public static void start(Context from, Intent intent, Pair<View, String>... sharedElements) {
        if (from == null || intent == null) {
            LogUtils.w(ActivityTrans.class, "start", "Context == null || intent == null");
            return;
        }
        if (from instanceof Activity) {
            Activity activity = (Activity) from;
            if (sharedElements == null || sharedElements.length < 1) {
                try {
                    activity.startActivity(intent);
                } catch (Exception e) {
                    LogUtils.e(ActivityTrans.class, "start", e);
                    if (e instanceof ActivityNotFoundException) return;
                    startByContext(from, intent);
                }
            } else {
                try {
                    startWithElement(activity, intent, sharedElements);
                } catch (Exception e) {
                    LogUtils.e(ActivityTrans.class, "start", e);
                    if (e instanceof ActivityNotFoundException) return;
                    activity.startActivity(intent);
                }
            }
        } else {
            try {
                startByContext(from, intent);
            } catch (Exception e) {
                LogUtils.e(ActivityTrans.class, "start", e);
            }
        }
    }

    /**
     * fragment启动activity
     */
    @SafeVarargs
    public static void start(Fragment from, Intent intent, Pair<View, String>... sharedElements) {
        if (from == null || intent == null) {
            LogUtils.w(ActivityTrans.class, "start", "Fragment == null || Intent == null");
            return;
        }
        if (from.getActivity() == null || sharedElements == null || sharedElements.length < 1) {
            try {
                from.startActivity(intent);
            } catch (Exception e) {
                LogUtils.e(ActivityTrans.class, "start", e);
            }
        } else {
            try {
                startWithElement(from.getActivity(), intent, sharedElements);
            } catch (Exception e) {
                LogUtils.e(ActivityTrans.class, "start", e);
                if (e instanceof ActivityNotFoundException) return;
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
            LogUtils.w(ActivityTrans.class, "startResult", "Activity == null || Intent == null");
            return;
        }
        if (sharedElements == null || sharedElements.length < 1) {
            try {
                from.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                LogUtils.e(ActivityTrans.class, "startResult", e);
            }
        } else {
            try {
                startWithElement(from, intent, requestCode, sharedElements);
            } catch (Exception e) {
                LogUtils.e(ActivityTrans.class, "startResult", e);
                if (e instanceof ActivityNotFoundException) return;
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
            LogUtils.w(ActivityTrans.class, "startResult", "Fragment == null || Intent == null");
            return;
        }
        if (from.getActivity() == null || sharedElements == null || sharedElements.length < 1) {
            try {
                from.startActivityForResult(intent, requestCode);
            } catch (Exception e) {
                LogUtils.e(ActivityTrans.class, "startResult", e);
            }
        } else {
            try {
                startWithElement(from.getActivity(), intent, requestCode, sharedElements);
            } catch (Exception e) {
                LogUtils.e(ActivityTrans.class, "startResult", e);
                if (e instanceof ActivityNotFoundException) return;
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
            LogUtils.w(ActivityTrans.class, "startResult2Fragment", "Fragment == null || Intent == null");
            return;
        }
        FragmentActivity activity = from.getActivity();
        if (activity == null) {
            LogUtils.w(ActivityTrans.class, "startResult2Fragment", "FragmentActivity == null");
            return;
        }
        if (sharedElements == null || sharedElements.length < 1) {
            try {
                activity.startActivityFromFragment(from, intent, requestCode);
            } catch (Exception e) {
                LogUtils.e(ActivityTrans.class, "startResult2Fragment", e);
            }
        } else {
            try {
                startWithElement(activity, intent, requestCode, sharedElements);
            } catch (Exception e) {
                LogUtils.e(ActivityTrans.class, "startResult2Fragment", e);
                if (e instanceof ActivityNotFoundException) return;
                activity.startActivityFromFragment(from, intent, requestCode);
            }
        }
    }

    /* context启动activity */
    private static void startByContext(Context from, Intent intent) {
        if (from == null || intent == null) {
            LogUtils.w(ActivityTrans.class, "startByContext", "from == null || intent == null");
            return;
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 新栈
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION); // 不要动画
        from.startActivity(intent);
    }

    /* 5.0过渡跳转(有分享元素) */
    @SafeVarargs
    private static void startWithElement(Activity from, Intent intent, Pair<View, String>... sharedElements) throws Exception {
        if (from == null || intent == null) {
            LogUtils.w(ActivityTrans.class, "startWithElement", "from == null || intent == null");
            return;
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(from, sharedElements);
        from.startActivity(intent, options.toBundle());
    }

    /* 5.0过渡跳转(有分享元素) */
    @SafeVarargs
    private static void startWithElement(Activity from, Intent intent, int requestCode, Pair<View, String>... sharedElements) throws Exception {
        if (from == null || intent == null) {
            LogUtils.w(ActivityTrans.class, "startWithElement", "from == null || intent == null");
            return;
        }
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(from, sharedElements);
        ActivityCompat.startActivityForResult(from, intent, requestCode, options.toBundle());
    }

}
