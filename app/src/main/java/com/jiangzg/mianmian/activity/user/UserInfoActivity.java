package com.jiangzg.mianmian.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.settings.HelpActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Help;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;
import com.jiangzg.mianmian.view.BirthPicker;

import java.util.Calendar;
import java.util.Stack;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;

public class UserInfoActivity extends BaseActivity<UserInfoActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.ivGirl)
    ImageView ivGirl;
    @BindView(R.id.rlGirl)
    RelativeLayout rlGirl;
    @BindView(R.id.ivBoy)
    ImageView ivBoy;
    @BindView(R.id.rlBoy)
    RelativeLayout rlBoy;
    @BindView(R.id.npYear)
    BirthPicker npYear;
    @BindView(R.id.npMonth)
    BirthPicker npMonth;
    @BindView(R.id.npDay)
    BirthPicker npDay;
    @BindView(R.id.btnOk)
    Button btnOk;

    private Call<Result> call;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, UserInfoActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // 启动其他activity时消失
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.user_info), false);
        // 时间选择器
        npYear.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.valueOf(value) + mActivity.getString(R.string.year);
            }
        });
        npMonth.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.valueOf(value) + mActivity.getString(R.string.month);
            }
        });
        npDay.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.valueOf(value) + mActivity.getString(R.string.dayR);
            }
        });
        setYeas();
        setMonths();
        setDays();
        // listener
        npYear.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setDays();
            }
        });
        npMonth.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setDays();
            }
        });
    }

    @Override
    protected void initData(Bundle state) {
    }

    @Override
    protected void onStart() {
        super.onStart();
        Stack<Activity> stack = ActivityStack.getStack();
        for (Activity activity : stack) {
            if (activity != mActivity) {
                activity.finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RetrofitHelper.cancel(call);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, Help.INDEX_USER_INFO_SET);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.rlGirl, R.id.rlBoy, R.id.btnOk})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlGirl: // 女
                ivGirl.setVisibility(View.VISIBLE);
                ivBoy.setVisibility(View.GONE);
                break;
            case R.id.rlBoy: // 男
                ivBoy.setVisibility(View.VISIBLE);
                ivGirl.setVisibility(View.GONE);
                break;
            case R.id.btnOk: // 完成
                checkUserInfo();
                break;
        }
    }

    private void checkUserInfo() {
        // 性别检查
        int isGirl = ivGirl.getVisibility();
        int isBoy = ivBoy.getVisibility();
        if (isGirl != View.VISIBLE && isBoy != View.VISIBLE) {
            ToastUtils.show(getString(R.string.please_select_sex));
            return;
        }
        // 生日检查
        int year = npYear.getValue();
        int month = npMonth.getValue();
        int day = npDay.getValue();
        if (year == 0 || month == 0 || day == 0) {
            ToastUtils.show(getString(R.string.please_select_birth));
            return;
        }
        final int sex = (isGirl == View.VISIBLE) ? User.SEX_GIRL : User.SEX_BOY;
        String sexShow = (sex == User.SEX_GIRL) ? getString(R.string.girl) : getString(R.string.boy);
        Calendar calendar = DateUtils.getCurrentCalendar();
        calendar.set(year, month - 1, day, 0, 0, 0);
        final long birth = TimeHelper.getGoTimeByJava(calendar.getTimeInMillis());
        String title = getString(R.string.once_push_never_modify);
        String birthShow = year + getString(R.string.year_space) + month + getString(R.string.month_space) + day + getString(R.string.dayR);
        String message = getString(R.string.sex_colon) + sexShow +
                "\n" + getString(R.string.birthday_colon) + birthShow;
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .title(title)
                .content(message)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        pushUserInfo(sex, birth);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void pushUserInfo(int sex, long birth) {
        User user = ApiHelper.getUserInfoBody(sex, birth);
        // api调用
        call = new RetrofitHelper().call(API.class).userModify(user);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                User user = data.getUser();
                SPHelper.setMe(user);
                ApiHelper.postEntry(mActivity);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

    private void setYeas() {
        int maxYear = Calendar.getInstance().get(Calendar.YEAR);
        int minYear = maxYear - 100;
        npYear.setMinValue(minYear);
        npYear.setMaxValue(maxYear);
        npYear.setValue(maxYear - 20);
    }

    public void setMonths() {
        npMonth.setMinValue(1);
        npMonth.setMaxValue(12);
    }

    public void setDays() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(npYear.getValue(), npMonth.getValue() - 1, 1);
        int daysOfMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        npDay.setMinValue(1);
        npDay.setMaxValue(daysOfMonth);
    }
}
