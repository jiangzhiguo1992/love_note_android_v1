package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.GiftAdapter;
import com.jiangzg.mianmian.adapter.PromiseAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Angry;
import com.jiangzg.mianmian.domain.Gift;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Promise;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

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
    @BindView(R.id.cvGiftAdd)
    CardView cvGiftAdd;
    @BindView(R.id.rvGift)
    RecyclerView rvGift;
    @BindView(R.id.cvPromiseAdd)
    CardView cvPromiseAdd;
    @BindView(R.id.rvPromise)
    RecyclerView rvPromise;

    private Angry angry;
    private RecyclerHelper recyclerPromise;
    private RecyclerHelper recyclerGift;
    private Call<Result> callGet;
    private Call<Result> callDel;
    private Observable<Gift> obGiftSelect;
    private Observable<Promise> obPromiseSelect;
    private Observable<Promise> obPromiseListDelete;
    private Observable<Promise> obPromiseListRefresh;

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
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.angry), true);
        srl.setEnabled(false);
        // init
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
    protected void initData(Intent intent, Bundle state) {
        // event
        obGiftSelect = RxBus.register(ConsHelper.EVENT_GIFT_SELECT, new Action1<Gift>() {
            @Override
            public void call(Gift gift) {
                updateGift(gift);
            }
        });
        obPromiseSelect = RxBus.register(ConsHelper.EVENT_PROMISE_SELECT, new Action1<Promise>() {
            @Override
            public void call(Promise promise) {
                updatePromise(promise);
            }
        });
        obPromiseListDelete = RxBus.register(ConsHelper.EVENT_PROMISE_LIST_ITEM_DELETE, new Action1<Promise>() {
            @Override
            public void call(Promise promise) {
                if (recyclerPromise == null) return;
                ListHelper.removeObjInAdapter(recyclerPromise.getAdapter(), promise);
                if (recyclerPromise.getAdapter().getData().size() <= 0) {
                    // 删除承诺
                    cvPromiseAdd.setVisibility(View.VISIBLE);
                    rvPromise.setVisibility(View.GONE);
                }
            }
        });
        obPromiseListRefresh = RxBus.register(ConsHelper.EVENT_PROMISE_LIST_ITEM_REFRESH, new Action1<Promise>() {
            @Override
            public void call(Promise promise) {
                if (recyclerPromise == null) return;
                ListHelper.refreshObjInAdapter(recyclerPromise.getAdapter(), promise);
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerPromise);
        RetrofitHelper.cancel(callDel);
        RetrofitHelper.cancel(callGet);
        RxBus.unregister(ConsHelper.EVENT_GIFT_SELECT, obGiftSelect);
        RxBus.unregister(ConsHelper.EVENT_PROMISE_SELECT, obPromiseSelect);
        RxBus.unregister(ConsHelper.EVENT_PROMISE_LIST_ITEM_DELETE, obPromiseListDelete);
        RxBus.unregister(ConsHelper.EVENT_PROMISE_LIST_ITEM_REFRESH, obPromiseListRefresh);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_del, menu);
        return super.onCreateOptionsMenu(menu);
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

    @OnClick({R.id.cvGiftAdd, R.id.cvPromiseAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvGiftAdd: // 添加礼物
                GiftListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.cvPromiseAdd: // 添加承诺
                PromiseListActivity.goActivityBySelect(mActivity);
                break;
        }
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
        // avatar
        String avatar = user.getAvatarInCp(angry.getHappenId());
        ivAvatar.setData(avatar);
        // happen
        String happenAt = TimeHelper.getTimeShowCn_HM_MD_YMD_ByGo(angry.getHappenAt());
        tvHappenAt.setText(happenAt);
        // content
        String content = angry.getContentText();
        tvContent.setText(content);
        // gift
        Gift gift = angry.getGift();
        if (gift == null || gift.getId() <= 0) {
            // 没有礼物
            cvGiftAdd.setVisibility(View.VISIBLE);
            rvGift.setVisibility(View.GONE);
        } else {
            // 有礼物
            cvGiftAdd.setVisibility(View.GONE);
            rvGift.setVisibility(View.VISIBLE);
            // recycler
            List<Gift> giftList = new ArrayList<>();
            giftList.add(gift);
            if (recyclerGift == null) {
                recyclerGift = new RecyclerHelper(rvGift)
                        .initLayoutManager(new LinearLayoutManager(mActivity))
                        .initAdapter(new GiftAdapter(mActivity))
                        .setAdapter()
                        .listenerClick(new OnItemLongClickListener() {
                            @Override
                            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                                showDeleteGiftDialog();
                            }
                        });
            }
            recyclerGift.dataNew(giftList);
        }
        // promise
        Promise promise = angry.getPromise();
        if (promise == null || promise.getId() <= 0) {
            // 没有承诺
            cvPromiseAdd.setVisibility(View.VISIBLE);
            rvPromise.setVisibility(View.GONE);
        } else {
            // 有承诺
            cvPromiseAdd.setVisibility(View.GONE);
            rvPromise.setVisibility(View.VISIBLE);
            // recycler
            List<Promise> promiseList = new ArrayList<>();
            promiseList.add(promise);
            if (recyclerPromise == null) {
                recyclerPromise = new RecyclerHelper(rvPromise)
                        .initLayoutManager(new LinearLayoutManager(mActivity))
                        .initAdapter(new PromiseAdapter(mActivity))
                        .setAdapter()
                        .listenerClick(new OnItemClickListener() {
                            @Override
                            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                                PromiseAdapter promiseAdapter = (PromiseAdapter) adapter;
                                promiseAdapter.goPromiseDetail(position);
                            }
                        })
                        .listenerClick(new OnItemLongClickListener() {
                            @Override
                            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                                showDeletePromiseDialog();
                            }
                        });
            }
            recyclerPromise.dataNew(promiseList);
        }
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

    private void showDeleteGiftDialog() {
        if (angry == null) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_remove_this_gift)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        updateGift(new Gift());
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void showDeletePromiseDialog() {
        if (angry == null) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_remove_this_promise)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        updatePromise(new Promise());
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void updateGift(Gift gift) {
        if (angry == null || gift == null) return;
        MaterialDialog loading = getLoading(true);
        angry.setGiftId(gift.getId());
        callDel = new RetrofitHelper().call(API.class).angryUpdate(angry);
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                angry = data.getAngry();
                // view
                refreshView();
                // event
                RxEvent<Angry> event = new RxEvent<>(ConsHelper.EVENT_ANGRY_LIST_ITEM_REFRESH, angry);
                RxBus.post(event);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    private void updatePromise(Promise promise) {
        if (angry == null || promise == null) return;
        MaterialDialog loading = getLoading(true);
        angry.setPromiseId(promise.getId());
        callDel = new RetrofitHelper().call(API.class).angryUpdate(angry);
        RetrofitHelper.enqueue(callDel, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                angry = data.getAngry();
                // view
                refreshView();
                // event
                RxEvent<Angry> event = new RxEvent<>(ConsHelper.EVENT_ANGRY_LIST_ITEM_REFRESH, angry);
                RxBus.post(event);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
