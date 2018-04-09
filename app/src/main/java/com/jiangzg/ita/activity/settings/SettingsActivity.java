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
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.activity.common.SuggestHomeActivity;
import com.jiangzg.ita.activity.common.WebActivity;
import com.jiangzg.ita.activity.user.LoginActivity;
import com.jiangzg.ita.activity.user.PasswordActivity;
import com.jiangzg.ita.activity.user.PhoneActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.helper.CleanHelper;
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
    @BindView(R.id.tvCacheSummary)
    TextView tvCacheSummary;
    @BindView(R.id.rlCache)
    RelativeLayout rlCache;

    @BindView(R.id.rlSystem)
    RelativeLayout rlSystem;
    @BindView(R.id.switchSystem)
    Switch switchSystem;
    @BindView(R.id.rlSocial)
    RelativeLayout rlSocial;
    @BindView(R.id.switchSocial)
    Switch switchSocial;

    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvPassword)
    TextView tvPassword;

    @BindView(R.id.rlUpdate)
    RelativeLayout rlUpdate;
    @BindView(R.id.tvUpdateSummary)
    TextView tvUpdateSummary;
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
        // 缓存大小
        String cachesSize = CleanHelper.getCachesSizeFmt();
        String cachesSizeShow = String.format(getString(R.string.contain_image_audio_video_total_colon_holder), cachesSize);
        tvCacheSummary.setText(cachesSizeShow);
        // 系统通知
        boolean noticeSystem = SPHelper.getSettingsNoticeSystem();
        switchSystem.setChecked(noticeSystem);
        // 社交通知
        boolean noticeSocial = SPHelper.getSettingsNoticeSocial();
        switchSocial.setChecked(noticeSocial);
        // 版本信息
        String versionName = AppInfo.get().getVersionName();
        String versionNameShow = String.format(getString(R.string.current_version_colon_holder), versionName);
        tvUpdateSummary.setText(versionNameShow);
    }

    @OnClick({R.id.tvTheme, R.id.rlCache, R.id.rlSystem, R.id.rlSocial,
            R.id.tvPhone, R.id.tvPassword, R.id.rlUpdate, R.id.tvHelp, R.id.tvSuggest,
            R.id.tvRate, R.id.tvOpen, R.id.tvProtocol, R.id.tvContact, R.id.tvExist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvTheme: // 主题
                ThemeActivity.goActivity(mActivity);
                break;
            case R.id.rlCache:// todo 缓存
                break;
            case R.id.rlSystem: // 系统通知
                switchSystem.setChecked(!switchSystem.isChecked());
                break;
            case R.id.rlSocial: // 社交通知
                switchSocial.setChecked(!switchSocial.isChecked());
                break;
            case R.id.tvPhone: // 电话
                PhoneActivity.goActivity(mActivity);
                break;
            case R.id.tvPassword: // 密码
                PasswordActivity.goActivity(mActivity);
                break;
            case R.id.rlUpdate: // todo 版本更新
                UpdateService.checkUpdate(mActivity);
                break;
            case R.id.tvHelp: // 帮助文档
                HelpActivity.goActivity(mActivity);
                break;
            case R.id.tvSuggest: // 意见反馈
                SuggestHomeActivity.goActivity(mActivity);
                break;
            case R.id.tvRate: // todo 去评价
                //Intent intent = IntentSend.getMarket();
                //ActivityTrans.start(mActivity, intent);
                break;
            case R.id.tvOpen: // todo 开源许可
                break;
            case R.id.tvProtocol: // 软件协议
                WebActivity.goActivity(mActivity, WebActivity.TYPE_USER_PROTOCOL);
                break;
            case R.id.tvContact: // 联系我们
                WebActivity.goActivity(mActivity, WebActivity.TYPE_CONTACT_US);
                break;
            case R.id.tvExist: // 退出账号
                existDialogShow();
                break;
        }
    }

    @OnCheckedChanged({R.id.switchSystem, R.id.switchSocial})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchSystem:
                SPHelper.setSettingsNoticeSystem(isChecked);
                break;
            case R.id.switchSocial:
                SPHelper.setSettingsNoticeSocial(isChecked);
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

}
