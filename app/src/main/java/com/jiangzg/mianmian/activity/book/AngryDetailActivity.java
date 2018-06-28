package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Angry;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import butterknife.BindView;
import retrofit2.Call;

public class AngryDetailActivity extends BaseActivity<AngryDetailActivity> {

    private static final int FROM_NONE = 0;
    private static final int FROM_ID = 1;
    private static final int FROM_ALL = 2;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.ivAvatar)
    FrescoAvatarView ivAvatar;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.tvContent)
    TextView tvContent;

    private Angry angry;
    private Call<Result> callGet;
    private Call<Result> callDel;

    public static void goActivity(Activity from, Angry angry) {
        Intent intent = new Intent(from, AngryDetailActivity.class);
        intent.putExtra("from", FROM_ALL);
        intent.putExtra("angry", angry);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long aid) {
        Intent intent = new Intent(from, DiaryDetailActivity.class);
        intent.putExtra("from", FROM_ID);
        intent.putExtra("aid", aid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_angry_detail;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.angry), true);
        srl.setEnabled(false);
    }

    @Override
    protected void initData(Bundle state) {
        Intent intent = getIntent();
        int from = intent.getIntExtra("from", FROM_NONE);
        if (from == FROM_ALL) {
            angry = intent.getParcelableExtra("angry");
            refreshView();
            // 必须加，要获取关联数据
            if (angry != null) {
                refreshData(angry.getId());
            }
        } else if (from == FROM_ID) {
            long aid = intent.getLongExtra("aid", 0);
            refreshData(aid);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_del, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callDel);
        RetrofitHelper.cancel(callGet);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_ANGRY_DETAIL);
                return true;
            case R.id.menuDel: // 删除
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData(long aid) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).angryGet(aid);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                angry = data.getAngry();
                refreshView();
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
            }
        });
    }

    private void refreshView() {
        if (angry == null) return;
        User user = SPHelper.getMe();
        String avatar = user.getAvatarInCp(angry.getHappenId());
        ivAvatar.setData(avatar);
        // happen
        String happenAt = TimeHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(angry.getHappenAt());
        tvHappenAt.setText(happenAt);
        // content
        String content = angry.getContentText();
        tvContent.setText(content);
    }

    private void showDeleteDialog() {
        if (angry == null || !angry.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_angry));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_angry)
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
        if (angry == null) return;
        MaterialDialog loading = getLoading(true);
        callDel = new RetrofitHelper().call(API.class).angryDel(angry.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Angry> event = new RxEvent<>(ConsHelper.EVENT_ANGRY_LIST_ITEM_DELETE, angry);
                RxBus.post(event);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    // TODO UI + 点击事件 + 对应板块的扩展 + 返回值处理 + 调用此方法
    private void addGift() {
        if (angry == null) return;
        // TODO
        // event
        RxEvent<Angry> event = new RxEvent<>(ConsHelper.EVENT_ANGRY_LIST_ITEM_REFRESH, angry);
        RxBus.post(event);
    }

    // TODO UI + 点击事件 + 对应板块的扩展 + 返回值处理 + 调用此方法
    private void addPromise() {
        if (angry == null) return;
        // TODO
        // event
        RxEvent<Angry> event = new RxEvent<>(ConsHelper.EVENT_ANGRY_LIST_ITEM_REFRESH, angry);
        RxBus.post(event);
    }

}
