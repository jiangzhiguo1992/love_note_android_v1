package com.jiangzg.mianmian.activity.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.HelpActivity;
import com.jiangzg.mianmian.adapter.DiaryAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class AlbumListActivity extends BaseActivity<AlbumListActivity> {

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
    private int page;
    private int searchType = ApiHelper.LIST_CP;
    //private Observable<List<Diary>> obListRefresh;
    //private Observable<Diary> obListItemRefresh;
    //private Observable<Diary> obListItemDelete;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AlbumListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
        return R.layout.activity_album_list;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.album), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new GridLayoutManager(mActivity, 2))
                .initRefresh(srl, false)
                //.initAdapter(new DiaryAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_white, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
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
                        //DiaryAdapter diaryAdapter = (DiaryAdapter) adapter;
                        //diaryAdapter.goDiaryDetail(position);
                    }
                });
    }

    @Override
    protected void initData(Bundle state) {

        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.TYPE_ALBUM_LIST);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llSearch, R.id.llAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llSearch: // 搜索
                showSearchDialog();
                break;
            case R.id.llAdd: // 添加
                //DiaryEditActivity.goActivity(mActivity);
                break;
        }
    }

    private void getData(final boolean more) {
        page = more ? page + 1 : 0;
        tvSearch.setText(ApiHelper.LIST_SHOW[searchType]);
        // api
        //Call<Result> call = new RetrofitHelper().call(API.class).diaryListGet(searchType, page);
        //RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
        //    @Override
        //    public void onResponse(int code, String message, Result.Data data) {
        //        recyclerHelper.viewEmptyShow(data.getShow());
        //        long total = data.getTotal();
        //        List<Diary> diaryList = data.getDiaryList();
        //        recyclerHelper.dataOk(diaryList, total, more);
        //        // searchShow
        //        String searchShow = ApiHelper.LIST_SHOW[searchType] + String.format(Locale.getDefault(), getString(R.string.space_bracket_holder), total);
        //        tvSearch.setText(searchShow);
        //    }
        //
        //    @Override
        //    public void onFailure(String errMsg) {
        //        recyclerHelper.dataFail(more, errMsg);
        //    }
        //});
    }

    private void showSearchDialog() {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.choose_search_type)
                .items(ApiHelper.LIST_SHOW)
                .itemsCallbackSingleChoice(searchType, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        searchType = which;
                        getData(false);
                        DialogUtils.dismiss(dialog);
                        return true;
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

}
