package com.jiangzg.lovenote.activity.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ThemeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;

import butterknife.BindView;
import butterknife.OnClick;

public class ThemeActivity extends BaseActivity<ThemeActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.rlRed)
    LinearLayout rlRed;
    @BindView(R.id.ivRed)
    ImageView ivRed;
    @BindView(R.id.rlPink)
    LinearLayout rlPink;
    @BindView(R.id.ivPink)
    ImageView ivPink;
    @BindView(R.id.rlPurple)
    LinearLayout rlPurple;
    @BindView(R.id.ivPurple)
    ImageView ivPurple;
    @BindView(R.id.rlIndigo)
    LinearLayout rlIndigo;
    @BindView(R.id.ivIndigo)
    ImageView ivIndigo;
    @BindView(R.id.rlBlue)
    LinearLayout rlBlue;
    @BindView(R.id.ivBlue)
    ImageView ivBlue;
    @BindView(R.id.rlTeal)
    LinearLayout rlTeal;
    @BindView(R.id.ivTeal)
    ImageView ivTeal;
    @BindView(R.id.rlGreen)
    LinearLayout rlGreen;
    @BindView(R.id.ivGreen)
    ImageView ivGreen;
    @BindView(R.id.rlYellow)
    LinearLayout rlYellow;
    @BindView(R.id.ivYellow)
    ImageView ivYellow;
    @BindView(R.id.rlOrange)
    LinearLayout rlOrange;
    @BindView(R.id.ivOrange)
    ImageView ivOrange;
    @BindView(R.id.rlBrown)
    LinearLayout rlBrown;
    @BindView(R.id.ivBrown)
    ImageView ivBrown;
    @BindView(R.id.rlGrey)
    LinearLayout rlGrey;
    @BindView(R.id.ivGrey)
    ImageView ivGrey;

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
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.theme), true);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        refreshDataView();
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.rlRed, R.id.rlPink, R.id.rlPurple, R.id.rlIndigo, R.id.rlBlue,
            R.id.rlTeal, R.id.rlGreen, R.id.rlYellow, R.id.rlOrange, R.id.rlBrown, R.id.rlGrey})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rlRed:
                ThemeHelper.setTheme(ThemeHelper.THEME_RED);
                break;
            case R.id.rlPink:
                ThemeHelper.setTheme(ThemeHelper.THEME_PINK);
                break;
            case R.id.rlPurple:
                ThemeHelper.setTheme(ThemeHelper.THEME_PURPLE);
                break;
            case R.id.rlIndigo:
                ThemeHelper.setTheme(ThemeHelper.THEME_INDIGO);
                break;
            case R.id.rlBlue:
                ThemeHelper.setTheme(ThemeHelper.THEME_BLUE);
                break;
            case R.id.rlTeal:
                ThemeHelper.setTheme(ThemeHelper.THEME_TEAL);
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
            case R.id.rlGrey:
                ThemeHelper.setTheme(ThemeHelper.THEME_GREY);
                break;
        }
        refreshDataView();
    }

    private void refreshDataView() {
        ivPink.setImageResource(R.mipmap.ic_brightness_1_grey_24dp);
        ivRed.setImageResource(R.mipmap.ic_brightness_1_grey_24dp);
        ivPurple.setImageResource(R.mipmap.ic_brightness_1_grey_24dp);
        ivIndigo.setImageResource(R.mipmap.ic_brightness_1_grey_24dp);
        ivBlue.setImageResource(R.mipmap.ic_brightness_1_grey_24dp);
        ivTeal.setImageResource(R.mipmap.ic_brightness_1_grey_24dp);
        ivGreen.setImageResource(R.mipmap.ic_brightness_1_grey_24dp);
        ivYellow.setImageResource(R.mipmap.ic_brightness_1_grey_24dp);
        ivOrange.setImageResource(R.mipmap.ic_brightness_1_grey_24dp);
        ivBrown.setImageResource(R.mipmap.ic_brightness_1_grey_24dp);
        ivGrey.setImageResource(R.mipmap.ic_brightness_1_grey_24dp);
        // 获取已选择的theme
        int settingsTheme = SPHelper.getTheme();
        switch (settingsTheme) {
            case ThemeHelper.THEME_PINK:
                ivPink.setImageResource(R.mipmap.ic_check_circle_grey_24dp);
                break;
            case ThemeHelper.THEME_RED:
                ivRed.setImageResource(R.mipmap.ic_check_circle_grey_24dp);
                break;
            case ThemeHelper.THEME_PURPLE:
                ivPurple.setImageResource(R.mipmap.ic_check_circle_grey_24dp);
                break;
            case ThemeHelper.THEME_INDIGO:
                ivIndigo.setImageResource(R.mipmap.ic_check_circle_grey_24dp);
                break;
            case ThemeHelper.THEME_BLUE:
                ivBlue.setImageResource(R.mipmap.ic_check_circle_grey_24dp);
                break;
            case ThemeHelper.THEME_TEAL:
                ivTeal.setImageResource(R.mipmap.ic_check_circle_grey_24dp);
                break;
            case ThemeHelper.THEME_GREEN:
                ivGreen.setImageResource(R.mipmap.ic_check_circle_grey_24dp);
                break;
            case ThemeHelper.THEME_YELLOW:
                ivYellow.setImageResource(R.mipmap.ic_check_circle_grey_24dp);
                break;
            case ThemeHelper.THEME_ORANGE:
                ivOrange.setImageResource(R.mipmap.ic_check_circle_grey_24dp);
                break;
            case ThemeHelper.THEME_BROWN:
                ivBrown.setImageResource(R.mipmap.ic_check_circle_grey_24dp);
                break;
            case ThemeHelper.THEME_GREY:
                ivGrey.setImageResource(R.mipmap.ic_check_circle_grey_24dp);
                break;
        }
    }

}
