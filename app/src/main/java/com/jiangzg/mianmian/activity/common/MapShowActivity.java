package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiSearch;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.LocationHelper;
import com.jiangzg.mianmian.helper.MapHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import java.util.ArrayList;

import butterknife.BindView;

public class MapShowActivity extends BaseActivity<MapShowActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.etSearch)
    EditText etSearch;

    private AMap aMap;
    private PoiSearch poiSearch;
    private PoiSearch.OnPoiSearchListener poiSearchListener;

    // 当前我的位置
    public static void goActivity(final Activity from) {
        PermUtils.requestPermissions(from, ConsHelper.REQUEST_LOCATION, PermUtils.location, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                Intent intent = new Intent(from, MapShowActivity.class);
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

    // 传入的位置
    public static void goActivity(final Activity from, final String address, final double latitude, final double longitude) {
        if (StringUtils.isEmpty(address) && (latitude == 0 && longitude == 0)) {
            ToastUtils.show(from.getString(R.string.no_location_info_cant_go_map));
            return;
        }
        PermUtils.requestPermissions(from, ConsHelper.REQUEST_LOCATION, PermUtils.location, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                Intent intent = new Intent(from, MapShowActivity.class);
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
    }

    @Override
    protected void initData(Bundle state) {
        if (aMap == null) return;
        // 检索回调
        poiSearchListener = MapHelper.getPoiSearchListener(new MapHelper.SearchCallBack() {
            @Override
            public void onSuccess(ArrayList<PoiItem> pois) {
                if (pois == null || pois.size() <= 0) return;
                PoiItem poiItem = pois.get(0);
                String title = poiItem.getTitle();
                LatLonPoint latLonPoint = poiItem.getLatLonPoint();
                double latitude = latLonPoint.getLatitude();
                double longitude = latLonPoint.getLongitude();
                // 根据经纬度
                MapHelper.moveMapByLatLon(aMap, latitude, longitude);
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(title)
                        .snippet(getString(R.string.lat_lon_colon) + latitude + "," + longitude)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_orange))
                        .draggable(false)
                        .visible(true);
                aMap.addMarker(markerOptions);
            }

            @Override
            public void onFailed() {
                ToastUtils.show(getString(R.string.location_search_fail));
            }
        });
        // 设置market
        final String address = getIntent().getStringExtra("address");
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);
        // 目标market
        if (latitude != 0 || longitude != 0) {
            // 根据经纬度
            MapHelper.moveMapByLatLon(aMap, latitude, longitude);
            LatLng latLng = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(address)
                    .snippet(getString(R.string.lat_lon_colon) + latitude + "," + longitude)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_orange))
                    .draggable(false)
                    .visible(true);
            aMap.addMarker(markerOptions);
        } else if (!StringUtils.isEmpty(address)) {
            // 根据地址
            poiSearch = MapHelper.startSearch(mActivity, address, latitude, longitude, poiSearchListener);
        }
        // 我的market
        LocationHelper.startLocation(true, new LocationHelper.LocationCallBack() {
            @Override
            public void onSuccess(LocationInfo info) {
                double latitude = info.getLatitude();
                double longitude = info.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(latLng)
                        .title(getString(R.string.my_location))
                        .snippet(getString(R.string.lat_lon_colon) + latitude + "," + longitude)
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_orange))
                        .draggable(false)
                        .visible(true);
                aMap.addMarker(markerOptions);
                if (StringUtils.isEmpty(address) && (latitude == 0 && longitude == 0)) {
                    // 没有目标位置
                    MapHelper.moveMapByLatLon(aMap, latitude, longitude);
                }
            }

            @Override
            public void onFailed(String errMsg) {
                ToastUtils.show(getString(R.string.location_error));
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
        if (poiSearch != null) {
            poiSearch.setOnPoiSearchListener(null);
        }
        poiSearchListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.TYPE_MAP_SHOW);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
