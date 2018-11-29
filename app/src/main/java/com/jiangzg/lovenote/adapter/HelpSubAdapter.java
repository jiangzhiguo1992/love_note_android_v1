package com.jiangzg.lovenote.adapter;

import android.app.Activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.settings.HelpActivity;
import com.jiangzg.lovenote.model.entity.Help;

/**
 * Created by JZG on 2018/3/12.
 * 帮助列表适配器
 */
public class HelpSubAdapter extends BaseQuickAdapter<Help, BaseViewHolder> {

    public HelpSubAdapter() {
        super(R.layout.list_item_help_sub);
    }

    @Override
    protected void convert(BaseViewHolder helper, Help item) {
        String title = item.getTitle();
        helper.setText(R.id.tvSubTitle, title);
    }

    public void goSubHelp(Activity activity, int position) {
        Help item = getItem(position);
        int index = item.getIndex();
        HelpActivity.goActivity(activity, index);
    }

}
