package com.jiangzg.ita.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.user.LoginActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.utils.PreferenceUser;
import com.jiangzg.ita.utils.ViewUtils;

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
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_settings;
    }

    @Override
    protected void initView(Bundle state) {
        ViewUtils.initTopBar(mActivity, tb, getString(R.string.settings), true);
    }

    @Override
    protected void initData(Bundle state) {
        boolean onlyWifi = PreferenceUser.getSettingsOnlyWifi();
        switchWifi.setChecked(onlyWifi);
        boolean autoDownload = PreferenceUser.getSettingsAutoDownload();
        switchDownland.setChecked(autoDownload);
        boolean noticeSystem = PreferenceUser.getSettingsNoticeSystem();
        switchSystem.setChecked(noticeSystem);
        boolean noticeTa = PreferenceUser.getSettingsNoticeTa();
        switchTa.setChecked(noticeTa);
        boolean noticeOther = PreferenceUser.getSettingsNoticeOther();
        switchOther.setChecked(noticeOther);
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
                // todo
                break;
            case R.id.tvPassword:
                // todo
                break;
            case R.id.rlUpdate:
                // todo
                break;
            case R.id.tvHelp:
                // todo
                break;
            case R.id.tvSuggest:
                // todo
                break;
            case R.id.tvRate:
                // todo
                break;
            case R.id.tvOpen:
                // todo
                break;
            case R.id.tvProtocol:
                // todo
                break;
            case R.id.tvContact:
                // todo
                break;
            case R.id.tvExist:
                PreferenceUser.clearUser();
                PreferenceUser.clearCouple();
                LoginActivity.goActivity(mActivity);
                break;
        }
    }

    @OnCheckedChanged({R.id.switchWifi, R.id.switchDownland, R.id.switchSystem, R.id.switchTa, R.id.switchOther})
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.switchWifi:
                PreferenceUser.setSettingsOnlyWifi(isChecked);
                break;
            case R.id.switchDownland:
                PreferenceUser.setSettingsAutoDownload(isChecked);
                break;
            case R.id.switchSystem:
                PreferenceUser.setSettingsNoticeSystem(isChecked);
                break;
            case R.id.switchTa:
                PreferenceUser.setSettingsNoticeTa(isChecked);
                break;
            case R.id.switchOther:
                PreferenceUser.setSettingsNoticeOther(isChecked);
                break;
        }
    }

}
