package com.jiangzg.mianmian.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.Toolbar;
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
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.HelpActivity;
import com.jiangzg.mianmian.activity.user.UserInfoActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.CheckHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.OssHelper;
import com.jiangzg.mianmian.helper.PopHelper;
import com.jiangzg.mianmian.helper.ResHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GImageView;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

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
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;

    @BindView(R.id.ivAvatarLeft)
    GImageView ivAvatarLeft;
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
    GImageView ivAvatarRight;
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

    private boolean isCreator;
    private User me;
    private User ta;

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
        // 我的数据和身份
        me = SPHelper.getUser();
        isCreator = me.isCoupleCreator();
        // 沉浸式状态栏
        abl.setBackgroundColor(Color.TRANSPARENT);
        int statusBarHeight = BarUtils.getStatusBarHeight(mActivity);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) abl.getLayoutParams();
        layoutParams.topMargin += statusBarHeight;
        abl.setLayoutParams(layoutParams);
        abl.setTargetElevation(0);
        // srl
        srl.setEnabled(false);
    }

    @Override
    protected void initData(Bundle state) {
        getTaInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
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
                ResHelper.deleteFileInBackground(cameraFile);
                return;
            }
            goCropActivity(cameraFile);
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = IntentResult.getPictureFile(data);
            if (FileUtils.isFileEmpty(pictureFile)) {
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
                HelpActivity.goActivity(mActivity, Help.TYPE_COUPLE_INFO);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.ivAvatarLeft, R.id.tvNameLeft, R.id.tvPhoneLeft, R.id.llUserInfoLeft,
            R.id.ivAvatarRight, R.id.tvNameRight, R.id.tvPhoneRight, R.id.llUserInfoRight,
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
            case R.id.llUserInfoLeft:
                if (isCreator) {
                    goUserInfo();
                }
                break;
            case R.id.llUserInfoRight:
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
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // api获取ta
        Call<Result> call = new RetrofitHelper().call(API.class).userGet(true);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                ta = data.getUser();
                setViewData();
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
            }
        });
    }

    private void setViewData() {
        // meData
        String mePhone = me.getPhone();
        int meSexRes = me.getSexResCircleSmall();
        long meBirth = ConvertHelper.getJavaTimeByGo(me.getBirthday());
        String meBirthShow = DateUtils.getString(meBirth, ConstantUtils.FORMAT_POINT_Y_M_D);
        // taData
        String taPhone = "";
        if (ta != null) {
            taPhone = ta.getPhone();
        }
        int taSexRes = 0;
        if (ta != null) {
            taSexRes = ta.getSexResCircleSmall();
        }
        String tabBirthShow = "";
        if (ta != null && ta.getBirthday() != 0) {
            long taBirth = ConvertHelper.getJavaTimeByGo(ta.getBirthday());
            tabBirthShow = DateUtils.getString(taBirth, ConstantUtils.FORMAT_POINT_Y_M_D);
        }
        // coupleData
        Couple couple = SPHelper.getCouple();
        String creatorAvatar = couple.getCreatorAvatar();
        String creatorName = couple.getCreatorName();
        String inviteeAvatar = couple.getInviteeAvatar();
        String inviteeName = couple.getInviteeName();
        boolean breaking = CheckHelper.isCoupleBreaking(couple);
        // view
        ivAvatarLeft.setDataOss(creatorAvatar);
        ivAvatarRight.setDataOss(inviteeAvatar);
        tvNameLeft.setText(creatorName);
        tvNameRight.setText(inviteeName);
        if (isCreator) {
            tvPhoneLeft.setText(mePhone);
            tvPhoneRight.setText(taPhone);
            if (meSexRes != 0) {
                ivSexLeft.setImageResource(meSexRes);
            }
            if (taSexRes != 0) {
                ivSexRight.setImageResource(taSexRes);
            }
            tvBirthLeft.setText(meBirthShow);
            tvBirthRight.setText(tabBirthShow);
        } else {
            tvPhoneLeft.setText(taPhone);
            tvPhoneRight.setText(mePhone);
            if (taSexRes != 0) {
                ivSexLeft.setImageResource(taSexRes);
            }
            if (meSexRes != 0) {
                ivSexRight.setImageResource(meSexRes);
            }
            tvBirthLeft.setText(tabBirthShow);
            tvBirthRight.setText(meBirthShow);
        }
        if (breaking) {
            tvBreakAbout.setText(R.string.i_regret_i_want_complex);
        } else {
            tvBreakAbout.setText(R.string.i_want_break_pair_relation);
        }
    }

    private void showAvatarSelect() {
        cameraFile = ResHelper.newImageOutCache();
        PopupWindow popupWindow = PopHelper.createPictureCamera(mActivity, cameraFile);
        PopUtils.show(popupWindow, root);
    }

    private void goCropActivity(File source) {
        cropFile = ResHelper.newImageOutCache();
        Intent intent = IntentFactory.getCrop(source, cropFile, 1, 1);
        ActivityTrans.startResult(mActivity, intent, ConsHelper.REQUEST_CROP);
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

    // 修改名称对话框
    private void showNameInput() {
        String show = isCreator ? tvNameRight.getText().toString().trim() : tvNameLeft.getText().toString().trim();
        String hint = getString(R.string.please_input_nickname);
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

    // api 修改couple
    private void apiCoupleInfo(String avatar, String name) {
        MaterialDialog loading = getLoading(true);
        User body = ApiHelper.getCoupleUpdateInfo(avatar, name);
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).coupleUpdate(body);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
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
        String phone = isCreator ? tvPhoneRight.getText().toString().trim() : tvPhoneLeft.getText().toString().trim();
        Intent dial = IntentFactory.getDial(phone);
        ActivityTrans.start(mActivity, dial);
    }

    // 用户信息
    private void goUserInfo() {
        // 也就改一次，所以直接修改完之后就跳转home吧
        if (CheckHelper.canUserInfo()) {
            UserInfoActivity.goActivity(mActivity);
        }
    }

    // 分手/复合
    private void breakAbout() {
        Couple couple = SPHelper.getCouple();
        if (!CheckHelper.isCoupleBreaking(couple)) {
            // 要分手
            showBreakDialog(couple);
        } else {
            // 要复合
            User body = ApiHelper.getCoupleUpdate2GoodBody(couple.getId());
            coupleStatus(body);
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
        Call<Result> call = new RetrofitHelper().call(API.class).coupleUpdate(body);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
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
