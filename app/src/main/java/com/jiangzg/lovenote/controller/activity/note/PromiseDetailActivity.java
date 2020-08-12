package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.note.PromiseBreakAdapter;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Promise;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class PromiseDetailActivity extends BaseActivity<PromiseDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;

    private Promise promise;
    private RecyclerHelper recyclerHelper;
    private int page = 0;

    public static void goActivity(Activity from, Promise promise) {
        Intent intent = new Intent(from, PromiseDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_OBJ);
        intent.putExtra("promise", promise);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long pid) {
        Intent intent = new Intent(from, PromiseDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        intent.putExtra("pid", pid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from, long pid) {
        Intent intent = new Intent(from, PromiseDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        intent.putExtra("pid", pid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_promise_detail;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.promise), true);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new PromiseBreakAdapter(mActivity))
                .viewHeader(mActivity, R.layout.list_head_promise_break)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .viewAnim()
                .setAdapter()
                .listenerRefresh(() -> getBreakData(false))
                .listenerMore(currentCount -> getBreakData(true))
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        PromiseBreakAdapter promiseBreakAdapter = (PromiseBreakAdapter) adapter;
                        promiseBreakAdapter.showDeleteDialog(position, promise);
                    }
                });
        // init
        int from = intent.getIntExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        if (from == BaseActivity.ACT_DETAIL_FROM_OBJ) {
            promise = intent.getParcelableExtra("promise");
            // view
            initHead();
            // 没有详情页的，可以不加
            if (promise != null) {
                refreshPromiseData(promise.getId());
            }
        } else if (from == BaseActivity.ACT_DETAIL_FROM_ID) {
            long pid = intent.getLongExtra("pid", 0);
            refreshPromiseData(pid);
        } else {
            mActivity.finish();
        }
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<Promise> obDetailRefresh = RxBus.register(RxBus.EVENT_PROMISE_DETAIL_REFRESH, promise -> {
            if (PromiseDetailActivity.this.promise == null) return;
            refreshPromiseData(PromiseDetailActivity.this.promise.getId());
        });
        pushBus(RxBus.EVENT_PROMISE_DETAIL_REFRESH, obDetailRefresh);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.del, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuDel: // 删除
                showDelDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.fabAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fabAdd:
                if (promise == null) return;
                PromiseBreakEditActivity.goActivity(mActivity, promise.getId());
                break;
        }
    }

    private void refreshPromiseData(long pid) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).notePromiseGet(pid);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                srl.setRefreshing(false);
                promise = data.getPromise();
                // head
                initHead();
                // break
                recyclerHelper.dataRefresh();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_PROMISE_LIST_ITEM_REFRESH, promise));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void initHead() {
        // data
        if (promise == null || recyclerHelper == null) return;
        User me = SPHelper.getMe();
        String name = UserHelper.getName(me, promise.getHappenId());
        String creator = String.format(Locale.getDefault(), getString(R.string.creator_colon_space_holder), name);
        String happen = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(promise.getHappenAt());
        String happenAt = String.format(Locale.getDefault(), getString(R.string.create_at_colon_space_holder), happen);
        String content = promise.getContentText();
        String breakCount = String.format(Locale.getDefault(), getString(R.string.break_space_holder_space_time), promise.getBreakCount());
        // view
        View head = recyclerHelper.getViewHead();
        if (head == null) return;
        TextView tvPromiseUser = head.findViewById(R.id.tvPromiseUser);
        TextView tvPromiseHappen = head.findViewById(R.id.tvPromiseHappen);
        TextView tvContent = head.findViewById(R.id.tvContent);
        TextView tvBreakCount = head.findViewById(R.id.tvBreakCount);
        // set
        tvPromiseUser.setText(creator);
        tvPromiseHappen.setText(happenAt);
        tvContent.setText(content);
        tvBreakCount.setText(breakCount);
    }

    private void getBreakData(final boolean more) {
        if (promise == null) return;
        page = more ? page + 1 : 0;
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).notePromiseBreakListGet(promise.getId(), page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataOk(data.getShow(), data.getPromiseBreakList(), more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
        pushApi(api);
    }

    private void showDelDialog() {
        if (promise == null || !promise.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_note));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_delete_this_note)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> delPromise())
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delPromise() {
        if (promise == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).notePromiseDel(promise.getId());
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_PROMISE_LIST_ITEM_DELETE, promise));
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
