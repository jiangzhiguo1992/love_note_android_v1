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
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.llHappenAt)
    LinearLayout llHappenAt;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;

    private Dream dream;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, DreamEditActivity.class);
        intent.putExtra("from", BaseActivity.ACT_EDIT_FROM_ADD);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, Dream dream) {
        if (dream == null) {
            goActivity(from);
            return;
        } else if (!dream.isMine()) {
            ToastUtils.show(from.getString(R.string.can_operation_self_create_note));
            return;
        }
        Intent intent = new Intent(from, DreamEditActivity.class);
        intent.putExtra("from", BaseActivity.ACT_EDIT_FROM_UPDATE);
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
        // content
        etContent.setText(dream.getContentText());
        // date
        refreshDateView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @Override
    public void onBackPressed() {
        // 更新
        if (isFromUpdate()) {
            super.onBackPressed();
            return;
        }
        // 没有数据
        if (dream == null || StringUtils.isEmpty(dream.getContentText())) {
            super.onBackPressed();
            return;
        }
        // 相同数据
        Dream draft = SPHelper.getDraftDream();
        if (draft != null && draft.getContentText().equals(dream.getContentText())) {
            super.onBackPressed();
            return;
        }
        // 草稿询问
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.is_save_draft)
                .positiveText(R.string.save_draft)
                .negativeText(R.string.cancel)
                .onPositive((dialog1, which) -> saveDraft(true))
                .onNegative((dialog12, which) -> super.onBackPressed())
                .build();
        DialogHelper.showWithAnim(dialog);
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
                saveDraft(false);
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

    @OnClick({R.id.llHappenAt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llHappenAt: // 日期
                showDatePicker();
                break;
        }
    }

    private boolean isFromUpdate() {
        return getIntent().getIntExtra("from", BaseActivity.ACT_EDIT_FROM_ADD) == BaseActivity.ACT_EDIT_FROM_UPDATE;
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
        tvHappenAt.setText(String.format(Locale.getDefault(), getString(R.string.time_colon_space_holder), happen));
    }

    private void saveDraft(boolean exit) {
        SPHelper.setDraftDream(dream);
        ToastUtils.show(getString(R.string.draft_save_success));
        if (exit) mActivity.finish();
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
        Call<Result> api = new RetrofitHelper().call(API.class).noteDreamUpdate(dream);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                Dream dream = data.getDream();
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_DREAM_LIST_ITEM_REFRESH, dream));
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_DREAM_DETAIL_REFRESH, dream));
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
        if (dream == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteDreamAdd(dream);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_DREAM_LIST_REFRESH, new ArrayList<>()));
                // draft
                SPHelper.setDraftDream(null);
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
