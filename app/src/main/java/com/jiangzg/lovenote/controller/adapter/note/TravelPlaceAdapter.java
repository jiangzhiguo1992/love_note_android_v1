package com.jiangzg.lovenote.controller.adapter.note;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.MapShowActivity;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.model.entity.TravelPlace;

/**
 * Created by JZG on 2018/3/13.
 * 游记地址适配器
 */
public class TravelPlaceAdapter extends BaseQuickAdapter<TravelPlace, BaseViewHolder> {

    private BaseActivity mActivity;

    public TravelPlaceAdapter(BaseActivity activity) {
        super(R.layout.list_item_travel_place);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, TravelPlace item) {
        String happen = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String address = StringUtils.isEmpty(item.getAddress()) ? mActivity.getString(R.string.now_no_address_info) : item.getAddress();
        String content = item.getContentText();
        // view
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvAddress, address);
        helper.setText(R.id.tvContent, content);
    }

    public void goMapShow(int position) {
        TravelPlace item = getItem(position);
        String address = item.getAddress();
        double longitude = item.getLongitude();
        double latitude = item.getLatitude();
        MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

}
