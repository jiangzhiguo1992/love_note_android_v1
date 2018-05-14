package com.jiangzg.mianmian.adapter;

import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.BigImageActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Whisper;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.view.GImageAvatarView;
import com.jiangzg.mianmian.view.GImageView;

/**
 * Created by JZG on 2018/3/12.
 * 耳语
 */
public class WhisperAdapter extends BaseMultiItemQuickAdapter<Whisper, BaseViewHolder> {

    private BaseActivity mActivity;
    private final Couple couple;

    public WhisperAdapter(BaseActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_MY, R.layout.list_item_whisper_right);
        addItemType(ApiHelper.LIST_TA, R.layout.list_item_whisper_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Whisper item) {
        // data
        String avatar = ConvertHelper.getAvatarByCp(couple, item.getUserId());
        String createAt = ConvertHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        boolean imgType = item.isImgType();
        final String content = item.getContent();
        // view
        GImageAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setDateAvatar(avatar);
        helper.setText(R.id.tvCreateAt, createAt);
        TextView tvContent = helper.getView(R.id.tvContent);
        GImageView ivContent = helper.getView(R.id.ivContent);
        if (imgType) {
            tvContent.setVisibility(View.GONE);
            ivContent.setVisibility(View.VISIBLE);
            ivContent.setClickListener(new GImageView.ClickListener() {
                @Override
                public void onSuccessClick(GImageView iv) {
                    BigImageActivity.goActivityByOss(mActivity, content, iv);
                }
            });
            ivContent.setDataOss(content);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            ivContent.setVisibility(View.GONE);
            tvContent.setText(content);
            ivContent.setClickListener(null);
        }
    }

}
