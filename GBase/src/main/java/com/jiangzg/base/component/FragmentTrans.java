package com.jiangzg.base.component;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.jiangzg.base.common.LogUtils;

/**
 * Created by Jiang on 2016/0/01
 * Fragment切换工具类
 */
public class FragmentTrans {

    /**
     * 添加，会遮挡主后面的
     */
    public static void add(FragmentManager manager, Fragment fragment, @IdRes int viewId) {
        add(manager, fragment, viewId, null, false);
    }

    public static void add(FragmentManager manager, Fragment fragment, @IdRes int viewId, String tag, boolean stack) {
        if (manager == null || fragment == null) {
            LogUtils.w(FragmentTrans.class, "add", "manager == null || fragment == null");
            return;
        }
        if (fragment.isAdded()) {
            LogUtils.i(FragmentTrans.class, "add", "isAdded = " + fragment.getClass().getSimpleName());
            return;
        }
        FragmentTransaction transaction = manager.beginTransaction();
        if (TextUtils.isEmpty(tag)) {
            transaction.add(viewId, fragment);
        } else if (viewId == 0) {
            transaction.add(fragment, tag);
        } else {
            transaction.add(viewId, fragment, tag);
        }
        commit(transaction, stack);
        LogUtils.i(FragmentTrans.class, "add", fragment.getClass().getSimpleName());
    }

    /**
     * remove会执行到detach
     */
    public static void remove(FragmentManager manager, Fragment fragment, boolean stack) {
        if (manager == null || fragment == null) {
            LogUtils.w(FragmentTrans.class, "remove", "manager == null || fragment == null");
            return;
        }
        if (!fragment.isAdded() || fragment.isRemoving()) {
            LogUtils.i(FragmentTrans.class, "remove", "noAdd = " + fragment.getClass().getSimpleName());
            return;
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        commit(transaction, stack);
        LogUtils.i(FragmentTrans.class, "remove", fragment.getClass().getSimpleName());
    }

    /**
     * 使用另一个Fragment替换当前的，实际上就是remove()然后add()的合体
     */
    public static void replace(FragmentManager manager, Fragment fragment, @IdRes int viewId) {
        replace(manager, fragment, viewId, null, false);
    }

    public static void replace(FragmentManager manager, Fragment fragment, @IdRes int viewId, String tag, boolean stack) {
        if (manager == null || fragment == null || viewId == 0) {
            LogUtils.w(FragmentTrans.class, "replace", "manager == null || fragment == null || viewId == 0");
            return;
        }
        if (fragment.isVisible()) {
            LogUtils.i(FragmentTrans.class, "replace", "isVisible = " + fragment.getClass().getSimpleName());
            return;
        }
        if (fragment.isAdded()) { // isAdd 则显示(但不会刷新)
            show(manager, fragment, stack);
            return;
        }
        FragmentTransaction transaction = manager.beginTransaction();
        if (TextUtils.isEmpty(tag)) {
            transaction.replace(viewId, fragment);
        } else {
            transaction.replace(viewId, fragment, tag);
        }
        commit(transaction, stack);
        LogUtils.i(FragmentTrans.class, "replace", fragment.getClass().getSimpleName());
    }

    /**
     * show出来之后还是之前的状态, 先hide后show，不hide的话，相当于只是遮挡
     * 只执行onPrepareOptionsMenu和onResume
     */
    public static void show(FragmentManager manager, Fragment fragment, boolean stack) {
        if (manager == null || fragment == null) {
            LogUtils.w(FragmentTrans.class, "show", "manager == null || fragment == null");
            return;
        }
        if (!fragment.isAdded() || !fragment.isHidden()) {
            LogUtils.i(FragmentTrans.class, "show", "isVisible = " + fragment.getClass().getSimpleName());
            return;
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.show(fragment);
        commit(transaction, stack);
        LogUtils.i(FragmentTrans.class, "show", fragment.getClass().getSimpleName());
    }

    /**
     * 可以保存状态, 先add/replace/show再hide
     * 只执行onPrepareOptionsMenu
     */
    public static void hide(FragmentManager manager, Fragment fragment, boolean stack) {
        if (manager == null || fragment == null) {
            LogUtils.w(FragmentTrans.class, "hide", "manager == null || fragment == null");
            return;
        }
        if (!fragment.isAdded() || fragment.isHidden()) {
            LogUtils.i(FragmentTrans.class, "hide", "isHidden = " + fragment.getClass().getSimpleName());
            return;
        }
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.hide(fragment);
        commit(transaction, stack);
        LogUtils.i(FragmentTrans.class, "hide", fragment.getClass().getSimpleName());
    }

    /* 事物提交 最后要commit */
    private static void commit(FragmentTransaction transaction, boolean stack) {
        if (transaction == null) {
            LogUtils.w(FragmentTrans.class, "commit", "transaction == null");
            return;
        }
        if (stack) {
            transaction.addToBackStack(null); // 加入栈
            //commit并不立即执行transaction中包含的动作,而是把它加入到UI线程队列中.
            //transaction.commit();
            //如果想要立即执行,可以在commit之后立即调用executePendingTransactions.
            //manager.executePendingTransactions();
            //commit必须在状态存储之前调用,否则会抛出异常,如果觉得状态丢失没关系,可以调用
            transaction.commitAllowingStateLoss();
        } else {
            // transaction.commitNow();
            transaction.commitNowAllowingStateLoss(); // 允许状态丢失
        }
    }

    /* onBackPress里已经处理了 */
    public static boolean goBack(FragmentManager manager) {
        if (manager == null) {
            LogUtils.w(FragmentTrans.class, "goBack", "manager == null");
            return false;
        }
        if (manager.getBackStackEntryCount() > 0) {
            LogUtils.i(FragmentTrans.class, "goBack", "-->");
            //manager.popBackStack(); // fragment栈中有fragment时，回退fragment
            manager.popBackStackImmediate();
            return true;
        }
        return false;
    }

    //@SafeVarargs
    //private static void addShareElement(FragmentTransaction transaction, Pair<View, String>... sharedElements) {
    //    if (sharedElements == null || sharedElements.length < 1) {
    //        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
    //    } else {
    //        for (Pair<View, String> element : sharedElements) { // 添加共享元素动画
    //            transaction.addSharedElement(element.first, element.second);
    //        }
    //    }
    //}

}
