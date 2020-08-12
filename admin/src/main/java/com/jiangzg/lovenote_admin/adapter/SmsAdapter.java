package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.UserDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Sms;

/**
 * Created by JZG on 2018/3/13.
 * sms适配器
 */
public class SmsAdapter extends BaseQuickAdapter<Sms, BaseViewHolder> {

    private BaseActivity mActivity;

    public SmsAdapter(BaseActivity activity) {
        super(R.layout.list_item_sms);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Sms item) {
        // data
        String id = "id:" + item.getId();
        String create = DateUtils.getStr(item.getCreateAt() * 1000, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        String phone = item.getPhone();
        String sendType = Sms.getTypeShow(item.getSendType());
        String content = item.getContent();
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvPhone, phone);
        helper.setText(R.id.tvType, sendType);
        helper.setText(R.id.tvContent, content);
    }

    public void goUser(final int position) {
        Sms item = getItem(position);
        UserDetailActivity.goActivity(mActivity, item.getPhone());
    }

}
