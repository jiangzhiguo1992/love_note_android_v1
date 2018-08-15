package com.jiangzg.lovenote.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.common.MapShowActivity;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Place;
import com.jiangzg.lovenote.helper.ApiHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;

/**
 * Created by JZG on 2018/3/13.
 * 地址适配器
 */
public class PlaceAdapter extends BaseMultiItemQuickAdapter<Place, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final Couple couple;
    private final String formatNo;
    private final String formatAddress;

    public PlaceAdapter(FragmentActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_NOTE_MY, R.layout.list_item_place_right);
        addItemType(ApiHelper.LIST_NOTE_TA, R.layout.list_item_place_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
        formatNo = mActivity.getString(R.string.now_no);
        formatAddress = mActivity.getString(R.string.now_no_address_info);
    }

    @Override
    protected void convert(BaseViewHolder helper, Place item) {
        // data
        String avatar = Couple.getAvatar(couple, item.getUserId());
        String time = TimeHelper.getTimeShowLocal_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        String address = StringUtils.isEmpty(item.getAddress()) ? formatAddress : item.getAddress();
        String province = StringUtils.isEmail(item.getProvince()) ? formatNo : item.getProvince();
        String city = StringUtils.isEmail(item.getCity()) ? formatNo : item.getCity();
        String district = StringUtils.isEmail(item.getDistrict()) ? formatNo : item.getDistrict();
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
