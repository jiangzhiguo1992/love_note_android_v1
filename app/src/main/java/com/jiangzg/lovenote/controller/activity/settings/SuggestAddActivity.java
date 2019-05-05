package com.jiangzg.lovenote.controller.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.engine.SuggestInfo;
import com.jiangzg.lovenote.model.entity.Suggest;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class SuggestAddActivity extends BaseActivity<SuggestAddActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvKind)
    TextView tvKind;
    @BindView(R.id.tvTitleLimit)
    TextView tvTitleLimit;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.tvContentLimit)
    TextView tvContentLimit;
    @BindView(R.id.etContent)
    EditText etContent;

    private int kindIndex = 0;
    private int limitTitleLength;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SuggestAddActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_add;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // input
        etTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onTitleInput(s.toString());
            }
        });
        etTitle.setText("");
        etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                onContentInput(s.toString());
            }
        });
        etContent.setText("");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        kindIndex = 0;
        // view
        refreshKindView();
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
                addSuggest();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.tvKind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvKind: // 选分类
                showKindDialog();
                break;
        }
    }

    private void onTitleInput(String s) {
        if (s == null) return;
        if (limitTitleLength <= 0) {
            limitTitleLength = SPHelper.getLimit().getSuggestTitleLength();
        }
        int length = s.length();
        if (length > limitTitleLength) {
            CharSequence charSequence = s.subSequence(0, limitTitleLength);
            etTitle.setText(charSequence);
            etTitle.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitTitleLength);
        tvTitleLimit.setText(limitShow);
    }

    private void onContentInput(String s) {
        if (s == null) return;
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getSuggestContentLength();
        }
        int length = s.length();
        if (length > limitContentLength) {
            CharSequence charSequence = s.subSequence(0, limitContentLength);
            etContent.setText(charSequence);
            etContent.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitContentLength);
        tvContentLimit.setText(limitShow);
    }

    private void showKindDialog() {
        SuggestInfo suggestInfo = ListHelper.getSuggestInfo();
        List<SuggestInfo.SuggestKind> kindList = suggestInfo.getKindList();
        CharSequence[] items = new CharSequence[kindList.size()];
        for (int i = 0; i < kindList.size(); i++) {
            SuggestInfo.SuggestKind kind = kindList.get(i);
            items[i] = kind.getShow();
        }
        // dialog
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.please_select_classify)
                .items(items)
                .itemsCallbackSingleChoice(kindIndex, (dialog1, view, which, text) -> {
                    kindIndex = which;
                    refreshKindView();
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void refreshKindView() {
        SuggestInfo suggestInfo = ListHelper.getSuggestInfo();
        List<SuggestInfo.SuggestKind> kindList = suggestInfo.getKindList();
        if (kindIndex < 0 || kindIndex >= kindList.size()) {
            tvKind.setText(R.string.please_select_classify);
            return;
        }
        SuggestInfo.SuggestKind suggestKind = kindList.get(kindIndex);
        tvKind.setText(suggestKind.getShow());
    }

    private void addSuggest() {
        String title = etTitle.getText().toString();
        if (StringUtils.isEmpty(title)) {
            ToastUtils.show(getString(R.string.please_input_title));
            return;
        }
        String content = etContent.getText().toString();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(getString(R.string.please_input_content));
            return;
        }
        SuggestInfo suggestInfo = ListHelper.getSuggestInfo();
        List<SuggestInfo.SuggestKind> kindList = suggestInfo.getKindList();
        if (kindIndex < 0 || kindIndex >= kindList.size()) {
            ToastUtils.show(getString(R.string.please_select_classify));
            return;
        }
        SuggestInfo.SuggestKind suggestKind = kindList.get(kindIndex);
        // body
        Suggest body = new Suggest();
        body.setTitle(etTitle.getText().toString());
        body.setKind(suggestKind.getKind());
        body.setContentText(etContent.getText().toString());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).setSuggestAdd(body);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SUGGEST_LIST_REFRESH, new ArrayList<>()));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
