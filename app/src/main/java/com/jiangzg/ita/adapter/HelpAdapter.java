package com.jiangzg.ita.adapter;

import android.app.Activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.HelpActivity;
import com.jiangzg.ita.domain.Help;

/**
 * Created by JZG on 2018/3/12.
 * 帮助列表适配器
 */
public class HelpAdapter extends BaseQuickAdapter<Help, BaseViewHolder> {

    public HelpAdapter() {
        super(R.layout.list_item_help);
    }

    @Override
    protected void convert(BaseViewHolder helper, Help item) {
        String title = item.getTitle();
        helper.setText(R.id.tvSubTitle, title);
    }

    public void goSubHelp(Activity activity, int position) {
        Help item = getItem(position);
        int contentType = item.getContentType();
        HelpActivity.goActivity(activity, contentType);
    }

}
