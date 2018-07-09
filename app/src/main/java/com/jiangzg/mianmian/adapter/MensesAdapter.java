package com.jiangzg.mianmian.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.MyApp;
import com.jiangzg.mianmian.domain.Menses;
import com.jiangzg.mianmian.helper.TimeHelper;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/12.
 * 睡眠信息适配器
 */
public class MensesAdapter extends BaseQuickAdapter<Menses, BaseViewHolder> {


    public MensesAdapter() {
        super(R.layout.list_item_menses);
    }

    @Override
    protected void convert(BaseViewHolder helper, Menses item) {
        String time = DateUtils.getString(TimeHelper.getJavaTimeByGo(item.getCreateAt()), ConstantUtils.FORMAT_LINE_M_D_H_M);
        String timeShow;
        if (item.isStart()) {
            timeShow = String.format(Locale.getDefault(), MyApp.get().getString(R.string.start_at_colon_space_holder), time);
        } else {
            timeShow = String.format(Locale.getDefault(), MyApp.get().getString(R.string.end_at_colon_space_holder), time);
        }
        helper.setText(R.id.tvTime, timeShow);
    }

}
