package com.jiangzg.mianmian.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.DiaryDetailActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.view.GImageView;

import java.util.Locale;

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
        if (content == null) content = "";
        String countShow = String.format(Locale.getDefault(), mActivity.getString(R.string.text_number_colon), content.length());
        String avatar = (item.getUserId() == creatorId) ? creatorAvatar : inviteeAvatar;
        // view
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvCount, countShow);
        helper.setText(R.id.tvContent, content);
        GImageView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setDataOss(avatar);
    }

    public void goDiaryDetail(int position) {
        Diary item = getItem(position);
        DiaryDetailActivity.goActivity(mActivity, item);
    }

}
