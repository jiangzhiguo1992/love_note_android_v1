package com.jiangzg.mianmian.view;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;

import com.jiangzg.mianmian.helper.ViewHelper;

/**
 * Created by JiangZhiGuo on 2016/8/11.
 * describe 只监听垂直下拉的SwipeRefreshLayout
 */
public class GSwipeRefreshLayout extends SwipeRefreshLayout {

    private int mTouchSlop;
    // 上一次触摸时的X坐标
    private float mPrevX;

    public GSwipeRefreshLayout(Context context) {
        super(context);
        init(context);
    }

    public GSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // 触发移动事件的最短距离，如果小于这个距离就不触发移动控件
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        // 设置颜色
        int colorDark = ViewHelper.getColorDark(context);
        int colorPrimary = ViewHelper.getColorPrimary(context);
        int colorAccent = ViewHelper.getColorAccent(context);
        int colorLight = ViewHelper.getColorLight(context);
        this.setColorSchemeResources(colorDark, colorPrimary, colorAccent, colorLight, colorAccent, colorPrimary);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);
                // LogUtils.d("refresh" ,"move----" + eventX + "   " + mPrevX + "   " + mTouchSlop);
                // 增加60的容差，让下拉刷新在竖直滑动时就可以触发
                if (xDiff > mTouchSlop + 60) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(event);
    }
}