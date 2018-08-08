package com.jiangzg.lovenote.activity.more;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Help;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.Sign;
import com.jiangzg.lovenote.domain.User;
import com.jiangzg.lovenote.helper.API;
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
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class SignActivity extends BaseActivity<SignActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.mcvSign)
    MaterialCalendarView mcvSign;
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
    private int clickYear, clickMonth, clickDay;
    private CalendarView.ClickDecorator clickDecorator;
    private CalendarView.SelectedDecorator selectedDecorator;

    public static void goActivity(Fragment from) {
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
        // avatar
        User me = SPHelper.getMe();
        String myAvatar = me.getMyAvatarInCp();
        String taAvatarInCp = me.getTaAvatarInCp();
        ivAvatarRight.setData(myAvatar);
        ivAvatarLeft.setData(taAvatarInCp);
        // calendar
        initCalendarView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        getSignMonthData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callListGet);
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

    @OnClick(R.id.cvState)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvState: // 发布
                signPush();
                break;
        }
    }

    private void initCalendarView() {
        Calendar calendar = DateUtils.getCurrentCalendar();
        clickYear = calendar.get(Calendar.YEAR);
        clickMonth = calendar.get(Calendar.MONTH) + 1;
        clickDay = -1;
        CalendarView.initMonthView(mActivity, mcvSign, calendar);
        // 设置滑动选择改变月份事件
        mcvSign.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                mcvSign.clearSelection();
                // data
                clickYear = date.getYear();
                clickMonth = date.getMonth() + 1;
                getSignMonthData();
            }
        });
        // 设置点击选择日期改变事件
        mcvSign.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                mcvSign.clearSelection();
                // data
                Calendar calendar = date.getCalendar();
                clickYear = calendar.get(Calendar.YEAR);
                clickMonth = calendar.get(Calendar.MONTH) + 1;
                clickDay = calendar.get(Calendar.DAY_OF_MONTH);
                // calendar
                if (clickDecorator == null) {
                    clickDecorator = new CalendarView.ClickDecorator(mActivity, calendar);
                    mcvSign.addDecorator(clickDecorator);
                } else {
                    clickDecorator.setClick(calendar);
                    mcvSign.invalidateDecorators();
                }
                // view
                refreshDayView();
            }
        });
        // view
        refreshMonthView();
        refreshDayView();
    }

    private void refreshMonthView() {
        if (mcvSign == null) return;
        // data
        Calendar cal = DateUtils.getCurrentCalendar();
        List<Calendar> selectList = new ArrayList<>();
        int rightCount = 0, leftCount = 0;
        if (signList != null && signList.size() > 0) {
            for (Sign s : signList) {
                if (s == null) continue;
                Calendar calendar = DateUtils.getCalendar(TimeHelper.getJavaTimeByGo(s.getCreateAt()));
                selectList.add(calendar);
                if (s.isMine()) {
                    ++rightCount;
                } else {
                    ++leftCount;
                }
                if (today == null && s.getYear() == cal.get(Calendar.YEAR)
                        && s.getMonthOfYear() == (cal.get(Calendar.MONTH) + 1)
                        && s.getDayOfMonth() == cal.get(Calendar.DAY_OF_MONTH)) {
                    today = s;
                }
            }
        }
        // calendar
        if (selectedDecorator == null) {
            selectedDecorator = new CalendarView.SelectedDecorator(mActivity, selectList);
            mcvSign.addDecorator(selectedDecorator);
        } else {
            selectedDecorator.setSelectedList(selectList);
            mcvSign.invalidateDecorators();
        }
        // view
        tvCountLeft.setText(String.format(Locale.getDefault(), getString(R.string.to_month_sign_holder_count), leftCount));
        tvCountRight.setText(String.format(Locale.getDefault(), getString(R.string.to_month_sign_holder_count), rightCount));
    }

    private void refreshDayView() {
        if (mcvSign == null) return;
        // data
        String signShow = null;
        if (signList != null && signList.size() > 0) {
            for (Sign s : signList) {
                if (s == null) continue;
                if (s.getYear() == clickYear && s.getMonthOfYear() == clickMonth && s.getDayOfMonth() == clickDay) {
                    signShow = DateUtils.getString(TimeHelper.getJavaTimeByGo(s.getCreateAt()), ConstantUtils.FORMAT_H_M);
                }
            }
        }
        if (StringUtils.isEmpty(signShow)) {
            Calendar cal = DateUtils.getCurrentCalendar();
            if (today == null || (cal.get(Calendar.YEAR) == clickYear
                    && cal.get(Calendar.MONTH) + 1 == clickMonth
                    && cal.get(Calendar.DAY_OF_MONTH) == clickDay)) {
                signShow = getString(R.string.sign);
            } else {
                signShow = getString(R.string.nil);
            }
        }
        // view
        tvState.setText(signShow);
    }

    private void getSignMonthData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        // clear (data + view)
        clickDay = -1;
        signList = null;
        refreshDayView();
        // call
        callListGet = new RetrofitHelper().call(API.class).moreSignDateGet(clickYear, clickMonth);
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
        if (today != null) return;
        callAdd = new RetrofitHelper().call(API.class).moreSignAdd();
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(callAdd, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                today = data.getSign();
                // view
                refreshDayView();
                // data
                getSignMonthData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
