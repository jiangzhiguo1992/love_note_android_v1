package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.UserDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.User;

/**
 * Created by JZG on 2018/3/13.
 * user适配器
 */
public class UserAdapter extends BaseQuickAdapter<User, BaseViewHolder> {

    private BaseActivity mActivity;

    public UserAdapter(BaseActivity activity) {
        super(R.layout.list_item_user);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, User item) {
        // data
        String id = "id:" + item.getId();
        String create = DateUtils.getString(item.getCreateAt() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
        String phone = item.getPhone();
        String sexShow = User.getSexShow(item.getSex());
        String birthday = DateUtils.getString(item.getBirthday() * 1000, ConstantUtils.FORMAT_LINE_Y_M_D);
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvPhone, phone);
        helper.setText(R.id.tvSex, sexShow);
        helper.setText(R.id.tvBirthday, birthday);
    }

    public void goUser(final int position) {
        User item = getItem(position);
        UserDetailActivity.goActivity(mActivity, item.getId());
    }

}
