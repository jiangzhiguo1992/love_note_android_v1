package com.jiangzg.lovenote.adapter;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.CalUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.common.MapShowActivity;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Place;
import com.jiangzg.lovenote.helper.CountHelper;
import com.jiangzg.lovenote.helper.LocationHelper;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 地址适配器
 */
public class PlaceAdapter extends BaseQuickAdapter<Place, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final Couple couple;
    private final String formatNo;
    private final String formatAddress;
    private final String formatDistance;

    public PlaceAdapter(FragmentActivity activity) {
        super(R.layout.list_item_place);
        mActivity = activity;
        couple = SPHelper.getCouple();
        formatNo = mActivity.getString(R.string.now_no);
        formatAddress = mActivity.getString(R.string.now_no_address_info);
        formatDistance = mActivity.getString(R.string.distance_space_holder);
    }

    @Override
    protected void convert(BaseViewHolder helper, Place item) {
        // data
        boolean isMine = item.getUserId() == SPHelper.getMe().getId();
        String avatar = Couple.getAvatar(couple, item.getUserId());
        long createAt = TimeHelper.getJavaTimeByGo(item.getCreateAt());
        boolean sameDay = CalUtils.isSameDay(DateUtils.getCalendar(createAt), DateUtils.getCurrentCalendar());
        String date = DateUtils.getString(createAt, ConstantUtils.FORMAT_LINE_M_D);
        String clock = DateUtils.getString(createAt, ConstantUtils.FORMAT_H_M);
        String address = StringUtils.isEmpty(item.getAddress()) ? formatAddress : item.getAddress();
        String province = StringUtils.isEmail(item.getProvince()) ? formatNo : item.getProvince();
        String city = StringUtils.isEmail(item.getCity()) ? formatNo : item.getCity();
        String district = StringUtils.isEmail(item.getDistrict()) ? formatNo : item.getDistrict();
        // view
        LinearLayout llLeft = helper.getView(R.id.llLeft);
        LinearLayout llRight = helper.getView(R.id.llRight);
        if (isMine) {
            helper.setVisible(R.id.tvDateLeft, false);
            helper.setVisible(R.id.tvClockLeft, false);
            llLeft.setVisibility(View.INVISIBLE);
            llRight.setVisibility(View.VISIBLE);
            helper.setVisible(R.id.tvDateRight, !sameDay);
            helper.setVisible(R.id.tvClockRight, true);
            // set
            FrescoAvatarView ivAvatarRight = helper.getView(R.id.ivAvatarRight);
            ivAvatarRight.setData(avatar);
            helper.setText(R.id.tvDateRight, date);
            helper.setText(R.id.tvClockRight, clock);
        } else {
            helper.setVisible(R.id.tvDateRight, false);
            helper.setVisible(R.id.tvClockRight, false);
            llRight.setVisibility(View.INVISIBLE);
            llLeft.setVisibility(View.VISIBLE);
            helper.setVisible(R.id.tvDateLeft, !sameDay);
            helper.setVisible(R.id.tvClockLeft, true);
            FrescoAvatarView ivAvatarLeft = helper.getView(R.id.ivAvatarLeft);
            // set
            ivAvatarLeft.setData(avatar);
            helper.setText(R.id.tvDateLeft, date);
            helper.setText(R.id.tvClockLeft, clock);
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
