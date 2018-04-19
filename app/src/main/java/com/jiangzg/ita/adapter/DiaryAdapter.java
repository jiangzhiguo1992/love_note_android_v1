package com.jiangzg.ita.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.SuggestDetailActivity;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.Diary;
import com.jiangzg.ita.helper.ConvertHelper;
import com.jiangzg.ita.helper.SPHelper;
import com.jiangzg.ita.view.GImageView;

/**
 * Created by JZG on 2018/3/13.
 * 意见反馈适配器
 */
public class DiaryAdapter extends BaseQuickAdapter<Diary, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final String creatorAvatar;
    private final String inviteeAvatar;
    private final long creatorId;

    public DiaryAdapter(FragmentActivity activity) {
        super(R.layout.list_item_diary);
        mActivity = activity;
        Couple couple = SPHelper.getCouple();
        creatorId = couple.getCreatorId();
        creatorAvatar = couple.getCreatorAvatar();
        inviteeAvatar = couple.getInviteeAvatar();
    }

    @Override
    protected void convert(BaseViewHolder helper, Diary item) {
        String happen = ConvertHelper.ConvertTimeGo2DiaryShow(item.getHappenAt());
        String content = item.getContent();
        String avatar = (item.getUserId() == creatorId) ? creatorAvatar : inviteeAvatar;
        // view
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvContent, content);
        GImageView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setDataOss(avatar);
    }

    public void goDiaryDetail(int position) {
        Diary item = getItem(position);
        //SuggestDetailActivity.goActivity(mActivity, item);
    }

}
