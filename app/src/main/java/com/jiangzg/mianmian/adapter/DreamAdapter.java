package com.jiangzg.mianmian.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.note.DreamDetailActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Dream;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 梦境适配器
 */
public class DreamAdapter extends BaseQuickAdapter<Dream, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final Couple couple;
    private final String formatNumber;

    public DreamAdapter(FragmentActivity activity) {
        super(R.layout.list_item_dream);
        mActivity = activity;
        couple = SPHelper.getCouple();
        formatNumber = mActivity.getString(R.string.text_number_space_colon_holder);
    }

    @Override
    protected void convert(BaseViewHolder helper, Dream item) {
        String avatar = Couple.getAvatar(couple, item.getUserId());
        String happen = TimeHelper.getTimeShowCn_HM_MD_YMD_ByGo(item.getHappenAt());
        String content = item.getContentText();
        if (content == null) content = "";
        String countShow = String.format(Locale.getDefault(), formatNumber, content.length());
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvCount, countShow);
        helper.setText(R.id.tvContent, content);
    }

    public void goDreamDetail(int position) {
        Dream item = getItem(position);
        DreamDetailActivity.goActivity(mActivity, item);
    }

}
