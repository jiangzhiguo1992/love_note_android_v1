package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.MapShowActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.TravelPlace;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.TimeHelper;

/**
 * Created by JZG on 2018/3/13.
 * 梦境适配器
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
        String address = item.getAddress();
        String content = item.getContentText();
        // view
        helper.setText(R.id.tvHappenAt, happen);
        helper.setVisible(R.id.tvAddress, !StringUtils.isEmpty(address));
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

    public void showDeleteDialogNoApi(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_track)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        remove(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

}
