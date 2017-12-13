package com.android.base.view.anim;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

/**
 * Created by Jiang on 2016/06/01
 * 1.帧动画 AnimationDrawable ---> 是drawable的子类
 */
public class AnimDrawable {

    private static final String LOG_TAG = "AnimDrawable";

    /**
     * **********************************帧动画**********************************
     *
     * @param image      加载帧动画的imageView
     * @param animListID res的drawable目录下定义：下面的true为重复播放
     *                   <animation-list android:oneshot="true" >
     *                   <item
     *                   android:drawable="@drawable/on_001"
     *                   android:duration="100"/>
     *                   .........
     *                   </animation-list>
     * @return Drawable可以强转成AnimationDrawable , animation.start(); animation.stop();
     */
    public static AnimationDrawable getAnimationDrawable(Context context, ImageView image, int animListID) {
        AnimationDrawable animation = (AnimationDrawable) ContextCompat.getDrawable(context, animListID);
        image.setImageDrawable(animation);
        return animation;
    }

}
