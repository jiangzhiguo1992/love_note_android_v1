package com.android.base.component.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.Collections;
import java.util.List;

/**
 * Created by Jiang on 2016/0/01
 * Fragment工具类
 */
public class FragmentUtils {

    /**
     * 通过tag发现存在的fragment
     */
    public static Fragment find(FragmentManager manager, String tag) {
        return manager.findFragmentByTag(tag);
    }

    /**
     * 通过replaceId发现存在的fragment
     */
    public static Fragment find(FragmentManager manager, int replaceId) {
        return manager.findFragmentById(replaceId);
    }

    /**
     * 用于双层fragment里获取FragmentManager
     *
     * @param fragment 2层以上的fragment
     */
    public static FragmentManager getChildManager(Fragment fragment) {
        return fragment.getChildFragmentManager();
    }

    /**
     * 获取所有栈中的fragment
     */
    private static List<Fragment> getFragmentsInStack(FragmentManager manager) {
        if (manager == null) return Collections.emptyList();
        List<Fragment> fragments = manager.getFragments();
        if (fragments == null || fragments.isEmpty()) return Collections.emptyList();
        return fragments;
    }

}
