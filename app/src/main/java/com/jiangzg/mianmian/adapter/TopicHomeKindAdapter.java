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
import com.jiangzg.mianmian.domain.PostSubKindInfo;

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
    private final List<Integer> colorList;

    public TopicHomeKindAdapter(Activity activity, Fragment fragment) {
        super(R.layout.list_item_topic_home_kind);
        mActivity = activity;
        mFragment = fragment;
        colorList = getColorList();
    }

    @Override
    protected void convert(BaseViewHolder helper, PostKindInfo item) {
        int position = helper.getLayoutPosition();
        boolean isLeft = position % 2 == 1;
        Integer color;
        if (position - 1 >= colorList.size()) {
            color = colorList.get((position - 1) % colorList.size());
        } else {
            color = colorList.get(position - 1);
        }
        String name = item.getName();
        List<PostSubKindInfo> postSubKindInfoList = item.getPostSubKindInfoList();
        String countShow = String.format(Locale.getDefault(), "内有%d个板块", postSubKindInfoList == null ? 0 : postSubKindInfoList.size());
        //helper.setVisible(R.id.tvNameRight, isLeft);
        //helper.setVisible(R.id.tvSubCountLeft, isLeft);
        //helper.setVisible(R.id.tvNameLeft, !isLeft);
        //helper.setVisible(R.id.tvSubCountRight, !isLeft);
        helper.setVisible(R.id.tvNameRight, true);
        helper.setVisible(R.id.tvSubCountLeft, true);
        helper.setVisible(R.id.tvNameLeft, false);
        helper.setVisible(R.id.tvSubCountRight, false);
        // view
        CardView root = helper.getView(R.id.root);
        root.setCardBackgroundColor(color);
        helper.setText(R.id.tvNameRight, name);
        helper.setText(R.id.tvSubCountLeft, countShow);
        //helper.setText(R.id.tvNameLeft, name);
        //helper.setText(R.id.tvSubCountRight, countShow);
        // TODO 今日发帖量：XXX
        // TODO 今日阅读数：XXX
    }

    public void goAngryDetail(int position) {
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

}
