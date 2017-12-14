package com.jiangzg.ita.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.jiangzg.base.common.ConvertUtils;

import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-11-3.
 * describe 自动换行view
 */

public class JWrapView extends ViewGroup {
    private static int MARGIN_LEFT = 20;
    private static int MARGIN_TOP = 20;

    /* java构造时调用 */
    public JWrapView(Context context) {
        super(context);
    }

    /* xml构造时调用 */
    public JWrapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /* xml构造，并有自定义style时调用 */
    public JWrapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /**
     * @param dpL 内部子View的左TopMargin
     * @param dpT 内部子View的LeftMargin
     */
    public void init(int dpL, int dpT) {
        MARGIN_LEFT = ConvertUtils.dp2px(dpL);
        MARGIN_TOP = ConvertUtils.dp2px(dpT);
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

    /* MyWrapView的高度测量 */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x = 0;//横坐标
        int y = 0;//纵坐标
        int rows = 1;//总行数
        int specWidth = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            int width = child.getMeasuredWidth();
            int height = child.getMeasuredHeight();

            if (index == 0) {
                x += width;
            } else {
                x += MARGIN_LEFT + width;
            }
            if (x > specWidth) { //换行
                x = width;
                rows++;
            }
            y = rows * (height + MARGIN_TOP);
        }
        setMeasuredDimension(specWidth, y); // 主要测量高度
    }

    /* 内部childView的布局 */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int autualWidth = r - l;
        int x = 0;// 横坐标开始
        int y = 0;// 纵坐标开始
        int rows = 1;
        for (int i = 0; i < childCount; i++) {
            View view = getChildAt(i);
            int width = view.getMeasuredWidth();
            int height = view.getMeasuredHeight();
            if (i == 0) {
                x += width;
            } else {
                x += MARGIN_LEFT + width;
            }
            if (x > autualWidth) {// 换行
                x = width;
                rows++;
            }
            y = rows * (height + MARGIN_TOP);
            view.layout(x - width, y - height, x, y);
        }
    }

}
