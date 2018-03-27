package com.jiangzg.ita.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.jiangzg.ita.R;
import com.jiangzg.ita.view.GImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

/**
 * Created by JiangZhiGuo on 2016-11-9.
 * describe 通用ViewPager适配器
 */
public class ImgScreenPagerAdapter extends PagerAdapter {

    private Context mContext;
    private List<Uri> mData;
    private Timer timer;

    public ImgScreenPagerAdapter(Context context) {
        mContext = context;
        mData = new ArrayList<>();
    }

    public void setData(List<Uri> data) {
        mData.clear();
        if (data != null) {
            mData.addAll(data);
        }
        notifyDataSetChanged();
    }

    public List<Uri> getData() {
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
        View root = LayoutInflater.from(mContext).inflate(R.layout.pager_item_img_screen, container,false);
        GImageView ivScreen = root.findViewById(R.id.ivScreen);
        // setImage
        Uri data = mData.get(position);
        ivScreen.setDataUri(data);
        // addView
        container.addView(root);
        root.requestLayout();
        root.invalidate();
        return root;
    }

}
