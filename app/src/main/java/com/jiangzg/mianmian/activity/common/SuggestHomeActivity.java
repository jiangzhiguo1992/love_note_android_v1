package com.jiangzg.mianmian.activity.common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
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
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.adapter.SuggestAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.Suggest;
import com.jiangzg.mianmian.domain.SuggestInfo;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.ListHelper;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;

import java.util.List;

import butterknife.BindView;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

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
    private Observable<List<Suggest>> obListRefresh;
    private Observable<Suggest> obListItemDelete;
    private Observable<Suggest> obListItemRefresh;
    private int page;
    private int searchStatus;
    private int searchType;
    private SuggestInfo suggestInfo;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SuggestHomeActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        page = 0;
        return R.layout.activity_suggest_home;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // suggest
        suggestInfo = SPHelper.getSuggestInfo();
        // recycler
        recyclerHelper = new RecyclerHelper(mActivity)
                .initRecycler(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new SuggestAdapter(mActivity))
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
                        SuggestAdapter suggestAdapter = (SuggestAdapter) adapter;
                        suggestAdapter.goSuggestDetail(position);
                    }
                });
        // head
        initHead();
    }

    @Override
    protected void initData(Bundle state) {
        // search
        List<SuggestInfo.SuggestStatus> suggestStatusList = suggestInfo.getSuggestStatusList();
        if (suggestStatusList.size() > 0) {
            searchStatus = suggestStatusList.get(0).getStatus();
        }
        List<SuggestInfo.SuggestContentType> suggestContentTypeList = suggestInfo.getSuggestContentTypeList();
        if (suggestContentTypeList.size() > 0) {
            searchType = suggestContentTypeList.get(0).getContentType();
        }
        // event
        obListRefresh = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, new Action1<List<Suggest>>() {
            @Override
            public void call(List<Suggest> suggests) {
                recyclerHelper.dataRefresh();
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_ITEM_DELETE, new Action1<Suggest>() {
            @Override
            public void call(Suggest suggest) {
                ListHelper.removeIndexInAdapter(recyclerHelper.getAdapter(), suggest);
            }
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_ITEM_REFRESH, new Action1<Suggest>() {
            @Override
            public void call(Suggest suggest) {
                ListHelper.refreshIndexInAdapter(recyclerHelper.getAdapter(), suggest);
            }
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_ITEM_REFRESH, obListItemRefresh);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.TYPE_SUGGEST_HOME);
                return true;
            case R.id.menuTop: // 返回顶部
                rv.smoothScrollToPosition(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.setTextAppearance(R.style.FontWhiteNormal);
        } else {
            button.setTextAppearance(mActivity, R.style.FontWhiteNormal);
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.setTextAppearance(R.style.FontWhiteNormal);
        } else {
            button.setTextAppearance(mActivity, R.style.FontWhiteNormal);
        }
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
                recyclerHelper.viewEmptyShow(data.getShow());
                long total = data.getTotal();
                List<Suggest> suggestList = data.getSuggestList();
                recyclerHelper.dataOk(suggestList, total, more);
            }

            @Override
            public void onFailure(String errMsg) {
                recyclerHelper.dataFail(more, errMsg);
            }
        });
    }

}
