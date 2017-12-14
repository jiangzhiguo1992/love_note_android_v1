package com.jiangzg.ita.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.jiangzg.base.media.image.DrawableUtils;
import com.jiangzg.base.view.DialogUtils;
import com.jiangzg.ita.R;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 符合项目样式的View管理类
 */
public class ViewUtils {

    private ProgressDialog loading;
    private ProgressDialog progress;

    /**
     * @return 对话框(静态会混乱)
     */
    public ProgressDialog getLoading(Activity mActivity,String text) {
        if (loading != null) return loading;
        loading = DialogUtils.createLoading(mActivity,
                0, "", text, true);
        return loading;
    }

    /**
     * @return 进度框(静态会混乱)
     */
    public ProgressDialog getProgress(Activity mActivity,String text) {
        if (progress != null) return progress;
        progress = DialogUtils.createProgress(mActivity,
                0, text, true, 100, 0, null);
        return progress;
    }

    public static void initTop(Activity activity, String title) {
        TextView tvCenter = (TextView) activity.findViewById(R.id.tvCenter);
        tvCenter.setVisibility(View.VISIBLE);
        tvCenter.setText(title);
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