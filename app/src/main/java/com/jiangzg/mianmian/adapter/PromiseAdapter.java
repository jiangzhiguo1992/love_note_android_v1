package com.jiangzg.mianmian.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.PromiseDetailActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Promise;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;

/**
 * Created by JZG on 2018/3/13.
 * 梦境适配器
 */
public class PromiseAdapter extends BaseQuickAdapter<Promise, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final Couple couple;

    public PromiseAdapter(FragmentActivity activity) {
        super(R.layout.list_item_promise);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Promise item) {
        String avatar = Couple.getAvatar(couple, item.getHappenId());
        String happen = TimeHelper.getTimeShowCn_HM_MD_YMD_ByGo(item.getHappenAt());
        String breakCount = String.valueOf(item.getBreakCount());
        String content = item.getContentText();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvBreakCount, breakCount);
        helper.setText(R.id.tvContent, content);
    }

    public void goPromiseDetail(int position) {
        Promise item = getItem(position);
        PromiseDetailActivity.goActivity(mActivity, item);
    }

    public void selectPromise(int position) {
        mActivity.finish(); // 必须先关闭
        Promise item = getItem(position);
        RxEvent<Promise> event = new RxEvent<>(ConsHelper.EVENT_PROMISE_SELECT, item);
        RxBus.post(event);
    }

}
