package com.jiangzg.lovenote.controller.adapter.note;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.BigImageActivity;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Whisper;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.FrescoView;

/**
 * Created by JZG on 2018/3/12.
 * 耳语
 */
public class WhisperAdapter extends BaseMultiItemQuickAdapter<Whisper, BaseViewHolder> {

    private final Couple couple;
    private BaseActivity mActivity;

    public WhisperAdapter(BaseActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_NOTE_WHO_MY, R.layout.list_item_whisper_right);
        addItemType(ApiHelper.LIST_NOTE_WHO_TA, R.layout.list_item_whisper_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Whisper item) {
        // data
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String createAt = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        boolean isImage = item.isImage();
        final String content = item.getContent();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar, item.getUserId());
        helper.setText(R.id.tvCreateAt, createAt);
        TextView tvContent = helper.getView(R.id.tvContent);
        FrescoView ivContent = helper.getView(R.id.ivContent);
        if (isImage) {
            tvContent.setVisibility(View.GONE);
            ivContent.setVisibility(View.VISIBLE);
            ivContent.setClickListener(iv -> BigImageActivity.goActivityByOss(mActivity, content, iv));
            ivContent.setData(content);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            ivContent.setVisibility(View.GONE);
            tvContent.setText(content);
            ivContent.setClickListener(null);
        }
    }

}
