package com.jiangzg.lovenote.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapOptions;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
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
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.main.MyApp;

import java.util.ArrayList;

/**
 * Created by JiangZhiGuo on 2016/08/05.
 * describe 高德地图管理 (不用的话，删掉lib下的几个jar 并去掉manifest下的配置)
 */
public class MapHelper {

    /**
     * ****************************************地图***********************************************
     */
    public static void initMapView(AMap aMap) {
        if (aMap == null) return;
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false); // 缩放按钮
        uiSettings.setCompassEnabled(false); // 指南针
        uiSettings.setMyLocationButtonEnabled(false); // 刷新定位按钮
        uiSettings.setScaleControlsEnabled(true); // 比例尺控件
        uiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_LEFT); // logo位置
    }

    /* 地图移动到指定区域 */
    public static void moveMapByLatLon(AMap aMap, double longitude, double latitude) {
        if (aMap == null) return;
        if (longitude == 0 || latitude == 0) {
            // 单纯放大
            aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
        } else {
            // 移动到指定位置并放大
            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 20));
        }
    }

    // 自定位坐标
    public static void initMyLocation(AMap aMap) {
        if (aMap == null) return;
        // myLocation
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE); // 只定位一次，没有方向，且将视角移动到地图中心点
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location_primary_40));
        myLocationStyle.strokeWidth(Color.BLACK);
        myLocationStyle.strokeColor(5);
        myLocationStyle.showMyLocation(true); // 不仅定位，还要定位蓝点
        // aMap
        aMap.setMyLocationStyle(myLocationStyle); // 设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 显示定位蓝点，默认是false
        //aMap.setOnMyLocationChangeListener();
    }

    // marker
    public static void initMarkerStyle(AMap aMap, final Activity activity) {
        if (aMap == null) return;
        aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                String title = marker.getTitle();
                String snippet = marker.getSnippet();
                View view = LayoutInflater.from(activity).inflate(R.layout.pop_map_maker, null);
                TextView tvTitle = view.findViewById(R.id.tvTitle);
                TextView tvContent = view.findViewById(R.id.tvContent);
                tvTitle.setText(title);
                tvContent.setText(snippet);
                return view;
            }
        });
    }

    // 只是设置marker，不会显示，点击才能显示
    public static Marker setMarker(AMap aMap, double longitude, double latitude, String title) {
        if (aMap == null || (longitude == 0 && latitude == 0)) return null;
        LatLng latLng = new LatLng(latitude, longitude);
        // icon
        VectorDrawableCompat vectorDrawable = VectorDrawableCompat.create(MyApp.get().getResources(), R.drawable.ic_location_primary_40, MyApp.get().getTheme());
        Bitmap bitmap = ConvertUtils.drawable2Bitmap(vectorDrawable);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
        // options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title)
                .snippet(MyApp.get().getString(R.string.lon_lat_colon) + longitude + " , " + latitude)
                .icon(icon)
                .draggable(false)
                .visible(true);
        return aMap.addMarker(markerOptions);
    }

    // 修改marker的信息
    public static Marker setMarker(Marker marker, double longitude, double latitude, String title) {
        if (marker == null) return null;
        LatLng latLng;
        if (longitude != 0 || latitude != 0) {
            latLng = new LatLng(latitude, longitude);
        } else {
            latLng = marker.getPosition();
        }
        String show;
        if (!StringUtils.isEmpty(title)) {
            show = title;
        } else {
            show = marker.getTitle();
        }
        marker.setPosition(latLng);
        marker.setTitle(show);
        return marker;
    }

    public static Marker showMarker(AMap aMap, double longitude, double latitude, String title) {
        Marker marker = setMarker(aMap, longitude, latitude, title);
        showMarker(marker);
        return marker;
    }

    public static void showMarker(Marker marker) {
        if (marker == null) return;
        marker.showInfoWindow();
    }

    /**
     * **************************************逆地理(坐标->地名)***************************************
     */
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
                                LogUtils.d(MapHelper.class, "onRegeocodeSearched", province + "---" + city + "---" + district + "---" + addressName);
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
    public static void getAddressByLatLon(GeocodeSearch geocodeSearch, double longitude, double latitude) {
        RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(latitude, longitude), 200, GeocodeSearch.AMAP);
        // 第一个参数表示一个LatLon，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        geocodeSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
    }

    /**
     * ***************************************搜索(地名->坐标)****************************************
     * 开始搜索
     */
    public static PoiSearch startSearch(Context context, String key, double longitude, double latitude,
                                        PoiSearch.OnPoiSearchListener poiSearchListener) {
        // keyWord表示搜索字符串，第二个参数表示POI搜索类型，默认为：生活服务、餐饮服务、商务住宅
        //共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        //cityCode表示POI搜索区域，（这里可以传空字符串，空字符串代表全国在全国范围内进行搜索）
        PoiSearch.Query query = new PoiSearch.Query(key, "");
        query.setPageSize(100); // 设置每页最多返回多少条poiitem
        query.setPageNum(0); // 设置查第一页
        PoiSearch poiSearch = new PoiSearch(context, query);
        if (longitude != 0 || latitude != 0) {
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latitude, longitude), 10000)); // 设置周边搜索的中心点以及区域
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
                    LogUtils.d(MapHelper.class, "onPoiSearched", LogUtils.getListLog(pois));
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
