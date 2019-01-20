package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
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
import com.jiangzg.lovenote.model.entity.PromiseBreak;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import rx.Observable;

public class PromiseDetailActivity extends BaseActivity<PromiseDetailActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.llDel)
    LinearLayout llDel;
    @BindView(R.id.llEdit)
    LinearLayout llEdit;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;
    @BindView(R.id.rlBreak)
    RelativeLayout rlBreak;
    @BindView(R.id.ivBreakClose)
    ImageView ivBreakClose;
    @BindView(R.id.ivAddCommit)
    ImageView ivAddCommit;
    @BindView(R.id.btnBreakHappen)
    Button btnBreakHappen;
    @BindView(R.id.tvBreakContentLimit)
    TextView tvBreakContentLimit;
    @BindView(R.id.etBreakContent)
    EditText etBreakContent;

    private Promise promise;
    private RecyclerHelper recyclerHelper;
    private BottomSheetBehavior behaviorBreak;
    private int page;
    private int limitBreakContentLength;
    private long breakHappen;

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
                .initRefresh(srl, false)
                .initAdapter(new PromiseBreakAdapter(mActivity))
                .viewHeader(mActivity, R.layout.list_head_promise_break)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
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
            recyclerHelper.dataRefresh();
            // 没有详情页的，可以不加
            if (promise != null) {
                refreshPromise(promise.getId());
            }
        } else if (from == BaseActivity.ACT_DETAIL_FROM_ID) {
            long pid = intent.getLongExtra("pid", 0);
            refreshPromise(pid);
        }
        // happen
        refreshDateView();
        // break
        breakShow(false);
        // content 防止开始显示错误
        etBreakContent.setText("");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // event
        Observable<Promise> obDetailRefresh = RxBus.register(RxBus.EVENT_PROMISE_DETAIL_REFRESH, promise -> {
            if (PromiseDetailActivity.this.promise == null) return;
            refreshPromise(PromiseDetailActivity.this.promise.getId());
        });
        pushBus(RxBus.EVENT_PROMISE_DETAIL_REFRESH, obDetailRefresh);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public void onBackPressed() {
        if (behaviorBreak.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            behaviorBreak.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @OnTextChanged({R.id.etBreakContent})
    public void afterTextChanged(Editable s) {
        onBreakContentInput(s.toString());
    }

    @OnClick({R.id.llDel, R.id.llEdit, R.id.llAdd,
            R.id.ivBreakClose, R.id.btnBreakHappen, R.id.ivAddCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llDel:
                showDelDialog();
                break;
            case R.id.llEdit:
                if (promise == null) return;
                //PromiseEditActivity.goActivity(mActivity, promise);
                break;
            case R.id.llAdd:
                breakShow(true);
                break;
            case R.id.ivBreakClose:
                breakShow(false);
                break;
            case R.id.btnBreakHappen:
                showBreakTimePicker();
                break;
            case R.id.ivAddCommit:
                commitBreak();
                break;
        }
    }

    private void refreshPromise(long pid) {
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
                // view
                initHead();
                recyclerHelper.dataRefresh();
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
        String happenAt = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(promise.getHappenAt());
        String content = promise.getContentText();
        String breakCount = String.format(Locale.getDefault(), getString(R.string.break_space_holder_space_time), promise.getBreakCount());
        // view
        View head = recyclerHelper.getViewHead();
        TextView tvPromiseUser = head.findViewById(R.id.tvPromiseUser);
        TextView tvPromiseHappen = head.findViewById(R.id.tvPromiseHappen);
        TextView tvContent = head.findViewById(R.id.tvContent);
        TextView tvBreakCount = head.findViewById(R.id.tvBreakCount);
        // set
        tvPromiseUser.setText(name);
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

    private void onBreakContentInput(String input) {
        if (limitBreakContentLength <= 0) {
            limitBreakContentLength = SPHelper.getLimit().getPromiseBreakContentLength();
        }
        int length = input.length();
        if (length > limitBreakContentLength) {
            CharSequence charSequence = input.subSequence(0, limitBreakContentLength);
            etBreakContent.setText(charSequence);
            etBreakContent.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitBreakContentLength);
        tvBreakContentLimit.setText(limitShow);
    }

    private void breakShow(boolean show) {
        //if (!show) InputUtils.hideSoftInput(etBreakContent);
        if (behaviorBreak == null) {
            behaviorBreak = BottomSheetBehavior.from(rlBreak);
        }
        int state = show ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN;
        behaviorBreak.setState(state);
    }

    private void showDelDialog() {
        if (promise == null || !promise.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_promise));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_delete_this_promise)
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

    private void showBreakTimePicker() {
        DialogHelper.showDateTimePicker(mActivity, DateUtils.getCurrentLong(), time -> {
            breakHappen = TimeHelper.getGoTimeByJava(time);
            refreshDateView();
        });
    }

    private void refreshDateView() {
        if (breakHappen == 0) {
            breakHappen = TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong());
        }
        String happen = TimeHelper.getTimeShowLocal_HM_MDHM_YMDHM_ByGo(breakHappen);
        btnBreakHappen.setText(happen);
    }

    private void commitBreak() {
        if (promise == null) return;
        String content = etBreakContent.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(etBreakContent.getHint());
            return;
        }
        InputUtils.hideSoftInput(etBreakContent);
        PromiseBreak promiseBreak = new PromiseBreak();
        promiseBreak.setPromiseId(promise.getId());
        promiseBreak.setHappenAt(breakHappen);
        promiseBreak.setContentText(content);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).notePromiseBreakAdd(promiseBreak);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                etBreakContent.setText("");
                breakShow(false);
                getBreakData(false);
                // head
                promise.setBreakCount(promise.getBreakCount() + 1);
                initHead();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_PROMISE_LIST_ITEM_REFRESH, promise));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
