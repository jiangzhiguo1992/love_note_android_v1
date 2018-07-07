package com.jiangzg.mianmian.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.domain.SleepInfo;
import com.jiangzg.mianmian.helper.TimeHelper;

import java.util.Calendar;

/**
 * Created by JZG on 2018/3/12.
 * 睡眠信息适配器
 */
public class SleepInfoAdapter extends BaseQuickAdapter<SleepInfo, BaseViewHolder> {


    public SleepInfoAdapter() {
        super(R.layout.list_item_sleep_info);
    }

    @Override
    protected void convert(BaseViewHolder helper, SleepInfo item) {
        Calendar calStart = DateUtils.getCalendar(TimeHelper.getJavaTimeByGo(item.getStartAt()));
        Calendar calEnd = DateUtils.getCalendar(TimeHelper.getJavaTimeByGo(item.getEndAt()));
        int hourStart = calStart.get(Calendar.HOUR_OF_DAY);
        int minuteStart = calStart.get(Calendar.MINUTE);
        int hourEnd = calEnd.get(Calendar.HOUR_OF_DAY);
        int minuteEnd = calEnd.get(Calendar.MINUTE);
        String sleepShow = hourStart + " : " + minuteStart + "  -  " + hourEnd + " : " + minuteEnd;
        helper.setText(R.id.tvTime, sleepShow);
    }

}
