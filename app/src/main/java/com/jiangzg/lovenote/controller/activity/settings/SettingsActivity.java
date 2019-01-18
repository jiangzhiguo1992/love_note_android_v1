package com.jiangzg.lovenote.controller.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.system.PushUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.main.SplashActivity;
import com.jiangzg.lovenote.controller.activity.user.PasswordActivity;
import com.jiangzg.lovenote.controller.activity.user.PhoneActivity;
import com.jiangzg.lovenote.helper.common.ResHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.PushHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.PushInfo;

import java.util.Locale;

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

    @BindView(R.id.tvNoticeStatus)
    TextView tvNoticeStatus;
    @BindView(R.id.rlSystem)
    RelativeLayout rlSystem;
    @BindView(R.id.switchSystem)
    Switch switchSystem;
    @BindView(R.id.rlSocial)
    RelativeLayout rlSocial;
    @BindView(R.id.switchSocial)
    Switch switchSocial;
    @BindView(R.id.rlDisturb)
    RelativeLayout rlDisturb;
    @BindView(R.id.tvDisturbSummary)
    TextView tvDisturbSummary;
    @BindView(R.id.switchDisturb)
    Switch switchDisturb;

    @BindView(R.id.tvPhone)
    TextView tvPhone;
    @BindView(R.id.tvPassword)
    TextView tvPassword;

    @BindView(R.id.rlNotice)
    RelativeLayout rlNotice;
    @BindView(R.id.ivNotice)
    ImageView ivNotice;
    @BindView(R.id.tvHelp)
    TextView tvHelp;
    @BindView(R.id.tvSuggest)
    TextView tvSuggest;
    @BindView(R.id.rlAbout)
    RelativeLayout rlAbout;
    @BindView(R.id.ivAbout)
    ImageView ivAbout;
    @BindView(R.id.tvExist)
    TextView tvExist;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), SettingsActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.settings), true);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // 缓存大小
        cacheShow();
        // 系统通知
        switchSystem.setChecked(SPHelper.getSettingsNoticeSystem());
        // 社交通知
        switchSocial.setChecked(SPHelper.getSettingsNoticeSocial());
        // 免打扰
        switchDisturb.setChecked(SPHelper.getSettingsNoticeDisturb());
        PushInfo pushInfo = SPHelper.getPushInfo();
        String disturb = String.format(Locale.getDefault(),
                getString(R.string.holder_clock_space_line_space_holder_clock),
                pushInfo.getNoStartHour(), pushInfo.getNoEndHour());
        tvDisturbSummary.setText(disturb);
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 推送刷新
        boolean enabled = PushUtils.isNotificationEnabled();
        tvNoticeStatus.setText(enabled ? R.string.notice_yes_open : R.string.notice_no_open);
        // 最新公告
        ivNotice.setVisibility(SPHelper.getCommonCount().getNoticeNewCount() > 0 ? View.VISIBLE : View.GONE);
        // 关于软件
        ivAbout.setVisibility(SPHelper.getCommonCount().getVersionNewCount() > 0 ? View.VISIBLE : View.GONE);
    }

    @OnClick({R.id.tvTheme, R.id.rlCache,
            R.id.tvNoticeStatus, R.id.rlSystem, R.id.rlSocial, R.id.rlDisturb,
            R.id.tvPhone, R.id.tvPassword,
            R.id.tvHelp, R.id.rlNotice, R.id.tvSuggest, R.id.rlAbout,
            R.id.tvExist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvTheme: // 主题
                ThemeActivity.goActivity(mActivity);
                break;
            case R.id.rlCache:// 缓存
                showCacheDialog();
                break;
            case R.id.tvNoticeStatus: // 推送开关
                goNoticeSettings();
                break;
            case R.id.rlSystem: // 系统通知
                switchSystem.setChecked(!switchSystem.isChecked());
                break;
            case R.id.rlSocial: // 社交通知
                switchSocial.setChecked(!switchSocial.isChecked());
                break;
            case R.id.rlDisturb: // 夜间免打扰
                switchDisturb.setChecked(!switchDisturb.isChecked());
                break;
            case R.id.tvPhone: // 电话
                PhoneActivity.goActivity(mActivity);
                break;
            case R.id.tvPassword: // 密码
                PasswordActivity.goActivity(mActivity);
                break;
            case R.id.rlNotice: // 最新公告
                NoticeListActivity.goActivity(mActivity);
                break;
            case R.id.tvHelp: // 帮助文档
                HelpActivity.goActivity(mActivity);
                break;
            case R.id.tvSuggest: // 意见反馈
                SuggestHomeActivity.goActivity(mActivity);
                break;
            case R.id.rlAbout: // 关于软件
                AboutActivity.goActivity(mActivity);
                break;
            case R.id.tvExist: // 退出账号
                existDialogShow();
                break;
        }
    }

    @OnCheckedChanged({R.id.switchSystem, R.id.switchSocial, R.id.switchDisturb})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchSystem: // 系统通知
                SPHelper.setSettingsNoticeSystem(isChecked);
                PushHelper.checkTagBind();
                break;
            case R.id.switchSocial: // 社交通知
                SPHelper.setSettingsNoticeSocial(isChecked);
                PushHelper.checkAccountBind();
                break;
            case R.id.switchDisturb: // 免打扰
                SPHelper.setSettingsNoticeDisturb(isChecked);
                PushHelper.checkDisturb();
                break;
        }
    }

    private void showCacheDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .canceledOnTouchOutside(true)
                .cancelable(true)
                .content(R.string.confirm_del_cache)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> clearCache())
                .build();
        DialogHelper.showWithAnim(dialog);
        DialogUtils.show(dialog);

    }

    private void clearCache() {
        final MaterialDialog loading = mActivity.getLoading(false);
        DialogUtils.show(loading);
        MyApp.get().getThread().execute(() -> {
            ResHelper.clearCaches();
            MyApp.get().getHandler().post(() -> {
                DialogUtils.dismiss(loading);
                cacheShow();
                ToastUtils.show(getString(R.string.cache_clear_success));
            });
        });
    }

    private void cacheShow() {
        String cachesSize = ResHelper.getCachesSizeFmt();
        String cachesSizeShow = String.format(Locale.getDefault(), getString(R.string.contain_image_audio_video_total_colon_holder), cachesSize);
        tvCacheSummary.setText(cachesSizeShow);
    }

    private void goNoticeSettings() {
        PermUtils.requestPermissions(mActivity, REQUEST_DEVICE_INFO, PermUtils.deviceInfo, null);
        //boolean enabled = PushUtils.isNotificationEnabled();
        //if (enabled) return;
        Intent notice = IntentFactory.getNotificationSettings();
        ActivityTrans.start(mActivity, notice);
    }

    private void existDialogShow() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.exist_account)
                .content(R.string.confirm_exist_account)
                .positiveText(R.string.confirm)
                .negativeText(R.string.cancel)
                .onPositive((dialog1, which) -> {
                    SPHelper.clearAll();
                    PushHelper.unBindAccount();
                    RxBus.post(new RxBus.Event<>(RxBus.EVENT_USER_REFRESH, null));
                    SplashActivity.goActivity(mActivity);
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

}
