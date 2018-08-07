package com.jiangzg.lovenote.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.more.BroadcastActivity;
import com.jiangzg.lovenote.domain.BaseObj;
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

    private Context context;
    private Fragment fragment;
    private Handler mHandler;
    private MyPagerAdapter adapter;
    private int width, height;

    public BroadcastPager(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public BroadcastPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @SuppressLint("HandlerLeak")
    public void initView(Fragment fragment) {
        this.fragment = fragment;
        width = ScreenUtils.getScreenWidth(context);
        height = ConvertUtils.dp2px(130); // 和ViewPager的高度对应
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
        if (adapter == null) {
            adapter = new MyPagerAdapter();
            setAdapter(adapter);
        }
        adapter.setBroadcastList(null);
        adapter.notifyDataSetChanged();
    }

    public void setDataList(List<Broadcast> broadcastList) {
        adapter.setBroadcastList(broadcastList);
        adapter.notifyDataSetChanged();
        recycle(true);
    }

    /**
     * 轮播状态
     */
    public void recycle(boolean recycle) {
        if (recycle) {
            // 发送message开始轮播
            mHandler.sendEmptyMessageDelayed(1, 2000);
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
            if (this.broadcastList.size() <= 0) {
                Broadcast broadcast = new Broadcast();
                broadcast.setStatus(BaseObj.STATUS_DELETE);
                this.broadcastList.add(broadcast);
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

        // 这里的instant不是当前的Item吗？为什么size = 3的时候只有0和2
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, final int position) {
            int size = broadcastList.size();
            final Broadcast broadcast = broadcastList.get(position);
            View root;
            if (size <= 1 && broadcast.getStatus() == BaseObj.STATUS_DELETE) {
                // 没有广播
                root = new TextView(context);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                root.setLayoutParams(params);
                ((TextView) root).setGravity(Gravity.CENTER);
                ((TextView) root).setTextAppearance(context, R.style.FontGreyNormal);
                ((TextView) root).setText(R.string.now_no_broadcast);
            } else {
                // 有广播
                root = LayoutInflater.from(context).inflate(R.layout.pager_item_broadcast, null);
                FrescoView ivBroadcast = root.findViewById(R.id.ivBroadcast);
                TextView tvTitle = root.findViewById(R.id.tvTitle);
                TextView tvTime = root.findViewById(R.id.tvTime);
                // data
                String cover = broadcast.getCover();
                String title = broadcast.getTitle();
                String start = broadcast.getStartAt() != 0 ? TimeHelper.getTimeShowCn_MD_YMD_ByGo(broadcast.getStartAt()) : "";
                String end = broadcast.getEndAt() != 0 ? TimeHelper.getTimeShowCn_MD_YMD_ByGo(broadcast.getEndAt()) : "";
                String time = String.format(Locale.getDefault(), context.getString(R.string.holder_space_line_space_holder), start, end);
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
                                BroadcastActivity.goActivity(fragment, broadcast);
                                break;
                        }
                        return true;
                    }
                });
            }
            //一定不能少，将imageView加入到viewPager中
            container.addView(root);
            return root;
        }
    }

    ///**
    // * 事件拦截机制,处理滑动viewPager导致tab切换的bug
    // * onInterceptTouchEvent为事件拦截
    // * onTouchEvent为事件处理
    // */
    //@Override
    //public boolean dispatchTouchEvent(MotionEvent ev) {
    //    // 请求父控件不要拦截触摸事件
    //    getParent().requestDisallowInterceptTouchEvent(true);
    //    return super.dispatchTouchEvent(ev);
    //}

}
