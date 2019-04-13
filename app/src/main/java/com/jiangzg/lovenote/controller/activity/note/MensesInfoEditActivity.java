package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Limit;
import com.jiangzg.lovenote.model.entity.MensesInfo;
import com.jiangzg.lovenote.model.entity.MensesLength;
import com.jiangzg.lovenote.view.GNumberPicker;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class MensesInfoEditActivity extends BaseActivity<MensesInfoEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.llMe)
    LinearLayout llMe;
    @BindView(R.id.tvMeCycle)
    TextView tvMeCycle;
    @BindView(R.id.tvMeDuration)
    TextView tvMeDuration;
    @BindView(R.id.llTa)
    LinearLayout llTa;
    @BindView(R.id.tvTaCycle)
    TextView tvTaCycle;
    @BindView(R.id.tvTaDuration)
    TextView tvTaDuration;
    @BindView(R.id.npDay)
    GNumberPicker npDay;

    private Limit limit;
    private MensesInfo mensesInfo;
    private MensesLength lengthMe;
    private MensesLength lengthTa;
    private int selectId;

    public static void goActivity(Activity from, MensesInfo info) {
        if (info == null || (!info.isCanMe() && !info.isCanTa())) return;
        Intent intent = new Intent(from, MensesInfoEditActivity.class);
        intent.putExtra("info", info);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_menses_info_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.menses_info), true);
        // init
        npDay.setMinValue(1);
        npDay.setFormatter(value -> String.valueOf(value) + mActivity.getString(R.string.dayT));
        npDay.setOnValueChangedListener((picker, oldVal, newVal) -> {
            switch (selectId) {
                case R.id.tvMeCycle:
                    if (lengthMe != null && newVal > 0 && newVal <= limit.getMensesMaxCycleDay()) {
                        lengthMe.setCycleDay(newVal);
                    }
                    break;
                case R.id.tvMeDuration:
                    if (lengthMe != null && newVal > 0 && newVal <= limit.getMensesMaxDurationDay()) {
                        lengthMe.setDurationDay(newVal);
                    }
                    break;
                case R.id.tvTaCycle:
                    if (lengthTa != null && newVal > 0 && newVal <= limit.getMensesMaxCycleDay()) {
                        lengthTa.setCycleDay(newVal);
                    }
                    break;
                case R.id.tvTaDuration:
                    if (lengthTa != null && newVal > 0 && newVal <= limit.getMensesMaxDurationDay()) {
                        lengthTa.setDurationDay(newVal);
                    }
                    break;
            }
            refreshView();
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // data
        selectId = 0;
        limit = SPHelper.getLimit();
        mensesInfo = getIntent().getParcelableExtra("info");
        if (mensesInfo == null) {
            mActivity.finish();
            return;
        }
        lengthMe = mensesInfo.getMensesLengthMe();
        lengthTa = mensesInfo.getMensesLengthTa();
        // view
        llMe.setVisibility(mensesInfo.isCanMe() ? View.VISIBLE : View.GONE);
        llTa.setVisibility(mensesInfo.isCanTa() ? View.VISIBLE : View.GONE);
        refreshView();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuCommit: // 提交
                push();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.tvMeCycle, R.id.tvMeDuration, R.id.tvTaCycle, R.id.tvTaDuration})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvMeCycle:
            case R.id.tvMeDuration:
            case R.id.tvTaCycle:
            case R.id.tvTaDuration:
                toggleNumberPicker(view.getId());
                break;
        }
    }

    private void refreshView() {
        int meCycle = lengthMe == null ? limit.getMensesDefaultCycleDay() : lengthMe.getCycleDay();
        int meDuration = lengthMe == null ? limit.getMensesDefaultDurationDay() : lengthMe.getDurationDay();
        int taCycle = lengthTa == null ? limit.getMensesDefaultCycleDay() : lengthTa.getCycleDay();
        int taDuration = lengthTa == null ? limit.getMensesDefaultDurationDay() : lengthTa.getDurationDay();
        tvMeCycle.setText(String.format(Locale.getDefault(), getString(R.string.menses_cycle_colon_holder_day), meCycle));
        tvMeDuration.setText(String.format(Locale.getDefault(), getString(R.string.menses_duration_colon_holder_day), meDuration));
        tvTaCycle.setText(String.format(Locale.getDefault(), getString(R.string.menses_cycle_colon_holder_day), taCycle));
        tvTaDuration.setText(String.format(Locale.getDefault(), getString(R.string.menses_duration_colon_holder_day), taDuration));
    }

    private void toggleNumberPicker(int resId) {
        // view
        if (selectId == resId) {
            selectId = 0;
            npDay.setVisibility(View.GONE);
            return;
        }
        selectId = resId;
        npDay.setVisibility(View.VISIBLE);
        // data
        int maxCycle = limit.getMensesMaxCycleDay();
        int maxDuration = limit.getMensesMaxDurationDay();
        int meCycle = lengthMe == null ? limit.getMensesDefaultCycleDay() : lengthMe.getCycleDay();
        int meDuration = lengthMe == null ? limit.getMensesDefaultDurationDay() : lengthMe.getDurationDay();
        int taCycle = lengthTa == null ? limit.getMensesDefaultCycleDay() : lengthTa.getCycleDay();
        int taDuration = lengthTa == null ? limit.getMensesDefaultDurationDay() : lengthTa.getDurationDay();
        switch (resId) {
            case R.id.tvMeCycle:
                npDay.setMaxValue(maxCycle);
                npDay.setValue(meCycle);
                break;
            case R.id.tvMeDuration:
                npDay.setMaxValue(maxDuration);
                npDay.setValue(meDuration);
                break;
            case R.id.tvTaCycle:
                npDay.setMaxValue(maxCycle);
                npDay.setValue(taCycle);
                break;
            case R.id.tvTaDuration:
                npDay.setMaxValue(maxDuration);
                npDay.setValue(taDuration);
                break;
        }
    }

    private void push() {
        if (mensesInfo == null) return;
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteMensesInfoUpdate(mensesInfo);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_MENSES_INFO_UPDATE, mensesInfo));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
