package com.jiangzg.ita.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.component.IntentSend;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.user.UserInfoActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.CheckHelper;
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

    private boolean isCreator;
    private String modifyName;
    private MaterialDialog dialogName;

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
        User user = SPHelper.getUser();
        Couple couple = user.getCouple();
        isCreator = user.isCoupleCreator();
        // meData
        String phone = user.getPhone();
        long birth = user.getBirthday() * 1000;
        String birthShow = DateUtils.getString(birth, ConstantUtils.FORMAT_CHINA_Y_M_D);
        // coupleData
        String creatorAvatar = couple.getCreatorAvatar();
        String creatorName = couple.getCreatorName();
        String inviteeAvatar = couple.getInviteeAvatar();
        String inviteeName = couple.getInviteeName();
        // view
        ivAvatarLeft.setDataOss(creatorAvatar);
        tvNameLeft.setText(creatorName);
        tvPhoneLeft.setText(phone);
        tvBirthLeft.setText(birthShow);
        ivAvatarRight.setDataOss(inviteeAvatar);
        tvNameRight.setText(inviteeName);
        // ta
        getTaInfo();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ConsHelper.REQUEST_CAMERA) {
            // 拍照
            goCropActivity(cameraFile);
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            pictureFile = IntentResult.getPictureFile(data);
            goCropActivity(pictureFile);
        } else if (requestCode == ConsHelper.REQUEST_CROP) {
            // 裁剪
            deleteCameraAndPictureFile();
            // todo oss
            coupleAvatar();
        }
    }

    //@Override
    //protected void onDestroy() {
    //    super.onDestroy();
    //    cameraFile = null;
    //    pictureFile = null;
    //    cropFile = null;
    //}

    @OnClick({R.id.ivAvatarLeft, R.id.tvNameLeft, R.id.tvPhoneLeft, R.id.tvBirthLeft,
            R.id.ivAvatarRight, R.id.tvNameRight, R.id.tvPhoneRight, R.id.tvBirthRight,
            R.id.tvBreakAbout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivAvatarLeft:
                if (!isCreator) {
                    showAvatarSelect();
                }
                break;
            case R.id.ivAvatarRight:
                if (isCreator) {
                    showAvatarSelect();
                }
                break;
            case R.id.tvNameLeft:
                if (!isCreator) {
                    showNameInput();
                }
                break;
            case R.id.tvNameRight:
                if (isCreator) {
                    showNameInput();
                }
                break;
            case R.id.tvPhoneLeft:
                if (!isCreator) {
                    showDial();
                }
                break;
            case R.id.tvPhoneRight:
                if (isCreator) {
                    showDial();
                }
                break;
            case R.id.tvBirthLeft:
                if (isCreator) {
                    goUserInfo();
                }
                break;
            case R.id.tvBirthRight:
                if (!isCreator) {
                    goUserInfo();
                }
                break;
            case R.id.tvBreakAbout:
                breakAbout();
                break;
        }
    }

    private void getTaInfo() {
        // api获取ta
        Call<Result> call = new RetrofitHelper().call(API.class).userGet(true);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                User user = data.getUser();
                if (user == null) return;
                String phone = user.getPhone();
                long birth = user.getBirthday() * 1000;
                String birthShow = DateUtils.getString(birth, ConstantUtils.FORMAT_CHINA_Y_M_D);
                if (isCreator) {
                    tvPhoneRight.setText(phone);
                    tvBirthRight.setText(birthShow);
                } else {
                    tvPhoneLeft.setText(phone);
                    tvBirthLeft.setText(birthShow);
                }
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private void goCropActivity(File source) {
        if (source != null) {
            cropFile = ResHelper.createJPEGInCache();
            Intent intent = IntentSend.getCrop(source, cropFile, 1, 1);
            ActivityTrans.startResult(mActivity, intent, ConsHelper.REQUEST_CROP);
        } else {
            ToastUtils.show(getString(R.string.picture_get_fail));
            LogUtils.w(LOG_TAG, "IntentResult.getPictureFile: fail");
        }
    }

    private void showAvatarSelect() {
        cameraFile = ResHelper.createJPEGInCache();
        PopupWindow popupWindow = PopHelper.createBookAlbumCamera(mActivity, cameraFile);
        PopUtils.show(popupWindow, root);
    }

    private void deleteCameraAndPictureFile() {
        MyApp.get().getThread().execute(new Runnable() {
            @Override
            public void run() {
                FileUtils.deleteFile(cameraFile);
                FileUtils.deleteFile(pictureFile);
                cameraFile = null;
                pictureFile = null;
            }
        });
    }

    // 修改名字
    private void coupleAvatar() {
        // todo api
        //ApiHelper.getCoupleUpdateInfo(,)
        //Call<Result> call = new RetrofitHelper().call(API.class).coupleUpdate(body);
        if (isCreator) {
            ivAvatarRight.setDataFile(cropFile);
        } else {
            ivAvatarLeft.setDataFile(cropFile);
        }
    }

    // 修改名称
    private void showNameInput() {
        String show = isCreator ? tvNameRight.getText().toString().trim() : tvNameLeft.getText().toString().trim();
        String hint = getString(R.string.please_input_nickname);
        if (dialogName == null) {
            dialogName = new MaterialDialog.Builder(mActivity)
                    .input(hint, show, false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            modifyName = input.toString().trim();
                        }
                    })
                    .positiveText(R.string.confirm_no_wrong)
                    .negativeText(R.string.i_think_again)
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            coupleName();
                        }
                    })
                    .build();
        }
        dialogName.show();
    }

    // 拨打电话
    private void showDial() {
        String phone = isCreator ? tvPhoneRight.getText().toString().trim() : tvPhoneLeft.getText().toString().trim();
        Intent dial = IntentSend.getDial(phone);
        ActivityTrans.start(mActivity, dial);
    }

    // 用户信息
    private void goUserInfo() {
        if (CheckHelper.canUserInfo()) {
            UserInfoActivity.goActivity(mActivity);
        }
    }

    // 修改名字
    private void coupleName() {
        // todo api
        if (isCreator) {
            tvNameRight.setText(modifyName);
        } else {
            tvNameLeft.setText(modifyName);
        }
    }

    // 分手/复合
    private void breakAbout() {
        // todo 是分手还是复合
        // coupleBreak();
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
