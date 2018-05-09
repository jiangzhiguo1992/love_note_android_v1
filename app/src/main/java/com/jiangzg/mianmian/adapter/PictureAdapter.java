package com.jiangzg.mianmian.adapter;

import android.support.v7.widget.CardView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.imagepipeline.image.ImageInfo;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Picture;
import com.jiangzg.mianmian.view.GImageView;

/**
 * Created by JZG on 2018/3/12.
 * 图片列表适配器
 */
public class PictureAdapter extends BaseQuickAdapter<Picture, BaseViewHolder> {

    private final BaseActivity mActivity;
    private final int imageWidth, imageHeight;

    public PictureAdapter(BaseActivity activity) {
        super(R.layout.list_item_picture);
        mActivity = activity;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        int dp5 = ConvertUtils.dp2px(5);
        imageWidth = imageHeight = (int) (screenWidth / 2) - dp5 * 2;
    }

    @Override
    protected void convert(final BaseViewHolder helper, Picture item) {
        // TODO
        long happenAt = item.getHappenAt();
        String address = item.getAddress();
        double latitude = item.getLatitude();
        double longitude = item.getLongitude();
        String content = item.getContent();
        // view
        GImageView ivPicture = helper.getView(R.id.ivPicture);
        ivPicture.setWidthAndHeight(imageWidth, imageHeight);
        ivPicture.setDataOss(content);
        ivPicture.setLoadListener(new GImageView.LoadListener() {
            @Override
            public void onLoadSuccess(GImageView iv, ImageInfo imageInfo) {
                // 为瀑布流定制不一样的宽高
                float height = imageInfo.getHeight();
                float width = imageInfo.getWidth();
                int finalHeight = (int) (height / width * imageWidth);
                CardView.LayoutParams layoutParams = (CardView.LayoutParams) iv.getLayoutParams();
                layoutParams.height = finalHeight;
                iv.setLayoutParams(layoutParams);
            }

            @Override
            public void onLoadFail(GImageView iv) {
            }
        });
    }

    public void goDetail(int position) {
        Picture item = getItem(position);
        // TODO
    }

}
