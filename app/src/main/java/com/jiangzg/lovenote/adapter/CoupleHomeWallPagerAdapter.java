package com.jiangzg.lovenote.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ViewFlipper;

import com.facebook.drawee.drawable.ScalingUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.view.FrescoView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * CoupleHome ViewPager适配器
 */
public class CoupleHomeWallPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<String> ossKeyList;
    private Timer timer;
    private ViewPager mPager;
    private final int screenWidth;
    private final int screenHeight;
    private int mChildCount;

    public CoupleHomeWallPagerAdapter(Context context, ViewPager pager) {
        mContext = context;
        ossKeyList = new ArrayList<>();
        mPager = pager;
        screenWidth = ScreenUtils.getScreenWidth(context);
        screenHeight = ScreenUtils.getScreenHeight(context);
        mChildCount = 0;
    }

    public void newData(String data) {
        mChildCount = getCount();
        ossKeyList.clear();
        ossKeyList.add(data);
        notifyDataSetChanged();
        mPager.setOffscreenPageLimit(1);
        stopAutoNext();
    }

    public void newData(List<String> data) {
        mChildCount = getCount();
        int originalSize = ossKeyList.size();
        ossKeyList.clear();
        if (data != null && data.size() > 0) {
            Collections.shuffle(data); // 轮播的时候也会随机，这里防止第一张不随机
            ossKeyList.addAll(data);
            notifyDataSetChanged();
            if (originalSize != ossKeyList.size()) {
                // 记得设置阔值，要不nextInt会超标
                mPager.setOffscreenPageLimit(ossKeyList.size());
                // 数量不同时才会重置动画，以免重置时间过长，导致动画结束时，还没有过场
                startAutoNext();
            }
        } else {
            mPager.setOffscreenPageLimit(1);
            stopAutoNext();
        }
    }

    // 重写getItemPosition方法,解决notifyDataSetChanged单个item不刷新
    // 但是只有在item只有一个的时候重写，其他时候动画会混乱
    @Override
    public int getItemPosition(@NonNull Object object) {
        if (ossKeyList != null && ossKeyList.size() == 1) {
            if (mChildCount > 0) {
                mChildCount--;
                return POSITION_NONE; // 返回POSITION_NONE
            }
        }
        return super.getItemPosition(object);
    }

    @Override
    public int getCount() {
        return ossKeyList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        // view
        FrescoView imageView = new FrescoView(mContext);
        ViewFlipper.LayoutParams paramsImage = new ViewFlipper.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(paramsImage);
        imageView.setWidthAndHeight(screenWidth / 2, screenHeight / 2);
        imageView.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.CENTER_CROP);
        imageView.getHierarchy().setFadeDuration(0);
        // data
        String ossKey = ossKeyList.get(position);
        imageView.setData(ossKey);
        // addView
        container.addView(imageView);
        return imageView;
    }

    // 开始轮播
    private void startAutoNext() {
        if (ossKeyList == null || ossKeyList.size() <= 1) return;
        // 停止其他pager的动画
        stopOtherAnimation();
        // 必须停止，要不会有多个timer任务一起执行
        stopAutoNext();
        if (timer == null) {
            timer = new Timer();
        }
        // 第一个页面别忘了加动画
        View firstChild = mPager.getChildAt(0);
        if (firstChild != null) firstChild.startAnimation(getAnimation());
        // 开始轮播
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mPager == null) return;
                        if (ossKeyList == null || ossKeyList.size() <= 1) return;
                        // 停止其他pager的动画
                        stopOtherAnimation();
                        // 随机展示
                        final int nextInt = new Random().nextInt(ossKeyList.size());
                        int shouldIndex = nextInt;
                        if (nextInt == mPager.getCurrentItem()) {
                            if (nextInt >= ossKeyList.size() - 1) {
                                shouldIndex = 0;
                            } else {
                                shouldIndex = nextInt + 1;
                            }
                        }
                        // 不要动画(倒退时，也会有动画)
                        mPager.setCurrentItem(shouldIndex, false);
                        // Animation
                        View child = mPager.getChildAt(shouldIndex);
                        if (child != null) {
                            child.startAnimation(getAnimation());
                        } else {
                            LogUtils.w(CoupleHomeWallPagerAdapter.class, "startAutoNext", "child == null");
                        }
                    }
                });
            }
        }, 10000, 10000);
    }

    // 停止轮播
    private void stopAutoNext() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    // 停止其他页面动画
    private void stopOtherAnimation() {
        if (mPager == null) return;
        //int currentItem = mPager.getCurrentItem();
        int childCount = mPager.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = mPager.getChildAt(i);
            if (child == null) continue;
            //if (i != currentItem) {
            child.clearAnimation();
            //}
        }
    }

    // 获取anim
    private Animation getAnimation() {
        Animation in = AnimationUtils.loadAnimation(mContext, R.anim.alpha_couple_bg_in);
        in.setInterpolator(new DecelerateInterpolator());
        in.setStartTime(AnimationUtils.currentAnimationTimeMillis());
        return in;
    }

}
