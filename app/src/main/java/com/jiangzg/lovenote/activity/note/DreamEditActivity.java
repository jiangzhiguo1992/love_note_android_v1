package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Dream;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class DreamEditActivity extends BaseActivity<DreamEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnHappenAt)
    Button btnHappenAt;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;

    private Dream dream;
    private Call<Result> callUpdate;
    private Call<Result> callAdd;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, DreamEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Dream dream) {
        if (dream == null) {
            goActivity(from);
            return;
        } else if (!dream.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_dream));
            return;
        }
        Intent intent = new Intent(from, DreamEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_UPDATE);
        intent.putExtra("dream", dream);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_dream_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.dream), true);
        // init
        if (isFromUpdate()) {
            dream = intent.getParcelableExtra("dream");
        } else {
            dream = SPHelper.getDraftDream();
        }
        if (dream == null) {
            dream = new Dream();
        }
        if (dream.getHappenAt() == 0) {
            dream.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        }
        // date
        refreshDateView();
        // content
        etContent.setText(dream.getContentText());
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.draft_commit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDraft: // 草稿
                saveDraft();
                return true;
            case R.id.menuCommit: // 提交
                checkPush();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.btnHappenAt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnHappenAt: // 日期
                showDatePicker();
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", ConsHelper.ACT_EDIT_FROM_ADD) == ConsHelper.ACT_EDIT_FROM_UPDATE;
    }

    private void showDatePicker() {
        if (dream == null) return;
        DialogHelper.showDatePicker(mActivity, TimeHelper.getJavaTimeByGo(dream.getHappenAt()), time -> {
            dream.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (dream == null) return;
        String happen = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(dream.getHappenAt());
        btnHappenAt.setText(happen);
    }

    private void onContentInput(String input) {
        if (dream == null) return;
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getDreamContentLength();
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
        dream.setContentText(etContent.getText().toString());
    }

    private void saveDraft() {
        SPHelper.setDraftDream(dream);
        ToastUtils.show(getString(R.string.draft_save_success));
    }

    private void checkPush() {
        if (dream == null) return;
        if (StringUtils.isEmpty(dream.getContentText())) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        }
        if (isFromUpdate()) {
            updateApi();
        } else {
            addApi();
        }
    }

    private void updateApi() {
        if (dream == null) return;
        MaterialDialog loading = getLoading(false);
        callUpdate = new RetrofitHelper().call(API.class).noteDreamUpdate(dream);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Dream dream = data.getDream();
                RxBus.Event<Dream> eventList = new RxBus.Event<>(ConsHelper.EVENT_DREAM_LIST_ITEM_REFRESH, dream);
                RxBus.post(eventList);
                RxBus.Event<Dream> eventSingle = new RxBus.Event<>(ConsHelper.EVENT_DREAM_DETAIL_REFRESH, dream);
                RxBus.post(eventSingle);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void addApi() {
        if (dream == null) return;
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).noteDreamAdd(dream);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.Event<ArrayList<Dream>> event = new RxBus.Event<>(ConsHelper.EVENT_DREAM_LIST_REFRESH, new ArrayList<>());
                RxBus.post(event);
                // sp
                SPHelper.setDraftDream(null);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
