package com.jiangzg.lovenote.activity.settings;

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
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.SuggestAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.entity.Help;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Suggest;
import com.jiangzg.lovenote.model.entity.SuggestInfo;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

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
    private Call<Result> call;
    private int page;
    private int searchStatus;
    private int searchKind;
    private SuggestInfo suggestInfo;

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
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.suggest_feedback), true);
        // init
        suggestInfo = SuggestInfo.getInstance();
        List<SuggestInfo.SuggestStatus> suggestStatusList = suggestInfo.getStatusList();
        searchStatus = suggestStatusList.get(0).getStatus();
        List<SuggestInfo.SuggestKind> suggestKindList = suggestInfo.getKindList();
        searchKind = suggestKindList.get(0).getKind();
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initRefresh(srl, false)
                .initAdapter(new SuggestAdapter(mActivity))
                .viewEmpty(mActivity, R.layout.list_empty_grey, true, true)
                .viewHeader(mActivity, R.layout.list_head_suggest_home)
                .viewLoadMore(new RecyclerHelper.MoreGreyView())
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
    protected void initData(Intent intent, Bundle state) {
        page = 0;
        // event
        obListRefresh = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, new Action1<List<Suggest>>() {
            @Override
            public void call(List<Suggest> suggests) {
                if (recyclerHelper == null) return;
                recyclerHelper.dataRefresh();
            }
        });
        obListItemDelete = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_ITEM_DELETE, new Action1<Suggest>() {
            @Override
            public void call(Suggest suggest) {
                if (recyclerHelper == null) return;
                ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), suggest);
            }
        });
        obListItemRefresh = RxBus.register(ConsHelper.EVENT_SUGGEST_LIST_ITEM_REFRESH, new Action1<Suggest>() {
            @Override
            public void call(Suggest suggest) {
                if (recyclerHelper == null) return;
                ListHelper.refreshObjInAdapter(recyclerHelper.getAdapter(), suggest);
            }
        });
        // refresh
        recyclerHelper.dataRefresh();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_REFRESH, obListRefresh);
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_ITEM_DELETE, obListItemDelete);
        RxBus.unregister(ConsHelper.EVENT_SUGGEST_LIST_ITEM_REFRESH, obListItemRefresh);
        RecyclerHelper.release(recyclerHelper);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_top, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_USER_SUGGEST);
                return true;
            case R.id.menuTop: // 返回顶部
                rv.smoothScrollToPosition(0);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // head
    private void initHead() {
        if (recyclerHelper == null) return;
        View head = recyclerHelper.getViewHead();
        CardView cvMy = head.findViewById(R.id.cvMy);
        CardView cvFollow = head.findViewById(R.id.cvFollow);
        CardView cvAdd = head.findViewById(R.id.cvAdd);
        HorizontalScrollView hsvStatus = head.findViewById(R.id.hsvStatus);
        HorizontalScrollView hsvKind = head.findViewById(R.id.hsvKind);
        RadioGroup rgKind = head.findViewById(R.id.rgKind);
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
        hsvKind.setHorizontalScrollBarEnabled(false);
        dp3 = ConvertUtils.dp2px(3);
        dp4 = ConvertUtils.dp2px(4);
        dp5 = ConvertUtils.dp2px(5);
        dp14 = ConvertUtils.dp2px(14);
        List<SuggestInfo.SuggestKind> suggestKindList = suggestInfo.getKindList();
        List<SuggestInfo.SuggestStatus> suggestStatusList = suggestInfo.getStatusList();
        for (int i = 0; i < suggestKindList.size(); i++) {
            SuggestInfo.SuggestKind kind = suggestKindList.get(i);
            RadioButton rb = getKindRadioButton(kind);
            rgKind.addView(rb, i);
        }
        for (int i = 0; i < suggestStatusList.size(); i++) {
            SuggestInfo.SuggestStatus status = suggestStatusList.get(i);
            RadioButton rb = getStatusRadioButton(status);
            rgStatus.addView(rb, i);
        }
        RadioButton child1 = (RadioButton) rgKind.getChildAt(0);
        child1.setChecked(true);
        RadioButton child2 = (RadioButton) rgStatus.getChildAt(0);
        child2.setChecked(true);
    }

    private RadioButton getKindRadioButton(final SuggestInfo.SuggestKind kind) {
        RadioButton button = new RadioButton(mActivity);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.bottomMargin = dp5;
        layoutParams.topMargin = dp5;
        layoutParams.setMarginStart(dp3);
        layoutParams.setMarginEnd(dp3);
        button.setLayoutParams(layoutParams);
        button.setPadding(dp14, dp4, dp14, dp4);
        button.setBackgroundResource(R.drawable.selector_check_stroke_solid_primary_r3);
        button.setButtonDrawable(null);
        button.setElevation(dp3);
        button.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            button.setTextAppearance(R.style.FontWhiteNormal);
        } else {
            button.setTextAppearance(mActivity, R.style.FontWhiteNormal);
        }
        button.setText(kind.getShow());
        ColorStateList colorStateList = ContextCompat.getColorStateList(mActivity, R.color.selector_text_check_primary_white);
        button.setTextColor(colorStateList);
        button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    searchKind = kind.getKind();
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
        button.setBackgroundResource(R.drawable.selector_check_stroke_solid_primary_r3);
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
        call = new RetrofitHelper().call(API.class).setSuggestListGet(searchStatus, searchKind, page);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
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
