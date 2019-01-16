package com.jiangzg.lovenote.controller.activity.couple;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
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
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.PairCard;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

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

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), CouplePairActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, CouplePairActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from) {
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
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.pair), true);
        // srl
        srl.setOnRefreshListener(this::refreshSelfCouple);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        refreshSelfCouple();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == BaseActivity.REQUEST_CONTACT) {
            String select = IntentResult.getContactSelect(data);
            etPhone.setText(select);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_COUPLE_PAIR);
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
        ActivityTrans.startResult(mActivity, contacts, BaseActivity.REQUEST_CONTACT);
    }

    private void allViewGone() {
        llInput.setVisibility(View.GONE);
        cardResult.setVisibility(View.GONE);
    }

    private void refreshSelfCouple() {
        allViewGone();
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api 获取自身可见的cp
        Call<Result> api = new RetrofitHelper().call(API.class).coupleSelfGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(data);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(null);
            }
        });
        pushApi(api);
    }

    private void inviteeTa() {
        allViewGone();
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        User user = new User();
        user.setPhone(etPhone.getText().toString().trim());
        // api 邀请
        Call<Result> api = new RetrofitHelper().call(API.class).coupleInvitee(user);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                // 邀请成功，重新获取等待的cp
                refreshSelfCouple();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(null);
            }
        });
        pushApi(api);
    }

    private void refreshSelfCoupleView(Result.Data data) {
        if (data == null || UserHelper.isEmpty(data.getCouple()) || data.getPairCard() == null) {
            // 没有等待处理的
            coupleId = 0;
            llInput.setVisibility(View.VISIBLE);
            cardResult.setVisibility(View.GONE);
            return;
        }
        // 有等待处理的
        coupleId = data.getCouple().getId();
        llInput.setVisibility(View.GONE);
        cardResult.setVisibility(View.VISIBLE);
        PairCard pairCard = data.getPairCard();
        // phone
        String taPhone = pairCard.getTaPhone();
        if (StringUtils.isEmpty(taPhone)) {
            tvCardPhone.setVisibility(View.GONE);
        } else {
            tvCardPhone.setVisibility(View.VISIBLE);
            String phone = String.format(Locale.getDefault(), getString(R.string.ta_colon_space_holder), taPhone);
            tvCardPhone.setText(phone);
        }
        // title
        String title = pairCard.getTitle();
        if (StringUtils.isEmpty(title)) {
            tvCardTitle.setVisibility(View.GONE);
        } else {
            tvCardTitle.setVisibility(View.VISIBLE);
            tvCardTitle.setText(title);
        }
        // message
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
        // bad
        String btnBad = pairCard.getBtnBad();
        if (StringUtils.isEmpty(btnBad)) {
            btnCardBad.setVisibility(View.GONE);
        } else {
            btnCardBad.setVisibility(View.VISIBLE);
            btnCardBad.setText(btnBad);
        }
        // good
        String btnGood = pairCard.getBtnGood();
        if (StringUtils.isEmpty(btnGood)) {
            btnCardGood.setVisibility(View.GONE);
        } else {
            btnCardGood.setVisibility(View.VISIBLE);
            btnCardGood.setText(btnGood);
        }
    }

    private void updateCouple2Bad() {
        coupleUpdate(ApiHelper.COUPLE_UPDATE_BAD, coupleId);
    }

    private void updateCouple2Good() {
        coupleUpdate(ApiHelper.COUPLE_UPDATE_GOOD, coupleId);
    }

    private void coupleUpdate(int type, long cid) {
        allViewGone();
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Couple couple = new Couple();
        couple.setId(cid);
        // api 提交状态更新
        Call<Result> api = new RetrofitHelper().call(API.class).coupleUpdate(type, couple);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                coupleGetVisible();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(null);
            }
        });
        pushApi(api);
    }

    private void coupleGetVisible() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        User me = SPHelper.getMe();
        if (UserHelper.isEmpty(me)) return;
        // api 查看是否有配对成功的
        Call<Result> api = new RetrofitHelper().call(API.class).coupleGet(me.getId());
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                if (data != null && !UserHelper.isCoupleBreak(data.getCouple())) {
                    // 有配对成功的，退出本界面
                    Couple couple = data.getCouple();
                    SPHelper.setCouple(couple);
                    RxBus.post(new RxBus.Event<>(RxBus.EVENT_COUPLE_REFRESH, couple));
                    mActivity.finish();
                } else {
                    // 没有则刷新数据和view
                    refreshSelfCouple();
                }
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                refreshSelfCoupleView(null);
            }
        });
        pushApi(api);
    }

}
