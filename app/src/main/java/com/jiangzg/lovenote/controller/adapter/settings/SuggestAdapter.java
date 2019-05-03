package com.jiangzg.lovenote.controller.adapter.settings;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.settings.SuggestDetailActivity;
import com.jiangzg.lovenote.helper.common.ShowHelper;
import com.jiangzg.lovenote.model.entity.Suggest;

/**
 * Created by JZG on 2018/3/13.
 * 意见反馈适配器
 */
public class SuggestAdapter extends BaseQuickAdapter<Suggest, BaseViewHolder> {

    private FragmentActivity mActivity;

    public SuggestAdapter(FragmentActivity activity) {
        super(R.layout.list_item_suggest);
        mActivity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, Suggest item) {
        // data
        String title = item.getTitle();
        String contentText = item.getContentText();
        final int followCount = item.getFollowCount();
        String followShow = followCount <= 0 ? mActivity.getString(R.string.follow) : ShowHelper.getShowCount2Thousand(followCount);
        int commentCount = item.getCommentCount();
        String commentShow = commentCount <= 0 ? mActivity.getString(R.string.comment) : ShowHelper.getShowCount2Thousand(commentCount);
        // view
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvContent, contentText);
        helper.setText(R.id.tvFollow, followShow);
        helper.setText(R.id.tvComment, commentShow);
    }

    public void goSuggestDetail(int position) {
        Suggest item = getItem(position);
        SuggestDetailActivity.goActivity(mActivity, item);
    }

}
