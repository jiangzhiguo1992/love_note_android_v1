package com.jiangzg.mianmian.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.SouvenirEditActivity;
import com.jiangzg.mianmian.adapter.SouvenirDoneAdapter;
import com.jiangzg.mianmian.base.BaseFragment;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.Souvenir;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class SouvenirListDoneFragment extends BaseFragment<SouvenirListDoneFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    private RecyclerHelper recyclerHelper;
    private Observable<List<Souvenir>> obListRefresh;
    private Observable<Souvenir> obListItemDelete;
    private Observable<Souvenir> obListItemRefresh;
    private Call<Result> call;

    public static SouvenirListDoneFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(SouvenirListDoneFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_souvenir_list_done;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new SouvenirDoneAdapter(mFragment))
                .viewEmpty(mActivity, R.layout.list_empty_white, true, true)
                .setAdapter()
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshData();
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        SouvenirDoneAdapter souvenirDoneAdapter = (SouvenirDoneAdapter) adapter;
                        souvenirDoneAdapter.goSouvenirDoneDetail(position);
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        // event
        obListRefresh = RxBus.register(ConsHelper.EVENT_SOUVENIR_DONE_LIST_REFRESH, new Action1<List<Souvenir>>() {
            @Override
            public void call(List<Souvenir> souvenirList) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataRefresh();
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_SOUVENIR_DONE_LIST_ITEM_DELETE, new Action1<Souvenir>() {
            @Override
            public void call(Souvenir souvenir) {
                if (recyclerHelper == null) return;
                ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), souvenir);
            }
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_SOUVENIR_DONE_LIST_ITEM_REFRESH, new Action1<Souvenir>() {
            @Override
            public void call(Souvenir souvenir) {
                if (recyclerHelper == null) return;
                ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), souvenir);
            }
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
        RetrofitHelper.cancel(call);
        RxBus.unregister(ConsHelper.EVENT_SOUVENIR_DONE_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_SOUVENIR_DONE_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(ConsHelper.EVENT_SOUVENIR_DONE_LIST_ITEM_REFRESH, obListItemRefresh);
    }

    @OnClick({R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd: // 添加
                SouvenirEditActivity.goActivity(mFragment, true);
                break;
        }
    }

    private void refreshData() {
        call = new RetrofitHelper().call(API.class).souvenirListGet(true);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Souvenir> souvenirList = data.getSouvenirList();
                recyclerHelper.dataNew(souvenirList);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(false, message);
            }
        });
    }

}
