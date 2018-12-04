package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.WeekView;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.adapter.ShyAdapter;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.ListHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Shy;
import com.jiangzg.lovenote.view.CalendarMonthView;
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
import rx.functions.Action1;

public class ShyActivity extends BaseActivity<ShyActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvDateShow)
    TextView tvDateShow;
    @BindView(R.id.tvBackCur)
    TextView tvBackCur;
    @BindView(R.id.cvShy)
    CalendarView cvShy;
    @BindView(R.id.tvMonth)
    TextView tvMonth;
    @BindView(R.id.cvPush)
    CardView cvPush;
    @BindView(R.id.rv)
    RecyclerView rv;

    private List<Shy> shyList;
    private RecyclerHelper recyclerHelper;
    private Observable<Shy> obListItemDelete;
    private Call<Result> callAdd;
    private Call<Result> callListGet;
    private int selectYear, selectMonth, selectDay;

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
        // calendar样式替换
        cvShy.setWeekView(WeekView.class);
        cvShy.setMonthView(CalendarMonthView.class);
        cvShy.update();
        // calendar监听
        cvShy.setOnYearChangeListener(new CalendarView.OnYearChangeListener() {
            @Override
            public void onYearChange(int year) {
                if (selectYear == year) return;
                selectYear = year;
                selectMonth = -1;
                selectDay = -1;
                refreshTopDateShow();
            }
        });
        cvShy.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
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
        recyclerHelper = new RecyclerHelper(rv)
                .initLayoutManager(new LinearLayoutManager(mActivity))
                .initAdapter(new ShyAdapter(mActivity))
                .setAdapter()
                .listenerClick(new OnItemLongClickListener() {
                    @Override
                    public void onSimpleItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                        ShyAdapter shyAdapter = (ShyAdapter) adapter;
                        shyAdapter.showDeleteDialog(position);
                    }
                });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obListItemDelete = RxBus.register(ConsHelper.EVENT_SHY_LIST_ITEM_DELETE, new Action1<Shy>() {
            @Override
            public void call(Shy shy) {
                if (recyclerHelper == null) return;
                ListHelper.removeObjInAdapter(recyclerHelper.getAdapter(), shy);
                refreshMonthData();
            }
        });
        // 设置当前日期
        refreshDateToCurrent();
        // 显示当前数据
        refreshMonthSchemeView();
        refreshDayView();
        // 开始获取数据
        refreshMonthData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callListGet);
        RecyclerHelper.release(recyclerHelper);
        RxBus.unregister(ConsHelper.EVENT_SHY_LIST_ITEM_DELETE, obListItemDelete);
    }

    @Override
    public void onBackPressed() {
        if (cvShy != null && cvShy.isYearSelectLayoutVisible()) {
            cvShy.closeYearSelectLayout();
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
            case R.id.cvPush: // 睡眠
                showDatePicker();
                break;
        }
    }

    private void refreshDateToCurrent() {
        Calendar calendar = DateUtils.getCurrentCalendar();
        selectYear = calendar.get(Calendar.YEAR);
        selectMonth = calendar.get(Calendar.MONTH) + 1;
        selectDay = calendar.get(Calendar.DAY_OF_MONTH);
        cvShy.scrollToCurrent(true);
        // 顶部显示
        refreshTopDateShow();
    }

    private void refreshMonthSchemeView() {
        if (cvShy == null) return;
        String monthFormat = getString(R.string.current_month_space_holder_space_second);
        // data
        int monthCount = 0;
        SparseIntArray countArray = new SparseIntArray();
        Map<String, com.haibin.calendarview.Calendar> schemeMap = new HashMap<>();
        if (shyList != null && shyList.size() > 0) {
            monthCount = shyList.size();
            for (Shy shy : shyList) {
                if (shy == null) continue;
                com.haibin.calendarview.Calendar calendar = CalendarMonthView.getCalendarView(shy.getHappenAt());
                int day = calendar.getDay();
                int count = countArray.get(day);
                if (count <= 0) {
                    count = 1;
                    countArray.put(day, count);
                } else {
                    countArray.put(day, ++count);
                }
                calendar.setSchemeColor(ContextCompat.getColor(mActivity, ViewHelper.getColorDark(mActivity)));
                calendar.setScheme(String.valueOf(count));
                schemeMap.put(calendar.toString(), calendar);
            }
        }
        // calendar
        cvShy.clearSchemeDate();
        cvShy.setSchemeDate(schemeMap);
        // view
        tvMonth.setText(String.format(Locale.getDefault(), monthFormat, monthCount));
    }

    private void refreshDayView() {
        if (recyclerHelper == null) return;
        // data
        List<Shy> dayList = new ArrayList<>();
        if (shyList != null && shyList.size() > 0) {
            for (Shy shy : shyList) {
                if (shy == null) continue;
                if (selectDay == shy.getDayOfMonth()) {
                    dayList.add(shy);
                }
            }
        }
        // view
        recyclerHelper.dataNew(dayList, 0);
    }

    private void yearShow() {
        if (cvShy == null) return;
        if (!cvShy.isYearSelectLayoutVisible()) {
            cvShy.showYearSelectLayout(selectYear);
        } else {
            cvShy.closeYearSelectLayout();
        }
    }

    private void dateBack() {
        refreshDateToCurrent();
        refreshMonthData();
    }

    private void refreshMonthData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // clear (data + view)
        shyList = null;
        refreshDayView();
        // call
        callListGet = new RetrofitHelper().call(API.class).noteShyListGetByDate(selectYear, selectMonth);
        RetrofitHelper.enqueue(callListGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                shyList = data.getShyList();
                // view
                refreshMonthSchemeView();
                refreshDayView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
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
                refreshMonthData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
