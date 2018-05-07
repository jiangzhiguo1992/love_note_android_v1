package com.jiangzg.mianmian.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.helper.ConvertHelper;
import com.jiangzg.mianmian.view.GImageView;

/**
 * Created by JZG on 2018/3/12.
 * 图片列表适配器
 */
public class PictureAdapter extends BaseQuickAdapter<Picture, BaseViewHolder> {

    private final BaseActivity mActivity;
    private final int imageWidth;

    public PictureAdapter(BaseActivity activity) {
        super(R.layout.list_item_picture);
        mActivity = activity;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        int dp5 = ConvertUtils.dp2px(5);
        imageWidth = (int) (screenWidth / 2) - dp5 * 2;
    }

    @Override
    protected void convert(final BaseViewHolder helper, Picture item) {
        String content = item.getContent();
        // TODO
        GImageView ivPicture = helper.getView(R.id.ivPicture);
        ivPicture.setDataOss(content);
    }

    public void goDetail(int position) {
        Picture item = getItem(position);
        // TODO
    }

}
