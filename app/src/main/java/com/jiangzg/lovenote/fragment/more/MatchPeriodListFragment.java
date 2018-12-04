package com.jiangzg.lovenote.fragment.more;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.MatchPeriodAdapter;
import com.jiangzg.lovenote.base.BaseFragment;
import com.jiangzg.lovenote.base.BasePagerFragment;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.MatchPeriod;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class MatchPeriodListFragment extends BasePagerFragment<MatchPeriodListFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private int kind;
    private RecyclerHelper recyclerHelper;
    private Call<Result> callGet;
    private int page;

    public static MatchPeriodListFragment newFragment(int kind) {
        Bundle bundle = new Bundle();
        bundle.putInt("kind", kind);
        return BaseFragment.newInstance(MatchPeriodListFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        kind = data.getInt("kind");
        return R.layout.fragment_match_period_list;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new MatchPeriodAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_white, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData(false);
                    }
                })
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        MatchPeriodAdapter periodAdapter = (MatchPeriodAdapter) adapter;
                        periodAdapter.getMatchWorkList(position);
                    }
                });
    }

    @Override
    protected void loadData() {
        page = 0;
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RecyclerHelper.release(recyclerHelper);
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        callGet = new RetrofitHelper().call(API.class).moreMatchPeriodListGet(kind, page);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<MatchPeriod> matchPeriodList = data.getMatchPeriodList();
                recyclerHelper.dataOk(matchPeriodList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
