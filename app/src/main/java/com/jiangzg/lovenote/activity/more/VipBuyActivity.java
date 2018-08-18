package com.jiangzg.lovenote.activity.more;

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
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.AliPayResult;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.PayHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class VipBuyActivity extends BaseActivity<VipBuyActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnPay)
    Button btnPay;
    @BindView(R.id.rgGoods)
    RadioGroup rgGoods;
    @BindView(R.id.rbGoods1)
    RadioButton rbGoods1;
    @BindView(R.id.rbGoods2)
    RadioButton rbGoods2;
    @BindView(R.id.rbGoods3)
    RadioButton rbGoods3;

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
        initPayPlatformCheck();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

    @OnClick({R.id.btnPay})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btnPay: // 支付
                getOrderInfo();
                break;
        }
    }

    private void initGoodsCheck() {
        rgGoods.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbGoods1: // 一月

                        break;
                    case R.id.rbGoods2: // 一年

                        break;
                    case R.id.rbGoods3: // 永久

                        break;
                }
            }
        });
        rbGoods1.setChecked(true);
        refreshPayBtn();
    }

    private void initPayPlatformCheck() {

    }

    private void refreshPayBtn() {
        if (!rbGoods1.isChecked() && !rbGoods2.isChecked() && !rbGoods3.isChecked()) {
            btnPay.setEnabled(false);
        }
        btnPay.setEnabled(true);
    }

    public void getOrderInfo() {
        int goods, payPlatform;
        if (rbGoods1.isChecked()) {
            goods = ApiHelper.VIP_GOODS_1_MONTH;
        } else if (rbGoods2.isChecked()) {
            goods = ApiHelper.VIP_GOODS_1_YEAR;
        } else if (rbGoods3.isChecked()) {
            goods = ApiHelper.VIP_GOODS_FOREVER;
        } else {
            return;
        }
        payPlatform = ApiHelper.PAY_PLATFORM_ALIPAY;
        Call<Result> call = new RetrofitHelper().call(API.class).morePayAliOrderInfoGet(payPlatform, goods);
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                String orderInfo = data.getOrderInfo();
                startPay(orderInfo);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {

            }
        });
    }

    private void startPay(final String orderInfo) {
        PermUtils.requestPermissions(mActivity, ConsHelper.REQUEST_DEVICE_INFO, PermUtils.deviceInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                PayHelper.payByAli(mActivity, orderInfo, new PayHelper.AliCallBack() {
                    @Override
                    public void onSuccess(AliPayResult result) {
                        String memo = result.getMemo();
                        String resultStatus = result.getResultStatus();
                        AliPayResult.Result result1 = result.getResult();

                    }
                });
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {

            }
        });
    }

}
