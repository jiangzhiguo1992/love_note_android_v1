package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.UserDetailActivity;
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
        String create = DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String userId = "uid:" + String.valueOf(item.getUserId());
        String duration = String.valueOf(item.getDuration());
        String uri = item.getMethod() + item.getUri();
        String method = item.getPlatform() + ":" + item.getLanguage();
        boolean pEmpty = StringUtils.isEmpty(item.getParams());
        boolean bEmpty = StringUtils.isEmpty(item.getBody());
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, userId);
        helper.setText(R.id.tvDuration, duration);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvUri, uri);
        helper.setText(R.id.tvMethod, method);
        if (pEmpty && bEmpty) {
            helper.setVisible(R.id.tvParams, false);
        } else {
            helper.setVisible(R.id.tvParams, true);
            String params;
            if (bEmpty) {
                params = item.getParams();
            } else if (pEmpty) {
                params = item.getBody();
            } else {
                params = item.getParams() + "\n" + item.getBody();
            }
            helper.setText(R.id.tvParams, params);
        }
    }

    public void goUser(final int position) {
        Api item = getItem(position);
        if (item.getUserId() > 0) {
            UserDetailActivity.goActivity(mActivity, item.getUserId());
        }
    }

}
