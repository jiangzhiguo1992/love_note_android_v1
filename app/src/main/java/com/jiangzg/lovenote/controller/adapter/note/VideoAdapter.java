package com.jiangzg.lovenote.controller.adapter.note;

import android.content.Intent;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.component.IntentFactory;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.common.MapShowActivity;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.OssResHelper;
import com.jiangzg.lovenote.helper.common.ResHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Video;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.FrescoView;

import java.io.File;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 视频适配器
 */
public class VideoAdapter extends BaseMultiItemQuickAdapter<Video, BaseViewHolder> {

    private final Couple couple;
    private final int dp200;
    private final int dp150;
    private final String year;
    private final String month;
    private final String dayT;
    private final String hour;
    private final String minute;
    private final String second;
    private BaseActivity mActivity;

    public VideoAdapter(BaseActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_NOTE_MY, R.layout.list_item_video_right);
        addItemType(ApiHelper.LIST_NOTE_TA, R.layout.list_item_video_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
        dp200 = ConvertUtils.dp2px(200);
        dp150 = ConvertUtils.dp2px(150);
        year = mActivity.getString(R.string.year);
        month = mActivity.getString(R.string.month);
        dayT = mActivity.getString(R.string.dayT);
        hour = mActivity.getString(R.string.hour_short);
        minute = mActivity.getString(R.string.minute_short);
        second = mActivity.getString(R.string.second);
    }

    @Override
    protected void convert(BaseViewHolder helper, Video item) {
        // data
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        String thumb = item.getContentThumb();
        String title = item.getTitle();
        TimeUnit timeUnit = TimeUnit.get(TimeHelper.getJavaTimeByGo(item.getDuration()));
        String duration = timeUnit.getAllShow(true, true, true, true, true, true, year, month, dayT, hour, minute, second);
        duration = StringUtils.isEmpty(duration) ? "--" : duration;
        String happenAt = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String address = item.getAddress();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar, item.getUserId());
        FrescoView ivThumb = helper.getView(R.id.ivThumb);
        if (StringUtils.isEmpty(thumb)) {
            ivThumb.setVisibility(View.GONE);
        } else {
            ivThumb.setVisibility(View.VISIBLE);
            ivThumb.setWidthAndHeight(dp200, dp150);
            ivThumb.setData(thumb);
        }
        helper.setText(R.id.tvTitle, title);
        helper.setText(R.id.tvDuration, duration);
        helper.setText(R.id.tvHappenAt, happenAt);
        helper.setText(R.id.tvAddress, address);
        helper.setVisible(R.id.tvAddress, !StringUtils.isEmpty(address));
        // click
        helper.addOnClickListener(R.id.cvVideo);
        helper.addOnClickListener(R.id.tvAddress);
        helper.addOnLongClickListener(R.id.cvVideo);
    }

    public void playVideo(int position) {
        Video item = getItem(position);
        if (item == null) return;
        //// 跳转视频播放页面
        //String contentVideo = item.getContentVideo();
        //VideoPlayActivity.goActivity(mActivity, contentVideo);
        // 检查是否下载完毕
        String contentVideo = item.getContentVideo();
        File file = OssResHelper.newKeyFile(contentVideo);
        if (!FileUtils.isFileExists(file)) {
            ToastUtils.show(mActivity.getString(R.string.are_download));
            return;
        }
        // 播放调用系统界面
        Intent intent = IntentFactory.getVideoPlayByFile(ResHelper.getFileProviderAuth(), file);
        ActivityTrans.start(mActivity, intent);
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
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_video));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_video)
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
