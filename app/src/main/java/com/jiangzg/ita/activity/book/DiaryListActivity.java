package com.jiangzg.ita.activity.book;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.adapter.DiaryAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Diary;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.ApiHelper;
import com.jiangzg.ita.helper.ConsHelper;
import com.jiangzg.ita.helper.RecyclerHelper;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.helper.RxBus;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

public class DiaryListActivity extends BaseActivity<DiaryListActivity> {

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
    private int page = 0;
    private int searchType = ApiHelper.LIST_CP;
    private Observable<List<Suggest>> observableListRefresh;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), DiaryListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_diary_list;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.diary), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new DiaryAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_common, true, true)
                .viewLoadMore(new RecyclerHelper.RecyclerMoreView())
                .listenerRefresh(new RecyclerHelper.RefreshListener() {
                    @Override
                    public void onRefresh() {
                        getData(false);
                    }
                })
                .listenerMore(new RecyclerHelper.MoreListener() {
                    @Override
                    public void onMore(int currentCount) {
                        getData(true);
                    }
                })
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        DiaryAdapter diaryAdapter = (DiaryAdapter) adapter;
                        diaryAdapter.goDiaryDetail(position);
                    }
                });
        // menu
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 帮助
                        HelpActivity.goActivity(mActivity, Help.TYPE_DIARY_LIST);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void initData(Bundle state) {
        observableListRefresh = RxBus.register(ConsHelper.EVENT_DIARY_LIST_REFRESH, new Action1<List<Suggest>>() {
            @Override
            public void call(List<Suggest> suggests) {
                recyclerHelper.dataRefresh();
            }
        });
        // TODO RxEvent 参照 suggestList
        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO RxEvent 参照 suggestList
        RxBus.unregister(ConsHelper.EVENT_DIARY_LIST_REFRESH, observableListRefresh);
        //RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, observableListItemDelete);
        //RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, observableListItemRefresh);
    }

    @OnClick({R.id.llSearch, R.id.llAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llSearch: // 搜索

                break;
            case R.id.llAdd: // 添加

                break;
        }
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).diaryListGet(searchType, page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                recyclerHelper.viewEmptyShow(data.getShow());
                long total = data.getTotal();
                List<Diary> diaryList = data.getDiaryList();
                recyclerHelper.dataOk(diaryList, total, more);
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(more, errMsg);
            }
        });
    }

}
