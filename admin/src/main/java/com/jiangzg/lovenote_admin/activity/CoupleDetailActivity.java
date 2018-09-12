package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Couple;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.User;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;
import com.jiangzg.lovenote_admin.view.FrescoView;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class CoupleDetailActivity extends BaseActivity<CoupleDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.etId)
    EditText etId;
    @BindView(R.id.etUid)
    EditText etUid;
    @BindView(R.id.btnSearch)
    Button btnSearch;

    @BindView(R.id.tvCreate)
    TextView tvCreate;
    @BindView(R.id.tvUpdate)
    TextView tvUpdate;
    @BindView(R.id.tvUid1)
    TextView tvUid1;
    @BindView(R.id.ivAvatar1)
    FrescoView ivAvatar1;
    @BindView(R.id.tvName1)
    TextView tvName1;
    @BindView(R.id.tvUid2)
    TextView tvUid2;
    @BindView(R.id.ivAvatar2)
    FrescoView ivAvatar2;
    @BindView(R.id.tvName2)
    TextView tvName2;
    @BindView(R.id.tvPhone1)
    TextView tvPhone1;
    @BindView(R.id.tvPhone2)
    TextView tvPhone2;
    @BindView(R.id.tvSex1)
    TextView tvSex1;
    @BindView(R.id.tvSex2)
    TextView tvSex2;
    @BindView(R.id.rv)
    RecyclerView rv;

    private Couple couple;
    private User creator, inviter;

    public static void goActivity(Activity from, long cid) {
        Intent intent = new Intent(from, CoupleDetailActivity.class);
        intent.putExtra("cid", cid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_couple_detail;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "couple_detail", true);
        long cid = intent.getLongExtra("cid", 0);
        long uid = intent.getLongExtra("uid", 0);
        if (cid > 0) {
            etId.setText(String.valueOf(cid));
        }
        if (uid > 0) {
            etUid.setText(String.valueOf(uid));
        }
        // TODO rv
    }

    @Override
    protected void initData(Intent intent, Bundle state) {

    }

    @Override
    protected void onFinish(Bundle state) {

    }

    @OnClick({R.id.btnSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                getCoupleData();
                break;
        }
    }

    private void getCoupleData() {
        String sCid = etId.getText().toString().trim();
        String sUid = etUid.getText().toString().trim();
        long cid = 0, uid = 0;
        if (StringUtils.isNumber(sCid)) {
            cid = Long.parseLong(sCid);
        }
        if (StringUtils.isNumber(sUid)) {
            uid = Long.parseLong(sUid);
        }
        Call<Result> call = new RetrofitHelper().call(API.class).coupleGet(uid, cid);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                couple = data.getCouple();
                refreshCoupleView();
                getUserData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void getUserData() {
        if (couple == null) return;
        long creatorId = couple.getCreatorId();
        long inviteeId = couple.getInviteeId();
        Call<Result> call1 = new RetrofitHelper().call(API.class).userGet(creatorId, "");
        RetrofitHelper.enqueue(call1, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                creator = data.getUser();
                refreshUserView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        Call<Result> call2 = new RetrofitHelper().call(API.class).userGet(inviteeId, "");
        RetrofitHelper.enqueue(call2, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                inviter = data.getUser();
                refreshUserView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    private void refreshCoupleView() {
        if (couple == null) {
            DialogHelper.getBuild(mActivity)
                    .cancelable(true)
                    .canceledOnTouchOutside(false)
                    .content("couple = null")
                    .positiveText(R.string.i_know)
                    .show();
            return;
        }
        // data
        int width = ConvertUtils.dp2px(30);
        int height = ConvertUtils.dp2px(30);
        String id = String.valueOf(couple.getId());
        String create = "c:" + DateUtils.getString(couple.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = "u:" + DateUtils.getString(couple.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String creatorId = "create:" + couple.getCreatorId();
        String creatorAvatar = couple.getCreatorAvatar();
        String creatorName = couple.getCreatorName();
        String inviteeId = "invitee:" + couple.getInviteeId();
        String inviteeAvatar = couple.getInviteeAvatar();
        String inviteeName = couple.getInviteeName();
        // view
        etId.setText(id);
        tvCreate.setText(create);
        tvUpdate.setText(update);
        tvUid1.setText(creatorId);
        ivAvatar1.setWidthAndHeight(width, height);
        ivAvatar1.setData(creatorAvatar);
        tvName1.setText(creatorName);
        tvUid2.setText(inviteeId);
        ivAvatar2.setWidthAndHeight(width, height);
        ivAvatar2.setData(inviteeAvatar);
        tvName2.setText(inviteeName);
    }

    private void refreshUserView() {
        if (creator == null || inviter == null) {
            return;
        }
        // creator
        String sex1 = User.getSexShow(creator.getSex());
        String birth1 = DateUtils.getString(creator.getBirthday() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D);
        String phone1 = creator.getPhone();
        String sex2 = User.getSexShow(inviter.getSex());
        String birth2 = DateUtils.getString(inviter.getBirthday() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D);
        String phone2 = inviter.getPhone();
        // view
        tvSex1.setText(sex1 + " " + birth1);
        tvPhone1.setText(phone1);
        tvSex2.setText(sex2 + " " + birth2);
        tvPhone2.setText(phone2);
    }

}
