package com.jiangzg.lovenote.controller.adapter.couple;

import android.app.Activity;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.CoupleState;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/12.
 * 配对状态适配器
 */
public class CoupleStateAdapter extends BaseQuickAdapter<CoupleState, BaseViewHolder> {

    private Activity mActivity;
    private User me;
    private Couple couple;

    public CoupleStateAdapter(Activity activity) {
        super(R.layout.list_item_couple_state);
        mActivity = activity;
        me = SPHelper.getMe();
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, CoupleState item) {
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String createAt = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        String state = getCoupleStateShow(item.getState());
        String content = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_space_space_holder), createAt, state);
        boolean isMine = me != null && item.getUserId() == me.getId();
        // view
        FrescoAvatarView ivAvatarLeft = helper.getView(R.id.ivAvatarLeft);
        FrescoAvatarView ivAvatarRight = helper.getView(R.id.ivAvatarRight);
        if (isMine) {
            ivAvatarLeft.setVisibility(View.INVISIBLE);
            ivAvatarRight.setVisibility(View.VISIBLE);
            ivAvatarRight.setData(avatar, item.getUserId());
        } else {
            ivAvatarLeft.setVisibility(View.VISIBLE);
            ivAvatarRight.setVisibility(View.INVISIBLE);
            ivAvatarLeft.setData(avatar, item.getUserId());
        }
        helper.setText(R.id.tvContent, content);
    }

    private String getCoupleStateShow(int state) {
        switch (state) {
            case CoupleState.STATUS_INVITE_CANCEL:
                return MyApp.get().getString(R.string.couple_state_110_show);
            case CoupleState.STATUS_INVITE_REJECT:
                return MyApp.get().getString(R.string.couple_state_120_show);
            case CoupleState.STATUS_BREAK:
                return MyApp.get().getString(R.string.couple_state_210_show);
            case CoupleState.STATUS_BREAK_ACCEPT:
                return MyApp.get().getString(R.string.couple_state_220_show);
            case CoupleState.STATUS_TOGETHER:
                return MyApp.get().getString(R.string.couple_state_520_show);
            case CoupleState.STATUS_INVITE:
            default:
                return MyApp.get().getString(R.string.couple_state_0_show);
        }
    }

}
