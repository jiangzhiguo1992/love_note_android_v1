package com.jiangzg.lovenote.controller.activity.note;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Angry;
import com.jiangzg.lovenote.model.entity.User;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class AngryEditActivity extends BaseActivity<AngryEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnHappenAt)
    Button btnHappenAt;
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

    private Angry angry;
    private Call<Result> callAdd;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AngryEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_angry_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.angry), true);
        // init
        angry = new Angry();
        angry.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        // date
        refreshDateView();
        // happen
        initHappenCheck();
        // content
        etContent.setText(angry.getContentText());
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
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

    @OnClick({R.id.btnHappenAt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnHappenAt: // 日期
                showDatePicker();
                break;
        }
    }

    private void initHappenCheck() {
        final User user = SPHelper.getMe();
        rgHappenUser.setOnCheckedChangeListener((group, checkedId) -> {
            if (angry == null) return;
            switch (checkedId) {
                case R.id.rbHappenMe: // 我的
                    angry.setHappenId(UserHelper.getMyId(user));
                    break;
                case R.id.rbHappenTa: // Ta的
                    angry.setHappenId(UserHelper.getTaId(user));
                    break;
            }
        });
        rbHappenMe.setChecked(true);
    }

    private void showDatePicker() {
        if (angry == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(angry.getHappenAt()), time -> {
            angry.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (angry == null) return;
        String happen = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(angry.getHappenAt());
        btnHappenAt.setText(happen);
    }

    private void onContentInput(String input) {
        if (angry == null || input == null) return;
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getAngryContentLength();
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
        angry.setContentText(etContent.getText().toString());
    }

    private void push() {
        if (angry == null) return;
        if (StringUtils.isEmpty(angry.getContentText())) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        }
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).noteAngryAdd(angry);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_ANGRY_LIST_REFRESH, new ArrayList<>()));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
