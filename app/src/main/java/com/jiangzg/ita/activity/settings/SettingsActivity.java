package com.jiangzg.ita.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.activity.common.SuggestHomeActivity;
import com.jiangzg.ita.activity.common.WebActivity;
import com.jiangzg.ita.activity.user.LoginActivity;
import com.jiangzg.ita.activity.user.PasswordActivity;
import com.jiangzg.ita.activity.user.PhoneActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.DialogHelper;
import com.jiangzg.ita.helper.RxBus;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.service.UpdateService;

import butterknife.BindView;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity<SettingsActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvTheme)
    TextView tvTheme;
    @BindView(R.id.switchWifi)
    Switch switchWifi;
    @BindView(R.id.rlWifi)
    RelativeLayout rlWifi;
    @BindView(R.id.switchDownland)
    Switch switchDownland;
    @BindView(R.id.rlDownload)
    RelativeLayout rlDownload;
    @BindView(R.id.tvCacheSummary)
    TextView tvCacheSummary;
    @BindView(R.id.rlCache)
    RelativeLayout rlCache;
    @BindView(R.id.switchSystem)
    Switch switchSystem;
    @BindView(R.id.rlSystem)
    RelativeLayout rlSystem;
    @BindView(R.id.switchTa)
    Switch switchTa;
    @BindView(R.id.rlTa)
    RelativeLayout rlTa;
    @BindView(R.id.switchOther)
    Switch switchOther;
    @BindView(R.id.rlOther)
    RelativeLayout rlOther;
    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvPassword)
    TextView tvPassword;
    @BindView(R.id.tvUpdateSummary)
    TextView tvUpdateSummary;
    @BindView(R.id.rlUpdate)
    RelativeLayout rlUpdate;
    @BindView(R.id.tvHelp)
    TextView tvHelp;
    @BindView(R.id.tvSuggest)
    TextView tvSuggest;
    @BindView(R.id.tvRate)
    TextView tvRate;
    @BindView(R.id.tvOpen)
    TextView tvOpen;
    @BindView(R.id.tvProtocol)
    TextView tvProtocol;
    @BindView(R.id.tvContact)
    TextView tvContact;
    @BindView(R.id.tvExist)
    TextView tvExist;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SettingsActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.settings), true);
    }

    @Override
    protected void initData(Bundle state) {
        boolean onlyWifi = SPHelper.getSettingsOnlyWifi();
        switchWifi.setChecked(onlyWifi);
        boolean autoDownload = SPHelper.getSettingsAutoDownload();
        switchDownland.setChecked(autoDownload);
        boolean noticeSystem = SPHelper.getSettingsNoticeSystem();
        switchSystem.setChecked(noticeSystem);
        boolean noticeTa = SPHelper.getSettingsNoticeTa();
        switchTa.setChecked(noticeTa);
        boolean noticeOther = SPHelper.getSettingsNoticeOther();
        switchOther.setChecked(noticeOther);

        String versionName = String.format(getString(R.string.current_version_colon_holder), AppInfo.get().getVersionName());
        tvUpdateSummary.setText(versionName);
    }

    @OnClick({R.id.tvTheme, R.id.rlWifi, R.id.rlDownload, R.id.rlCache, R.id.rlSystem, R.id.rlTa,
            R.id.rlOther, R.id.tvPhone, R.id.tvPassword, R.id.rlUpdate, R.id.tvHelp, R.id.tvSuggest,
            R.id.tvRate, R.id.tvOpen, R.id.tvProtocol, R.id.tvContact, R.id.tvExist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvTheme:
                ThemeActivity.goActivity(mActivity);
                break;
            case R.id.rlWifi:
                switchWifi.setChecked(!switchWifi.isChecked());
                break;
            case R.id.rlDownload:
                switchDownland.setChecked(!switchDownland.isChecked());
                break;
            case R.id.rlCache:
                // todo
                break;
            case R.id.rlSystem:
                switchSystem.setChecked(!switchSystem.isChecked());
                break;
            case R.id.rlTa:
                switchTa.setChecked(!switchTa.isChecked());
                break;
            case R.id.rlOther:
                switchOther.setChecked(!switchOther.isChecked());
                break;
            case R.id.tvPhone:
                PhoneActivity.goActivity(mActivity);
                break;
            case R.id.tvPassword:
                PasswordActivity.goActivity(mActivity);
                break;
            case R.id.rlUpdate:
                UpdateService.checkUpdate(mActivity);
                break;
            case R.id.tvHelp:
                HelpActivity.goActivity(mActivity);
                break;
            case R.id.tvSuggest:
                SuggestHomeActivity.goActivity(mActivity);
                break;
            case R.id.tvRate:
                // todo
                //Intent intent = IntentSend.getMarket();
                //ActivityTrans.start(mActivity, intent);
                break;
            case R.id.tvOpen:
                // todo
                break;
            case R.id.tvProtocol:
                WebActivity.goActivity(mActivity, WebActivity.TYPE_USER_PROTOCOL);
                break;
            case R.id.tvContact:
                WebActivity.goActivity(mActivity, WebActivity.TYPE_CONTACT_US);
                break;
            case R.id.tvExist:
                existDialogShow();
                break;
        }
    }

    private void existDialogShow() {
        MaterialDialog dialog = new MaterialDialog.Builder(mActivity)
                .title(R.string.exist_account)
                .content(R.string.confirm_exist_account)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .autoDismiss(true)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        SPHelper.clearUser();
                        SPHelper.clearCouple();
                        RxEvent<User> event = new RxEvent<>(ConsHelper.EVENT_USER_CHANGE, null);
                        RxBus.post(event);
                        LoginActivity.goActivity(mActivity);
                    }
                })
                .build();
        DialogHelper.setAnim(dialog);
        DialogHelper.show(dialog);
    }

    @OnCheckedChanged({R.id.switchWifi, R.id.switchDownland, R.id.switchSystem, R.id.switchTa, R.id.switchOther})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchWifi:
                SPHelper.setSettingsOnlyWifi(isChecked);
                break;
            case R.id.switchDownland:
                SPHelper.setSettingsAutoDownload(isChecked);
                break;
            case R.id.switchSystem:
                SPHelper.setSettingsNoticeSystem(isChecked);
                break;
            case R.id.switchTa:
                SPHelper.setSettingsNoticeTa(isChecked);
                break;
            case R.id.switchOther:
                SPHelper.setSettingsNoticeOther(isChecked);
                break;
        }
    }

}
