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
import com.jiangzg.lovenote.model.entity.Award;
import com.jiangzg.lovenote.model.entity.AwardRule;
import com.jiangzg.lovenote.model.entity.User;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import rx.Observable;

public class AwardEditActivity extends BaseActivity<AwardEditActivity> {

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
    @BindView(R.id.llRule)
    LinearLayout llRule;
    @BindView(R.id.tvRule)
    TextView tvRule;

    private Award award;
    private AwardRule rule;
    private int limitContentLength;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AwardEditActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_award_edit;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.award), true);
        // init
        award = new Award();
        award.setHappenAt(TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()));
        User me = SPHelper.getMe();
        if (me != null) {
            award.setHappenId(me.getId());
        }
        // content
        etContent.setText(award.getContentText());
        // date
        refreshDateView();
        // happen
        refreshHappenUser();
        // rule
        refreshRuleView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<AwardRule> obSelectAwardRule = RxBus.register(RxBus.EVENT_AWARD_RULE_SELECT, awardRule -> {
            if (awardRule == null) return;
            award.setAwardRuleId(awardRule.getId());
            rule = awardRule;
            refreshRuleView();
        });
        pushBus(RxBus.EVENT_AWARD_RULE_SELECT, obSelectAwardRule);
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

    @OnClick({R.id.llHappenAt, R.id.llHappenUser, R.id.llRule})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llHappenAt: // 日期
                showDatePicker();
                break;
            case R.id.llHappenUser: // 所属
                showUserDialog();
                break;
            case R.id.llRule: // 规则
                AwardRuleListActivity.goActivityBySelect(mActivity);
                break;
        }
    }

    private void onContentInput(String input) {
        if (award == null) return;
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getAwardContentLength();
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
        award.setContentText(etContent.getText().toString());
    }

    private void showDatePicker() {
        if (award == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(award.getHappenAt()), time -> {
            award.setHappenAt(TimeHelper.getGoTimeByJava(time));
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (award == null) return;
        String happen = TimeHelper.getTimeShowLocal_HM_MDHM_YMDHM_ByGo(award.getHappenAt());
        tvHappenAt.setText(String.format(Locale.getDefault(), getString(R.string.time_colon_space_holder), happen));
    }

    private void showUserDialog() {
        User me = SPHelper.getMe();
        User ta = SPHelper.getTa();
        if (me == null || ta == null) return;
        int searchIndex = (award.getHappenId() == ta.getId()) ? 1 : 0;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_user)
                .items(new String[]{getString(R.string.me_de), getString(R.string.ta_de)})
                .itemsCallbackSingleChoice(searchIndex, (dialog1, view, which, text) -> {
                    if (which < 0 || which > 1) {
                        return true;
                    }
                    award.setHappenId(which == 0 ? me.getId() : ta.getId());
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
        if (me == null || ta == null) return;
        if (award.getHappenId() == ta.getId()) {
            tvHappenUser.setText(String.format(Locale.getDefault(), getString(R.string.belong_colon_space_holder), getString(R.string.ta_de)));
        } else {
            tvHappenUser.setText(String.format(Locale.getDefault(), getString(R.string.belong_colon_space_holder), getString(R.string.me_de)));
        }
    }

    private void refreshRuleView() {
        if (award == null) return;
        String scoreShow = "0";
        String content = "";
        if (rule != null) {
            scoreShow = String.valueOf(rule.getScore());
            if (rule.getScore() > 0) {
                scoreShow = "+" + scoreShow;
            }
            content = rule.getTitle();
        }
        if (StringUtils.isEmpty(etContent.getText().toString().trim())) {
            etContent.setText(content);
        }
        tvRule.setText(String.format(Locale.getDefault(), getString(R.string.rule_colon_space_holder), scoreShow));
    }

    private void push() {
        if (award == null) return;
        if (award.getAwardRuleId() == 0) {
            ToastUtils.show(getString(R.string.please_select_rule));
            return;
        } else if (StringUtils.isEmpty(award.getContentText())) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteAwardAdd(award);
        RetrofitHelper.enqueue(api, getLoading(false), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_AWARD_LIST_REFRESH, new ArrayList<>()));
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
