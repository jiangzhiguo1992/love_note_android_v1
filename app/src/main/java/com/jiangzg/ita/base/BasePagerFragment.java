package com.jiangzg.ita.base;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;

import com.jiangzg.base.view.BarUtils;

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

    // 沉浸式状态栏适配
    protected void fitToolBar(Toolbar tb) {
        int statusBarHeight = BarUtils.getStatusBarHeight(mActivity);
        AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) tb.getLayoutParams();
        params.setMargins(params.leftMargin, params.topMargin + statusBarHeight, params.rightMargin, params.bottomMargin);
        tb.setLayoutParams(params);
    }
}
