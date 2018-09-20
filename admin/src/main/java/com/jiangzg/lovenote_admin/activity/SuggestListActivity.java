package com.jiangzg.lovenote_admin.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.adapter.SuggestAdapter;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.domain.Suggest;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;
import com.jiangzg.lovenote_admin.helper.ViewHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class SuggestListActivity extends BaseActivity<SuggestListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.btnFollow)
    Button btnFollow;
    @BindView(R.id.etUid)
    EditText etUid;
    @BindView(R.id.btnSearch)
    Button btnSearch;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;
    private boolean follow;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), SuggestListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, long uid) {
        Intent intent = new Intent(from, SuggestListActivity.class);
        intent.putExtra("uid", uid);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, "suggest_list", true);
        long uid = intent.getLongExtra("uid", 0);
        if (uid > 0) {
            etUid.setText(String.valueOf(uid));
        }
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new SuggestAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getListData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        SuggestAdapter suggestAdapter = (SuggestAdapter) adapter;
                        suggestAdapter.goSuggestCommentList(position);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        SuggestAdapter suggestAdapter = (SuggestAdapter) adapter;
                        suggestAdapter.showDelDialog(position);
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        SuggestAdapter suggestAdapter = (SuggestAdapter) adapter;
                        switch (view.getId()) {
                            case R.id.btnStatus:
                                suggestAdapter.showStatusSelectDialog(position);
                                break;
                            case R.id.btnOfficial:
                                suggestAdapter.toggleOfficial(position);
                                break;
                            case R.id.btnTop:
                                suggestAdapter.toggleTop(position);
                                break;
                        }
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        follow = false;
        getListData(false);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.btnFollow, R.id.btnSearch})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnFollow:
                follow = true;
                getListData(false);
                break;
            case R.id.btnSearch:
                follow = false;
                getListData(false);
                break;
        }
    }

    private void getListData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> call;
        if (follow) {
            call = new RetrofitHelper().call(API.class).setSuggestFollowListGet(page);
        } else {
            long uid = 0;
            String sUid = etUid.getText().toString().trim();
            if (StringUtils.isNumber(sUid)) {
                uid = Long.parseLong(sUid);
            }
            if (uid > 0) {
                call = new RetrofitHelper().call(API.class).setSuggestUserListGet(uid, page);
            } else {
                call = new RetrofitHelper().call(API.class).setSuggestListGet(page);
            }
        }
        MaterialDialog loading = null;
        if (!more) {
            loading = getLoading(true);
        }
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Suggest> suggestList = data.getSuggestList();
                recyclerHelper.dataOk(suggestList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
