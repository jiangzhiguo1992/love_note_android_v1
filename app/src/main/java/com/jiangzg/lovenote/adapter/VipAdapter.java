package com.jiangzg.lovenote.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Vip;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 会员适配器
 */
public class VipAdapter extends BaseMultiItemQuickAdapter<Vip, BaseViewHolder> {

    private final Couple couple;
    private FragmentActivity mActivity;

    public VipAdapter(FragmentActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_NOTE_MY, R.layout.list_item_vip_right);
        addItemType(ApiHelper.LIST_NOTE_TA, R.layout.list_item_vip_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Vip item) {
        // data
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String time = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        int expireDays = item.getExpireDays();
        String expireAt = DateUtils.getString(TimeHelper.getJavaTimeByGo(item.getExpireAt()), ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String content = String.format(Locale.getDefault(), mActivity.getString(R.string.get_space_holder_space_day_vip_on_space_holder_space_expire), expireDays, expireAt);
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvTime, time);
        helper.setText(R.id.tvContent, content);
    }

}
