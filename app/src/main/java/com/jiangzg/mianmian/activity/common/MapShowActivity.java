package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.helper.MapHelper;

import java.util.List;

public class MapShowActivity extends BaseActivity<MapShowActivity> {

    private GeocodeSearch geocodeSearch;

    // 当前我的位置
    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, MapShowActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    // 传入的位置
    public static void goActivity(Activity from, double lon, double lat) {
        Intent intent = new Intent(from, MapShowActivity.class);
        // intent.putExtra(); // TODO
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_map_show;
    }

    @Override
    protected void initView(Bundle state) {
        // TODO 本身显示定位蓝点 + 目标地点
    }

    @Override
    protected void initData(Bundle state) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //MapHelper.getAddressByLatLon(geocodeSearch, target.latitude, target.longitude);

        // 逆地理编码 (getAddressByLatLon的回调)
        geocodeSearch = MapHelper.initGeocode(mActivity, new MapHelper.GeocodeSearchCallBack() {
            @Override
            public void onSuccess(RegeocodeAddress regeocodeAddress) {
                List<PoiItem> pois = regeocodeAddress.getPois();
                if (pois.size() > 0) {
                    PoiItem poiItem = pois.get(0);
                    LatLonPoint latLonPoint = poiItem.getLatLonPoint();
                    //locationInfo.setLongitude(latLonPoint.getLongitude());
                    //locationInfo.setLatitude(latLonPoint.getLatitude());
                    //locationInfo.setAddress(poiItem.getAdName());
                    String formatAddress = regeocodeAddress.getFormatAddress();

                }
                // TODO RV
            }

            @Override
            public void onFailed() {
            }
        });
    }
}
