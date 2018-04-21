//package com.jiangzg.ita.helper.MapUtils;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.text.TextUtils;
//
//import com.amap.api.maps2d.AMap;
//import com.amap.api.maps2d.CameraUpdateFactory;
//import com.amap.api.maps2d.LocationSource;
//import com.amap.api.maps2d.MapView;
//import com.amap.api.maps2d.model.BitmapDescriptorFactory;
//import com.amap.api.maps2d.model.LatLng;
//import com.amap.api.maps2d.model.MyLocationStyle;
//import com.amap.api.services.core.LatLonPoint;
//import com.amap.api.services.core.PoiItem;
//import com.amap.api.services.geocoder.GeocodeResult;
//import com.amap.api.services.geocoder.GeocodeSearch;
//import com.amap.api.services.geocoder.RegeocodeAddress;
//import com.amap.api.services.geocoder.RegeocodeQuery;
//import com.amap.api.services.geocoder.RegeocodeResult;
//import com.amap.api.services.poisearch.PoiResult;
//import com.amap.api.services.poisearch.PoiSearch;
//import com.jiangzg.base.common.LogUtils;
//
//import java.util.ArrayList;
//
///**
// * Created by JiangZhiGuo on 2016/08/05.
// * describe 高德地图管理 (不用的话，删掉lib下的几个jar 并去掉manifest下的配置)
// */
//public class MapUtils {
//
//    private static MapUtils mapUtils;
//
//    public static MapUtils get() {
//        if (mapUtils == null) {
//            synchronized (MapUtils.class) {
//                if (mapUtils == null) {
//                    mapUtils = new MapUtils();
//                }
//            }
//        }
//        return mapUtils;
//    }
//
//    /**
//     * ****************************************地图***********************************************
//     * 初始化地图
//     */
//    private MapView mapView;
//    private GeocodeSearch geocoderSearch; // 地理编码只有地图的时候需要
//
//    /* 初始化地图 两个参数都是自己定义,这里没有 */
//    public void initMap(MapView mapView, int locationRes, LocationSource locationSource,
//                        AMap.OnCameraChangeListener onCameraChangeListener) {
//        this.mapView = mapView;
//        AMap aMap = mapView.getMap();
//        // 自定义系统定位蓝点
//        MyLocationStyle myLocationStyle = new MyLocationStyle();
//        // 自定义定位蓝点图标
//        if (locationRes != 0) {
//            myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(locationRes));
//        }
//        // 自定义精度范围的圆形边框颜色
//        myLocationStyle.strokeColor(Color.BLACK);
//        //自定义精度范围的圆形边框宽度
//        myLocationStyle.strokeWidth(5);
//        // 将自定义的 myLocationStyle 对象添加到地图上
//        aMap.setMyLocationStyle(myLocationStyle);
//        // 设置定位监听
//        aMap.setLocationSource(locationSource);
//        // 设置默认定位按钮是否显示 ,如果不设置此定位资源则定位按钮不可点击。
//        aMap.getUiSettings().setMyLocationButtonEnabled(true);
//        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
//        aMap.setMyLocationEnabled(true);
//        // 地图移动效果监听
//        aMap.setOnCameraChangeListener(onCameraChangeListener);
//    }
//
//    /* 地图移动到指定区域 */
//    public void moveCamera(double latitude, double longitude) {
//        if (mapView != null) {
//            if (latitude == 0 || longitude == 0) { // 单纯放大
//                mapView.getMap().moveCamera(CameraUpdateFactory.zoomTo(15f));
//            } else {// 移动到指定位置并放大
//                mapView.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom
//                        (new LatLng(latitude, longitude), 15f));
//            }
//        }
//    }
//
//    /* 实现地图生命周期管理 */
//    public void onCreate(Bundle savedInstanceState) {
//        if (mapView != null) {
//            mapView.onCreate(savedInstanceState);
//        }
//    }
//
//    public void onResume() {
//        if (mapView != null) {
//            mapView.onResume();
//        }
//    }
//
//    public void onSaveInstanceState(Bundle outState) {
//        if (mapView != null) {
//            mapView.onSaveInstanceState(outState);
//        }
//    }
//
//    public void onPause() {
//        if (mapView != null) {
//            mapView.onPause();
//        }
//    }
//
//    public void onDestroy() {
//        if (mapView != null) {
//            mapView.onDestroy();
//        }
//    }
//
//    /* 逆地理编码 */
//    public void initGeocode(Context context, GeocodeSearch.OnGeocodeSearchListener onGeocodeSearchListener) {
//        geocoderSearch = new GeocodeSearch(context);
//        geocoderSearch.setOnGeocodeSearchListener(onGeocodeSearchListener);
//    }
//
//    /* 逆地理回调 */
//    public interface GeocodeSearchCallBack {
//        void onSuccess(RegeocodeAddress regeocodeAddress);
//
//        void onFailed();
//    }
//
//    public GeocodeSearch.OnGeocodeSearchListener getOnGeocodeSearchListener(final GeocodeSearchCallBack geocodeSearchCallBack) {
//        return new GeocodeSearch.OnGeocodeSearchListener() {
//            @Override
//            public void onRegeocodeSearched(final RegeocodeResult result, final int rCode) {
//                final int successCode = 1000; // 以后可能会变
////                MyApp.get().getHandler().post(new Runnable() {
////                    @Override
////                    public void run() {
////                        if (rCode == successCode && geocodeSearchCallBack != null) {
////                            if (result != null && result.getRegeocodeAddress() != null) {
////                                RegeocodeAddress regeocodeAddress = result.getRegeocodeAddress();
////                                String city = regeocodeAddress.getCity();
////                                String district = regeocodeAddress.getDistrict();
////                                String province = regeocodeAddress.getProvince();
////                                String adCode = regeocodeAddress.getAdCode();
////                                String addressName = regeocodeAddress.getFormatAddress();
////
////                                geocodeSearchCallBack.onSuccess(regeocodeAddress);
////                            } else {
////                                geocodeSearchCallBack.onFailed();
////                            }
////                        }
////                    }
////                });
//            }
//
//            @Override
//            public void onGeocodeSearched(GeocodeResult geocodeResult, int rCode) {
//            }
//        };
//    }
//
//    /* 响应逆地理编码(在camera回调中调用) */
//    public void getAddress(double latitude, double longitude) {
//        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longitude), 200,
//                GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
//
//        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
//    }
//
//    /**
//     * *************************************搜索***************************************************
//     * 开始搜索
//     */
//    public void startSearch(Context context, String key, String category, String cityCode,
//                            double latitude, double longitude,
//                            PoiSearch.OnPoiSearchListener poiSearchListener) {
//        // keyWord表示搜索字符串，第二个参数表示POI搜索类型，默认为：生活服务、餐饮服务、商务住宅
//        //共分为以下20种：汽车服务|汽车销售|
//        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
//        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
//        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
//        //cityCode表示POI搜索区域，（这里可以传空字符串，空字符串代表全国在全国范围内进行搜索）
//        PoiSearch.Query query;
//        if (TextUtils.isEmpty(cityCode)) {
//            query = new PoiSearch.Query(key, category);
//        } else {
//            query = new PoiSearch.Query(key, category, cityCode);
//        }
//        query.setPageSize(100);// 设置每页最多返回多少条poiitem
//        query.setPageNum(0);//设置查第一页
//        PoiSearch poiSearch = new PoiSearch(context, query);
//        if (latitude != 0 && longitude != 0) {
//            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 1000));//设置周边搜索的中心点以及区域
//        }
//        if (poiSearchListener != null) {
//            poiSearch.setOnPoiSearchListener(poiSearchListener);//设置数据返回的监听器
//        }
//        poiSearch.searchPOIAsyn();// 开始搜索
//    }
//
//    /* 搜索SDK回调 */
//    public interface SearchCallBack {
//        void onSuccess(ArrayList<PoiItem> pois);
//    }
//
//    public PoiSearch.OnPoiSearchListener getOnPoiSearchListener(final SearchCallBack searchCallBack) {
//        return new PoiSearch.OnPoiSearchListener() {
//            @Override
//            public void onPoiSearched(PoiResult poiResult, int rCode) {
//                int successCode = 1000; // 以后可能会变
//                if (rCode == successCode) {
//                    //result.getPois()可以获取到PoiItem列表，Poi详细信息可参考PoiItem类
//                    //若当前城市查询不到所需Poi信息，可以通过result.getSearchSuggestionCitys()获取当前Poi搜索的建议城市
//                    //如果搜索关键字明显为误输入，则可通过result.getSearchSuggestionKeywords()方法得到搜索关键词建议
//                    final ArrayList<PoiItem> pois = poiResult.getPois();
//                    LogUtils.d(pois.toString());
////                    MyApp.get().getHandler().post(new Runnable() {
////                        @Override
////                        public void run() {
////                            if (searchCallBack != null) {
////                                searchCallBack.onSuccess(pois);
////                            }
////                        }
////                    });
//                }
//            }
//
//            @Override
//            public void onPoiItemSearched(PoiItem poiItem, int i) {
//
//            }
//        };
//    }
//
//}
