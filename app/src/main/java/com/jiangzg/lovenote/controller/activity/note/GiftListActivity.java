package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.adapter.note.GiftAdapter;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.OssResHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Gift;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class GiftListActivity extends BaseActivity<GiftListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tvSearch)
    TextView tvSearch;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private RecyclerHelper recyclerHelper;
    private Observable<List<Gift>> obListRefresh;
    private Observable<Gift> obListItemRefresh;
    private Observable<Gift> obListItemDelete;
    private Call<Result> call;
    private int page;
    private int searchIndex;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, GiftListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), GiftListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivityBySelect(Activity from) {
        Intent intent = new Intent(from, GiftListActivity.class);
        intent.putExtra("from", BaseActivity.ACT_LIST_FROM_SELECT);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_gift_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        String title;
        if (isFromSelect()) {
            title = getString(R.string.please_select_gift);
        } else {
            title = getString(R.string.gift);
        }
        ViewHelper.initTopBar(mActivity, tb, title, true);
        // search
        searchIndex = 0;
        tvSearch.setText(ApiHelper.LIST_NOTE_SHOW[searchIndex]);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new GiftAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        GiftAdapter giftAdapter = (GiftAdapter) adapter;
                        if (isFromSelect()) {
                            // 礼物选择
                            giftAdapter.selectGift(position);
                        }
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        GiftAdapter giftAdapter = (GiftAdapter) adapter;
                        giftAdapter.goEditActivity(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // event
        obListRefresh = RxBus.register(RxBus.EVENT_GIFT_LIST_REFRESH, giftList -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        obListItemDelete = RxBus.register(RxBus.EVENT_GIFT_LIST_ITEM_DELETE, gift -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), gift);
        });
        obListItemRefresh = RxBus.register(RxBus.EVENT_GIFT_LIST_ITEM_REFRESH, gift -> {
            if (recyclerHelper == null) return;
            ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), gift);
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RxBus.unregister(RxBus.EVENT_GIFT_LIST_REFRESH, obListRefresh);
        RxBus.unregister(RxBus.EVENT_GIFT_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(RxBus.EVENT_GIFT_LIST_ITEM_REFRESH, obListItemRefresh);
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.llSearch, R.id.llAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llSearch: // 搜索
                showSearchDialog();
                break;
            case R.id.llAdd: // 添加
                GiftEditActivity.goActivity(mActivity);
                break;
        }
    }

    private boolean isFromSelect() {
        return getIntent().getIntExtra("from", BaseActivity.ACT_LIST_FROM_BROWSE) == BaseActivity.ACT_LIST_FROM_SELECT;
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        int searchType = ApiHelper.LIST_NOTE_TYPE[searchIndex];
        call = new RetrofitHelper().call(API.class).noteGiftListGet(searchType, page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Gift> giftList = data.getGiftList();
                recyclerHelper.dataOk(giftList, more);
                // 刷新本地资源
                List<String> ossKeyList = ListHelper.getOssKeyListByGift(giftList);
                OssResHelper.refreshResWithDelExpire(OssResHelper.TYPE_NOTE_GIFT, ossKeyList);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

    private void showSearchDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(ApiHelper.LIST_NOTE_SHOW)
                .itemsCallbackSingleChoice(searchIndex, (dialog1, view, which, text) -> {
                    if (recyclerHelper == null) return true;
                    if (which < 0 || which >= ApiHelper.LIST_NOTE_TYPE.length) {
                        return true;
                    }
                    searchIndex = which;
                    tvSearch.setText(ApiHelper.LIST_NOTE_SHOW[searchIndex]);
                    recyclerHelper.dataRefresh();
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

}
