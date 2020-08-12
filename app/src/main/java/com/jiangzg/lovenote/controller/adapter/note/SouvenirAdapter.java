package com.jiangzg.lovenote.controller.adapter.note;

import android.support.v4.app.Fragment;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.note.SouvenirDetailDoneActivity;
import com.jiangzg.lovenote.controller.activity.note.SouvenirDetailWishActivity;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.model.entity.Souvenir;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 纪念日适配器
 */
public class SouvenirAdapter extends BaseQuickAdapter<Souvenir, BaseViewHolder> {

    private Fragment mFragment;

    public SouvenirAdapter(Fragment fragment, boolean done) {
        super(R.layout.list_item_souvenir);
        mFragment = fragment;
    }

    @Override
    protected void convert(BaseViewHolder helper, Souvenir item) {
        String title = item.getTitle();
        long happenAt = TimeHelper.getJavaTimeByGo(item.getHappenAt());
        String happen = DateUtils.getStr(happenAt, DateUtils.FORMAT_LINE_Y_M_D_H_M);
        long dayCount;
        String format;
        if (DateUtils.getCurrentLong() > happenAt) {
            format = mFragment.getString(R.string.add_holder);
            dayCount = (DateUtils.getCurrentLong() - happenAt) / TimeUnit.DAY;
        } else {
            format = mFragment.getString(R.string.sub_holder);
            dayCount = (happenAt - DateUtils.getCurrentLong()) / TimeUnit.DAY;
        }
        String days = String.format(Locale.getDefault(), format, dayCount);
        // view
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvDayCount, days);
    }

    public void goSouvenirDetail(int position) {
        Souvenir item = getItem(position);
        if (item.isDone()) {
            SouvenirDetailDoneActivity.goActivity(mFragment, item);
        } else {
            SouvenirDetailWishActivity.goActivity(mFragment, item);
        }
    }

}
