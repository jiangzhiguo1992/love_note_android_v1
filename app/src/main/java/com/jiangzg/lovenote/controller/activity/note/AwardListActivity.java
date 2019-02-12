package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
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

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.adapter.note.AwardAdapter;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Award;
import com.jiangzg.lovenote.model.entity.AwardScore;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class AwardListActivity extends BaseActivity<AwardListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.tvScoreMe)
    TextView tvScoreMe;
    @BindView(R.id.tvScoreTa)
    TextView tvScoreTa;
    @BindView(R.id.tvSearch)
    TextView tvSearch;
    @BindView(R.id.llSearch)
    LinearLayout llSearch;
    @BindView(R.id.llRule)
    LinearLayout llRule;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private RecyclerHelper recyclerHelper;
    private int page = 0;
    private int searchIndex;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, AwardListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), AwardListActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_award_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.award), true);
        // search
        searchIndex = 0;
        tvSearch.setText(ApiHelper.LIST_NOTE_SHOW[searchIndex]);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new AwardAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        AwardAdapter awardAdapter = (AwardAdapter) adapter;
                        awardAdapter.showDeleteDialog(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<List<Award>> obListRefresh = RxBus.register(RxBus.EVENT_AWARD_LIST_REFRESH, awardList -> {
            if (recyclerHelper == null) return;
            recyclerHelper.dataRefresh();
        });
        pushBus(RxBus.EVENT_AWARD_LIST_REFRESH, obListRefresh);
        Observable<Award> obListItemDelete = RxBus.register(RxBus.EVENT_AWARD_LIST_ITEM_DELETE, award -> {
            if (recyclerHelper == null) return;
            ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), award);
            refreshScoreData();
        });
        pushBus(RxBus.EVENT_AWARD_LIST_ITEM_DELETE, obListItemDelete);
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
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
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_NOTE_AWARD);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llSearch, R.id.llRule, R.id.llAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llSearch: // 搜索
                showSearchDialog();
                break;
            case R.id.llRule: // 搜索
                AwardRuleListActivity.goActivity(mActivity);
                break;
            case R.id.llAdd: // 添加
                AwardEditActivity.goActivity(mActivity);
                break;
        }
    }

    private void getData(final boolean more) {
        if (!more) refreshScoreData(); // 加载分数
        page = more ? page + 1 : 0;
        int searchType = ApiHelper.LIST_NOTE_TYPE[searchIndex];
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteAwardListGet(searchType, page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataOk(data.getShow(), data.getAwardList(), more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
        pushApi(api);
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

    private void refreshScoreData() {
        Call<Result> api = new RetrofitHelper().call(API.class).noteAwardScoreGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                AwardScore awardScoreMe = data.getAwardScoreMe();
                AwardScore awardScoreTa = data.getAwardScoreTa();
                String scoreMe = "0";
                if (awardScoreMe != null) {
                    long totalScore = awardScoreMe.getTotalScore();
                    scoreMe = String.valueOf(totalScore);
                    if (totalScore > 0) {
                        scoreMe = "+" + scoreMe;
                    }
                }
                String scoreTa = "0";
                if (awardScoreTa != null) {
                    long totalScore = awardScoreTa.getTotalScore();
                    scoreTa = String.valueOf(totalScore);
                    if (totalScore > 0) {
                        scoreTa = "+" + scoreTa;
                    }
                }
                tvScoreMe.setText(scoreMe);
                tvScoreTa.setText(scoreTa);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
