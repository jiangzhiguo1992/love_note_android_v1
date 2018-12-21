package com.jiangzg.base.view;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.method.LinkMovementMethod;
import android.util.TypedValue;
import android.widget.TextView;

import com.jiangzg.base.R;

/**
 * Created by JZG on 2018/12/21.
 * ViewUtils
 */
public class ViewUtils {


    /**
     * textView.setCompoundDrawables(null, null, null, null);
     */
    public static Drawable getDrawable(Context context, int draResId) {
        if (draResId == 0) return null;
        Drawable icon = ContextCompat.getDrawable(context, draResId);
        if (icon == null) return null;
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        return icon;
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

    public static int getColorPrimary(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.resourceId;
    }

    public static int getColorDark(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.resourceId;
    }

    public static int getColorAccent(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.resourceId;
    }

    public static int getColorLight(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorControlNormal, typedValue, true);
        return typedValue.resourceId;
    }
}
