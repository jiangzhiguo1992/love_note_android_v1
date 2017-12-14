package com.jiangzg.base.component.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.jiangzg.base.common.StringUtils;

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
    public static Fragment find(FragmentManager manager, String tag) {
        if (manager == null || StringUtils.isEmpty(tag)) {
            Log.e(LOG_TAG, "find: manager == null || tag == null");
            return null;
        }
        return manager.findFragmentByTag(tag);
    }

    /**
     * 通过replaceId发现存在的fragment
     */
    public static Fragment find(FragmentManager manager, int replaceId) {
        if (manager == null) {
            Log.e(LOG_TAG, "find: manager == null ");
            return null;
        }
        return manager.findFragmentById(replaceId);
    }

    /**
     * 用于双层fragment里获取FragmentManager
     *
     * @param fragment 2层以上的fragment
     */
    public static FragmentManager getChildManager(Fragment fragment) {
        if (fragment == null) {
            Log.e(LOG_TAG, "getChildManager: fragment == null ");
            return null;
        }
        return fragment.getChildFragmentManager();
    }

    /**
     * 获取所有栈中的fragment
     */
    public static List<Fragment> getFragmentsInStack(FragmentManager manager) {
        if (manager == null) {
            Log.e(LOG_TAG, "getFragmentsInStack: manager == null ");
            return null;
        }
        List<Fragment> fragments = manager.getFragments();
        if (fragments == null || fragments.isEmpty()) {
            fragments = Collections.emptyList();
        }
        Log.d(LOG_TAG, "getFragmentsInStack->" + fragments.size());
        return fragments;
    }

}
