package com.jiangzg.lovenote.controller.adapter.topic;

import android.support.v4.app.Fragment;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.topic.PostKindListActivity;
import com.jiangzg.lovenote.model.entity.PostKindInfo;

/**
 * Created by JZG on 2018/3/13.
 * 话题分类适配器
 */
public class HomeKindAdapter extends BaseQuickAdapter<PostKindInfo, BaseViewHolder> {

    private Fragment mFragment;

    public HomeKindAdapter(Fragment fragment) {
        super(R.layout.list_item_topic_home_kind);
        mFragment = fragment;
    }

    @Override
    protected void convert(BaseViewHolder helper, PostKindInfo item) {
        helper.setImageResource(R.id.ivKind, getKindIcon(helper.getLayoutPosition()));
        helper.setText(R.id.tvName, item.getName());
    }

    public void goPostList(int position) {
        PostKindInfo item = getItem(position);
        PostKindListActivity.goActivity(mFragment, item);
    }

    private static int getKindIcon(int position) {
        switch (position) {
            case 0:
                return R.mipmap.ic_topic_kind_life_36dp;
            case 1:
                return R.mipmap.ic_topic_kind_star_36dp;
            case 2:
                return R.mipmap.ic_topic_kind_anim_36dp;
            case 3:
                return R.mipmap.ic_topic_kind_unknow_36dp;
        }
        return R.mipmap.ic_nav_topic_black_24dp;
    }

}
