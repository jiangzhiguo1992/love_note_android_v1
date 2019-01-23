package com.jiangzg.lovenote.controller.adapter.note;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.note.DreamDetailActivity;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Dream;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.util.Locale;

/**
 * Created by JZG on 2018/3/13.
 * 梦境适配器
 */
public class DreamAdapter extends BaseQuickAdapter<Dream, BaseViewHolder> {

    private final Couple couple;
    private final String formatNumber;
    private FragmentActivity mActivity;

    public DreamAdapter(FragmentActivity activity) {
        super(R.layout.list_item_dream);
        mActivity = activity;
        couple = SPHelper.getCouple();
        formatNumber = mActivity.getString(R.string.text_number_space_colon_holder);
    }

    @Override
    protected void convert(BaseViewHolder helper, Dream item) {
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String happen = TimeHelper.getTimeShowLocal_HM_MD_YMD_ByGo(item.getHappenAt());
        String content = item.getContentText();
        if (content == null) content = "";
        String countShow = String.format(Locale.getDefault(), formatNumber, content.length());
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar, item.getUserId());
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvCount, countShow);
        helper.setText(R.id.tvContent, content);
    }

    public void goDreamDetail(int position) {
        Dream item = getItem(position);
        DreamDetailActivity.goActivity(mActivity, item);
    }

}
