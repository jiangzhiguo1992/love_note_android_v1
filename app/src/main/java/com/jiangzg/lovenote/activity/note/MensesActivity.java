package com.jiangzg.lovenote.activity.note;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.WeekView;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.time.TimeUnit;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.entity.Menses;
import com.jiangzg.lovenote.model.entity.MensesInfo;
import com.jiangzg.lovenote.model.entity.Result;
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

public class MensesActivity extends BaseActivity<MensesActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvDateShow)
    TextView tvDateShow;
    @BindView(R.id.tvBackCur)
    TextView tvBackCur;
    @BindView(R.id.cvMenses)
    CalendarView cvMenses;
    @BindView(R.id.tvShow)
    TextView tvShow;
    @BindView(R.id.tvTip)
    TextView tvTip;
    @BindView(R.id.rgUser)
    RadioGroup rgUser;
    @BindView(R.id.rbMe)
    RadioButton rbMe;
    @BindView(R.id.rbTa)
    RadioButton rbTa;
    @BindView(R.id.cvPush)
    CardView cvPush;
    @BindView(R.id.tvPush)
    TextView tvPush;

    private boolean isMine;
    private MensesInfo mensesInfo;
    private Menses mensesMe;
    private Menses mensesTa;
    private List<Menses> mensesList;
    private Call<Result> callAdd;
    private Call<Result> callGet;
    private Call<Result> callListGet;
    private int selectYear, selectMonth;

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
        // data
        isMine = (SPHelper.getMe().getSex() == User.SEX_GIRL);
        mensesInfo = new MensesInfo();
        mensesInfo.setCanMe(false);
        mensesInfo.setCanTa(false);
        // check
        initCheckView();
        // calendar样式替换
        cvMenses.setWeekView(WeekView.class);
        cvMenses.setMonthView(CalendarMonthView.class);
        cvMenses.update();
        // calendar监听
        cvMenses.setOnYearChangeListener(new CalendarView.OnYearChangeListener() {
            @Override
            public void onYearChange(int year) {
                if (selectYear == year) return;
                selectYear = year;
                selectMonth = -1;
                refreshTopDateShow();
            }
        });
        cvMenses.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(com.haibin.calendarview.Calendar calendar) {
            }

            @Override
            public void onCalendarSelect(com.haibin.calendarview.Calendar calendar, boolean isClick) {
                if (selectYear == calendar.getYear() && selectMonth == calendar.getMonth()) {
                    return;
                }
                selectYear = calendar.getYear();
                selectMonth = calendar.getMonth();
                refreshTopDateShow();
                refreshMonthData();
                refreshBottomView();
            }
        });
        cvMenses.setOnCalendarLongClickListener(new CalendarView.OnCalendarLongClickListener() {
            @Override
            public void onCalendarLongClickOutOfRange(com.haibin.calendarview.Calendar calendar) {
            }

            @Override
            public void onCalendarLongClick(com.haibin.calendarview.Calendar calendar) {
                if (mensesInfo == null || !mensesInfo.isCanMe()) return;
                int year = calendar.getYear();
                int month = calendar.getMonth();
                int day = calendar.getDay();
                showDeleteDialog(year, month, day);
            }
        });
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // 设置当前日期
        refreshDateToCurrent();
        // 显示当前数据
        refreshMonthView("");
        refreshBottomView();
        // 开始获取数据
        getLatestData();
        refreshMonthData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callGet);
        RetrofitHelper.cancel(callListGet);
    }

    @Override
    public void onBackPressed() {
        if (cvMenses != null && cvMenses.isYearSelectLayoutVisible()) {
            cvMenses.closeYearSelectLayout();
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
                mensesPush();
                break;
        }
    }

    private void refreshDateToCurrent() {
        Calendar calendar = DateUtils.getCurrentCalendar();
        selectYear = calendar.get(Calendar.YEAR);
        selectMonth = calendar.get(Calendar.MONTH) + 1;
        cvMenses.scrollToCurrent(true);
        // 顶部显示
        refreshTopDateShow();
    }

    private void refreshMonthView(String show) {
        // show
        if (!StringUtils.isEmpty(show)) {
            cvMenses.setVisibility(View.GONE);
            tvShow.setVisibility(View.VISIBLE);
            tvShow.setText(show);
            return;
        }
        cvMenses.setVisibility(View.VISIBLE);
        tvShow.setVisibility(View.GONE);
        // data
        Map<String, com.haibin.calendarview.Calendar> schemeMap = new HashMap<>();
        if (mensesList != null && mensesList.size() > 0) {
            String come = getString(R.string.come);
            String gone = getString(R.string.gone);
            for (Menses menses : mensesList) {
                if (menses == null) continue;
                com.haibin.calendarview.Calendar calendar = CalendarMonthView.getCalendarView(menses.getYear(), menses.getMonthOfYear(), menses.getDayOfMonth());
                calendar.setSchemeColor(ContextCompat.getColor(mActivity, ViewHelper.getColorDark(mActivity)));
                calendar.setScheme(menses.isStart() ? come : gone);
                schemeMap.put(calendar.toString(), calendar);
            }
        }
        // calendar
        cvMenses.clearSchemeDate();
        cvMenses.setSchemeDate(schemeMap);
    }

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

    private void initCheckView() {
        if (isMine) {
            rbMe.setChecked(true);
        } else {
            rbTa.setChecked(true);
        }
        rgUser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                isMine = (R.id.rbMe == checkedId);
                refreshBottomView();
                refreshMonthData();
            }
        });
    }

    private void refreshBottomView() {
        // tip
        if (isMine) {
            if (!mensesInfo.isCanMe() || mensesMe == null || mensesMe.getId() <= 0) {
                tvTip.setVisibility(View.INVISIBLE);
            } else {
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(getTipShow(mensesMe));
            }
        } else {
            if (!mensesInfo.isCanTa() || mensesTa == null || mensesTa.getId() <= 0) {
                tvTip.setVisibility(View.INVISIBLE);
            } else {
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(getTipShow(mensesTa));
            }
        }
        // push
        if (!mensesInfo.isCanMe()) {
            cvPush.setVisibility(View.GONE);
        } else {
            if (mensesMe == null) {
                mensesMe = new Menses();
                mensesMe.setStart(false);
            }
            cvPush.setVisibility(View.VISIBLE);
            String pushShow = mensesMe.isStart() ? getString(R.string.menses_gone) : getString(R.string.menses_come);
            tvPush.setText(pushShow);
        }
    }

    private String getTipShow(Menses menses) {
        if (menses == null) return "";
        String tipShow;
        boolean start = menses.isStart();
        long createAt = TimeHelper.getJavaTimeByGo(menses.getCreateAt());
        if (start) {
            // 已开始
            TimeUnit timeUnit = TimeUnit.convertTime2Unit(DateUtils.getCurrentLong() - createAt);
            String timeSHow = timeUnit.getAllShow(false, false, true, true, true, true,
                    R.string.year, R.string.month, R.string.dayT,
                    R.string.hour_short, R.string.minute_short, R.string.second);
            tipShow = String.format(Locale.getDefault(), getString(R.string.already_start_space_holder), timeSHow);
        } else {
            long between = DateUtils.getCurrentLong() - createAt;
            if (between > ConstantUtils.DAY * 30) {
                // 已推迟
                TimeUnit timeUnit = TimeUnit.convertTime2Unit(between - ConstantUtils.DAY * 30);
                String timeSHow = timeUnit.getMaxShow(false, true, true, true, true, true,
                        R.string.year, R.string.month, R.string.dayT,
                        R.string.hour_short, R.string.minute_short, R.string.second);
                tipShow = String.format(Locale.getDefault(), getString(R.string.already_delay_space_holder), timeSHow);
            } else {
                // 倒计时
                TimeUnit timeUnit = TimeUnit.convertTime2Unit(ConstantUtils.DAY * 30 - between);
                String timeSHow = timeUnit.getMaxShow(false, true, true, true, true, true,
                        R.string.year, R.string.month, R.string.dayT,
                        R.string.hour_short, R.string.minute_short, R.string.second);
                tipShow = String.format(Locale.getDefault(), getString(R.string.count_down_space_holder), timeSHow);
            }
        }
        return tipShow;
    }

    private void getLatestData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).noteMensesLatestGet();
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                mensesInfo = data.getMensesInfo();
                mensesMe = data.getMensesMe();
                mensesTa = data.getMensesTa();
                // view
                refreshBottomView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void refreshMonthData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // clear
        if (mensesList != null) {
            mensesList.clear();
        }
        // call
        callListGet = new RetrofitHelper().call(API.class).noteMensesListGetByDate(isMine, selectYear, selectMonth);
        RetrofitHelper.enqueue(callListGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                mensesList = data.getMensesList();
                // view
                refreshMonthView(data.getShow());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void mensesPush() {
        Menses menses = new Menses();
        callAdd = new RetrofitHelper().call(API.class).noteMensesAdd(menses);
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                mensesMe = data.getMenses();
                // data
                refreshMonthData();
                // view
                refreshBottomView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    public void showDeleteDialog(final int year, final int month, final int day) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_the_day_menses)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteApi(year, month, day);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi(int year, int month, int day) {
        Call<Result> call = new RetrofitHelper().call(API.class).noteMensesDel(year, month, day);
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                getLatestData();
                refreshMonthData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
