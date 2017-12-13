package com.android.base.view.anim;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * Created by gg on 2017/5/9.
 * 补间动画(四大属性类)
 */
public class AnimTween {

    private static final String LOG_TAG = "AnimTween";

    /**
     * 渐变动画
     */
    public static AlphaAnimation getAnimationAlpha(float from, float to) {
        return new AlphaAnimation(from, to);
    }

    /**
     * 移动动画
     */
    public static TranslateAnimation getAnimationTranslate(float fromX, float toX, float fromY, float toY) {
        return new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, fromX, Animation.RELATIVE_TO_SELF, toX,
                Animation.RELATIVE_TO_SELF, fromY, Animation.RELATIVE_TO_SELF, toY);
    }

    /**
     * 旋转动画
     */
    public static RotateAnimation getAnimationRotate(float from, float to, float pivotX, float pivotY) {
        return new RotateAnimation(from, to, Animation.RELATIVE_TO_SELF,
                pivotX, Animation.RELATIVE_TO_SELF, pivotY);
    }

    /**
     * 缩放动画
     */
    public static ScaleAnimation getAnimationScale(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY) {
        return new ScaleAnimation(fromX, toX, fromY, toY, Animation.RELATIVE_TO_SELF,
                pivotX, Animation.RELATIVE_TO_SELF, pivotY);
    }

    /**
     * 多种动画效果叠加, view.setAnimation(AnimationSet); set.start();
     */
    public static AnimationSet getAnimationSet(long offset, long duration, int repeat,
                                               boolean fill, int mode, Animation... items) {
        AnimationSet set = new AnimationSet(true);
        for (Animation param : items) {
            set.addAnimation(param);
        }
        set.setStartOffset(offset); // 开始延迟时间
        set.setDuration(duration); // 持续时间
        set.setRepeatCount(repeat); // 重复次数 (默认0)
        set.setFillAfter(fill); // 是否保持在结束的位置
        set.setRepeatMode(mode); // 重复模式  (默认RESTART)
        set.setInterpolator(new DecelerateInterpolator());//此处为减速
        return set;
    }

    /**
     * 补间动画group使用，ViewGroup.setLayoutAnimation(lac);
     */
    public static LayoutAnimationController getAnimationGroup(AnimationSet set, float delay) {
        LayoutAnimationController lac = new LayoutAnimationController(set);
        lac.setDelay(delay); // 开始延迟时间
        lac.setOrder(LayoutAnimationController.ORDER_NORMAL); // 正常顺序
        lac.setInterpolator(new LinearInterpolator()); // 正常速率
        return lac;
    }

    /**
     * 补间动画是最后和view绑定和start
     */
    public static void startAnimation(View view, Animation animation) {
        // view.setAnimation(animation);
        // animation.start();
        view.startAnimation(animation);
    }

    /**
     * ViewGroup启动当时不一样
     */
    public static void startAnimationLayout(ViewGroup group, LayoutAnimationController controller) {
        group.setLayoutAnimation(controller);
        group.startLayoutAnimation();
    }

    /**
     * 监听开始, 结束, 重复
     */
    public static void setAnimationListener(Animation animation, Animation.AnimationListener listener) {
        animation.setAnimationListener(listener);
    }

    /**
     * 监听开始, 结束, 重复, 看清楚和view的加载不一样，调用者不一样
     */
    public static void setAnimationLayoutListener(ViewGroup group, Animation.AnimationListener listener) {
        group.setLayoutAnimationListener(listener);
    }

}
