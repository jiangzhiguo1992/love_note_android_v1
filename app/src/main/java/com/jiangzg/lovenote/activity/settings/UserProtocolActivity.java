package com.jiangzg.lovenote.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ViewHelper;

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
        // TODO
        tvContent.setText("用户协议用户协议用户协议用户协议" +
                "\n用户协议用户协议用户协议用户协议用户协议用户协议用户协议用户协议" +
                "\n用户协议用户协议用户协议用户协议用户协议用户协议用户协议用户协议" +
                "\n用户协议用户协议用户协议用户协议用户协议用户协议用户协议用户协议" +
                "\n用户协议用户协议用户协议用户协议用户协议用户协议用户协议用户协议" +
                "\n用户协议用户协议用户协议用户协议用户协议用户协议用户协议用户协议" +
                "\n用户协议用户协议用户协议用户协议用户协议用户协议用户协议用户协议" +
                "\n用户协议用户协议用户协议用户协议用户协议用户协议用户协议用户协议" +
                "\n用户协议用户协议用户协议用户协议用户协议用户协议用户协议用户协议" +
                "\n用户协议用户协议用户协议用户协议用户协议用户协议用户协议用户协议");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

}
