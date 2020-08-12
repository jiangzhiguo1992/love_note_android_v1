package com.jiangzg.lovenote.controller.adapter.common;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.common.BigImageActivity;
import com.jiangzg.lovenote.view.FrescoView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/3/12.
 * 图片显示列表适配器
 */
public class ImgSquareShowAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private final FragmentActivity mActivity;
    private final int imageWidth;
    private final int imageHeight;
    private int visibleLimit, totalCount;

    public ImgSquareShowAdapter(FragmentActivity activity, int spanCount) {
        super(R.layout.list_item_img_square_show);
        mActivity = activity;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        int dp10 = ConvertUtils.dp2px(10);
        // 左右是5+5 中间也是5+5 有变动要跟着改
        imageWidth = imageHeight = (int) ((screenWidth - dp10 - (dp10 * spanCount)) / spanCount);
        visibleLimit = -1;
        totalCount = 0;
    }

    public void setVisibleLimit(int limitSize) {
        List<String> data = getData();
        if (limitSize > 0 && limitSize < data.size()) {
            visibleLimit = limitSize;
            totalCount = data.size();
            setNewData(data.subList(0, visibleLimit));
        } else {
            visibleLimit = -1;
            totalCount = 0;
        }
    }

    @Override
    protected void convert(final BaseViewHolder helper, final String item) {
        final int position = helper.getLayoutPosition();
        FrescoView ivShow = helper.getView(R.id.ivShow);
        CardView.LayoutParams layoutParams = (CardView.LayoutParams) ivShow.getLayoutParams();
        layoutParams.height = imageHeight;
        ivShow.setLayoutParams(layoutParams);
        ivShow.setWidthAndHeight(imageWidth / 2, imageHeight / 2);
        ivShow.setClickListener(iv -> {
            List<String> data = ImgSquareShowAdapter.this.getData();
            ArrayList<String> pathList = new ArrayList<>(data);
            BigImageActivity.goActivityByOssList(mActivity, pathList, position, iv);
        });
        ivShow.setData(item);
        // limit
        if (helper.getLayoutPosition() == visibleLimit - 1) {
            helper.setVisible(R.id.llLimit, true);
            helper.setText(R.id.tvLimit, String.format(Locale.getDefault(), mActivity.getString(R.string.total_holder_paper), totalCount));
        } else {
            helper.setVisible(R.id.llLimit, false);
        }
    }

}
