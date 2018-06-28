package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Dream;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class DreamEditActivity extends BaseActivity<DreamEditActivity> {

    private static final int TYPE_ADD = 0;
    private static final int TYPE_UPDATE = 1;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.cvHappenAt)
    CardView cvHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;
    @BindView(R.id.btnPublish)
    Button btnPublish;
    @BindView(R.id.btnDraft)
    Button btnDraft;

    private Dream dream;
    private Call<Result> callUpdate;
    private Call<Result> callAdd;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, DreamEditActivity.class);
        intent.putExtra("type", TYPE_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Dream dream) {
        if (dream == null) {
            goActivity(from);
        } else if (!dream.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_dream));
            return;
        }
        Intent intent = new Intent(from, DreamEditActivity.class);
        intent.putExtra("type", TYPE_UPDATE);
        intent.putExtra("dream", dream);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_dream_edit;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.dream), true);
    }

    @Override
    protected void initData(Bundle state) {
        if (isTypeUpdate()) {
            dream = getIntent().getParcelableExtra("dream");
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
        // input
        etContent.setText(dream.getContentText());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callUpdate);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_DREAM_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.cvHappenAt, R.id.btnDraft, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.btnDraft: // 保存草稿
                saveDraft();
                break;
            case R.id.btnPublish: // 发表
                checkPush();
                break;
        }
    }

    private boolean isTypeUpdate() {
        return getIntent().getIntExtra("type", TYPE_ADD) == TYPE_UPDATE;
    }

    private void showDatePicker() {
        Calendar calendar = DateUtils.getCalendar(TimeHelper.getJavaTimeByGo(dream.getHappenAt()));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog picker = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar instance = DateUtils.getCurrentCalendar();
                instance.set(year, month, dayOfMonth);
                dream.setHappenAt(TimeHelper.getGoTimeByJava(instance.getTimeInMillis()));
                refreshDateView();
            }
        }, year, month, day);
        picker.show();
    }

    private void refreshDateView() {
        String happen = TimeHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(dream.getHappenAt());
        tvHappenAt.setText(happen);
    }

    private void onContentInput(String input) {
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
        String content = etContent.getText().toString();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        }
        if (isTypeUpdate()) {
            updateApi(dream);
        } else {
            addApi(dream);
        }
    }

    private void updateApi(Dream dream) {
        MaterialDialog loading = getLoading(false);
        callUpdate = new RetrofitHelper().call(API.class).dreamUpdate(dream);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Dream dream = data.getDream();
                RxEvent<Dream> eventList = new RxEvent<>(ConsHelper.EVENT_DREAM_LIST_ITEM_REFRESH, dream);
                RxBus.post(eventList);
                RxEvent<Dream> eventSingle = new RxEvent<>(ConsHelper.EVENT_DREAM_DETAIL_REFRESH, dream);
                RxBus.post(eventSingle);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    private void addApi(Dream dream) {
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).dreamAdd(dream);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<ArrayList<Dream>> event = new RxEvent<>(ConsHelper.EVENT_DREAM_LIST_REFRESH, new ArrayList<Dream>());
                RxBus.post(event);
                // sp
                SPHelper.setDraftDream(null);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
