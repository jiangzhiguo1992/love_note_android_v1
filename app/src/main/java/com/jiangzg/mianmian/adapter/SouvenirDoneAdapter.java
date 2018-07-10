package com.jiangzg.mianmian.adapter;

import android.support.v4.app.Fragment;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.SouvenirDetailDoneActivity;
import com.jiangzg.mianmian.domain.Souvenir;
import com.jiangzg.mianmian.helper.TimeHelper;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 梦境适配器
 */
public class SouvenirDoneAdapter extends BaseQuickAdapter<Souvenir, BaseViewHolder> {

    private Fragment mFragment;

    public SouvenirDoneAdapter(Fragment fragment) {
        super(R.layout.list_item_souvenir_done);
        mFragment = fragment;
    }

    @Override
    protected void convert(BaseViewHolder helper, Souvenir item) {
        String title = item.getTitle();
        long happenAt = TimeHelper.getJavaTimeByGo(item.getHappenAt());
        String happen = DateUtils.getString(happenAt, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M);
        long dayCount = (DateUtils.getCurrentLong() - happenAt) / ConstantUtils.DAY;
        String days = String.format(Locale.getDefault(), mFragment.getString(R.string.holder_day), dayCount);
        String address = item.getAddress();
        // view
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvDayCount, days);
        helper.setVisible(R.id.tvAddress, !StringUtils.isEmpty(address));
        helper.setText(R.id.tvAddress, address);
    }

    public void goSouvenirDoneDetail(int position) {
        Souvenir item = getItem(position);
        SouvenirDetailDoneActivity.goActivity(mFragment, item);
    }

}
