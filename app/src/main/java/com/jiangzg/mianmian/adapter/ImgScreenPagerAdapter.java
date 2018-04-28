package com.jiangzg.mianmian.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jiangzg.base.common.FileUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.ImgScreenActivity;
import com.jiangzg.mianmian.view.GImageBigView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * describe 通用ViewPager适配器
 */
public class ImgScreenPagerAdapter extends PagerAdapter {

    private Context mContext;
    private int mType;
    private List<String> mData;

    public ImgScreenPagerAdapter(Context context) {
        mContext = context;
        mType = ImgScreenActivity.TYPE_OSS_SINGLE;
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
        View root = LayoutInflater.from(mContext).inflate(R.layout.pager_item_img_screen, container, false);
        GImageBigView ivScreen = root.findViewById(R.id.ivScreen);
        // setImage
        String data = mData.get(position);
        switch (mType) {
            case ImgScreenActivity.TYPE_FILE_SINGLE:
            case ImgScreenActivity.TYPE_FILE_LIST:
                ivScreen.setDataFile(FileUtils.getFileByPath(data));
                break;
            case ImgScreenActivity.TYPE_OSS_SINGLE:
            case ImgScreenActivity.TYPE_OSS_LIST:
            default:
                ivScreen.setDataOss(data);
                break;
        }
        // addView
        container.addView(root);
        root.requestLayout();
        root.invalidate();
        return root;
    }

}
