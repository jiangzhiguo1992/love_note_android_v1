package com.jiangzg.lovenote.activity.couple;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.system.PermUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Help;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.RxEvent;
import com.jiangzg.lovenote.domain.User;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.OssHelper;
import com.jiangzg.lovenote.helper.ResHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.io.File;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CoupleInfoActivity extends BaseActivity<CoupleInfoActivity> {

    @BindView(R.id.abl)
    AppBarLayout abl;
    @BindView(R.id.tb)
    Toolbar tb;

    @BindView(R.id.ivAvatarLeft)
    FrescoAvatarView ivAvatarLeft;
    @BindView(R.id.tvNameLeft)
    TextView tvNameLeft;
    @BindView(R.id.llPhoneLeft)
    LinearLayout llPhoneLeft;
    @BindView(R.id.tvPhoneLeft)
    TextView tvPhoneLeft;
    @BindView(R.id.llUserInfoLeft)
    LinearLayout llUserInfoLeft;
    @BindView(R.id.ivSexLeft)
    ImageView ivSexLeft;
    @BindView(R.id.tvBirthLeft)
    TextView tvBirthLeft;

    @BindView(R.id.ivAvatarRight)
    FrescoAvatarView ivAvatarRight;
    @BindView(R.id.tvNameRight)
    TextView tvNameRight;
    @BindView(R.id.llPhoneRight)
    LinearLayout llPhoneRight;
    @BindView(R.id.tvPhoneRight)
    TextView tvPhoneRight;
    @BindView(R.id.llUserInfoRight)
    LinearLayout llUserInfoRight;
    @BindView(R.id.ivSexRight)
    ImageView ivSexRight;
    @BindView(R.id.tvBirthRight)
    TextView tvBirthRight;

    @BindView(R.id.tvPairDays)
    TextView tvPairDays;

    private Call<Result> callTaGet;
    private Call<Result> callUpdateInfo;
    private Call<Result> callUpdateStatus;
    private File cropFile;

    public static void goActivity(Fragment from) {
        if (Couple.isBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), CoupleInfoActivity.class);
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
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.pair_info), true);
        // 沉浸式状态栏
        abl.setBackgroundColor(Color.TRANSPARENT);
        int statusBarHeight = BarUtils.getStatusBarHeight(mActivity);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) abl.getLayoutParams();
        layoutParams.topMargin += statusBarHeight;
        abl.setLayoutParams(layoutParams);
        abl.setTargetElevation(0);
        // view
        setViewData();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // ta 对方可能更改手机，需要每次同步
        getTaData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callTaGet);
        RetrofitHelper.cancel(callUpdateInfo);
        RetrofitHelper.cancel(callUpdateStatus);
        // 创建成功的cropFile都要删除
        ResHelper.deleteFileInBackground(cropFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.break_help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        User me = SPHelper.getMe();
        Couple couple = me.getCouple();
        if (Couple.isBreaking(couple) && couple.getState().getUserId() == me.getId()) {
            getMenuInflater().inflate(R.menu.complex_help, menu);
        } else {
            getMenuInflater().inflate(R.menu.break_help, menu);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            ResHelper.deleteFileInBackground(cropFile);
            return;
        }
        if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = IntentResult.getPictureFile(data);
            if (FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            cropFile = ResHelper.newImageCacheFile();
            Intent intent = IntentFactory.getImageCrop(ResHelper.PROVIDER_AUTH, pictureFile, cropFile, 1, 1);
            ActivityTrans.startResult(mActivity, intent, ConsHelper.REQUEST_CROP);
        } else if (requestCode == ConsHelper.REQUEST_CROP) {
            // 裁剪
            ossUploadAvatar();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuBreak: // 解散
                showBreakDialog();
                return true;
            case R.id.menuComplex: // 复合
                coupleStatus(ApiHelper.COUPLE_UPDATE_GOOD);
                return true;
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_COUPLE_INFO);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.ivAvatarLeft, R.id.tvNameLeft, R.id.llPhoneLeft})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivAvatarLeft: // 修改ta的头像
                showAvatarSelect();
                break;
            case R.id.tvNameLeft: // 修改ta的昵称
                showNameInput();
                break;
            case R.id.llPhoneLeft: // 拨打ta的电话
                showDial();
                break;
        }
    }

    private void getTaData() {
        callTaGet = new RetrofitHelper().call(API.class).userGetTa();
        RetrofitHelper.enqueue(callTaGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                User ta = data.getUser();
                SPHelper.setTa(ta);
                setViewData();
                // menu
                mActivity.invalidateOptionsMenu();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void setViewData() {
        // data
        User me = SPHelper.getMe();
        User ta = SPHelper.getTa();
        String myName = me.getMyNameInCp();
        String taName = me.getTaNameInCp();
        String myAvatar = me.getMyAvatarInCp();
        String taAvatar = me.getTaAvatarInCp();
        String mePhone = me.getPhone();
        String taPhone = ta == null ? "" : ta.getPhone();
        int meSexRes = me.getSexResCircleSmall();
        int taSexRes = ta == null ? 0 : ta.getSexResCircleSmall();
        long meBirth = TimeHelper.getJavaTimeByGo(me.getBirthday());
        String meBirthShow = DateUtils.getString(meBirth, ConstantUtils.FORMAT_POINT_Y_M_D);
        String taBirthShow = "";
        if (ta != null && ta.getBirthday() != 0) {
            long taBirth = TimeHelper.getJavaTimeByGo(ta.getBirthday());
            taBirthShow = DateUtils.getString(taBirth, ConstantUtils.FORMAT_POINT_Y_M_D);
        }
        int togetherDay = SPHelper.getTogetherDay();
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
        tvPairDays.setText(String.format(Locale.getDefault(), getString(R.string.pair_holder_day), togetherDay));
    }

    private void showAvatarSelect() {
        PermUtils.requestPermissions(mActivity, ConsHelper.REQUEST_APP_INFO, PermUtils.appInfo, new PermUtils.OnPermissionListener() {
            @Override
            public void onPermissionGranted(int requestCode, String[] permissions) {
                Intent picture = IntentFactory.getPicture();
                ActivityTrans.startResult(mActivity, picture, ConsHelper.REQUEST_PICTURE);
            }

            @Override
            public void onPermissionDenied(int requestCode, String[] permissions) {
                DialogHelper.showGoPermDialog(mActivity);
            }
        });
    }

    // 修改名称对话框
    private void showNameInput() {
        String show = SPHelper.getMe().getTaNameInCp().trim();
        String hint = getString(R.string.please_input_nickname);
        int coupleNameLength = SPHelper.getLimit().getCoupleNameLength();
        MaterialDialog dialogName = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .autoDismiss(true)
                .title(R.string.modify_ta_name)
                .input(hint, show, false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        LogUtils.i(CoupleInfoActivity.class, "onInput", input.toString());
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
        Couple body = ApiHelper.getCoupleUpdateInfo(avatar, name);
        // api
        callUpdateInfo = new RetrofitHelper().call(API.class).coupleUpdate(ApiHelper.COUPLE_UPDATE_INFO, body);
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
            public void onFailure(int code, String message, Result.Data data) {
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

    private void showBreakDialog() {
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
                        coupleStatus(ApiHelper.COUPLE_UPDATE_BAD);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    // 分手
    private void coupleStatus(int type) {
        Couple couple = SPHelper.getCouple();
        MaterialDialog loading = getLoading(true);
        // api
        callUpdateStatus = new RetrofitHelper().call(API.class).coupleUpdate(type, couple);
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
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
