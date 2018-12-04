package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.entity.Promise;
import com.jiangzg.lovenote.model.entity.Result;
import com.jiangzg.lovenote.model.entity.RxEvent;
import com.jiangzg.lovenote.model.entity.User;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class PromiseEditActivity extends BaseActivity<PromiseEditActivity> {

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

    private Promise promise;
    private Call<Result> callUpdate;
    private Call<Result> callAdd;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, PromiseEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Promise promise) {
        if (promise == null) {
            goActivity(from);
            return;
        } else if (!promise.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_promise));
            return;
        }
        Intent intent = new Intent(from, PromiseEditActivity.class);
        intent.putExtra("from", ConsHelper.ACT_EDIT_FROM_UPDATE);
        intent.putExtra("promise", promise);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_promise_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.promise), true);
        // init
        if (isFromUpdate()) {
            promise = intent.getParcelableExtra("promise");
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
        // content
        etContent.setText(promise.getContentText());
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

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.cvHappenAt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvHappenAt: // 日期
                showDatePicker();
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", ConsHelper.ACT_EDIT_FROM_ADD) == ConsHelper.ACT_EDIT_FROM_UPDATE;
    }

    private void initHappenCheck() {
        if (promise == null) return;
        if (isFromUpdate()) {
            rgHappenUser.setVisibility(View.GONE);
            return;
        }
        rgHappenUser.setVisibility(View.VISIBLE);
        final User user = SPHelper.getMe();
        rgHappenUser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (promise == null) return;
                switch (checkedId) {
                    case R.id.rbHappenMe: // 我的
                        promise.setHappenId(UserHelper.getMyId(user));
                        break;
                    case R.id.rbHappenTa: // Ta的
                        promise.setHappenId(UserHelper.getTaId(user));
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
        if (promise == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(promise.getHappenAt()), new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                promise.setHappenAt(TimeHelper.getGoTimeByJava(time));
                refreshDateView();
            }
        });
    }

    private void refreshDateView() {
        if (promise == null) return;
        String happen = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(promise.getHappenAt());
        tvHappenAt.setText(happen);
    }

    private void onContentInput(String input) {
        if (promise == null) return;
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
        if (promise == null) return;
        if (StringUtils.isEmpty(promise.getContentText())) {
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
        if (promise == null) return;
        MaterialDialog loading = getLoading(false);
        callUpdate = new RetrofitHelper().call(API.class).notePromiseUpdate(promise);
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
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void addApi() {
        if (promise == null) return;
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).notePromiseAdd(promise);
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
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
