package com.jiangzg.ita.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.ImgScreenActivity;
import com.jiangzg.ita.view.GImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 帮助列表适配器
 */
public class WallPaperAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private final Activity mActivity;
    private final float screenWidth;
    private final float screenHeight;

    public WallPaperAdapter(Activity activity) {
        super(R.layout.list_item_wall_paper);
        mActivity = activity;
        screenWidth = ScreenUtils.getScreenWidth(activity);
        screenHeight = ScreenUtils.getScreenHeight(activity);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        GImageView ivWallPaper = helper.getView(R.id.ivWallPaper);
        ivWallPaper.setAspectRatio(screenWidth / screenHeight);
        ivWallPaper.setUri(Uri.parse(item));
    }

    public void goImgScreen(int position, GImageView view) {
        List<String> data = getData();
        ArrayList<Uri> uriList = convertListString2uri(data);
        ImgScreenActivity.goActivity(mActivity, uriList, position, view);
    }

    public void showDeleteDialog(int position) {
        ToastUtils.show("" + position);
    }

    private ArrayList<Uri> convertListString2uri(List<String> strings) {
        ArrayList<Uri> uriList = new ArrayList<>();
        if (strings == null || strings.size() <= 0) return uriList;
        for (String s : strings) {
            uriList.add(Uri.parse(s));
        }
        return uriList;
    }

}
