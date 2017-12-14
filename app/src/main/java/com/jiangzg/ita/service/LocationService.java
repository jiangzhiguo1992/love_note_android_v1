//package com.jiangzg.project.service;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.IBinder;
//
//import com.amap.api.location.AMapLocation;
//import com.amap.api.location.AMapLocationListener;
//import AppContext;
//import LocationInfo;
//import ToastUtils;
//import com.jiangzg.project.R;
//import com.jiangzg.project.utils.third.MapUtils;
//
//public class LocationService extends Service {
//
//    public static void startService(final Context from, final boolean once) {
////        RxPermUtils.requestMap(new RxPermUtils.PermissionListener() {
////            @Override
////            public void onAgree() {
////
////            }
////        });
//
//        Intent intent = new Intent(from, LocationService.class);
//        intent.putExtra("once", once);
//        from.startService(intent);
//    }
//
//    @Override
//    public void onStart(Intent intent, int startId) {
//        super.onStart(intent, startId);
//
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        final boolean once = intent.getBooleanExtra("once", true);
//        MapUtils.get().initLocation(this, once);
//        AMapLocationListener locationListener = MapUtils.get()
//                .getAMapLocationListener(new MapUtils.LocationCallBack() {
//                    @Override
//                    public void onSuccess(AMapLocation aMapLocation) {
//                        double longitude = aMapLocation.getLongitude();
//                        double latitude = aMapLocation.getLatitude();
//                        String province = aMapLocation.getProvince();
//                        String city = aMapLocation.getCity();
//                        String district = aMapLocation.getDistrict();
//                        String address = aMapLocation.getAddress();
//
//                        LocationInfo locationUtils = LocationInfo.getInfo();
//                        locationUtils.setLongitude(longitude);
//                        locationUtils.setLatitude(latitude);
//                        locationUtils.setProvince(province);
//                        locationUtils.setCity(city);
//                        locationUtils.setDistrict(district);
//                        locationUtils.setAddress(address);
//                        if (once) {
//                            stopSelf();
//                        }
//                    }
//
//                    @Override
//                    public void onFailed(AMapLocation aMapLocation) {
//                        int errorCode = aMapLocation.getErrorCode();
//                        String toast = AppContext.get().getString(R.string.location_error);
//                        ToastUtils.show(toast + errorCode);
//                    }
//                });
//        MapUtils.get().startLocation(locationListener);
//        return super.onStartCommand(intent, flags, startId);
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        throw null;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        MapUtils.get().stopLocation();
//    }
//
//}
