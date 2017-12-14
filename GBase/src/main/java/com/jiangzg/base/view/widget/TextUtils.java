package com.jiangzg.base.view.widget;

import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.jiangzg.base.media.image.DrawableUtils;

/**
 * Created by gg on 2017/4/17.
 * 文本管理类
 */
public class TextUtils {

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
