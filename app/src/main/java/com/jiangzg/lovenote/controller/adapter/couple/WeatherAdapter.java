package com.jiangzg.lovenote.controller.adapter.couple;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.WeatherHelper;
import com.jiangzg.lovenote.model.entity.WeatherForecast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 天气适配器
 */
public class WeatherAdapter extends BaseQuickAdapter<WeatherAdapter.CoupleWeatherForecast, BaseViewHolder> {

    private final String formatWave;
    private final String formatWaveC;

    public WeatherAdapter(FragmentActivity activity) {
        super(R.layout.list_item_weather);
        formatWave = activity.getString(R.string.holder_wave_holder);
        formatWaveC = activity.getString(R.string.holder_wave_holder_c);
    }

    @Override
    protected void convert(BaseViewHolder helper, WeatherAdapter.CoupleWeatherForecast item) {
        // time
        long time = 0;
        // ta
        WeatherForecast taForecast = item.getTaForecast();
        if (taForecast == null) {
            helper.setVisible(R.id.rlWeatherLeft, false);
            helper.setVisible(R.id.tvShowLeft, true);
            helper.setText(R.id.tvShowLeft, R.string.now_no_weather_info);
        } else {
            helper.setVisible(R.id.rlWeatherLeft, true);
            helper.setVisible(R.id.tvShowLeft, false);
            // taData
            long taTime = taForecast.getTimeAt();
            if (taTime > 0) time = taTime;
            String condition = String.format(Locale.getDefault(), formatWave, taForecast.getConditionDay(), taForecast.getConditionNight());
            int iconDay = WeatherHelper.getIconById(taForecast.getIconDay());
            int iconNight = WeatherHelper.getIconById(taForecast.getIconNight());
            String temp = String.format(Locale.getDefault(), formatWaveC, taForecast.getTempDay(), taForecast.getTempNight());
            String wind = String.format(Locale.getDefault(), formatWave, taForecast.getWindDay(), taForecast.getWindNight());
            // leftView
            helper.setText(R.id.tvConditionLeft, condition);
            if (iconDay != 0) {
                helper.setImageResource(R.id.ivIconLeftDay, iconDay);
            } else {
                helper.setImageDrawable(R.id.ivIconLeftDay, null);
            }
            if (iconNight != 0) {
                helper.setImageResource(R.id.ivIconLeftNight, iconNight);
            } else {
                helper.setImageDrawable(R.id.ivIconLeftNight, null);
            }
            helper.setText(R.id.tvTempLeft, temp);
            helper.setText(R.id.tvWindLeft, wind);
        }
        // my
        WeatherForecast myForecast = item.getMyForecast();
        if (myForecast == null) {
            helper.setVisible(R.id.rlWeatherRight, false);
            helper.setVisible(R.id.tvShowRight, true);
            helper.setText(R.id.tvShowRight, R.string.now_no_weather_info);
        } else {
            helper.setVisible(R.id.rlWeatherRight, true);
            helper.setVisible(R.id.tvShowRight, false);
            // myData
            long taTime = myForecast.getTimeAt();
            if (taTime > 0) time = taTime;
            String condition = String.format(Locale.getDefault(), formatWave, myForecast.getConditionDay(), myForecast.getConditionNight());
            int iconDay = WeatherHelper.getIconById(myForecast.getIconDay());
            int iconNight = WeatherHelper.getIconById(myForecast.getIconNight());
            String temp = String.format(Locale.getDefault(), formatWaveC, myForecast.getTempDay(), myForecast.getTempNight());
            String wind = String.format(Locale.getDefault(), formatWave, myForecast.getWindDay(), myForecast.getWindNight());
            // rightView
            helper.setText(R.id.tvConditionRight, condition);
            if (iconDay != 0) {
                helper.setImageResource(R.id.ivIconRightDay, iconDay);
            } else {
                helper.setImageDrawable(R.id.ivIconRightDay, null);
            }
            if (iconNight != 0) {
                helper.setImageResource(R.id.ivIconRightNight, iconNight);
            } else {
                helper.setImageDrawable(R.id.ivIconRightNight, null);
            }
            helper.setText(R.id.tvTempRight, temp);
            helper.setText(R.id.tvWindRight, wind);
        }
        String timeShow = TimeHelper.getTimeShowLocal_MD_YMD_ByGo(time);
        helper.setText(R.id.tvTime, timeShow);
    }

    // 把两个单独的WeatherForecastList 封装成CoupleWeatherForecastList
    public void setData(List<WeatherForecast> myWeatherForecastList, List<WeatherForecast> taWeatherForecastList) {
        // my
        List<WeatherForecast> myForecastList = new ArrayList<>();
        if (myWeatherForecastList != null && myWeatherForecastList.size() >= 1) {
            myForecastList.addAll(myWeatherForecastList);
            myForecastList.remove(0); // 去掉第一个
        }
        // ta
        List<WeatherForecast> taForecastList = new ArrayList<>();
        if (taWeatherForecastList != null && taWeatherForecastList.size() >= 1) {
            taForecastList.addAll(taWeatherForecastList);
            taForecastList.remove(0); // 去掉第一个
        }
        // cp
        List<CoupleWeatherForecast> coupleWeatherForecastList = new ArrayList<>();
        int myForecastSize = myForecastList.size();
        int taForecastSize = taForecastList.size();
        int cpForecastSize = Math.max(myForecastSize, taForecastSize);
        for (int i = 0; i < cpForecastSize; i++) {
            WeatherForecast myForecast = null;
            if (myForecastSize > i) {
                myForecast = myForecastList.get(i);
            }
            WeatherForecast taForecast = null;
            if (taForecastSize > i) {
                taForecast = taForecastList.get(i);
            }
            CoupleWeatherForecast coupleWeatherForecast = new CoupleWeatherForecast();
            coupleWeatherForecast.setMyForecast(myForecast);
            coupleWeatherForecast.setTaForecast(taForecast);
            coupleWeatherForecastList.add(coupleWeatherForecast);
        }
        setNewData(coupleWeatherForecastList);
    }

    static class CoupleWeatherForecast {
        WeatherForecast myForecast;
        WeatherForecast taForecast;

        WeatherForecast getMyForecast() {
            return myForecast;
        }

        void setMyForecast(WeatherForecast myForecast) {
            this.myForecast = myForecast;
        }

        WeatherForecast getTaForecast() {
            return taForecast;
        }

        void setTaForecast(WeatherForecast taForecast) {
            this.taForecast = taForecast;
        }
    }

}
