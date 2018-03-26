package com.jiangzg.ita.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.SuggestComment;
import com.jiangzg.ita.utils.Convert;

/**
 * Created by JZG on 2018/3/15.
 * 意见反馈评论适配器
 */
public class SuggestCommentAdapter extends BaseQuickAdapter<SuggestComment, BaseViewHolder> {

    private FragmentActivity mActivity;

    public SuggestCommentAdapter(FragmentActivity activity) {
        super(R.layout.list_item_suggest_comment);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, SuggestComment item) {
        // data
        int layoutPosition = helper.getLayoutPosition();
        long createdAt = item.getCreatedAt();
        String create = Convert.ConvertSecond2Day(createdAt);
        String title = String.format(mActivity.getString(R.string.holder_storey_in_holder_say), layoutPosition, create);
        String contentText = item.getContentText();
        boolean official = item.isOfficial();
        boolean mine = item.isMine();
        // view
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvContent, contentText);
        if (official) {
            helper.setVisible(R.id.tvTag, true);
            helper.setText(R.id.tvTag, mActivity.getString(R.string.official));
        } else if (mine) {
            helper.setVisible(R.id.tvTag, true);
            helper.setText(R.id.tvTag, mActivity.getString(R.string.me));
        } else {
            helper.setVisible(R.id.tvTag, false);
            helper.setText(R.id.tvTag, "");
        }
    }

}
