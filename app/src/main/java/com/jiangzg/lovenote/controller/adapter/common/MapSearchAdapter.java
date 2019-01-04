package com.jiangzg.lovenote.controller.adapter.common;

import android.support.v4.app.FragmentActivity;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.helper.RxBus;

/**
 * Created by JZG on 2018/3/12.
 * 地图搜索适配器
 */
public class MapSearchAdapter extends BaseQuickAdapter<PoiItem, BaseViewHolder> {

    private FragmentActivity mActivity;

    public MapSearchAdapter(FragmentActivity activity) {
        super(R.layout.list_item_map_search);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, PoiItem item) {
        // data
        String title = item.getTitle();
        String provinceName = item.getProvinceName();
        String cityName = item.getCityName();
        if (provinceName != null && provinceName.equals(cityName)) {
            cityName = "";
        }
        String location = provinceName + cityName + item.getAdName() + item.getSnippet();
        // view
        helper.setText(R.id.tvAddress, title);
        helper.setText(R.id.tvLocation, location);
    }

    public void select(int position) {
        PoiItem item = getItem(position);
        LocationInfo select = new LocationInfo();
        LatLonPoint latLonPoint = item.getLatLonPoint();
        if (latLonPoint != null) {
            select.setLatitude(latLonPoint.getLatitude());
            select.setLongitude(latLonPoint.getLongitude());
        }
        select.setAddress(item.getTitle());
        select.setCityId(item.getCityCode());
        // 传输数据
        RxBus.post(new RxBus.Event<>(RxBus.EVENT_MAP_SELECT, select));
        mActivity.finish();
    }

}
