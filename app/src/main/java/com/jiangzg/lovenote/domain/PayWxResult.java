package com.jiangzg.lovenote.domain;

/**
 * Created by JZG on 2018/11/3.
 */
public class PayWxResult {

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
