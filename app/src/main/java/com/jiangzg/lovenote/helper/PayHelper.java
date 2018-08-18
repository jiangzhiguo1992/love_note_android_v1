package com.jiangzg.lovenote.helper;

import android.app.Activity;

import com.alipay.sdk.app.PayTask;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.domain.AliPayResult;

import java.util.Map;

/**
 * Created by JZG on 2018/8/17.
 * PayHelper
 * TODO 微信
 */
public class PayHelper {

    public interface AliCallBack {
        void onSuccess(AliPayResult result);
    }

    public static void payByAli(final Activity activity, final String orderInfo, final AliCallBack callBack) {
        // 必须异步调用
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                PayTask aliPay = new PayTask(activity);
                Map<String, String> result = aliPay.payV2(orderInfo, true);
                if (result == null || result.isEmpty()) {
                    LogUtils.w(PayHelper.class, "payByAli", "result == null");
                    // TODO
                    return;
                }
                LogUtils.d(PayHelper.class, "payByAli", "result = " + result.toString());
                // aliPayResultResponse
                AliPayResult.Response aliPayResultResponse = new AliPayResult.Response();
                aliPayResultResponse.setCode(result.get("code")); // 结果码
                aliPayResultResponse.setMsg(result.get("msg")); // 处理结果的描述，信息来自于code返回结果的描述 success
                aliPayResultResponse.setApp_id(result.get("app_id")); // 支付宝分配给开发者的应用Id。
                aliPayResultResponse.setOut_trade_no(result.get("out_trade_no")); // 商户网站唯一订单号
                aliPayResultResponse.setTrade_no(result.get("trade_no")); // 该交易在支付宝系统中的交易流水号。最长64位。
                aliPayResultResponse.setTotal_amount(result.get("total_amount")); // 该笔订单的资金总额，单位为RMB-Yuan。取值范围为[0.01,100000000.00]，精确到小数点后两位。
                aliPayResultResponse.setSeller_id(result.get("seller_id")); // 收款支付宝账号对应的支付宝唯一用户号。以2088开头的纯16位数字
                aliPayResultResponse.setCharset(result.get("charset")); // 编码格式 utf-8
                aliPayResultResponse.setTimestamp(result.get("timestamp")); // 时间 2016-10-11 17:43:36
                // aliPayResultResult
                AliPayResult.Result aliPayResultResult = new AliPayResult.Result();
                aliPayResultResult.setSign(result.get("sign"));
                aliPayResultResult.setSign_type(result.get("sign_type"));
                aliPayResultResult.setAlipay_trade_app_pay_response(aliPayResultResponse);
                // aliPayResult
                final AliPayResult aliPayResult = new AliPayResult();
                aliPayResult.setMemo(result.get("memo")); // 描述信息
                aliPayResult.setResultStatus(result.get("resultStatus")); // 结果码
                aliPayResult.setResult(aliPayResultResult); // 处理结果
                if (callBack != null) {
                    MyApp.get().getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(aliPayResult);
                        }
                    });
                }
            }
        });
    }

}
