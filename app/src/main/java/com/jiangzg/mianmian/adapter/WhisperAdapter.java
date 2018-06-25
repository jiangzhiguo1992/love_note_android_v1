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
import com.jiangzg.mianmian.view.FrescoAvatarView;
import com.jiangzg.mianmian.view.FrescoView;

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
        String avatar = Couple.getAvatar(couple, item.getUserId());
        String createAt = ConvertHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getCreateAt());
        boolean isImage = item.isImage();
        final String content = item.getContent();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvCreateAt, createAt);
        TextView tvContent = helper.getView(R.id.tvContent);
        FrescoView ivContent = helper.getView(R.id.ivContent);
        if (isImage) {
            tvContent.setVisibility(View.GONE);
            ivContent.setVisibility(View.VISIBLE);
            ivContent.setClickListener(new FrescoView.ClickListener() {
                @Override
                public void onSuccessClick(FrescoView iv) {
                    BigImageActivity.goActivityByOss(mActivity, content, iv);
                }
            });
            ivContent.setData(content);
        } else {
            tvContent.setVisibility(View.VISIBLE);
            ivContent.setVisibility(View.GONE);
            tvContent.setText(content);
            ivContent.setClickListener(null);
        }
    }

}
