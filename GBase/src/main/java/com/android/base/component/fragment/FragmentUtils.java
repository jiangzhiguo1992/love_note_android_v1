package com.android.base.component.fragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jiang on 2016/0/01
 * Fragment工具类
 */
public class FragmentUtils {

    private static final String LOG_TAG = "FragmentUtils";

    /**
     * 通过tag发现存在的fragment
     */
    public static Fragment find(@NonNull FragmentManager manager, String tag) {
        return manager.findFragmentByTag(tag);
    }

    /**
     * 通过replaceId发现存在的fragment
     */
    public static Fragment find(@NonNull FragmentManager manager, int replaceId) {
        return manager.findFragmentById(replaceId);
    }

    /**
     * 用于双层fragment里获取FragmentManager
     *
     * @param fragment 2层以上的fragment
     */
    public static FragmentManager getChildManager(@NonNull Fragment fragment) {
        return fragment.getChildFragmentManager();
    }

    /**
     * 获取所有栈中的fragment
     */
    public static List<Fragment> getFragmentsInStack(@NonNull FragmentManager manager) {
        List<Fragment> fragments = manager.getFragments();
        if (fragments == null || fragments.isEmpty()) {
            fragments = Collections.emptyList();
        }
        Log.d(LOG_TAG, "getFragmentsInStack->" + fragments.size());
        return fragments;
    }

}
