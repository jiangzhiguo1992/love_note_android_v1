package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.CoupleDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Couple;
import com.jiangzg.lovenote_admin.view.FrescoView;

/**
 * Created by JZG on 2018/3/13.
 * couple适配器
 */
public class CoupleAdapter extends BaseQuickAdapter<Couple, BaseViewHolder> {

    private BaseActivity mActivity;
    private final int ivWidth, ivHeight;

    public CoupleAdapter(BaseActivity activity) {
        super(R.layout.list_item_couple);
        mActivity = activity;
        ivWidth = ivHeight = ConvertUtils.dp2px(30);
    }

    @Override
    protected void convert(BaseViewHolder helper, Couple item) {
        // data
        String id = "id:" + item.getId();
        String create = "c:" + DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = "u:" + DateUtils.getString(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String creatorId = "cid:" + item.getCreatorId();
        String creatorAvatar = item.getCreatorAvatar();
        String creatorName = item.getCreatorName();
        String inviteeId = "iid:" + item.getInviteeId();
        String inviteeAvatar = item.getInviteeAvatar();
        String inviteeName = item.getInviteeName();
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvUpdate, update);
        helper.setText(R.id.tvUid1, creatorId);
        helper.setText(R.id.tvName1, creatorName);
        helper.setText(R.id.tvUid2, inviteeId);
        helper.setText(R.id.tvName2, inviteeName);
        FrescoView ivAvatar1 = helper.getView(R.id.ivAvatar1);
        ivAvatar1.setWidthAndHeight(ivWidth, ivHeight);
        ivAvatar1.setData(creatorAvatar);
        FrescoView ivAvatar2 = helper.getView(R.id.ivAvatar2);
        ivAvatar2.setWidthAndHeight(ivWidth, ivHeight);
        ivAvatar2.setData(inviteeAvatar);
    }

    public void goCouple(final int position) {
        Couple item = getItem(position);
        CoupleDetailActivity.goActivity(mActivity, item.getId());
    }

}
