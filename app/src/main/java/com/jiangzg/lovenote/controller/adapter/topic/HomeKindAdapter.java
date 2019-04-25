package com.jiangzg.lovenote.controller.adapter.topic;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.topic.PostKindListActivity;
import com.jiangzg.lovenote.helper.common.CountHelper;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.model.entity.TopicInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 话题分类适配器
 */
public class HomeKindAdapter extends BaseQuickAdapter<PostKindInfo, BaseViewHolder> {

    private final List<Integer> colorList;
    private Fragment mFragment;
    private Activity mActivity;

    public HomeKindAdapter(Activity activity, Fragment fragment) {
        super(R.layout.list_item_topic_home_kind);
        mActivity = activity;
        mFragment = fragment;
        colorList = getColorList();
    }

    @Override
    protected void convert(BaseViewHolder helper, PostKindInfo item) {
        // data
        int position = helper.getLayoutPosition();
        Integer color;
        if (position >= colorList.size()) {
            color = colorList.get((position) % colorList.size());
        } else {
            color = colorList.get(position);
        }
        String name = item.getName();
        TopicInfo topicInfo = item.getTopicInfo();
        String postCount = String.format(Locale.getDefault(), mActivity.getString(R.string.today_post_colon_space_holder), topicInfo == null ? 0 : CountHelper.getShowCount2Thousand(topicInfo.getPostCount()));
        String commentCount = String.format(Locale.getDefault(), mActivity.getString(R.string.today_comment_colon_space_holder), topicInfo == null ? 0 : CountHelper.getShowCount2Thousand(topicInfo.getCommentCount()));
        // view
        //CardView root = helper.getView(R.id.root);
        //root.setCardBackgroundColor(color);
        helper.setTextColor(R.id.tvName, color);
        helper.setText(R.id.tvName, name);
        helper.setText(R.id.tvPostCount, postCount);
        helper.setText(R.id.tvCommentCount, commentCount);
    }

    public void goPostList(int position) {
        PostKindInfo item = getItem(position);
        PostKindListActivity.goActivity(mFragment, item);
    }

    private List<Integer> getColorList() {
        List<Integer> colorList = new ArrayList<>();
        colorList.add(ContextCompat.getColor(mActivity, R.color.theme_indigo_accent));
        colorList.add(ContextCompat.getColor(mActivity, R.color.theme_green_accent));
        colorList.add(ContextCompat.getColor(mActivity, R.color.theme_red_accent));
        colorList.add(ContextCompat.getColor(mActivity, R.color.theme_teal_accent));
        colorList.add(ContextCompat.getColor(mActivity, R.color.theme_grey_accent));
        colorList.add(ContextCompat.getColor(mActivity, R.color.theme_purple_accent));
        colorList.add(ContextCompat.getColor(mActivity, R.color.theme_brown_accent));
        colorList.add(ContextCompat.getColor(mActivity, R.color.theme_yellow_accent));
        colorList.add(ContextCompat.getColor(mActivity, R.color.theme_blue_accent));
        colorList.add(ContextCompat.getColor(mActivity, R.color.theme_orange_accent));
        colorList.add(ContextCompat.getColor(mActivity, R.color.theme_pink_accent));
        return colorList;
    }

}
