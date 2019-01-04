package com.jiangzg.lovenote.controller.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.common.MapSelectAdapter;
import com.jiangzg.lovenote.helper.LocationHelper;
import com.jiangzg.lovenote.helper.MapHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Observable;

/**
 * 地址选择
 */
public class MapSelectActivity extends BaseActivity<MapSelectActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.cvLocation)
    CardView cvLocation;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private AMap aMap;
    private LocationInfo locationMe;
    private LocationInfo locationSearch;
    private LocationInfo locationSelect;
    private PoiSearch poiSearch;
    private PoiSearch.OnPoiSearchListener poiSearchListener;
    private Observable<LocationInfo> obMapSearch;
    private RecyclerHelper recyclerHelper;
    private boolean moveWithSearch;

    public static void goActivity(final Activity from) {
        if (!LocationHelper.checkLocationEnable(from)) return;
        Intent intent = new Intent(from, MapSelectActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(final Activity from, final String address, final double longitude, final double latitude) {
        if (!LocationHelper.checkLocationEnable(from)) return;
        Intent intent = new Intent(from, MapSelectActivity.class);
        intent.putExtra("address", address);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_map_select;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.please_select_address), true);
        // map
        if (map != null) {
            map.onCreate(null);
        }
        if (aMap == null && map != null) {
            aMap = map.getMap();
        }
        // uiSettings
        if (aMap == null) return;
        MapHelper.initMapView(aMap);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new MapSelectAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        MapSelectAdapter mapAdapter = (MapSelectAdapter) adapter;
                        LocationInfo locationSelect = mapAdapter.select(position);
                        // 修改选中位置，移动，但不搜索
                        setLocationSelect(locationSelect, true, false);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        moveWithSearch = true;
        if (aMap == null) return;
        // 地图拖动回调
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                LatLng target = cameraPosition.target;
                if (target == null) return;
                // 修改搜索列表
                LogUtils.i(MapSelectActivity.class, "onCameraChangeFinish", target.longitude + "---" + target.latitude);
                if (moveWithSearch) {
                    LocationInfo info = new LocationInfo();
                    info.setLongitude(target.longitude);
                    info.setLatitude(target.latitude);
                    setLocationSearch(info);
                }
                moveWithSearch = true; // 默认是要搜索的
            }
        });
        // 地图搜索
        obMapSearch = RxBus.register(RxBus.EVENT_MAP_SELECT, info -> {
            // 修改选中位置
            //setLocationSelect(info, true, true);
            mActivity.finish();
        });
        // 传入的位置
        LocationInfo info = null;
        String address = intent.getStringExtra("address");
        double longitude = intent.getDoubleExtra("longitude", 0);
        double latitude = intent.getDoubleExtra("latitude", 0);
        if (!StringUtils.isEmpty(address) && (longitude != 0 && latitude != 0)) {
            info = new LocationInfo();
            info.setAddress(address);
            info.setLongitude(longitude);
            info.setLatitude(latitude);
            setLocationSelect(info, true, true);
        }
        // 我的位置
        startMyLocation(info == null);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
        RxBus.unregister(RxBus.EVENT_MAP_SELECT, obMapSearch);
        if (poiSearch != null) {
            poiSearch.setOnPoiSearchListener(null);
        }
        poiSearchListener = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_complete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (map != null) {
            map.onPause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (map != null) {
            map.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (map != null) {
            map.onDestroy();
            map = null;
        }
        if (aMap != null) {
            aMap.removecache();
            aMap.clear();
            aMap = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSearch: // 搜索
                MapSearchActivity.goActivity(mActivity, 0, 0);
                return true;
            case R.id.menuComplete: // 完成
                if (locationSelect == null) {
                    ToastUtils.show(getString(R.string.please_select_address));
                    return true;
                }
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_MAP_SELECT, locationSelect));
                //mActivity.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvLocation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvLocation: // 定位
                setLocationSelect(locationMe, true, true);
                break;
        }
    }

    // 开始我的定位
    private void startMyLocation(final boolean move) {
        LocationHelper.startLocation(mActivity, true, new LocationHelper.LocationCallBack() {
            @Override
            public void onSuccess(LocationInfo info) {
                if (info == null) return;
                locationMe = info;
                if (move) {
                    setLocationSelect(locationMe, true, true);
                }
            }

            @Override
            public void onFailed(String errMsg) {
                ToastUtils.show(getString(R.string.location_error));
            }
        });
    }

    // 修改选中位置，地图移动 + 搜索列表
    private void setLocationSelect(LocationInfo info, boolean move, boolean search) {
        locationSelect = info;
        if (locationSelect == null) return;
        if (move) {
            moveWithSearch = search;
            MapHelper.moveMapByLatLon(aMap, locationSelect.getLongitude(), locationSelect.getLatitude());
        }
    }

    // 修改搜索列表
    private void setLocationSearch(LocationInfo info) {
        if (aMap == null) return;
        if (srl != null && !srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        if (info != null) locationSearch = info;
        if (locationSearch == null) return;
        // 检查搜索条件
        if (StringUtils.isEmpty(locationSearch.getAddress()) && locationSearch.getLongitude() == 0 && locationSearch.getLatitude() == 0) {
            ToastUtils.show(getString(R.string.search_location_no_exist));
            return;
        }
        // 搜索回调
        if (poiSearchListener == null) {
            poiSearchListener = MapHelper.getPoiSearchListener(new MapHelper.SearchCallBack() {
                @Override
                public void onSuccess(ArrayList<PoiItem> pois) {
                    if (srl != null) srl.setRefreshing(false);
                    if (recyclerHelper != null && recyclerHelper.getAdapter() != null) {
                        // list的数据
                        ((MapSelectAdapter) recyclerHelper.getAdapter()).select(-1);
                        recyclerHelper.dataNew(pois);
                    }
                    // 选中的数据
                    setLocationSelect(null, false, false);
                }

                @Override
                public void onFailed() {
                    srl.setRefreshing(false);
                    ToastUtils.show(getString(R.string.location_search_fail));
                }
            });
        }
        // 开始poi检索
        poiSearch = MapHelper.startSearch(mActivity, locationSearch.getAddress(),
                locationSearch.getLongitude(), locationSearch.getLatitude(), poiSearchListener);
    }

}
