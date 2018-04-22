package com.jiangzg.mianmian.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.ThemeHelper;
import com.jiangzg.mianmian.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class ThemeActivity extends BaseActivity<ThemeActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.ivRed)
    ImageView ivRed;
    @BindView(R.id.rlRed)
    LinearLayout rlRed;
    @BindView(R.id.ivPink)
    ImageView ivPink;
    @BindView(R.id.rlPink)
    LinearLayout rlPink;
    @BindView(R.id.ivPurple)
    ImageView ivPurple;
    @BindView(R.id.rlPurple)
    LinearLayout rlPurple;
    @BindView(R.id.ivBlue)
    ImageView ivBlue;
    @BindView(R.id.rlBlue)
    LinearLayout rlBlue;
    @BindView(R.id.ivGreen)
    ImageView ivGreen;
    @BindView(R.id.rlGreen)
    LinearLayout rlGreen;
    @BindView(R.id.ivYellow)
    ImageView ivYellow;
    @BindView(R.id.rlYellow)
    LinearLayout rlYellow;
    @BindView(R.id.ivOrange)
    ImageView ivOrange;
    @BindView(R.id.rlOrange)
    LinearLayout rlOrange;
    @BindView(R.id.ivBrown)
    ImageView ivBrown;
    @BindView(R.id.rlBrown)
    LinearLayout rlBrown;

    public static void goActivity(Activity from) {
        Intent intent = new Intent(from, ThemeActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_theme;
    }

    @Override
    protected void initView(Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.theme), true);
        // view
        refreshView();
    }

    @Override
    protected void initData(Bundle state) {

    }

    @OnClick({R.id.rlRed, R.id.rlPink, R.id.rlPurple, R.id.rlBlue, R.id.rlGreen, R.id.rlYellow, R.id.rlOrange, R.id.rlBrown})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlPink:
                ThemeHelper.setTheme(ThemeHelper.THEME_PINK);
                break;
            case R.id.rlRed:
                ThemeHelper.setTheme(ThemeHelper.THEME_RED);
                break;
            case R.id.rlPurple:
                ThemeHelper.setTheme(ThemeHelper.THEME_PURPLE);
                break;
            case R.id.rlBlue:
                ThemeHelper.setTheme(ThemeHelper.THEME_BLUE);
                break;
            case R.id.rlGreen:
                ThemeHelper.setTheme(ThemeHelper.THEME_GREEN);
                break;
            case R.id.rlYellow:
                ThemeHelper.setTheme(ThemeHelper.THEME_YELLOW);
                break;
            case R.id.rlOrange:
                ThemeHelper.setTheme(ThemeHelper.THEME_ORANGE);
                break;
            case R.id.rlBrown:
                ThemeHelper.setTheme(ThemeHelper.THEME_BROWN);
                break;
        }
        refreshView();
    }

    private void refreshView() {
        ivPink.setImageResource(R.drawable.ic_circle_primary);
        ivRed.setImageResource(R.drawable.ic_circle_primary);
        ivPurple.setImageResource(R.drawable.ic_circle_primary);
        ivBlue.setImageResource(R.drawable.ic_circle_primary);
        ivGreen.setImageResource(R.drawable.ic_circle_primary);
        ivYellow.setImageResource(R.drawable.ic_circle_primary);
        ivOrange.setImageResource(R.drawable.ic_circle_primary);
        ivBrown.setImageResource(R.drawable.ic_circle_primary);
        // 获取已选择的theme
        int settingsTheme = SPHelper.getSettingsTheme();
        switch (settingsTheme) {
            case ThemeHelper.THEME_PINK:
                ivPink.setImageResource(R.drawable.ic_check_circle_primary);
                break;
            case ThemeHelper.THEME_RED:
                ivRed.setImageResource(R.drawable.ic_check_circle_primary);
                break;
            case ThemeHelper.THEME_PURPLE:
                ivPurple.setImageResource(R.drawable.ic_check_circle_primary);
                break;
            case ThemeHelper.THEME_BLUE:
                ivBlue.setImageResource(R.drawable.ic_check_circle_primary);
                break;
            case ThemeHelper.THEME_GREEN:
                ivGreen.setImageResource(R.drawable.ic_check_circle_primary);
                break;
            case ThemeHelper.THEME_YELLOW:
                ivYellow.setImageResource(R.drawable.ic_check_circle_primary);
                break;
            case ThemeHelper.THEME_ORANGE:
                ivOrange.setImageResource(R.drawable.ic_check_circle_primary);
                break;
            case ThemeHelper.THEME_BROWN:
                ivBrown.setImageResource(R.drawable.ic_check_circle_primary);
                break;
        }
    }

}
