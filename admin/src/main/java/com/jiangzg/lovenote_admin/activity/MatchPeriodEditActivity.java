package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.MatchPeriod;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.ApiHelper;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class MatchPeriodEditActivity extends BaseActivity<MatchPeriodEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.btnKind)
    Button btnKind;
    @BindView(R.id.btnCoinChange)
    Button btnCoinChange;
    @BindView(R.id.btnStart)
    Button btnStart;
    @BindView(R.id.btnEnd)
    Button btnEnd;
    @BindView(R.id.btnPush)
    Button btnPush;

    private int kindIndex, coinChange;
    private long startAt, endAt;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, MatchPeriodEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_match_period_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "match_period_edit", true);
        refreshDateView();
        // kind
        kindIndex = 0;
        btnKind.setText("类型:" + ApiHelper.LIST_MATCH_KIND_SHOW[kindIndex]);
        // coin
        coinChange = 100;
        btnCoinChange.setText("奖励金币:" + coinChange);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.btnKind, R.id.btnCoinChange, R.id.btnStart, R.id.btnEnd, R.id.btnPush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnKind:
                showKindSelectDialog();
                break;
            case R.id.btnCoinChange:
                showCoinInputDialog();
                break;
            case R.id.btnStart:
                DialogHelper.showDateTimePicker(mActivity, startAt, new DialogHelper.OnPickListener() {
                    @Override
                    public void onPick(long time) {
                        startAt = time;
                        refreshDateView();
                    }
                });
                break;
            case R.id.btnEnd:
                DialogHelper.showDateTimePicker(mActivity, endAt, new DialogHelper.OnPickListener() {
                    @Override
                    public void onPick(long time) {
                        endAt = time;
                        refreshDateView();
                    }
                });
                break;
            case R.id.btnPush:
                push();
                break;
        }
    }

    private void refreshDateView() {
        if (startAt <= 0) {
            startAt = DateUtils.getCurrentLong();
        }
        if (endAt <= 0) {
            endAt = DateUtils.getCurrentLong();
        }
        String start = "s: " + DateUtils.getStr(startAt, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String end = "e: " + DateUtils.getStr(endAt, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        btnStart.setText(start);
        btnEnd.setText(end);
    }

    private void showKindSelectDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.LIST_MATCH_KIND_SHOW)
                .itemsCallbackSingleChoice(kindIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        kindIndex = which;
                        btnKind.setText("类型:" + ApiHelper.LIST_MATCH_KIND_SHOW[kindIndex]);
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void showCoinInputDialog() {
        MaterialDialog dialogName = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .autoDismiss(true)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("金币数量", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        LogUtils.i(CoupleDetailActivity.class, "onInput", input.toString());
                    }
                })
                .inputRange(1, 5)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // api
                        EditText editText = dialog.getInputEditText();
                        if (editText != null) {
                            String input = editText.getText().toString();
                            if (StringUtils.isNumber(input)) {
                                coinChange = Integer.parseInt(input);
                                btnCoinChange.setText("奖励金币:" + coinChange);
                            }
                        }
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialogName);
    }

    private void push() {
        MatchPeriod period = new MatchPeriod();
        period.setKind(ApiHelper.LIST_MATCH_KIND_TYPE[kindIndex]);
        period.setCoinChange(coinChange);
        period.setStartAt(startAt / 1000);
        period.setEndAt(endAt / 1000);
        period.setTitle(etTitle.getText().toString());
        MaterialDialog loading = getLoading(false);
        Call<Result> call = new RetrofitHelper().call(API.class).moreMatchPeriodAdd(period);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
