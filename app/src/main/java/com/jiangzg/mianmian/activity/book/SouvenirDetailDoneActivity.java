package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.MapShowActivity;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.Souvenir;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class SouvenirDetailDoneActivity extends BaseActivity<SouvenirDetailDoneActivity> {

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
    @BindView(R.id.tvAddress)
    TextView tvAddress;
    @BindView(R.id.tvCreator)
    TextView tvCreator;
    @BindView(R.id.tvCreateAt)
    TextView tvCreateAt;

    private Souvenir souvenir;
    private Call<Result> callGet;
    private Call<Result> callDel;
    private Observable<Souvenir> obDetailRefresh;

    public static void goActivity(Fragment from, Souvenir souvenir) {
        Intent intent = new Intent(from.getActivity(), SouvenirDetailDoneActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_OBJ);
        intent.putExtra("souvenir", souvenir);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long sid) {
        Intent intent = new Intent(from, SouvenirDetailDoneActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
        intent.putExtra("sid", sid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_souvenir_detail_done;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.souvenir), true);
        srl.setEnabled(false);
        // init
        int from = intent.getIntExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
        if (from == ConsHelper.ACT_DETAIL_FROM_OBJ) {
            souvenir = intent.getParcelableExtra("souvenir");
            refreshView();
            // 没有详情页的，可以不加
            if (souvenir != null) {
                refreshData(souvenir.getId());
            }
        } else if (from == ConsHelper.ACT_DETAIL_FROM_ID) {
            long sid = intent.getLongExtra("sid", 0);
            refreshData(sid);
        }
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obDetailRefresh = RxBus.register(ConsHelper.EVENT_SOUVENIR_DETAIL_REFRESH, new Action1<Souvenir>() {
            @Override
            public void call(Souvenir souvenir) {
                if (souvenir == null) return;
                refreshData(souvenir.getId());
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RxBus.unregister(ConsHelper.EVENT_SOUVENIR_DETAIL_REFRESH, obDetailRefresh);
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callDel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_del_edit, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_BOOK_SOUVENIR_DETAIL_DONE);
                return true;
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

    @OnClick({R.id.tvAddress})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvAddress:
                if (souvenir == null) return;
                MapShowActivity.goActivity(mActivity, souvenir.getAddress(), souvenir.getLongitude(), souvenir.getLatitude());
                break;
        }
    }

    private void refreshData(long sid) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).souvenirGet(sid);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
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
    }

    private void refreshView() {
        if (souvenir == null) return;
        // title
        tvTitle.setText(souvenir.getTitle());
        // happen
        long happenAt = TimeHelper.getJavaTimeByGo(souvenir.getHappenAt());
        String happen = DateUtils.getString(happenAt, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M);
        tvHappenAt.setText(happen);
        // dayCount
        long dayCount;
        String format;
        if (DateUtils.getCurrentLong() > happenAt) {
            dayCount = (DateUtils.getCurrentLong() - happenAt) / ConstantUtils.DAY;
            format = getString(R.string.already_gone_holder_day);
        } else {
            dayCount = (happenAt - DateUtils.getCurrentLong()) / ConstantUtils.DAY;
            format = getString(R.string.just_have_holder_day);
        }
        String days = String.format(Locale.getDefault(), format, dayCount);
        tvDayCount.setText(days);
        // address
        if (StringUtils.isEmpty(souvenir.getAddress())) {
            tvAddress.setVisibility(View.GONE);
        } else {
            tvAddress.setVisibility(View.VISIBLE);
            tvAddress.setText(souvenir.getAddress());
        }
        // creator
        User user = SPHelper.getMe();
        String name = user.getNameInCp(souvenir.getUserId());
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
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteApi();
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi() {
        if (souvenir == null) return;
        MaterialDialog loading = getLoading(true);
        callDel = new RetrofitHelper().call(API.class).souvenirDel(souvenir.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Souvenir> event = new RxEvent<>(ConsHelper.EVENT_SOUVENIR_LIST_ITEM_DELETE, souvenir);
                RxBus.post(event);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }


    public void edit() {
        // TODO edit：foreign的修改只带year和id(包括souvenir)

    }
}
