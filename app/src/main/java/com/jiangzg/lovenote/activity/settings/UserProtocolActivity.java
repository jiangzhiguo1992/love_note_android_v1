package com.jiangzg.lovenote.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;

import java.util.Locale;

import butterknife.BindView;

public class UserProtocolActivity extends BaseActivity<UserProtocolActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvContent)
    TextView tvContent;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, UserProtocolActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_user_protocol;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.user_protocol), true);
        String company = SPHelper.getCommonConst().getCompanyName();
        String appShort = getString(R.string.app_name);
        String app = "\"" + appShort + "\"";
        String companyApp = company + app;
        // 重要须知
        String importKnow = getString(R.string.agreement_import_know);
        String importKnowShow = String.format(Locale.getDefault(), importKnow, company, company, company, app, app);
        // 使用规则
        String useRule = getString(R.string.agreement_use_rule);
        String useRuleShow = String.format(Locale.getDefault(), useRule,
                companyApp, companyApp, company,
                companyApp, companyApp, company, company,
                companyApp, companyApp, company, companyApp,
                companyApp, company,
                company, companyApp,
                companyApp, companyApp,
                companyApp, companyApp,
                company, company, companyApp, companyApp,
                companyApp, company, companyApp, company, companyApp,
                companyApp,
                company,
                company, companyApp, company, companyApp);
        // 隐私保护
        String privateProtect = getString(R.string.agreement_private_protect);
        String privateProtectShow = String.format(Locale.getDefault(), privateProtect, company,
                company, company, company, company, company, company, company, company, company,
                companyApp, company, company);
        // 商标信息
        String iconInfo = getString(R.string.agreement_icon_info);
        String iconInfoShow = String.format(Locale.getDefault(), iconInfo, companyApp,
                companyApp, companyApp, companyApp, company, company, company, company);
        // 法律责任
        String lawResponse = getString(R.string.agreement_law_response);
        String lawResponseShow = String.format(Locale.getDefault(), lawResponse, company, company, company, company, company, company, company,
                company, company, company, company, companyApp, companyApp, company, companyApp, company);
        // 社区管理
        String socialManage = getString(R.string.agreement_social_manage);
        String socialManageShow = String.format(Locale.getDefault(), socialManage, app, app, company, companyApp, company, app);
        // 其他条款
        String otherClause = getString(R.string.agreement_other_clause);
        String otherClauseShow = String.format(Locale.getDefault(), otherClause, company, company, company, company, company);
        // 全程
        String allName = getString(R.string.agreement_all_name);
        String allNameShow = String.format(Locale.getDefault(), allName, appShort);
        // content
        String show = importKnowShow + "\n\n" + useRuleShow + "\n\n" + privateProtectShow + "\n\n" + iconInfoShow + "\n\n" + lawResponseShow + "\n\n" + socialManageShow + "\n\n" + otherClauseShow + "\n\n" + allNameShow;
        tvContent.setText(show);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

}
