package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.MapSelectAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.LocationHelper;
import com.jiangzg.mianmian.helper.MapHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.ArrayList;

import butterknife.BindView;

public class MapSelectActivity extends BaseActivity<MapSelectActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private boolean init;
    private AMap aMap;
    private LocationInfo locationInfo;
    private PoiSearch poiSearch;
    private PoiSearch.OnPoiSearchListener poiSearchListener;
    private RecyclerHelper recyclerHelper;

    public static void goActivity(final Activity from) {
        PermUtils.requestPermissions(from, ConsHelper.REQUEST_LOCATION, PermUtils.location, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                Intent intent = new Intent(from, MapSelectActivity.class);
                // intent.putExtra();
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ActivityTrans.start(from, intent);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(from);
            }
        });
    }

    public static void goActivity(final Activity from, final String address, final double latitude, final double longitude) {
        PermUtils.requestPermissions(from, ConsHelper.REQUEST_LOCATION, PermUtils.location, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                Intent intent = new Intent(from, MapSelectActivity.class);
                intent.putExtra("address", address);
                intent.putExtra("longitude", longitude);
                intent.putExtra("latitude", latitude);
                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                ActivityTrans.start(from, intent);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(from);
            }
        });
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_map_select;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.please_select_location), true);
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
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new MapSelectAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_white, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        MapSelectAdapter mapSelectAdapter = (MapSelectAdapter) adapter;
                        locationInfo = mapSelectAdapter.select(position);
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        init = true;
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
                LogUtils.i(LOG_TAG, "onCameraChangeFinish: " + target.latitude + "---" + target.longitude);
                startMapSearch("", target.latitude, target.longitude);
            }
        });
        // 检索回调
        poiSearchListener = MapHelper.getPoiSearchListener(new MapHelper.SearchCallBack() {
            @Override
            public void onSuccess(ArrayList<PoiItem> pois) {
                if (recyclerHelper == null || srl == null) return;
                locationInfo = ((MapSelectAdapter) recyclerHelper.getAdapter()).select(-1);
                srl.setRefreshing(false);
                recyclerHelper.dataNew(pois);
            }

            @Override
            public void onFailed() {
                srl.setRefreshing(false);
                ToastUtils.show(getString(R.string.location_search_fail));
            }
        });
        String address = getIntent().getStringExtra("address");
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        startMapSearch(address, latitude, longitude);
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
        getMenuInflater().inflate(R.menu.help_complete, menu);
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
        if (poiSearch != null) {
            poiSearch.setOnPoiSearchListener(null);
        }
        poiSearchListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.TYPE_MAP_SELECT);
                return true;
            case R.id.menuComplete: // 完成
                if (locationInfo == null) {
                    ToastUtils.show(getString(R.string.please_select_location));
                    return true;
                }
                RxEvent<LocationInfo> event = new RxEvent<>(ConsHelper.EVENT_MAP_SELECT, locationInfo);
                RxBus.post(event);
                mActivity.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startMapSearch(String address, double latitude, double longitude) {
        if (srl != null && !srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        if (aMap == null) return;
        // 检查搜索条件
        if (StringUtils.isEmpty(address) && latitude == 0 && longitude == 0) {
            LocationInfo info = LocationInfo.getInfo();
            if (StringUtils.isEmpty(info.getAddress()) && info.getLatitude() == 0 && info.getLongitude() == 0) {
                // startLocation
                LocationHelper.startLocation(false, new LocationHelper.LocationCallBack() {
                    @Override
                    public void onSuccess(LocationInfo info) {
                        if (aMap == null) return;
                        // 拖动map并开始搜索
                        MapHelper.moveMapByLatLon(aMap, info.getLatitude(), info.getLongitude());
                        MapSelectActivity.this.startMapSearch(info.getAddress(), info.getLatitude(), info.getLongitude());
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        if (srl != null) srl.setRefreshing(false);
                        ToastUtils.show(getString(R.string.location_error));
                    }
                });
                return;
            } else {
                // 自己手机里存的位置
                address = info.getAddress();
                latitude = info.getLatitude();
                longitude = info.getLongitude();
                MapHelper.moveMapByLatLon(aMap, latitude, longitude);
            }
        }
        // 开始poi检索
        poiSearch = MapHelper.startSearch(mActivity, address, latitude, longitude, poiSearchListener);
        if (init) {
            init = false;
            // map的移动只有初始定位和原先位置，其他时候只有用户拖动
            MapHelper.moveMapByLatLon(aMap, latitude, longitude);
        }
    }

}
