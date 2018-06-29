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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Promise;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.User;
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

public class PromiseEditActivity extends BaseActivity<PromiseEditActivity> {

    private static final int TYPE_ADD = 0;
    private static final int TYPE_UPDATE = 1;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.cvHappenAt)
    CardView cvHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.rgHappenUser)
    RadioGroup rgHappenUser;
    @BindView(R.id.rbHappenMe)
    RadioButton rbHappenMe;
    @BindView(R.id.rbHappenTa)
    RadioButton rbHappenTa;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;
    @BindView(R.id.btnPublish)
    Button btnPublish;

    private Promise promise;
    private Call<Result> callUpdate;
    private Call<Result> callAdd;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, PromiseEditActivity.class);
        intent.putExtra("type", TYPE_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Promise promise) {
        if (promise == null) {
            goActivity(from);
        } else if (!promise.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_promise));
            return;
        }
        Intent intent = new Intent(from, PromiseEditActivity.class);
        intent.putExtra("type", TYPE_UPDATE);
        intent.putExtra("promise", promise);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_promise_edit;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.promise), true);
    }

    @Override
    protected void initData(Bundle state) {
        if (isTypeUpdate()) {
            promise = getIntent().getParcelableExtra("promise");
        }
        if (promise == null) {
            promise = new Promise();
        }
        if (promise.getHappenAt() == 0) {
            promise.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        }
        // date
        refreshDateView();
        // happenUser
        initHappenCheck();
        // input
        etContent.setText(promise.getContentText());
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
                HelpActivity.goActivity(mActivity, Help.INDEX_PROMISE_EDIT);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.cvHappenAt, R.id.btnPublish})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.btnPublish: // 发表
                checkPush();
                break;
        }
    }

    private boolean isTypeUpdate() {
        return getIntent().getIntExtra("type", TYPE_ADD) == TYPE_UPDATE;
    }

    private void initHappenCheck() {
        if (isTypeUpdate()) {
            rgHappenUser.setVisibility(View.GONE);
            return;
        }
        rgHappenUser.setVisibility(View.VISIBLE);
        final User user = SPHelper.getMe();
        rgHappenUser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbHappenMe: // 我的
                        promise.setHappenId(user.getId());
                        break;
                    case R.id.rbHappenTa: // Ta的
                        promise.setHappenId(user.getTaId());
                        break;
                }
            }
        });
        long happenId = promise.getHappenId();
        if (happenId == 0 || happenId == user.getId()) {
            rbHappenMe.setChecked(true);
        } else {
            rbHappenTa.setChecked(true);
        }
    }

    private void showDatePicker() {
        Calendar calendar = DateUtils.getCalendar(TimeHelper.getJavaTimeByGo(promise.getHappenAt()));
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog picker = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar instance = DateUtils.getCurrentCalendar();
                instance.set(year, month, dayOfMonth);
                promise.setHappenAt(TimeHelper.getGoTimeByJava(instance.getTimeInMillis()));
                refreshDateView();
            }
        }, year, month, day);
        picker.show();
    }

    private void refreshDateView() {
        String happen = TimeHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(promise.getHappenAt());
        tvHappenAt.setText(happen);
    }

    private void onContentInput(String input) {
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getPromiseContentLength();
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
        promise.setContentText(etContent.getText().toString());
    }

    private void checkPush() {
        if (StringUtils.isEmpty(promise.getContentText())) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        }
        if (isTypeUpdate()) {
            updateApi(promise);
        } else {
            addApi(promise);
        }
    }

    private void updateApi(Promise promise) {
        MaterialDialog loading = getLoading(false);
        callUpdate = new RetrofitHelper().call(API.class).promiseUpdate(promise);
        RetrofitHelper.enqueue(callUpdate, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Promise promise = data.getPromise();
                RxEvent<Promise> eventList = new RxEvent<>(ConsHelper.EVENT_PROMISE_LIST_ITEM_REFRESH, promise);
                RxBus.post(eventList);
                RxEvent<Promise> eventSingle = new RxEvent<>(ConsHelper.EVENT_PROMISE_DETAIL_REFRESH, promise);
                RxBus.post(eventSingle);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    private void addApi(Promise promise) {
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).promiseAdd(promise);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<ArrayList<Promise>> event = new RxEvent<>(ConsHelper.EVENT_PROMISE_LIST_REFRESH, new ArrayList<Promise>());
                RxBus.post(event);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
