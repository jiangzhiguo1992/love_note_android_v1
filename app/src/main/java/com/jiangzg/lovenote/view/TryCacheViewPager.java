package com.jiangzg.lovenote.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jiangzg.base.common.LogUtils;

/**
 * Created by JZG on 2018/8/23.
 * TryCacheViewPager
 * 防止PhotoView多点触控的时候异常 IllegalArgumentException: pointerIndex out of range
 */
public class TryCacheViewPager extends ViewPager {

    public TryCacheViewPager(@NonNull Context context) {
        super(context);
    }

    public TryCacheViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            LogUtils.w(TryCacheViewPager.class, "onInterceptTouchEvent", e.getMessage());
        }
        return false;
    }
}
