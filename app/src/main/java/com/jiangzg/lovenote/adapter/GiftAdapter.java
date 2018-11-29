package com.jiangzg.lovenote.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.note.GiftEditActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Gift;
import com.jiangzg.lovenote.model.entity.RxEvent;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.util.List;

/**
 * Created by JZG on 2018/3/13.
 * 礼物适配器
 */
public class GiftAdapter extends BaseQuickAdapter<Gift, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final Couple couple;

    public GiftAdapter(FragmentActivity activity) {
        super(R.layout.list_item_gift);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Gift item) {
        String avatar = Couple.getAvatar(couple, item.getReceiveId());
        String title = item.getTitle();
        String happen = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(item.getHappenAt());
        List<String> imageList = item.getContentImageList();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvHappenAt, happen);
        RecyclerView rv = helper.getView(R.id.rv);
        if (imageList != null && imageList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            new RecyclerHelper(rv)
                    .initLayoutManager(new GridLayoutManager(mActivity, 3))
                    .initAdapter(new ImgSquareShowAdapter(mActivity, 3))
                    .setAdapter()
                    .dataNew(imageList, 0);
        } else {
            rv.setVisibility(View.GONE);
        }
        // click
        helper.addOnClickListener(R.id.rv);
    }

    public void goEditActivity(int position) {
        Gift item = getItem(position);
        GiftEditActivity.goActivity(mActivity, item);
    }

    public void selectGift(int position) {
        mActivity.finish(); // 必须先关闭
        Gift item = getItem(position);
        RxEvent<Gift> event = new RxEvent<>(ConsHelper.EVENT_GIFT_SELECT, item);
        RxBus.post(event);
    }

}
