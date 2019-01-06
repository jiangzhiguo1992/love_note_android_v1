package com.jiangzg.lovenote.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.system.PayHelper;
import com.jiangzg.lovenote.model.engine.PayWxResult;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wx_pay_entry);
        // 还得注册一遍
        api = WXAPIFactory.createWXAPI(this, PayHelper.getWXAppId());
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

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
            RxBus.post(new RxBus.Event<>(RxBus.EVENT_PAY_WX_RESULT, result));
            finish();
        }
    }

}
