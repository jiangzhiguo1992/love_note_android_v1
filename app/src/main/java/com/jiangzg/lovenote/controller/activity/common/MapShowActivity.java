package com.jiangzg.lovenote.controller.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiSearch;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.system.LocationHelper;
import com.jiangzg.lovenote.helper.view.MapHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;

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

    // 当前我的位置
    //public static void goActivity(final Activity from) {
    //    if (!LocationHelper.checkLocationEnable(from)) return;
    //    Intent intent = new Intent(from, MapShowActivity.class);
    //    // intent.putExtra();
    //    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    //    ActivityTrans.start(from, intent);
    //}

    // 传入的位置
    public static void goActivity(final Activity from, final String address, final double longitude, final double latitude) {
        if (!LocationHelper.checkLocationEnable(from)) return;
        if (StringUtils.isEmpty(address) && (longitude == 0 && latitude == 0)) {
            ToastUtils.show(from.getString(R.string.no_location_info_cant_go_map));
            return;
        }
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
    protected void initView(Intent intent, Bundle state) {
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
    protected void initData(Intent intent, Bundle state) {
        if (aMap == null) return;
        // 我的定位(自带系统market)
        MapHelper.initMyLocation(mActivity, aMap, location -> {
            final String tarAddress = intent.getStringExtra("address");
            final double tarLongitude = intent.getDoubleExtra("longitude", 0);
            final double tarLatitude = intent.getDoubleExtra("latitude", 0);
            // 目标market
            if (tarLongitude != 0 || tarLatitude != 0) {
                // 根据经纬度
                MapHelper.showMarker(aMap, tarLongitude, tarLatitude, tarAddress);
                MapHelper.moveMapByLatLon(aMap, tarLongitude, tarLatitude);
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
                        MapHelper.showMarker(aMap, longitude, latitude, title);
                        MapHelper.moveMapByLatLon(aMap, longitude, latitude);
                    }

                    @Override
                    public void onFailed() {
                        ToastUtils.show(getString(R.string.location_error));
                    }
                });
                poiTarget = MapHelper.startSearch(mActivity, tarAddress, tarLongitude, tarLatitude, poiTargetListener);
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        if (poiTarget != null) {
            poiTarget.setOnPoiSearchListener(null);
        }
        poiTargetListener = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (map != null) {
            map.onResume();
        }
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

}
