package com.jiangzg.mianmian.adapter;

import android.support.v4.app.Fragment;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.note.SouvenirDetailDoneActivity;
import com.jiangzg.mianmian.activity.note.SouvenirDetailWishActivity;
import com.jiangzg.mianmian.domain.Souvenir;
import com.jiangzg.mianmian.helper.TimeHelper;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 梦境适配器
 */
public class SouvenirAdapter extends BaseQuickAdapter<Souvenir, BaseViewHolder> {

    private Fragment mFragment;
    private boolean done;
    private String formatGone;
    private String formatHave;

    public SouvenirAdapter(Fragment fragment, boolean done) {
        super(R.layout.list_item_souvenir);
        mFragment = fragment;
        this.done = done;
        formatGone = mFragment.getString(R.string.already_gone_holder_day);
        formatHave = mFragment.getString(R.string.just_have_holder_day);
    }

    @Override
    protected void convert(BaseViewHolder helper, Souvenir item) {
        String title = item.getTitle();
        long happenAt = TimeHelper.getJavaTimeByGo(item.getHappenAt());
        String happen = DateUtils.getString(happenAt, ConstantUtils.FORMAT_CHINA_Y_M_D_H_M);
        long dayCount;
        String format;
        if (DateUtils.getCurrentLong() > happenAt) {
            dayCount = (DateUtils.getCurrentLong() - happenAt) / ConstantUtils.DAY;
            format = formatGone;
        } else {
            dayCount = (happenAt - DateUtils.getCurrentLong()) / ConstantUtils.DAY;
            format = formatHave;
        }
        String days = String.format(Locale.getDefault(), format, dayCount);
        String address = item.getAddress();
        // view
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvDayCount, days);
        helper.setVisible(R.id.tvAddress, !StringUtils.isEmpty(address));
        helper.setText(R.id.tvAddress, address);
    }

    public void goSouvenirDetail(int position) {
        Souvenir item = getItem(position);
        if (done) {
            SouvenirDetailDoneActivity.goActivity(mFragment, item);
        } else {
            SouvenirDetailWishActivity.goActivity(mFragment, item);
        }
    }

}
