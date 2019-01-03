package com.jiangzg.lovenote.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.base.BaseActivity;
import com.jiangzg.lovenote.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.adapter.MatchWifeAdapter;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.CountHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.MediaPickHelper;
import com.jiangzg.lovenote.helper.OssHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.MatchPeriod;
import com.jiangzg.lovenote.model.entity.MatchWork;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class MatchWifeListActivity extends BaseActivity<MatchWifeListActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.llTop)
    LinearLayout llTop;
    @BindView(R.id.llOrder)
    LinearLayout llOrder;
    @BindView(R.id.tvOrder)
    TextView tvOrder;
    @BindView(R.id.llAdd)
    LinearLayout llAdd;

    private MatchPeriod period;
    private boolean showNew;
    private RecyclerHelper recyclerHelper;
    private Call<Result> callGet;
    private Call<Result> callAdd;
    private int page;
    private int orderIndex;

    public static void goActivity(Fragment from, MatchPeriod period) {
        Intent intent = new Intent(from.getActivity(), MatchWifeListActivity.class);
        intent.putExtra("period", period);
        intent.putExtra("showNew", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, MatchPeriod period) {
        Intent intent = new Intent(from, MatchWifeListActivity.class);
        intent.putExtra("period", period);
        intent.putExtra("showNew", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_match_wife_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_wife), true);
        // search
        orderIndex = 0;
        tvOrder.setText(ApiHelper.LIST_MATCH_ORDER_SHOW[orderIndex]);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new GridLayoutManager(mActivity, 2))
                .initRefresh(srl, true)
                .initAdapter(new MatchWifeAdapter(mActivity))
                .viewHeader(mActivity, R.layout.list_head_match_work)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
                .listenerClick(new OnItemClickListener() {
                    @Override
                    public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                        MatchWifeAdapter wifeAdapter = (MatchWifeAdapter) adapter;
                        wifeAdapter.goWifeDetail(position);
                    }
                })
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        ApiHelper.showMatchWorksDeleteDialog(mActivity, adapter, position);
                    }
                })
                .listenerClick(new OnItemChildClickListener() {
                    @Override
                    public void onSimpleItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                        switch (view.getId()) {
                            case R.id.llReport: // 举报
                                ApiHelper.matchReportAdd(adapter, position, true);
                                break;
                            case R.id.llPoint: // 点赞
                                ApiHelper.matchPointToggle(adapter, position, true);
                                break;
                            case R.id.llCoin: // 金币
                                ApiHelper.matchCoinAdd(mActivity, adapter, position);
                                break;
                        }
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        period = intent.getParcelableExtra("period");
        showNew = intent.getBooleanExtra("showNew", false);
        // head
        initHead();
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callAdd);
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        if (requestCode == ConsHelper.REQUEST_PICTURE) {
            // 相册
            File pictureFile = MediaPickHelper.getResultFile(mActivity, data);
            if (FileUtils.isFileEmpty(pictureFile)) {
                ToastUtils.show(getString(R.string.file_no_exits));
                return;
            }
            ossUpload(pictureFile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_MORE_MATCH);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.llTop, R.id.llOrder, R.id.llAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llTop: // 置顶
                rv.smoothScrollToPosition(0);
                break;
            case R.id.llOrder: // 搜索
                showSearchDialog();
                break;
            case R.id.llAdd: // 添加
                goPicture();
                break;
        }
    }

    private void initHead() {
        if (period == null) {
            mActivity.finish();
            return;
        }
        // view
        View head = recyclerHelper.getViewHead();
        CardView root = head.findViewById(R.id.root);
        TextView tvTitle = head.findViewById(R.id.tvTitle);
        TextView tvTime = head.findViewById(R.id.tvTime);
        TextView tvPeriod = head.findViewById(R.id.tvPeriod);
        TextView tvCoin = head.findViewById(R.id.tvCoin);
        TextView tvWorksCount = head.findViewById(R.id.tvWorksCount);
        TextView tvCoinCount = head.findViewById(R.id.tvCoinCount);
        TextView tvPointCount = head.findViewById(R.id.tvPointCount);
        // data
        String title = period.getTitle();
        String start = DateUtils.getString(TimeHelper.getJavaTimeByGo(period.getStartAt()), ConstantUtils.FORMAT_LINE_M_D_H_M);
        String end = DateUtils.getString(TimeHelper.getJavaTimeByGo(period.getEndAt()), ConstantUtils.FORMAT_LINE_M_D_H_M);
        String time = String.format(Locale.getDefault(), getString(R.string.holder_space_line_space_holder), start, end);
        String periodShow = String.format(Locale.getDefault(), getString(R.string.the_holder_period), this.period.getPeriod());
        String coinChange = String.format(Locale.getDefault(), getString(R.string.go_in_award_colon_holder_coin), period.getCoinChange());
        String workCount = String.format(Locale.getDefault(), getString(R.string.total_works_count_colon_holder), CountHelper.getShowCount2Thousand(period.getWorksCount()));
        String coinCount = String.format(Locale.getDefault(), getString(R.string.total_coin_count_colon_holder), CountHelper.getShowCount2Thousand(period.getCoinCount()));
        String pointCount = String.format(Locale.getDefault(), getString(R.string.total_point_count_colon_holder), CountHelper.getShowCount2Thousand(period.getPointCount()));
        // set
        tvTitle.setText(title);
        tvTime.setText(time);
        tvPeriod.setText(periodShow);
        tvCoin.setText(coinChange);
        tvWorksCount.setText(workCount);
        tvCoinCount.setText(coinCount);
        tvPointCount.setText(pointCount);
        // listener
        root.setOnClickListener(v -> MatchWifeActivity.goActivity(mActivity));
    }

    private void getData(final boolean more) {
        if (period == null) {
            srl.setRefreshing(false);
        }
        page = more ? page + 1 : 0;
        // api
        int orderType = ApiHelper.LIST_MATCH_ORDER_TYPE[orderIndex];
        callGet = new RetrofitHelper().call(API.class).moreMatchWordListGet(period.getId(), orderType, page);
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.viewEmptyShow(data.getShow());
                List<MatchWork> matchWorkList = data.getMatchWorkList();
                recyclerHelper.dataOk(matchWorkList, more);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataFail(more, message);
            }
        });
    }

    private void showSearchDialog() {
        String[] newSelectList = ApiHelper.LIST_MATCH_ORDER_SHOW;
        if (!showNew) {
            newSelectList = Arrays.copyOf(newSelectList, newSelectList.length - 1);
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(R.string.select_search_type)
                .items(newSelectList)
                .itemsCallbackSingleChoice(orderIndex, (dialog1, view, which, text) -> {
                    if (recyclerHelper == null) return true;
                    if (which < 0 || which >= ApiHelper.LIST_MATCH_ORDER_TYPE.length) {
                        return true;
                    }
                    orderIndex = which;
                    tvOrder.setText(ApiHelper.LIST_MATCH_ORDER_SHOW[orderIndex]);
                    recyclerHelper.dataRefresh();
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    // 图片获取
    private void goPicture() {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(mActivity);
            return;
        }
        MediaPickHelper.selectImage(mActivity, 1);
    }

    // 上传
    private void ossUpload(File file) {
        if (period == null) return;
        OssHelper.uploadMoreMatch(mActivity, file, new OssHelper.OssUploadCallBack() {
            @Override
            public void success(File source, String ossPath) {
                MatchWork body = new MatchWork();
                body.setMatchPeriodId(period.getId());
                body.setContentImage(ossPath);
                api(body);
            }

            @Override
            public void failure(File source, String errMsg) {
            }
        });
    }

    private void api(MatchWork body) {
        callAdd = new RetrofitHelper().call(API.class).moreMatchWorkAdd(body);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
