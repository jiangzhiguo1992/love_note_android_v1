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
import com.jiangzg.mianmian.domain.Dream;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class DreamDetailActivity extends BaseActivity<DreamDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvHappenAt)
    TextView tvHappenAt;
    @BindView(R.id.tvCreator)
    TextView tvCreator;
    @BindView(R.id.tvTextCount)
    TextView tvTextCount;
    @BindView(R.id.tvContent)
    TextView tvContent;

    private Dream dream;
    private Call<Result> callGet;
    private Call<Result> callDel;
    private Observable<Dream> obDetailRefresh;

    public static void goActivity(Activity from, Dream dream) {
        Intent intent = new Intent(from, DreamDetailActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_ALL);
        intent.putExtra("dream", dream);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long did) {
        Intent intent = new Intent(from, DiaryDetailActivity.class);
        intent.putExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
        intent.putExtra("did", did);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_dream_detail;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.dream), true);
        srl.setEnabled(false);
        // init
        int from = intent.getIntExtra("from", ConsHelper.ACT_DETAIL_FROM_ID);
        if (from == ConsHelper.ACT_DETAIL_FROM_ALL) {
            dream = intent.getParcelableExtra("dream");
            refreshView();
            // 没有详情页的，可以不加
            if (dream != null) {
                refreshData(dream.getId());
            }
        } else if (from == ConsHelper.ACT_DETAIL_FROM_ID) {
            long did = intent.getLongExtra("did", 0);
            refreshData(did);
        }
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obDetailRefresh = RxBus.register(ConsHelper.EVENT_DREAM_DETAIL_REFRESH, new Action1<Dream>() {
            @Override
            public void call(Dream dream) {
                if (dream == null) return;
                refreshData(dream.getId());
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callDel);
        RetrofitHelper.cancel(callGet);
        RxBus.unregister(ConsHelper.EVENT_DREAM_DETAIL_REFRESH, obDetailRefresh);
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
                HelpActivity.goActivity(mActivity, Help.INDEX_DREAM_DETAIL);
                return true;
            case R.id.menuEdit: // 编辑
                if (dream == null) return true;
                DreamEditActivity.goActivity(mActivity, dream);
                return true;
            case R.id.menuDel: // 删除
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void refreshData(long did) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).dreamGet(did);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                dream = data.getDream();
                refreshView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void refreshView() {
        if (dream == null) return;
        User user = SPHelper.getMe();
        // happen
        String happenAt = TimeHelper.getTimeShowCn_HM_MD_YMD_ByGo(dream.getHappenAt());
        tvHappenAt.setText(happenAt);
        // author
        String authorName = user.getNameInCp(dream.getUserId());
        String authorShow = String.format(Locale.getDefault(), getString(R.string.creator_colon_space_holder), authorName);
        tvCreator.setText(authorShow);
        // textCount
        String content = dream.getContentText();
        if (content == null) content = "";
        String countShow = String.format(Locale.getDefault(), mActivity.getString(R.string.text_number_space_colon_holder), content.length());
        tvTextCount.setText(countShow);
        // content
        tvContent.setText(content);
    }

    private void showDeleteDialog() {
        if (dream == null || !dream.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_dream));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_dream)
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
        if (dream == null) return;
        MaterialDialog loading = getLoading(true);
        callDel = new RetrofitHelper().call(API.class).dreamDel(dream.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Dream> event = new RxEvent<>(ConsHelper.EVENT_DREAM_LIST_ITEM_DELETE, dream);
                RxBus.post(event);
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
