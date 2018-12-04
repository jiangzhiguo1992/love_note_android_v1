package com.jiangzg.lovenote.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.topic.PostListActivity;
import com.jiangzg.lovenote.helper.CountHelper;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.model.entity.TopicInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 话题分类适配器
 */
public class TopicHomeKindAdapter extends BaseQuickAdapter<PostKindInfo, BaseViewHolder> {

    private final List<Integer> colorList;
    private Fragment mFragment;
    private Activity mActivity;

    public TopicHomeKindAdapter(Activity activity, Fragment fragment) {
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
        if (position - 1 >= colorList.size()) {
            color = colorList.get((position - 1) % colorList.size());
        } else {
            color = colorList.get(position - 1);
        }
        String name = item.getName();
        TopicInfo topicInfo = item.getTopicInfo();
        String postCount = String.format(Locale.getDefault(), mActivity.getString(R.string.post_colon_space_holder), topicInfo == null ? 0 : CountHelper.getShowCount2Thousand(topicInfo.getPostCount()));
        String commentCount = String.format(Locale.getDefault(), mActivity.getString(R.string.comment_colon_space_holder), topicInfo == null ? 0 : CountHelper.getShowCount2Thousand(topicInfo.getCommentCount()));
        String pointCount = String.format(Locale.getDefault(), mActivity.getString(R.string.point_colon_space_holder), topicInfo == null ? 0 : CountHelper.getShowCount2Thousand(topicInfo.getPointCount()));
        String collectCount = String.format(Locale.getDefault(), mActivity.getString(R.string.collect_colon_space_holder), topicInfo == null ? 0 : CountHelper.getShowCount2Thousand(topicInfo.getCollectCount()));
        // view
        CardView root = helper.getView(R.id.root);
        root.setCardBackgroundColor(color);
        helper.setText(R.id.tvName, name);
        helper.setText(R.id.tvPostCount, postCount);
        helper.setText(R.id.tvCommentCount, commentCount);
        helper.setText(R.id.tvPointCount, pointCount);
        helper.setText(R.id.tvCollectCount, collectCount);
    }

    public void goPostList(int position) {
        PostKindInfo item = getItem(position);
        PostListActivity.goActivity(mFragment, item);
    }

    private List<Integer> getColorList() {
        List<Integer> colorList = new ArrayList<>();
        int k1 = ContextCompat.getColor(mActivity, R.color.topic_kind_1);
        int k2 = ContextCompat.getColor(mActivity, R.color.topic_kind_2);
        int k3 = ContextCompat.getColor(mActivity, R.color.topic_kind_3);
        int k4 = ContextCompat.getColor(mActivity, R.color.topic_kind_4);
        int k5 = ContextCompat.getColor(mActivity, R.color.topic_kind_5);
        int k6 = ContextCompat.getColor(mActivity, R.color.topic_kind_6);
        colorList.add(k1);
        colorList.add(k2);
        colorList.add(k3);
        colorList.add(k4);
        colorList.add(k5);
        colorList.add(k6);
        return colorList;
    }

}
