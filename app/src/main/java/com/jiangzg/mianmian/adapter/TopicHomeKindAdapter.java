package com.jiangzg.mianmian.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.topic.PostListActivity;
import com.jiangzg.mianmian.domain.PostKindInfo;
import com.jiangzg.mianmian.domain.TopicInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 话题分类适配器
 */
public class TopicHomeKindAdapter extends BaseQuickAdapter<PostKindInfo, BaseViewHolder> {

    private Fragment mFragment;
    private Activity mActivity;
    private List<TopicInfo> topicInfoList;
    private final List<Integer> colorList;

    public TopicHomeKindAdapter(Activity activity, Fragment fragment) {
        super(R.layout.list_item_topic_home_kind);
        mActivity = activity;
        mFragment = fragment;
        topicInfoList = new ArrayList<>();
        colorList = getColorList();
    }

    public void setTopicInfoList(List<TopicInfo> topicInfoList) {
        this.topicInfoList = topicInfoList;
    }

    @Override
    protected void convert(BaseViewHolder helper, PostKindInfo item) {
        // data
        int position = helper.getLayoutPosition();
        //boolean isLeft = position % 2 == 1;
        boolean isLeft = false;
        Integer color;
        if (position - 1 >= colorList.size()) {
            color = colorList.get((position - 1) % colorList.size());
        } else {
            color = colorList.get(position - 1);
        }
        String name = item.getName();
        TopicInfo topicInfo = getTopicInfo(item.getId());
        String postCount = String.format(Locale.getDefault(), mActivity.getString(R.string.today_post_colon_holder), topicInfo.getPostCount());
        String commentCount = String.format(Locale.getDefault(), mActivity.getString(R.string.today_comment_colon_holder), topicInfo.getCommentCount());
        // view
        CardView root = helper.getView(R.id.root);
        root.setCardBackgroundColor(color);
        helper.setVisible(R.id.tvNameLeft, isLeft);
        helper.setVisible(R.id.tvNameRight, !isLeft);
        helper.setText(R.id.tvNameLeft, name);
        helper.setText(R.id.tvNameRight, name);
        helper.setText(R.id.tvPostCount, postCount);
        helper.setText(R.id.tvCommentCount, commentCount);
    }

    public void goPostList(int position) {
        PostKindInfo item = getItem(position);
        PostListActivity.goActivity(mFragment, item);
    }

    private List<Integer> getColorList() {
        List<Integer> colorList = new ArrayList<>();
        int pink = ContextCompat.getColor(mActivity, R.color.theme_pink_primary);
        int purple = ContextCompat.getColor(mActivity, R.color.theme_purple_accent);
        int blue = ContextCompat.getColor(mActivity, R.color.theme_blue_primary);
        int teal = ContextCompat.getColor(mActivity, R.color.theme_teal_primary);
        int green = ContextCompat.getColor(mActivity, R.color.theme_green_primary);
        int orange = ContextCompat.getColor(mActivity, R.color.theme_orange_primary);
        int brown = ContextCompat.getColor(mActivity, R.color.theme_brown_accent);
        colorList.add(pink);
        colorList.add(purple);
        colorList.add(blue);
        colorList.add(teal);
        colorList.add(green);
        colorList.add(orange);
        colorList.add(brown);
        return colorList;
    }

    private TopicInfo getTopicInfo(int kind) {
        if (topicInfoList == null || topicInfoList.size() <= 0) return new TopicInfo();
        for (TopicInfo info : topicInfoList) {
            if (info == null) continue;
            if (info.getKind() == kind) return info;
        }
        return new TopicInfo();
    }

}
