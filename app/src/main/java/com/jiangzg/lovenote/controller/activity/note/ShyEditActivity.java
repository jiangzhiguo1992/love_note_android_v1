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

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Shy;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class ShyEditActivity extends BaseActivity<ShyEditActivity> {

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
    @BindView(R.id.llEndAt)
    LinearLayout llEndAt;
    @BindView(R.id.tvEndAt)
    TextView tvEndAt;
    @BindView(R.id.llSafe)
    LinearLayout llSafe;
    @BindView(R.id.tvSafe)
    TextView tvSafe;

    private Shy shy;
    private int limitContentLength;
    private String[] safeItems;
    private int safeIndex;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, ShyEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_shy_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.shy), true);
        // init
        shy = new Shy();
        // happen
        shy.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        refreshHappenView();
        // end
        shy.setEndAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong() + TimeUnit.MIN * 10));
        refreshEndView();
        // safe
        safeItems = new String[]{getString(R.string.nil), getString(R.string.condom), getString(R.string.acyeterion), getString(R.string.out_shoot), getString(R.string.other)};
        safeIndex = 0;
        shy.setSafe(safeItems[safeIndex]);
        refreshSafeView();
        // content
        etContent.setText(shy.getDesc());
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
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

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.llHappenAt, R.id.llEndAt, R.id.llSafe})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llHappenAt: // 开始日期
                showHappenPicker();
                break;
            case R.id.llEndAt: // 结束日期
                showEndPicker();
                break;
            case R.id.llSafe: // 安全措施
                showSafeDialog();
                break;
        }
    }

    private void onContentInput(String input) {
        if (shy == null || input == null) return;
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getShyDescLength();
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
        shy.setDesc(etContent.getText().toString());
    }

    private void showHappenPicker() {
        if (shy == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(shy.getHappenAt()), time -> {
            shy.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshHappenView();
        });
    }

    private void refreshHappenView() {
        if (shy == null) return;
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(shy.getHappenAt());
        tvHappenAt.setText(String.format(Locale.getDefault(), getString(R.string.start_time_colon_holder), happen));
    }

    private void showEndPicker() {
        if (shy == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(shy.getEndAt()), time -> {
            shy.setEndAt(TimeHelper.getGoTimeByJava(time));
            refreshEndView();
        });
    }

    private void refreshEndView() {
        if (shy == null) return;
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(shy.getEndAt());
        tvEndAt.setText(String.format(Locale.getDefault(), getString(R.string.end_time_colon_holder), happen));
    }

    private void showSafeDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.please_select_safe_method)
                .items(safeItems)
                .itemsCallbackSingleChoice(safeIndex, (dialog1, view, which, text) -> {
                    if (which < 0 || which >= safeItems.length) {
                        return true;
                    }
                    safeIndex = which;
                    shy.setSafe(safeItems[safeIndex]);
                    refreshSafeView();
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void refreshSafeView() {
        if (shy == null) return;
        tvSafe.setText(String.format(Locale.getDefault(), getString(R.string.safe_method_colon_holder), shy.getSafe()));
    }

    private void push() {
        if (shy == null) return;
        if (StringUtils.isEmpty(shy.getSafe())) {
            ToastUtils.show(getString(R.string.please_select_safe_method));
            return;
        } else if (StringUtils.isEmpty(shy.getDesc())) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteShyAdd(shy);
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SHY_LIST_REFRESH, new ArrayList<>()));
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
