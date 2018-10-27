package com.jiangzg.lovenote.view;

import android.content.Context;
import android.graphics.Rect;
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

    // 主要是这个 防止焦点丢失 没有跑马灯效果
    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(true, direction, previouslyFocusedRect);
    }

    // 这个还是在xml里设置吧，有渐变效果
    public void init() {
        //this.setFocusable(true);
        //this.setFocusableInTouchMode(true);
        //this.setSingleLine(true);
        //this.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        //this.setMarqueeRepeatLimit(-1);
        //this.setHorizontallyScrolling(true);
    }

}
