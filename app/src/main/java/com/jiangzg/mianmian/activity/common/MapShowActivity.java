package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiSearch;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.helper.LocationHelper;
import com.jiangzg.mianmian.helper.MapHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 地图展示
 */
public class MapShowActivity extends BaseActivity<MapShowActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.map)
    MapView map;

    private AMap aMap;
    private PoiSearch poiTarget;
    private PoiSearch.OnPoiSearchListener poiTargetListener;
    private AMapLocationClient locationMe;

    // 当前我的位置
    public static void goActivity(final Activity from) {
        if (!LocationHelper.checkLocationEnable(from)) return;
        Intent intent = new Intent(from, MapShowActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    // 传入的位置
    public static void goActivity(final Activity from, final String address, final double longitude, final double latitude) {
        if (StringUtils.isEmpty(address) && (longitude == 0 && latitude == 0)) {
            ToastUtils.show(from.getString(R.string.no_location_info_cant_go_map));
            return;
        }
        if (!LocationHelper.checkLocationEnable(from)) return;
        Intent intent = new Intent(from, MapShowActivity.class);
        intent.putExtra("address", address);
        intent.putExtra("longitude", longitude);
        intent.putExtra("latitude", latitude);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_map_show;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.map), true);
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
        MapHelper.initMarkerStyle(aMap, mActivity);
    }

    @Override
    protected void initData(Bundle state) {
        if (aMap == null) return;
        // 设置market
        final String tarAddress = getIntent().getStringExtra("address");
        final double tarLongitude = getIntent().getDoubleExtra("longitude", 0);
        final double tarLatitude = getIntent().getDoubleExtra("latitude", 0);
        // 目标market
        if (tarLongitude != 0 || tarLatitude != 0) {
            // 根据经纬度
            MapHelper.moveMapByLatLon(aMap, tarLongitude, tarLatitude);
            MapHelper.showMarker(aMap, tarLongitude, tarLatitude, tarAddress);
        } else if (!StringUtils.isEmpty(tarAddress)) {
            // 根据地址
            poiTargetListener = MapHelper.getPoiSearchListener(new MapHelper.SearchCallBack() {
                @Override
                public void onSuccess(ArrayList<PoiItem> pois) {
                    if (aMap == null) return;
                    if (pois == null || pois.size() <= 0) return;
                    PoiItem poiItem = pois.get(0);
                    String title = poiItem.getTitle();
                    LatLonPoint latLonPoint = poiItem.getLatLonPoint();
                    double longitude = latLonPoint.getLongitude();
                    double latitude = latLonPoint.getLatitude();
                    // 根据经纬度
                    MapHelper.moveMapByLatLon(aMap, longitude, latitude);
                    MapHelper.showMarker(aMap, longitude, latitude, title);
                }

                @Override
                public void onFailed() {
                    ToastUtils.show(getString(R.string.location_error));
                }
            });
            poiTarget = MapHelper.startSearch(mActivity, tarAddress, tarLongitude, tarLatitude, poiTargetListener);
        }
        // 我的market
        locationMe = LocationHelper.startLocation(mActivity, false, new LocationHelper.LocationCallBack() {
            @Override
            public void onSuccess(LocationInfo info) {
                if (aMap == null) return;
                double longitude = info.getLongitude();
                double latitude = info.getLatitude();
                MapHelper.setMarker(aMap, longitude, latitude, getString(R.string.my_location));
                // 没有Target位置
                if (StringUtils.isEmpty(tarAddress) && (tarLongitude == 0 && tarLatitude == 0)) {
                    MapHelper.moveMapByLatLon(aMap, tarLongitude, tarLatitude);
                }
            }

            @Override
            public void onFailed(String errMsg) {
                //ToastUtils.show(getString(R.string.location_error));
            }
        });
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
        getMenuInflater().inflate(R.menu.help, menu);
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
        if (poiTarget != null) {
            poiTarget.setOnPoiSearchListener(null);
        }
        poiTargetListener = null;
        LocationHelper.stopLocation(locationMe);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_MAP_SHOW);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
