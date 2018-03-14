package com.jiangzg.ita.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-11-3.
 * describe 自动换行view
 */

public class GWrapView extends FrameLayout {

    /* java构造时调用 */
    public GWrapView(Context context) {
        super(context);
    }

    /* xml构造时调用 */
    public GWrapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* xml构造，并有自定义style时调用 */
    public GWrapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /* MyWrapView的高度测量 */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int paddingStart = getPaddingStart();
        int paddingEnd = getPaddingEnd();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int x = 0; // 横坐标
        int y = 0; // 纵坐标
        int rows = 1; // 总行数
        int parentMatchWidth = MeasureSpec.getSize(widthMeasureSpec); // 只有在match时管用
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            // 获取child的属性
            View child = getChildAt(i);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            FrameLayout.LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            int marginStart = layoutParams.getMarginStart();
            int marginEnd = layoutParams.getMarginEnd();
            int bottomMargin = layoutParams.bottomMargin; // 所有child的应该一致
            int topMargin = layoutParams.topMargin; // 所有child的应该一致
            // 开始计算child的x轴
            if (i <= 0) {
                x = paddingStart + marginStart + width + marginEnd;
            } else {
                x += marginStart + width + marginEnd;
            }
            // 换行处理
            if (x + paddingEnd > parentMatchWidth) {
                x = paddingStart + marginStart + width + marginEnd; // 与上面的index=0保持一致
                rows++;
            }
            // 开始计算child的y轴
            y = rows * (topMargin + height + bottomMargin);
        }
        y += paddingTop + paddingBottom; // 总高度=child总高度+padding值
        setMeasuredDimension(parentMatchWidth, y);
    }

    /* 内部childView的布局 */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int paddingStart = getPaddingStart();
        int paddingEnd = getPaddingEnd();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingBottom();

        int x = 0; // 横坐标结束点
        int y = 0; // 纵坐标结束点
        int rows = 1; // 行数开始
        int parentWidth = r - l; // 父组件可布局的宽度
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            // 获取child的属性
            View child = getChildAt(i);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();
            FrameLayout.LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
            int marginStart = layoutParams.getMarginStart();
            int marginEnd = layoutParams.getMarginEnd();
            int bottomMargin = layoutParams.bottomMargin; // 所有child的应该一致
            int topMargin = layoutParams.topMargin; // 所有child的应该一致
            // 开始测量child的x轴
            if (i <= 0) {
                x = paddingStart + marginStart;
            } else {
                View lastChild = getChildAt(i - 1);
                int lastChildWidth = lastChild.getMeasuredWidth();
                FrameLayout.LayoutParams lastChildLayoutParams = (LayoutParams) lastChild.getLayoutParams();
                int lastChildLayoutParamsMarginEnd = lastChildLayoutParams.getMarginEnd();
                x += lastChildWidth + lastChildLayoutParamsMarginEnd + marginStart;
            }
            // 换行处理
            if (x + width + marginEnd + paddingEnd > parentWidth) {
                x = paddingStart + marginStart; // 与上面的index=0保持一致
                rows++;
            }
            // 开始测量child的x轴
            y = paddingTop + topMargin + (rows - 1) * (height + bottomMargin + topMargin);
            child.layout(x, y, x + width, y + height);
        }
    }

    public void addChild(View child) {
        addView(child);
    }

    public void addChild(View child, int index) {
        addView(child, index);
    }

    public void addChilds(List<View> childes) {
        for (View child : childes) {
            addChild(child);
        }
    }

    public void removeChild(int index) {
        removeViewAt(index);
    }

    public void removeChild(View child) {
        removeView(child);
    }

    public void removeAllChild() {
        removeAllViews();
    }

}
