package com.android.base.view.anim;

import android.annotation.TargetApi;
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
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

/**
 * Created by gg on 2017/5/9.
 * 过渡动画(5.0布局之间切换的高级动画)
 */
public class AnimTrans {

    // trans的标志
    public static final int TRANS_EXPLODE = 1;  // 随机边缘进出
    public static final int TRANS_SLIDE = 2;    // 指定边缘进出
    public static final int TRANS_FADE = 3;     // 透明度渐变进出

    /**
     * **********************************过渡动画**********************************
     * <p>
     * 用于切换的场景,就是布局
     */
    public static Scene getScene(ViewGroup parent, int layoutID, Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) return null;
        return Scene.getSceneForLayout(parent, layoutID, context);
    }

    /**
     * 获取自定义的Trans,也可以auto生成
     */
    public static Transition getTrans(long duration, boolean together, int mode) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return null;
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
     * 监听开始、结束、取消、暂停、结束
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void addTransListener(Transition transition, Transition.TransitionListener listener) {
        transition.addListener(listener);
    }

    /**
     * 使用默认的new AutoTransition()来启动的
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void goTrans(Scene scene) {
        TransitionManager.go(scene);
    }

    /**
     * 使用的自定义的Transition来启动的
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static void goTrans(Scene scene, Transition transition) {
        TransitionManager.go(scene, transition);
    }

}
