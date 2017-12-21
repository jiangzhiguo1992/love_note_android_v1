package com.jiangzg.ita.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.jiangzg.base.media.image.DrawableUtils;
import com.jiangzg.ita.R;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 符合项目样式的View管理类
 */
public class ViewUtils {

    public static void initTop(Activity activity, String title) {
        TextView tvCenter = (TextView) activity.findViewById(R.id.tvCenter);
        tvCenter.setVisibility(View.VISIBLE);
        tvCenter.setText(title);
    }

    public static void initToolbar(final AppCompatActivity activity, Toolbar tb) {
        activity.setSupportActionBar(tb);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        tb.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
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

    public static ViewSwitcher.ViewFactory getViewFactory(final Context context) {
        return new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new ImageView(context);
            }
        };
    }

}