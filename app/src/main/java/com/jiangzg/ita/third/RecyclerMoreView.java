package com.jiangzg.ita.third;

import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jiangzg.ita.R;

/**
 * Created by JZG on 2018/3/14.
 * 加载更多视图
 */
public class RecyclerMoreView extends LoadMoreView {

    @Override
    public int getLayoutId() {
        return R.layout.list_more_common;
    }

    @Override
    protected int getLoadingViewId() {
        return R.id.llLoading;
    }

    @Override
    protected int getLoadEndViewId() {
        return R.id.llLoadEnd;
    }

    @Override
    protected int getLoadFailViewId() {
        return R.id.llLoadFail;
    }

}
