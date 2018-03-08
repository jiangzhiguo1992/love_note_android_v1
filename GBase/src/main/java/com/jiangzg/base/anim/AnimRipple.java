package com.jiangzg.base.anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.jiangzg.base.R;

/**
 * Created by JZG on 2018/3/8.
 * 水波纹动画
 */

public class AnimRipple {

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
}
