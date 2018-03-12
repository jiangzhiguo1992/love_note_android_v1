package com.jiangzg.ita.activity.user;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.jiangzg.base.component.activity.ActivityTrans;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.HomeActivity;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.base.BaseActivity;
import com.jiangzg.ita.domain.Help;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.utils.ViewUtils;
import com.jiangzg.ita.view.GNumberPicker;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

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
    GNumberPicker npYear;
    @BindView(R.id.npMonth)
    GNumberPicker npMonth;
    @BindView(R.id.npDay)
    GNumberPicker npDay;
    @BindView(R.id.btnOk)
    Button btnOk;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, UserInfoActivity.class);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_user_info;
    }

    @Override
    protected void initView(Bundle state) {
        ViewUtils.initTopBar(mActivity, tb, getString(R.string.user_info), false);
        // 时间选择器
        npYear.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.valueOf(value) + mActivity.getString(R.string.dayR);
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
                return String.valueOf(value) + mActivity.getString(R.string.year);
            }
        });
        setYeas();
        setMonths();
        setDays();
        // listener
        tb.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menuHelp: // 帮助
                        HelpActivity.goActivity(mActivity, Help.TYPE_USER_INFO_SET);
                        break;
                }
                return true;
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
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
        String sexShow = (sex == User.SEX_GIRL) ? "女" : "男";
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        final long birth = calendar.getTimeInMillis() / 1000;
        String birthShow = year + "年 " + month + "月 " + day + "日";
        String message = "性别: " + sexShow + "\n生日: " + birthShow;
        AlertDialog alert = DialogUtils.createAlert(mActivity, "一旦提交，将不允许修改！", message, "确认无误", "我再想想", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pushUserInfo(sex, birth);
            }
        }, null);
        alert.show();
    }

    private void pushUserInfo(int sex, long birth) {
        // todo http请求
        HomeActivity.goActivity(mActivity);
        mActivity.finish();
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
