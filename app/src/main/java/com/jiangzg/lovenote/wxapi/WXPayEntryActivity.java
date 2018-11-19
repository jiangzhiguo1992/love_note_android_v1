package com.jiangzg.lovenote.wxapi;

import android.app.Activity;

import com.jiangzg.lovenote.domain.PayWxResult;
import com.jiangzg.lovenote.domain.RxEvent;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp resp) {
        // 支付回调
        PayWxResult result = new PayWxResult();
        result.setRespType(resp.getType());
        result.setErrCode(resp.errCode);
        if (result.getRespType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            RxEvent<PayWxResult> event = new RxEvent<>(ConsHelper.EVENT_PAY_WX_RESULT, result);
            RxBus.post(event);
            finish();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
