package com.jiangzg.lovenote.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.note.GiftEditActivity;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Gift;
import com.jiangzg.lovenote.domain.RxEvent;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RecyclerHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 礼物适配器
 */
public class GiftAdapter extends BaseQuickAdapter<Gift, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final Couple couple;
    private final String formatSend;

    public GiftAdapter(FragmentActivity activity) {
        super(R.layout.list_item_gift);
        mActivity = activity;
        couple = SPHelper.getCouple();
        formatSend = mActivity.getString(R.string.on_space_holder_space_send_to);
    }

    @Override
    protected void convert(BaseViewHolder helper, Gift item) {
        String avatarLeft = Couple.getAvatar(couple, Couple.getTaId(couple, item.getReceiveId()));
        String avatarRight = Couple.getAvatar(couple, item.getReceiveId());
        String title = item.getTitle();
        String happen = TimeHelper.getTimeShowCn_HM_MD_YMD_ByGo(item.getHappenAt());
        String happenShow = String.format(Locale.getDefault(), formatSend, happen);
        List<String> imageList = item.getContentImageList();
        // view
        FrescoAvatarView ivAvatarLeft = helper.getView(R.id.ivAvatarLeft);
        FrescoAvatarView ivAvatarRight = helper.getView(R.id.ivAvatarRight);
        RecyclerView rv = helper.getView(R.id.rv);
        ivAvatarLeft.setData(avatarLeft);
        ivAvatarRight.setData(avatarRight);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvHappenAt, happenShow);
        if (imageList != null && imageList.size() > 0) {
            rv.setVisibility(View.VISIBLE);
            int spanCount = imageList.size() > 3 ? 3 : imageList.size();
            new RecyclerHelper(rv)
                    .initLayoutManager(new GridLayoutManager(mActivity, spanCount))
                    .initAdapter(new ImgSquareShowAdapter(mActivity, spanCount))
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
