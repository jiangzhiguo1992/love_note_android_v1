package com.jiangzg.lovenote.helper;

import android.app.Activity;

import com.alipay.sdk.app.PayTask;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.domain.PayAliResult;
import com.jiangzg.lovenote.domain.PayWxResult;
import com.jiangzg.lovenote.domain.WXOrder;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.Map;

import rx.Observable;
import rx.functions.Action1;

/**
 * Created by JZG on 2018/8/17.
 * PayHelper
 */
public class PayHelper {

    private static Observable<PayWxResult> registerPayWX;
    public static String WX_APP_ID = "wx956330a3fd0ef37e";

    public interface AliCallBack {
        void onSuccess(PayAliResult result);
    }

    public interface WXCallBack {
        void onSuccess(PayWxResult result);
    }

    public static void payByAli(final Activity activity, final String orderInfo, final AliCallBack callBack) {
        PermUtils.requestPermissions(activity, ConsHelper.REQUEST_DEVICE_INFO, PermUtils.deviceInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                payByAliNoPermission(activity, orderInfo, callBack);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(activity);
            }
        });
    }

    private static void payByAliNoPermission(final Activity activity, final String orderInfo, final AliCallBack callBack) {
        if (StringUtils.isEmpty(orderInfo)) return;
        // 必须异步调用
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                // 开始调起支付宝app
                PayTask aliPay = new PayTask(activity);
                Map<String, String> result = aliPay.payV2(orderInfo, true);
                // 支付宝app返回
                if (result == null || result.isEmpty()) {
                    LogUtils.w(PayHelper.class, "payByAli", "result == null");
                    ToastUtils.show(activity.getString(R.string.pay_error));
                    return;
                }
                LogUtils.d(PayHelper.class, "payByAli", "result = " + result.toString());
                // aliPayResultResponse
                PayAliResult.Response aliPayResultResponse = new PayAliResult.Response();
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
                PayAliResult.Result aliPayResultResult = new PayAliResult.Result();
                aliPayResultResult.setSign(result.get("sign"));
                aliPayResultResult.setSign_type(result.get("sign_type"));
                aliPayResultResult.setAlipay_trade_app_pay_response(aliPayResultResponse);
                // payAliResult
                final PayAliResult payAliResult = new PayAliResult();
                payAliResult.setMemo(result.get("memo")); // 描述信息
                payAliResult.setResultStatus(result.get("resultStatus")); // 结果码
                payAliResult.setResult(aliPayResultResult); // 处理结果
                if (callBack != null) {
                    MyApp.get().getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.onSuccess(payAliResult);
                        }
                    });
                }
            }
        });
    }

    public static void payByWX(final Activity activity, final WXOrder order, final WXCallBack callBack) {
        if (order == null) return;
        WX_APP_ID = order.getAppId();
        // 商户APP工程中引入微信JAR包，调用API前，需要先向微信注册您的APPID
        final IWXAPI api = WXAPIFactory.createWXAPI(activity, null);
        // 将该app注册到微信
        api.registerApp(WX_APP_ID);
        // 先解除事件
        if (registerPayWX != null) {
            RxBus.unregister(ConsHelper.EVENT_PAY_WX_RESULT, registerPayWX);
        }
        // 再添加事件
        registerPayWX = RxBus.register(ConsHelper.EVENT_PAY_WX_RESULT, new Action1<PayWxResult>() {
            @Override
            public void call(PayWxResult result) {
                if (registerPayWX != null) {
                    // 别忘了解除事件
                    RxBus.unregister(ConsHelper.EVENT_PAY_WX_RESULT, registerPayWX);
                    registerPayWX = null;
                }
                if (result == null || result.getErrCode() == PayWxResult.CODE_ERR) {
                    // 异常
                    LogUtils.w(PayHelper.class, "payByWX", String.valueOf(result == null ? "???" : result.getErrCode()));
                    ToastUtils.show(activity.getString(R.string.pay_error));
                } else if (result.getErrCode() == PayWxResult.CODE_OK) {
                    // 正常
                    if (callBack != null) callBack.onSuccess(result);
                }
                // 取消 不处理
            }
        });
        // 开始调起微信app
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                PayReq request = new PayReq();
                request.appId = order.getAppId();
                request.partnerId = order.getPartnerId();
                request.prepayId = order.getPrepayId();
                request.packageValue = order.getPackageValue();
                request.nonceStr = order.getNonceStr();
                request.timeStamp = order.getTimeStamp();
                request.sign = order.getSign();
                api.sendReq(request);
            }
        });
    }

}
