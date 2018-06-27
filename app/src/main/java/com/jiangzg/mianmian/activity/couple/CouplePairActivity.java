package com.jiangzg.mianmian.activity.couple;

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
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.PairCard;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.Locale;

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

    @BindView(R.id.cardResult)
    CardView cardResult;
    @BindView(R.id.tvCardPhone)
    TextView tvCardPhone;
    @BindView(R.id.tvCardTitle)
    TextView tvCardTitle;
    @BindView(R.id.tvCardMessage)
    TextView tvCardMessage;
    @BindView(R.id.btnCardBad)
    Button btnCardBad;
    @BindView(R.id.btnCardGood)
    Button btnCardGood;

    private long coupleId;
    private Call<Result> callRefreshSelf;
    private Call<Result> callInvitee;
    private Call<Result> callUpdate;
    private Call<Result> callGetVisible;

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
        // srl
        srl.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshSelfCouple();
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
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callRefreshSelf);
        RetrofitHelper.cancel(callGetVisible);
        RetrofitHelper.cancel(callInvitee);
        RetrofitHelper.cancel(callUpdate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == ConsHelper.REQUEST_CONTACT) {
            String select = IntentResult.getContactSelect(data);
            etPhone.setText(select);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_COUPLE_PAIR);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etPhone})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.ivContact, R.id.btnInvitee, R.id.btnCardBad, R.id.btnCardGood})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivContact: // 联系人
                selectContact();
                break;
            case R.id.btnInvitee: // 邀请
                inviteeTa();
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
    }

    private void selectContact() {
        Intent contacts = IntentFactory.getContacts();
        ActivityTrans.startResult(mActivity, contacts, ConsHelper.REQUEST_CONTACT);
    }

    private void allViewGone() {
        llInput.setVisibility(View.GONE);
        cardResult.setVisibility(View.GONE);
    }

    // 获取自身可见的cp
    private void refreshSelfCouple() {
        allViewGone();
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api获取和ta的以往cp
        callRefreshSelf = new RetrofitHelper().call(API.class).coupleGet(true, 0);
        RetrofitHelper.enqueue(callRefreshSelf, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(data);
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(null);
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
        callInvitee = new RetrofitHelper().call(API.class).coupleInvitee(user);
        RetrofitHelper.enqueue(callInvitee, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                // 邀请成功，重新获取等待的cp
                refreshSelfCouple();
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(null);
            }
        });
    }

    // 刷新view
    private void refreshSelfCoupleView(Result.Data data) {
        if (data == null || Couple.isEmpty(data.getCouple())) {
            // 没有等待处理的
            coupleId = 0;
            llInput.setVisibility(View.VISIBLE);
            cardResult.setVisibility(View.GONE);
            return;
        }
        Couple couple = data.getCouple();
        // 有等待处理的
        coupleId = couple.getId();
        llInput.setVisibility(View.GONE);
        PairCard pairCard = data.getPairCard();
        // card
        cardResult.setVisibility(View.VISIBLE);
        // taPhone
        String taPhone = pairCard.getTaPhone();
        String phone = String.format(Locale.getDefault(), getString(R.string.ta_colon_space_holder), taPhone);
        if (StringUtils.isEmpty(taPhone)) {
            tvCardPhone.setVisibility(View.GONE);
        } else {
            tvCardPhone.setVisibility(View.VISIBLE);
            tvCardPhone.setText(phone);
        }

        String title = pairCard.getTitle();
        if (StringUtils.isEmpty(title)) {
            tvCardTitle.setVisibility(View.GONE);
        } else {
            tvCardTitle.setVisibility(View.VISIBLE);
            tvCardTitle.setText(title);
        }
        String message = pairCard.getMessage();
        if (StringUtils.isEmpty(message)) {
            if (StringUtils.isEmpty(data.getShow())) {
                tvCardMessage.setVisibility(View.GONE);
            } else {
                tvCardMessage.setVisibility(View.VISIBLE);
                tvCardMessage.setText(data.getShow());
            }
        } else {
            tvCardMessage.setVisibility(View.VISIBLE);
            tvCardMessage.setText(message);
        }
        String btnGood = pairCard.getBtnGood();
        if (StringUtils.isEmpty(btnGood)) {
            btnCardGood.setVisibility(View.GONE);
        } else {
            btnCardGood.setVisibility(View.VISIBLE);
            btnCardGood.setText(btnGood);
        }
        String btnBad = pairCard.getBtnBad();
        if (StringUtils.isEmpty(btnBad)) {
            btnCardBad.setVisibility(View.GONE);
        } else {
            btnCardBad.setVisibility(View.VISIBLE);
            btnCardBad.setText(btnBad);
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
        callUpdate = new RetrofitHelper().call(API.class).coupleUpdate(body);
        RetrofitHelper.enqueue(callUpdate, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                coupleGetVisible();
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(null);
            }
        });
    }

    // 查看是否有配对成功的
    private void coupleGetVisible() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api获取和ta的以往cp
        long uid = SPHelper.getMe().getId();
        callGetVisible = new RetrofitHelper().call(API.class).coupleGet(false, uid);
        RetrofitHelper.enqueue(callGetVisible, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                if (data != null && !Couple.isBreak(data.getCouple())) {
                    // 有配对成功的，退出本界面
                    Couple couple = data.getCouple();
                    SPHelper.setCouple(couple);
                    RxEvent<Couple> event = new RxEvent<>(ConsHelper.EVENT_COUPLE_REFRESH, new Couple());
                    RxBus.post(event);
                    mActivity.finish();
                } else {
                    // 没有则刷新数据和view
                    refreshSelfCouple();
                }
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(null);
            }
        });
    }

}
