package com.jiangzg.lovenote.controller.adapter.common;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/12.
 * 地图选择适配器
 */
public class MapSelectAdapter extends BaseQuickAdapter<PoiItem, BaseViewHolder> {

    private final int colorPrimary;
    private final int colorFontBlack;
    private final int colorFontWhite;
    private final int colorFontGrey;
    private final String formatDistance;
    //private BaseActivity mActivity;
    private int selectIndex = -1;

    public MapSelectAdapter(Activity activity) {
        super(R.layout.list_item_map_select);
        //mActivity = activity;
        colorPrimary = ContextCompat.getColor(activity, ViewUtils.getColorPrimary(activity));
        colorFontBlack = ContextCompat.getColor(activity, R.color.font_black);
        colorFontWhite = ContextCompat.getColor(activity, R.color.font_white);
        colorFontGrey = ContextCompat.getColor(activity, R.color.font_grey);
        formatDistance = activity.getString(R.string.distance_colon_space_holder);
    }

    @Override
    protected void convert(BaseViewHolder helper, PoiItem item) {
        String title = item.getTitle();
        String provinceName = item.getProvinceName();
        String cityName = item.getCityName();
        if (provinceName != null && provinceName.equals(cityName)) {
            cityName = "";
        }
        String location = provinceName + cityName + item.getAdName() + item.getSnippet();
        String distance = String.format(Locale.getDefault(), formatDistance, item.getDistance());

        helper.setText(R.id.tvAddress, title);
        helper.setText(R.id.tvLocation, location);
        helper.setText(R.id.tvDistance, distance);
        // select
        if (helper.getLayoutPosition() == selectIndex) {
            helper.setBackgroundColor(R.id.root, colorPrimary);
            helper.setTextColor(R.id.tvAddress, colorFontWhite);
            helper.setTextColor(R.id.tvLocation, colorFontWhite);
            helper.setTextColor(R.id.tvDistance, colorFontWhite);
        } else {
            helper.setBackgroundColor(R.id.root, Color.TRANSPARENT);
            helper.setTextColor(R.id.tvAddress, colorFontBlack);
            helper.setTextColor(R.id.tvLocation, colorFontGrey);
            helper.setTextColor(R.id.tvDistance, colorFontGrey);
        }
    }

    public LocationInfo select(int position) {
        int foreSelectIndex = selectIndex;
        selectIndex = position;
        if (foreSelectIndex >= 0 && foreSelectIndex < getData().size()) {
            notifyItemChanged(foreSelectIndex);
        }
        if (selectIndex < 0 || selectIndex >= getData().size()) return null;
        notifyItemChanged(selectIndex);
        // locationInfo
        PoiItem item = getItem(selectIndex);
        LocationInfo locationInfo = new LocationInfo();
        LatLonPoint latLonPoint = item.getLatLonPoint();
        if (latLonPoint != null) {
            locationInfo.setLatitude(latLonPoint.getLatitude());
            locationInfo.setLongitude(latLonPoint.getLongitude());
        }
        locationInfo.setAddress(item.getTitle());
        locationInfo.setCityId(item.getAdCode());
        return locationInfo;
    }

}
