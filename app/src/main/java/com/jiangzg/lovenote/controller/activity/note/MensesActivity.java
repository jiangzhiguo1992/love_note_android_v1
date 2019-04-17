package com.jiangzg.lovenote.controller.activity.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Menses;
import com.jiangzg.lovenote.model.entity.Menses2;
import com.jiangzg.lovenote.model.entity.MensesDay;
import com.jiangzg.lovenote.model.entity.MensesInfo;
import com.jiangzg.lovenote.model.entity.MensesLength;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.CalendarMonthView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    @BindView(R.id.cvDayInfo)
    CardView cvDayInfo;
    @BindView(R.id.sMensesStatus)
    Switch sMensesStatus;
    @BindView(R.id.sMensesEnd)
    Switch sMensesEnd;
    @BindView(R.id.tvForecast)
    TextView tvForecast;
    @BindView(R.id.llDayInfo)
    LinearLayout llDayInfo;
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
    private ColorStateList colorGreyStateList, colorPrimaryStateList;

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
        // color
        int colorGrey = ContextCompat.getColor(mActivity, R.color.icon_grey);
        int colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
        colorGreyStateList = ColorStateList.valueOf(colorGrey);
        colorPrimaryStateList = ColorStateList.valueOf(colorPrimary);
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
                    refreshBottomDayInfoView();
                    return;
                }
                selectYear = calendar.getYear();
                selectMonth = calendar.getMonth();
                selectDay = calendar.getDay();
                refreshTopDateShow();
                refreshCenterMonthData();
            }
        });
        // switch
        sMensesStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!sMensesStatus.isEnabled()) return;
            mensesPush(selectYear, selectMonth, selectDay, isChecked);
        });
        sMensesEnd.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!sMensesEnd.isEnabled() || !isChecked) return;
            mensesPush(selectYear, selectMonth, selectDay, false);
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<MensesInfo> obLengthRefresh = RxBus.register(RxBus.EVENT_MENSES_INFO_UPDATE, mensesInfo -> {
            refreshCenterMonthData();
            refreshBottomMensesInfoData();
        });
        pushBus(RxBus.EVENT_MENSES_INFO_UPDATE, obLengthRefresh);
        // info
        mensesInfo = new MensesInfo();
        mensesInfo.setCanMe(false);
        mensesInfo.setCanTa(false);
        // user
        initBottomUserView();
        // 设置当前日期
        refreshDateToCurrent();
        // 显示当前数据
        refreshCenterMonthView("");
        refreshBottomMensesInfoView();
        // 开始获取数据
        refreshCenterMonthData();
        refreshBottomMensesInfoData();
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
                MensesInfoEditActivity.goActivity(mActivity, mensesInfo);
                break;
            case R.id.ivBlood1: // 血量
                ivBlood1.setImageTintList(colorPrimaryStateList);
                ivBlood2.setImageTintList(colorGreyStateList);
                ivBlood3.setImageTintList(colorGreyStateList);
                pushMensesDay();
                break;
            case R.id.ivBlood2:
                ivBlood1.setImageTintList(colorPrimaryStateList);
                ivBlood2.setImageTintList(colorPrimaryStateList);
                ivBlood3.setImageTintList(colorGreyStateList);
                pushMensesDay();
                break;
            case R.id.ivBlood3:
                ivBlood1.setImageTintList(colorPrimaryStateList);
                ivBlood2.setImageTintList(colorPrimaryStateList);
                ivBlood3.setImageTintList(colorPrimaryStateList);
                pushMensesDay();
                break;
            case R.id.ivPain1: // 痛经
                ivPain1.setImageTintList(colorPrimaryStateList);
                ivPain2.setImageTintList(colorGreyStateList);
                ivPain3.setImageTintList(colorGreyStateList);
                pushMensesDay();
                break;
            case R.id.ivPain2:
                ivPain1.setImageTintList(colorPrimaryStateList);
                ivPain2.setImageTintList(colorPrimaryStateList);
                ivPain3.setImageTintList(colorGreyStateList);
                pushMensesDay();
                break;
            case R.id.ivPain3:
                ivPain1.setImageTintList(colorPrimaryStateList);
                ivPain2.setImageTintList(colorPrimaryStateList);
                ivPain3.setImageTintList(colorPrimaryStateList);
                pushMensesDay();
                break;
            case R.id.ivMood1: // 心情
                ivMood1.setImageTintList(colorPrimaryStateList);
                ivMood2.setImageTintList(colorGreyStateList);
                ivMood3.setImageTintList(colorGreyStateList);
                pushMensesDay();
                break;
            case R.id.ivMood2:
                ivMood1.setImageTintList(colorGreyStateList);
                ivMood2.setImageTintList(colorPrimaryStateList);
                ivMood3.setImageTintList(colorGreyStateList);
                pushMensesDay();
                break;
            case R.id.ivMood3:
                ivMood1.setImageTintList(colorGreyStateList);
                ivMood2.setImageTintList(colorGreyStateList);
                ivMood3.setImageTintList(colorPrimaryStateList);
                pushMensesDay();
                break;
        }
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
            if (selectMonth >= 0) {
                show = String.format(Locale.getDefault(), getString(R.string.holder_month_space_holder), selectMonth, selectYear);
            } else {
                show = String.valueOf(selectYear);
            }
        }
        tvDateShow.setText(show);
    }

    /**
     * **************************************** center ***********************************************
     */
    private void refreshCenterMonthData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // clear
        if (menses2List != null) {
            menses2List.clear();
        }
        refreshBottomDayInfoView();
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteMenses2ListGetByDate(isMine, selectYear, selectMonth);
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                menses2List = data.getMenses2List();
                // view
                refreshCenterMonthView(data.getShow());
                refreshBottomDayInfoView();
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
            tvShow.setVisibility(View.VISIBLE);
            tvShow.setText(show);
            cvMenses.setVisibility(View.INVISIBLE);
            return;
        }
        tvShow.setVisibility(View.GONE);
        cvMenses.setVisibility(View.VISIBLE);
        // data
        Map<String, com.haibin.calendarview.Calendar> schemeMap = new HashMap<>();
        long dayTimestamp = TimeUnit.DAY / TimeUnit.SEC;
        if (menses2List != null && menses2List.size() > 0) {
            for (Menses2 menses2 : menses2List) {
                if (menses2 == null) continue;
                // 姨妈期
                for (long timestamp = menses2.getStartAt(); timestamp <= menses2.getEndAt(); timestamp = timestamp + dayTimestamp) {
                    Calendar cal = DateUtils.getCal(TimeHelper.getJavaTimeByGo(timestamp));
                    int index = (int) ((timestamp - menses2.getStartAt()) / dayTimestamp);
                    // scheme
                    com.haibin.calendarview.Calendar calendar = CalendarMonthView.getCalendarView(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
                    calendar.setSchemeColor(ContextCompat.getColor(mActivity, menses2.isReal() ? ViewUtils.getColorDark(mActivity) : R.color.icon_grey));
                    calendar.setScheme(String.valueOf(index + 1));
                    schemeMap.put(calendar.toString(), calendar);
                }
                // 安全期
                // 危险期
                // 排卵日
            }
        }
        // calendar
        cvMenses.clearSchemeDate();
        cvMenses.setSchemeDate(schemeMap);
    }

    /**
     * **************************************** bottom ***********************************************
     */
    private void initBottomUserView() {
        User me = SPHelper.getMe();
        isMine = (me != null && me.getSex() == User.SEX_GIRL);
        if (isMine) {
            rbMe.setChecked(true);
        } else {
            rbTa.setChecked(true);
        }
        rgUser.setOnCheckedChangeListener((group, checkedId) -> {
            isMine = (R.id.rbMe == checkedId);
            refreshBottomMensesInfoView();
            refreshCenterMonthData();
        });
    }

    private void refreshBottomMensesInfoData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).noteMensesInfoGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                mensesInfo = data.getMensesInfo();
                // view
                refreshBottomMensesInfoView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void refreshBottomMensesInfoView() {
        if (mensesInfo == null) {
            cvLength.setVisibility(View.GONE);
            cvDayInfo.setVisibility(View.GONE);
            return;
        }
        if (isMine) {
            if (!mensesInfo.isCanMe()) {
                cvLength.setVisibility(View.GONE);
                cvDayInfo.setVisibility(View.GONE);
                return;
            }
        } else {
            if (!mensesInfo.isCanTa()) {
                cvLength.setVisibility(View.GONE);
                cvDayInfo.setVisibility(View.GONE);
                return;
            }
        }
        // dayInfo
        cvDayInfo.setVisibility(View.VISIBLE);
        // length
        MensesLength length = isMine ? mensesInfo.getMensesLengthMe() : mensesInfo.getMensesLengthTa();
        if (length == null) {
            cvLength.setVisibility(View.GONE);
            return;
        }
        cvLength.setVisibility(View.VISIBLE);
        tvLengthCycle.setText(String.format(Locale.getDefault(), getString(R.string.menses_cycle_colon_holder_day), length.getCycleDay()));
        tvLengthDuration.setText(String.format(Locale.getDefault(), getString(R.string.menses_duration_colon_holder_day), length.getDurationDay()));
    }

    private void refreshBottomDayInfoView() {
        boolean isProgress = false, isForecast = false;
        MensesDay mensesDay = null;
        if (menses2List != null && menses2List.size() > 0) {
            for (Menses2 menses2 : menses2List) {
                if (menses2 == null) continue;
                // 先找出所属menses2
                Calendar cStart = DateUtils.getCurrentCal();
                cStart.set(menses2.getStartYear(), menses2.getStartMonthOfYear(), menses2.getStartDayOfMonth());
                Calendar cEnd = DateUtils.getCurrentCal();
                cEnd.set(menses2.getEndYear(), menses2.getEndMonthOfYear(), menses2.getEndDayOfMonth());
                Calendar cSelect = DateUtils.getCurrentCal();
                cSelect.set(selectYear, selectMonth, selectDay);
                if (cSelect.getTimeInMillis() >= cStart.getTimeInMillis() && cSelect.getTimeInMillis() <= cEnd.getTimeInMillis()) {
                    // menses
                    isProgress = menses2.isReal();
                    isForecast = !menses2.isReal();
                    // dayInfo
                    List<MensesDay> dayInfoList = menses2.getMensesDayList();
                    if (dayInfoList == null || dayInfoList.size() <= 0) continue;
                    for (MensesDay dayInfo : dayInfoList) {
                        if (dayInfo == null) continue;
                        if (selectYear == dayInfo.getYear() && selectMonth == dayInfo.getMonthOfYear() && selectDay == dayInfo.getDayOfMonth()) {
                            mensesDay = dayInfo;
                            break;
                        }
                    }
                    break;
                }
            }
        }
        // view
        sMensesStatus.setEnabled(false);
        sMensesStatus.setChecked(isProgress);
        sMensesStatus.setEnabled(true);
        sMensesEnd.setEnabled(false);
        sMensesEnd.setChecked(false);
        sMensesEnd.setEnabled(true);
        tvForecast.setVisibility(isForecast ? View.VISIBLE : View.GONE);
        if (!isProgress) {
            llDayInfo.setVisibility(View.GONE);
            return;
        }
        llDayInfo.setVisibility(View.VISIBLE);
        // blood
        ivBlood1.setImageTintList(colorGreyStateList);
        ivBlood2.setImageTintList(colorGreyStateList);
        ivBlood3.setImageTintList(colorGreyStateList);
        int blood = mensesDay == null ? 0 : mensesDay.getBlood();
        if (blood > 0) {
            ivBlood1.setImageTintList(colorPrimaryStateList);
        }
        if (blood > 1) {
            ivBlood2.setImageTintList(colorPrimaryStateList);
        }
        if (blood > 2) {
            ivBlood3.setImageTintList(colorPrimaryStateList);
        }
        // pain
        ivPain1.setImageTintList(colorGreyStateList);
        ivPain2.setImageTintList(colorGreyStateList);
        ivPain3.setImageTintList(colorGreyStateList);
        int pain = mensesDay == null ? 0 : mensesDay.getPain();
        if (pain > 0) {
            ivPain1.setImageTintList(colorPrimaryStateList);
        }
        if (pain > 1) {
            ivPain2.setImageTintList(colorPrimaryStateList);
        }
        if (pain > 2) {
            ivPain3.setImageTintList(colorPrimaryStateList);
        }
        // mood
        ivMood1.setImageTintList(colorGreyStateList);
        ivMood2.setImageTintList(colorGreyStateList);
        ivMood3.setImageTintList(colorGreyStateList);
        int mood = mensesDay == null ? 0 : mensesDay.getMood();
        if (mood == 1) {
            ivMood1.setImageTintList(colorPrimaryStateList);
        } else {
            ivMood1.setImageTintList(colorGreyStateList);
        }
        if (mood == 2) {
            ivMood2.setImageTintList(colorPrimaryStateList);
        } else {
            ivMood2.setImageTintList(colorGreyStateList);
        }
        if (mood == 3) {
            ivMood3.setImageTintList(colorPrimaryStateList);
        } else {
            ivMood3.setImageTintList(colorGreyStateList);
        }
    }

    /**
     * **************************************** push ***********************************************
     */
    private void mensesPush(int year, int month, int day, boolean come) {
        Menses menses = new Menses();
        menses.setMe(isMine);
        menses.setYear(year);
        menses.setMonthOfYear(month);
        menses.setDayOfMonth(day);
        menses.setStart(come);
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteMenses2Add(menses);
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // data
                refreshCenterMonthData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

    private void pushMensesDay() {
        MensesDay dayInfo = new MensesDay();
        dayInfo.setYear(selectYear);
        dayInfo.setMonthOfYear(selectMonth);
        dayInfo.setDayOfMonth(selectDay);
        if (ivBlood3.getImageTintList() == colorPrimaryStateList) {
            dayInfo.setBlood(3);
        } else if (ivBlood2.getImageTintList() == colorPrimaryStateList) {
            dayInfo.setBlood(2);
        } else if (ivBlood1.getImageTintList() == colorPrimaryStateList) {
            dayInfo.setBlood(1);
        } else {
            dayInfo.setBlood(0);
        }
        if (ivPain3.getImageTintList() == colorPrimaryStateList) {
            dayInfo.setPain(3);
        } else if (ivPain2.getImageTintList() == colorPrimaryStateList) {
            dayInfo.setPain(2);
        } else if (ivPain1.getImageTintList() == colorPrimaryStateList) {
            dayInfo.setPain(1);
        } else {
            dayInfo.setPain(0);
        }
        if (ivMood3.getImageTintList() == colorPrimaryStateList) {
            dayInfo.setMood(3);
        } else if (ivMood2.getImageTintList() == colorPrimaryStateList) {
            dayInfo.setMood(2);
        } else if (ivMood1.getImageTintList() == colorPrimaryStateList) {
            dayInfo.setMood(1);
        } else {
            dayInfo.setMood(0);
        }
        // api
        Call<Result> api = new RetrofitHelper().call(API.class).noteMensesDayAdd(dayInfo);
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // data
                refreshCenterMonthData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        pushApi(api);
    }

}
