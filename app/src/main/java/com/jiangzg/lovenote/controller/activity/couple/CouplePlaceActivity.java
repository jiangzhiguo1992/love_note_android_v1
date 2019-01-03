package com.jiangzg.lovenote.controller.activity.couple;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.couple.PlaceAdapter;
import com.jiangzg.lovenote.helper.LocationHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Place;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class CouplePlaceActivity extends BaseActivity<CouplePlaceActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private Call<Result> call;
    private int page;

    public static void goActivity(Fragment from) {
        if (!LocationHelper.checkLocationEnable(from)) return;
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), CouplePlaceActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_couple_place;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.place_info), true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new PlaceAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_white, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        PlaceAdapter placeAdapter = (PlaceAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.cvPlace:
                                placeAdapter.goPlaceDetail(position);
                                break;
                        }
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RecyclerHelper.release(recyclerHelper);
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        call = new RetrofitHelper().call(API.class).couplePlaceListGet(page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Place> placeList = data.getPlaceList();
                recyclerHelper.dataOk(placeList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
