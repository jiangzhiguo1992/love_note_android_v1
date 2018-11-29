package com.jiangzg.lovenote.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.WeekView;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Help;
import com.jiangzg.lovenote.model.entity.Result;
import com.jiangzg.lovenote.model.entity.Sign;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.CalendarMonthView;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class SignActivity extends BaseActivity<SignActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.tvDateShow)
    TextView tvDateShow;
    @BindView(R.id.tvBackCur)
    TextView tvBackCur;
    @BindView(R.id.cvSign)
    CalendarView cvSign;
    @BindView(R.id.tvContinue)
    TextView tvContinue;
    @BindView(R.id.tvState)
    TextView tvState;
    @BindView(R.id.cvState)
    CardView cvState;
    @BindView(R.id.ivAvatarLeft)
    FrescoAvatarView ivAvatarLeft;
    @BindView(R.id.tvCountLeft)
    TextView tvCountLeft;
    @BindView(R.id.ivAvatarRight)
    FrescoAvatarView ivAvatarRight;
    @BindView(R.id.tvCountRight)
    TextView tvCountRight;

    private Sign today;
    private List<Sign> signList;
    private Call<Result> callAdd;
    private Call<Result> callListGet;
    private int selectYear, selectMonth, selectDay;

    public static void goActivity(Fragment from) {
        if (Couple.isBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), SignActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_sign;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.sign), true);
        srl.setEnabled(false);
        // calendar样式替换
        cvSign.setWeekView(WeekView.class);
        cvSign.setMonthView(CalendarMonthView.class);
        cvSign.update();
        // calendar监听
        cvSign.setOnYearChangeListener(new CalendarView.OnYearChangeListener() {
            @Override
            public void onYearChange(int year) {
                if (selectYear == year) return;
                selectYear = year;
                selectMonth = -1;
                selectDay = -1;
                refreshTopDateShow();
            }
        });
        cvSign.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
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
        // avatar
        User me = SPHelper.getMe();
        String myAvatar = me.getMyAvatarInCp();
        String taAvatarInCp = me.getTaAvatarInCp();
        ivAvatarRight.setData(myAvatar);
        ivAvatarLeft.setData(taAvatarInCp);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // 设置当前日期
        refreshDateToCurrent();
        // 显示当前数据
        refreshMonthView();
        refreshDayView();
        // 开始获取数据
        refreshMonthData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callListGet);
    }

    @Override
    public void onBackPressed() {
        if (cvSign != null && cvSign.isYearSelectLayoutVisible()) {
            cvSign.closeYearSelectLayout();
            return;
        }
        super.onBackPressed();
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
                HelpActivity.goActivity(mActivity, Help.INDEX_MORE_SIGN);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.tvDateShow, R.id.tvBackCur, R.id.cvState})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvDateShow: // 日期显示
                yearShow();
                break;
            case R.id.tvBackCur: // 回到当前
                dateBack();
                break;
            case R.id.cvState: // 发布
                signPush();
                break;
        }
    }

    private void refreshDateToCurrent() {
        Calendar calendar = DateUtils.getCurrentCalendar();
        selectYear = calendar.get(Calendar.YEAR);
        selectMonth = calendar.get(Calendar.MONTH) + 1;
        selectDay = calendar.get(Calendar.DAY_OF_MONTH);
        cvSign.scrollToCurrent(true);
        // 顶部显示
        refreshTopDateShow();
    }

    private void refreshMonthView() {
        if (cvSign == null) return;
        // data
        User ta = SPHelper.getTa();
        String meShow = mActivity.getString(R.string.me);
        String heShow = mActivity.getString(R.string.he);
        String sheShow = mActivity.getString(R.string.she);
        String taShow = mActivity.getString(R.string.ta);
        int rightCount = 0, leftCount = 0;
        Map<String, com.haibin.calendarview.Calendar> schemeMap = new HashMap<>();
        if (signList != null && signList.size() > 0) {
            Calendar c = DateUtils.getCurrentCalendar();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DAY_OF_MONTH);
            for (Sign sign : signList) {
                if (sign == null) continue;
                boolean mine = sign.isMine();
                // count
                if (mine) {
                    ++rightCount;
                } else {
                    ++leftCount;
                }
                // today
                if (today == null && sign.getYear() == year && sign.getMonthOfYear() == month && sign.getDayOfMonth() == day) {
                    today = sign;
                    tvContinue.setText(String.format(Locale.getDefault(), getString(R.string.continue_sign_holder_day), today.getContinueDay()));
                }
                // scheme
                com.haibin.calendarview.Calendar calendar = CalendarMonthView.getCalendarView(sign.getYear(), sign.getMonthOfYear(), sign.getDayOfMonth());
                String scheme = mine ? meShow : (ta == null ? taShow : (ta.getSex() == User.SEX_BOY ? heShow : sheShow));
                calendar.setSchemeColor(ContextCompat.getColor(mActivity, ViewHelper.getColorDark(mActivity)));
                calendar.setScheme(String.valueOf(scheme));
                schemeMap.put(calendar.toString(), calendar);
            }
        }
        // calendar
        cvSign.clearSchemeDate();
        cvSign.setSchemeDate(schemeMap);
        // view
        tvCountLeft.setText(String.format(Locale.getDefault(), getString(R.string.to_month_sign_holder_count), leftCount));
        tvCountRight.setText(String.format(Locale.getDefault(), getString(R.string.to_month_sign_holder_count), rightCount));
    }

    private void refreshDayView() {
        if (cvSign == null) return;
        // data
        String signShow = null;
        if (signList != null && signList.size() > 0) {
            for (Sign s : signList) {
                if (s == null) continue;
                if (s.getYear() == selectYear && s.getMonthOfYear() == selectMonth && s.getDayOfMonth() == selectDay) {
                    signShow = DateUtils.getString(TimeHelper.getJavaTimeByGo(s.getCreateAt()), ConstantUtils.FORMAT_H_M);
                }
            }
        }
        if (StringUtils.isEmpty(signShow)) {
            Calendar cal = DateUtils.getCurrentCalendar();
            if (today == null || (cal.get(Calendar.YEAR) == selectYear
                    && cal.get(Calendar.MONTH) + 1 == selectMonth
                    && cal.get(Calendar.DAY_OF_MONTH) == selectDay)) {
                signShow = getString(R.string.sign);
            } else {
                signShow = getString(R.string.now_no);
            }
        }
        // view
        tvState.setText(signShow);
    }

    private void yearShow() {
        if (cvSign == null) return;
        if (!cvSign.isYearSelectLayoutVisible()) {
            cvSign.showYearSelectLayout(selectYear);
        } else {
            cvSign.closeYearSelectLayout();
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

    private void refreshMonthData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // clear (data + view)
        signList = null;
        refreshDayView();
        // call
        callListGet = new RetrofitHelper().call(API.class).moreSignDateGet(selectYear, selectMonth);
        RetrofitHelper.enqueue(callListGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                signList = data.getSignList();
                // view
                refreshMonthView();
                refreshDayView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void signPush() {
        callAdd = new RetrofitHelper().call(API.class).moreSignAdd();
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                today = data.getSign();
                if (today != null) {
                    tvContinue.setText(String.format(Locale.getDefault(), getString(R.string.continue_sign_holder_day), today.getContinueDay()));
                }
                // view
                refreshDayView();
                // data
                refreshMonthData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
