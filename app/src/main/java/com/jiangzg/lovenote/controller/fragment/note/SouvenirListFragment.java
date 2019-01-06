package com.jiangzg.lovenote.controller.fragment.note;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.adapter.note.SouvenirAdapter;
import com.jiangzg.lovenote.controller.fragment.base.BaseFragment;
import com.jiangzg.lovenote.controller.fragment.base.BasePagerFragment;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Souvenir;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;

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
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
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
        obListRefresh = RxBus.register(RxBus.EVENT_SOUVENIR_LIST_REFRESH, souvenirList -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        obListItemDelete = RxBus.register(RxBus.EVENT_SOUVENIR_LIST_ITEM_DELETE, souvenir -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), souvenir);
        });
        obListItemRefresh = RxBus.register(RxBus.EVENT_SOUVENIR_LIST_ITEM_REFRESH, souvenir -> {
            if (recyclerHelper == null || souvenir == null) return;
            if (souvenir.isDone() == done) {
                // 没改变状态则刷新item
                ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), souvenir);
            } else {
                // 改变done则刷新list,两个fragment都要刷
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SOUVENIR_LIST_REFRESH, new ArrayList<>()));
            }
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RxBus.unregister(RxBus.EVENT_SOUVENIR_LIST_REFRESH, obListRefresh);
        RxBus.unregister(RxBus.EVENT_SOUVENIR_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(RxBus.EVENT_SOUVENIR_LIST_ITEM_REFRESH, obListItemRefresh);
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
