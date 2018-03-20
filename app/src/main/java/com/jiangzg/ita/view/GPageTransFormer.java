package com.jiangzg.ita.view;

import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by JZG on 2018/3/20.
 * viewPager的切换动画
 */
public class GPageTransFormer implements ViewPager.PageTransformer {

    public static final int TYPE_COVER = 0;
    public static final int TYPE_BOX = 1;
    public static final int TYPE_SCALE = 2;
    public static final int TYPE_SCALE_FADE = 3;
    public static final int TYPE_ZOOM = 4;

    private int mType;

    public GPageTransFormer(int type) {
        mType = type;
    }

    @Override
    public void transformPage(@NonNull View page, float position) {
        // position 表示相对于当前页正中的位置
        // 0表示在正中的这个页面
        // 1表示右边一个完整的页面
        // -1表示左边一个完整的页面
        switch (mType) {
            case TYPE_COVER:
                cover(page, position);
                break;
            case TYPE_BOX:
                box(page, position);
                break;
            case TYPE_SCALE:
                scale(page, position);
                break;
            case TYPE_SCALE_FADE:
                scaleFade(page, position);
                break;
            case TYPE_ZOOM:
                zoom(page, position);
                break;
        }
    }

    private void cover(@NonNull View page, float position) {
        //這裏只對右邊的view做了操作
        if (position > 0 && position <= 1) {
            //設置該view的X軸不動，刚开始是完全在右面慢慢出现的，需要处理
            page.setTranslationX(-page.getWidth() * position);
        }
    }

    private void box(@NonNull View page, float position) {
        int pivotX = 0;
        if (position >= -1 && position < 0) {
            pivotX = page.getWidth();
        } else if (position > 0 && position <= 1) {
            pivotX = 0;
        }
        //设置x轴的锚点
        page.setPivotX(pivotX);
        //设置绕Y轴旋转的角度
        page.setRotationY(90f * position);
    }

    private void scale(@NonNull View page, float position) {
        int width = page.getWidth();
        int height = page.getHeight();
        //這裏只對右邊的view做了操作
        if (position > 0 && position <= 1) {
            //設置該view的X軸不動，刚开始是完全在右面慢慢出现的，需要处理
            page.setTranslationX(-width * position);
            //設置縮放中心點在該view的正中心
            page.setPivotX(width / 2);
            page.setPivotY(height / 2);
            //設置縮放比例（0.0，1.0]
            page.setScaleX(1 - position);
            page.setScaleY(1 - position);
        } else if (position >= -1 && position < 0) {

        }
    }

    private void scaleFade(@NonNull View page, float position) {
        final float MIN_SCALE = 0.75f;
        int pageWidth = page.getWidth();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0);

        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            page.setAlpha(1);
            page.setTranslationX(0);
            page.setScaleX(1);
            page.setScaleY(1);

        } else if (position <= 1) { // (0,1]
            // Fade the page out.
            page.setAlpha(1 - position);

            // Counteract the default slide transition
            page.setTranslationX(pageWidth * -position);

            // Scale the page down (between MIN_SCALE and 1)
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(0);
        }
    }

    private void zoom(@NonNull View page, float position) {
        final float MIN_SCALE = 0.75f;
        final float MIN_ALPHA = 0.5f;
        int pageWidth = page.getWidth();
        int pageHeight = page.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.setAlpha(0);
        } else if (position <= 1) {
            //a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
            // [-1,1]
            // Modify the default slide transition to shrink the page as well
            float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
            float vertMargin = pageHeight * (1 - scaleFactor) / 2;
            float horzMargin = pageWidth * (1 - scaleFactor) / 2;
            if (position < 0) {
                page.setTranslationX(horzMargin - vertMargin / 2);
            } else {
                page.setTranslationX(-horzMargin + vertMargin / 2);
            }

            // Scale the page down (between MIN_SCALE and 1)
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);

            // Fade the page relative to its size.
            page.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE) / (1 - MIN_SCALE) * (1 - MIN_ALPHA));

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            page.setAlpha(0);
        }
    }

}
