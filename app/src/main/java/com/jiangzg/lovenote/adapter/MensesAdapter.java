package com.jiangzg.lovenote.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.MyApp;
import com.jiangzg.lovenote.domain.Menses;
import com.jiangzg.lovenote.helper.TimeHelper;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/12.
 * 姨妈适配器
 */
public class MensesAdapter extends BaseQuickAdapter<Menses, BaseViewHolder> {


    public MensesAdapter() {
        super(R.layout.list_item_menses);
    }

    @Override
    protected void convert(BaseViewHolder helper, Menses item) {
        String time = DateUtils.getString(TimeHelper.getJavaTimeByGo(item.getCreateAt()), ConstantUtils.FORMAT_LINE_M_D);
        String timeShow;
        if (item.isStart()) {
            timeShow = String.format(Locale.getDefault(), MyApp.get().getString(R.string.start_at_colon_space_holder), time);
        } else {
            timeShow = String.format(Locale.getDefault(), MyApp.get().getString(R.string.end_at_colon_space_holder), time);
        }
        helper.setText(R.id.tvTime, timeShow);
    }

}
