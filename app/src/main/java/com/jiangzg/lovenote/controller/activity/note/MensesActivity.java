package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.WeekView;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Menses2;
import com.jiangzg.lovenote.model.entity.MensesInfo;
import com.jiangzg.lovenote.model.entity.MensesLength;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.CalendarMonthView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class MensesActivity extends BaseActivity<MensesActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvDateShow)
    TextView tvDateShow;
    @BindView(R.id.tvBackCur)
    TextView tvBackCur;
    @BindView(R.id.tvShow)
    TextView tvShow;
    @BindView(R.id.cvMenses)
    CalendarView cvMenses;
    @BindView(R.id.rgUser)
    RadioGroup rgUser;
    @BindView(R.id.rbMe)
    RadioButton rbMe;
    @BindView(R.id.rbTa)
    RadioButton rbTa;
    @BindView(R.id.cvLength)
    CardView cvLength;
    @BindView(R.id.tvLengthCycle)
    TextView tvLengthCycle;
    @BindView(R.id.tvLengthDuration)
    TextView tvLengthDuration;
    @BindView(R.id.llDayInfo)
    LinearLayout llDayInfo; // TODO show
    @BindView(R.id.sMensesStatus)
    Switch sMensesStatus; // TODO check
    @BindView(R.id.cvDayInfo)
    CardView cvDayInfo; // TODO show
    @BindView(R.id.ivBlood1)
    ImageView ivBlood1;
    @BindView(R.id.ivBlood2)
    ImageView ivBlood2;
    @BindView(R.id.ivBlood3)
    ImageView ivBlood3;
    @BindView(R.id.ivPain1)
    ImageView ivPain1;
    @BindView(R.id.ivPain2)
    ImageView ivPain2;
    @BindView(R.id.ivPain3)
    ImageView ivPain3;
    @BindView(R.id.ivMood1)
    ImageView ivMood1;
    @BindView(R.id.ivMood2)
    ImageView ivMood2;
    @BindView(R.id.ivMood3)
    ImageView ivMood3;

    private boolean isMine;
    private MensesInfo mensesInfo;
    private List<Menses2> menses2List;
    private int selectYear, selectMonth, selectDay;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, MensesActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), MensesActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from) {
        Intent intent = new Intent(from, MensesActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_menses;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.menses), true);
        srl.setEnabled(false);
        // calendar样式替换
        cvMenses.setWeekView(WeekView.class);
        cvMenses.setMonthView(CalendarMonthView.class);
        cvMenses.update();
        // calendar监听
        cvMenses.setOnYearChangeListener(year -> {
            if (selectYear == year || cvMenses == null || cvMenses.getVisibility() != View.VISIBLE) {
                return;
            }
            selectYear = year;
            selectMonth = -1;
            selectDay = -1;
            refreshTopDateShow();
        });
        cvMenses.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {
            }

            @Override
            public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean isClick) {
                if (cvMenses == null || cvMenses.getVisibility() != View.VISIBLE) return;
                if (selectYear == calendar.getYear() && selectMonth == calendar.getMonth()) {
                    // 只是选择的同月day
                    selectDay = calendar.getDay();
                    refreshTopDateShow();
                    refreshBottomLeftView();
                    return;
                }
                selectYear = calendar.getYear();
                selectMonth = calendar.getMonth();
                selectDay = calendar.getDay();
                refreshTopDateShow();
                refreshCenterMonthData();
                refreshBottomLeftView();
            }
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<MensesLength> obLengthRefresh = RxBus.register(RxBus.EVENT_MENSES_LENGTH_UPDATE, mensesLength -> {
            refreshCenterMonthData();
            getBottomRightMensesInfoData();
        });
        pushBus(RxBus.EVENT_MENSES_LENGTH_UPDATE, obLengthRefresh);
        // user
        User me = SPHelper.getMe();
        isMine = (me != null && me.getSex() == User.SEX_GIRL);
        initBottomRightUserCheckView();
        // info
        mensesInfo = new MensesInfo();
        mensesInfo.setCanMe(false);
        mensesInfo.setCanTa(false);
        // 设置当前日期
        refreshDateToCurrent();
        // 显示当前数据
        refreshCenterMonthView("");
        refreshBottomRightLengthView();
        // 开始获取数据
        refreshCenterMonthData();
        getBottomRightMensesInfoData();
    }

    @Override
    protected void onFinish(Bundle state) {
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
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_NOTE_MENSES);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (cvMenses != null && cvMenses.isYearSelectLayoutVisible()) {
            cvMenses.closeYearSelectLayout();
            return;
        }
        super.onBackPressed();
    }

    @OnClick({R.id.tvDateShow, R.id.tvBackCur, R.id.cvLength,
            R.id.ivBlood1, R.id.ivBlood2, R.id.ivBlood3,
            R.id.ivPain1, R.id.ivPain2, R.id.ivPain3,
            R.id.ivMood1, R.id.ivMood2, R.id.ivMood3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvDateShow: // 日期显示
                yearShow();
                break;
            case R.id.tvBackCur: // 回到当前
                dateBack();
                break;
            case R.id.cvLength: // 经期/周期设置
                // TODO goEdit
                break;
            case R.id.ivBlood1: // 血量
            case R.id.ivBlood2:
            case R.id.ivBlood3:
            case R.id.ivPain1: // 痛经
            case R.id.ivPain2:
            case R.id.ivPain3:
            case R.id.ivMood1: // 心情
            case R.id.ivMood2:
            case R.id.ivMood3:
                pushMensesDayInfo();
                break;
        }
    }

    private void initBottomRightUserCheckView() {
        if (isMine) {
            rbMe.setChecked(true);
        } else {
            rbTa.setChecked(true);
        }
        rgUser.setOnCheckedChangeListener((group, checkedId) -> {
            isMine = (R.id.rbMe == checkedId);
            refreshBottomRightLengthView();
            refreshCenterMonthData();
        });
    }

    /**
     * **************************************** top ***********************************************
     */
    private void yearShow() {
        if (cvMenses == null) return;
        if (!cvMenses.isYearSelectLayoutVisible()) {
            cvMenses.showYearSelectLayout(selectYear);
        } else {
            cvMenses.closeYearSelectLayout();
        }
    }

    private void dateBack() {
        refreshDateToCurrent();
        refreshCenterMonthData();
    }

    private void refreshDateToCurrent() {
        Calendar calendar = DateUtils.getCurrentCal();
        selectYear = calendar.get(Calendar.YEAR);
        selectMonth = calendar.get(Calendar.MONTH) + 1;
        selectDay = calendar.get(Calendar.DAY_OF_MONTH);
        cvMenses.scrollToCurrent(true);
        // 顶部显示
        refreshTopDateShow();
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

    /**
     * **************************************** top ***********************************************
     */
    private void refreshCenterMonthData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // clear
        if (menses2List != null) {
            menses2List.clear();
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteMenses2ListGetByDate(isMine, selectYear, selectMonth);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                menses2List = data.getMenses2List();
                // view
                refreshCenterMonthView(data.getShow());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void refreshCenterMonthView(String show) {
        // show
        if (!StringUtils.isEmpty(show)) {
            cvMenses.setVisibility(View.INVISIBLE);
            tvShow.setVisibility(View.VISIBLE);
            tvShow.setText(show);
            return;
        }
        cvMenses.setVisibility(View.VISIBLE);
        tvShow.setVisibility(View.GONE);
        // TODO data
        //Map<String, com.haibin.calendarview.Calendar> schemeMap = new HashMap<>();
        //if (mensesList != null && mensesList.size() > 0) {
        //    String come = getString(R.string.come);
        //    String gone = getString(R.string.gone);
        //    for (Menses menses : mensesList) {
        //        if (menses == null) continue;
        //        com.haibin.calendarview.Calendar calendar = CalendarMonthView.getCalendarView(menses.getYear(), menses.getMonthOfYear(), menses.getDayOfMonth());
        //        calendar.setSchemeColor(ContextCompat.getColor(mActivity, ViewUtils.getColorDark(mActivity)));
        //        calendar.setScheme(menses.isStart() ? come : gone);
        //        schemeMap.put(calendar.toString(), calendar);
        //    }
        //}
        //// calendar
        //cvMenses.clearSchemeDate();
        //cvMenses.setSchemeDate(schemeMap);
    }

    /**
     * **************************************** bottom ***********************************************
     */
    private void getBottomRightMensesInfoData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).noteMenses2InfoGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                mensesInfo = data.getMensesInfo();
                // view
                refreshBottomRightLengthView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void refreshBottomRightLengthView() {
        if (mensesInfo == null) {
            cvLength.setVisibility(View.GONE);
            return;
        }
        if (isMine) {
            if (!mensesInfo.isCanMe()) {
                cvLength.setVisibility(View.GONE);
                return;
            }
        } else {
            if (!mensesInfo.isCanTa()) {
                cvLength.setVisibility(View.GONE);
                return;
            }
        }
        MensesLength length = isMine ? mensesInfo.getMensesLengthMe() : mensesInfo.getMensesLengthTa();
        if (length == null) {
            cvLength.setVisibility(View.GONE);
            return;
        }
        cvLength.setVisibility(View.VISIBLE);
        tvLengthCycle.setText(String.format(Locale.getDefault(), getString(R.string.menses_cycle_colon_holder_day), length.getCycleDay()));
        tvLengthDuration.setText(String.format(Locale.getDefault(), getString(R.string.menses_duration_colon_holder_day), length.getDurationDay()));
    }

    private void refreshBottomLeftView() {
        // TODO
    }

    /**
     * **************************************** push ***********************************************
     */
    private void mensesPush(int year, int month, int day) {
        // TODO
        //Menses menses = new Menses();
        //menses.setYear(year);
        //menses.setMonthOfYear(month);
        //menses.setDayOfMonth(day);
        //menses.setStart();
        //// api
        //Call<Result> api = new RetrofitHelper().call(API.class).noteMenses2Add(menses);
        //RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
        //    @Override
        //    public void onResponse(int code, String message, Result.Data data) {
        //        menses2List = data.getMenses2List();
        //        // data
        //        refreshCenterMonthData();
        //        // view
        //        refreshBottomRightLengthView();
        //    }
        //
        //    @Override
        //    public void onFailure(int code, String message, Result.Data data) {
        //    }
        //});
        //pushApi(api);
    }

    private void pushMensesDayInfo() {
        // TODO
    }

}
