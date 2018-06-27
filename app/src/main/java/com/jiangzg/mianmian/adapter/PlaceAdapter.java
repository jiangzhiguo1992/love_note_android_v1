package com.jiangzg.mianmian.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.MapShowActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Place;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;

/**
 * Created by JZG on 2018/3/13.
 * 登录信息适配器
 */
public class PlaceAdapter extends BaseMultiItemQuickAdapter<Place, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final Couple couple;

    public PlaceAdapter(FragmentActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_MY, R.layout.list_item_place_right);
        addItemType(ApiHelper.LIST_TA, R.layout.list_item_place_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Place item) {
        // data
        String avatar = Couple.getAvatar(couple, item.getUserId());
        String time = TimeHelper.getTimeShowCnSpace_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        String address = StringUtils.isEmpty(item.getAddress()) ? mActivity.getString(R.string.now_no_address_info) : item.getAddress();
        String province = StringUtils.isEmail(item.getProvince()) ? mActivity.getString(R.string.now_no) : item.getProvince();
        String city = StringUtils.isEmail(item.getCity()) ? mActivity.getString(R.string.now_no) : item.getCity();
        String district = StringUtils.isEmail(item.getDistrict()) ? mActivity.getString(R.string.now_no) : item.getDistrict();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvTime, time);
        helper.setText(R.id.tvAddress, address);
        helper.setText(R.id.tvProvince, province);
        helper.setText(R.id.tvCity, city);
        helper.setText(R.id.tvDistrict, district);
    }

    public void goDiaryDetail(int position) {
        Place item = getItem(position);
        String address = item.getAddress();
        double longitude = item.getLongitude();
        double latitude = item.getLatitude();
        MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

}
