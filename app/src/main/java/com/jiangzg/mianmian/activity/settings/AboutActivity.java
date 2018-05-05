package com.jiangzg.mianmian.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.WebActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Version;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.service.UpdateService;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutActivity extends BaseActivity<AboutActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvAppInfo)
    TextView tvAppInfo;
    @BindView(R.id.llUpdate)
    LinearLayout llUpdate;
    @BindView(R.id.tvUpdateSummary)
    TextView tvUpdateSummary;
    @BindView(R.id.llRate)
    LinearLayout llRate;
    @BindView(R.id.llProtocol)
    LinearLayout llProtocol;
    @BindView(R.id.llAbout)
    LinearLayout llAbout;
    @BindView(R.id.tvCompany)
    TextView tvCompany;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AboutActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_about;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.about_mian_mian), true);
    }

    @Override
    protected void initData(Bundle state) {
        // AppInfo
        String name = AppInfo.get().getName();
        String versionName = AppInfo.get().getVersionName();
        tvAppInfo.setText(String.format(Locale.getDefault(), "%s v%s", name, versionName));
        // 版本更新
        String versionNewShow = getString(R.string.already_is_latest_version);
        Version version = SPHelper.getVersion();
        if (version != null) {
            String holder = getString(R.string.new_version_colon_space_holder);
            versionNewShow = String.format(Locale.getDefault(), holder, version.getVersionName());
        } else {
            tvUpdateSummary.setTextColor(ContextCompat.getColor(mActivity, R.color.font_grey));
        }
        tvUpdateSummary.setText(versionNewShow);
        // 公司名
        String companyName = SPHelper.getCommonConst().getCompanyName();
        tvCompany.setText(companyName);
    }

    @OnClick({R.id.llUpdate, R.id.llRate, R.id.llProtocol, R.id.llAbout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llUpdate: // 软件更新
                UpdateService.checkUpdate(mActivity);
                break;
            case R.id.llRate: // 去评价
                Intent market = IntentFactory.getMarket("");
                // TODO 机型适配问题
                break;
            case R.id.llProtocol: // 用户协议
                String userProtocolUrl = SPHelper.getCommonConst().getUserProtocolUrl();
                WebActivity.goActivity(mActivity, userProtocolUrl);
                break;
            case R.id.llAbout: // 关于我们
                String contactUsUrl = SPHelper.getCommonConst().getAboutUsUrl();
                WebActivity.goActivity(mActivity, contactUsUrl);
                break;
        }
    }
}
