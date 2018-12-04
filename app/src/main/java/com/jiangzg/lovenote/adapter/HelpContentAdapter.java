package com.jiangzg.lovenote.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.settings.HelpActivity;

/**
 * Created by JZG on 2018/3/12.
 * 帮助文档一级问答
 */
public class HelpContentAdapter extends BaseQuickAdapter<HelpActivity.Help.HelpContent, BaseViewHolder> {

    public HelpContentAdapter() {
        super(R.layout.list_item_help_content);
    }

    @Override
    protected void convert(BaseViewHolder helper, HelpActivity.Help.HelpContent item) {
        String question = item.getQuestion();
        String answer = item.getAnswer();
        helper.setText(R.id.tvTop, question);
        helper.setText(R.id.tvBottom, answer);
    }

}
