package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.adapter.ShyAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Help;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.Shy;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class ShyActivity extends BaseActivity<ShyActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.mcvShy)
    MaterialCalendarView mcvShy;
    @BindView(R.id.tvMonth)
    TextView tvMonth;
    @BindView(R.id.tvDay)
    TextView tvDay;
    @BindView(R.id.cvPush)
    CardView cvPush;
    @BindView(R.id.rv)
    RecyclerView rv;

    private List<Shy> shyList;
    private RecyclerHelper recyclerHelper;
    private Call<Result> callAdd;
    private Call<Result> callListGet;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, ShyActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), ShyActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_shy;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.shy), true);
        srl.setEnabled(false);
        // calendar
        initCalendarView();
        // info
        refreshShyListView();
        // recycler
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new ShyAdapter())
                .setAdapter();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        getShyListData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callListGet);
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
                HelpActivity.goActivity(mActivity, Help.INDEX_NOTE_SHY);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.cvPush)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvPush: // 睡眠
                showDatePicker();
                break;
        }
    }

    private void initCalendarView() {
        ViewHelper.initMonthView(mActivity, mcvShy);
        // 设置滑动选择改变月份事件
        mcvShy.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                widget.setSelectedDate(date);
                getShyListData();
            }
        });
        // 设置点击选择日期改变事件
        mcvShy.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                refreshShyListView();
            }
        });
    }

    private void refreshShyListView() {
        if (recyclerHelper == null || mcvShy == null) return;
        String monthFormat = getString(R.string.current_month_space_holder_space_second);
        String dayFormat = getString(R.string.current_day_space_holder_space_second);
        int monthCount = 0;
        int dayCount = 0;
        List<Shy> dayList = new ArrayList<>();
        if (shyList != null && shyList.size() > 0) {
            monthCount = shyList.size();
            CalendarDay currentDate = mcvShy.getSelectedDate();
            Calendar calToday = DateUtils.getCalendar(currentDate.getDate().getTime());
            int day = calToday.get(Calendar.DAY_OF_MONTH);
            for (Shy shy : shyList) {
                if (shy == null) continue;
                if (day == shy.getDayOfMonth()) {
                    dayList.add(shy);
                    ++dayCount;
                }
            }
        }
        recyclerHelper.dataNew(dayList, 0);
        tvMonth.setText(String.format(Locale.getDefault(), monthFormat, monthCount));
        tvDay.setText(String.format(Locale.getDefault(), dayFormat, dayCount));
    }

    private void getShyListData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        shyList = null;
        refreshShyListView(); // 先清空选择
        CalendarDay selectedDate = mcvShy.getSelectedDate();
        if (selectedDate == null) return;
        int year = selectedDate.getYear();
        int month = selectedDate.getMonth() + 1;
        if (month > 12) {
            month = 1;
        }
        callListGet = new RetrofitHelper().call(API.class).noteShyListGetByDate(year, month);
        RetrofitHelper.enqueue(callListGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                shyList = data.getShyList();
                refreshShyListView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void showDatePicker() {
        DialogHelper.showDateTimePicker(mActivity, DateUtils.getCurrentLong(), new DialogHelper.OnPickListener() {
            @Override
            public void onPick(long time) {
                shyPush(TimeHelper.getGoTimeByJava(time));
            }
        });
    }

    private void shyPush(long happenAt) {
        Shy shy = new Shy();
        shy.setHappenAt(happenAt);
        callAdd = new RetrofitHelper().call(API.class).noteShyAdd(shy);
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                getShyListData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
