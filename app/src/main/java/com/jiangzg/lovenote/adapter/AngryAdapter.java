package com.jiangzg.lovenote.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.note.AngryDetailActivity;
import com.jiangzg.lovenote.domain.Angry;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;

/**
 * Created by JZG on 2018/3/13.
 * 生气适配器
 */
public class AngryAdapter extends BaseQuickAdapter<Angry, BaseViewHolder> {

    private FragmentActivity mActivity;
    private final Couple couple;

    public AngryAdapter(FragmentActivity activity) {
        super(R.layout.list_item_angry);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Angry item) {
        String avatar = Couple.getAvatar(couple, item.getHappenId());
        String happen = TimeHelper.getTimeShowCn_HM_MD_YMD_ByGo(item.getHappenAt());
        String content = item.getContentText();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvHappenAt, happen);
        helper.setText(R.id.tvContent, content);
    }

    public void goAngryDetail(int position) {
        Angry item = getItem(position);
        AngryDetailActivity.goActivity(mActivity, item);
    }

}
