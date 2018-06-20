package com.jiangzg.mianmian.adapter;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.helper.ViewHelper;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/12.
 * 帮助列表适配器
 */
public class MapSelectAdapter extends BaseQuickAdapter<PoiItem, BaseViewHolder> {

    private BaseActivity mActivity;
    private int selectIndex = -1;
    private final int colorPrimary;
    private final int colorFontBlack;
    private final int colorFontWhite;
    private final int colorFontGrey;

    public MapSelectAdapter(BaseActivity activity) {
        super(R.layout.list_item_map_select);
        mActivity = activity;
        colorPrimary = ContextCompat.getColor(mActivity, ViewHelper.getColorPrimary(mActivity));
        colorFontBlack = ContextCompat.getColor(mActivity, R.color.font_black);
        colorFontWhite = ContextCompat.getColor(mActivity, R.color.font_white);
        colorFontGrey = ContextCompat.getColor(mActivity, R.color.font_grey);
    }

    @Override
    protected void convert(BaseViewHolder helper, PoiItem item) {
        String title = item.getTitle();
        String provinceName = item.getProvinceName();
        String cityName = item.getCityName();
        if (provinceName != null && provinceName.equals(cityName)) {
            cityName = "";
        }
        String adName = item.getAdName();
        String snippet = item.getSnippet();
        String distance = String.format(Locale.getDefault(), mActivity.getString(R.string.distance_colon_space_holder), item.getDistance());
        String location = provinceName + cityName + adName + snippet;

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
        if (foreSelectIndex >= 0) {
            notifyItemChanged(foreSelectIndex);
        }
        if (selectIndex < 0) return null;
        notifyItemChanged(selectIndex);
        return getSelect();
    }

    public LocationInfo getSelect() {
        if (selectIndex < 0 || selectIndex >= getData().size()) return null;
        PoiItem item = getItem(selectIndex);
        LocationInfo locationInfo = new LocationInfo();
        LatLonPoint latLonPoint = item.getLatLonPoint();
        if (latLonPoint != null) {
            locationInfo.setLatitude(latLonPoint.getLatitude());
            locationInfo.setLongitude(latLonPoint.getLongitude());
        }
        locationInfo.setAddress(item.getTitle());
        locationInfo.setCityId(item.getCityCode());
        return locationInfo;
    }

}
