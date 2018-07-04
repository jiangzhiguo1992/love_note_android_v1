package com.jiangzg.mianmian.adapter;

import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.TimeUnit;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.mianmian.R;
import com.jiangzg.mianmian.base.BaseActivity;
import com.jiangzg.mianmian.domain.Audio;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Result;
import com.jiangzg.mianmian.domain.RxEvent;
import com.jiangzg.mianmian.helper.API;
import com.jiangzg.mianmian.helper.ApiHelper;
import com.jiangzg.mianmian.helper.ConsHelper;
import com.jiangzg.mianmian.helper.DialogHelper;
import com.jiangzg.mianmian.helper.RetrofitHelper;
import com.jiangzg.mianmian.helper.RxBus;
import com.jiangzg.mianmian.helper.SPHelper;
import com.jiangzg.mianmian.helper.TimeHelper;
import com.jiangzg.mianmian.view.FrescoAvatarView;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 耳语
 */
public class AudioAdapter extends BaseMultiItemQuickAdapter<Audio, BaseViewHolder> {

    private BaseActivity mActivity;
    private final Couple couple;
    private int playingIndex;
    private final String year;
    private final String month;
    private final String dayT;
    private final String hour;
    private final String minute;
    private final String second;

    public AudioAdapter(BaseActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_MY, R.layout.list_item_audio_right);
        addItemType(ApiHelper.LIST_TA, R.layout.list_item_audio_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
        playingIndex = -1;
        year = mActivity.getString(R.string.year);
        month = mActivity.getString(R.string.month);
        dayT = mActivity.getString(R.string.dayT);
        hour = mActivity.getString(R.string.hour_short);
        minute = mActivity.getString(R.string.minute_short);
        second = mActivity.getString(R.string.second);
    }

    @Override
    protected void convert(BaseViewHolder helper, Audio item) {
        // data
        String avatar = Couple.getAvatar(couple, item.getUserId());
        int playIcon = (playingIndex == helper.getLayoutPosition()) ? R.drawable.ic_pause_circle_primary : R.drawable.ic_play_circle_primary;
        TimeUnit timeUnit = TimeUnit.convertTime2Unit(TimeHelper.getJavaTimeByGo(item.getDuration()));
        String duration = timeUnit.getAllShow(true, true, true, true, true, true, year, month, dayT, hour, minute, second);
        duration = StringUtils.isEmpty(duration) ? "--" : duration;
        String happenAt = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String title = item.getTitle();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar);
        helper.setImageResource(R.id.ivPlay, playIcon);
        helper.setText(R.id.tvDuration, duration);
        helper.setText(R.id.tvHappenAt, happenAt);
        helper.setText(R.id.tvTitle, title);
        // click
        helper.addOnClickListener(R.id.llContent);
    }

    public void togglePlayAudio(int position) {
        Audio item = getItem(position);
        // 记录上一个播放
        int oldPlayingIndex = playingIndex;
        if (oldPlayingIndex == position) {
            // 点击相同的暂停
            playingIndex = -1;
        } else {
            // 点击不同的播放
            playingIndex = position;
        }
        if (oldPlayingIndex >= 0) {
            // 中断其他的播放
            notifyItemChanged(oldPlayingIndex);
        }
        if (playingIndex >= 0) {
            // 开始现在点击的
            notifyItemChanged(position);
        }
        // TODO 检查是否下载完毕
        // TODO 播放完毕记得刷新 index 和 adapter
        String contentAudio = item.getContentAudio();

    }

    public void showDeleteDialog(final int position) {
        Audio item = getItem(position);
        if (!item.isMine()) {
            ToastUtils.show(mActivity.getString(R.string.can_operation_self_create_audio));
            return;
        }
        MaterialDialog dialog = DialogHelper.getBuild(mActivity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_delete_this_audio)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        deleteApi(position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi(int position) {
        final Audio item = getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).audioDel(item.getId());
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxEvent<Audio> event = new RxEvent<>(ConsHelper.EVENT_AUDIO_LIST_ITEM_DELETE, item);
                RxBus.post(event);
            }

            @Override
            public void onFailure(String errMsg) {
            }
        });
    }

}
