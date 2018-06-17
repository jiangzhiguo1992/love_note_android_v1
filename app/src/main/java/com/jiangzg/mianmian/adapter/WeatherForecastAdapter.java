//package com.jiangzg.mianmian.adapter;
//
//import android.support.v4.app.FragmentActivity;
//
//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.chad.library.adapter.base.BaseViewHolder;
//import com.jiangzg.base.common.StringUtils;
//import com.jiangzg.mianmian.R;
//import com.jiangzg.mianmian.helper.ConvertHelper;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Locale;
//
///**
// * Created by JZG on 2018/3/13.
// * 意见反馈适配器
// */
//public class WeatherForecastAdapter extends BaseQuickAdapter<WeatherForecastAdapter.CpForecast, BaseViewHolder> {
//
//    private FragmentActivity mActivity;
//
//    public WeatherForecastAdapter(FragmentActivity activity) {
//        super(R.layout.list_item_weather_forecast);
//        mActivity = activity;
//    }
//
//    @Override
//    protected void convert(BaseViewHolder helper, CpForecast item) {
//        // time
//        String time = "";
//        // ta
//        Weather.Forecast taForecast = item.getTaForecast();
//        if (taForecast == null) {
//            helper.setVisible(R.id.rlWeatherLeft, false);
//            helper.setVisible(R.id.tvShowLeft, true);
//            helper.setText(R.id.tvShowLeft, R.string.cant_get_weather_info);
//        } else {
//            helper.setVisible(R.id.rlWeatherLeft, true);
//            helper.setVisible(R.id.tvShowLeft, false);
//            // taData
//            String taPredictDate = taForecast.getPredictDate();
//            if (!StringUtils.isEmpty(taPredictDate)) {
//                time = taPredictDate;
//            }
//            String taConditionDay = taForecast.getConditionDay();
//            String taConditionNight = taForecast.getConditionNight();
//            String taCondition = String.format(Locale.getDefault(), "%s~%s", taConditionDay, taConditionNight);
//            String taConditionIdDay = taForecast.getConditionIdDay();
//            int taIconDay = ConvertHelper.getWeatherIconById(taConditionIdDay);
//            String taConditionIdNight = taForecast.getConditionIdNight();
//            int taIconNight = ConvertHelper.getWeatherIconById(taConditionIdNight);
//            String taTempDay = taForecast.getTempDay();
//            String taTempNight = taForecast.getTempNight();
//            String taTemp = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_wave_holder_c), taTempDay, taTempNight);
//            String taWindLevelDay = taForecast.getWindLevelDay();
//            String taWindDirDay = taForecast.getWindDirDay();
//            String taWindLevelNight = taForecast.getWindLevelNight();
//            String taWindDirNight = taForecast.getWindDirNight();
//            String taWind = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_level_holder_holder_level_holder),
//                    taWindLevelDay, taWindDirDay, taWindLevelNight, taWindDirNight);
//            // leftView
//            helper.setText(R.id.tvConditionLeft, taCondition);
//            if (taIconDay != 0) {
//                helper.setImageResource(R.id.ivIconLeftDay, taIconDay);
//            } else {
//                helper.setImageDrawable(R.id.ivIconLeftDay, null);
//            }
//            if (taIconNight != 0) {
//                helper.setImageResource(R.id.ivIconLeftNight, taIconNight);
//            } else {
//                helper.setImageDrawable(R.id.ivIconLeftNight, null);
//            }
//            helper.setText(R.id.tvTempLeft, taTemp);
//            helper.setText(R.id.tvWindLeft, taWind);
//        }
//        // my
//        Weather.Forecast myForecast = item.getMyForecast();
//        if (myForecast == null) {
//            helper.setVisible(R.id.rlWeatherRight, false);
//            helper.setVisible(R.id.tvShowRight, true);
//            helper.setText(R.id.tvShowRight, R.string.cant_get_weather_info);
//        } else {
//            helper.setVisible(R.id.rlWeatherRight, true);
//            helper.setVisible(R.id.tvShowRight, false);
//            // myData
//            String myPredictDate = myForecast.getPredictDate();
//            if (!StringUtils.isEmpty(myPredictDate)) {
//                time = myPredictDate;
//            }
//            String myConditionDay = myForecast.getConditionDay();
//            String myConditionNight = myForecast.getConditionNight();
//            String myCondition = String.format(Locale.getDefault(), "%s~%s", myConditionDay, myConditionNight);
//            String myConditionIdDay = myForecast.getConditionIdDay();
//            int myIconDay = ConvertHelper.getWeatherIconById(myConditionIdDay);
//            String myConditionIdNight = myForecast.getConditionIdNight();
//            int myIconNight = ConvertHelper.getWeatherIconById(myConditionIdNight);
//            String myTempDay = myForecast.getTempDay();
//            String myTempNight = myForecast.getTempNight();
//            String myTemp = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_wave_holder_c), myTempDay, myTempNight);
//            String myWindLevelDay = myForecast.getWindLevelDay();
//            String myWindDirDay = myForecast.getWindDirDay();
//            String myWindLevelNight = myForecast.getWindLevelNight();
//            String myWindDirNight = myForecast.getWindDirNight();
//            String myWind = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_level_holder_holder_level_holder),
//                    myWindLevelDay, myWindDirDay, myWindLevelNight, myWindDirNight);
//            // rightView
//            helper.setText(R.id.tvConditionRight, myCondition);
//            if (myIconDay != 0) {
//                helper.setImageResource(R.id.ivIconRightDay, myIconDay);
//            } else {
//                helper.setImageDrawable(R.id.ivIconRightDay, null);
//            }
//            if (myIconNight != 0) {
//                helper.setImageResource(R.id.ivIconRightNight, myIconNight);
//            } else {
//                helper.setImageDrawable(R.id.ivIconRightNight, null);
//            }
//            helper.setText(R.id.tvTempRight, myTemp);
//            helper.setText(R.id.tvWindRight, myWind);
//        }
//        helper.setText(R.id.tvTime, time);
//    }
//
//    public static class CpForecast {
//        public Weather.Forecast myForecast;
//        public Weather.Forecast taForecast;
//
//        public Weather.Forecast getMyForecast() {
//            return myForecast;
//        }
//
//        public void setMyForecast(Weather.Forecast myForecast) {
//            this.myForecast = myForecast;
//        }
//
//        public Weather.Forecast getTaForecast() {
//            return taForecast;
//        }
//
//        public void setTaForecast(Weather.Forecast taForecast) {
//            this.taForecast = taForecast;
//        }
//    }
//
//    public void setData(Weather myWeather, Weather taWeather) {
//        // my
//        List<Weather.Forecast> myForecastList = new ArrayList<>();
//        if (myWeather != null && myWeather.getForecast() != null && myWeather.getForecast().size() >= 1) {
//            List<Weather.Forecast> forecast = myWeather.getForecast();
//            myForecastList.addAll(forecast);
//            myForecastList.remove(0); // 去掉第一个
//        }
//        // ta
//        List<Weather.Forecast> taForecastList = new ArrayList<>();
//        if (taWeather != null && taWeather.getForecast() != null && taWeather.getForecast().size() >= 1) {
//            List<Weather.Forecast> forecast = taWeather.getForecast();
//            taForecastList.addAll(forecast);
//            taForecastList.remove(0); // 去掉第一个
//        }
//        // cp
//        List<CpForecast> cpForecastList = new ArrayList<>();
//        int myForecastSize = myForecastList.size();
//        int taForecastSize = taForecastList.size();
//        int cpForecastSize = Math.max(myForecastSize, taForecastSize);
//        for (int i = 0; i < cpForecastSize; i++) {
//            Weather.Forecast myForecast = null;
//            if (myForecastSize > i) {
//                myForecast = myForecastList.get(i);
//            }
//            Weather.Forecast taForecast = null;
//            if (taForecastSize > i) {
//                taForecast = taForecastList.get(i);
//            }
//            CpForecast cpForecast = new CpForecast();
//            cpForecast.setMyForecast(myForecast);
//            cpForecast.setTaForecast(taForecast);
//            cpForecastList.add(cpForecast);
//        }
//        setNewData(cpForecastList);
//    }
//
//}
