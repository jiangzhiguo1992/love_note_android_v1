package com.jiangzg.ita.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.ImgScreenActivity;
import com.jiangzg.ita.helper.ConvertHelper;
import com.jiangzg.ita.helper.PopHelper;
import com.jiangzg.ita.helper.ResHelper;
import com.jiangzg.ita.view.GImageView;

import java.io.File;
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
    private PopupWindow popupWindow;

    public WallPaperAdapter(Activity activity) {
        super(R.layout.list_item_wall_paper);
        mActivity = activity;
        screenWidth = ScreenUtils.getScreenWidth(activity);
        screenHeight = ScreenUtils.getScreenHeight(activity);
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        GImageView ivWallPaper = helper.getView(R.id.ivWallPaper);
        ivWallPaper.setAspectRatio(screenWidth / screenHeight);
        if (isAddItem(helper.getLayoutPosition())) {
            ivWallPaper.setDataRes(R.drawable.ic_add_2_grey);
        } else {
            ivWallPaper.setDataUri(Uri.parse(item));
        }
    }

    public void goImgScreen(int position, GImageView view) {
        List<String> data = getData();
        ArrayList<Uri> uriList = ConvertHelper.convertListString2uri(data);
        ImgScreenActivity.goActivity(mActivity, uriList, position, view);
    }

    public void showAddPop(ViewGroup parent, File cameraFile) {
        if (popupWindow == null) {
            popupWindow = PopHelper.createBookAlbumCamera(mActivity, cameraFile);
        }
        PopUtils.show(popupWindow, parent);
    }

    public void showDeleteDialog(final int position) {
        new MaterialDialog.Builder(mActivity)
                .title(R.string.confirm_delete_this_image)
                .positiveText(R.string.confirm)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // todo api
                        List<String> data = getData();
                        data.remove(position);
                        WallPaperAdapter.this.notifyItemRemoved(position);
                    }
                })
                .negativeText(R.string.cancel)
                .show();
    }

    public boolean isAddItem(int position) {
        List<String> data = getData();
        String item = data.get(position);
        int size = data.size();
        return (item == null || item.isEmpty()) && size <= 9 && position == size - 1;
    }

}
