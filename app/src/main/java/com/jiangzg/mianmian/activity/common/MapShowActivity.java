package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.geocoder.GeocodeSearch;
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
import com.jiangzg.mianmian.helper.MapHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import butterknife.BindView;

public class MapShowActivity extends BaseActivity<MapShowActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.etSearch)
    EditText etSearch;

    private AMap aMap;
    private GeocodeSearch geocodeSearch;
    private LocationInfo locationInfo;
    private PoiSearch poiSearch;
    private PoiSearch.OnPoiSearchListener poiSearchListener;
    private RecyclerHelper recyclerHelper;

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
        //MapHelper.initMyLocation(aMap); // 要定位蓝点

    }

    @Override
    protected void initData(Bundle state) {
        if (aMap == null) return;
        // 逆地理编码 (getAddressByLatLon的回调)
        //geocodeSearch = MapHelper.initGeocode(mActivity, new MapHelper.GeocodeSearchCallBack() {
        //    @Override
        //    public void onSuccess(RegeocodeAddress regeocodeAddress) {
        //        List<PoiItem> pois = regeocodeAddress.getPois();
        //        if (pois.size() > 0) {
        //            PoiItem poiItem = pois.get(0);
        //            LatLonPoint latLonPoint = poiItem.getLatLonPoint();
        //            //locationInfo.setLongitude(latLonPoint.getLongitude());
        //            //locationInfo.setLatitude(latLonPoint.getLatitude());
        //            //locationInfo.setAddress(poiItem.getAdName());
        //            String formatAddress = regeocodeAddress.getFormatAddress();
        //
        //        }
        //        // TODO RV
        //    }
        //
        //    @Override
        //    public void onFailed() {
        //    }
        //});

        String address = getIntent().getStringExtra("address");
        double latitude = getIntent().getDoubleExtra("latitude", 0);
        double longitude = getIntent().getDoubleExtra("longitude", 0);

        if (latitude != 0 || longitude != 0) {
            View view = new View(mActivity);
            view.setBackgroundColor(Color.BLUE);
            // 根据经纬度
            MapHelper.moveMapByLatLon(aMap, latitude, longitude);
            LatLng latLng = new LatLng(latitude, longitude);
            MarkerOptions markerOptions = new MarkerOptions()
                    .position(latLng)
                    .title(address)
                    .snippet(address + ":" + latitude + "," + longitude)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_location_orange))
                    .draggable(false)
                    .visible(true);
            aMap.addMarker(markerOptions);

        } else if (!StringUtils.isEmpty(address)) {
            // 根据地址
            // search
        } else {
            // 只显示自己的
        }
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
