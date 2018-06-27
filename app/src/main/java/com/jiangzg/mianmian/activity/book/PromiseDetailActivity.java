package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.PromiseBreakAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Promise;
import com.jiangzg.mianmian.domain.PromiseBreak;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class PromiseDetailActivity extends BaseActivity<PromiseDetailActivity> {

    private static final int FROM_NONE = 0;
    private static final int FROM_ID = 1;
    private static final int FROM_ALL = 2;

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.rlDel)
    RelativeLayout rlDel;
    @BindView(R.id.rlEdit)
    RelativeLayout rlEdit;
    @BindView(R.id.rlAdd)
    RelativeLayout rlAdd;
    @BindView(R.id.rlBreak)
    RelativeLayout rlBreak;
    @BindView(R.id.ivBreakClose)
    ImageView ivBreakClose;
    @BindView(R.id.tvBreakCommit)
    TextView tvBreakCommit;
    @BindView(R.id.tvBreakHappen)
    TextView tvBreakHappen;
    @BindView(R.id.cvBreakHappen)
    CardView cvBreakHappen;
    @BindView(R.id.tvBreakContentLimit)
    TextView tvBreakContentLimit;
    @BindView(R.id.etBreakContent)
    EditText etBreakContent;

    private Promise promise;
    private RecyclerHelper recyclerHelper;
    private BottomSheetBehavior behaviorBreak;
    private Call<Result> callGet;
    private Call<Result> callDel;
    private Call<Result> callBreakAdd;
    private Call<Result> callBreakListGet;
    private Observable<Promise> obDetailRefresh;
    private int page;
    private int limitBreakContentLength;
    private long breakHappen;

    public static void goActivity(Activity from, Promise promise) {
        Intent intent = new Intent(from, PromiseDetailActivity.class);
        intent.putExtra("from", FROM_ALL);
        intent.putExtra("promise", promise);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long pid) {
        Intent intent = new Intent(from, DiaryDetailActivity.class);
        intent.putExtra("from", FROM_ID);
        intent.putExtra("pid", pid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
        return R.layout.activity_promise_detail;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.promise), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new PromiseBreakAdapter(mActivity))
                .viewHeader(R.layout.list_head_promise_break)
                .viewEmpty(R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getBreakData(false);
                    }
                })
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getBreakData(true);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        PromiseBreakAdapter promiseBreakAdapter = (PromiseBreakAdapter) adapter;
                        promiseBreakAdapter.showDeleteDialog(position);
                    }
                });
        // break
        breakShow(false);
        // content 防止开始显示错误
        etBreakContent.setText("");
    }

    @Override
    protected void initData(Bundle state) {
        // event
        obDetailRefresh = RxBus.register(ConsHelper.EVENT_PROMISE_DETAIL_REFRESH, new Action1<Promise>() {
            @Override
            public void call(Promise promise) {
                if (PromiseDetailActivity.this.promise == null) return;
                refreshPromise(PromiseDetailActivity.this.promise.getId());
            }
        });
        Intent intent = getIntent();
        int from = intent.getIntExtra("from", FROM_NONE);
        if (from == FROM_ALL) {
            promise = getIntent().getParcelableExtra("promise");
            // view
            initHead();
            recyclerHelper.dataRefresh();
            // 没有详情页的，可以不加
            if (promise != null) {
                refreshPromise(promise.getId());
            }
        } else if (from == FROM_ID) {
            long pid = intent.getLongExtra("pid", 0);
            refreshPromise(pid);
        }
        // breakHappen
        breakHappen = TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callDel);
        RetrofitHelper.cancel(callBreakAdd);
        RetrofitHelper.cancel(callBreakListGet);
        RxBus.unregister(ConsHelper.EVENT_PROMISE_DETAIL_REFRESH, obDetailRefresh);
    }

    @Override
    public void onBackPressed() {
        if (behaviorBreak.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            behaviorBreak.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_PROMISE_DETAIL);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnTextChanged({R.id.etBreakContent})
    public void afterTextChanged(Editable s) {
        onBreakContentInput(s.toString());
    }

    @OnClick({R.id.rlDel, R.id.rlEdit, R.id.rlAdd,
            R.id.ivBreakClose, R.id.cvBreakHappen, R.id.tvBreakCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlDel:
                showDelDialog();
                break;
            case R.id.rlEdit:
                if (promise == null) return;
                PromiseEditActivity.goActivity(mActivity, promise);
                break;
            case R.id.rlAdd:
                breakShow(true);
                break;
            case R.id.ivBreakClose:
                breakShow(false);
                break;
            case R.id.cvBreakHappen:
                showBreakTimePicker();
                break;
            case R.id.tvBreakCommit:
                commitBreak();
                break;
        }
    }

    public void refreshPromise(long pid) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).promiseGet(pid);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                promise = data.getPromise();
                // view
                initHead();
                recyclerHelper.dataRefresh();
            }

            @Override
            public void onFailure(String errMsg) {
                srl.setRefreshing(false);
            }
        });
    }

    private void initHead() {
        // data
        if (promise == null) return;
        User me = SPHelper.getMe();
        String happenName = String.format(Locale.getDefault(), getString(R.string.promise_user_colon_space_holder), me.getNameInCp(promise.getHappenId()));
        String happenTime = String.format(Locale.getDefault(), getString(R.string.promise_time_colon_space_holder), TimeHelper.getTimeShowCnSpace_HM_MD_YMD_ByGo(promise.getHappenAt()));
        String content = promise.getContentText();
        String breakCount = String.format(Locale.getDefault(), getString(R.string.break_space_holder_space_time), promise.getBreakCount());
        // view
        View head = recyclerHelper.getViewHead();
        TextView tvPromiseUser = head.findViewById(R.id.tvPromiseUser);
        TextView tvPromiseHappen = head.findViewById(R.id.tvPromiseHappen);
        TextView tvContent = head.findViewById(R.id.tvContent);
        TextView tvBreakCount = head.findViewById(R.id.tvBreakCount);
        // set
        tvPromiseUser.setText(happenName);
        tvPromiseHappen.setText(happenTime);
        tvContent.setText(content);
        tvBreakCount.setText(breakCount);
    }

    private void getBreakData(final boolean more) {
        if (promise == null) return;
        page = more ? page + 1 : 0;
        // api
        callBreakListGet = new RetrofitHelper().call(API.class).promiseBreakListGet(promise.getId(), page);
        RetrofitHelper.enqueue(callBreakListGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                recyclerHelper.viewEmptyShow(data.getShow());
                List<PromiseBreak> promiseBreakList = data.getPromiseBreakList();
                recyclerHelper.dataOk(promiseBreakList, more);
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(more, errMsg);
            }
        });
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
        if (behaviorBreak == null) {
            behaviorBreak = BottomSheetBehavior.from(rlBreak);
        }
        int state = show ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN;
        behaviorBreak.setState(state);
    }

    private void showDelDialog() {
        if (promise == null || !promise.isMine()) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .content(R.string.confirm_delete_this_promise)
                .cancelable(true)
                .canceledOnTouchOutside(false)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delPromise();
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void delPromise() {
        if (promise == null) return;
        MaterialDialog loading = getLoading(getString(R.string.are_deleting), true);
        callDel = new RetrofitHelper().call(API.class).promiseDel(promise.getId());
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // ListItemDelete
                RxEvent<Promise> event = new RxEvent<>(ConsHelper.EVENT_PROMISE_LIST_ITEM_DELETE, promise);
                RxBus.post(event);
                mActivity.finish();
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    private void showBreakTimePicker() {
        Calendar calendar = DateUtils.getCurrentCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog picker = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar instance = DateUtils.getCurrentCalendar();
                instance.set(year, month, dayOfMonth);
                breakHappen = TimeHelper.getGoTimeByJava(instance.getTimeInMillis());
            }
        }, year, month, day);
        picker.show();
    }

    private void commitBreak() {
        if (promise == null) return;
        String content = etBreakContent.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(etBreakContent.getHint());
            return;
        }
        PromiseBreak promiseBreak = ApiHelper.getPromiseBreakBody(promise.getId(), breakHappen, content);
        MaterialDialog loading = getLoading(getString(R.string.are_deleting), true);
        callBreakAdd = new RetrofitHelper().call(API.class).promiseBreakAdd(promiseBreak);
        RetrofitHelper.enqueue(callBreakAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                etBreakContent.setText("");
                breakShow(false);
                getBreakData(false);
                // head
                promise.setBreakCount(promise.getBreakCount() + 1);
                initHead();
                // event
                RxEvent<Promise> event = new RxEvent<>(ConsHelper.EVENT_PROMISE_LIST_ITEM_REFRESH, promise);
                RxBus.post(event);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
