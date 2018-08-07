package com.jiangzg.lovenote.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.common.BigImageActivity;
import com.jiangzg.lovenote.activity.common.WebActivity;
import com.jiangzg.lovenote.activity.more.BroadcastActivity;
import com.jiangzg.lovenote.domain.Broadcast;
import com.jiangzg.lovenote.helper.TimeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/8/7.
 * BroadcastViewPager
 */
public class BroadcastPager extends ViewPager {

    private Activity activity;
    private Handler mHandler;
    private MyPagerAdapter adapter;
    private int width, height;

    public BroadcastPager(@NonNull Context context) {
        super(context);
    }

    public BroadcastPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("HandlerLeak")
    public void initView(Activity activity) {
        this.activity = activity;
        width = ScreenUtils.getScreenWidth(activity);
        height = ConvertUtils.dp2px(180); // 和ViewPager的高度对应
        if (mHandler == null) {
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case 1:
                            if (adapter == null) break;
                            int nextItem = BroadcastPager.this.getCurrentItem() + 1;
                            if (nextItem > adapter.getBroadcastList().size() - 1) {
                                nextItem = 0;
                            }
                            BroadcastPager.this.setCurrentItem(nextItem);
                            recycle(true);
                            break;
                    }
                }
            };
        }
        this.setPageTransformer(true, new GPageTransFormer(GPageTransFormer.TYPE_SCALE_FADE));
    }

    public void setDataList(List<Broadcast> broadcastList) {
        recycle(false);
        if (adapter == null) {
            adapter = new MyPagerAdapter();
            setAdapter(adapter);
        }
        adapter.setBroadcastList(broadcastList);
        adapter.notifyDataSetChanged();
        this.setOffscreenPageLimit(broadcastList == null ? 0 : broadcastList.size());
        recycle(true);
    }

    /**
     * 轮播状态
     */
    public void recycle(boolean recycle) {
        if (recycle) {
            // 发送message开始轮播
            mHandler.sendEmptyMessageDelayed(1, 4000);
        } else {
            // 清空消息队列,停止轮播
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    // MyPagerAdapter
    private class MyPagerAdapter extends PagerAdapter {

        private List<Broadcast> broadcastList = new ArrayList<>();

        public List<Broadcast> getBroadcastList() {
            return broadcastList;
        }

        public void setBroadcastList(List<Broadcast> broadcastList) {
            if (broadcastList == null) {
                this.broadcastList = new ArrayList<>();
            } else {
                this.broadcastList = broadcastList;
            }
        }

        @Override
        public int getCount() {
            return broadcastList.size();
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
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            // view
            View root = LayoutInflater.from(activity).inflate(R.layout.pager_item_broadcast, null);
            FrescoView ivBroadcast = root.findViewById(R.id.ivBroadcast);
            TextView tvTitle = root.findViewById(R.id.tvTitle);
            TextView tvTime = root.findViewById(R.id.tvTime);
            // data
            final Broadcast broadcast = broadcastList.get(position);
            String cover = broadcast.getCover();
            String title = broadcast.getTitle();
            String start = broadcast.getStartAt() != 0 ? TimeHelper.getTimeShowLine_MD_YMD_ByGo(broadcast.getStartAt()) : "";
            String end = broadcast.getEndAt() != 0 ? TimeHelper.getTimeShowLine_MD_YMD_ByGo(broadcast.getEndAt()) : "";
            String time = String.format(Locale.getDefault(), activity.getString(R.string.holder_space_space_holder), start, end);
            // set
            ivBroadcast.setWidthAndHeight(width, height);
            ivBroadcast.setData(cover);
            tvTitle.setText(title);
            tvTime.setText(time);
            // 触摸监听
            root.setOnTouchListener(new OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN: // 按下时清空消息队列,停止轮播
                            recycle(false);
                            break;
                        case MotionEvent.ACTION_CANCEL: // 动作滑动一半取消后,继续发送轮播消息
                            recycle(true);
                            break;
                        case MotionEvent.ACTION_UP: // 抬起后,进入详情页面
                            recycle(true);
                            go(broadcast);
                            break;
                    }
                    return true;
                }
            });
            //一定不能少，将imageView加入到viewPager中
            container.addView(root);
            return root;
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 请求父控件不要拦截触摸事件
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    private void go(Broadcast broadcast) {
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

}
