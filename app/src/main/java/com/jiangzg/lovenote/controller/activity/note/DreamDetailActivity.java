package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Dream;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;

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
    private Observable<Dream> obDetailRefresh;
    private Call<Result> callGet;
    private Call<Result> callDel;

    public static void goActivity(Activity from, Dream dream) {
        Intent intent = new Intent(from, DreamDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_OBJ);
        intent.putExtra("dream", dream);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long did) {
        Intent intent = new Intent(from, DreamDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
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
        int from = intent.getIntExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        if (from == BaseActivity.ACT_DETAIL_FROM_OBJ) {
            dream = intent.getParcelableExtra("dream");
            refreshView();
            // 没有详情页的，可以不加
            if (dream != null) {
                refreshData(dream.getId());
            }
        } else if (from == BaseActivity.ACT_DETAIL_FROM_ID) {
            long did = intent.getLongExtra("did", 0);
            refreshData(did);
        }
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obDetailRefresh = RxBus.register(RxBus.EVENT_DREAM_DETAIL_REFRESH, dream -> {
            if (dream == null) return;
            refreshData(dream.getId());
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callDel);
        RetrofitHelper.cancel(callGet);
        RxBus.unregister(RxBus.EVENT_DREAM_DETAIL_REFRESH, obDetailRefresh);
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
        callGet = new RetrofitHelper().call(API.class).noteDreamGet(did);
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
        String happenAt = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(dream.getHappenAt());
        tvHappenAt.setText(happenAt);
        // author
        String authorName = UserHelper.getName(user, dream.getUserId());
        tvCreator.setText(authorName);
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
                .onPositive((dialog1, which) -> deleteApi())
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi() {
        if (dream == null) return;
        MaterialDialog loading = getLoading(true);
        callDel = new RetrofitHelper().call(API.class).noteDreamDel(dream.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_DREAM_LIST_ITEM_DELETE, dream));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
