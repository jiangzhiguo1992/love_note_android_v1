package com.jiangzg.base.anim;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewAnimator;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import java.util.List;

/**
 * Created by gg on 2017/5/9.
 * 切换动画(ViewFlipper + ViewSwitcher(ImageSwitcher + TextSwitcher))
 */
public class AnimView {

    /**
     * **********************************切换动画**********************************
     * <p>
     * 除了可以xml里添加切换的view之外，还可以代码中动态添加
     */
    public static void addViewAnimatorChild(ViewAnimator animator, View child, int index, ViewGroup.LayoutParams params) {
        animator.addView(child, index, params);
    }

    /**
     * 开始幻灯片效果 flipper.startFlipping() 暂停 flipper.stopFlipping()
     * 也可animator.showNext() 和 animator.showPrevious()
     */
    public static ViewFlipper loadFlipper(ViewFlipper flipper, Context context, int delay, boolean auto) {
        // 也可以AnimationUtils.load创建动画
        flipper.setInAnimation(AnimationUtils.makeInAnimation(context, true));
        flipper.setOutAnimation(AnimationUtils.makeOutAnimation(context, true));
        flipper.setFlipInterval(delay); // 切换时间间隔
        flipper.setAutoStart(auto); // 是否自动幻灯片
        return flipper;
    }

    /**
     * 可通过switcher.setImageDrawable(list.get(X)) 来进行切换
     * 也可animator.showNext() 和 animator.showPrevious()
     */
    public static ImageSwitcher loadImageSwitcher(ImageSwitcher switcher, final Context context, List<Drawable> list) {
        // 也可以AnimationUtils.load创建动画
        switcher.setInAnimation(AnimationUtils.makeInAnimation(context, true));
        switcher.setOutAnimation(AnimationUtils.makeOutAnimation(context, true));
        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new ImageView(context);
            }
        });
        if (list != null && list.size() != 0)
            switcher.setImageDrawable(list.get(0)); // 开始时的展现
        return switcher;
    }

    /**
     * 可通过switcher.setCurrentText(list.get(X)) 来进行切换
     * 也可animator.showNext() 和 animator.showPrevious()
     */
    public static TextSwitcher LoadTextSwitcher(TextSwitcher switcher, final Context context, List<String> list) {
        // 也可以AnimationUtils.load创建动画
        switcher.setInAnimation(AnimationUtils.makeInAnimation(context, true));
        switcher.setOutAnimation(AnimationUtils.makeOutAnimation(context, true));

        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new TextView(context);
            }
        });
        if (list != null && list.size() != 0)
            switcher.setCurrentText(list.get(0)); // 开始时的展现
        return switcher;
    }

}
