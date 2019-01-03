package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.UserDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Couple;

/**
 * Created by JZG on 2018/3/13.
 * state适配器
 */
public class CoupleStateAdapter extends BaseQuickAdapter<Couple.State, BaseViewHolder> {

    private BaseActivity mActivity;

    public CoupleStateAdapter(BaseActivity activity) {
        super(R.layout.list_item_couple_state);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Couple.State item) {
        // data
        String id = "id:" + item.getId();
        String uid = "uid:" + item.getUserId();
        String cid = "cid:" + item.getCoupleId();
        String create = DateUtils.getStr(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String state = Couple.getStateShow(item.getState());
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, uid);
        helper.setText(R.id.tvCid, cid);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvState, state);
    }

    public void goUser(final int position) {
        Couple.State item = getItem(position);
        UserDetailActivity.goActivity(mActivity, item.getUserId());
    }

}
