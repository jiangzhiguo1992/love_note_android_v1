package com.jiangzg.base.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.content.Context;
import android.os.Build;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeScroll;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by JZG on 2018/3/29.
 * 动画类
 */
public class AnimUtils {

    /**
     * 属性动画group使用，ViewGroup.setLayoutTransition(transition);
     * animateLayoutChanges="true"只能是系统自带的
     */
    public static LayoutTransition getAnimatorGroup(long duration, Animator appear, Animator disappear) {
        LayoutTransition transition = new LayoutTransition();
        transition.setDuration(duration);
        //transition.setAnimateParentHierarchy();
        transition.setInterpolator(LayoutTransition.CHANGE_APPEARING, new DecelerateInterpolator());
        transition.setInterpolator(LayoutTransition.CHANGE_DISAPPEARING, new DecelerateInterpolator());
        transition.setAnimator(LayoutTransition.APPEARING, appear);
        transition.setAnimator(LayoutTransition.DISAPPEARING, disappear);
        return transition;
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
        return ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
    }

    // 开始水波纹
    private void start(View ripple) {
        // get the center for the clipping circle
        int cx = (ripple.getLeft() + ripple.getRight()) / 2;
        int cy = (ripple.getTop() + ripple.getBottom()) / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(ripple.getWidth(), ripple.getHeight());

        // create the animator for this view (the start radius is zero)
        Animator anim = getCircular(ripple, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        ripple.setVisibility(View.VISIBLE);
        anim.start();
    }

    // 开始水波纹
    private void b(final View ripple) {
        // get the center for the clipping circle
        int cx = (ripple.getLeft() + ripple.getRight()) / 2;
        int cy = (ripple.getTop() + ripple.getBottom()) / 2;

        // get the initial radius for the clipping circle
        int initialRadius = ripple.getWidth();

        // create the animation (the final radius is zero)
        Animator anim = getCircular(ripple, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                ripple.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }

    // trans的标志
    public static final int TRANS_EXPLODE = 1;  // 随机边缘进出
    public static final int TRANS_SLIDE = 2;    // 指定边缘进出
    public static final int TRANS_FADE = 3;     // 透明度渐变进出

    /**
     * **********************************过渡动画 **********************************
     * <p>
     * 用于切换的场景,就是布局
     */
    public static Scene getScene(ViewGroup parent, int layoutID, Context context) {
        return Scene.getSceneForLayout(parent, layoutID, context);
    }

    /**
     * 获取自定义的Trans,也可以auto生成
     */
    public static Transition getTrans(long duration, boolean together, int mode) {
        TransitionSet set = new TransitionSet();
        set.setDuration(duration);
        set.setInterpolator(new LinearInterpolator());
        if (together) {
            set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        } else {
            set.setOrdering(TransitionSet.ORDERING_SEQUENTIAL);
        }
        if (mode == TRANS_EXPLODE) {
            set.addTransition(new Explode());
        } else if (mode == TRANS_FADE) {
            set.addTransition(new Fade(Fade.OUT)).addTransition(new Fade(Fade.IN));
        } else if (mode == TRANS_SLIDE) {
            set.addTransition(new Slide(Gravity.BOTTOM));
        }
        set.addTransition(new ChangeBounds()); // 捕捉到边界
        set.addTransition(new ChangeTransform()); // 捕捉到旋转
        set.addTransition(new ChangeImageTransform()); // 捕捉Matrix
        set.addTransition(new ChangeClipBounds()); //  捕捉裁剪
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            set.addTransition(new ChangeScroll()); // 捕捉滚动
        }
        return set;
    }

    /**
     * 使用默认的new AutoTransition()来启动的
     */
    public static void goTrans(Scene scene) {
        TransitionManager.go(scene);
    }

    /**
     * 使用的自定义的Transition来启动的
     */
    public static void goTrans(Scene scene, Transition transition) {
        TransitionManager.go(scene, transition);
    }

}
