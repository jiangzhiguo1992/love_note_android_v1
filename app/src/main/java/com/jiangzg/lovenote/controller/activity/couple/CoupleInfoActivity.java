package com.jiangzg.lovenote.controller.activity.couple;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.view.BarUtils;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.BigImageActivity;
import com.jiangzg.lovenote.controller.activity.note.SouvenirListActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.activity.user.PhoneActivity;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.OssHelper;
import com.jiangzg.lovenote.helper.common.ResHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.media.PickHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.FrescoAvatarView;

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
    FrescoAvatarView ivAvatarLeft;
    @BindView(R.id.tvNameLeft)
    TextView tvNameLeft;
    @BindView(R.id.tvPhoneLeft)
    TextView tvPhoneLeft;
    @BindView(R.id.tvBirthLeft)
    TextView tvBirthLeft;

    @BindView(R.id.ivAvatarRight)
    FrescoAvatarView ivAvatarRight;
    @BindView(R.id.tvNameRight)
    TextView tvNameRight;
    @BindView(R.id.tvPhoneRight)
    TextView tvPhoneRight;
    @BindView(R.id.tvBirthRight)
    TextView tvBirthRight;

    @BindView(R.id.cvTogether)
    CardView cvTogether;
    @BindView(R.id.tvTogether)
    TextView tvTogether;
    @BindView(R.id.tvTogetherTimer)
    TextView tvTogetherTimer;

    private File cropFile;
    private Runnable togetherCountDownTask;

    public static void goActivity(Fragment from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), CoupleInfoActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
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
    }

    @Override
    protected void onFinish(Bundle state) {
        stopTogetherCountDownTask();
        // 创建成功的cropFile都要删除
        ResHelper.deleteFileInBackground(cropFile);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_break, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        User me = SPHelper.getMe();
        Couple couple = me == null ? null : me.getCouple();
        if (couple != null) {
            if (UserHelper.isCoupleBreaking(couple) && couple.getState().getUserId() == me.getId()) {
                getMenuInflater().inflate(R.menu.help_complex, menu);
            } else {
                getMenuInflater().inflate(R.menu.help_break, menu);
            }
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
        if (requestCode == BaseActivity.REQUEST_PICTURE) {
            // 相册
            File pictureFile = PickHelper.getResultFile(mActivity, data);
            if (FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            cropFile = ResHelper.newImageCacheFile();
            Intent intent = IntentFactory.getImageCrop(ResHelper.getFileProviderAuth(), pictureFile, cropFile, 1, 1);
            ActivityTrans.startResult(mActivity, intent, BaseActivity.REQUEST_CROP);
        } else if (requestCode == BaseActivity.REQUEST_CROP) {
            // 裁剪
            ossUploadAvatar();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_COUPLE_INFO);
                return true;
            case R.id.menuBreak: // 解散
                showBreakDialog();
                return true;
            case R.id.menuComplex: // 复合
                coupleStatus(ApiHelper.COUPLE_UPDATE_GOOD);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.ivAvatarLeft, R.id.ivAvatarRight, R.id.tvNameLeft, R.id.tvPhoneLeft, R.id.tvPhoneRight,
            R.id.tvBirthLeft, R.id.tvBirthRight, R.id.cvTogether})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivAvatarLeft: // 左头像
                showLeftAvatarPop();
                break;
            case R.id.ivAvatarRight: // 右头像
                String myAvatar = UserHelper.getMyAvatar(SPHelper.getMe());
                BigImageActivity.goActivityByOss(mActivity, myAvatar, ivAvatarRight);
                break;
            case R.id.tvNameLeft: // 左昵称
                showNameInput();
                break;
            case R.id.tvPhoneLeft: // 左电话
                showDial();
                break;
            case R.id.tvPhoneRight: // 右电话
                PhoneActivity.goActivity(mActivity);
                break;
            case R.id.tvBirthLeft: // ta的生日
                break;
            case R.id.tvBirthRight: // 我的生日
                break;
            case R.id.cvTogether: // 在一起
                showTogetherTimePicker();
                break;
        }
    }

    private void setViewData() {
        // data
        User me = SPHelper.getMe();
        User ta = SPHelper.getTa();
        Couple couple = me == null ? null : me.getCouple();
        String myName = UserHelper.getMyName(me);
        String taName = UserHelper.getTaName(me);
        String myAvatar = UserHelper.getMyAvatar(me);
        String taAvatar = UserHelper.getTaAvatar(me);
        String myPhone = me == null ? "" : me.getPhone();
        String taPhone = ta == null ? "" : ta.getPhone();
        long myBirth = TimeHelper.getJavaTimeByGo(me == null ? 0 : me.getBirthday());
        long taBirth = TimeHelper.getJavaTimeByGo(ta == null ? 0 : ta.getBirthday());
        String myBirthShow = DateUtils.getStr(myBirth, DateUtils.FORMAT_POINT_Y_M_D);
        String taBirthShow = DateUtils.getStr(taBirth, DateUtils.FORMAT_POINT_Y_M_D);
        String togetherDay = String.valueOf(UserHelper.getCoupleTogetherDay(couple));
        // view
        ivAvatarLeft.setData(taAvatar, ta);
        ivAvatarRight.setData(myAvatar, me);
        tvNameLeft.setText(taName);
        tvNameRight.setText(myName);
        tvPhoneLeft.setText(taPhone);
        tvPhoneRight.setText(myPhone);
        tvBirthLeft.setText(taBirthShow);
        tvBirthRight.setText(myBirthShow);
        tvTogether.setText(togetherDay);
        // countdown
        MyApp.get().getHandler().post(getTogetherCountDownTask());
    }

    @SuppressLint("InflateParams")
    private void showLeftAvatarPop() {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.pop_img_show_select, null);
        final PopupWindow pop = PopUtils.createWindow(view);
        View.OnClickListener listener = v -> {
            switch (v.getId()) {
                case R.id.root:
                    PopUtils.dismiss(pop);
                    break;
                case R.id.llBigImage:
                    PopUtils.dismiss(pop);
                    BigImageActivity.goActivityByOss(mActivity, UserHelper.getTaAvatar(SPHelper.getMe()), ivAvatarLeft);
                    break;
                case R.id.llPicture:
                    PopUtils.dismiss(pop);
                    PickHelper.selectImage(mActivity, 1, false);
                    break;
                case R.id.llCancel:
                    PopUtils.dismiss(pop);
                    break;
            }
        };
        RelativeLayout root = view.findViewById(R.id.root);
        LinearLayout llBigImage = view.findViewById(R.id.llBigImage);
        LinearLayout llPicture = view.findViewById(R.id.llPicture);
        LinearLayout llCancel = view.findViewById(R.id.llCancel);
        root.setOnClickListener(listener);
        llBigImage.setOnClickListener(listener);
        llPicture.setOnClickListener(listener);
        llCancel.setOnClickListener(listener);
        PopUtils.show(pop, root, Gravity.CENTER);
    }

    private void showDial() {
        User ta = SPHelper.getTa();
        if (ta != null) {
            String phone = ta.getPhone().trim();
            Intent dial = IntentFactory.getDial(phone);
            ActivityTrans.start(mActivity, dial);
        }
    }

    private void ossUploadAvatar() {
        OssHelper.uploadAvatar(mActivity, cropFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                updateCoupleInfo(0, ossPath, "");
                ResHelper.deleteFileInBackground(cropFile);
            }

            @Override
            public void failure(File source, String errMsg) {
                ResHelper.deleteFileInBackground(cropFile);
            }
        });
    }

    private void showNameInput() {
        String name = UserHelper.getTaName(SPHelper.getMe()).trim();
        int coupleNameLength = SPHelper.getLimit().getCoupleNameLength();
        MaterialDialog dialogName = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .autoDismiss(true)
                .title(R.string.modify_ta_name)
                .input(getString(R.string.please_input_nickname), name, false,
                        (dialog, input) -> LogUtils.i(CoupleInfoActivity.class, "onInput", input.toString()))
                .inputRange(1, coupleNameLength)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog, which) -> {
                    // api
                    EditText editText = dialog.getInputEditText();
                    if (editText != null) {
                        String modifyName = editText.getText().toString();
                        updateCoupleInfo(0, "", modifyName);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialogName);
    }

    private void showTogetherTimePicker() {
        Couple couple = SPHelper.getCouple();
        long togetherAt = couple == null ? DateUtils.getCurrentLong() :
                (couple.getTogetherAt() == 0 ? DateUtils.getCurrentLong() : TimeHelper.getJavaTimeByGo(couple.getTogetherAt()));
        DialogHelper.showDateTimePicker(mActivity, togetherAt, time -> updateCoupleInfo(TimeHelper.getGoTimeByJava(time), "", ""));
    }

    private void updateCoupleInfo(long togetherAt, String avatar, String name) {
        User me = SPHelper.getMe();
        if (me == null) return;
        Couple body = me.getCouple();
        if (body == null) {
            body = new Couple();
        }
        body.setTogetherAt(togetherAt);
        if (body.getCreatorId() == me.getId()) {
            body.setInviteeAvatar(avatar);
            body.setInviteeName(name);
        } else {
            body.setCreatorAvatar(avatar);
            body.setCreatorName(name);
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).coupleUpdate(ApiHelper.COUPLE_UPDATE_INFO, body);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                Couple couple = data.getCouple();
                SPHelper.setCouple(couple);
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_COUPLE_REFRESH, couple));
                setViewData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void showBreakDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.u_confirm_break)
                .content(R.string.impulse_is_devil_3)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> coupleStatus(ApiHelper.COUPLE_UPDATE_BAD))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void coupleStatus(int type) {
        Couple couple = SPHelper.getCouple();
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).coupleUpdate(type, couple);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                Couple couple = data.getCouple();
                SPHelper.setCouple(couple);
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_COUPLE_REFRESH, couple));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    // 分手倒计时
    private Runnable getTogetherCountDownTask() {
        if (togetherCountDownTask == null) {
            togetherCountDownTask = new Runnable() {
                @Override
                public void run() {
                    Couple couple = SPHelper.getCouple();
                    if (couple == null) return;
                    long togetherAt = TimeHelper.getJavaTimeByGo(couple.getTogetherAt());
                    long timeGo = (DateUtils.getCurrentLong() - togetherAt) % TimeUnit.DAY / TimeUnit.SEC;
                    if (timeGo <= 0) {
                        String togetherDay = String.valueOf(UserHelper.getCoupleTogetherDay(couple));
                        tvTogether.setText(togetherDay);
                        RxBus.post(new RxBus.Event<>(RxBus.EVENT_COUPLE_REFRESH, couple));
                    }
                    String breakCountDownShow = getTogetherCountDownShow(timeGo);
                    tvTogetherTimer.setText(breakCountDownShow);
                    MyApp.get().getHandler().postDelayed(this, TimeUnit.SEC);
                }
            };
        }
        return togetherCountDownTask;
    }

    private void stopTogetherCountDownTask() {
        if (togetherCountDownTask != null) {
            MyApp.get().getHandler().removeCallbacks(togetherCountDownTask);
            togetherCountDownTask = null;
        }
    }

    private String getTogetherCountDownShow(long breakCountDown) {
        long hour = breakCountDown / (TimeUnit.HOUR / TimeUnit.SEC);
        String hourF = hour >= 10 ? "" : "0";
        long min = (breakCountDown - hour * (TimeUnit.HOUR / TimeUnit.SEC)) / (TimeUnit.MIN / TimeUnit.SEC);
        String minF = min >= 10 ? ":" : ":0";
        long sec = breakCountDown - hour * (TimeUnit.HOUR / TimeUnit.SEC) - min * (TimeUnit.MIN / TimeUnit.SEC);
        String secF = sec >= 10 ? ":" : ":0";
        return hourF + hour + minF + min + secF + sec;
    }

}
