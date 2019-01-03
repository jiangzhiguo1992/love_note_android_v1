package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.UserDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Entry;

/**
 * Created by JZG on 2018/3/13.
 * Entry适配器
 */
public class EntryAdapter extends BaseQuickAdapter<Entry, BaseViewHolder> {

    private BaseActivity mActivity;

    public EntryAdapter(BaseActivity activity) {
        super(R.layout.list_item_entry);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Entry item) {
        // data
        String id = "id:" + item.getId();
        String userId = "uid:" + item.getUserId();
        String create = DateUtils.getStr(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = DateUtils.getStr(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String platform = item.getPlatform();
        String market = item.getMarket();
        String deviceName = item.getDeviceName();
        String osVersion = item.getOsVersion();
        String appVersion = String.valueOf(item.getAppVersion());
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, userId);
        helper.setText(R.id.tvDid, create);
        helper.setText(R.id.tvCreate, update);
        helper.setText(R.id.tvPlatform, platform);
        helper.setText(R.id.tvDName, deviceName);
        helper.setText(R.id.tvOsVersion, osVersion);
        helper.setText(R.id.tvMarket, market);
        helper.setText(R.id.tvAppVersion, appVersion);
    }

    public void goUser(final int position) {
        Entry item = getItem(position);
        UserDetailActivity.goActivity(mActivity, item.getUserId());
    }

}
