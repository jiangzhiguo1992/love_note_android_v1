package com.jiangzg.ita.adapter;

import android.support.v7.widget.CardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.PopUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.ita.R;
import com.jiangzg.ita.activity.common.ImgScreenActivity;
import com.jiangzg.ita.base.BaseActivity;
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
public class ImgSquareAddAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    private final BaseActivity mActivity;
    private final View mActivityRoot;
    private final int imageWidth;
    private final int imageHeight;
    private int ossSize;
    private int limit;
    private boolean addShow;
    private File cameraFile;

    public ImgSquareAddAdapter(BaseActivity activity, View root, int spanCount, int limit) {
        super(R.layout.list_item_img_suqare_add);
        mActivity = activity;
        mActivityRoot = root;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        int dp10 = ConvertUtils.dp2px(10);
        // 左右是10 中间也是10 有变动要跟着改
        imageWidth = imageHeight = (int) ((screenWidth - dp10 - (dp10 * spanCount)) / spanCount);
        ossSize = 0;
        this.limit = limit;
        addShow = false;
        checkFoot();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    protected void convert(final BaseViewHolder helper, final String item) {
        int layoutPosition = helper.getLayoutPosition();
        final GImageView ivShow = helper.getView(R.id.ivShow);
        ImageView ivAdd = helper.getView(R.id.ivAdd);
        if (layoutPosition < getData().size() - 1 || !addShow) {
            // show
            ivShow.setVisibility(View.VISIBLE);
            ivAdd.setVisibility(View.GONE);
            ivShow.setWidthAndHeight(imageWidth, imageHeight);
            CardView.LayoutParams layoutParams = (CardView.LayoutParams) ivShow.getLayoutParams();
            layoutParams.height = imageHeight;
            ivShow.setLayoutParams(layoutParams);
            if (layoutPosition < ossSize) {
                // oss
                ivShow.setDataOss(item);
                ivShow.setSuccessClickListener(new GImageView.onSuccessClickListener() {
                    @Override
                    public void onClick(GImageView iv) {
                        ImgScreenActivity.goActivityByOss(mActivity, item, ivShow);
                    }
                });
            } else {
                // file
                ivShow.setDataFile(FileUtils.getFileByPath(item));
                ivShow.setSuccessClickListener(new GImageView.onSuccessClickListener() {
                    @Override
                    public void onClick(GImageView iv) {
                        ImgScreenActivity.goActivityByFile(mActivity, item, ivShow);
                    }
                });
            }
        } else {
            // add
            ivAdd.setVisibility(View.VISIBLE);
            ivShow.setVisibility(View.GONE);
            CardView.LayoutParams layoutParams = (CardView.LayoutParams) ivAdd.getLayoutParams();
            layoutParams.height = imageHeight;
            ivAdd.setLayoutParams(layoutParams);
            ivAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showImgSelect();
                }
            });
        }
    }

    private void showImgSelect() {
        cameraFile = ResHelper.createJPEGInCache();
        PopupWindow popupWindow = PopHelper.createBookPictureCamera(mActivity, cameraFile);
        PopUtils.show(popupWindow, mActivityRoot);
    }

    public File getCameraFile() {
        return cameraFile;
    }

    private void checkFoot() {
        if (limit == 0) {
            // 不让添加图片
            addShow = false;
            this.setNewData(new ArrayList<String>());
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

    public void setOssData(List<String> ossPaths) {
        if (ossPaths == null || ossPaths.size() <= 0) return;
        this.setNewData(ossPaths);
        ossSize = ossPaths.size();
        checkFoot();
    }

    public void addOssData(String ossPath) {
        if (StringUtils.isEmpty(ossPath)) return;
        if (addShow) {
            addShow = false;
            this.remove(getData().size() - 1);
        }
        this.addData(ossSize, ossPath);
        ++ossSize;
        checkFoot();
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

    public void removeData(int position) {
        if (position < 0 || position >= this.getData().size()) return;
        if (addShow) {
            addShow = false;
            this.remove(getData().size() - 1);
        }
        if (position < ossSize) {
            --ossSize;
        }
        this.remove(position);
    }

    public List<String> getOssData() {
        if (ossSize <= 0) return new ArrayList<>();
        List<String> data = this.getData();
        return data.subList(0, ossSize - 1);
    }

    public List<String> getFileData() {
        List<String> data = this.getData();
        int size = data.size();
        if (size <= 0 || size >= ossSize) return new ArrayList<>();
        List<String> strings = data.subList(ossSize, size - 1);
        if (addShow) {
            strings.remove(strings.size() - 1);
        }
        return strings;
    }

}
