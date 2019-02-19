package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.MapSelectActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.entity.TravelPlace;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import rx.Observable;

public class TravelPlaceEditActivity extends BaseActivity<TravelPlaceEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.llHappenAt)
    LinearLayout llHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.llAddress)
    LinearLayout llAddress;
    @BindView(R.id.tvAddress)
    TextView tvAddress;

    private TravelPlace place;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, TravelPlaceEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_travel_place_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.track), true);
        // init
        place = new TravelPlace();
        place.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        // input
        etContent.setText(place.getContentText());
        // date
        refreshDateView();
        // location
        refreshLocationView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<LocationInfo> obSelectMap = RxBus.register(RxBus.EVENT_MAP_SELECT, info -> {
            if (info == null || place == null) return;
            place.setLatitude(info.getLatitude());
            place.setLongitude(info.getLongitude());
            place.setAddress(info.getAddress());
            place.setCityId(info.getCityId());
            refreshLocationView();
        });
        pushBus(RxBus.EVENT_MAP_SELECT, obSelectMap);
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
                commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.llHappenAt, R.id.llAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.llAddress: // 地址
                if (place == null) return;
                MapSelectActivity.goActivity(mActivity, place.getAddress(), place.getLongitude(), place.getLatitude());
                break;
        }
    }

    private void showDatePicker() {
        if (place == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(place.getHappenAt()), time -> {
            place.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (place == null) return;
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(place.getHappenAt());
        tvHappenAt.setText(String.format(Locale.getDefault(), getString(R.string.time_colon_space_holder), happen));
    }

    private void refreshLocationView() {
        if (place == null) return;
        String address = StringUtils.isEmpty(place.getAddress()) ? getString(R.string.now_no) : place.getAddress();
        tvAddress.setText(String.format(Locale.getDefault(), getString(R.string.address_colon_space_holder), address));
    }

    private void onContentInput(String input) {
        if (place == null) return;
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getTravelPlaceContentLength();
        }
        int length = input.length();
        if (length > limitContentLength) {
            CharSequence charSequence = input.subSequence(0, limitContentLength);
            etContent.setText(charSequence);
            etContent.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitContentLength);
        tvContentLimit.setText(limitShow);
        // 设置进去
        place.setContentText(etContent.getText().toString());
    }

    private void commit() {
        if (place == null) return;
        if (StringUtils.isEmpty(place.getContentText())) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        }
        // event
        RxBus.post(new RxBus.Event<>(RxBus.EVENT_TRAVEL_EDIT_ADD_PLACE, place));
        mActivity.finish();
    }

}
