package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.WeekView;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.controller.adapter.note.SleepAdapter;
import com.jiangzg.lovenote.helper.common.ListHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.RecyclerHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Sleep;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.CalendarMonthView;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class SleepActivity extends BaseActivity<SleepActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvDateShow)
    TextView tvDateShow;
    @BindView(R.id.tvBackCur)
    TextView tvBackCur;
    @BindView(R.id.cvSleep)
    CalendarView cvSleep;
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
    private List<Sleep> sleepList;
    private RecyclerHelper recyclerLeft;
    private RecyclerHelper recyclerRight;
    private int selectYear, selectMonth, selectDay;

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

    public static void goActivity(Context from) {
        Intent intent = new Intent(from, SleepActivity.class);
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
        // calendar样式替换
        cvSleep.setWeekView(WeekView.class);
        cvSleep.setMonthView(CalendarMonthView.class);
        cvSleep.update();
        // calendar监听
        cvSleep.setOnYearChangeListener(year -> {
            if (selectYear == year) return;
            selectYear = year;
            selectMonth = -1;
            selectDay = -1;
            refreshTopDateShow();
        });
        cvSleep.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {
            }

            @Override
            public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean isClick) {
                if (selectYear == calendar.getYear() && selectMonth == calendar.getMonth()) {
                    // 只是选择的同月day
                    selectDay = calendar.getDay();
                    refreshTopDateShow();
                    refreshDayView();
                    return;
                }
                selectYear = calendar.getYear();
                selectMonth = calendar.getMonth();
                selectDay = calendar.getDay();
                refreshTopDateShow();
                refreshMonthData();
                refreshDayView();
            }
        });
        // recycler
        recyclerRight = new RecyclerHelper(rvRight)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new SleepAdapter(mActivity))
                .viewAnim()
                .setAdapter()
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        SleepAdapter sleepAdapter = (SleepAdapter) adapter;
                        sleepAdapter.showDeleteDialog(position);
                    }
                });
        recyclerLeft = new RecyclerHelper(rvLeft)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new SleepAdapter(mActivity))
                .viewAnim()
                .setAdapter()
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        SleepAdapter sleepAdapter = (SleepAdapter) adapter;
                        sleepAdapter.showDeleteDialog(position);
                    }
                });
        // avatar
        User me = SPHelper.getMe();
        User ta = SPHelper.getTa();
        String myAvatar = UserHelper.getMyAvatar(me);
        String taAvatar = UserHelper.getTaAvatar(me);
        ivAvatarRight.setData(myAvatar, me);
        ivAvatarLeft.setData(taAvatar, ta);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<Sleep> obListItemDelete = RxBus.register(RxBus.EVENT_SLEEP_LIST_ITEM_DELETE, sleep -> {
            if (recyclerLeft != null) {
                ListHelper.removeObjInAdapter(recyclerLeft.getAdapter(), sleep);
            }
            if (recyclerRight != null) {
                ListHelper.removeObjInAdapter(recyclerRight.getAdapter(), sleep);
            }
            getLatestData();
            refreshMonthData();
        });
        pushBus(RxBus.EVENT_SLEEP_LIST_ITEM_DELETE, obListItemDelete);
        // 设置当前日期
        refreshDateToCurrent();
        // 显示当前数据
        refreshMonthView();
        refreshDayView();
        refreshLatestView();
        // 开始获取数据
        getLatestData();
        refreshMonthData();
    }

    @Override
    protected void onFinish(Bundle state) {
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
            case R.id.menuHelp: // 帮助文档
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_NOTE_SLEEP);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (cvSleep != null && cvSleep.isYearSelectLayoutVisible()) {
            cvSleep.closeYearSelectLayout();
            return;
        }
        super.onBackPressed();
    }

    @OnClick({R.id.tvDateShow, R.id.tvBackCur, R.id.cvPush})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvDateShow: // 日期显示
                yearShow();
                break;
            case R.id.tvBackCur: // 回到当前
                dateBack();
                break;
            case R.id.cvPush: // 发布
                sleepPush();
                break;
        }
    }

    private void refreshDateToCurrent() {
        Calendar calendar = DateUtils.getCurrentCal();
        selectYear = calendar.get(Calendar.YEAR);
        selectMonth = calendar.get(Calendar.MONTH) + 1;
        selectDay = calendar.get(Calendar.DAY_OF_MONTH);
        cvSleep.scrollToCurrent(true);
        // 顶部显示
        refreshTopDateShow();
    }

    private void refreshMonthView() {
        if (cvSleep == null) return;
        // data
        SparseIntArray countArray = new SparseIntArray();
        Map<String, com.haibin.calendarview.Calendar> schemeMap = new HashMap<>();
        if (sleepList != null && sleepList.size() > 0) {
            for (Sleep sleep : sleepList) {
                if (sleep == null) continue;
                com.haibin.calendarview.Calendar calendar = CalendarMonthView.getCalendarView(sleep.getYear(), sleep.getMonthOfYear(), sleep.getDayOfMonth());
                int day = calendar.getDay();
                int count = countArray.get(day);
                if (count <= 0) {
                    count = 1;
                    countArray.put(day, count);
                } else {
                    countArray.put(day, ++count);
                }
                calendar.setSchemeColor(ContextCompat.getColor(mActivity, ViewUtils.getColorDark(mActivity)));
                calendar.setScheme(String.valueOf(count));
                schemeMap.put(calendar.toString(), calendar);
            }
        }
        // calendar
        cvSleep.clearSchemeDate();
        cvSleep.setSchemeDate(schemeMap);
    }

    private void refreshDayView() {
        List<Sleep> selectLeftList = new ArrayList<>();
        List<Sleep> selectRightList = new ArrayList<>();
        if (sleepList != null && sleepList.size() > 0) {
            for (Sleep s : sleepList) {
                if (s == null) continue;
                if (s.getDayOfMonth() == selectDay) {
                    if (s.isMine()) { // 我的
                        selectRightList.add(s);
                    } else { // TA的
                        selectLeftList.add(s);
                    }
                }
            }
        }
        recyclerRight.dataNew(selectRightList, 0);
        recyclerLeft.dataNew(selectLeftList, 0);
    }

    private void yearShow() {
        if (cvSleep == null) return;
        if (!cvSleep.isYearSelectLayoutVisible()) {
            cvSleep.showYearSelectLayout(selectYear);
        } else {
            cvSleep.closeYearSelectLayout();
        }
    }

    private void dateBack() {
        refreshDateToCurrent();
        refreshMonthData();
    }

    private void refreshTopDateShow() {
        String show = "";
        if (selectYear > 0) {
            String year = String.valueOf(selectYear);
            String month = String.valueOf(selectMonth);
            if (selectMonth >= 0) {
                show = String.format(Locale.getDefault(), getString(R.string.holder_space_line_space_holder), year, month);
            } else {
                show = year;
            }
        }
        tvDateShow.setText(show);
    }

    private void refreshLatestView() {
        String sleepBtnShow = getString(R.string.good_night);
        String sleepShowMe = getString(R.string.now_no);
        if (sleepMe != null && sleepMe.getId() > 0) {
            long continueTime = DateUtils.getCurrentLong() - TimeHelper.getJavaTimeByGo(sleepMe.getCreateAt());
            TimeUnit timeUnit = TimeUnit.get(continueTime);
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
            TimeUnit timeUnit = TimeUnit.get(continueTime);
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

    private void getLatestData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).noteSleepLatestGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                sleepMe = data.getSleepMe();
                sleepTa = data.getSleepTa();
                // view
                refreshLatestView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void refreshMonthData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // clear (data + view)
        sleepList = null;
        refreshDayView();
        // call
        Call<Result> api = new RetrofitHelper().call(API.class).noteSleepListGetByDate(selectYear, selectMonth);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                sleepList = data.getSleepList();
                // view
                refreshMonthView();
                refreshDayView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void sleepPush() {
        Sleep sleep = new Sleep();
        sleep.setSleep(sleepMe == null || !sleepMe.isSleep());
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteSleepAdd(sleep);
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                sleepMe = data.getSleep();
                // view
                refreshLatestView();
                // data
                refreshMonthData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
