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
public class WallPaperPager extends ViewPager {

    public WallPaperPager(Context context) {
        super(context);
    }

    public WallPaperPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // 事件分发，返回值代表触摸事件是否被当前 View 处理完成
    // 只要上层传来触摸事件，就一定会被调用，
    // 返回值由onInterceptTouchEvent和onTouchEvent来确定
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true); // 请求父控件不要拦截事件
        return super.dispatchTouchEvent(ev);
    }

    // 事件拦截，是否不传递给子view，处理与下级的关系，所以只有viewGroup中有此方法
    // 返回true则调用onTouchEvent，并且不会传递给子view
    // 返回false则不调用onTouchEvent，传给子view去判断
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //return super.onInterceptTouchEvent(ev);
        return true;
    }

    // 事件处理，返回值代表触摸事件是否被当前 View 处理完成
    // 返回false，则没有处理完成，父view还可以在做处理
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //return super.onTouchEvent(ev);
        return true;
    }
}
