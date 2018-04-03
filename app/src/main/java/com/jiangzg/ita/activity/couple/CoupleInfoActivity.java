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
import com.jiangzg.ita.third.API;
import com.jiangzg.ita.third.RetrofitHelper;
import com.jiangzg.ita.third.RxBus;

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


    private void push() {
        String endpoint = "http://itaoss.jiangzhiguo.com";
        // 明文设置secret的方式建议只在测试时使用，更多鉴权模式请参考访问控制章节
        // 也可查看sample 中 sts 使用方式了解更多(https://github.com/aliyun/aliyun-oss-android-sdk/tree/master/app/src/main/java/com/alibaba/sdk/android/oss/app)
        OSSCredentialProvider credentialProvider = new OSSStsTokenCredentialProvider("LTAIDPG7c8KCSC4S", "<StsToken.SecretKeyId>", "BcgE3j6MooLIvlqkc91IKuTnFYwiT2");
        // 该配置类如果不设置，会有默认配置，具体可看该类
        ClientConfiguration conf = new ClientConfiguration();
        conf.setConnectionTimeout(15 * 1000); // 连接超时，默认15秒
        conf.setSocketTimeout(15 * 1000); // socket超时，默认15秒
        conf.setMaxConcurrentRequest(5); // 最大并发请求数，默认5个
        conf.setMaxErrorRetry(2); // 失败后最大重试次数，默认2次
        OSS oss = new OSSClient(getApplicationContext(), endpoint, credentialProvider, conf);


    }

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
