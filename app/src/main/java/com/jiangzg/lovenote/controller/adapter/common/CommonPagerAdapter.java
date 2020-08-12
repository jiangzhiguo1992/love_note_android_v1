package com.jiangzg.lovenote.controller.adapter.common;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * describe CommonPagerAdapter
 */
public class CommonPagerAdapter extends PagerAdapter {

    private Activity mActivity;
    private List<Integer> dataList;

    public CommonPagerAdapter(Activity context) {
        mActivity = context;
        dataList = new ArrayList<>();
    }

    public void setDataList(List<Integer> dataList) {
        this.dataList.clear();
        if (dataList != null) {
            this.dataList.addAll(dataList);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dataList == null ? 0 : dataList.size();
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
        if (dataList == null || position < 0 || position >= dataList.size()) {
            return new RelativeLayout(mActivity);
        }
        int layoutId = dataList.get(position);
        View view = LayoutInflater.from(mActivity).inflate(layoutId, null);
        container.addView(view);
        return view;
    }

}
