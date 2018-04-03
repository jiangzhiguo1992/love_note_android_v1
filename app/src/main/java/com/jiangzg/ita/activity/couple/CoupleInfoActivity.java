package com.jiangzg.ita.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alibaba.sdk.android.oss.ClientConfiguration;
import com.alibaba.sdk.android.oss.OSS;
import com.alibaba.sdk.android.oss.OSSClient;
import com.alibaba.sdk.android.oss.common.auth.OSSCredentialProvider;
import com.alibaba.sdk.android.oss.common.auth.OSSStsTokenCredentialProvider;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentSend;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.helper.RxBus;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CoupleInfoActivity extends BaseActivity<CoupleInfoActivity> {

    @BindView(R.id.tb)
    Toolbar tb;

    @BindView(R.id.ivAvatar)
    ImageView ivAvatar;
    @BindView(R.id.btnAlbum)
    Button btnAlbum;
    @BindView(R.id.btnBreak)
    Button btnBreak;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, CoupleInfoActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_couple_info;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.couple_info), true);

    }

    @Override
    protected void initData(Bundle state) {

    }

    @OnClick({R.id.ivAvatar, R.id.btnAlbum, R.id.btnBreak})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivAvatar: //选取
                Intent picture = IntentSend.getPicture();
                ActivityTrans.startResult(mActivity, picture, ConsHelper.REQUEST_PICTURE);
                break;
            case R.id.btnAlbum: // 上传
                break;
            case R.id.btnBreak: // 分手
                coupleBreak();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == ConsHelper.REQUEST_PICTURE) {
            //Bitmap bitmap = IntentResult.getPictureBitmap(data);
            //ivAvatar.setImageBitmap(bitmap);
            //ImageConvert.save();
        }
    }

    // 分手
    private void coupleBreak() {
        MaterialDialog loading = getLoading(true);
        long cid = SPHelper.getCouple().getId();
        User body = ApiHelper.getCoupleUpdate2BadBody(cid);
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).coupleUpdate(body);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                Couple couple = data.getCouple();
                SPHelper.setCouple(couple);
                RxEvent<Couple> event = new RxEvent<>(ConsHelper.EVENT_COUPLE, couple);
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure() {
            }
        });
    }

}
