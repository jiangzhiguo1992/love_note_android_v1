package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Souvenir;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class SouvenirEditActivity extends BaseActivity<SouvenirEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.cvHappenAt)
    CardView cvHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.cvAddress)
    CardView cvAddress;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.rgType)
    RadioGroup rgType;
    @BindView(R.id.rbDone)
    RadioButton rbDone;
    @BindView(R.id.rbWish)
    RadioButton rbWish;

    private Souvenir souvenir;
    private Observable<LocationInfo> obSelectMap;
    private Call<Result> callAdd;
    private Call<Result> callUpdate;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SouvenirEditActivity.class);
        intent.putExtra("from", BaseActivity.ACT_EDIT_FROM_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Souvenir souvenir) {
        if (souvenir == null) {
            goActivity(from);
            return;
        } else if (!souvenir.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_souvenir));
            return;
        }
        Intent intent = new Intent(from, SouvenirEditActivity.class);
        intent.putExtra("from", BaseActivity.ACT_EDIT_FROM_UPDATE);
        intent.putExtra("souvenir", souvenir);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_souvenir_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        // init
        if (isFromUpdate()) {
            souvenir = intent.getParcelableExtra("souvenir");
        }
        if (souvenir == null) {
            souvenir = new Souvenir();
            souvenir.setDone(true);
        }
        if (souvenir.getHappenAt() == 0) {
            souvenir.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        }
        // toolBar
        String title = souvenir.isDone() ? getString(R.string.souvenir) : getString(R.string.wish_list);
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // etTitle
        String format = getString(R.string.please_input_title_no_over_holder_text);
        String hint = String.format(Locale.getDefault(), format, SPHelper.getLimit().getSouvenirTitleLength());
        etTitle.setHint(hint);
        etTitle.setText(souvenir.getTitle());
        // date
        refreshDateView();
        // location
        refreshLocationView();
        // type
        initTypeCheck();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obSelectMap = RxBus.register(RxBus.EVENT_MAP_SELECT, info -> {
            if (info == null || souvenir == null) return;
            souvenir.setLatitude(info.getLatitude());
            souvenir.setLongitude(info.getLongitude());
            souvenir.setAddress(info.getAddress());
            souvenir.setCityId(info.getCityId());
            refreshLocationView();
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
        RxBus.unregister(RxBus.EVENT_MAP_SELECT, obSelectMap);
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
                checkPush();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.cvHappenAt, R.id.cvAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.cvAddress: // 地址
                if (souvenir == null) return;
                MapSelectActivity.goActivity(mActivity, souvenir.getAddress(), souvenir.getLongitude(), souvenir.getLatitude());
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", BaseActivity.ACT_EDIT_FROM_ADD) == BaseActivity.ACT_EDIT_FROM_UPDATE;
    }

    private void initTypeCheck() {
        if (souvenir == null) return;
        rgType.setOnCheckedChangeListener((group, checkedId) -> {
            if (souvenir == null) return;
            switch (checkedId) {
                case R.id.rbDone: // 送给我
                    souvenir.setDone(true);
                    break;
                case R.id.rbWish: // 送给ta
                    souvenir.setDone(false);
                    break;
            }
        });
        if (souvenir.isDone()) {
            rbDone.setChecked(true);
        } else {
            rbWish.setChecked(true);
        }
    }

    private void showDatePicker() {
        if (souvenir == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(souvenir.getHappenAt()), time -> {
            souvenir.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (souvenir == null) return;
        long happenAt = TimeHelper.getJavaTimeByGo(souvenir.getHappenAt());
        String happen = DateUtils.getStr(happenAt, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        tvHappenAt.setText(happen);
    }

    private void refreshLocationView() {
        if (souvenir == null) return;
        String location = StringUtils.isEmpty(souvenir.getAddress()) ? getString(R.string.now_no) : souvenir.getAddress();
        tvAddress.setText(location);
    }

    private void checkPush() {
        if (souvenir == null) return;
        String title = etTitle.getText().toString();
        if (StringUtils.isEmpty(title)) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        } else if (title.length() > SPHelper.getLimit().getSouvenirTitleLength()) {
            ToastUtils.show(etTitle.getHint().toString());
            return;
        }
        souvenir.setTitle(title);
        if (isFromUpdate()) {
            updateApi();
        } else {
            addApi();
        }
    }

    private void updateApi() {
        if (souvenir == null) return;
        MaterialDialog loading = getLoading(false);
        callUpdate = new RetrofitHelper().call(API.class).noteSouvenirUpdateBody(souvenir);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Souvenir souvenir = data.getSouvenir();
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SOUVENIR_DETAIL_REFRESH, souvenir));
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SOUVENIR_LIST_ITEM_REFRESH, souvenir));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void addApi() {
        if (souvenir == null) return;
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).noteSouvenirAdd(souvenir);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SOUVENIR_LIST_REFRESH, new ArrayList<>()));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
