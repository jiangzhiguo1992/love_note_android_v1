package com.jiangzg.mianmian.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.activity.user.UserInfoActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GImageAvatarView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CoupleInfoActivity extends BaseActivity<CoupleInfoActivity> {

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.abl)
    AppBarLayout abl;
    @BindView(R.id.tb)
    Toolbar tb;

    @BindView(R.id.ivAvatarLeft)
    GImageAvatarView ivAvatarLeft;
    @BindView(R.id.tvNameLeft)
    TextView tvNameLeft;
    @BindView(R.id.tvPhoneLeft)
    TextView tvPhoneLeft;
    @BindView(R.id.llUserInfoLeft)
    LinearLayout llUserInfoLeft;
    @BindView(R.id.ivSexLeft)
    ImageView ivSexLeft;
    @BindView(R.id.tvBirthLeft)
    TextView tvBirthLeft;

    @BindView(R.id.ivAvatarRight)
    GImageAvatarView ivAvatarRight;
    @BindView(R.id.tvNameRight)
    TextView tvNameRight;
    @BindView(R.id.tvPhoneRight)
    TextView tvPhoneRight;
    @BindView(R.id.llUserInfoRight)
    LinearLayout llUserInfoRight;
    @BindView(R.id.ivSexRight)
    ImageView ivSexRight;
    @BindView(R.id.tvBirthRight)
    TextView tvBirthRight;

    @BindView(R.id.tvBreakAbout)
    TextView tvBreakAbout;

    private Call<Result> callUpdateInfo;
    private Call<Result> callUpdateStatus;
    private File cameraFile;
    private File cropFile;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, CoupleInfoActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        BarUtils.setStatusBarTrans(mActivity, true);
        BarUtils.setNavigationBarTrans(mActivity, true);
        return R.layout.activity_couple_info;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.couple_info), true);
        // 沉浸式状态栏
        abl.setBackgroundColor(Color.TRANSPARENT);
        int statusBarHeight = BarUtils.getStatusBarHeight(mActivity);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) abl.getLayoutParams();
        layoutParams.topMargin += statusBarHeight;
        abl.setLayoutParams(layoutParams);
        abl.setTargetElevation(0);
    }

    @Override
    protected void initData(Bundle state) {
        setViewData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callUpdateInfo);
        RetrofitHelper.cancel(callUpdateStatus);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            ResHelper.deleteFileInBackground(cameraFile);
            ResHelper.deleteFileInBackground(cropFile);
            return;
        }
        if (requestCode == ConsHelper.REQUEST_CAMERA) {
            // 拍照
            if (FileUtils.isFileEmpty(cameraFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                ResHelper.deleteFileInBackground(cameraFile);
                return;
            }
            goCropActivity(cameraFile);
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = IntentResult.getPictureFile(data);
            if (FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            goCropActivity(pictureFile);
        } else if (requestCode == ConsHelper.REQUEST_CROP) {
            // 裁剪
            ossUploadAvatar();
            ResHelper.deleteFileInBackground(cameraFile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_COUPLE_INFO);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.ivAvatarLeft, R.id.tvNameLeft, R.id.tvPhoneLeft, R.id.llUserInfoRight, R.id.tvBreakAbout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivAvatarLeft: // 修改ta的头像
                showAvatarSelect();
                break;
            case R.id.tvNameLeft: // 修改ta的昵称
                showNameInput();
                break;
            case R.id.tvPhoneLeft: // 拨打ta的电话
                showDial();
                break;
            case R.id.llUserInfoRight: // 修改我的信息
                goUserInfo();
                break;
            case R.id.tvBreakAbout:
                breakAbout();
                break;
        }
    }

    private void setViewData() {
        // data
        User me = SPHelper.getUser();
        User ta = SPHelper.getTa();
        Couple couple = me.getCouple();
        String myName = me.getMyNameInCp();
        String taName = me.getTaNameInCp();
        String myAvatar = me.getMyAvatarInCp();
        String taAvatar = me.getTaAvatarInCp();
        String mePhone = me.getPhone();
        String taPhone = ta == null ? "" : ta.getPhone();
        int meSexRes = me.getSexResCircleSmall();
        int taSexRes = ta == null ? 0 : ta.getSexResCircleSmall();
        long meBirth = ConvertHelper.getJavaTimeByGo(me.getBirthday());
        String meBirthShow = DateUtils.getString(meBirth, ConstantUtils.FORMAT_POINT_Y_M_D);
        String taBirthShow = "";
        if (ta != null && ta.getBirthday() != 0) {
            long taBirth = ConvertHelper.getJavaTimeByGo(ta.getBirthday());
            taBirthShow = DateUtils.getString(taBirth, ConstantUtils.FORMAT_POINT_Y_M_D);
        }
        boolean breaking = Couple.isBreaking(couple);

        // view
        ivAvatarLeft.setData(taAvatar);
        ivAvatarRight.setData(myAvatar);
        tvNameLeft.setText(taName);
        tvNameRight.setText(myName);
        tvPhoneLeft.setText(taPhone);
        tvPhoneRight.setText(mePhone);
        ivSexLeft.setImageResource(taSexRes);
        ivSexRight.setImageResource(meSexRes);
        tvBirthLeft.setText(taBirthShow);
        tvBirthRight.setText(meBirthShow);
        if (breaking && couple.getState().getUserId() == me.getId()) {
            tvBreakAbout.setText(R.string.i_regret_i_want_complex);
        } else {
            tvBreakAbout.setText(R.string.i_want_break_pair_relation);
        }
    }

    private void showAvatarSelect() {
        cameraFile = ResHelper.newImageOutCacheFile();
        PopupWindow popupWindow = ViewHelper.createPictureCameraPop(mActivity, cameraFile);
        PopUtils.show(popupWindow, root, Gravity.CENTER);
    }

    private void goCropActivity(File source) {
        cropFile = ResHelper.newImageOutCacheFile();
        Intent intent = IntentFactory.getCrop(source, cropFile, 1, 1);
        ActivityTrans.startResult(mActivity, intent, ConsHelper.REQUEST_CROP);
    }

    // 修改名称对话框
    private void showNameInput() {
        String show = SPHelper.getUser().getTaNameInCp().trim();
        String hint = getString(R.string.please_input_nickname);
        int coupleNameLength = SPHelper.getLimit().getCoupleNameLength();
        MaterialDialog dialogName = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.modify_ta_name)
                .input(hint, show, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        LogUtils.i(LOG_TAG, "showNameInput: onInput: " + input.toString());
                    }
                })
                .inputRange(1, coupleNameLength)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // api
                        EditText editText = dialog.getInputEditText();
                        if (editText != null) {
                            String modifyName = editText.getText().toString();
                            apiCoupleInfo("", modifyName);
                        }
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialogName);
    }

    // oss上传头像
    private void ossUploadAvatar() {
        OssHelper.uploadAvatar(mActivity, cropFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                apiCoupleInfo(ossPath, "");
                ResHelper.deleteFileInBackground(cropFile);
            }

            @Override
            public void failure(File source, String errMsg) {
                ResHelper.deleteFileInBackground(cropFile);
            }
        });
    }

    // api 修改couple
    private void apiCoupleInfo(String avatar, String name) {
        MaterialDialog loading = getLoading(true);
        User body = ApiHelper.getCoupleUpdateInfo(avatar, name);
        // api
        callUpdateInfo = new RetrofitHelper().call(API.class).coupleUpdate(body);
        RetrofitHelper.enqueue(callUpdateInfo, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                Couple couple = data.getCouple();
                SPHelper.setCouple(couple);
                RxEvent<Couple> event = new RxEvent<>(ConsHelper.EVENT_COUPLE_REFRESH, couple);
                RxBus.post(event);
                setViewData();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    // 拨打电话
    private void showDial() {
        User ta = SPHelper.getTa();
        if (ta != null) {
            String phone = ta.getPhone().trim();
            Intent dial = IntentFactory.getDial(phone);
            ActivityTrans.start(mActivity, dial);
        }
    }

    // 用户信息
    private void goUserInfo() {
        // 也就改一次，所以直接修改完之后就跳转home吧
        if (User.canUserInfo()) {
            UserInfoActivity.goActivity(mActivity);
        }
    }

    // 分手/复合
    private void breakAbout() {
        User me = SPHelper.getUser();
        Couple couple = me.getCouple();
        if (Couple.isBreaking(couple) && couple.getState().getUserId() == me.getId()) {
            // 要复合
            User body = ApiHelper.getCoupleUpdate2GoodBody(couple.getId());
            coupleStatus(body);
        } else {
            // 要分手
            showBreakDialog(couple);
        }
    }

    private void showBreakDialog(final Couple couple) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.u_confirm_break)
                .content(R.string.impulse_is_devil_3)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        User body = ApiHelper.getCoupleUpdate2BadBody(couple.getId());
                        coupleStatus(body);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    // 分手
    private void coupleStatus(User body) {
        MaterialDialog loading = getLoading(true);
        // api
        callUpdateStatus = new RetrofitHelper().call(API.class).coupleUpdate(body);
        RetrofitHelper.enqueue(callUpdateStatus, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                Couple couple = data.getCouple();
                SPHelper.setCouple(couple);
                RxEvent<Couple> event = new RxEvent<>(ConsHelper.EVENT_COUPLE_REFRESH, couple);
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
