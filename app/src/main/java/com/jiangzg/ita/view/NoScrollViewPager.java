package com.jiangzg.ita.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by gg on 2017/4/3.
 * 取消侧滑的viewPager
 */

public class NoScrollViewPager extends ViewPager {
    public NoScrollViewPager(Context context) {
        super(context);
    }

    public NoScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 事件分发，请求父控件不要拦截事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    // 事件拦截，表示不拦截
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // return super.onInterceptTouchEvent(ev);
        return false;
    }

    // 事件处理，表示什么也不做
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // return super.onTouchEvent(ev);
        return false;
    }
}
