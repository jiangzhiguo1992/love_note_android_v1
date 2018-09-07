package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.UserActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Api;

/**
 * Created by JZG on 2018/3/13.
 * Api适配器
 */
public class ApiAdapter extends BaseQuickAdapter<Api, BaseViewHolder> {

    private BaseActivity mActivity;

    public ApiAdapter(BaseActivity activity) {
        super(R.layout.list_item_api);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Api item) {
        // data
        String id = "id:" + item.getId();
        String create = "create:" + DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String update = "update:" + DateUtils.getString(item.getUpdateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String userId = String.valueOf(item.getUserId());
        String duration = String.valueOf(item.getDuration());
        String uri = item.getUri();
        String method = item.getMethod();
        String params = item.getParams();
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvUpdate, update);
        helper.setText(R.id.tvUid, "uid:" + userId);
        helper.setText(R.id.tvDuration, "d:" + duration);
        helper.setText(R.id.tvUri, "u:" + uri);
        helper.setText(R.id.tvMethod, "m:" + method);
        helper.setText(R.id.tvParams, "p:" + params);
    }

    public void goUser(final int position) {
        Api item = getItem(position);
        if (item.getUserId() > 0) {
            UserActivity.goActivity(mActivity, item.getUserId());
        }
    }

}
