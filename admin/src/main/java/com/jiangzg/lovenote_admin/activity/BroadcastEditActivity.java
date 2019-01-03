package com.jiangzg.lovenote_admin.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Broadcast;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.SPHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class BroadcastEditActivity extends BaseActivity<BroadcastEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.rbTypeText)
    RadioButton rbTypeText;
    @BindView(R.id.rbTypeUrl)
    RadioButton rbTypeUrl;
    @BindView(R.id.rbTypeImg)
    RadioButton rbTypeImg;
    @BindView(R.id.rgType)
    RadioGroup rgType;
    @BindView(R.id.tvStart)
    TextView tvStart;
    @BindView(R.id.cvStart)
    CardView cvStart;
    @BindView(R.id.tvEnd)
    TextView tvEnd;
    @BindView(R.id.cvEnd)
    CardView cvEnd;
    @BindView(R.id.etCover)
    EditText etCover;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.btnPush)
    Button btnPush;

    private long startAt, endAt;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), BroadcastEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_broadcast_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "broadcast_edit", true);
        rbTypeText.setChecked(true);
        etCover.setText(SPHelper.getOssInfo().getPathBroadcast());
        refreshDateView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.cvStart, R.id.cvEnd, R.id.btnPush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvStart: // 开始
                DialogHelper.showDateTimePicker(mActivity, startAt, new DialogHelper.OnPickListener() {
                    @Override
                    public void onPick(long time) {
                        startAt = time;
                        refreshDateView();
                    }
                });
                break;
            case R.id.cvEnd: // 结束
                DialogHelper.showDateTimePicker(mActivity, endAt, new DialogHelper.OnPickListener() {
                    @Override
                    public void onPick(long time) {
                        endAt = time;
                        refreshDateView();
                    }
                });
                break;
            case R.id.btnPush: // 发布
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
        String start = "s: " + DateUtils.getStr(startAt, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        String end = "e: " + DateUtils.getStr(endAt, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        tvStart.setText(start);
        tvEnd.setText(end);
    }

    private void push() {
        Broadcast broadcast = new Broadcast();
        if (rbTypeText.isChecked()) {
            broadcast.setContentType(Broadcast.TYPE_TEXT);
        } else if (rbTypeUrl.isChecked()) {
            broadcast.setContentType(Broadcast.TYPE_URL);
        } else if (rbTypeImg.isChecked()) {
            broadcast.setContentType(Broadcast.TYPE_IMAGE);
        } else {
            return;
        }
        broadcast.setStartAt(startAt / 1000);
        broadcast.setEndAt(endAt / 1000);
        broadcast.setTitle(etTitle.getText().toString());
        broadcast.setCover(etCover.getText().toString());
        broadcast.setContentText(etContent.getText().toString());
        MaterialDialog loading = getLoading(false);
        Call<Result> call = new RetrofitHelper().call(API.class).broadcastAdd(broadcast);
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
