package com.jiangzg.mianmian.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.TimeUnit;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.activity.book.VideoEditActivity;
import com.jiangzg.mianmian.activity.common.MapShowActivity;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Video;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;
import com.jiangzg.mianmian.view.FrescoView;

/**
 * Created by JZG on 2018/3/12.
 * 耳语
 */
public class VideoAdapter extends BaseMultiItemQuickAdapter<Video, BaseViewHolder> {

    private BaseActivity mActivity;
    private final Couple couple;
    private final int dp200;
    private final int dp150;
    private final String year;
    private final String month;
    private final String dayT;
    private final String hour;
    private final String minute;
    private final String second;

    public VideoAdapter(BaseActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_MY, R.layout.list_item_video_right);
        addItemType(ApiHelper.LIST_TA, R.layout.list_item_video_left);
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
        String avatar = Couple.getAvatar(couple, item.getUserId());
        String thumb = item.getContentThumb();
        String title = item.getTitle();
        TimeUnit timeUnit = TimeUnit.convertTime2Unit(TimeHelper.getJavaTimeByGo(item.getDuration()));
        String duration = timeUnit.getAllShow(true, true, true, true, true, true, year, month, dayT, hour, minute, second);
        duration = StringUtils.isEmpty(duration) ? "--" : duration;
        String happenAt = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String address = item.getAddress();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
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
    }

    public void playAudio(int position) {
        // TODO
    }

    public void goVideoEdit(int position) {
        Video item = getItem(position);
        VideoEditActivity.goActivity(mActivity, item);
    }

    public void goMapShow(int position) {
        Video item = getItem(position);
        String address = item.getAddress();
        double longitude = item.getLongitude();
        double latitude = item.getLatitude();
        MapShowActivity.goActivity(mActivity, address, longitude, latitude);
    }

}
