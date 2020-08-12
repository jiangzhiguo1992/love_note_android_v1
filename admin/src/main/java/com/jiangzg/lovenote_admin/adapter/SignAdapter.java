package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.CoupleDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Sign;

/**
 * Created by JZG on 2018/3/13.
 * sign适配器
 */
public class SignAdapter extends BaseQuickAdapter<Sign, BaseViewHolder> {

    private BaseActivity mActivity;

    public SignAdapter(BaseActivity activity) {
        super(R.layout.list_item_sign);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Sign item) {
        // data
        String id = "id:" + item.getId();
        String uid = "uid:" + item.getUserId();
        String cid = "cid:" + item.getCoupleId();
        String create = DateUtils.getStr(item.getCreateAt() * 1000, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        String year = item.getYear() + "年";
        String month = item.getMonthOfYear() + "月";
        String day = item.getDayOfMonth() + "日";
        String continueDay = String.valueOf(item.getContinueDay());
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, uid);
        helper.setText(R.id.tvCid, cid);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvYear, year);
        helper.setText(R.id.tvMonth, month);
        helper.setText(R.id.tvDay, day);
        helper.setText(R.id.tvContinue, continueDay);
    }

    public void goCouple(final int position) {
        Sign item = getItem(position);
        CoupleDetailActivity.goActivity(mActivity, item.getCoupleId(), 0);
    }

}
