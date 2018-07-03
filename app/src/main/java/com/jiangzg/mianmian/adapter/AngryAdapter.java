package com.jiangzg.mianmian.adapter;

import android.support.v4.app.FragmentActivity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.AngryDetailActivity;
import com.jiangzg.mianmian.domain.Angry;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;

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
