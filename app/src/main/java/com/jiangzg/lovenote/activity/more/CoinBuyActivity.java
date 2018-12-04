package com.jiangzg.lovenote.activity.more;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.PayHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.entity.Bill;
import com.jiangzg.lovenote.model.entity.Coin;
import com.jiangzg.lovenote.model.entity.Limit;
import com.jiangzg.lovenote.model.entity.OrderBefore;
import com.jiangzg.lovenote.model.entity.PayAliResult;
import com.jiangzg.lovenote.model.entity.PayWxResult;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.WXOrder;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CoinBuyActivity extends BaseActivity<CoinBuyActivity> {

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
    @BindView(R.id.tvBillCheck)
    TextView tvBillCheck;

    private Call<Result> callBefore;
    private Call<Result> callAfter;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, CoinBuyActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_coin_buy;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.coin_buy), true);
        initGoodsCheck();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callBefore);
        RetrofitHelper.cancel(callAfter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_MORE_BILL);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btnAliPay, R.id.btnWeChatPay, R.id.tvBillCheck})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btnAliPay: // 支付宝支付
                payBefore(Bill.PAY_PLATFORM_ALI);
                break;
            case R.id.btnWeChatPay: // 微信支付
                payBefore(Bill.PAY_PLATFORM_WX);
                break;
            case R.id.tvBillCheck:// 检查
                checkPayResult();
                break;
        }
    }

    @SuppressLint("SetTextI18n")
    private void initGoodsCheck() {
        Limit limit = SPHelper.getLimit();
        String format = getString(R.string.pay_coin_goods_format);
        String goods1 = String.format(Locale.getDefault(), format, limit.getPayCoinGoods1Title(), limit.getPayCoinGoods1Count(), limit.getPayCoinGoods1Amount());
        String goods2 = String.format(Locale.getDefault(), format, limit.getPayCoinGoods2Title(), limit.getPayCoinGoods2Count(), limit.getPayCoinGoods2Amount());
        String goods3 = String.format(Locale.getDefault(), format, limit.getPayCoinGoods3Title(), limit.getPayCoinGoods3Count(), limit.getPayCoinGoods3Amount());
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

    public void payBefore(int payPlatform) {
        final int goods;
        if (rbGoods1.isChecked()) {
            goods = Bill.GOODS_COIN_1;
        } else if (rbGoods2.isChecked()) {
            goods = Bill.GOODS_COIN_2;
        } else if (rbGoods3.isChecked()) {
            goods = Bill.GOODS_COIN_3;
        } else {
            return;
        }
        callBefore = new RetrofitHelper().call(API.class).morePayBeforeGet(payPlatform, goods);
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(callBefore, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                OrderBefore orderBefore = data.getOrderBefore();
                if (orderBefore == null) return;
                int platform = orderBefore.getPlatform();
                if (platform == Bill.PAY_PLATFORM_ALI) {
                    String aliOrder = orderBefore.getAliOrder();
                    startAliPay(aliOrder);
                } else if (platform == Bill.PAY_PLATFORM_WX) {
                    WXOrder wxOrder = orderBefore.getWxOrder();
                    startWeChatPay(wxOrder);
                }
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    // 支付宝
    private void startAliPay(final String orderInfo) {
        PayHelper.payByAli(mActivity, orderInfo, new PayHelper.AliCallBack() {
            @Override
            public void onSuccess(PayAliResult result) {
                if (result == null) return;
                //String memo = result.getMemo();
                String resultStatus = result.getResultStatus();
                //PayAliResult.Result result1 = result.getResult();
                if (resultStatus.equals(String.valueOf(PayAliResult.RESULT_STATUS_OK))) {
                    checkPayResult();
                }
            }
        });
    }

    // 微信
    private void startWeChatPay(WXOrder wxOrder) {
        PayHelper.payByWX(mActivity, wxOrder, new PayHelper.WXCallBack() {
            @Override
            public void onSuccess(PayWxResult result) {
                if (result == null) return;
                if (result.getErrCode() == PayWxResult.CODE_OK) {
                    checkPayResult();
                }
            }
        });
    }

    private void checkPayResult() {
        callAfter = new RetrofitHelper().call(API.class).morePayAfterCheck();
        MaterialDialog loading = mActivity.getLoading(false);
        RetrofitHelper.enqueue(callAfter, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                RxBus.Event<Coin> event = new RxBus.Event<>(ConsHelper.EVENT_COIN_INFO_REFRESH, new Coin());
                RxBus.post(event);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
