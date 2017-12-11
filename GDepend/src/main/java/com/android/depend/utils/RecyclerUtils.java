package com.android.depend.utils;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.loadmore.LoadMoreView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * describe RecyclerViewAdapter管理类
 */
public class RecyclerUtils {

    private Context mContext;
    private RecyclerView mRecycler;
    private RecyclerView.LayoutManager mLayoutManager;
    private BaseQuickAdapter mAdapter;
    private SwipeRefreshLayout mRefresh;
    private RefreshListener mRefreshListener;
    private MoreListener mMoreListener;
    private LoadMoreView mLoading;
    private View mEmpty, mHead, mFoot;

    public RecyclerUtils(Context context) {
        mContext = context;
    }

    /**
     * ************************************初始化***************************************
     */
    public RecyclerUtils initRecycler(RecyclerView recyclerView) {
        mRecycler = recyclerView;
        return this;
    }

    /**
     * 布局管理器
     */
    public RecyclerUtils initLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (mRecycler == null || layoutManager == null) return this;
        mLayoutManager = layoutManager;
        mRecycler.setLayoutManager(mLayoutManager);
        return this;
    }

    /**
     * SwipeRefreshLayout
     */
    public RecyclerUtils initRefresh(SwipeRefreshLayout srl) {
        mRefresh = srl;
        return this;
    }

    /**
     * 设置适配器，http获取数据
     */
    public RecyclerUtils initAdapter(BaseQuickAdapter adapter) {
        if (adapter == null) return this;
        mAdapter = adapter;
        return this;
    }

    /**
     * 直接设置适配器，本地数据
     */
    public RecyclerUtils setAdapter(BaseQuickAdapter adapter) {
        if (mRecycler == null || adapter == null) return this;
        mAdapter = adapter;
        mRecycler.setAdapter(mAdapter);
        return this;
    }

    /**
     * ************************************VIEW***************************************
     * 无Data时显示的view
     */
    public RecyclerUtils viewEmpty(int emptyLayoutId) {
        if (mRecycler == null || mContext == null || emptyLayoutId == 0) return this;
        View empty = LayoutInflater.from(mContext).inflate(emptyLayoutId, mRecycler, false);
        viewEmpty(empty);
        return this;
    }

    public RecyclerUtils viewEmpty(View empty) {
        if (mAdapter == null || empty == null) return this;
        mEmpty = empty;
        mAdapter.setEmptyView(mEmpty);
        return this;
    }

    /**
     * list顶部view（可remove）
     */
    public RecyclerUtils viewHeader(int headLayoutId) {
        if (mContext == null || mRecycler == null) return this;
        View head = LayoutInflater.from(mContext).inflate(headLayoutId, mRecycler, false);
        viewHeader(head);
        return this;
    }

    public RecyclerUtils viewHeader(View head) {
        if (mAdapter == null || head == null) return this;
        mHead = head;
        mAdapter.setHeaderView(mHead);
        return this;
    }

    /**
     * list底部view（可remove）
     */
    public RecyclerUtils viewFooter(int footLayoutId) {
        if (mContext == null || mRecycler == null) return this;
        View foot = LayoutInflater.from(mContext).inflate(footLayoutId, mRecycler, false);
        viewFooter(foot);
        return this;
    }

    public RecyclerUtils viewFooter(View foot) {
        if (mAdapter == null || foot == null) return this;
        mFoot = foot;
        mAdapter.setFooterView(mFoot);
        return this;
    }

    /**
     * 加载更多视图
     */
    public RecyclerUtils viewLoading(int loadingLayoutId) {
        if (mContext == null || mRecycler == null) return this;
        View head = LayoutInflater.from(mContext).inflate(loadingLayoutId, mRecycler, false);
        viewHeader(head);
        return this;
    }

    public RecyclerUtils viewLoading(LoadMoreView loading) {
        if (mAdapter == null || loading == null) return this;
        mLoading = loading;
        mAdapter.setLoadMoreView(mLoading);
        return this;
    }

    /**
     * item动画
     */
    public RecyclerUtils viewAnim(int animType) {
        if (mAdapter == null) return this;
        mAdapter.openLoadAnimation(animType);
        mAdapter.isFirstOnly(true);
        return this;
    }

    /**
     * ************************************监听器***************************************
     * RecyclerView的item点击监听
     */
    public RecyclerUtils listenerClick(RecyclerView.OnItemTouchListener listener) {
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
    public RecyclerUtils listenerMore(final MoreListener listener) {
        if (mRecycler == null || mAdapter == null || listener == null) return this;
        mMoreListener = listener;
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
    public RecyclerUtils listenerRefresh(RefreshListener listener) {
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
            mAdapter.loadMoreEnd(true); // 关闭更多
        } else {
            mAdapter.setNewData(list);
            if (list.size() >= totalCount) {
                mAdapter.loadMoreEnd(true); // 关闭更多
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
            if (mAdapter.getItemCount() >= totalCount) {
                mAdapter.loadMoreEnd(); // 关闭更多
            }
        }
        if (null != mRefresh) { // 停止刷新
            mRefresh.setRefreshing(false);
        }
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
        viewLoading(mLoading);
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

}
