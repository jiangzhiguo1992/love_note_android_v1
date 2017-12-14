package com.jiangzg.base.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.DecelerateInterpolator;

/**
 * Created by gg on 2017/5/9.
 * 属性动画(ValueAnimator(TimeAnimator + ObjectAnimator))
 */
public class AnimFrame {

    // 渐变property
    public static final String ALPHA = "alpha";
    // 缩放property
    public static final String SCALE_X = "scaleX";
    public static final String SCALE_Y = "scaleY";
    // 移动property
    public static final String TRANSLATE_X = "translationX";
    public static final String TRANSLATE_Y = "translationY";
    public static final String TRANSLATE_Z = "translationZ"; //看清楚有Y轴
    // 旋转property, 看清楚是轴，不是点，所有有立体效果
    public static final String ROTATION = "rotation";//以自身中心做中心点旋转
    public static final String ROTATION_X = "rotationX";//以X轴做中心线旋转
    public static final String ROTATION_Y = "rotationY";//以Y轴做中心线旋转

    /**
     * @param view     执行动画的view，补间动画可以最后设置view
     * @param property 看上面的标志
     * @param values   除了起始值和终点值之外，还可以有过渡值
     */
    public static ObjectAnimator getAnimator(View view, String property, float... values) {
        return ObjectAnimator.ofFloat(view, property, values);
    }

    /**
     * set.start(); set.pause(); set.cancel(); set.end(); ...
     */
    public static AnimatorSet getAnimatorSet(long startDelay, long duration, boolean together, Animator... items) {
        AnimatorSet set = new AnimatorSet();
        set.setStartDelay(startDelay); // 开始延迟时间
        set.setDuration(duration); // 持续时间
        set.setInterpolator(new DecelerateInterpolator());//此处为减速
        if (together) {
            set.playTogether(items); // 一起执行
        } else {
            set.playSequentially(items); // 一个一个执行
        }
        return set;
    }

    /**
     * 属性动画group使用，ViewGroup.setLayoutTransition(transition);
     */
    public static LayoutTransition getAnimatorGroup(long duration, Animator appear, Animator disappear) {
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(duration);
        transition.setAnimator(LayoutTransition.APPEARING, appear);
        transition.setAnimator(LayoutTransition.DISAPPEARING, disappear);
        return transition;
    }

    /**
     * 监听开始，结束，取消，重复
     */
    public static void addAnimatorListener(ValueAnimator animator, Animator.AnimatorListener listener) {
        animator.addListener(listener);
    }

    public static void removeAnimatorListeners(ValueAnimator animator) {
        animator.removeAllListeners();
    }

    /**
     * ObjectAnimator继承自ValueAnimator吧，但ValueAnimator没有操作view四大属性的方法
     * 看看底下这个方法，就是给ValueAnimator用的，可以更灵活的操作view的各种属性
     */
    public static void addAnimatorUpdateListener(ValueAnimator animator, ValueAnimator.AnimatorUpdateListener listener) {
        animator.addUpdateListener(listener);
    }

    public static void removeAnimatorUpdateListeners(ValueAnimator animator) {
        animator.removeAllUpdateListeners();
    }

    /**
     * android5.0的水波纹效果也是用的Animator实现的
     * android:background="?android:attr/selectableItemBackground"波纹有边界
     * android:background="?android:attr/selectableItemBackgroundBorderless"波纹超出边界
     * android:colorControlHighlight：设置波纹颜色
     *
     * @param view        可以在View上做水波纹
     * @param centerX     这里的(0,0)是view的左上角
     * @param centerY     (view.getWidth() / 2，view.getHeight() / 2)为中心点
     * @param startRadius 圆形开始的半径
     * @param endRadius   结束时候的半径
     */
    public static Animator getCircular(View view, int centerX, int centerY, float startRadius, float endRadius) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return null;
        return ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
    }

}
