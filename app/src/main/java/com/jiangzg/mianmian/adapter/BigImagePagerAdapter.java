package com.jiangzg.mianmian.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.jiangzg.base.common.FileUtils;
import com.jiangzg.mianmian.activity.common.BigImageActivity;
import com.jiangzg.mianmian.view.GImageBigView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * describe BigImagePagerAdapter
 */
public class BigImagePagerAdapter extends PagerAdapter {

    private Context mContext;
    private int mType;
    private List<String> mData;

    public BigImagePagerAdapter(Context context) {
        mContext = context;
        mType = BigImageActivity.TYPE_OSS_SINGLE;
        mData = new ArrayList<>();
    }

    public void setType(int type) {
        mType = type;
    }

    public void setData(List<String> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public void setData(String data) {
        mData.clear();
        if (data != null) {
            mData.add(data);
        }
        notifyDataSetChanged();
    }

    public List<String> getData() {
        return mData;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        view.requestLayout();
        view.invalidate();
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
        GImageBigView ivBig = new GImageBigView(mContext);
        // setImage
        String data = mData.get(position);
        switch (mType) {
            case BigImageActivity.TYPE_FILE_SINGLE:
            case BigImageActivity.TYPE_FILE_LIST:
                ivBig.setDataFile(FileUtils.getFileByPath(data));
                break;
            case BigImageActivity.TYPE_OSS_SINGLE:
            case BigImageActivity.TYPE_OSS_LIST:
            default:
                ivBig.setDataOss(data);
                break;
        }
        // addView
        container.addView(ivBig);
        ivBig.requestLayout();
        ivBig.invalidate();
        return ivBig;
    }

}
