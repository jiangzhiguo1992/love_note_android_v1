package com.jiangzg.ita.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.component.IntentSend;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.PopHelper;
import com.jiangzg.ita.helper.ResHelper;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.helper.RxBus;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CoupleInfoActivity extends BaseActivity<CoupleInfoActivity> {

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.ivAvatarLeft)
    GImageView ivAvatarLeft;
    @BindView(R.id.tvNameLeft)
    TextView tvNameLeft;
    @BindView(R.id.tvPhoneLeft)
    TextView tvPhoneLeft;
    @BindView(R.id.tvBirthLeft)
    TextView tvBirthLeft;
    @BindView(R.id.ivAvatarRight)
    GImageView ivAvatarRight;
    @BindView(R.id.tvNameRight)
    TextView tvNameRight;
    @BindView(R.id.tvPhoneRight)
    TextView tvPhoneRight;
    @BindView(R.id.tvBirthRight)
    TextView tvBirthRight;
    @BindView(R.id.tvBreakAbout)
    TextView tvBreakAbout;

    private File cameraFile;
    private File pictureFile;
    private File cropFile;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ConsHelper.REQUEST_CAMERA) {
            // 拍照
            //IntentSend.getCrop();
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            cropFile = ResHelper.createJPEGInCache();
            pictureFile = IntentResult.getPictureFile(data);
            if (pictureFile != null) {
                Intent intent = IntentSend.getCrop(pictureFile, cropFile, 1, 1);
                ActivityTrans.startResult(mActivity, intent, ConsHelper.REQUEST_CROP);
            } else {
                ToastUtils.show(getString(R.string.picture_get_fail));
                LogUtils.w(LOG_TAG, "IntentResult.getPictureFile: fail");
            }
        } else if (requestCode == ConsHelper.REQUEST_CROP) {
            // 裁剪
            deleteCameraAndPictureFile();
            ivAvatarLeft.setDataFile(cropFile);
        } else if (requestCode == ConsHelper.REQUEST_BOOK_PICTURE) {
            // 小本本

        }
    }

    @OnClick({R.id.ivAvatarLeft, R.id.tvNameLeft, R.id.tvPhoneLeft, R.id.tvBirthLeft, R.id.ivAvatarRight, R.id.tvNameRight, R.id.tvPhoneRight, R.id.tvBirthRight, R.id.tvBreakAbout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivAvatarLeft:
                // todo 标识
                showImgSelect();
                break;
            case R.id.ivAvatarRight:
                // todo 标识
                showImgSelect();
                break;
            case R.id.tvNameLeft:
                break;
            case R.id.tvNameRight:
                break;
            case R.id.tvPhoneLeft:
                break;
            case R.id.tvPhoneRight:
                break;
            case R.id.tvBirthLeft:
                break;
            case R.id.tvBirthRight:
                break;
            case R.id.tvBreakAbout:
                break;
        }
    }

    private void showImgSelect() {
        PopupWindow popupWindow = PopHelper.createBookAlbumCamera(mActivity, cameraFile);
        PopUtils.show(popupWindow, root);
    }

    private void deleteCameraAndPictureFile() {
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                FileUtils.deleteFile(cameraFile);
                FileUtils.deleteFile(pictureFile);
            }
        });
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
