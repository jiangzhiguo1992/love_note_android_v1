package com.jiangzg.ita.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.jiangzg.base.component.activity.ActivityStack;
import com.jiangzg.base.media.image.DrawableUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.Version;
import com.jiangzg.ita.service.UpdateService;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 符合项目样式的View管理类
 */
public class ViewUtils {

    public static void initToolbar(final AppCompatActivity activity, Toolbar tb, boolean enable) {
        activity.setSupportActionBar(tb);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false); //不用默认的title
            actionBar.setDisplayHomeAsUpEnabled(enable);
            actionBar.setHomeButtonEnabled(enable);
            actionBar.setDisplayShowHomeEnabled(enable);
        }
        if (enable) {
            //tb.setNavigationIcon(R.drawable.ab_android);
            tb.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        }
    }

    public static void initDrawerLayout(Activity activity, DrawerLayout dl, Toolbar tb) {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, dl, tb,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dl.addDrawerListener(toggle);
        toggle.syncState();
    }

    public static void showUpdateDialog(Version version) {
        final Activity top = ActivityStack.getTop();
        if (top == null) return;
        String title = String.format(top.getString(R.string.find_new_version), version.getVersionName());
        String message = version.getUpdateLog();
        String positive = top.getString(R.string.update_now);
        String negative = top.getString(R.string.update_delay);
        AlertDialog dialog = DialogUtils.createAlert(top, title, message, positive, negative,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UpdateService.goService(top);
                    }
                }, null);
        dialog.show();
    }

    /**
     * 边图
     */
    public static void setDrawables(TextView textView, int leftId, int topId, int rightId, int bottomId) {
        Drawable left = DrawableUtils.getDrawable(textView.getContext(), leftId);
        Drawable top = DrawableUtils.getDrawable(textView.getContext(), topId);
        Drawable right = DrawableUtils.getDrawable(textView.getContext(), rightId);
        Drawable bottom = DrawableUtils.getDrawable(textView.getContext(), bottomId);
        textView.setCompoundDrawables(left, top, right, bottom);
    }

    /**
     * 底划线
     */
    public static void setLineBottom(TextView view) {
        view.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        view.getPaint().setAntiAlias(true);
    }

    /**
     * 中划线
     */
    public static void setLineCenter(TextView view) {
        view.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        view.getPaint().setAntiAlias(true);
    }

    /**
     * 连接点击
     */
    public static void setLinkClick(TextView view) {
        view.setMovementMethod(LinkMovementMethod.getInstance());
    }

}