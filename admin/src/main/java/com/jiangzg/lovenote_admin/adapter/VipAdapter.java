package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.CoupleDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Vip;

/**
 * Created by JZG on 2018/3/13.
 * vip适配器
 */
public class VipAdapter extends BaseQuickAdapter<Vip, BaseViewHolder> {

    private BaseActivity mActivity;

    public VipAdapter(BaseActivity activity) {
        super(R.layout.list_item_vip);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Vip item) {
        // data
        String id = "id:" + item.getId();
        String uid = "uid:" + item.getUserId();
        String cid = "cid:" + item.getCoupleId();
        String bid = "bid:" + item.getBillId();
        String create = DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String fromType = Vip.getFromTypeShow(item.getFromType());
        String expireDays = "天数:" + item.getExpireDays();
        String expireAt = "到期:" + DateUtils.getString(item.getExpireAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, uid);
        helper.setText(R.id.tvCid, cid);
        helper.setText(R.id.tvBid, bid);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvFromType, fromType);
        helper.setText(R.id.tvExpireDays, expireDays);
        helper.setText(R.id.tvExpireAt, expireAt);
    }

    public void goCouple(final int position) {
        Vip item = getItem(position);
        CoupleDetailActivity.goActivity(mActivity, item.getCoupleId());
    }

}
