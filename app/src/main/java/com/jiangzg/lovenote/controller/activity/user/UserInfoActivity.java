package com.jiangzg.lovenote.controller.activity.user;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.jiangzg.base.component.ActivityStack;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.DialogHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.BirthPicker;

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
    @BindView(R.id.cvGirl)
    CardView cvGirl;
    @BindView(R.id.ivBoy)
    ImageView ivBoy;
    @BindView(R.id.cvBoy)
    CardView cvBoy;
    @BindView(R.id.npYear)
    BirthPicker npYear;
    @BindView(R.id.npMonth)
    BirthPicker npMonth;
    @BindView(R.id.npDay)
    BirthPicker npDay;
    @BindView(R.id.btnOk)
    Button btnOk;

    private User me;
    private Call<Result> call;

    public static void goActivity(Activity from, User user) {
        Intent intent = new Intent(from, UserInfoActivity.class);
        intent.putExtra("user", user);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY); // 启动其他activity时消失
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.user_info), false);
        // 时间选择器
        npYear.setFormatter(value -> String.valueOf(value) + mActivity.getString(R.string.year));
        npMonth.setFormatter(value -> String.valueOf(value) + mActivity.getString(R.string.month));
        npDay.setFormatter(value -> String.valueOf(value) + mActivity.getString(R.string.dayR));
        setYeas();
        setMonths();
        setDays();
        // listener
        npYear.setOnValueChangedListener((picker, oldVal, newVal) -> setDays());
        npMonth.setOnValueChangedListener((picker, oldVal, newVal) -> setDays());
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        SPHelper.clearMe(); // 清除数据
        me = intent.getParcelableExtra("user");
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(call);
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

    @OnClick({R.id.cvGirl, R.id.cvBoy, R.id.btnOk})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvGirl: // 女
                ivGirl.setVisibility(View.VISIBLE);
                ivBoy.setVisibility(View.GONE);
                break;
            case R.id.cvBoy: // 男
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
                .onPositive((dialog1, which) -> pushUserInfo(sex, birth))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void pushUserInfo(int sex, long birth) {
        User user = new User();
        user.setSex(sex);
        user.setBirthday(birth);
        SPHelper.setMe(me); // api要用token
        // api调用
        call = new RetrofitHelper().call(API.class).userModify(ApiHelper.MODIFY_INFO, "", "", user);
        MaterialDialog loading = getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                User user = data.getUser();
                SPHelper.setMe(user);
                ApiHelper.postEntry(mActivity);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                SPHelper.clearMe();
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
