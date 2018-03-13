package com.jiangzg.ita.adapter;

import android.content.res.ColorStateList;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.ita.R;
import com.jiangzg.ita.domain.Suggest;
import com.jiangzg.ita.utils.TimeUtils;
import com.jiangzg.ita.utils.ViewUtils;

/**
 * Created by JZG on 2018/3/13.
 * 意见反馈适配器
 */

public class SuggestAdapter extends BaseQuickAdapter<Suggest, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final int colorGrey;
    private final int colorPrimary;

    public SuggestAdapter(FragmentActivity activity) {
        super(R.layout.list_item_suggest);
        mActivity = activity;
        colorGrey = ContextCompat.getColor(activity, R.color.icon_grey);
        int rId = ViewUtils.getColorPrimary(activity);
        colorPrimary = ContextCompat.getColor(activity, rId);
    }

    @Override
    protected void convert(BaseViewHolder helper, Suggest item) {
        // data
        String title = item.getTitle();
        long createdAt = item.getCreatedAt();
        String create = TimeUtils.getSuggestShowBySecond(createdAt);
        String createShow = String.format(mActivity.getString(R.string.create_at_holder), create);
        long updatedAt = item.getUpdatedAt();
        String update = TimeUtils.getSuggestShowBySecond(updatedAt);
        String updatedShow = String.format(mActivity.getString(R.string.update_at_holder), update);
        final int watchCount = item.getWatchCount();
        String watchShow;
        if (watchCount <= 0) {
            watchShow = mActivity.getString(R.string.follow);
        } else {
            watchShow = String.format("%d", watchCount);
        }
        int commentCount = item.getCommentCount();
        String commentShow;
        if (commentCount <= 0) {
            commentShow = mActivity.getString(R.string.comment);
        } else {
            commentShow = String.format("%d", commentCount);
        }
        final boolean watch = item.isWatch();
        boolean comment = item.isComment();
        // view
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvCreateAt, createShow);
        helper.setText(R.id.tvUpdateAt, updatedShow);
        helper.setText(R.id.tvWatch, watchShow);
        helper.setText(R.id.tvComment, commentShow);
        if (watch) {
            helper.setImageResource(R.id.ivWatch, R.drawable.ic_visibility_on_primary);
        } else {
            helper.setImageResource(R.id.ivWatch, R.drawable.ic_visibility_off_grey);
        }
        ImageView ivComment = helper.getView(R.id.ivComment);
        if (comment) {
            ivComment.setImageTintList(ColorStateList.valueOf(colorPrimary));
        } else {
            ivComment.setImageTintList(ColorStateList.valueOf(colorGrey));
        }
        // listener
        helper.addOnClickListener(R.id.llTitle);
        helper.addOnClickListener(R.id.llWatch);
        helper.addOnClickListener(R.id.llComment);
    }

    public void goSuggestDetail(int position) {
        Suggest item = getItem(position);
        // todo 详情页跳转
    }

    public void toggleWatch(int position) {
        Suggest item = getItem(position);
        boolean watch = item.isWatch();
        int watchCount = item.getWatchCount();
        boolean willWatch = !watch;
        item.setWatch(willWatch);
        int willWatchCount;
        if (willWatch) {
            willWatchCount = watchCount + 1;
        } else {
            willWatchCount = watchCount - 1;
        }
        if (willWatchCount <= 0) {
            willWatchCount = 0;
        }
        item.setWatchCount(willWatchCount);
        notifyItemChanged(position);
        // todo api
    }

    public void comment(int position) {
        // todo 详情页跳转 位置到评论
    }
}
