package com.jiangzg.project.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.jiangzg.project.R;

/**
 * Created by JiangZhiGuo on 2016-11-3.
 * describe 顶部下拉伸缩View
 */
public class JZoomScrollView extends ScrollView implements View.OnTouchListener {

    private int layoutIndex = 2; // 第几层view的顶视图需要伸缩
    private float mFirstPosition = 0; // 记录首次按下位置
    private Boolean mScaling = false; // 是否正在放大
    private View dropZoomView; // 伸缩的view
    private int dropZoomViewWidth;
    private int dropZoomViewHeight;

    /* java构造时调用 */
    public JZoomScrollView(Context context) {
        super(context);
    }

    /* xml构造时调用 */
    public JZoomScrollView(Context context, AttributeSet set) {
        super(context, set, 0); // 有必要
    }

    /* xml构造，并有自定义style时调用 */
    public JZoomScrollView(Context context, AttributeSet set, int defStyleAttr) {
        super(context, set, defStyleAttr);
        int[] attrs = R.styleable.JZoomScrollView;
        // 1.获取attrs里定义的属性
        TypedArray typedArray = context.obtainStyledAttributes(set, attrs, defStyleAttr, 0);
        // 2.获取各项属性的值
        layoutIndex = typedArray.getInteger(R.styleable.JZoomScrollView_layout_index, 2);
        if (layoutIndex < 2) { // 最少为2
            layoutIndex = 2;
        }
        // 3.最后关闭
        typedArray.recycle();
    }

    /* 当View中所有的子控件 均被映射成xml后触发 在onLayout之前执行 */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOverScrollMode(OVER_SCROLL_NEVER); // 不允许滚动超出边界
        ViewGroup group = (ViewGroup) getChildAt(0);
        for (int i = 2; i < Integer.MAX_VALUE; i++) {
            if (i < layoutIndex) { // 继续递归
                group = (ViewGroup) group.getChildAt(0);
            } else { // 当前这层viewGroup的顶View需要伸缩
                dropZoomView = group.getChildAt(0);
                setOnTouchListener(this);
            }
        }
    }

    /* 触摸机制 */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (dropZoomViewWidth <= 0 || dropZoomViewHeight <= 0) {
            dropZoomViewWidth = dropZoomView.getMeasuredWidth();
            dropZoomViewHeight = dropZoomView.getMeasuredHeight();
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP: // 手指离开后恢复图片
                mScaling = false;
                replyImage();
                break;
            case MotionEvent.ACTION_MOVE: // 手指按下时计算伸缩
                if (!mScaling) {
                    if (getScrollY() == 0) {
                        mFirstPosition = event.getY(); // 滚动到顶部时记录位置，否则正常返回
                    } else {
                        break;
                    }
                }
                int distance = (int) ((event.getY() - mFirstPosition) * 0.6); // 滚动距离乘以一个系数
                if (distance < 0) { // 当前位置比记录位置要小，正常返回
                    break;
                }

                // 处理放大
                mScaling = true;
                setZoom(1 + distance);
                return true; // 返回true表示已经完成触摸事件，不再处理
        }
        return false;
    }

    /* 回弹动画 */
    public void replyImage() {
        // 计算没伸缩时候的宽度
        final float distance = dropZoomView.getMeasuredWidth() - dropZoomViewWidth;
        // 属性动画(没有设置View？)
        ValueAnimator anim = ObjectAnimator.ofFloat(0.0F, 1.0F).setDuration((long) (distance * 0.7));
        // 动画监听(这里是用setLayoutParams来模仿属性动画吗)
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float cVal = (Float) animation.getAnimatedValue();
                setZoom(distance - ((distance) * cVal));
            }
        });
        anim.start();
    }

    /* 伸缩 */
    public void setZoom(float s) {
        if (dropZoomViewHeight <= 0 || dropZoomViewWidth <= 0) {
            return;
        }
        ViewGroup.LayoutParams lp = dropZoomView.getLayoutParams();
        lp.width = (int) (dropZoomViewWidth + s); // 为什么宽高的伸缩规则不一样？
        lp.height = (int) (dropZoomViewHeight * ((dropZoomViewWidth + s) / dropZoomViewWidth));
        dropZoomView.setLayoutParams(lp);
    }
}
