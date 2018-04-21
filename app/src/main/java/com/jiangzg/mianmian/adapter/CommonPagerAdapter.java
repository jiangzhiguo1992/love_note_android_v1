package com.jiangzg.mianmian.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jiangzg.mianmian.base.MyApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * describe 通用ViewPager适配器
 */
public class CommonPagerAdapter<T> extends PagerAdapter {

    private Context mContext;
    private ViewPager mPager;
    private List<T> mData;
    private Timer timer;

    public CommonPagerAdapter(Context context, ViewPager pager) {
        mContext = context;
        mPager = pager;
        mData = new ArrayList<>();
    }

    public void newData(List<T> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addData(List<T> data) {
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void addData(T data) {
        if (data != null) {
            mData.add(data);
        }
        notifyDataSetChanged();
    }

    public List<T> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData.size();
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
        ImageView view = new ImageView(mContext);
        view.setScaleType(ImageView.ScaleType.CENTER_CROP);
        // setImage
        T data = mData.get(position);
        if (data instanceof String) {
            //GlideManager.loadNet(new GlideManager(mContext), data, view);
        } else {
            //GlideManager.loadNative(new GlideManager(mContext), data, view);
        }
        // addView
        container.addView(view);
        return view;
    }

    public void startAutoNext(long interval) {
        stopAutoNext();
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int currentItem = mPager.getCurrentItem();
                final int nextItem;
                if (currentItem >= CommonPagerAdapter.this.getCount() - 1) {
                    nextItem = 0;
                } else {
                    nextItem = currentItem + 1;
                }
                MyApp.get().getHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        mPager.setCurrentItem(nextItem, true);
                    }
                });
            }
        }, interval, interval);
    }

    public void stopAutoNext() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

}
