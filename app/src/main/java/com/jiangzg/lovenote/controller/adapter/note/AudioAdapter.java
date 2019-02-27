package com.jiangzg.lovenote.controller.adapter.note;

import android.media.MediaPlayer;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.base.media.PlayerUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.OssResHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Audio;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import java.io.File;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 音频适配器
 */
public class AudioAdapter extends BaseMultiItemQuickAdapter<Audio, BaseViewHolder> {

    private final Couple couple;
    private final String year;
    private final String month;
    private final String dayT;
    private final String hour;
    private final String minute;
    private final String second;
    private BaseActivity mActivity;
    private int playingIndex;
    private MediaPlayer mMediaPlayer;

    public AudioAdapter(BaseActivity activity, MediaPlayer mediaPlayer) {
        super(null);
        addItemType(ApiHelper.LIST_NOTE_WHO_MY, R.layout.list_item_audio_right);
        addItemType(ApiHelper.LIST_NOTE_WHO_TA, R.layout.list_item_audio_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
        mMediaPlayer = mediaPlayer;
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
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        int playIcon = (playingIndex == helper.getLayoutPosition()) ? R.mipmap.ic_pause_circle_outline_white_48dp : R.mipmap.ic_play_circle_outline_white_48dp;
        TimeUnit timeUnit = TimeUnit.get(TimeHelper.getJavaTimeByGo(item.getDuration()));
        String duration = timeUnit.getAllShow(true, true, true, true, true, true, year, month, dayT, hour, minute, second);
        duration = StringUtils.isEmpty(duration) ? "--" : duration;
        String happenAt = TimeHelper.getTimeShowLine_HM_MDHM_YMDHM_ByGo(item.getHappenAt());
        String title = item.getTitle();
        // view
        FrescoAvatarView ivAvatar = helper.getView(R.id.ivAvatar);
        ivAvatar.setData(avatar, item.getUserId());
        helper.setImageResource(R.id.ivPlay, playIcon);
        helper.setText(R.id.tvDuration, duration);
        helper.setText(R.id.tvHappenAt, happenAt);
        helper.setText(R.id.tvTitle, title);
        // click
        helper.addOnClickListener(R.id.llContent);
    }

    public void togglePlayAudio(int position) {
        Audio item = getItem(position);
        if (item == null) return;
        // 检查是否下载完毕
        String contentAudio = item.getContentAudio();
        boolean fileExists = OssResHelper.isKeyFileExists(contentAudio);
        if (!fileExists) {
            ToastUtils.show(mActivity.getString(R.string.are_download));
            return;
        }
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
        togglePlay();
    }

    private void togglePlay() {
        if (mMediaPlayer == null) {
            ToastUtils.show(mActivity.getString(R.string.audio_load_fail));
            return;
        }
        if (playingIndex >= 0) {
            // 播放
            String contentAudio = getItem(playingIndex).getContentAudio();
            File audioFile = OssResHelper.newKeyFile(contentAudio);
            if (audioFile == null) {
                ToastUtils.show(mActivity.getString(R.string.audio_load_fail));
                return;
            }
            PlayerUtils.setData(mMediaPlayer, audioFile.getAbsolutePath());
            PlayerUtils.play(mMediaPlayer, mp -> {
                int oldPlay = AudioAdapter.this.playingIndex;
                AudioAdapter.this.playingIndex = -1;
                AudioAdapter.this.notifyItemChanged(oldPlay);
            }, null, (mp, what, extra) -> {
                ToastUtils.show(mActivity.getString(R.string.audio_load_fail));
                return true;
            });
        } else {
            // 暂停
            PlayerUtils.stop(mMediaPlayer);
        }
    }

    public void stopPlay() {
        int oldPlayingIndex = playingIndex;
        playingIndex = -1;
        if (oldPlayingIndex >= 0) {
            notifyItemChanged(oldPlayingIndex);
        }
        PlayerUtils.stop(mMediaPlayer);
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
                .onPositive((dialog1, which) -> deleteApi(position))
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private void deleteApi(int position) {
        final Audio item = getItem(position);
        Call<Result> api = new RetrofitHelper().call(API.class).noteAudioDel(item.getId());
        RetrofitHelper.enqueue(api, mActivity.getLoading(true), new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                // event
                RxBus.post(new RxBus.Event<>(RxBus.EVENT_AUDIO_LIST_ITEM_DELETE, item));
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
        mActivity.pushApi(api);
    }

}
