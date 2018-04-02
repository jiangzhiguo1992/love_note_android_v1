package com.jiangzg.ita.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.component.IntentSend;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class CouplePairActivity extends BaseActivity<CouplePairActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;

    @BindView(R.id.llInput)
    LinearLayout llInput;
    @BindView(R.id.etPhone)
    TextInputEditText etPhone;
    @BindView(R.id.ivContact)
    ImageView ivContact;
    @BindView(R.id.btnInvitee)
    Button btnInvitee;
    @BindView(R.id.btnComplex)
    Button btnComplex;

    @BindView(R.id.tvTaPhone)
    TextView tvTaPhone;

    private String taPhone;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, CouplePairActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_couple_pair;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.pair), false);
        // listener
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getSelfCouple();
            }
        });
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 帮助
                        HelpActivity.goActivity(mActivity, Help.TYPE_COUPLE_PAIR);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void initData(Bundle state) {
        getSelfCouple();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ConsHelper.REQUEST_CONTACT) {
            String select = IntentResult.getContactSelect(data);
            etPhone.setText(select);
        }
    }

    @OnTextChanged({R.id.etPhone})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.ivContact, R.id.btnInvitee, R.id.btnComplex})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivContact: // 联系人
                selectContact();
                break;
            case R.id.btnInvitee: // 邀请
                // todo
                ToastUtils.show("邀请");
                break;
            case R.id.btnComplex: // 查询可复合
                // todo
                ToastUtils.show("查询可复合");
                break;
        }
    }

    private void onInputChange() {
        boolean phone = etPhone.getText().toString().trim().length() > 0;
        btnInvitee.setEnabled(phone);
        btnComplex.setEnabled(phone);
    }

    private void selectContact() {
        Intent contacts = IntentSend.getContacts();
        ActivityTrans.startResult(mActivity, contacts, ConsHelper.REQUEST_CONTACT);
    }

    private void getSelfCouple() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // todo api
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(false);
                refreshSelfCoupleView(null);
            }
        }, 1000);
    }

    private void refreshSelfCoupleView(Couple couple) {
        llInput.setVisibility(View.VISIBLE);
        tvTaPhone.setVisibility(View.VISIBLE);
    }

}
