package com.jiangzg.ita.third;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;
import com.jiangzg.ita.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * describe RecyclerViewAdapter管理类
 */
public class RecyclerHelper {

    private Context mContext;
    private RecyclerView mRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private BaseQuickAdapter mAdapter;
    private SwipeRefreshLayout mRefresh;
    private RefreshListener mRefreshListener;
    private MoreListener mMoreListener;
    private LoadMoreView mLoadMore;
    private View mEmpty, mHead, mFoot;

    public RecyclerHelper(Context context) {
        mContext = context;
    }

    /**
     * ************************************初始化***************************************
     */
    public RecyclerHelper initRecycler(RecyclerView recyclerView) {
        mRecycler = recyclerView;
        return this;
    }

    /**
     * 布局管理器
     */
    public RecyclerHelper initLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (mRecycler == null || layoutManager == null) return this;
        mLayoutManager = layoutManager;
        mRecycler.setLayoutManager(mLayoutManager);
        return this;
    }

    /**
     * SwipeRefreshLayout
     */
    public RecyclerHelper initRefresh(SwipeRefreshLayout srl, boolean enable) {
        if (srl == null) return this;
        mRefresh = srl;
        mRefresh.setEnabled(enable);
        return this;
    }

    /**
     * 在viewFunc之前设置
     */
    public RecyclerHelper initAdapter(BaseQuickAdapter adapter) {
        mAdapter = adapter;
        return this;
    }

    /**
     * 在viewFunc之后设置，也可以等data加载完后自动调用
     */
    public RecyclerHelper setAdapter() {
        if (mRecycler == null || mAdapter == null) return this;
        mRecycler.setAdapter(mAdapter);
        return this;
    }

    /**
     * ************************************VIEW***************************************
     * 无Data时显示的view
     */
    public RecyclerHelper viewEmpty(int emptyLayoutId, boolean head, boolean foot) {
        if (mRecycler == null || mContext == null || emptyLayoutId == 0) return this;
        View empty = LayoutInflater.from(mContext).inflate(emptyLayoutId, mRecycler, false);
        return viewEmpty(empty, head, foot);
    }

    public RecyclerHelper viewEmpty(View empty, boolean head, boolean foot) {
        if (mAdapter == null || empty == null) return this;
        mEmpty = empty;
        mAdapter.setEmptyView(mEmpty);
        mAdapter.setHeaderFooterEmpty(head, foot); // Empty、Head、Foot共存
        return this;
    }

    public void viewEmptyShow(@IdRes int tvId, String show) {
        if (mAdapter == null || mEmpty == null) return;
        TextView tvShow = mEmpty.findViewById(tvId);
        if (tvShow == null) return;
        tvShow.setText(show);
    }

    public void viewEmptyShow() {
        viewEmptyShow(R.id.tvEmptyShow, mContext.getString(R.string.master_there_nothing));
    }

    /**
     * list顶部view（可remove）
     */
    public RecyclerHelper viewHeader(int headLayoutId) {
        if (mContext == null || mRecycler == null) return this;
        View head = LayoutInflater.from(mContext).inflate(headLayoutId, mRecycler, false);
        return viewHeader(head);
    }

    public RecyclerHelper viewHeader(View head) {
        if (mAdapter == null || head == null) return this;
        mHead = head;
        mAdapter.setHeaderView(mHead);
        //mAdapter.setHeaderViewAsFlow(true);
        return this;
    }

    public View getViewHead() {
        return mHead;
    }

    /**
     * list底部view（可remove）
     */
    public RecyclerHelper viewFooter(int footLayoutId) {
        if (mContext == null || mRecycler == null) return this;
        View foot = LayoutInflater.from(mContext).inflate(footLayoutId, mRecycler, false);
        return viewFooter(foot);
    }

    public RecyclerHelper viewFooter(View foot) {
        if (mAdapter == null || foot == null) return this;
        mFoot = foot;
        mAdapter.setFooterView(mFoot);
        //mAdapter.setFooterViewAsFlow(true);
        return this;
    }

    public void removeFoot(View foot) {
        if (mAdapter == null) return;
        mAdapter.removeFooterView(foot);
        mFoot = null;
    }

    public View getViewFoot() {
        return mFoot;
    }

    /**
     * 加载更多视图
     */
    public RecyclerHelper viewLoadMore(LoadMoreView loadMore) {
        if (mAdapter == null || loadMore == null) return this;
        mLoadMore = loadMore;
        mAdapter.setLoadMoreView(mLoadMore);
        return this;
    }

    /**
     * item动画
     */
    public RecyclerHelper viewAnim(int animType) {
        if (mAdapter == null) return this;
        mAdapter.openLoadAnimation(animType);
        mAdapter.isFirstOnly(true);
        return this;
    }

    /**
     * ************************************监听器***************************************
     * RecyclerView的item点击监听
     */
    public RecyclerHelper listenerClick(RecyclerView.OnItemTouchListener listener) {
        if (mRecycler == null || listener == null) return this;
        mRecycler.addOnItemTouchListener(listener);
        return this;
    }

    public interface MoreListener {
        void onMore(int currentCount);
    }

    /**
     * 加载更多监听 回调时addData() 记得offset叠加
     */
    public RecyclerHelper listenerMore(final MoreListener listener) {
        if (mRecycler == null || mAdapter == null || listener == null) return this;
        mMoreListener = listener;
        mAdapter.setEnableLoadMore(true);
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                mRecycler.post(new Runnable() {
                    @Override
                    public void run() {
                        mMoreListener.onMore(mAdapter.getItemCount());
                    }
                });
            }
        }, mRecycler);
        return this;
    }

    public interface RefreshListener {
        void onRefresh();
    }

    /**
     * 刷新监听 回调时newData() 记得offset重置
     */
    public RecyclerHelper listenerRefresh(RefreshListener listener) {
        if (mRefresh == null || listener == null) return this;
        mRefreshListener = listener;
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mRefresh.post(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshListener.onRefresh();
                    }
                });
            }
        });
        return this;
    }

    /**
     * ************************************数据***************************************
     * 主动刷新 页面进入时调用 记得offset重置
     */
    public void dataRefresh() {
        if (mRefresh == null || mRefreshListener == null) return;
        mRefresh.post(new Runnable() {
            @Override
            public void run() {
                mRefresh.setRefreshing(true); // 执行等待动画
                mRefreshListener.onRefresh();
            }
        });
    }

    public void data(List list, boolean more) {
        if (list == null) return;
        data(list, list.size(), more);
    }

    public void data(List list, int totalCount, boolean more) {
        if (more) {
            dataAdd(list, totalCount);
        } else {
            dataNew(list, totalCount);
        }
    }

    /**
     * 刷新数据
     */
    public void dataNew(List list) {
        if (list == null) return;
        dataNew(list, list.size());
    }

    public void dataNew(List list, int totalCount) {
        if (mAdapter == null) return;
        if (null == list) {
            mAdapter.setNewData(new ArrayList());
            mAdapter.loadMoreEnd(false); // 显示没有更多
        } else {
            mAdapter.setNewData(list);
            if (list.size() >= totalCount) {
                mAdapter.loadMoreEnd(false); // 显示没有更多
            }
        }
        if (null != mRefresh) { // 停止刷新
            mRefresh.setRefreshing(false);
        }
        if (mRecycler == null) return; // 提前加载空adapter会有loadMore的标示
        if (mRecycler.getAdapter() == null) {
            mRecycler.setAdapter(mAdapter);
        }
    }

    /**
     * 更多数据
     */
    public void dataAdd(List list) {
        if (list == null) return;
        dataAdd(list, list.size());
    }

    public void dataAdd(List list, int totalCount) {
        if (mAdapter == null) return;
        mAdapter.loadMoreComplete();
        if (null != list) {
            mAdapter.addData(list);
            if (mAdapter.getData().size() >= totalCount) {
                mAdapter.loadMoreEnd(false); // 显示没有更多
            }
        }
        if (null != mRefresh) { // 停止刷新
            mRefresh.setRefreshing(false);
        }
        if (mRecycler == null) return; // 提前加载空adapter会有loadMore的标示
        if (mRecycler.getAdapter() == null) {
            mRecycler.setAdapter(mAdapter);
        }
    }

    public void dataAddFail() {
        mAdapter.loadMoreFail();
    }

    /**
     * ************************************变更***************************************
     * 变更布局管理器，需要紧接着changeAdapter
     */
    public void changeLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (mRecycler == null || layoutManager == null) return;
        mLayoutManager = layoutManager;
        mRecycler.setAdapter(null);
        mRecycler.setLayoutManager(mLayoutManager);
    }

    /**
     * 变更适配器，空数据的适配器刚开始会有加载更多的提示
     */
    public void changeAdapter(BaseQuickAdapter adapter) {
        if (mRecycler == null || adapter == null) return;
        mAdapter = adapter;
        mRecycler.setAdapter(mAdapter);
        viewLoadMore(mLoadMore);
//        viewEmpty(mEmpty); 重新设置
        viewHeader(mHead);
        viewFooter(mFoot);
        listenerRefresh(mRefreshListener);
//        listenerClick(mClickListener); recycler会重复
        listenerMore(mMoreListener);
    }

    /**
     * ************************************实例获取***************************************
     */
    @SuppressWarnings("unchecked")
    public <A extends BaseQuickAdapter> A getAdapter() {
        return (A) mAdapter;
    }

    @SuppressWarnings("unchecked")
    public <A extends RecyclerView.LayoutManager> A getLayoutManager() {
        return (A) mLayoutManager;
    }

    @SuppressWarnings("unchecked")
    public <A extends BaseViewHolder> A getHolder(int position) {
        if (mRecycler == null) return null;
        return (A) mRecycler.findViewHolderForLayoutPosition(position);
    }

    public static class RecyclerMoreView extends LoadMoreView {

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

}
