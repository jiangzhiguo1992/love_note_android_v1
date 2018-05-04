package com.jiangzg.mianmian.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.domain.Weather;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.view.GImageAvatarView;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CoupleWeatherActivity extends BaseActivity<CoupleWeatherActivity> {

    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.ivHelp)
    ImageView ivHelp;

    @BindView(R.id.llToady)
    LinearLayout llToady;
    @BindView(R.id.rlTopLeft)
    RelativeLayout rlTopLeft;
    @BindView(R.id.rlTopRight)
    RelativeLayout rlTopRight;
    @BindView(R.id.tvShowLeft)
    TextView tvShowLeft;
    @BindView(R.id.tvShowRight)
    TextView tvShowRight;

    @BindView(R.id.ivAvatarLeft)
    GImageAvatarView ivAvatarLeft;
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
    GImageAvatarView ivAvatarRight;
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

    private String myAvatar;
    private String taAvatar;
    private Call<Result> call;

    public static void goActivity(Activity from) {
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
    protected void initView(Bundle state) {
        srl.setEnabled(false);
    }

    @Override
    protected void initData(Bundle state) {
        User user = SPHelper.getUser();
        myAvatar = user.getMyAvatarInCp();
        taAvatar = user.getTaAvatarInCp();
        getData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(call);
    }

    @OnClick({R.id.ivBack, R.id.ivHelp})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                break;
            case R.id.ivHelp:
                break;
        }
    }

    private void getData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        call = new RetrofitHelper().call(API.class).weatherForecastGet();
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                setTopViewLeft(data.getTaShow(), data.getTaWeather());
                setTopViewRight(data.getMyShow(), data.getMyWeather());
                // TODO recycler
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
            }
        });
    }

    private void setTopViewLeft(String taShow, Weather taWeather) {
        if (!StringUtils.isEmpty(taShow) || taWeather == null || taWeather.getForecast() == null || taWeather.getForecast().size() <= 0) {
            tvShowLeft.setVisibility(View.VISIBLE);
            rlTopLeft.setVisibility(View.GONE);
            tvShowLeft.setText(taShow);
            return;
        }
        tvShowLeft.setVisibility(View.GONE);
        rlTopLeft.setVisibility(View.VISIBLE);
        // data
        Weather.Forecast forecast = taWeather.getForecast().get(0);
        String conditionDay = forecast.getConditionDay();
        String conditionNight = forecast.getConditionNight();
        String condition = String.format(Locale.getDefault(), "%s~%s", conditionDay, conditionNight);
        String conditionIdDay = forecast.getConditionIdDay();
        int iconDay = ConvertHelper.getWeatherIconById(conditionIdDay);
        String conditionIdNight = forecast.getConditionIdNight();
        int iconNight = ConvertHelper.getWeatherIconById(conditionIdNight);
        String tempDay = forecast.getTempDay();
        String tempNight = forecast.getTempNight();
        String temp = String.format(Locale.getDefault(), getString(R.string.holder_wave_holder_c), tempDay, tempNight);
        String windLevelDay = forecast.getWindLevelDay();
        String windDirDay = forecast.getWindDirDay();
        String windLevelNight = forecast.getWindLevelNight();
        String windDirNight = forecast.getWindDirNight();
        String wind = String.format(Locale.getDefault(), getString(R.string.holder_level_holder_holder_level_holder),
                windLevelDay, windDirDay, windLevelNight, windDirNight);
        // view
        ivAvatarLeft.setDateAvatar(taAvatar);
        ivIconLeftDay.setImageResource(iconDay);
        ivIconLeftNight.setImageResource(iconNight);
        tvConditionLeft.setText(condition);
        tvTempLeft.setText(temp);
        tvWindLeft.setText(wind);
    }

    private void setTopViewRight(String myShow, Weather myWeather) {
        if (!StringUtils.isEmpty(myShow) || myWeather == null || myWeather.getForecast() == null || myWeather.getForecast().size() <= 0) {
            tvShowRight.setVisibility(View.VISIBLE);
            rlTopRight.setVisibility(View.GONE);
            tvShowRight.setText(myShow);
            return;
        }
        tvShowRight.setVisibility(View.GONE);
        rlTopRight.setVisibility(View.VISIBLE);
        // data
        Weather.Forecast forecast = myWeather.getForecast().get(0);
        String conditionDay = forecast.getConditionDay();
        String conditionNight = forecast.getConditionNight();
        String condition = String.format(Locale.getDefault(), "%s~%s", conditionDay, conditionNight);
        String conditionIdDay = forecast.getConditionIdDay();
        int iconDay = ConvertHelper.getWeatherIconById(conditionIdDay);
        String conditionIdNight = forecast.getConditionIdNight();
        int iconNight = ConvertHelper.getWeatherIconById(conditionIdNight);
        String tempDay = forecast.getTempDay();
        String tempNight = forecast.getTempNight();
        String temp = String.format(Locale.getDefault(), getString(R.string.holder_wave_holder_c), tempDay, tempNight);
        String windLevelDay = forecast.getWindLevelDay();
        String windDirDay = forecast.getWindDirDay();
        String windLevelNight = forecast.getWindLevelNight();
        String windDirNight = forecast.getWindDirNight();
        String wind = String.format(Locale.getDefault(), getString(R.string.holder_level_holder_holder_level_holder),
                windLevelDay, windDirDay, windLevelNight, windDirNight);
        // view
        ivAvatarRight.setDateAvatar(myAvatar);
        ivIconRightDay.setImageResource(iconDay);
        ivIconRightNight.setImageResource(iconNight);
        tvConditionRight.setText(condition);
        tvTempRight.setText(temp);
        tvWindRight.setText(wind);
    }

}
