package com.jiangzg.ita.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TextInputEditText;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.component.intent.IntentUtils;
import com.jiangzg.base.function.ContactUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.base.BaseFragment;
import com.jiangzg.ita.base.BasePagerFragment;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.third.RxBus;
import com.jiangzg.ita.utils.Constants;
import com.jiangzg.ita.utils.ViewUtils;
import com.jiangzg.ita.view.GVerticalSwipeRefresh;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class PairFragment extends BasePagerFragment<PairFragment> {

    private static final int SHOW_NULL = -1;
    private static final int SHOW_INPUT_ONLY = 0;
    private static final int SHOW_CARD_COMPLEX_TA = 1; // 复合他人中
    private static final int SHOW_CARD_COMPLEX_BY_TA = 2; // 被他人复合中
    private static final int SHOW_CARD_INVITEE_TA = 3; // 邀请他人中
    private static final int SHOW_CARD_INVITEE_BY_TA = 4; // 被他人邀请中
    private static final int SHOW_CARD_PAIR_LATE = 5; // 我知道了
    private static final int SHOW_CARD_COUPLE_EXIST = 6; // 我知道了
    private static final int SHOW_CARD_COMPLEX_CAN = 7; // 发送复合请求
    private static final int SHOW_CARD_PAIR_CAN = 8; // 发送配对请求
    private static final int SHOW_CARD_COMPLEX_PAIR_CAN = 9; // 发送配对+复合请求(虚拟的)

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etPhone)
    TextInputEditText etPhone;
    @BindView(R.id.ivContact)
    ImageView ivContact;
    @BindView(R.id.btnFind)
    Button btnFind;
    @BindView(R.id.llInput)
    LinearLayout llInput;
    @BindView(R.id.tvUserCardTop)
    TextView tvUserCardTop;
    @BindView(R.id.btnComplexTa)
    Button btnComplexTa;
    @BindView(R.id.llComplexTa)
    LinearLayout llComplexTa;
    @BindView(R.id.btnComplexByTaRefuse)
    Button btnComplexByTaRefuse;
    @BindView(R.id.btnComplexByTaAccept)
    Button btnComplexByTaAccept;
    @BindView(R.id.llComplexByTa)
    LinearLayout llComplexByTa;
    @BindView(R.id.btnInviteeTa)
    Button btnInviteeTa;
    @BindView(R.id.llInviteeTa)
    LinearLayout llInviteeTa;
    @BindView(R.id.btnInviteeByTaRefuse)
    Button btnInviteeByTaRefuse;
    @BindView(R.id.btnInviteeByTaAccept)
    Button btnInviteeByTaAccept;
    @BindView(R.id.llInviteeByTa)
    LinearLayout llInviteeByTa;
    @BindView(R.id.btnPairLate)
    Button btnPairLate;
    @BindView(R.id.llPairLate)
    LinearLayout llPairLate;
    @BindView(R.id.btnCoupleExist)
    Button btnCoupleExist;
    @BindView(R.id.llCoupleExist)
    LinearLayout llCoupleExist;
    @BindView(R.id.btnComplexCanSend)
    Button btnComplexOk;
    @BindView(R.id.btnComplexCanCancel)
    Button btnComplexCancel;
    @BindView(R.id.llComplexCan)
    LinearLayout llComplexCan;
    @BindView(R.id.btnPairCanSend)
    Button btnPairCanOk;
    @BindView(R.id.btnPairCanCancel)
    Button btnPairCanCancel;
    @BindView(R.id.llPairCan)
    LinearLayout llPairCan;
    @BindView(R.id.card)
    CardView card;
    @BindView(R.id.vsr)
    GVerticalSwipeRefresh vsr;

    private String taPhone;
    private int showType = SHOW_NULL;

    public static PairFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(PairFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_pair;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        ViewUtils.initTopBar(mActivity, tb, getString(R.string.pair), false);
        fitToolBar(tb);
        // listener
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 帮助
                        HelpActivity.goActivity(mActivity, HelpActivity.TYPE_COUPLE_PAIR);
                        break;
                }
                return true;
            }
        });
        vsr.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    @Override
    protected void refreshData() {
        if (!vsr.isRefreshing()) {
            vsr.setRefreshing(true);
        }
        // todo api获取home数据
        MyApp.get().getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vsr.setRefreshing(false);
                showType = SHOW_INPUT_ONLY;
                changeViewShow();
            }
        }, 1000);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.help, menu);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.REQUEST_CONTACT) {
            String select = ContactUtils.getContactSelect(data);
            etPhone.setText(select);
        }
    }

    @OnTextChanged({R.id.etPhone})
    public void afterTextChanged(Editable s) {
        onInputChange();
    }

    @OnClick({R.id.ivContact, R.id.btnFind, R.id.btnComplexTa, R.id.btnComplexByTaRefuse,
            R.id.btnComplexByTaAccept, R.id.btnInviteeTa, R.id.btnInviteeByTaRefuse,
            R.id.btnInviteeByTaAccept, R.id.btnPairLate, R.id.btnCoupleExist, R.id.btnComplexCanSend,
            R.id.btnComplexCanCancel, R.id.btnPairCanSend, R.id.btnPairCanCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivContact: // 联系人
                selectContact();
                break;
            case R.id.btnFind: // 开始寻找
                findTa();
                break;
            case R.id.btnComplexTa: // 停止复合
                stopComplex();
                break;
            case R.id.btnComplexByTaRefuse: // 拒绝复合
                refuseComplex();
                break;
            case R.id.btnComplexByTaAccept: // 接受复合
                acceptComplex();
                break;
            case R.id.btnInviteeTa: // 停止邀请
                stopInvitee();
                break;
            case R.id.btnInviteeByTaRefuse: // 拒绝邀请
                refuseInvitee();
                break;
            case R.id.btnInviteeByTaAccept: // 接受邀请
                acceptInvitee();
                break;
            case R.id.btnPairLate: // 邀请晚了
                //showType = SHOW_INPUT_ONLY;
                showType = ++showType;
                changeViewShow();
                break;
            case R.id.btnCoupleExist: // 配对存在了
                //showType = SHOW_INPUT_ONLY;
                showType = ++showType;
                changeViewShow();
                break;
            case R.id.btnComplexCanSend: // 开始复合
                startComplex();
                break;
            case R.id.btnComplexCanCancel: // 不复合
                if (showType == SHOW_CARD_COMPLEX_PAIR_CAN) {
                    showType = SHOW_CARD_PAIR_CAN;
                } else {
                    showType = SHOW_INPUT_ONLY;
                }
                changeViewShow();
                break;
            case R.id.btnPairCanSend: // 开始邀请
                startPair();
                break;
            case R.id.btnPairCanCancel: // 不邀请
                if (showType == SHOW_CARD_COMPLEX_PAIR_CAN) {
                    showType = SHOW_CARD_COMPLEX_CAN;
                } else {
                    showType = SHOW_INPUT_ONLY;
                }
                changeViewShow();
                break;
        }
    }

    private void onInputChange() {
        boolean phone = etPhone.getText().toString().trim().length() > 0;
        btnFind.setEnabled(phone);
    }

    private void selectContact() {
        Intent contacts = IntentUtils.getContacts();
        ActivityTrans.startResult(mFragment, contacts, Constants.REQUEST_CONTACT);
    }

    private void changeViewShow() {
        if (showType == SHOW_NULL) {
            llInput.setVisibility(View.GONE);
            card.setVisibility(View.GONE);
            llComplexTa.setVisibility(View.GONE);
            llComplexByTa.setVisibility(View.GONE);
            llInviteeTa.setVisibility(View.GONE);
            llInviteeByTa.setVisibility(View.GONE);
            llPairLate.setVisibility(View.GONE);
            llCoupleExist.setVisibility(View.GONE);
            llComplexCan.setVisibility(View.GONE);
            llPairCan.setVisibility(View.GONE);
        } else if (showType == SHOW_INPUT_ONLY) {
            llInput.setVisibility(View.VISIBLE);
            card.setVisibility(View.GONE);
            llComplexTa.setVisibility(View.GONE);
            llComplexByTa.setVisibility(View.GONE);
            llInviteeTa.setVisibility(View.GONE);
            llInviteeByTa.setVisibility(View.GONE);
            llPairLate.setVisibility(View.GONE);
            llCoupleExist.setVisibility(View.GONE);
            llComplexCan.setVisibility(View.GONE);
            llPairCan.setVisibility(View.GONE);
        } else if (showType == SHOW_CARD_COMPLEX_TA) {
            llInput.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            llComplexTa.setVisibility(View.VISIBLE);
            llComplexByTa.setVisibility(View.GONE);
            llInviteeTa.setVisibility(View.GONE);
            llInviteeByTa.setVisibility(View.GONE);
            llPairLate.setVisibility(View.GONE);
            llCoupleExist.setVisibility(View.GONE);
            llComplexCan.setVisibility(View.GONE);
            llPairCan.setVisibility(View.GONE);
        } else if (showType == SHOW_CARD_COMPLEX_BY_TA) {
            llInput.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            llComplexTa.setVisibility(View.GONE);
            llComplexByTa.setVisibility(View.VISIBLE);
            llInviteeTa.setVisibility(View.GONE);
            llInviteeByTa.setVisibility(View.GONE);
            llPairLate.setVisibility(View.GONE);
            llCoupleExist.setVisibility(View.GONE);
            llComplexCan.setVisibility(View.GONE);
            llPairCan.setVisibility(View.GONE);
        } else if (showType == SHOW_CARD_INVITEE_TA) {
            llInput.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            llComplexTa.setVisibility(View.GONE);
            llComplexByTa.setVisibility(View.GONE);
            llInviteeTa.setVisibility(View.VISIBLE);
            llInviteeByTa.setVisibility(View.GONE);
            llPairLate.setVisibility(View.GONE);
            llCoupleExist.setVisibility(View.GONE);
            llComplexCan.setVisibility(View.GONE);
            llPairCan.setVisibility(View.GONE);
        } else if (showType == SHOW_CARD_INVITEE_BY_TA) {
            llInput.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            llComplexTa.setVisibility(View.GONE);
            llComplexByTa.setVisibility(View.GONE);
            llInviteeTa.setVisibility(View.GONE);
            llInviteeByTa.setVisibility(View.VISIBLE);
            llPairLate.setVisibility(View.GONE);
            llCoupleExist.setVisibility(View.GONE);
            llComplexCan.setVisibility(View.GONE);
            llPairCan.setVisibility(View.GONE);
        } else if (showType == SHOW_CARD_PAIR_LATE) {
            llInput.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            llComplexTa.setVisibility(View.GONE);
            llComplexByTa.setVisibility(View.GONE);
            llInviteeTa.setVisibility(View.GONE);
            llInviteeByTa.setVisibility(View.GONE);
            llPairLate.setVisibility(View.VISIBLE);
            llCoupleExist.setVisibility(View.GONE);
            llComplexCan.setVisibility(View.GONE);
            llPairCan.setVisibility(View.GONE);
        } else if (showType == SHOW_CARD_COUPLE_EXIST) {
            llInput.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            llComplexTa.setVisibility(View.GONE);
            llComplexByTa.setVisibility(View.GONE);
            llInviteeTa.setVisibility(View.GONE);
            llInviteeByTa.setVisibility(View.GONE);
            llPairLate.setVisibility(View.GONE);
            llCoupleExist.setVisibility(View.VISIBLE);
            llComplexCan.setVisibility(View.GONE);
            llPairCan.setVisibility(View.GONE);
        } else if (showType == SHOW_CARD_COMPLEX_CAN) {
            llInput.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            llComplexTa.setVisibility(View.GONE);
            llComplexByTa.setVisibility(View.GONE);
            llInviteeTa.setVisibility(View.GONE);
            llInviteeByTa.setVisibility(View.GONE);
            llPairLate.setVisibility(View.GONE);
            llCoupleExist.setVisibility(View.GONE);
            llComplexCan.setVisibility(View.VISIBLE);
            llPairCan.setVisibility(View.GONE);
        } else if (showType == SHOW_CARD_PAIR_CAN) {
            llInput.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            llComplexTa.setVisibility(View.GONE);
            llComplexByTa.setVisibility(View.GONE);
            llInviteeTa.setVisibility(View.GONE);
            llInviteeByTa.setVisibility(View.GONE);
            llPairLate.setVisibility(View.GONE);
            llCoupleExist.setVisibility(View.GONE);
            llComplexCan.setVisibility(View.GONE);
            llPairCan.setVisibility(View.VISIBLE);
        } else if (showType == SHOW_CARD_COMPLEX_PAIR_CAN) {
            llInput.setVisibility(View.GONE);
            card.setVisibility(View.VISIBLE);
            llComplexTa.setVisibility(View.GONE);
            llComplexByTa.setVisibility(View.GONE);
            llInviteeTa.setVisibility(View.GONE);
            llInviteeByTa.setVisibility(View.GONE);
            llPairLate.setVisibility(View.GONE);
            llCoupleExist.setVisibility(View.GONE);
            llComplexCan.setVisibility(View.VISIBLE);
            llPairCan.setVisibility(View.VISIBLE);
        }
    }

    private void findTa() {
        taPhone = etPhone.getText().toString().trim();
        // todo api获取Ta信息
        //showType = ++showType;
        //changeViewShow();
        RxEvent<Couple> event = new RxEvent<>(Constants.EVENT_COUPLE, new Couple());
        RxBus.post(event);
    }

    // 开始邀请 todo
    private void startPair() {
        showType = ++showType;
        changeViewShow();
    }

    // 开始复合 todo
    private void startComplex() {
        showType = ++showType;
        changeViewShow();
    }

    // 接受邀请 todo
    private void acceptInvitee() {
        showType = ++showType;
        changeViewShow();
    }

    // 拒绝邀请 todo
    private void refuseInvitee() {
        showType = ++showType;
        changeViewShow();
    }

    // 停止邀请 todo
    private void stopInvitee() {
        showType = ++showType;
        changeViewShow();
    }

    // 接受复合 todo
    private void acceptComplex() {
        showType = ++showType;
        changeViewShow();
    }

    // 拒绝复合 todo
    private void refuseComplex() {
        showType = ++showType;
        changeViewShow();
    }

    // 停止复合 todo
    private void stopComplex() {
        showType = ++showType;
        changeViewShow();
    }

}
