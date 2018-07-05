package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.adapter.MapSearchAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.helper.LocationHelper;
import com.jiangzg.mianmian.helper.MapHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * 地址搜索
 */
public class MapSearchActivity extends BaseActivity<MapSearchActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;

    private RecyclerHelper recyclerHelper;
    private double longitude;
    private double latitude;
    private PoiSearch poiSearch;
    private PoiSearch.OnPoiSearchListener poiSearchListener;

    public static void goActivity(final Activity from) {
        if (!LocationHelper.checkLocationEnable(from)) return;
        Intent intent = new Intent(from, MapSearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(final Activity from, final double longitude, final double latitude) {
        if (!LocationHelper.checkLocationEnable(from)) return;
        Intent intent = new Intent(from, MapSearchActivity.class);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_map_search;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.address_search), true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new MapSearchAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        MapSearchAdapter mapAdapter = (MapSearchAdapter) adapter;
                        mapAdapter.select(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // 搜索中心点
        longitude = intent.getDoubleExtra("longitude", 0);
        latitude = intent.getDoubleExtra("latitude", 0);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
        if (poiSearch != null) {
            poiSearch.setOnPoiSearchListener(null);
        }
        poiSearchListener = null;
    }

    @OnTextChanged({R.id.etSearch})
    public void afterTextChanged(Editable s) {
        btnSearch.setEnabled(!StringUtils.isEmpty(s.toString()));
    }

    @OnClick({R.id.btnSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSearch: // 搜索
                search();
                break;
        }
    }

    private void search() {
        if (srl != null && !srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        String address = etSearch.getText().toString().trim();
        // 搜索回调
        if (poiSearchListener == null) {
            poiSearchListener = MapHelper.getPoiSearchListener(new MapHelper.SearchCallBack() {
                @Override
                public void onSuccess(ArrayList<PoiItem> pois) {
                    if (srl != null) srl.setRefreshing(false);
                    if (recyclerHelper != null) recyclerHelper.dataNew(pois);
                }

                @Override
                public void onFailed() {
                    if (srl != null) srl.setRefreshing(false);
                    ToastUtils.show(getString(R.string.location_search_fail));
                }
            });
        }
        // 开始poi检索
        poiSearch = MapHelper.startSearch(mActivity, address, longitude, latitude, poiSearchListener);
    }

}
