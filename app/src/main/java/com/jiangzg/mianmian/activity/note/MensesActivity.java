package com.jiangzg.mianmian.activity.note;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.time.TimeUnit;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.adapter.MensesAdapter;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Menses;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.RecyclerHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.GSwipeRefreshLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class MensesActivity extends BaseActivity<MensesActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;
    @BindView(R.id.mcvMenses)
    MaterialCalendarView mcvMenses;
    @BindView(R.id.tvShow)
    TextView tvShow;
    @BindView(R.id.tvTip)
    TextView tvTip;
    @BindView(R.id.rv)
    RecyclerView rv;
    @BindView(R.id.cvPush)
    CardView cvPush;
    @BindView(R.id.tvPush)
    TextView tvPush;
    @BindView(R.id.rgUser)
    RadioGroup rgUser;
    @BindView(R.id.rbMe)
    RadioButton rbMe;
    @BindView(R.id.rbTa)
    RadioButton rbTa;

    private boolean isMine;
    private boolean canMe;
    private boolean canTa;
    private Menses mensesMe;
    private Menses mensesTa;
    private List<Menses> mensesList;
    private RecyclerHelper recyclerHelper;
    private Call<Result> callAdd;
    private Call<Result> callGet;
    private Call<Result> callListGet;

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
        canMe = false;
        // calendar
        initCalendarView();
        // checkView
        initCheckView();
        // latestView
        refreshLatestView();
        // listView
        refreshListView("");
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        getLatestData();
        getListData();
    }

    @Override
    protected void onFinish(Bundle state) {
        RecyclerHelper.release(recyclerHelper);
        RetrofitHelper.cancel(callAdd);
        RetrofitHelper.cancel(callGet);
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
                HelpActivity.goActivity(mActivity, Help.INDEX_NOTE_MENSES);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.cvPush)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvPush: // 发布
                mensesPush();
                break;
        }
    }

    private void initCalendarView() {
        ViewHelper.initMonthView(mActivity, mcvMenses);
        // 设置滑动选择改变月份事件
        mcvMenses.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                widget.setSelectedDate(date);
                getListData();
            }
        });
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
                refreshLatestView();
                getListData();
            }
        });
    }

    private void refreshLatestView() {
        // tip
        if (isMine) {
            if (!canMe || mensesMe == null || mensesMe.getId() <= 0) {
                tvTip.setVisibility(View.INVISIBLE);
            } else {
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(getTipShow(mensesMe));
            }
        } else {
            if (!canTa || mensesTa == null || mensesTa.getId() <= 0) {
                tvTip.setVisibility(View.INVISIBLE);
            } else {
                tvTip.setVisibility(View.VISIBLE);
                tvTip.setText(getTipShow(mensesTa));
            }
        }
        // push
        if (!canMe) {
            cvPush.setVisibility(View.GONE);
        } else {
            if (mensesMe == null || mensesMe.getId() <= 0) {
                cvPush.setVisibility(View.GONE);
            } else {
                cvPush.setVisibility(View.VISIBLE);
                String pushShow = mensesMe.isStart() ? getString(R.string.menses_gone) : getString(R.string.menses_come);
                tvPush.setText(pushShow);
            }
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

    private void refreshListView(String show) {
        // calendar
        if (StringUtils.isEmpty(show)) {
            mcvMenses.setVisibility(View.VISIBLE);
            tvShow.setVisibility(View.GONE);
        } else {
            mcvMenses.setVisibility(View.GONE);
            tvShow.setVisibility(View.VISIBLE);
            tvShow.setText(show);
        }
        if (recyclerHelper == null) {
            recyclerHelper = new RecyclerHelper(rv)
                    .initLayoutManager(new LinearLayoutManager(mActivity))
                    .initAdapter(new MensesAdapter())
                    .viewEmpty(mActivity, R.layout.list_empty_primary, true, true)
                    .setAdapter();
        }
        if (mensesList == null || mensesList.size() <= 0) {
            rv.setVisibility(View.INVISIBLE);
        } else {
            rv.setVisibility(View.VISIBLE);
            recyclerHelper.dataNew(mensesList);
        }
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
                canMe = data.isCanMe();
                canTa = data.isCanTa();
                mensesMe = data.getMensesMe();
                mensesTa = data.getMensesTa();
                refreshLatestView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

    private void getListData() {
        mensesList = null;
        refreshLatestView(); // 先清空选择
        CalendarDay selectedDate = mcvMenses.getSelectedDate();
        if (selectedDate == null) return;
        int year = selectedDate.getYear();
        int month = selectedDate.getMonth() + 1;
        if (month > 12) {
            month = 1;
        }
        callListGet = new RetrofitHelper().call(API.class).noteMensesListGetByDate(isMine, year, month);
        RetrofitHelper.enqueue(callListGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                mensesList = data.getMensesList();
                refreshListView(data.getShow());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
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
                refreshLatestView();
                getListData();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

}
