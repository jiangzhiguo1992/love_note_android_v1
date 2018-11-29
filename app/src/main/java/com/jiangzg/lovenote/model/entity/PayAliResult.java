package com.jiangzg.lovenote.model.entity;

/**
 * Created by JZG on 2018/8/17.
 * PayAliResult
 */
public class PayAliResult {

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
