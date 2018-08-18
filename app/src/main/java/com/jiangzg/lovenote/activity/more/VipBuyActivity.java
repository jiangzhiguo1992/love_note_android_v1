package com.jiangzg.lovenote.activity.more;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.AliPayResult;
import com.jiangzg.lovenote.domain.Limit;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.PayHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class VipBuyActivity extends BaseActivity<VipBuyActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rgGoods)
    RadioGroup rgGoods;
    @BindView(R.id.rbGoods1)
    RadioButton rbGoods1;
    @BindView(R.id.rbGoods2)
    RadioButton rbGoods2;
    @BindView(R.id.rbGoods3)
    RadioButton rbGoods3;
    @BindView(R.id.btnAliPay)
    Button btnAliPay;
    @BindView(R.id.btnWeChatPay)
    Button btnWeChatPay;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, VipBuyActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_vip_buy;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.vip_buy), true);
        initGoodsCheck();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

    @OnClick({R.id.btnAliPay, R.id.btnWeChatPay})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btnAliPay: // 支付宝支付
                payBefore(ApiHelper.BILL_PAY_PLATFORM_ALI);
                break;
            case R.id.btnWeChatPay: // 微信支付
                payBefore(ApiHelper.BILL_PAY_PLATFORM_WX);
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void initGoodsCheck() {
        Limit limit = SPHelper.getLimit();
        String format = getString(R.string.pay_vip_goods_format);
        String goods1 = String.format(Locale.getDefault(), format, limit.getPayVipGoods1Title(), limit.getPayVipGoods1Days(), limit.getPayVipGoods1Amount());
        String goods2 = String.format(Locale.getDefault(), format, limit.getPayVipGoods2Title(), limit.getPayVipGoods2Days(), limit.getPayVipGoods2Amount());
        String goods3 = String.format(Locale.getDefault(), format, limit.getPayVipGoods3Title(), limit.getPayVipGoods3Days(), limit.getPayVipGoods3Amount());
        rbGoods1.setText(goods1);
        rbGoods2.setText(goods2);
        rbGoods3.setText(goods3);
        rbGoods1.setChecked(true);
        refreshPayBtn();
    }

    private void refreshPayBtn() {
        if (!rbGoods1.isChecked() && !rbGoods2.isChecked() && !rbGoods3.isChecked()) {
            btnAliPay.setEnabled(false);
            btnWeChatPay.setEnabled(false);
        }
        btnAliPay.setEnabled(true);
        btnWeChatPay.setEnabled(true);
    }

    public void payBefore(final int payPlatform) {
        final int goods;
        if (rbGoods1.isChecked()) {
            goods = ApiHelper.BILL_GOODS_VIP_1;
        } else if (rbGoods2.isChecked()) {
            goods = ApiHelper.BILL_GOODS_VIP_2;
        } else if (rbGoods3.isChecked()) {
            goods = ApiHelper.BILL_GOODS_VIP_3;
        } else {
            return;
        }
        Call<Result> call = new RetrofitHelper().call(API.class).morePayBeforeAliGet(payPlatform, goods);
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (payPlatform == ApiHelper.BILL_PAY_PLATFORM_ALI) {
                    String orderInfo = data.getAliOrderInfo();
                    startAliPay(orderInfo);
                } else if (payPlatform == ApiHelper.BILL_PAY_PLATFORM_WX) {
                    startWeChatPay();
                }
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void startAliPay(final String orderInfo) {
        PayHelper.payByAli(mActivity, orderInfo, new PayHelper.AliCallBack() {
            @Override
            public void onSuccess(AliPayResult result) {
                String memo = result.getMemo();
                String resultStatus = result.getResultStatus();
                AliPayResult.Result result1 = result.getResult();
                // TODO 结果处理
                // TODO 界面也要变化
                // TODO 轮询服务器 请求服务器处理结果
            }
        });
    }

    private void startWeChatPay() {
        // TODO
    }

    private void waitPayResult(final int payPlatform) {
        // TODO
    }

    private void checkPayResult() {
        // TODO
    }

}
