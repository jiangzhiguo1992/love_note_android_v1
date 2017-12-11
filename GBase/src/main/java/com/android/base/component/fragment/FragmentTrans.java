package com.android.base.component.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

/**
 * Created by Jiang on 2016/0/01
 * Fragment切换工具类
 */
public class FragmentTrans {

    /**
     * 添加，会遮挡主后面的
     */
    public static void add(FragmentManager manager, Fragment fragment, int viewId) {
        add(manager, fragment, viewId, "", false);
    }

    public static void add(FragmentManager manager, Fragment fragment,
                           int viewId, String tag, boolean stack) {
        if (fragment == null) return;
        if (fragment.isAdded()) return;
        FragmentTransaction transaction = manager.beginTransaction();
        if (TextUtils.isEmpty(tag)) {
            transaction.add(viewId, fragment);
        } else {
            transaction.add(viewId, fragment, tag);
        }
        commit(manager, transaction, stack, false);
    }

    /**
     * remove会执行到detach
     */
    public static void remove(FragmentManager manager, Fragment fragment, boolean stack) {
        if (fragment == null) return;
        if (!fragment.isAdded()) return;
        if (fragment.isRemoving()) return;
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.remove(fragment);
        commit(manager, transaction, stack, false);
    }

    /**
     * 使用另一个Fragment替换当前的，实际上就是remove()然后add()的合体
     */
    public static void replace(FragmentManager manager, Fragment fragment, int viewId) {
        replace(manager, fragment, viewId, "", false);
    }

    public static void replace(FragmentManager manager, Fragment fragment,
                               int viewId, String tag, boolean stack) {
        if (fragment == null) return;
        if (fragment.isVisible()) return;
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
        commit(manager, transaction, stack, false);
    }

    /**
     * show出来之后还是之前的状态, 先hide后show
     * 只执行onPrepareOptionsMenu和onResume
     */
    public static void show(FragmentManager manager, Fragment fragment, boolean stack) {
        if (fragment == null) return;
        if (!fragment.isAdded()) return;
        if (!fragment.isHidden()) return;
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.show(fragment);
        commit(manager, transaction, stack, false);
    }

    /**
     * 可以保存状态, 先add/replace/show再hide
     * 只执行onPrepareOptionsMenu
     */
    public static void hide(FragmentManager manager, Fragment fragment, boolean stack) {
        if (fragment == null) return;
        if (!fragment.isVisible()) return;
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.hide(fragment);
        commit(manager, transaction, stack, false);
    }
//
//    @SafeVarargs
//    private static void addShareElement(FragmentTransaction transaction,
//                                        Pair<View, String>... sharedElements) {
//        if (sharedElements == null || sharedElements.length < 1) {
//            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
//        } else {
//            for (Pair<View, String> element : sharedElements) { // 添加共享元素动画
//                transaction.addSharedElement(element.first, element.second);
//            }
//        }
//    }

    /* 事物提交 最后要commit */
    private static void commit(FragmentManager manager, FragmentTransaction transaction,
                               boolean stack, boolean multi) {
        if (stack) {
            transaction.addToBackStack(null); // 加入栈
            // transaction.commit();
            transaction.commitAllowingStateLoss(); // 允许状态丢失
            if (multi) { // 多次提交操作在同一个时间点一起执行
                manager.executePendingTransactions();
            }
        } else {
            // transaction.commitNow();
            transaction.commitNowAllowingStateLoss(); // 允许状态丢失
        }
    }

    /* onBackPress里已经处理了 */
    public static boolean goBack(FragmentManager manager) {
        if (manager != null && manager.getBackStackEntryCount() > 0) {
            manager.popBackStack(); // fragment栈中有fragment时，回退fragment
            // manager.popBackStackImmediate();
            return true;
        }
        return false;
    }

}
