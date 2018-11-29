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
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.entity.Award;
import com.jiangzg.lovenote.model.entity.AwardRule;
import com.jiangzg.lovenote.model.entity.Result;
import com.jiangzg.lovenote.model.entity.RxEvent;
import com.jiangzg.lovenote.model.entity.User;

import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class AwardEditActivity extends BaseActivity<AwardEditActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.cvRule)
    CardView cvRule;
    @BindView(R.id.tvRule)
    TextView tvRule;
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

    private Award award;
    private AwardRule rule;
    private Observable<AwardRule> obSelectAwardRule;
    private Call<Result> callAdd;
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
        // rule
        refreshRuleView();
        // date
        refreshDateView();
        // happen
        initHappenCheck();
        // content
        etContent.setText(award.getContentText());
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obSelectAwardRule = RxBus.register(ConsHelper.EVENT_AWARD_RULE_SELECT, new Action1<AwardRule>() {
            @Override
            public void call(AwardRule awardRule) {
                if (awardRule == null) return;
                award.setAwardRuleId(awardRule.getId());
                rule = awardRule;
                refreshRuleView();
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RxBus.unregister(ConsHelper.EVENT_AWARD_RULE_SELECT, obSelectAwardRule);
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

    @OnClick({R.id.cvRule, R.id.cvHappenAt})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvRule: // 规则
                AwardRuleListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.cvHappenAt: // 日期
                showDatePicker();
                break;
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
        tvRule.setText(scoreShow);
        if (StringUtils.isEmpty(etContent.getText().toString().trim())) {
            etContent.setText(content);
        }
    }

    private void initHappenCheck() {
        final User user = SPHelper.getMe();
        rgHappenUser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (award == null) return;
                switch (checkedId) {
                    case R.id.rbHappenMe: // 我的
                        award.setHappenId(user.getId());
                        break;
                    case R.id.rbHappenTa: // Ta的
                        award.setHappenId(user.getTaId());
                        break;
                }
            }
        });
        rbHappenMe.setChecked(true);
    }

    private void showDatePicker() {
        if (award == null) return;
        DialogHelper.showDateTimePicker(mActivity, TimeHelper.getJavaTimeByGo(award.getHappenAt()), new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                award.setHappenAt(TimeHelper.getGoTimeByJava(time));
                refreshDateView();
            }
        });
    }

    private void refreshDateView() {
        if (award == null) return;
        String happen = TimeHelper.getTimeShowLocal_HM_MDHM_YMDHM_ByGo(award.getHappenAt());
        tvHappenAt.setText(happen);
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

    private void push() {
        if (award == null) return;
        if (award.getAwardRuleId() == 0) {
            ToastUtils.show(getString(R.string.please_select_rule));
            return;
        } else if (StringUtils.isEmpty(award.getContentText())) {
            ToastUtils.show(etContent.getHint().toString());
            return;
        }
        MaterialDialog loading = getLoading(false);
        callAdd = new RetrofitHelper().call(API.class).noteAwardAdd(award);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<ArrayList<Award>> event = new RxEvent<>(ConsHelper.EVENT_AWARD_LIST_REFRESH, new ArrayList<Award>());
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
