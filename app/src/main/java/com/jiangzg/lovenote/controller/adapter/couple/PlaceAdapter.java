package com.jiangzg.lovenote.controller.adapter.couple;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.common.MapShowActivity;
import com.jiangzg.lovenote.helper.common.CountHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.LocationHelper;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Place;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 地址适配器
 */
public class PlaceAdapter extends BaseQuickAdapter<Place, BaseViewHolder> {

    private final User me;
    private final Couple couple;
    private final String formatNo;
    private final String formatAddress;
    private final String formatDistance;
    private FragmentActivity mActivity;

    public PlaceAdapter(FragmentActivity activity) {
        super(R.layout.list_item_place);
        mActivity = activity;
        me = SPHelper.getMe();
        couple = me == null ? null : me.getCouple();
        formatNo = mActivity.getString(R.string.now_no);
        formatAddress = mActivity.getString(R.string.now_no_address_info);
        formatDistance = mActivity.getString(R.string.distance_space_holder);
    }

    @Override
    protected void convert(BaseViewHolder helper, Place item) {
        // data
        String address = StringUtils.isEmpty(item.getAddress()) ? formatAddress : item.getAddress();
        String province = StringUtils.isEmpty(item.getProvince()) ? formatNo : item.getProvince();
        String city = StringUtils.isEmpty(item.getCity()) ? formatNo : item.getCity();
        String district = StringUtils.isEmpty(item.getDistrict()) ? formatNo : item.getDistrict();
        boolean isMine = me != null && item.getUserId() == me.getId();
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String time = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        // view
        FrescoAvatarView ivAvatarRight = helper.getView(R.id.ivAvatarRight);
        FrescoAvatarView ivAvatarLeft = helper.getView(R.id.ivAvatarLeft);
        TextView tvTimeLeft = helper.getView(R.id.tvTimeLeft);
        TextView tvTimeRight = helper.getView(R.id.tvTimeRight);
        if (isMine) {
            ivAvatarLeft.setVisibility(View.INVISIBLE);
            ivAvatarRight.setVisibility(View.VISIBLE);
            tvTimeLeft.setVisibility(View.INVISIBLE);
            tvTimeRight.setVisibility(View.VISIBLE);
            // set
            ivAvatarRight.setData(avatar, item.getUserId());
            helper.setText(R.id.tvTimeRight, time);
        } else {
            ivAvatarLeft.setVisibility(View.VISIBLE);
            ivAvatarRight.setVisibility(View.INVISIBLE);
            tvTimeLeft.setVisibility(View.VISIBLE);
            tvTimeRight.setVisibility(View.INVISIBLE);
            // set
            ivAvatarLeft.setData(avatar, item.getUserId());
            helper.setText(R.id.tvTimeLeft, time);
        }
        helper.setText(R.id.tvAddress, address);
        helper.setText(R.id.tvProvince, province);
        helper.setText(R.id.tvCity, city);
        helper.setText(R.id.tvDistrict, district);
        // distance
        int position = helper.getLayoutPosition();
        if (position >= getData().size() - 1) {
            helper.setVisible(R.id.llDistance, false);
        } else {
            Place next = getItem(position + 1);
            if (next == null) {
                helper.setVisible(R.id.llDistance, false);
            } else {
                helper.setVisible(R.id.llDistance, true);
                float distance = LocationHelper.distance(item.getLongitude(), item.getLatitude(), next.getLongitude(), next.getLatitude());
                String distanceShow = CountHelper.getShowDistance(distance);
                String format = String.format(Locale.getDefault(), formatDistance, distanceShow);
                helper.setText(R.id.tvDistance, format);
            }
        }
        // click
        helper.addOnClickListener(R.id.cvPlace);
    }

    public void goPlaceDetail(int position) {
        Place item = getItem(position);
        String address = item.getAddress();
        double longitude = item.getLongitude();
        double latitude = item.getLatitude();
        MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

}
