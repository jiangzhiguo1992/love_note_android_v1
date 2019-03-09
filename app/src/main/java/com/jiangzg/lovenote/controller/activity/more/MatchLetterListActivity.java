package com.jiangzg.lovenote.controller.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.system.InputUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.adapter.more.MatchLetterAdapter;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.CountHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.MatchPeriod;
import com.jiangzg.lovenote.model.entity.MatchWork;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Arrays;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import retrofit2.Call;

public class MatchLetterListActivity extends BaseActivity<MatchLetterListActivity> {

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
    @BindView(R.id.rlAdd)
    RelativeLayout rlAdd;
    @BindView(R.id.ivAddClose)
    ImageView ivAddClose;
    @BindView(R.id.tvAddLimit)
    TextView tvAddLimit;
    @BindView(R.id.ivAddCommit)
    ImageView ivAddCommit;
    @BindView(R.id.etContent)
    EditText etContent;

    private MatchPeriod period;
    private boolean showNew;
    private RecyclerHelper recyclerHelper;
    private BottomSheetBehavior behaviorAdd;
    private int page = 0, orderIndex, limitContentLength;

    public static void goActivity(Fragment from, MatchPeriod period) {
        Intent intent = new Intent(from.getActivity(), MatchLetterListActivity.class);
        intent.putExtra("period", period);
        intent.putExtra("showNew", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from, MatchPeriod period) {
        Intent intent = new Intent(from, MatchLetterListActivity.class);
        intent.putExtra("period", period);
        intent.putExtra("showNew", false);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_match_letter_list;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.nav_letter), true);
        // search
        orderIndex = 0;
        tvOrder.setText(ApiHelper.LIST_MATCH_ORDER_SHOW[orderIndex]);
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, true)
                .initAdapter(new MatchLetterAdapter(mActivity))
                .viewHeader(mActivity, R.layout.list_head_match_work)
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
                .viewAnim()
                .setAdapter()
                .listenerRefresh(() -> getData(false))
                .listenerMore(currentCount -> getData(true))
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
                                ApiHelper.matchReportAdd(mActivity, adapter, position, true);
                                break;
                            case R.id.llPoint: // 点赞
                                ApiHelper.matchPointToggle(mActivity, adapter, position, true);
                                break;
                            case R.id.llCoin: // 金币
                                ApiHelper.matchCoinAdd(mActivity, adapter, position);
                                break;
                        }
                    }
                });
        // comment
        addShow(false);
        // content 防止开始显示错误
        etContent.setText("");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        period = intent.getParcelableExtra("period");
        showNew = intent.getBooleanExtra("showNew", false);
        // head
        initHead();
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
    public void onBackPressed() {
        if (behaviorAdd.getState() != BottomSheetBehavior.STATE_HIDDEN) {
            behaviorAdd.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            super.onBackPressed();
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

    @OnTextChanged({R.id.etContent})
    public void afterTextChanged(Editable s) {
        onContentInput(s.toString());
    }

    @OnClick({R.id.llTop, R.id.llOrder, R.id.llAdd, R.id.ivAddClose, R.id.ivAddCommit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.llTop: // 置顶
                rv.smoothScrollToPosition(0);
                break;
            case R.id.llOrder: // 搜索
                showSearchDialog();
                break;
            case R.id.llAdd: // 添加
                if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
                    CouplePairActivity.goActivity(mActivity);
                    return;
                }
                addShow(true);
                break;
            case R.id.ivAddClose: // 评论关闭
                addShow(false);
                break;
            case R.id.ivAddCommit: // 评论提交
                push();
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
        String start = DateUtils.getStr(TimeHelper.getJavaTimeByGo(period.getStartAt()), DateUtils.FORMAT_LINE_M_D_H_M);
        String end = DateUtils.getStr(TimeHelper.getJavaTimeByGo(period.getEndAt()), DateUtils.FORMAT_LINE_M_D_H_M);
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
        root.setOnClickListener(v -> MatchLetterActivity.goActivity(mActivity));
    }

    private void getData(final boolean more) {
        if (period == null) {
            srl.setRefreshing(false);
        }
        page = more ? page + 1 : 0;
        int orderType = ApiHelper.LIST_MATCH_ORDER_TYPE[orderIndex];
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).moreMatchWordListGet(period.getId(), orderType, page);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataOk(data.getShow(), data.getMatchWorkList(), more);
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
                    orderIndex = which;
                    tvOrder.setText(ApiHelper.LIST_MATCH_ORDER_SHOW[orderIndex]);
                    recyclerHelper.dataRefresh();
                    DialogUtils.dismiss(dialog1);
                    return true;
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void addShow(boolean show) {
        //if (!show) InputUtils.hideSoftInput(etBreakContent);
        if (behaviorAdd == null) {
            behaviorAdd = BottomSheetBehavior.from(rlAdd);
        }
        int state = show ? BottomSheetBehavior.STATE_COLLAPSED : BottomSheetBehavior.STATE_HIDDEN;
        behaviorAdd.setState(state);
    }

    private void onContentInput(String input) {
        if (limitContentLength <= 0) {
            limitContentLength = SPHelper.getLimit().getMatchWorkTitleLength();
        }
        int length = input.length();
        if (length > limitContentLength) {
            CharSequence charSequence = input.subSequence(0, limitContentLength);
            etContent.setText(charSequence);
            etContent.setSelection(charSequence.length());
            length = charSequence.length();
        }
        String limitShow = String.format(Locale.getDefault(), getString(R.string.holder_sprit_holder), length, limitContentLength);
        tvAddLimit.setText(limitShow);
    }

    private void push() {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(mActivity);
            return;
        }
        if (period == null) return;
        String content = etContent.getText().toString().trim();
        if (StringUtils.isEmpty(content)) {
            ToastUtils.show(etContent.getHint());
            return;
        }
        InputUtils.hideSoftInput(etContent);
        MatchWork body = new MatchWork();
        body.setMatchPeriodId(period.getId());
        body.setTitle(content);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).moreMatchWorkAdd(body);
        RetrofitHelper.enqueue(api, getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                etContent.setText("");
                addShow(false);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
