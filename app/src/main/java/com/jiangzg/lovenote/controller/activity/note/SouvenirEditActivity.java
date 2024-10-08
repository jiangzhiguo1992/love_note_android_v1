package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.view.DialogUtils;
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
    @BindView(R.id.llHappenAt)
    LinearLayout llHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.llAddress)
    LinearLayout llAddress;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.llType)
    LinearLayout llType;
    @BindView(R.id.tvType)
    TextView tvType;

    private Souvenir souvenir;

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
            ToastUtils.show(from.getString(R.string.can_operation_self_create_note));
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
        refreshTypeView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<LocationInfo> obSelectMap = RxBus.register(RxBus.EVENT_MAP_SELECT, info -> {
            if (info == null || souvenir == null) return;
            souvenir.setLatitude(info.getLatitude());
            souvenir.setLongitude(info.getLongitude());
            souvenir.setAddress(info.getAddress());
            souvenir.setCityId(info.getCityId());
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
                checkPush();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llHappenAt, R.id.llAddress, R.id.llType})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.llAddress: // 地址
                if (souvenir == null) return;
                MapSelectActivity.goActivity(mActivity, souvenir.getAddress(), souvenir.getLongitude(), souvenir.getLatitude());
                break;
            case R.id.llType: // 类型
                showTypeDialog();
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", BaseActivity.ACT_EDIT_FROM_ADD) == BaseActivity.ACT_EDIT_FROM_UPDATE;
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
        tvHappenAt.setText(String.format(Locale.getDefault(), getString(R.string.time_colon_space_holder), happen));
    }

    private void refreshLocationView() {
        if (souvenir == null) return;
        String address = StringUtils.isEmpty(souvenir.getAddress()) ? getString(R.string.now_no) : souvenir.getAddress();
        tvAddress.setText(String.format(Locale.getDefault(), getString(R.string.address_colon_space_holder), address));
    }

    private void showTypeDialog() {
        int searchIndex = souvenir.isDone() ? 0 : 1;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.please_select_classify)
                .items(new String[]{getString(R.string.souvenir), getString(R.string.wish_list)})
                .itemsCallbackSingleChoice(searchIndex, (dialog1, view, which, text) -> {
                    if (which < 0 || which > 1) {
                        return true;
                    }
                    souvenir.setDone(which == 0);
                    refreshTypeView();
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void refreshTypeView() {
        if (souvenir == null) return;
        if (souvenir.isDone()) {
            tvType.setText(String.format(Locale.getDefault(), getString(R.string.type_colon_space_holder), getString(R.string.souvenir)));
        } else {
            tvType.setText(String.format(Locale.getDefault(), getString(R.string.type_colon_space_holder), getString(R.string.wish_list)));
        }
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
        Call<Result> api = new RetrofitHelper().call(API.class).noteSouvenirUpdateBody(souvenir);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
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
        pushApi(api);
    }

    private void addApi() {
        if (souvenir == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteSouvenirAdd(souvenir);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
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
        pushApi(api);
    }

}
