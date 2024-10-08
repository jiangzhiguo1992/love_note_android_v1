package com.jiangzg.lovenote.controller.adapter.common;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.common.BigImageActivity;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.view.FrescoView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 图片编辑列表适配器
 */
public class ImgSquareEditAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private final FragmentActivity mActivity;
    private final int imageWidth;
    private final int imageHeight;
    private int ossSize;
    private int limit;
    private boolean addShow;
    private boolean canDel;
    private OnAddClickListener addClickListener;

    public ImgSquareEditAdapter(FragmentActivity activity, int spanCount, int limit) {
        super(R.layout.list_item_img_suqare_edit);
        mActivity = activity;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        int dp10 = ConvertUtils.dp2px(10);
        // 左右是5+5 中间也是5+5 有变动要跟着改
        imageWidth = imageHeight = (int) ((screenWidth - dp10 - (dp10 * spanCount)) / spanCount);
        ossSize = 0;
        this.limit = limit;
        addShow = false;
        canDel = true;
        checkFoot();
    }

    public void setCanDel(boolean canDel) {
        this.canDel = canDel;
    }

    public void setOnAddClick(OnAddClickListener addClickListener) {
        this.addClickListener = addClickListener;
    }

    @Override
    protected void convert(final BaseViewHolder helper, final String item) {
        final int layoutPosition = helper.getLayoutPosition();
        FrescoView ivShow = helper.getView(R.id.ivShow);
        ImageView ivAdd = helper.getView(R.id.ivAdd);
        if (layoutPosition < getData().size() - 1 || !addShow) {
            // show
            ivShow.setVisibility(View.VISIBLE);
            ivAdd.setVisibility(View.GONE);
            ivShow.setWidthAndHeight(imageWidth / 2, imageHeight / 2);
            CardView.LayoutParams layoutParams = (CardView.LayoutParams) ivShow.getLayoutParams();
            layoutParams.height = imageHeight;
            ivShow.setLayoutParams(layoutParams);
            if (layoutPosition < ossSize) {
                // oss
                ivShow.setClickListener(iv -> BigImageActivity.goActivityByOss(mActivity, item, iv));
                ivShow.setData(item);
            } else {
                // file
                ivShow.setClickListener(iv -> BigImageActivity.goActivityByFile(mActivity, item, iv));
                ivShow.setDataFile(FileUtils.getFileByPath(item));
            }
            ivShow.setOnLongClickListener(v -> {
                if (ImgSquareEditAdapter.this.canDel) {
                    showDeleteDialog(layoutPosition);
                }
                return true;
            });
        } else {
            // add
            ivAdd.setVisibility(View.VISIBLE);
            ivShow.setVisibility(View.GONE);
            CardView.LayoutParams layoutParams = (CardView.LayoutParams) ivAdd.getLayoutParams();
            layoutParams.height = imageHeight;
            ivAdd.setLayoutParams(layoutParams);
            ivAdd.setOnClickListener(v -> {
                if (addClickListener != null) {
                    addClickListener.onAdd();
                }
            });
        }
    }

    // 删除对话框
    private void showDeleteDialog(final int position) {
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .canceledOnTouchOutside(true)
                .cancelable(true)
                .content(R.string.confirm_delete_this_image)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> ImgSquareEditAdapter.this.removeData(position))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    public void addFileData(String filePath) {
        if (!FileUtils.isFileExists(filePath)) return;
        if (addShow) {
            addShow = false;
            this.remove(getData().size() - 1);
        }
        this.addData(filePath);
        checkFoot();
    }

    public void addFileDataList(List<String> filePathList) {
        if (filePathList == null || filePathList.size() <= 0) return;
        if (addShow) {
            addShow = false;
            this.remove(getData().size() - 1);
        }
        this.addData(filePathList);
        checkFoot();
    }

    private void removeData(int position) {
        List<String> data = this.getData();
        int size = data.size();
        if (position < 0 || position >= size) return;
        if (position < ossSize) --ossSize;
        if (addShow) {
            addShow = false;
            this.remove(size - 1);
        }
        // file 还是不删了 防止删除相册文件
        // String item = data.get(position);
        // ResHelper.deleteFileInBackground(FileUtils.getFileByPath(item));
        this.remove(position);
        checkFoot();
    }

    // 检查+号的显示与否
    private void checkFoot() {
        if (limit <= 0) {
            // 不让添加图片
            addShow = false;
            this.setNewData(new ArrayList<>());
            return;
        }
        int size = getData().size();
        if (size < limit && !addShow) {
            // 还能添加图片，而且addView不在
            addShow = true;
            addData("");
            return;
        }
        if (size > limit && addShow) {
            // 不能添加图片，而且addView还在
            addShow = false;
            remove(size - 1);
        }
    }

    // 获取oss数据
    public List<String> getOssData() {
        if (ossSize <= 0) return new ArrayList<>();
        List<String> data = this.getData();
        return data.subList(0, ossSize);
    }

    public void setOssData(List<String> ossPaths) {
        if (ossPaths == null || ossPaths.size() <= 0) return;
        ossSize = ossPaths.size();
        if (limit > ossPaths.size()) {
            // 还可以再添加
            addShow = true;
            ossPaths.add("");
        } else {
            // 不能添加了
            addShow = false;
        }
        this.setNewData(ossPaths);
    }

    // 获取file数据
    public List<String> getFileData() {
        List<String> data = this.getData();
        int size = data.size();
        if (addShow) {
            --size;
        }
        if (size <= 0 || ossSize >= size) return new ArrayList<>();
        return data.subList(ossSize, size);
    }

    public interface OnAddClickListener {
        void onAdd();
    }

}
