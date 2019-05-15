package com.jiangzg.lovenote.controller.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.service.UpdateService;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.entity.CommonConst;
import com.jiangzg.lovenote.model.entity.Version;

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
    @BindView(R.id.llMarket)
    LinearLayout llMarket;
    @BindView(R.id.tvCustomerQQ)
    TextView tvCustomerQQ;
    @BindView(R.id.llWeiBo)
    LinearLayout llWeiBo;
    @BindView(R.id.tvWeiBo)
    TextView tvWeiBo;
    @BindView(R.id.llOfficialWeb)
    LinearLayout llOfficialWeb;
    @BindView(R.id.tvOfficialWeb)
    TextView tvOfficialWeb;
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
        // 客服Q号
        String customerQQ = commonConst.getCustomerQQ();
        tvCustomerQQ.setText(StringUtils.isEmpty(customerQQ) ? getString(R.string.now_no) : customerQQ.replace("\\n", "\n"));
        // 官方微博
        String officialWeibo = commonConst.getOfficialWeibo();
        tvWeiBo.setText(StringUtils.isEmpty(officialWeibo) ? getString(R.string.now_no) : officialWeibo);
        // 官方网站
        String officialWeb = commonConst.getOfficialWeb();
        tvOfficialWeb.setText(StringUtils.isEmpty(officialWeb) ? getString(R.string.now_no) : officialWeb);
        // 联系我们
        String contactEmail = commonConst.getContactEmail();
        tvContact.setText(StringUtils.isEmpty(contactEmail) ? getString(R.string.now_no) : contactEmail.replace("\\n", "\n"));
        // 公司名
        String companyName = commonConst.getCompanyName();
        tvCompany.setText(companyName);
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_protocol, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuProtocol: // 用户协议
                UserProtocolActivity.goActivity(mActivity);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llUpdate, R.id.llMarket, R.id.llWeiBo, R.id.llOfficialWeb})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llUpdate: // 软件更新
                UpdateService.checkUpdate(mActivity);
                break;
            case R.id.llMarket: // 给个好评
                Intent market = IntentFactory.getMarket();
                ActivityTrans.start(mActivity, market);
                break;
            case R.id.llWeiBo: // 官网微博
                String officialWeibo = SPHelper.getCommonConst().getOfficialWeibo();
                Intent weiboUser = IntentFactory.getWeiboUser(officialWeibo);
                ActivityTrans.start(mActivity, weiboUser);
                break;
            case R.id.llOfficialWeb: // 官网网站
                String officialWeb = SPHelper.getCommonConst().getOfficialWeb();
                if (StringUtils.isEmpty(officialWeb)) return;
                if (!officialWeb.startsWith("http")) {
                    officialWeb = "http://" + officialWeb;
                }
                Intent intent = IntentFactory.getWebBrowse(officialWeb);
                ActivityTrans.start(mActivity, intent);
                break;
        }
    }
}
