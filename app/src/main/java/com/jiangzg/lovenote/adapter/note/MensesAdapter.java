package com.jiangzg.lovenote.adapter.note;

import android.app.Activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.model.entity.Menses;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/12.
 * 姨妈适配器
 */
public class MensesAdapter extends BaseQuickAdapter<Menses, BaseViewHolder> {

    private Activity mActivity;

    public MensesAdapter(Activity activity) {
        super(R.layout.list_item_menses);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Menses item) {
        String month = String.valueOf(item.getMonthOfYear());
        String day = String.valueOf(item.getDayOfMonth());
        String time = String.format(Locale.getDefault(), mActivity.getString(R.string.holder_space_line_space_holder), month, day);
        String timeShow;
        if (item.isStart()) {
            timeShow = String.format(Locale.getDefault(), mActivity.getString(R.string.start_at_colon_space_holder), time);
        } else {
            timeShow = String.format(Locale.getDefault(), mActivity.getString(R.string.end_at_colon_space_holder), time);
        }
        // view
        helper.setText(R.id.tvTime, timeShow);
    }

}
