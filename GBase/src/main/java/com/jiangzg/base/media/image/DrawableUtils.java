package com.jiangzg.base.media.image;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

/**
 * Created by JZG on 2017/12/14.
 * DrawableUtils
 */

public class DrawableUtils {

    /**
     * textView.setCompoundDrawables(null, null, null, null);
     */
    public static Drawable getDrawable(Context context, int draResId) {
        Drawable icon = ContextCompat.getDrawable(context, draResId);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        return icon;
    }

}
