package com.jiangzg.mianmian.helper;

import android.content.Context;
import android.graphics.Color;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;

import java.util.ArrayList;

/**
 * Created by JiangZhiGuo on 2016/08/05.
 * describe 高德地图管理 (不用的话，删掉lib下的几个jar 并去掉manifest下的配置)
 */
public class MapHelper {

    private static final String LOG_TAG = "MapHelper";

    /**
     * ****************************************地图***********************************************
     */
    public static void initMapView(AMap aMap) {
        if (aMap == null) return;
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false); // 缩放按钮
        uiSettings.setCompassEnabled(false); // 指南针
        uiSettings.setMyLocationButtonEnabled(false);// 刷新定位按钮
        uiSettings.setScaleControlsEnabled(false); // 比例尺控件
        uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT); // logo位置
    }

    public static void initMyLocation(AMap aMap) {
        if (aMap == null) return;
        // myLocation
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE); // 只定位一次，没有方向，且将视角移动到地图中心点
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_grey));
        myLocationStyle.strokeWidth(Color.BLACK);
        myLocationStyle.strokeColor(5);
        myLocationStyle.showMyLocation(true); // 不仅定位，还要定位蓝点
        // aMap
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 显示定位蓝点，默认是false
        //aMap.setOnMyLocationChangeListener();
    }

    /* 逆地理回调 */
    public interface GeocodeSearchCallBack {
        void onSuccess(RegeocodeAddress regeocodeAddress);

        void onFailed();
    }

    /* 逆地理编码(通过经纬度获取address) */
    public static GeocodeSearch initGeocode(Context context, final GeocodeSearchCallBack callBack) {
        GeocodeSearch geocodeSearch = new GeocodeSearch(context);
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(final RegeocodeResult result, final int rCode) {
                final int successCode = 1000; // 以后可能会变
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (rCode == successCode) {
                            if (result != null && result.getRegeocodeAddress() != null) {
                                RegeocodeAddress regeocodeAddress = result.getRegeocodeAddress();
                                String province = regeocodeAddress.getProvince();
                                String city = regeocodeAddress.getCity();
                                String district = regeocodeAddress.getDistrict();
                                String addressName = regeocodeAddress.getFormatAddress();
                                LogUtils.i(LOG_TAG, "initGeocode: onRegeocodeSearched:  " + province + "---" + city + "---" + district + "---" + addressName);
                                if (callBack != null) {
                                    callBack.onSuccess(regeocodeAddress);
                                }
                            } else {
                                if (callBack != null) {
                                    callBack.onFailed();
                                }
                            }
                        } else {
                            if (callBack != null) {
                                callBack.onFailed();
                            }
                        }
                    }
                });
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
            }
        });
        return geocodeSearch;
    }

    /* 响应逆地理编码(在camera回调中调用) */
    public static void getAddressByLatLon(GeocodeSearch geocodeSearch, double latitude, double longitude) {
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longitude), 200, GeocodeSearch.AMAP);
        // 第一个参数表示一个LatLon，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocodeSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /* 地图移动到指定区域 */
    public static void moveMapByLatLon(AMap aMap, double latitude, double longitude) {
        if (aMap == null) return;
        if (latitude == 0 || longitude == 0) {
            // 单纯放大
            aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        } else {
            // 移动到指定位置并放大
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15));
        }
    }

    /**
     * *************************************搜索***************************************************
     * 开始搜索
     */
    public static PoiSearch startSearch(Context context, String key, double latitude, double longitude,
                                        PoiSearch.OnPoiSearchListener poiSearchListener) {
        PoiSearch.Query query = new PoiSearch.Query(key, "");
        query.setPageSize(100); // 设置每页最多返回多少条poiitem
        query.setPageNum(0); // 设置查第一页
        PoiSearch poiSearch = new PoiSearch(context, query);
        if (latitude != 0 || longitude != 0) {
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 1000)); // 设置周边搜索的中心点以及区域
        }
        if (poiSearchListener != null) {
            poiSearch.setOnPoiSearchListener(poiSearchListener); // 设置数据返回的监听器
        }
        poiSearch.searchPOIAsyn(); // 开始搜索
        return poiSearch;
    }

    /* 搜索SDK回调 */
    public interface SearchCallBack {
        void onSuccess(ArrayList<PoiItem> pois);

        void onFailed();
    }

    public static PoiSearch.OnPoiSearchListener getPoiSearchListener(final SearchCallBack searchCallBack) {
        return new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int rCode) {
                int successCode = 1000; // 以后可能会变
                if (rCode == successCode) {
                    //result.getPois()可以获取到PoiItem列表，Poi详细信息可参考PoiItem类
                    //若当前城市查询不到所需Poi信息，可以通过result.getSearchSuggestionCitys()获取当前Poi搜索的建议城市
                    //如果搜索关键字明显为误输入，则可通过result.getSearchSuggestionKeywords()方法得到搜索关键词建议
                    final ArrayList<PoiItem> pois = poiResult.getPois();
                    LogUtils.d(LOG_TAG, pois.toString());
                    MyApp.get().getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (searchCallBack != null) {
                                searchCallBack.onSuccess(pois);
                            }
                        }
                    });
                } else {
                    if (searchCallBack != null) {
                        searchCallBack.onFailed();
                    }
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        };
    }

}
