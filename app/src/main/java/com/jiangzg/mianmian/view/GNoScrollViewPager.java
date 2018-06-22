package com.jiangzg.mianmian.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by gg on 2017/4/3.
 * 取消侧滑的viewPager
 */
public class GNoScrollViewPager extends ViewPager {

    public GNoScrollViewPager(Context context) {
        super(context);
    }

    public GNoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 事件分发，是否分发给下级
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true); // 请求父控件不要拦截事件
        return super.dispatchTouchEvent(ev);
    }

    // 事件拦截，自己是否会处理
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //return super.onInterceptTouchEvent(ev);
        return true;
    }

    // 事件处理，自己要做啥处理
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //return super.onTouchEvent(ev);
        return true;
    }
}
