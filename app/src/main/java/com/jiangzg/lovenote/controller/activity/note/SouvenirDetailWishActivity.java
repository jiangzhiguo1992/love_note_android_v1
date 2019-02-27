package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.MapShowActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Souvenir;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class SouvenirDetailWishActivity extends BaseActivity<SouvenirDetailDoneActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.tvDayCount)
    TextView tvDayCount;
    @BindView(R.id.rlAddress)
    RelativeLayout rlAddress;
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvCreator)
    TextView tvCreator;
    @BindView(R.id.tvCreateAt)
    TextView tvCreateAt;

    private Souvenir souvenir;

    public static void goActivity(Fragment from, Souvenir souvenir) {
        Intent intent = new Intent(from.getActivity(), SouvenirDetailWishActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_OBJ);
        intent.putExtra("souvenir", souvenir);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long sid) {
        Intent intent = new Intent(from, SouvenirDetailWishActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        intent.putExtra("sid", sid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_souvenir_detail_wish;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.wish_list), true);
        srl.setEnabled(false);
        // init
        int from = intent.getIntExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        if (from == BaseActivity.ACT_DETAIL_FROM_OBJ) {
            souvenir = intent.getParcelableExtra("souvenir");
            refreshView();
            // 没有详情页的，可以不加
            if (souvenir != null) {
                refreshData(souvenir.getId());
            }
        } else if (from == BaseActivity.ACT_DETAIL_FROM_ID) {
            long sid = intent.getLongExtra("sid", 0);
            refreshData(sid);
        } else {
            mActivity.finish();
        }
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<Souvenir> obDetailRefresh = RxBus.register(RxBus.EVENT_SOUVENIR_DETAIL_REFRESH, souvenir -> {
            if (souvenir == null) return;
            refreshData(souvenir.getId());
        });
        pushBus(RxBus.EVENT_SOUVENIR_DETAIL_REFRESH, obDetailRefresh);
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.del_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuEdit: // 编辑
                if (souvenir == null) return true;
                SouvenirEditActivity.goActivity(mActivity, souvenir);
                return true;
            case R.id.menuDel: // 删除
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.rlAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlAddress:
                if (souvenir == null) return;
                MapShowActivity.goActivity(mActivity, souvenir.getAddress(), souvenir.getLongitude(), souvenir.getLatitude());
                break;
        }
    }

    private void refreshData(long sid) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).noteSouvenirGet(sid);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                souvenir = data.getSouvenir();
                refreshView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void refreshView() {
        if (souvenir == null) return;
        // title
        tvTitle.setText(souvenir.getTitle());
        // happen
        long happenAt = TimeHelper.getJavaTimeByGo(souvenir.getHappenAt());
        String happen = DateUtils.getStr(happenAt, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        tvHappenAt.setText(happen);
        // dayCount
        long dayCount;
        String format;
        if (DateUtils.getCurrentLong() > happenAt) {
            dayCount = (DateUtils.getCurrentLong() - happenAt) / TimeUnit.DAY;
            format = getString(R.string.add_holder);
        } else {
            dayCount = (happenAt - DateUtils.getCurrentLong()) / TimeUnit.DAY;
            format = getString(R.string.sub_holder);
        }
        String days = String.format(Locale.getDefault(), format, dayCount);
        tvDayCount.setText(days);
        // address
        if (StringUtils.isEmpty(souvenir.getAddress())) {
            rlAddress.setVisibility(View.GONE);
        } else {
            rlAddress.setVisibility(View.VISIBLE);
            tvAddress.setText(souvenir.getAddress());
        }
        // creator
        User me = SPHelper.getMe();
        String name = UserHelper.getName(me, souvenir.getUserId());
        tvCreator.setText(String.format(Locale.getDefault(), getString(R.string.creator_colon_space_holder), name));
        // createAt
        String createAt = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(souvenir.getCreateAt());
        String createShow = String.format(Locale.getDefault(), getString(R.string.create_at_colon_space_holder), createAt);
        tvCreateAt.setText(createShow);
    }

    private void showDeleteDialog() {
        if (souvenir == null || !souvenir.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_souvenir));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_souvenir)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> deleteApi())
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi() {
        if (souvenir == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteSouvenirDel(souvenir.getId());
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_SOUVENIR_LIST_ITEM_DELETE, souvenir));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
