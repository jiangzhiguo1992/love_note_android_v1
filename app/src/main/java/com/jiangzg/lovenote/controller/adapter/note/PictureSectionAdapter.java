package com.jiangzg.lovenote.controller.adapter.note;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.BigImageActivity;
import com.jiangzg.lovenote.controller.activity.common.MapShowActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.engine.PictureSection;
import com.jiangzg.lovenote.model.entity.Picture;
import com.jiangzg.lovenote.view.FrescoView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by JZG on 2019/3/9.
 */
public class PictureSectionAdapter extends BaseSectionQuickAdapter<PictureSection, BaseViewHolder> {

    private static final int MODEL_IMAGE = 0;
    private static final int MODEL_DETAIL = 1;

    private final BaseActivity mActivity;
    private final int imageWidth, imageHeight;
    //private final int minHeight, maxHeight;
    private final int colorPrimary;
    private int mModel;

    public PictureSectionAdapter(BaseActivity activity) {
        super(R.layout.list_item_picture, R.layout.list_head_section_picture, new ArrayList<>());
        mActivity = activity;
        mModel = MODEL_IMAGE;
        float screenWidth = ScreenUtils.getScreenRealWidth(activity);
        int dp5 = ConvertUtils.dp2px(5);
        imageWidth = imageHeight = (int) (screenWidth - dp5 * 4) / 3;
        //minHeight = imageHeight / 2;
        //maxHeight = imageHeight * 2;
        colorPrimary = ContextCompat.getColor(activity, ViewUtils.getColorPrimary(mActivity));
    }

    public List<PictureSection> getSectionList(boolean clear, List<Picture> list) {
        if (clear) return getNewPictureList(list);
        else return getMorePictureList(list);
    }

    private List<PictureSection> getNewPictureList(List<Picture> list) {
        List<PictureSection> sectionList = new ArrayList<>();
        if (sectionList.size() > 0) sectionList.clear();
        if (list == null || list.size() <= 0) return sectionList;
        // 当前head
        PictureSection head = null;
        for (int i = 0; i < list.size(); i++) {
            // 开始循环
            Picture picture = list.get(i);
            if (picture == null) continue;
            if (i <= 0 || sectionList.size() <= 0 || head == null) {
                // 第一个
                head = new PictureSection(true, "");
                head.t = picture;
                head.setSection(0);
                sectionList.add(head);
                PictureSection section = new PictureSection(false, "");
                section.t = picture;
                section.setSection(0);
                sectionList.add(section);
            } else {
                // 不是第一个
                int oldSection = head.getSection();
                long headHappenAt = TimeHelper.getJavaTimeByGo(head.t.getHappenAt());
                long nowHappenAt = TimeHelper.getJavaTimeByGo(picture.getHappenAt());
                if (!DateUtils.isSameDay(headHappenAt, nowHappenAt)) {
                    // 不是同一天
                    head = new PictureSection(true, "");
                    head.t = picture;
                    head.setSection(oldSection + 1);
                    sectionList.add(head);
                    PictureSection section = new PictureSection(false, "");
                    section.t = picture;
                    section.setSection(oldSection + 1);
                    sectionList.add(section);
                } else {
                    // 是同一天
                    PictureSection section = new PictureSection(false, "");
                    section.t = picture;
                    section.setSection(oldSection);
                    sectionList.add(section);
                }
            }
        }
        return sectionList;
    }

    private List<PictureSection> getMorePictureList(List<Picture> list) {
        List<PictureSection> sectionList = new ArrayList<>();
        if (list == null || list.size() <= 0) return sectionList;
        if (sectionList.size() <= 0) return getNewPictureList(list);
        // 当前head
        PictureSection head = null;
        for (int i = 0; i < list.size(); i++) {
            // 开始循环
            Picture picture = list.get(i);
            if (picture == null) continue;
            if (i <= 0 || sectionList.size() <= 0 || head == null) {
                // 第一个
                List<PictureSection> datas = getData();
                if (datas.size() <= 0) {
                    head = new PictureSection(true, "");
                    head.t = picture;
                    head.setSection(0);
                    sectionList.add(head);
                    PictureSection section = new PictureSection(false, "");
                    section.t = picture;
                    section.setSection(0);
                    sectionList.add(section);
                    continue;
                } else {
                    head = datas.get(getItemCount() - 1);
                }
                int oldSection = head.getSection();
                long headHappenAt = TimeHelper.getJavaTimeByGo(head.t.getHappenAt());
                long nowHappenAt = TimeHelper.getJavaTimeByGo(picture.getHappenAt());
                if (!DateUtils.isSameDay(headHappenAt, nowHappenAt)) {
                    // 不是同一天
                    head = new PictureSection(true, "");
                    head.t = picture;
                    head.setSection(oldSection + 1);
                    sectionList.add(head);
                    PictureSection section = new PictureSection(false, "");
                    section.t = picture;
                    section.setSection(oldSection + 1);
                    sectionList.add(section);
                } else {
                    // 是同一天
                    PictureSection section = new PictureSection(false, "");
                    section.t = picture;
                    section.setSection(oldSection);
                    sectionList.add(section);
                }
            } else {
                // 不是第一个
                int oldSection = head.getSection();
                long headHappenAt = TimeHelper.getJavaTimeByGo(head.t.getHappenAt());
                long nowHappenAt = TimeHelper.getJavaTimeByGo(picture.getHappenAt());
                if (!DateUtils.isSameDay(headHappenAt, nowHappenAt)) {
                    // 不是同一天
                    head = new PictureSection(true, "");
                    head.t = picture;
                    head.setSection(oldSection + 1);
                    sectionList.add(head);
                    PictureSection section = new PictureSection(false, "");
                    section.t = picture;
                    section.setSection(oldSection + 1);
                    sectionList.add(section);
                } else {
                    // 是同一天
                    PictureSection section = new PictureSection(false, "");
                    section.t = picture;
                    section.setSection(oldSection);
                    sectionList.add(section);
                }
            }
        }
        return sectionList;
    }

    private ArrayList<String> getOssKeyListWithoutHeader() {
        ArrayList<String> ossKeyList = new ArrayList<>();
        List<PictureSection> datas = getData();
        if (datas.size() <= 0) return ossKeyList;
        for (PictureSection section : datas) {
            if (section == null || section.isHeader) {
                continue;
            }
            Picture picture = section.t;
            if (picture == null || StringUtils.isEmpty(picture.getContentImage())) {
                continue;
            }
            ossKeyList.add(picture.getContentImage());
        }
        return ossKeyList;
    }

    // 切换显示模式
    public void toggleModel() {
        mModel = (mModel == MODEL_DETAIL) ? MODEL_IMAGE : MODEL_DETAIL;
        this.notifyDataSetChanged();
    }

    @Override
    protected void convertHead(BaseViewHolder helper, PictureSection item) {
        Picture picture = item.t;
        String happenAt = DateUtils.getStr(TimeHelper.getJavaTimeByGo(picture.getHappenAt()), DateUtils.FORMAT_LINE_Y_M_D);
        helper.setText(R.id.tvHappenAt, happenAt);
    }

    @Override
    protected void convert(BaseViewHolder helper, PictureSection item) {
        Picture picture = item.t;
        String happen = DateUtils.getStr(TimeHelper.getJavaTimeByGo(picture.getHappenAt()), DateUtils.FORMAT_H_M);
        String address = picture.getAddress();
        String content = picture.getContentImage();
        // view
        if (mModel != MODEL_DETAIL) {
            helper.setVisible(R.id.tvHappenAt, false);
            helper.setVisible(R.id.vLine, false);
            helper.setVisible(R.id.tvAddress, false);
        } else {
            helper.setVisible(R.id.tvHappenAt, true);
            helper.setVisible(R.id.vLine, !StringUtils.isEmpty(address));
            helper.setVisible(R.id.tvAddress, !StringUtils.isEmpty(address));
            helper.setText(R.id.tvHappenAt, happen);
            helper.setText(R.id.tvAddress, address);
        }
        FrescoView ivPicture = helper.getView(R.id.ivPicture);
        // 按版本来采样吧，低版本不能太大
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ivPicture.setWidthAndHeight(imageWidth / 2, imageHeight / 2);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ivPicture.setWidthAndHeight(imageWidth / 3, imageHeight / 3);
        } else {
            ivPicture.setWidthAndHeight(imageWidth / 4, imageHeight / 4);
        }
        // 主色值设置
        ivPicture.setBitmapListener(new FrescoView.BitmapListener() {
            @Override
            public void onBitmapSuccess(FrescoView iv, Bitmap bitmap) {
                Palette.from(bitmap).generate(palette -> {
                    int rgb = 0;
                    Palette.Swatch vibrantSwatch = palette.getVibrantSwatch();
                    if (vibrantSwatch != null) {
                        rgb = vibrantSwatch.getRgb();
                    } else {
                        Palette.Swatch mutedSwatch = palette.getMutedSwatch();
                        if (mutedSwatch != null) {
                            rgb = mutedSwatch.getRgb();
                        }
                    }
                    if (rgb != 0) {
                        helper.setBackgroundColor(R.id.llBottom, rgb);
                    } else {
                        helper.setBackgroundColor(R.id.llBottom, colorPrimary);
                    }
                });
            }

            @Override
            public void onBitmapFail(FrescoView iv) {
                helper.setBackgroundColor(R.id.llBottom, colorPrimary);
            }
        });
        // 为瀑布流定制不一样的宽高(这里起始高度一定不能wrap，否则会一开始就都加在)
        //ivPicture.setLoadListener(new FrescoView.LoadListener() {
        //    @Override
        //    public void onLoadSuccess(FrescoView iv, ImageInfo imageInfo) {
        //        float width = imageInfo.getWidth();
        //        float height = imageInfo.getHeight();
        //        float finalHeight = (height / width * imageWidth);
        //        if (finalHeight < minHeight) finalHeight = minHeight;
        //        if (finalHeight > maxHeight) finalHeight = maxHeight;
        //        LogUtils.i(PictureSectionAdapter.class, "高度", finalHeight + " == " + width + " " + height);
        //        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) iv.getLayoutParams();
        //        layoutParams.height = (int) finalHeight;
        //        iv.setLayoutParams(layoutParams);
        //    }
        //
        //    @Override
        //    public void onLoadFail(FrescoView iv) {
        //    }
        //});
        // 点击全屏
        ivPicture.setClickListener(iv -> {
            ArrayList<String> ossKeyList = PictureSectionAdapter.this.getOssKeyListWithoutHeader();
            if (ossKeyList.size() <= 0) return;
            int layoutPosition = helper.getLayoutPosition();
            PictureSection ps = getItem(layoutPosition);
            int position = layoutPosition - ps.getSection() - 1;
            if (position < 0 || position > ossKeyList.size()) {
                position = 0;
            }
            BigImageActivity.goActivityByOssList(mActivity, ossKeyList, position, iv);
        });
        ivPicture.setData(content);
        // listener
        helper.addOnClickListener(R.id.tvAddress);
    }

    public void onLocationClick(int position) {
        Picture item = getItem(position).t;
        String address = item.getAddress();
        double longitude = item.getLongitude();
        double latitude = item.getLatitude();
        MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

    public void showDeleteDialog(final int position) {
        //hideOperation();
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_note)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive((dialog1, which) -> deleteApi(position))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi(int position) {
        final Picture item = getItem(position).t;
        Call<Result> api = new RetrofitHelper().call(API.class).notePictureDel(item.getId());
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_PICTURE_LIST_ITEM_DELETE, item));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

}
