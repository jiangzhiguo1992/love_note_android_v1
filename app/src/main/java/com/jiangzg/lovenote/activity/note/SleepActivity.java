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
import com.jiangzg.base.time.TimeUnit;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.adapter.SleepAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Help;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.Sleep;
import com.jiangzg.lovenote.domain.User;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.CalendarView;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class SleepActivity extends BaseActivity<SleepActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.mcvSleep)
    MaterialCalendarView mcvSleep;
    @BindView(R.id.rvLeft)
    RecyclerView rvLeft;
    @BindView(R.id.rvRight)
    RecyclerView rvRight;
    @BindView(R.id.cvPush)
    CardView cvPush;
    @BindView(R.id.tvPush)
    TextView tvPush;
    @BindView(R.id.ivAvatarLeft)
    FrescoAvatarView ivAvatarLeft;
    @BindView(R.id.ivAvatarRight)
    FrescoAvatarView ivAvatarRight;
    @BindView(R.id.tvStateLeft)
    TextView tvStateLeft;
    @BindView(R.id.tvStateRight)
    TextView tvStateRight;

    private Sleep sleepMe;
    private Sleep sleepTa;
    private RecyclerHelper recyclerLeft;
    private RecyclerHelper recyclerRight;
    private List<Sleep> sleepList;
    private Call<Result> callAdd;
    private Call<Result> callGet;
    private Call<Result> callListGet;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, SleepActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), SleepActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_sleep;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.sleep), true);
        srl.setEnabled(false);
        // calendar
        initCalendarView();
        // data
        refreshSleepListView();
        // avatar
        User me = SPHelper.getMe();
        String myAvatar = me.getMyAvatarInCp();
        String taAvatarInCp = me.getTaAvatarInCp();
        ivAvatarRight.setData(myAvatar);
        ivAvatarLeft.setData(taAvatarInCp);
        // latest
        refreshSleepLatestView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        getLatestData();
        getSleepListData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callListGet);
        RecyclerHelper.release(recyclerLeft);
        RecyclerHelper.release(recyclerRight);
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
                HelpActivity.goActivity(mActivity, Help.INDEX_NOTE_SLEEP);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.cvPush)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvPush: // 发布
                sleepPush();
                break;
        }
    }

    private void initCalendarView() {
        CalendarView.initMonthView(mActivity, mcvSleep);
        // 设置滑动选择改变月份事件
        mcvSleep.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                widget.setSelectedDate(date);
                getSleepListData();
            }
        });
        // 设置点击选择日期改变事件
        mcvSleep.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                refreshSleepListView();
            }
        });
    }

    private void refreshSleepLatestView() {
        String sleepBtnShow = getString(R.string.good_night);
        String sleepShowMe = getString(R.string.now_no);
        if (sleepMe != null && sleepMe.getId() > 0) {
            long continueTime = DateUtils.getCurrentLong() - TimeHelper.getJavaTimeByGo(sleepMe.getCreateAt());
            TimeUnit timeUnit = TimeUnit.convertTime2Unit(continueTime);
            String timeShow = timeUnit.getMaxShow(true, true, true, true, true, true, R.string.year, R.string.month, R.string.dayT, R.string.hour_short, R.string.minute_short, R.string.second);
            if (!sleepMe.isSleep()) {
                sleepShowMe = String.format(Locale.getDefault(), getString(R.string.already_wake_space_holder), timeShow);
            } else {
                sleepBtnShow = getString(R.string.i_am_wake);
                sleepShowMe = String.format(Locale.getDefault(), getString(R.string.already_sleep_space_holder), timeShow);
            }
        }
        String sleetShowTa = getString(R.string.now_no);
        if (sleepTa != null && sleepTa.getId() > 0) {
            long continueTime = DateUtils.getCurrentLong() - TimeHelper.getJavaTimeByGo(sleepTa.getCreateAt());
            TimeUnit timeUnit = TimeUnit.convertTime2Unit(continueTime);
            String timeShow = timeUnit.getMaxShow(true, true, true, true, true, true, R.string.year, R.string.month, R.string.dayT, R.string.hour_short, R.string.minute_short, R.string.second);
            if (!sleepTa.isSleep()) {
                sleetShowTa = String.format(Locale.getDefault(), getString(R.string.already_wake_space_holder), timeShow);
            } else {
                sleetShowTa = String.format(Locale.getDefault(), getString(R.string.already_sleep_space_holder), timeShow);
            }
        }
        tvPush.setText(sleepBtnShow);
        tvStateRight.setText(sleepShowMe);
        tvStateLeft.setText(sleetShowTa);
    }

    private void refreshSleepListView() {
        rvRight.setVisibility(View.INVISIBLE);
        rvLeft.setVisibility(View.INVISIBLE);
        // recycler
        if (recyclerRight == null) {
            recyclerRight = new RecyclerHelper(rvRight)
                    .initLayoutManager(new LinearLayoutManager(mActivity))
                    .initAdapter(new SleepAdapter(mActivity))
                    .setAdapter();
        }
        if (recyclerLeft == null) {
            recyclerLeft = new RecyclerHelper(rvLeft)
                    .initLayoutManager(new LinearLayoutManager(mActivity))
                    .initAdapter(new SleepAdapter(mActivity))
                    .setAdapter();
        }
        // date
        CalendarDay selectedDate = mcvSleep.getSelectedDate();
        int day = selectedDate.getDay();
        // view
        List<Sleep> selectLeftList = new ArrayList<>();
        List<Sleep> selectRightList = new ArrayList<>();
        if (sleepList != null && sleepList.size() > 0) {
            for (Sleep s : sleepList) {
                if (s == null) continue;
                if (s.getDayOfMonth() == day) {
                    if (s.isMine()) { // 我的
                        selectRightList.add(s);
                    } else { // TA的
                        selectLeftList.add(s);
                    }
                }
            }
        }
        if (selectRightList.size() > 0) {
            rvRight.setVisibility(View.VISIBLE);
            recyclerRight.dataNew(selectRightList, 0);
        }
        if (selectLeftList.size() > 0) {
            rvLeft.setVisibility(View.VISIBLE);
            recyclerLeft.dataNew(selectLeftList, 0);
        }
    }

    private void getLatestData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).noteSleepLatestGet();
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                sleepMe = data.getSleepMe();
                sleepTa = data.getSleepTa();
                refreshSleepLatestView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void getSleepListData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        sleepList = null;
        refreshSleepListView(); // 先清空选择
        CalendarDay selectedDate = mcvSleep.getSelectedDate();
        if (selectedDate == null) return;
        int year = selectedDate.getYear();
        int month = selectedDate.getMonth() + 1;
        if (month > 12) {
            month = 1;
        }
        callListGet = new RetrofitHelper().call(API.class).noteSleepListGetByDate(year, month);
        RetrofitHelper.enqueue(callListGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                sleepList = data.getSleepList();
                refreshSleepListView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void sleepPush() {
        Sleep sleep = new Sleep();
        sleep.setSleep(sleepMe == null || !sleepMe.isSleep());
        callAdd = new RetrofitHelper().call(API.class).noteSleepAdd(sleep);
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                sleepMe = data.getSleep();
                // view
                refreshSleepLatestView();
                // data
                getSleepListData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
