package com.jiangzg.ita.activity.couple;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentResult;
import com.jiangzg.base.component.IntentSend;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.activity.user.UserInfoActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.RxEvent;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.CheckHelper;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.DialogHelper;
import com.jiangzg.ita.helper.OssHelper;
import com.jiangzg.ita.helper.PopHelper;
import com.jiangzg.ita.helper.ResHelper;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.helper.RxBus;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GImageView;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CoupleInfoActivity extends BaseActivity<CoupleInfoActivity> {

    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tb)
    Toolbar tb;

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
        // 我的数据和身份
        me = SPHelper.getUser();
        isCreator = me.isCoupleCreator();
        // srl
        srl.setEnabled(false);
        // menu
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 帮助
                        HelpActivity.goActivity(mActivity, Help.TYPE_COUPLE_INFO);
                        break;
                }
                return true;
            }
        });
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
            ResHelper.deleteFileInBackground(cameraFile, true);
            ResHelper.deleteFileInBackground(cropFile, false);
            return;
        }
        if (requestCode == ConsHelper.REQUEST_CAMERA) {
            // 拍照
            goCropActivity(cameraFile);
        } else if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = IntentResult.getPictureFile(data);
            goCropActivity(pictureFile);
        } else if (requestCode == ConsHelper.REQUEST_CROP) {
            // 裁剪
            ResHelper.deleteFileInBackground(cameraFile, true);
            ossUploadAvatar();
        }
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
            public void onFailure() {
                srl.setRefreshing(false);
            }
        });
    }

    private void setViewData() {
        // meData
        String mePhone = me.getPhone();
        int meSex = 0;
        if (me.getSex() == User.SEX_BOY) {
            meSex = R.mipmap.ic_sex_boy_circle;
        } else if (me.getSex() == User.SEX_GIRL) {
            meSex = R.mipmap.ic_sex_girl_circle;
        }
        long meBirth = me.getBirthday() * 1000;
        String meBirthShow = DateUtils.getString(meBirth, ConstantUtils.FORMAT_POINT_Y_M_D);
        // taData
        String taPhone = "";
        if (ta != null) {
            taPhone = ta.getPhone();
        }
        int taSex = 0;
        if (ta != null) {
            if (ta.getSex() == User.SEX_BOY) {
                taSex = R.mipmap.ic_sex_boy_circle;
            } else if (ta.getSex() == User.SEX_GIRL) {
                taSex = R.mipmap.ic_sex_girl_circle;
            }
        }
        String tabBirthShow = "";
        if (ta != null && ta.getBirthday() != 0) {
            long taBirth = ta.getBirthday() * 1000;
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
            if (meSex != 0) {
                ivSexLeft.setImageResource(meSex);
            }
            if (taSex != 0) {
                ivSexRight.setImageResource(taSex);
            }
            tvBirthLeft.setText(meBirthShow);
            tvBirthRight.setText(tabBirthShow);
        } else {
            tvPhoneLeft.setText(taPhone);
            tvPhoneRight.setText(mePhone);
            if (taSex != 0) {
                ivSexLeft.setImageResource(taSex);
            }
            if (meSex != 0) {
                ivSexRight.setImageResource(meSex);
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
        cameraFile = ResHelper.createJPEGInCache();
        PopupWindow popupWindow = PopHelper.createAlbumCamera(mActivity, cameraFile);
        PopUtils.show(popupWindow, root);
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

    // oss上传头像
    private void ossUploadAvatar() {
        OssHelper.uploadAvatar(mActivity, cropFile, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(String ossPath) {
                apiCoupleInfo(ossPath, "");
            }

            @Override
            public void failure(String ossPath) {
            }
        });
    }

    // 修改名称对话框
    private void showNameInput() {
        String show = isCreator ? tvNameRight.getText().toString().trim() : tvNameLeft.getText().toString().trim();
        String hint = getString(R.string.please_input_nickname);
        if (dialogName == null) {
            dialogName = new MaterialDialog.Builder(mActivity)
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
            DialogHelper.setAnim(dialogName);
        }
        DialogHelper.show(dialogName);
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
            public void onFailure() {
            }
        });
    }

    // 拨打电话
    private void showDial() {
        String phone = isCreator ? tvPhoneRight.getText().toString().trim() : tvPhoneLeft.getText().toString().trim();
        Intent dial = IntentSend.getDial(phone);
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
        long cid = SPHelper.getCouple().getId();
        User body = ApiHelper.getCoupleUpdate2GoodBody(cid);
        if (!CheckHelper.isCoupleBreaking()) {
            // 要分手
            body = ApiHelper.getCoupleUpdate2BadBody(cid);
        }
        coupleStatus(body);
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
            public void onFailure() {
            }
        });
    }

}
