package com.jiangzg.lovenote.adapter;

import android.app.Activity;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.domain.Couple;
import com.jiangzg.lovenote.domain.Shy;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;

/**
 * Created by JZG on 2018/3/12.
 * 羞羞适配器
 */
public class ShyAdapter extends BaseQuickAdapter<Shy, BaseViewHolder> {

    private Activity mActivity;
    private final Couple couple;

    public ShyAdapter(Activity activity) {
        super(R.layout.list_item_shy);
        mActivity = activity;
        couple = SPHelper.getCouple();
    }

    @Override
    protected void convert(BaseViewHolder helper, Shy item) {
        String avatar = Couple.getAvatar(couple, item.getUserId());
        String happenAt = DateUtils.getString(TimeHelper.getJavaTimeByGo(item.getHappenAt()), ConstantUtils.FORMAT_H_M);
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setText(R.id.tvTime, happenAt);
    }

}
