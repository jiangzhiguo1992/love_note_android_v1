package com.jiangzg.lovenote.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.jiangzg.base.common.FileUtils;
import com.jiangzg.lovenote.activity.common.BigImageActivity;
import com.jiangzg.lovenote.view.FrescoBigView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * describe BigImagePagerAdapter
 */
public class BigImagePagerAdapter extends PagerAdapter {

    private Activity mActivity;
    private List<String> mData;
    private int mType;
    private onTapListener tapListener;

    public BigImagePagerAdapter(Activity context, int type, onTapListener tapListener) {
        mActivity = context;
        mData = new ArrayList<>();
        mType = type;
        this.tapListener = tapListener;
    }

    public void setData(List<String> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<String> getData() {
        return mData;
    }

    public void setData(String data) {
        mData.clear();
        if (data != null) {
            mData.add(data);
        }
        notifyDataSetChanged();
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
        // BigImageView
        FrescoBigView ivBig = new FrescoBigView(mActivity);
        String data = mData.get(position);
        switch (mType) {
            case BigImageActivity.TYPE_FILE_SINGLE:
            case BigImageActivity.TYPE_FILE_LIST:
                ivBig.setDataFile(FileUtils.getFileByPath(data));
                break;
            case BigImageActivity.TYPE_OSS_SINGLE:
            case BigImageActivity.TYPE_OSS_LIST:
            default:
                ivBig.setData(data);
                break;
        }
        // 单击退出全屏图
        ivBig.setOnPhotoTapListener((view, x, y) -> {
            if (tapListener != null) {
                tapListener.onTab(view, x, y);
            }
        });
        // addView
        container.addView(ivBig);
        return ivBig;
    }

    public interface onTapListener {
        void onTab(View view, float x, float y);
    }

}
