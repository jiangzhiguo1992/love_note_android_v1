package com.jiangzg.lovenote.helper.view;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;

import java.util.Random;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 符合项目样式的View管理类
 */
public class ViewHelper {

    public static void initTopBar(final AppCompatActivity activity, Toolbar tb, String title, boolean navBack) {
        if (activity == null || tb == null) return;
        tb.setTitle(title);
        activity.setSupportActionBar(tb);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(!title.trim().isEmpty());
            actionBar.setDisplayHomeAsUpEnabled(navBack);
            actionBar.setHomeButtonEnabled(navBack);
            actionBar.setDisplayShowHomeEnabled(navBack);
        }
        if (navBack) {
            //tb.setNavigationIcon(R.drawable.ab_android);
            tb.setNavigationOnClickListener(v -> activity.finish());
        } else {
            tb.setTitleMarginStart(50);
        }
    }

    public static void initDrawerLayout(Activity activity, DrawerLayout dl, Toolbar tb) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, dl, tb,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(toggle);
        toggle.syncState();
    }

    public static int getRandomThemePrimaryRes() {
        int[] colors = new int[]{R.color.theme_red_primary, R.color.theme_pink_primary,
                R.color.theme_purple_primary, R.color.theme_indigo_primary, R.color.theme_blue_primary,
                R.color.theme_teal_primary, R.color.theme_green_primary, R.color.theme_yellow_primary,
                R.color.theme_orange_primary, R.color.theme_brown_primary, R.color.theme_grey_primary,
        };
        Random random = new Random();
        int nextInt = random.nextInt(colors.length);
        return colors[nextInt];
    }

    // getWrapTextView
    public static View getWrapTextView(Context context, String show) {
        if (context == null || StringUtils.isEmpty(show)) {
            LogUtils.w(ViewHelper.class, "getWrapTextView", "context == null || show == null");
            return null;
        }
        int dp7 = ConvertUtils.dp2px(7);
        int dp5 = ConvertUtils.dp2px(5);
        int dp2 = ConvertUtils.dp2px(2);
        FrameLayout.LayoutParams mTextLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mTextLayoutParams.setMarginEnd(dp7);
        TextView textView = new TextView(context);
        textView.setLayoutParams(mTextLayoutParams);
        textView.setBackgroundResource(R.drawable.shape_solid_primary_r2);
        //textView.setBackgroundResource(resId);
        textView.setPadding(dp5, dp2, dp5, dp2);
        textView.setGravity(Gravity.CENTER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.FontWhiteSmall);
        } else {
            textView.setTextAppearance(context, R.style.FontWhiteSmall);
        }
        textView.setText(show);
        return textView;
    }

}