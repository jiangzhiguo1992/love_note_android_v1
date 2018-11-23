package com.jiangzg.lovenote.adapter;

import android.app.Activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.domain.Sleep;
import com.jiangzg.lovenote.helper.TimeHelper;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/12.
 * 睡眠适配器
 */
public class SleepAdapter extends BaseQuickAdapter<Sleep, BaseViewHolder> {

    private Activity mActivity;

    public SleepAdapter(Activity activity) {
        super(R.layout.list_item_sleep);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Sleep item) {
        String time = DateUtils.getString(TimeHelper.getJavaTimeByGo(item.getCreateAt()), ConstantUtils.FORMAT_H_M);
        String format = item.isSleep() ? mActivity.getString(R.string.holder_colon_sleep) : mActivity.getString(R.string.holder_colon_wake);
        String show = String.format(Locale.getDefault(), format, time);
        // view
        helper.setText(R.id.tvTime, show);
    }

}
