package com.jiangzg.mianmian.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by gg on 2017/4/3.
 * 必须侧滑的viewPager
 */
public class GScrollViewPager extends ViewPager {

    private float downX;

    public GScrollViewPager(Context context) {
        super(context);
    }

    public GScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 事件分发，是否分发给下级
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    // 事件拦截，自己是否会处理
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                return super.onInterceptTouchEvent(ev);
            case MotionEvent.ACTION_MOVE:
                float abs = Math.abs(ev.getX() - downX);
                return abs >= 60 || super.onInterceptTouchEvent(ev);
        }
        return super.onInterceptTouchEvent(ev);
    }

    // 事件处理，自己要做啥处理
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }
}
