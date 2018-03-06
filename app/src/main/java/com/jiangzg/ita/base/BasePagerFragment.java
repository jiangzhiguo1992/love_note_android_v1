package com.jiangzg.ita.base;

import android.os.Bundle;

/**
 * Created by JZG on 2018/3/5.
 * ViewPager中的不预加载的Fragment
 */

public abstract class BasePagerFragment<M> extends BaseFragment<M> {

    private boolean isFirst = true;
    private boolean canLoad = false;

    /**
     * 刷新数据
     */
    protected abstract void refreshData();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && canLoad && isFirst) {
            isFirst = false;
            refreshData();
        }
    }

    @Override
    protected void initData(Bundle state) {
        if (getUserVisibleHint() && isFirst) {
            isFirst = false;
            canLoad = true;
            refreshData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        canLoad = false;
    }
}
