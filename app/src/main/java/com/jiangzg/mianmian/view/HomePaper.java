package com.jiangzg.mianmian.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by gg on 2017/4/3.
 * 必须侧滑的viewPager
 */
public class HomePaper extends ViewPager {

    private float downX;
    private float downY;

    public HomePaper(Context context) {
        super(context);
    }

    public HomePaper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float absX = Math.abs(ev.getX() - downX);
                float absY = Math.abs(ev.getY() - downY);
                //LogUtils.w(HomePaper.class, "Home", "分发 x:" + absX + " y:" + absY);
                if (absX > 60 && absY < 60) {
                    return true;
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

}
