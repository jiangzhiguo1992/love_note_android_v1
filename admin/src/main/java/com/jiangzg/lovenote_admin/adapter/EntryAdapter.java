package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.UserActivity;
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
        String create = "create:" + DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = "update:" + DateUtils.getString(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String userId = String.valueOf(item.getUserId());
        String platform = item.getPlatform();
        String market = item.getMarket();
        String deviceId = item.getDeviceId();
        String deviceName = item.getDeviceName();
        String osVersion = item.getOsVersion();
        String appVersion = String.valueOf(item.getAppVersion());
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvUpdate, update);
        helper.setText(R.id.tvUid, "uid:" + userId);
        helper.setText(R.id.tvPlatform, "p:" + platform);
        helper.setText(R.id.tvMarket, "m:" + market);
        helper.setText(R.id.tvDid, "did:" + deviceId);
        helper.setText(R.id.tvDName, "dn:" + deviceName);
        helper.setText(R.id.tvOsVersion, "ov:" + osVersion);
        helper.setText(R.id.tvAppVersion, "av:" + appVersion);
    }

    public void goUser(final int position) {
        Entry item = getItem(position);
        UserActivity.goActivity(mActivity, item.getUserId());
    }

}
