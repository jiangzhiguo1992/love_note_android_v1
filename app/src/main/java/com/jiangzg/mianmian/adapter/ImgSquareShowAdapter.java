package com.jiangzg.mianmian.adapter;

import android.support.v7.widget.CardView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.common.BigImageActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.view.GImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 图片显示列表适配器
 */
public class ImgSquareShowAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private final BaseActivity mActivity;
    private final int imageWidth;
    private final int imageHeight;

    public ImgSquareShowAdapter(BaseActivity activity, int spanCount) {
        super(R.layout.list_item_img_suqare_show);
        mActivity = activity;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        int dp10 = ConvertUtils.dp2px(10);
        // 左右是5+5 中间也是5+5 有变动要跟着改
        imageWidth = imageHeight = (int) ((screenWidth - dp10 - (dp10 * spanCount)) / spanCount);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final String item) {
        final int position = helper.getLayoutPosition();
        GImageView ivShow = helper.getView(R.id.ivShow);
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) ivShow.getLayoutParams();
        layoutParams.height = imageHeight;
        ivShow.setLayoutParams(layoutParams);
        ivShow.setWidthAndHeight(imageWidth, imageHeight);
        ivShow.setClickListener(new GImageView.ClickListener() {
            @Override
            public void onSuccessClick(GImageView iv) {
                List<String> data = ImgSquareShowAdapter.this.getData();
                ArrayList<String> pathList = new ArrayList<>();
                pathList.addAll(data);
                BigImageActivity.goActivityByOssList(mActivity, pathList, position, iv);
            }
        });
        ivShow.setDataOss(item);
    }

}
