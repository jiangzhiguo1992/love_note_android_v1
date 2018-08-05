package com.jiangzg.lovenote.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.CommonConst;
import com.jiangzg.lovenote.domain.Version;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.service.UpdateService;

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
    @BindView(R.id.llProtocol)
    LinearLayout llProtocol;
    @BindView(R.id.tvQQ)
    TextView tvQQ;
    @BindView(R.id.tvContact)
    TextView tvContact;
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
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.about_app), true);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        CommonConst commonConst = SPHelper.getCommonConst();
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
        // 官方qq群
        String officialQQ = commonConst.getOfficialQQ();
        tvQQ.setText(StringUtils.isEmpty(officialQQ) ? getString(R.string.now_no) : officialQQ);
        // 联系我们
        String contactEmail = commonConst.getContactEmail();
        tvContact.setText(StringUtils.isEmpty(contactEmail) ? getString(R.string.now_no) : contactEmail);
        // 公司名
        String companyName = commonConst.getCompanyName();
        tvCompany.setText(companyName);
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.llUpdate, R.id.llProtocol})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llUpdate: // 软件更新
                UpdateService.checkUpdate(mActivity);
                break;
            case R.id.llProtocol: // 用户协议
                UserProtocolActivity.goActivity(mActivity);
                break;
        }
    }
}
