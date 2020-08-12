package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.note.GiftAdapter;
import com.jiangzg.lovenote.controller.adapter.note.PromiseAdapter;
import com.jiangzg.lovenote.helper.common.ListHelper;
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
import com.jiangzg.lovenote.model.entity.Angry;
import com.jiangzg.lovenote.model.entity.Gift;
import com.jiangzg.lovenote.model.entity.Promise;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class AngryDetailActivity extends BaseActivity<AngryDetailActivity> {

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
    @BindView(R.id.tvGiftAdd)
    TextView tvGiftAdd;
    @BindView(R.id.rvGift)
    RecyclerView rvGift;
    @BindView(R.id.tvPromiseAdd)
    TextView tvPromiseAdd;
    @BindView(R.id.rvPromise)
    RecyclerView rvPromise;

    private Angry angry;
    private RecyclerHelper recyclerPromise;
    private RecyclerHelper recyclerGift;

    public static void goActivity(Activity from, Angry angry) {
        Intent intent = new Intent(from, AngryDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_OBJ);
        intent.putExtra("angry", angry);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long aid) {
        Intent intent = new Intent(from, AngryDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        intent.putExtra("aid", aid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from, long aid) {
        Intent intent = new Intent(from, AngryDetailActivity.class);
        intent.putExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
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
        int from = intent.getIntExtra("from", BaseActivity.ACT_DETAIL_FROM_ID);
        if (from == BaseActivity.ACT_DETAIL_FROM_OBJ) {
            angry = intent.getParcelableExtra("angry");
            refreshView();
            // 必须加，要获取关联数据
            if (angry != null) {
                refreshData(angry.getId());
            }
        } else if (from == BaseActivity.ACT_DETAIL_FROM_ID) {
            long aid = intent.getLongExtra("aid", 0);
            refreshData(aid);
        } else {
            mActivity.finish();
        }
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<Gift> busGiftSelect = RxBus.register(RxBus.EVENT_GIFT_SELECT, this::updateGift);
        pushBus(RxBus.EVENT_GIFT_SELECT, busGiftSelect);
        Observable<Promise> busPromiseSelect = RxBus.register(RxBus.EVENT_PROMISE_SELECT, this::updatePromise);
        pushBus(RxBus.EVENT_PROMISE_SELECT, busPromiseSelect);
        Observable<Gift> busGiftListDelete = RxBus.register(RxBus.EVENT_GIFT_LIST_ITEM_DELETE, gift -> {
            if (recyclerGift == null) return;
            ListHelper.removeObjInAdapter(recyclerGift.getAdapter(), gift);
            if (recyclerGift.getAdapter().getData().size() <= 0) {
                // 删除礼物
                tvGiftAdd.setVisibility(View.VISIBLE);
                rvGift.setVisibility(View.GONE);
            }
        });
        pushBus(RxBus.EVENT_GIFT_LIST_ITEM_DELETE, busGiftListDelete);
        Observable<Gift> busGiftListRefresh = RxBus.register(RxBus.EVENT_GIFT_LIST_ITEM_REFRESH, gift -> {
            if (recyclerGift == null) return;
            ListHelper.refreshObjInAdapter(recyclerGift.getAdapter(), gift);
        });
        pushBus(RxBus.EVENT_GIFT_LIST_ITEM_REFRESH, busGiftListRefresh);
        Observable<Promise> busPromiseListDelete = RxBus.register(RxBus.EVENT_PROMISE_LIST_ITEM_DELETE, promise -> {
            if (recyclerPromise == null) return;
            ListHelper.removeObjInAdapter(recyclerPromise.getAdapter(), promise);
            if (recyclerPromise.getAdapter().getData().size() <= 0) {
                // 删除承诺
                tvPromiseAdd.setVisibility(View.VISIBLE);
                rvPromise.setVisibility(View.GONE);
            }
        });
        pushBus(RxBus.EVENT_PROMISE_LIST_ITEM_DELETE, busPromiseListDelete);
        Observable<Promise> busPromiseListRefresh = RxBus.register(RxBus.EVENT_PROMISE_LIST_ITEM_REFRESH, promise -> {
            if (recyclerPromise == null) return;
            ListHelper.refreshObjInAdapter(recyclerPromise.getAdapter(), promise);
        });
        pushBus(RxBus.EVENT_PROMISE_LIST_ITEM_REFRESH, busPromiseListRefresh);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerPromise);
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
                showDeleteDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.tvGiftAdd, R.id.tvPromiseAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvGiftAdd: // 添加礼物
                GiftListActivity.goActivityBySelect(mActivity);
                break;
            case R.id.tvPromiseAdd: // 添加承诺
                PromiseListActivity.goActivityBySelect(mActivity);
                break;
        }
    }

    private void refreshData(long aid) {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).noteAngryGet(aid);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                angry = data.getAngry();
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
        if (angry == null) return;
        User user = SPHelper.getMe();
        // avatar
        String avatar = UserHelper.getAvatar(user, angry.getHappenId());
        ivAvatar.setData(avatar, angry.getHappenId());
        // happen
        String happenAt = TimeHelper.getTimeShowLine_HM_MD_YMD_ByGo(angry.getHappenAt());
        tvHappenAt.setText(happenAt);
        // content
        String content = angry.getContentText();
        tvContent.setText(content);
        // gift
        Gift gift = angry.getGift();
        if (gift == null || gift.getId() <= 0) {
            // 没有礼物
            tvGiftAdd.setVisibility(View.VISIBLE);
            rvGift.setVisibility(View.GONE);
        } else {
            // 有礼物
            tvGiftAdd.setVisibility(View.GONE);
            rvGift.setVisibility(View.VISIBLE);
            // recycler
            List<Gift> giftList = new ArrayList<>();
            giftList.add(gift);
            if (recyclerGift == null) {
                recyclerGift = new RecyclerHelper(rvGift)
                        .initLayoutManager(new LinearLayoutManager(mActivity))
                        .initAdapter(new GiftAdapter(mActivity))
                        .viewAnim()
                        .setAdapter()
                        .listenerClick(new OnItemChildClickListener() {
                            @Override
                            public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                                GiftAdapter giftAdapter = (GiftAdapter) adapter;
                                switch (view.getId()) {
                                    case R.id.ivMore: // 编辑
                                        giftAdapter.goEditActivity(position);
                                        break;
                                }
                            }
                        })
                        .listenerClick(new OnItemLongClickListener() {
                            @Override
                            public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                                showRemoveGiftDialog();
                            }
                        });
            }
            recyclerGift.dataNew(giftList, 0);
        }
        // promise
        Promise promise = angry.getPromise();
        if (promise == null || promise.getId() <= 0) {
            // 没有承诺
            tvPromiseAdd.setVisibility(View.VISIBLE);
            rvPromise.setVisibility(View.GONE);
        } else {
            // 有承诺
            tvPromiseAdd.setVisibility(View.GONE);
            rvPromise.setVisibility(View.VISIBLE);
            // recycler
            List<Promise> promiseList = new ArrayList<>();
            promiseList.add(promise);
            if (recyclerPromise == null) {
                recyclerPromise = new RecyclerHelper(rvPromise)
                        .initLayoutManager(new LinearLayoutManager(mActivity))
                        .initAdapter(new PromiseAdapter(mActivity))
                        .viewAnim()
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
                                showRemovePromiseDialog();
                            }
                        });
            }
            recyclerPromise.dataNew(promiseList, 0);
        }
    }

    private void showDeleteDialog() {
        if (angry == null || !angry.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_note));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_note)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> deleteApi())
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi() {
        if (angry == null) return;
        Call<Result> api = new RetrofitHelper().call(API.class).noteAngryDel(angry.getId());
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_ANGRY_LIST_ITEM_DELETE, angry));
                // finish
                mActivity.finish();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void showRemoveGiftDialog() {
        if (angry == null) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_remove_this_note)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> updateGift(new Gift()))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void showRemovePromiseDialog() {
        if (angry == null) return;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_remove_this_note)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> updatePromise(new Promise()))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void updateGift(Gift gift) {
        if (angry == null || gift == null) return;
        angry.setGiftId(gift.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteAngryUpdate(angry);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                angry = data.getAngry();
                // view
                refreshView();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_ANGRY_LIST_ITEM_REFRESH, angry));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void updatePromise(Promise promise) {
        if (angry == null || promise == null) return;
        angry.setPromiseId(promise.getId());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteAngryUpdate(angry);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                angry = data.getAngry();
                // view
                refreshView();
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_ANGRY_LIST_ITEM_REFRESH, angry));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
