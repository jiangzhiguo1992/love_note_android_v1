package com.jiangzg.ita.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

/**
 * Created by JZG on 2018/3/6.
 * 跑马灯的TextView
 */

public class GMarqueeText extends android.support.v7.widget.AppCompatTextView {

    public GMarqueeText(Context context) {
        super(context);
        init();
    }

    public GMarqueeText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GMarqueeText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public boolean isFocused() {
        return true;
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(true, direction, previouslyFocusedRect);
    }

    public void init() {
        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
        this.setSingleLine(true);
        this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        this.setMarqueeRepeatLimit(-1);
        this.setHorizontallyScrolling(true);
    }

}
