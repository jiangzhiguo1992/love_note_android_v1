package com.jiangzg.lovenote.controller.activity.couple;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.couple.WeatherAdapter;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.common.WeatherHelper;
import com.jiangzg.lovenote.helper.system.LocationHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.model.entity.WeatherForecast;
import com.jiangzg.lovenote.model.entity.WeatherForecastInfo;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CoupleWeatherActivity extends BaseActivity<CoupleWeatherActivity> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.tvTime)
    TextView tvTime;

    @BindView(R.id.llToady)
    LinearLayout llToady;
    @BindView(R.id.llTopLeft)
    LinearLayout llTopLeft;
    @BindView(R.id.llTopRight)
    LinearLayout llTopRight;
    @BindView(R.id.tvShowLeft)
    TextView tvShowLeft;
    @BindView(R.id.tvShowRight)
    TextView tvShowRight;

    @BindView(R.id.ivAvatarLeft)
    FrescoAvatarView ivAvatarLeft;
    @BindView(R.id.ivIconLeftDay)
    ImageView ivIconLeftDay;
    @BindView(R.id.ivIconLeftNight)
    ImageView ivIconLeftNight;
    @BindView(R.id.tvConditionLeft)
    TextView tvConditionLeft;
    @BindView(R.id.tvTempLeft)
    TextView tvTempLeft;
    @BindView(R.id.tvWindLeft)
    TextView tvWindLeft;

    @BindView(R.id.ivAvatarRight)
    FrescoAvatarView ivAvatarRight;
    @BindView(R.id.ivIconRightDay)
    ImageView ivIconRightDay;
    @BindView(R.id.ivIconRightNight)
    ImageView ivIconRightNight;
    @BindView(R.id.tvConditionRight)
    TextView tvConditionRight;
    @BindView(R.id.tvTempRight)
    TextView tvTempRight;
    @BindView(R.id.tvWindRight)
    TextView tvWindRight;

    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;

    public static void goActivity(Fragment from) {
        if (!LocationHelper.checkLocationEnable(from)) return;
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), CoupleWeatherActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from) {
        //if (!LocationHelper.checkLocationEnable(from)) return;
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from, CoupleWeatherActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarTrans(mActivity, true);
        return R.layout.activity_couple_weather;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        tvTime.setText(DateUtils.getCurrentStr(DateUtils.FORMAT_CHINA_M_D));
        // srl
        srl.setEnabled(false);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new WeatherAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_white, true, true)
                .setAdapter();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // avatar
        User me = SPHelper.getMe();
        User ta = SPHelper.getTa();
        String taAvatar = UserHelper.getTaAvatar(me);
        String myAvatar = UserHelper.getMyAvatar(me);
        ivAvatarLeft.setData(taAvatar, ta);
        ivAvatarRight.setData(myAvatar, me);
        // data
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.ivBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivBack: // 返回
                mActivity.finish();
                break;
        }
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).coupleWeatherForecastListGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (srl == null || recyclerHelper == null) return;
                srl.setRefreshing(false);
                // right
                WeatherForecastInfo weatherForecastMe = data.getWeatherForecastMe();
                if (weatherForecastMe == null) {
                    weatherForecastMe = new WeatherForecastInfo();
                }
                String myShow = weatherForecastMe.getShow();
                List<WeatherForecast> myWeatherForecastList = weatherForecastMe.getWeatherForecastList();
                setTopViewRight(myShow, myWeatherForecastList);
                // left
                WeatherForecastInfo dataWeatherForecastTa = data.getWeatherForecastTa();
                if (dataWeatherForecastTa == null) {
                    dataWeatherForecastTa = new WeatherForecastInfo();
                }
                String taShow = dataWeatherForecastTa.getShow();
                List<WeatherForecast> taWeatherForecastList = dataWeatherForecastTa.getWeatherForecastList();
                setTopViewLeft(taShow, taWeatherForecastList);
                // recycler
                WeatherAdapter adapter = recyclerHelper.getAdapter();
                adapter.setData(myWeatherForecastList, taWeatherForecastList);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (srl == null || recyclerHelper == null) return;
                srl.setRefreshing(false);
                recyclerHelper.dataFail(false, message);
            }
        });
        pushApi(api);
    }

    private void setTopViewRight(String msg, List<WeatherForecast> forecastList) {
        if (forecastList == null || forecastList.size() <= 0) {
            tvShowRight.setVisibility(View.VISIBLE);
            llTopRight.setVisibility(View.GONE);
            tvShowRight.setText(msg);
            return;
        }
        tvShowRight.setVisibility(View.GONE);
        llTopRight.setVisibility(View.VISIBLE);
        // data
        WeatherForecast forecast = forecastList.get(0);
        String condition = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_wave_holder), forecast.getConditionDay(), forecast.getConditionNight());
        int iconDay = WeatherHelper.getIconById(forecast.getIconDay());
        int iconNight = WeatherHelper.getIconById(forecast.getIconNight());
        String temp = String.format(Locale.getDefault(), getString(R.string.holder_wave_holder_c), forecast.getTempDay(), forecast.getTempNight());
        String wind = String.format(Locale.getDefault(), getString(R.string.holder_wave_holder), forecast.getWindDay(), forecast.getWindNight());
        // view
        tvConditionRight.setText(condition);
        tvTempRight.setText(temp);
        tvWindRight.setText(wind);
        if (iconDay > 0) {
            ivIconRightDay.setImageResource(iconDay);
        }
        if (iconNight > 0) {
            ivIconRightNight.setImageResource(iconNight);
        }
    }

    private void setTopViewLeft(String msg, List<WeatherForecast> forecastList) {
        if (forecastList == null || forecastList.size() <= 0) {
            tvShowLeft.setVisibility(View.VISIBLE);
            llTopLeft.setVisibility(View.GONE);
            tvShowLeft.setText(msg);
            return;
        }
        tvShowLeft.setVisibility(View.GONE);
        llTopLeft.setVisibility(View.VISIBLE);
        // data
        WeatherForecast forecast = forecastList.get(0);
        String condition = String.format(Locale.getDefault(), getString(R.string.holder_wave_holder), forecast.getConditionDay(), forecast.getConditionNight());
        int iconDay = WeatherHelper.getIconById(forecast.getIconDay());
        int iconNight = WeatherHelper.getIconById(forecast.getIconNight());
        String temp = String.format(Locale.getDefault(), getString(R.string.holder_wave_holder_c), forecast.getTempDay(), forecast.getTempNight());
        String wind = String.format(Locale.getDefault(), getString(R.string.holder_wave_holder), forecast.getWindDay(), forecast.getWindNight());
        // view
        tvConditionLeft.setText(condition);
        tvTempLeft.setText(temp);
        tvWindLeft.setText(wind);
        if (iconDay > 0) {
            ivIconLeftDay.setImageResource(iconDay);
        }
        if (iconNight > 0) {
            ivIconLeftNight.setImageResource(iconNight);
        }
    }

}
