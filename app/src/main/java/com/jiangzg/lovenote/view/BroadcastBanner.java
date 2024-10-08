package com.jiangzg.lovenote.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.controller.activity.common.BigImageActivity;
import com.jiangzg.lovenote.controller.activity.common.WebActivity;
import com.jiangzg.lovenote.controller.activity.more.BroadcastActivity;
import com.jiangzg.lovenote.model.entity.Broadcast;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoaderInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZG on 2018/8/8.
 * BroadcastBanner
 */
public class BroadcastBanner extends Banner {

    private Activity activity;
    private List<Broadcast> broadcastList;

    public BroadcastBanner(Context context) {
        super(context);
    }

    public BroadcastBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BroadcastBanner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 请求父控件不要拦截触摸事件
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    public void initView(Activity activity) {
        this.activity = activity;
        this.setImageLoader(new BroadcastLoader()); // 图片加载
        this.setBannerStyle(BannerConfig.CIRCLE_INDICATOR); // 标题 下标
        //this.setIndicatorGravity(BannerConfig.RIGHT); // 下标右
        this.setBannerAnimation(Transformer.Accordion); // 动画
        this.isAutoPlay(true); // 自动播放
        this.setViewPagerIsScroll(true); // 手动滑动
        this.setDelayTime(4000); // 轮播间隔
        this.start();
        this.setOnBannerListener(position -> {
            if (broadcastList == null || broadcastList.size() <= position) return;
            goBroadcast(broadcastList.get(position));
        });
    }

    public void setDataList(List<Broadcast> list) {
        broadcastList = list;
        if (broadcastList == null || broadcastList.size() <= 0) {
            broadcastList = new ArrayList<>();
        }
        List<String> imageList = new ArrayList<>();
        List<String> titleList = new ArrayList<>();
        for (Broadcast broadcast : broadcastList) {
            if (broadcast == null) continue;
            imageList.add(broadcast.getCover());
            titleList.add(broadcast.getTitle());
        }
        this.update(imageList, titleList);
        int size = Math.max(imageList.size(), titleList.size());
        this.setOffscreenPageLimit(size);
    }

    private void goBroadcast(Broadcast broadcast) {
        if (broadcast == null) return;
        switch (broadcast.getContentType()) {
            case Broadcast.TYPE_URL: // 网页
                WebActivity.goActivity(activity, broadcast.getTitle(), broadcast.getContentText());
                break;
            case Broadcast.TYPE_IMAGE: // 图片
                BigImageActivity.goActivityByOss(activity, broadcast.getContentText(), null);
                break;
            case Broadcast.TYPE_TEXT: // 文字
            default:
                BroadcastActivity.goActivity(activity, broadcast);
                break;
        }
    }

    // BroadcastLoader
    private static class BroadcastLoader implements ImageLoaderInterface<FrescoView> {

        @Override
        public void displayImage(Context context, Object path, FrescoView view) {
            String url = (String) path;
            view.setWidthAndHeight(ScreenUtils.getScreenWidth(context), ConvertUtils.dp2px(160));
            view.setData(url);
        }

        // 提供createImageView 方法，如果不用可以不重写这个方法，主要是方便自定义ImageView的创建
        @Override
        public FrescoView createImageView(Context context) {
            FrescoView view = new FrescoView(context);
            view.initHierarchy(null, true, true, true, true, true, true);
            return view;
        }
    }
}
