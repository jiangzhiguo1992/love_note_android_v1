package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.CoupleStateAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Coin;
import com.jiangzg.lovenote_admin.domain.Couple;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.User;
import com.jiangzg.lovenote_admin.domain.Vip;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.DialogHelper;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;
import com.jiangzg.lovenote_admin.view.FrescoView;

import java.util.List;

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
    @BindView(R.id.btnBill)
    Button btnBill;
    @BindView(R.id.btnVip)
    Button btnVip;
    @BindView(R.id.btnSign)
    Button btnSign;
    @BindView(R.id.btnCoin)
    Button btnCoin;
    @BindView(R.id.btnNote)
    Button btnNote;

    @BindView(R.id.btnUid1)
    Button btnUid1;
    @BindView(R.id.btnUid2)
    Button btnUid2;
    @BindView(R.id.btnVipAdd1)
    Button btnVipAdd1;
    @BindView(R.id.btnCoinAdd1)
    Button btnCoinAdd1;
    @BindView(R.id.btnVipAdd2)
    Button btnVipAdd2;
    @BindView(R.id.btnCoinAdd2)
    Button btnCoinAdd2;
    @BindView(R.id.btnPost1)
    Button btnPost1;
    @BindView(R.id.btnPostComment1)
    Button btnPostComment1;
    @BindView(R.id.btnMatchWork1)
    Button btnMatchWork1;
    @BindView(R.id.btnPost2)
    Button btnPost2;
    @BindView(R.id.btnPostComment2)
    Button btnPostComment2;
    @BindView(R.id.btnMatchWork2)
    Button btnMatchWork2;

    @BindView(R.id.tvCreate)
    TextView tvCreate;
    @BindView(R.id.tvUpdate)
    TextView tvUpdate;
    @BindView(R.id.ivAvatar1)
    FrescoView ivAvatar1;
    @BindView(R.id.tvName1)
    TextView tvName1;
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
    private RecyclerHelper recyclerHelper;
    private int page;

    public static void goActivity(Activity from, long cid, long uid) {
        Intent intent = new Intent(from, CoupleDetailActivity.class);
        intent.putExtra("cid", cid);
        intent.putExtra("uid", uid);
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
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new CoupleStateAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getListData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        CoupleStateAdapter coupleStateAdapter = (CoupleStateAdapter) adapter;
                        coupleStateAdapter.goUser(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        getCoupleData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.btnSearch, R.id.btnBill, R.id.btnVip, R.id.btnCoin, R.id.btnSign, R.id.btnNote,
            R.id.btnUid1, R.id.btnUid2, R.id.btnVipAdd1, R.id.btnCoinAdd1, R.id.btnVipAdd2, R.id.btnCoinAdd2,
            R.id.btnPost1, R.id.btnPost2, R.id.btnPostComment1, R.id.btnPostComment2, R.id.btnMatchWork1, R.id.btnMatchWork2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnSearch:
                getCoupleData();
                break;
            case R.id.btnBill:
                if (couple == null) return;
                BillListActivity.goActivity(mActivity, 0, couple.getId());
                break;
            case R.id.btnVip:
                if (couple == null) return;
                VipListActivity.goActivity(mActivity, 0, couple.getId());
                break;
            case R.id.btnCoin:
                if (couple == null) return;
                CoinListActivity.goActivity(mActivity, 0, couple.getId());
                break;
            case R.id.btnSign:
                if (couple == null) return;
                SignListActivity.goActivity(mActivity, 0, couple.getId());
                break;
            case R.id.btnNote:
                if (couple == null) return;
                TrendsListActivity.goActivity(mActivity, 0, couple.getId());
                break;
            case R.id.btnUid1:
                if (couple == null) return;
                UserDetailActivity.goActivity(mActivity, couple.getCreatorId());
                break;
            case R.id.btnUid2:
                if (couple == null) return;
                UserDetailActivity.goActivity(mActivity, couple.getInviteeId());
                break;
            case R.id.btnVipAdd1:
                if (couple == null) return;
                showVipAddDialog(couple.getCreatorId());
                break;
            case R.id.btnCoinAdd1:
                if (couple == null) return;
                showCoinAddDialog(couple.getCreatorId());
                break;
            case R.id.btnVipAdd2:
                if (couple == null) return;
                showVipAddDialog(couple.getInviteeId());
                break;
            case R.id.btnCoinAdd2:
                if (couple == null) return;
                showCoinAddDialog(couple.getInviteeId());
                break;
            case R.id.btnPost1:
                if (couple == null) return;
                PostListActivity.goActivity(mActivity, couple.getCreatorId());
                break;
            case R.id.btnPostComment1:
                if (couple == null) return;
                PostCommentListActivity.goActivity(mActivity, couple.getCreatorId(), 0, 0);
                break;
            case R.id.btnMatchWork1:
                if (couple == null) return;
                MatchWorkListActivity.goActivity(mActivity, couple.getCreatorId(), 0);
                break;
            case R.id.btnPost2:
                if (couple == null) return;
                PostListActivity.goActivity(mActivity, couple.getInviteeId());
                break;
            case R.id.btnPostComment2:
                if (couple == null) return;
                PostCommentListActivity.goActivity(mActivity, couple.getInviteeId(), 0, 0);
                break;
            case R.id.btnMatchWork2:
                if (couple == null) return;
                MatchWorkListActivity.goActivity(mActivity, couple.getInviteeId(), 0);
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
                getListData(false);
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

    private void getListData(final boolean more) {
        if (couple == null) return;
        page = more ? page + 1 : 0;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).coupleStateListGet(couple.getId(), page);
        MaterialDialog loading = null;
        if (!more) {
            loading = getLoading(true);
        }
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Couple.State> coupleStateList = data.getCoupleStateList();
                recyclerHelper.dataOk(coupleStateList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
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
        String create = "c:" + DateUtils.getStr(couple.getCreateAt() * 1000, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = "u:" + DateUtils.getStr(couple.getUpdateAt() * 1000, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        String creatorId = "c用户:" + couple.getCreatorId();
        String creatorAvatar = couple.getCreatorAvatar();
        String creatorName = couple.getCreatorName();
        String inviteeId = "i用户:" + couple.getInviteeId();
        String inviteeAvatar = couple.getInviteeAvatar();
        String inviteeName = couple.getInviteeName();
        // view
        etId.setText(id);
        tvCreate.setText(create);
        tvUpdate.setText(update);
        btnUid1.setText(creatorId);
        ivAvatar1.setWidthAndHeight(width, height);
        ivAvatar1.setData(creatorAvatar);
        tvName1.setText(creatorName);
        btnUid2.setText(inviteeId);
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
        String birth1 = DateUtils.getStr(creator.getBirthday() * 1000, DateUtils.FORMAT_LINE_Y_M_D);
        String phone1 = creator.getPhone();
        String sex2 = User.getSexShow(inviter.getSex());
        String birth2 = DateUtils.getStr(inviter.getBirthday() * 1000, DateUtils.FORMAT_LINE_Y_M_D);
        String phone2 = inviter.getPhone();
        // view
        tvSex1.setText(birth1 + " " + sex1);
        tvPhone1.setText(phone1);
        tvSex2.setText(sex2 + " " + birth2);
        tvPhone2.setText(phone2);
    }

    private void showVipAddDialog(final long uid) {
        MaterialDialog dialogName = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .autoDismiss(true)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("会员天数", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        LogUtils.i(CoupleDetailActivity.class, "onInput", input.toString());
                    }
                })
                .inputRange(1, 5)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // api
                        EditText editText = dialog.getInputEditText();
                        if (editText != null) {
                            String input = editText.getText().toString();
                            vipAdd(uid, input);
                        }
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialogName);
    }

    private void vipAdd(long uid, String input) {
        if (!StringUtils.isNumber(input)) return;
        int days = Integer.parseInt(input);
        Vip vip = new Vip();
        vip.setUserId(uid);
        vip.setCoupleId(couple.getId());
        vip.setExpireDays(days);
        Call<Result> call = new RetrofitHelper().call(API.class).moreVipAdd(vip);
        RetrofitHelper.enqueue(call, getLoading(true), null);
    }

    private void showCoinAddDialog(final long uid) {
        MaterialDialog dialogName = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .autoDismiss(true)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input("金币数量", "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        LogUtils.i(CoupleDetailActivity.class, "onInput", input.toString());
                    }
                })
                .inputRange(1, 5)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // api
                        EditText editText = dialog.getInputEditText();
                        if (editText != null) {
                            String input = editText.getText().toString();
                            coinAdd(uid, input);
                        }
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialogName);
    }

    private void coinAdd(long uid, String input) {
        if (!StringUtils.isNumber(input)) return;
        int count = Integer.parseInt(input);
        Coin coin = new Coin();
        coin.setUserId(uid);
        coin.setCoupleId(couple.getId());
        coin.setChange(count);
        Call<Result> call = new RetrofitHelper().call(API.class).moreCoinAdd(coin);
        RetrofitHelper.enqueue(call, getLoading(true), null);
    }

}
