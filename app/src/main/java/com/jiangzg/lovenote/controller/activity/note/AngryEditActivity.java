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
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.llHappenAt)
    LinearLayout llHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.llHappenUser)
    LinearLayout llHappenUser;
    @BindView(R.id.tvHappenUser)
    TextView tvHappenUser;

    private Angry angry;
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
        User me = SPHelper.getMe();
        if (me != null) {
            angry.setHappenId(me.getId());
        }
        // content
        etContent.setText(angry.getContentText());
        // date
        refreshDateView();
        // happen
        refreshHappenUser();
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

    @OnClick({R.id.llHappenAt, R.id.llHappenUser})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.llHappenUser: // 所属
                showUserDialog();
                break;
        }
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
        tvHappenAt.setText(String.format(Locale.getDefault(), getString(R.string.time_colon_space_holder), happen));
    }

    private void showUserDialog() {
        User me = SPHelper.getMe();
        User ta = SPHelper.getTa();
        if (angry == null || me == null || ta == null) return;
        int searchIndex = (angry.getHappenId() == ta.getId()) ? 1 : 0;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_user)
                .items(new String[]{getString(R.string.me_de), getString(R.string.ta_de)})
                .itemsCallbackSingleChoice(searchIndex, (dialog1, view, which, text) -> {
                    if (which < 0 || which > 1) {
                        return true;
                    }
                    angry.setHappenId(which == 0 ? me.getId() : ta.getId());
                    refreshHappenUser();
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void refreshHappenUser() {
        User me = SPHelper.getMe();
        User ta = SPHelper.getTa();
        if (angry == null || me == null || ta == null) return;
        if (angry.getHappenId() == ta.getId()) {
            tvHappenUser.setText(String.format(Locale.getDefault(), getString(R.string.belong_colon_space_holder), getString(R.string.ta_de)));
        } else {
            tvHappenUser.setText(String.format(Locale.getDefault(), getString(R.string.belong_colon_space_holder), getString(R.string.me_de)));
        }
    }

    private void push() {
        if (angry == null) return;
        if (StringUtils.isEmpty(angry.getContentText())) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteAngryAdd(angry);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
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
        pushApi(api);
    }

}
