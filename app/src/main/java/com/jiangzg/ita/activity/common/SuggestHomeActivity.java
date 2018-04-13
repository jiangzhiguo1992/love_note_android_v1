package com.jiangzg.ita.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.ita.R;
import com.jiangzg.ita.adapter.SuggestListAdapter;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.domain.SuggestInfo;
import com.jiangzg.ita.helper.API;
import com.jiangzg.ita.helper.RecyclerHelper;
import com.jiangzg.ita.helper.RetrofitHelper;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.helper.ViewHelper;
import com.jiangzg.ita.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;

public class SuggestHomeActivity extends BaseActivity<SuggestHomeActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.rv)
    RecyclerView rv;

    private int dp3;
    private int dp4;
    private int dp5;
    private int dp14;

    private RecyclerHelper recyclerHelper;
    private int page = 0;
    private int searchStatus = 0; // 0是所有
    private int searchType = 0; // 0是所有

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SuggestHomeActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_suggest_home;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new SuggestListAdapter(mActivity))
                .viewEmpty(R.layout.list_empty_common, true, true)
                .viewHeader(R.layout.list_head_suggest_home)
                .viewLoadMore(new RecyclerHelper.RecyclerMoreView())
                .setAdapter()
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
                        SuggestListAdapter suggestListAdapter = (SuggestListAdapter) adapter;
                        suggestListAdapter.goSuggestDetail(position);
                    }
                });
        // head
        initHead();
        // menu
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 帮助
                        HelpActivity.goActivity(mActivity, Help.TYPE_SUGGEST_HOME);
                        break;
                    case R.id.menuTop: // 返回顶部
                        rv.smoothScrollToPosition(0);
                        break;
                }
                return true;
            }
        });
    }

    @Override
    protected void initData(Bundle state) {
        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // head
    private void initHead() {
        View head = recyclerHelper.getViewHead();
        CardView cvMy = head.findViewById(R.id.cvMy);
        CardView cvFollow = head.findViewById(R.id.cvFollow);
        CardView cvAdd = head.findViewById(R.id.cvAdd);
        HorizontalScrollView hsvStatus = head.findViewById(R.id.hsvStatus);
        HorizontalScrollView hsvType = head.findViewById(R.id.hsvType);
        RadioGroup rgType = head.findViewById(R.id.rgType);
        RadioGroup rgStatus = head.findViewById(R.id.rgStatus);
        Button btnSearch = head.findViewById(R.id.btnSearch);
        cvMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestListActivity.goActivity(mActivity, SuggestListActivity.ENTRY_MINE);
            }
        });
        cvFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestListActivity.goActivity(mActivity, SuggestListActivity.ENTRY_FOLLOW);
            }
        });
        cvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuggestAddActivity.goActivity(mActivity);
            }
        });
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerHelper.dataRefresh();
            }
        });
        // 动态组件拼装
        hsvStatus.setHorizontalScrollBarEnabled(false);
        hsvType.setHorizontalScrollBarEnabled(false);
        dp3 = ConvertUtils.dp2px(3);
        dp4 = ConvertUtils.dp2px(4);
        dp5 = ConvertUtils.dp2px(5);
        dp14 = ConvertUtils.dp2px(14);
        SuggestInfo suggestInfo = SPHelper.getSuggestInfo();
        List<SuggestInfo.SuggestContentType> suggestContentTypeList = suggestInfo.getSuggestContentTypeList();
        List<SuggestInfo.SuggestStatus> suggestStatusList = suggestInfo.getSuggestStatusList();
        for (int i = 0; i < suggestContentTypeList.size(); i++) {
            SuggestInfo.SuggestContentType contentType = suggestContentTypeList.get(i);
            RadioButton rb = getTypeRadioButton(contentType);
            rgType.addView(rb, i);
        }
        for (int i = 0; i < suggestStatusList.size(); i++) {
            SuggestInfo.SuggestStatus status = suggestStatusList.get(i);
            RadioButton rb = getStatusRadioButton(status);
            rgStatus.addView(rb, i);
        }
        RadioButton child1 = (RadioButton) rgType.getChildAt(0);
        child1.setChecked(true);
        RadioButton child2 = (RadioButton) rgStatus.getChildAt(0);
        child2.setChecked(true);
    }

    private RadioButton getTypeRadioButton(final SuggestInfo.SuggestContentType contentType) {
        RadioButton button = new RadioButton(mActivity);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = dp5;
        layoutParams.topMargin = dp5;
        layoutParams.setMarginStart(dp3);
        layoutParams.setMarginEnd(dp3);
        button.setLayoutParams(layoutParams);
        button.setPadding(dp14, dp4, dp14, dp4);
        button.setBackgroundResource(R.drawable.selector_rb_r3_solid_stroke_primary);
        button.setButtonDrawable(null);
        button.setElevation(dp3);
        button.setGravity(Gravity.CENTER);
        button.setText(contentType.getShow());
        ColorStateList colorStateList = ContextCompat.getColorStateList(mActivity, R.color.selector_text_check_primary_white);
        button.setTextColor(colorStateList);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    searchType = contentType.getContentType();
                }
            }
        });
        return button;
    }

    private RadioButton getStatusRadioButton(final SuggestInfo.SuggestStatus status) {
        RadioButton button = new RadioButton(mActivity);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = dp5;
        layoutParams.topMargin = dp5;
        layoutParams.setMarginStart(dp3);
        layoutParams.setMarginEnd(dp3);
        button.setLayoutParams(layoutParams);
        button.setPadding(dp14, dp4, dp14, dp4);
        button.setBackgroundResource(R.drawable.selector_rb_r3_solid_stroke_primary);
        button.setButtonDrawable(null);
        button.setElevation(dp3);
        button.setGravity(Gravity.CENTER);
        button.setText(status.getShow());
        ColorStateList colorStateList = ContextCompat.getColorStateList(mActivity, R.color.selector_text_check_primary_white);
        button.setTextColor(colorStateList);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    searchStatus = status.getStatus();
                }
            }
        });
        return button;
    }

    public void getData(final boolean more) {
        page = more ? page + 1 : 0;
        // api
        Call<Result> call = new RetrofitHelper().call(API.class).suggestListHomeGet(searchStatus, searchType, page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                long total = data.getTotal();
                List<Suggest> suggestList = data.getSuggestList();
                recyclerHelper.data(suggestList, total, more);
                recyclerHelper.viewEmptyShow(data.getShow());
            }

            @Override
            public void onFailure() {
                srl.setRefreshing(false);
            }
        });
    }

}
