package com.jiangzg.ita.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.component.IntentSend;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.CheckHelper;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.helper.RxBus;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

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
    @BindView(R.id.cardResult)
    CardView cardResult;
    @BindView(R.id.tvCardTitle)
    TextView tvCardTitle;
    @BindView(R.id.tvCardMessage)
    TextView tvCardMessage;
    @BindView(R.id.btnCardBad)
    Button btnCardBad;
    @BindView(R.id.btnCardGood)
    Button btnCardGood;

    private long coupleId;

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
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.pair), true);
        // listener
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshSelfCouple();
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
        refreshSelfCouple();
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

    @OnClick({R.id.ivContact, R.id.btnInvitee, R.id.btnComplex, R.id.btnCardBad, R.id.btnCardGood})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivContact: // 联系人
                selectContact();
                break;
            case R.id.btnInvitee: // 邀请
                inviteeTa();
                break;
            case R.id.btnComplex: // 查询可复合
                findComplex();
                break;
            case R.id.btnCardBad: // 变坏
                updateCouple2Bad();
                break;
            case R.id.btnCardGood: // 变好
                updateCouple2Good();
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

    private void allViewGone() {
        llInput.setVisibility(View.GONE);
        tvTaPhone.setVisibility(View.GONE);
        cardResult.setVisibility(View.GONE);
    }

    // 获取自身可见的cp
    private void refreshSelfCouple() {
        allViewGone();
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api获取和ta的以往cp
        Call<Result> call = new RetrofitHelper().call(API.class).coupleGet(true, "", 0, 0);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(data);
            }

            @Override
            public void onFailure() {
                srl.setRefreshing(false);
            }
        });
    }

    // 邀请ta
    private void inviteeTa() {
        allViewGone();
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api邀请
        User user = new User();
        user.setPhone(etPhone.getText().toString().trim());
        Call<Result> call = new RetrofitHelper().call(API.class).coupleInvitee(user);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                // 邀请成功，重新获取等待的cp
                refreshSelfCouple();
            }

            @Override
            public void onFailure() {
                srl.setRefreshing(false);
            }
        });
    }

    // 发现可以复合的cp
    private void findComplex() {
        allViewGone();
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api获取正在等待处理的cp
        String phone = etPhone.getText().toString().trim();
        Call<Result> call = new RetrofitHelper().call(API.class).coupleGet(false, phone, 0, 0);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(data);
            }

            @Override
            public void onFailure() {
                srl.setRefreshing(false);
            }
        });
    }

    // 刷新view
    private void refreshSelfCoupleView(Result.Data data) {
        if (data == null || CheckHelper.isNullCouple(data.getCouple())) {
            // 没有等待处理的
            coupleId = 0;
            llInput.setVisibility(View.VISIBLE);
            tvTaPhone.setVisibility(View.GONE);
            cardResult.setVisibility(View.GONE);
            return;
        }
        Couple couple = data.getCouple();
        // 有等待处理的
        coupleId = couple.getId();
        llInput.setVisibility(View.GONE);
        // taPhone
        tvTaPhone.setVisibility(View.VISIBLE);
        String taPhone = data.getTaPhone();
        String phone = String.format(getString(R.string.ta_colon_holder), taPhone);
        tvTaPhone.setText(phone);
        // card
        cardResult.setVisibility(View.VISIBLE);
        String title = data.getTitle();
        if (StringUtils.isEmpty(title)) {
            tvCardTitle.setVisibility(View.GONE);
        } else {
            tvCardTitle.setText(title);
            tvCardTitle.setVisibility(View.VISIBLE);
        }
        String message = data.getMessage();
        if (StringUtils.isEmpty(message)) {
            tvCardMessage.setVisibility(View.GONE);
        } else {
            tvCardMessage.setText(message);
            tvCardMessage.setVisibility(View.VISIBLE);
        }
        String btnGood = data.getBtnGood();
        if (StringUtils.isEmpty(btnGood)) {
            btnCardGood.setVisibility(View.GONE);
        } else {
            btnCardGood.setText(btnGood);
            btnCardGood.setVisibility(View.VISIBLE);
        }
        String btnBad = data.getBtnBad();
        if (StringUtils.isEmpty(btnBad)) {
            btnCardBad.setVisibility(View.GONE);
        } else {
            btnCardBad.setText(btnBad);
            btnCardBad.setVisibility(View.VISIBLE);
        }
    }

    // 变好
    private void updateCouple2Good() {
        allViewGone();
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api
        User body = ApiHelper.getCoupleUpdate2GoodBody(coupleId);
        coupleUpdate(body);
    }

    // 变坏
    private void updateCouple2Bad() {
        allViewGone();
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api
        User body = ApiHelper.getCoupleUpdate2BadBody(coupleId);
        coupleUpdate(body);
    }

    // 提交状态更新
    private void coupleUpdate(User body) {
        Call<Result> call = new RetrofitHelper().call(API.class).coupleUpdate(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                coupleGetVisible();
            }

            @Override
            public void onFailure() {
                srl.setRefreshing(false);
            }
        });
    }

    // 查看是否有配对成功的
    private void coupleGetVisible() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api获取和ta的以往cp
        long uid = SPHelper.getUser().getId();
        Call<Result> call = new RetrofitHelper().call(API.class).coupleGet(false, "", 0, uid);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                if (data != null && !CheckHelper.isCoupleBreak(data.getCouple())) {
                    // 有配对成功的，退出本界面
                    Couple couple = data.getCouple();
                    SPHelper.setCouple(couple);
                    RxEvent<Couple> event = new RxEvent<>(ConsHelper.EVENT_COUPLE, new Couple());
                    RxBus.post(event);
                    mActivity.finish();
                } else {
                    // 没有则刷新数据和view
                    refreshSelfCouple();
                }
            }

            @Override
            public void onFailure() {
                srl.setRefreshing(false);
            }
        });
    }

}
