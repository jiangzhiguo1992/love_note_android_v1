package com.jiangzg.ita.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.Help;

/**
 * Created by JZG on 2018/3/12.
 * 帮助文档一级问答
 */
public class HelpContentAdapter extends BaseQuickAdapter<Help.Content, BaseViewHolder> {

    public HelpContentAdapter() {
        super(R.layout.list_item_help_content);
    }

    @Override
    protected void convert(BaseViewHolder helper, Help.Content item) {
        String question = item.getQuestion();
        String answer = item.getAnswer();
        helper.setText(R.id.tvTop, question);
        helper.setText(R.id.tvBottom, answer);
    }

}
