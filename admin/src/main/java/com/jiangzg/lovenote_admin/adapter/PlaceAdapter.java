package com.jiangzg.lovenote_admin.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote_admin.R;
import com.jiangzg.lovenote_admin.activity.UserDetailActivity;
import com.jiangzg.lovenote_admin.base.BaseActivity;
import com.jiangzg.lovenote_admin.domain.Place;

/**
 * Created by JZG on 2018/3/13.
 * place适配器
 */
public class PlaceAdapter extends BaseQuickAdapter<Place, BaseViewHolder> {

    private BaseActivity mActivity;

    public PlaceAdapter(BaseActivity activity) {
        super(R.layout.list_item_place);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Place item) {
        // data
        String id = "id:" + item.getId();
        String userId = "uid:" + item.getUserId();
        String create = DateUtils.getStr(item.getCreateAt() * 1000, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        String address = StringUtils.isEmpty(item.getAddress()) ? item.getStreet() : item.getAddress();
        String cityId = "city:" + item.getCityId();
        String lon = "lon:" + item.getLongitude();
        String lat = "lat:" + item.getLatitude();
        String country = item.getCountry();
        String province = item.getProvince();
        String city = item.getCity();
        String district = item.getDistrict();
        // view
        helper.setText(R.id.tvId, id);
        helper.setText(R.id.tvUid, userId);
        helper.setText(R.id.tvCreate, create);
        helper.setText(R.id.tvAddress, address);
        helper.setText(R.id.tvCityId, cityId);
        helper.setText(R.id.tvLon, lon);
        helper.setText(R.id.tvLat, lat);
        helper.setText(R.id.tvCountry, country);
        helper.setText(R.id.tvProvince, province);
        helper.setText(R.id.tvCity, city);
        helper.setText(R.id.tvDistrict, district);
    }

    public void goUser(final int position) {
        Place item = getItem(position);
        UserDetailActivity.goActivity(mActivity, item.getUserId());
    }

}
