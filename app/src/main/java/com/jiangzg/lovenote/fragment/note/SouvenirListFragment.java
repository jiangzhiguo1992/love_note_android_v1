package com.jiangzg.lovenote.fragment.note;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.SouvenirAdapter;
import com.jiangzg.lovenote.base.BaseFragment;
import com.jiangzg.lovenote.base.BasePagerFragment;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.entity.Result;
import com.jiangzg.lovenote.model.entity.RxEvent;
import com.jiangzg.lovenote.model.entity.Souvenir;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class SouvenirListFragment extends BasePagerFragment<SouvenirListFragment> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private boolean done;
    private RecyclerHelper recyclerHelper;
    private Observable<List<Souvenir>> obListRefresh;
    private Observable<Souvenir> obListItemDelete;
    private Observable<Souvenir> obListItemRefresh;
    private Call<Result> call;
    private int page;

    public static SouvenirListFragment newFragment(boolean done) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("done", done);
        return BaseFragment.newInstance(SouvenirListFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        done = data.getBoolean("done");
        return R.layout.fragment_souvenir_list;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new SouvenirAdapter(mFragment, done))
                .viewEmpty(mActivity, R.layout.list_empty_white, true, true)
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
                        SouvenirAdapter souvenirAdapter = (SouvenirAdapter) adapter;
                        souvenirAdapter.goSouvenirDetail(position);
                    }
                });
    }

    @Override
    protected void loadData() {
        page = 0;
        // event
        obListRefresh = RxBus.register(ConsHelper.EVENT_SOUVENIR_LIST_REFRESH, new Action1<List<Souvenir>>() {
            @Override
            public void call(List<Souvenir> souvenirList) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataRefresh();
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_SOUVENIR_LIST_ITEM_DELETE, new Action1<Souvenir>() {
            @Override
            public void call(Souvenir souvenir) {
                if (recyclerHelper == null) return;
                ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), souvenir);
            }
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_SOUVENIR_LIST_ITEM_REFRESH, new Action1<Souvenir>() {
            @Override
            public void call(Souvenir souvenir) {
                if (recyclerHelper == null || souvenir == null) return;
                if (souvenir.isDone() == done) {
                    // 没改变状态则刷新item
                    ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), souvenir);
                } else {
                    // 改变done则刷新list,两个fragment都要刷
                    RxEvent<ArrayList<Souvenir>> event = new RxEvent<>(ConsHelper.EVENT_SOUVENIR_LIST_REFRESH, new ArrayList<Souvenir>());
                    RxBus.post(event);
                }
            }
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RxBus.unregister(ConsHelper.EVENT_SOUVENIR_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_SOUVENIR_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(ConsHelper.EVENT_SOUVENIR_LIST_ITEM_REFRESH, obListItemRefresh);
        RecyclerHelper.release(recyclerHelper);
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        call = new RetrofitHelper().call(API.class).noteSouvenirListGet(done, page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Souvenir> souvenirList = data.getSouvenirList();
                recyclerHelper.dataOk(souvenirList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(false, message);
            }
        });
    }

}
