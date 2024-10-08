package com.jiangzg.lovenote.controller.activity.more;

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

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.PayHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.engine.PayAliResult;
import com.jiangzg.lovenote.model.engine.PayWxResult;
import com.jiangzg.lovenote.model.entity.Bill;
import com.jiangzg.lovenote.model.entity.Limit;
import com.jiangzg.lovenote.model.entity.OrderBefore;
import com.jiangzg.lovenote.model.entity.Vip;
import com.jiangzg.lovenote.model.entity.WXOrder;

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
    @BindView(R.id.tvBillCheck)
    TextView tvBillCheck;

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

    public void payBefore(int payPlatform) {
        int goods = Bill.GOODS_VIP_1;
        if (rbGoods2.isChecked()) {
            goods = Bill.GOODS_VIP_2;
        } else if (rbGoods3.isChecked()) {
            goods = Bill.GOODS_VIP_3;
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).morePayBeforeGet(payPlatform, goods);
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
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
        pushApi(api);
    }

    // 支付宝
    private void startAliPay(final String orderInfo) {
        PayHelper.payByAli(mActivity, orderInfo, result -> {
            if (result == null) return;
            //String memo = result.getMemo();
            String resultStatus = result.getResultStatus();
            //PayAliResult.Result result1 = result.getResult();
            if (resultStatus.equals(String.valueOf(PayAliResult.RESULT_STATUS_OK))) {
                checkPayResult();
            }
        });
    }

    // 微信
    private void startWeChatPay(WXOrder wxOrder) {
        PayHelper.payByWX(mActivity, wxOrder, result -> {
            if (result == null) return;
            if (result.getErrCode() == PayWxResult.CODE_OK) {
                checkPayResult();
            }
        });
    }

    private void checkPayResult() {
        Call<Result> api = new RetrofitHelper().call(API.class).morePayAfterCheck();
        RetrofitHelper.enqueue(api, mActivity.getLoading(false), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_VIP_INFO_REFRESH, new Vip()));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
