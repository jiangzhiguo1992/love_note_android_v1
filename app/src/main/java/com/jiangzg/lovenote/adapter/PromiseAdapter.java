package com.jiangzg.lovenote.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.note.PromiseDetailActivity;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Promise;
import com.jiangzg.lovenote.domain.RxEvent;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;

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
