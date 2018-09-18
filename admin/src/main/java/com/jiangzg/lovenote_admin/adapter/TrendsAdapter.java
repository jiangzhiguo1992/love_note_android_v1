package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.CoupleDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Trends;

/**
 * Created by JZG on 2018/3/13.
 * trends适配器
 */
public class TrendsAdapter extends BaseQuickAdapter<Trends, BaseViewHolder> {

    private BaseActivity mActivity;

    public TrendsAdapter(BaseActivity activity) {
        super(R.layout.list_item_trends);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Trends item) {
        // data
        String id = "id:" + item.getId();
        String uid = "uid:" + item.getUserId();
        String cid = "cid:" + item.getCoupleId();
        String create = DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String actType = Trends.getActShow(item.getActionType(), item.getContentId());
        String conType = Trends.getContentShow(item.getContentType());
        String conId = String.valueOf(item.getContentId());
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, uid);
        helper.setText(R.id.tvCid, cid);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvActType, actType);
        helper.setText(R.id.tvConType, conType);
        helper.setText(R.id.tvConId, conId);
    }

    public void goCouple(final int position) {
        Trends item = getItem(position);
        CoupleDetailActivity.goActivity(mActivity, item.getCoupleId(), 0);
    }

}
