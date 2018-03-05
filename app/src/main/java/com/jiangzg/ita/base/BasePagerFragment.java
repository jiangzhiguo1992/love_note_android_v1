package com.jiangzg.ita.base;

import android.os.Bundle;

import com.jiangzg.ita.third.LogUtils;

/**
 * Created by JZG on 2018/3/5.
 * ViewPager中的不预加载的Fragment
 */

public abstract class BasePagerFragment<M> extends BaseFragment<M> {

    private boolean isFirst = true;

    /**
     * 刷新数据
     */
    protected abstract void refreshData();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (!isVisibleToUser) return;
        if (isFirst) {
            isFirst = false;
            refreshData();
            LogUtils.e(getCls().getSimpleName() + "--->setUserVisibleHint");
        }
    }

    @Override
    protected void initData(Bundle state) {
        if (!getUserVisibleHint()) return;
        if (isFirst) {
            isFirst = false;
            refreshData();
            LogUtils.e(getCls().getSimpleName() + "--->initData");
        }
    }

}
