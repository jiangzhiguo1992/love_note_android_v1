package com.jiangzg.mianmian.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.MapShowActivity;
import com.jiangzg.mianmian.domain.Entry;
import com.jiangzg.mianmian.helper.ConvertHelper;

/**
 * Created by JZG on 2018/3/13.
 * 登录信息适配器
 */
public class CouplePlaceAdapter extends BaseQuickAdapter<Entry, BaseViewHolder> {

    private FragmentActivity mActivity;

    public CouplePlaceAdapter(FragmentActivity activity) {
        super(R.layout.list_item_couple_place);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Entry item) {
        String time;
        if (helper.getLayoutPosition() <= 0) {
            time = mActivity.getString(R.string.current_location);
        } else {
            time = ConvertHelper.getTimeShowCnSpace_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        }
        // TODO 没有entry了
        String address = "";
        //Entry.EntryPlace entryPlace = item.getEntryPlace();
        //if (entryPlace != null) {
        //    address = entryPlace.getAddress();
        //}
        //if (StringUtils.isEmpty(address)) {
        //    address = mActivity.getString(R.string.cant_get_ta_entry_info);
        //}
        // view
        helper.setText(R.id.tvTime, time);
        helper.setText(R.id.tvAddress, address);
    }

    // TODO
    public void goDiaryDetail(int position) {
        Entry item = getItem(position);
        //Entry.EntryPlace entryPlace = item.getEntryPlace();
        //if (entryPlace == null) {
        //    ToastUtils.show(mActivity.getString(R.string.no_location_info_cant_go_map));
        //    return;
        //}
        //String address = entryPlace.getAddress();
        //double longitude = entryPlace.getLongitude();
        //double latitude = entryPlace.getLatitude();
        //MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

}
