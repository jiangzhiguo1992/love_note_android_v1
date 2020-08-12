package com.jiangzg.lovenote_admin.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.BroadcastEditActivity;
import com.jiangzg.lovenote_admin.adapter.BroadcastAdapter;
import com.jiangzg.lovenote_admin.base.BaseFragment;
import com.jiangzg.lovenote_admin.domain.Broadcast;
import com.jiangzg.lovenote_admin.domain.Result;
import com.jiangzg.lovenote_admin.helper.API;
import com.jiangzg.lovenote_admin.helper.RecyclerHelper;
import com.jiangzg.lovenote_admin.helper.RetrofitHelper;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class BroadcastFragment extends BaseFragment<BroadcastFragment> {

    @BindView(R.id.btnPush)
    Button btnPush;
    @BindView(R.id.rv)
    RecyclerView rv;

    private RecyclerHelper recyclerHelper;
    private int page;

    public static BroadcastFragment newFragment() {
        Bundle bundle = new Bundle();
        // bundle.putData();
        return BaseFragment.newInstance(BroadcastFragment.class, bundle);
    }

    @Override
    protected int getView(Bundle data) {
        return R.layout.fragment_broadcast;
    }

    @Override
    protected void initView(@Nullable Bundle state) {
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new BroadcastAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getData(true);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        BroadcastAdapter broadcastAdapter = (BroadcastAdapter) adapter;
                        broadcastAdapter.showDeleteDialog(position);
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {
        page = 0;
        getData(false);
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
    }

    @OnClick({R.id.btnPush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnPush: // 发布
                BroadcastEditActivity.goActivity(mFragment);
                break;
        }
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).broadcastListGet(page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<Broadcast> broadcastList = data.getBroadcastList();
                recyclerHelper.dataOk(broadcastList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

}
