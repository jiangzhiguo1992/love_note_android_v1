package com.jiangzg.lovenote.helper;

import android.app.Activity;

import com.alipay.sdk.app.PayTask;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.WXOrder;
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

    public static class PayWxResult {

        public static final int CODE_OK = 0; // 成功
        public static final int CODE_ERR = -1; // 错误
        public static final int CODE_CANCEL = -2; // 用户取消

        private int respType;
        private int errCode;

        public int getRespType() {
            return respType;
        }

        public void setRespType(int respType) {
            this.respType = respType;
        }

        public int getErrCode() {
            return errCode;
        }

        public void setErrCode(int errCode) {
            this.errCode = errCode;
        }
    }

    public static class PayAliResult {

        public static final int RESPONSE_CODE_SUCCESS = 10000; // 接口调用成功
        public static final int RESPONSE_CODE_UNAVAILABLE = 20000; // 服务不可用
        public static final int RESPONSE_CODE_PERMISSION_NO = 20001; // 授权权限不足
        public static final int RESPONSE_CODE_PARAMS_NEED = 40001; // 缺少必选参数
        public static final int RESPONSE_CODE_PARAMS_ERROR = 40002; // 非法的参数
        public static final int RESPONSE_CODE_BUSINESS_ERROR = 40004; // 业务处理失败
        public static final int RESPONSE_CODE_NO_PERMISSION = 40006; // 权限不足

        public static final int RESULT_STATUS_OK = 9000; // 订单支付成功
        public static final int RESULT_STATUS_LOADING = 8000; // 正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
        public static final int RESULT_STATUS_UNKNOWN = 6004; // 支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
        public static final int RESULT_STATUS_CONNECT = 6002; // 网络连接出错
        public static final int RESULT_STATUS_CANCEL = 6001; // 用户中途取消
        public static final int RESULT_STATUS_REPEAT = 5000; // 重复请求
        public static final int RESULT_STATUS_FAIL = 4000; // 订单支付失败

        private String memo;
        private String resultStatus;
        private Result result;

        public String getMemo() {
            return memo;
        }

        public void setMemo(String memo) {
            this.memo = memo;
        }

        public Result getResult() {
            return result;
        }

        public void setResult(Result result) {
            this.result = result;
        }

        public String getResultStatus() {
            return resultStatus;
        }

        public void setResultStatus(String resultStatus) {
            this.resultStatus = resultStatus;
        }

        public static class Result {

            private Response alipay_trade_app_pay_response;
            private String sign;
            private String sign_type;

            public Response getAlipay_trade_app_pay_response() {
                return alipay_trade_app_pay_response;
            }

            public void setAlipay_trade_app_pay_response(Response alipay_trade_app_pay_response) {
                this.alipay_trade_app_pay_response = alipay_trade_app_pay_response;
            }

            public String getSign() {
                return sign;
            }

            public void setSign(String sign) {
                this.sign = sign;
            }

            public String getSign_type() {
                return sign_type;
            }

            public void setSign_type(String sign_type) {
                this.sign_type = sign_type;
            }
        }

        public static class Response {

            private String code;
            private String msg;
            private String app_id;
            private String out_trade_no;
            private String trade_no;
            private String total_amount;
            private String seller_id;
            private String charset;
            private String timestamp;

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getMsg() {
                return msg;
            }

            public void setMsg(String msg) {
                this.msg = msg;
            }

            public String getApp_id() {
                return app_id;
            }

            public void setApp_id(String app_id) {
                this.app_id = app_id;
            }

            public String getOut_trade_no() {
                return out_trade_no;
            }

            public void setOut_trade_no(String out_trade_no) {
                this.out_trade_no = out_trade_no;
            }

            public String getTrade_no() {
                return trade_no;
            }

            public void setTrade_no(String trade_no) {
                this.trade_no = trade_no;
            }

            public String getTotal_amount() {
                return total_amount;
            }

            public void setTotal_amount(String total_amount) {
                this.total_amount = total_amount;
            }

            public String getSeller_id() {
                return seller_id;
            }

            public void setSeller_id(String seller_id) {
                this.seller_id = seller_id;
            }

            public String getCharset() {
                return charset;
            }

            public void setCharset(String charset) {
                this.charset = charset;
            }

            public String getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }
        }
    }
}
