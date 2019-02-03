package com.jiangzg.lovenote.controller.adapter.more;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.Coin;
import com.jiangzg.lovenote.model.entity.Couple;
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
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String time = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        String kindShow = getCoinKindShow(item.getKind());
        String change;
        if (item.getChange() >= 0) {
            change = "+ " + String.valueOf(item.getChange());
        } else {
            change = String.valueOf(item.getChange());
        }
        String count = "= " + String.valueOf(item.getCount());
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar, item.getUserId());
        helper.setText(R.id.tvTime, time);
        helper.setText(R.id.tvKind, kindShow);
        helper.setText(R.id.tvChange, change);
        helper.setText(R.id.tvCount, count);
    }

    private String getCoinKindShow(int form) {
        switch (form) {
            case Coin.KIND_ADD_BY_SYS:
                return MyApp.get().getString(R.string.sys_change);
            case Coin.KIND_ADD_BY_PLAY_PAY:
                return MyApp.get().getString(R.string.pay);
            case Coin.KIND_ADD_BY_SIGN_DAY:
                return MyApp.get().getString(R.string.sign);
            case Coin.KIND_ADD_BY_MATCH_POST:
                return MyApp.get().getString(R.string.nav_match);
            case Coin.KIND_SUB_BY_MATCH_UP:
                return MyApp.get().getString(R.string.nav_match);
            case Coin.KIND_SUB_BY_WISH_UP:
                return MyApp.get().getString(R.string.nav_wish);
            case Coin.KIND_SUB_BY_CARD_UP:
                return MyApp.get().getString(R.string.nav_postcard);
        }
        return MyApp.get().getString(R.string.unknown_kind);
    }

}
