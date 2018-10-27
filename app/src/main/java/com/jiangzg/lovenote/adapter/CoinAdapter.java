package com.jiangzg.lovenote.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.domain.Coin;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;

/**
 * Created by JZG on 2018/3/13.
 * 金币适配器
 */
public class CoinAdapter extends BaseMultiItemQuickAdapter<Coin, BaseViewHolder> {

    //private FragmentActivity mActivity;
    private final Couple couple;

    public CoinAdapter(FragmentActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_NOTE_MY, R.layout.list_item_coin_right);
        addItemType(ApiHelper.LIST_NOTE_TA, R.layout.list_item_coin_left);
        //mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Coin item) {
        // data
        String avatar = Couple.getAvatar(couple, item.getUserId());
        String time = TimeHelper.getTimeShowLocal_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        String kindShow = Coin.getKindShow(item.getKind());
        String change;
        if (item.getChange() >= 0) {
            change = "+" + String.valueOf(item.getChange());
        } else {
            change = String.valueOf(item.getChange());
        }
        String count = String.valueOf(item.getCount());
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvTime, time);
        helper.setText(R.id.tvKind, kindShow);
        helper.setText(R.id.tvChange, change);
        helper.setText(R.id.tvCount, count);
    }

}
