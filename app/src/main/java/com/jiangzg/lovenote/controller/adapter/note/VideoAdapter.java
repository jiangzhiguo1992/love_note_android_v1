package com.jiangzg.lovenote.controller.adapter.note;

import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.view.ScreenUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.MapShowActivity;
import com.jiangzg.lovenote.controller.activity.common.VideoPlayActivity;
import com.jiangzg.lovenote.helper.common.CountHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Video;
import com.jiangzg.lovenote.view.FrescoView;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 视频适配器
 */
public class VideoAdapter extends BaseQuickAdapter<Video, BaseViewHolder> {

    private final int imgWidth, imgHeight;
    private BaseActivity mActivity;

    public VideoAdapter(BaseActivity activity) {
        super(R.layout.list_item_video);
        mActivity = activity;
        imgWidth = imgHeight = ScreenUtils.getScreenRealWidth(activity) / 2;
    }

    @Override
    protected void convert(BaseViewHolder helper, Video item) {
        // data
        String thumb = item.getContentThumb();
        String title = item.getTitle();
        String duration = CountHelper.getDurationShow(item.getDuration());
        String happenAt = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String address = item.getAddress();
        // view
        FrescoView ivThumb = helper.getView(R.id.ivThumb);
        if (StringUtils.isEmpty(thumb)) {
            ivThumb.setVisibility(View.GONE);
        } else {
            ivThumb.setVisibility(View.VISIBLE);
            ivThumb.initHierarchy(null, true, false, false, false, false, false);
            ivThumb.setWidthAndHeight(imgWidth, imgHeight);
            ivThumb.setData(thumb);
        }
        helper.setText(R.id.tvDuration, duration);
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvHappenAt, happenAt);
        helper.setVisible(R.id.ivLocation, !StringUtils.isEmpty(address));
        // click
        helper.addOnClickListener(R.id.ivLocation);
    }

    // TODO
    public void playVideo(int position) {
        Video item = getItem(position);
        if (item == null) return;
        // 跳转视频播放页面
        VideoPlayActivity.goActivity(mActivity, item);
        //// 检查是否下载完毕
        //String contentVideo = item.getContentVideo();
        //File file = ResHelper.newKeyFile(contentVideo);
        //if (!FileUtils.isFileExists(file)) {
        //    ToastUtils.show(mActivity.getString(R.string.are_download));
        //    return;
        //}
        //// 播放调用系统界面
        //Intent intent = IntentFactory.getVideoPlayByFile(ResHelper.getFileProviderAuth(), file);
        //ActivityTrans.start(mActivity, intent);
    }

    public void goMapShow(int position) {
        Video item = getItem(position);
        String address = item.getAddress();
        double longitude = item.getLongitude();
        double latitude = item.getLatitude();
        MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

    public void showDeleteDialog(final int position) {
        Video item = getItem(position);
        if (!item.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_note));
            return;
        }
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
        final Video item = getItem(position);
        Call<Result> api = new RetrofitHelper().call(API.class).noteVideoDel(item.getId());
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_VIDEO_LIST_ITEM_DELETE, item));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

    public void selectVideo(int position) {
        mActivity.finish(); // 必须先关闭
        Video item = getItem(position);
        RxBus.post(new RxBus.Event<>(RxBus.EVENT_VIDEO_SELECT, item));
    }
}
