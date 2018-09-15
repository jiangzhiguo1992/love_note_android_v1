package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.CoupleDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Coin;

/**
 * Created by JZG on 2018/3/13.
 * coin适配器
 */
public class CoinAdapter extends BaseQuickAdapter<Coin, BaseViewHolder> {

    private BaseActivity mActivity;

    public CoinAdapter(BaseActivity activity) {
        super(R.layout.list_item_coin);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Coin item) {
        // data
        String id = "id:" + item.getId();
        String uid = "uid:" + item.getUserId();
        String cid = "cid:" + item.getCoupleId();
        String bid = "bid:" + item.getBillId();
        String create = DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String kind = Coin.getKindShow(item.getKind());
        String change = item.getChange() > 0 ? "+" + item.getChange() : String.valueOf(item.getChange());
        String count = String.valueOf(item.getCount());
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, uid);
        helper.setText(R.id.tvCid, cid);
        helper.setText(R.id.tvBid, bid);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvKind, kind);
        helper.setText(R.id.tvChange, change);
        helper.setText(R.id.tvCount, count);
    }

    public void goCouple(final int position) {
        Coin item = getItem(position);
        CoupleDetailActivity.goActivity(mActivity, item.getCoupleId());
    }

}
