package com.jiangzg.mianmian.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.Weather;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.RetrofitHelper;

import retrofit2.Call;

public class CoupleWeatherActivity extends BaseActivity<CoupleWeatherActivity> {

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, CoupleWeatherActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    private Call<Result> call;

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_couple_weather;
    }

    @Override
    protected void initView(Bundle state) {

    }

    @Override
    protected void initData(Bundle state) {
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(call);
    }

    private void getData() {
        // TODO srl
        call = new RetrofitHelper().call(API.class).weatherForecastGet();
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // TODO
                String myShow = data.getMyShow();
                String taShow = data.getTaShow();
                Weather myWeather = data.getMyWeather();
                Weather taWeather = data.getTaWeather();
            }

            @Override
            public void onFailure(String errMsg) {
                // TODO srl
            }
        });
    }

}
