package com.jiangzg.lovenote.controller.adapter.note;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.common.ApiHelper;
import com.jiangzg.lovenote.helper.common.CountHelper;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.TimeHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.media.PlayerHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.DialogHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.Audio;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.view.FrescoAvatarView;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/12.
 * 音频适配器
 */
public class AudioAdapter extends BaseMultiItemQuickAdapter<Audio, BaseViewHolder> {

    private final Couple couple;
    private BaseActivity mActivity;

    private ExoPlayer player;
    private SimpleCache simpleCache;
    private ExtractorMediaSource mediaSource;
    private int playingIndex;

    public AudioAdapter(BaseActivity activity) {
        super(null);
        addItemType(ApiHelper.LIST_NOTE_WHO_MY, R.layout.list_item_audio_right);
        addItemType(ApiHelper.LIST_NOTE_WHO_TA, R.layout.list_item_audio_left);
        mActivity = activity;
        couple = SPHelper.getCouple();
        // player
        player = PlayerHelper.getAudioPlayer(mActivity);
        simpleCache = PlayerHelper.getSimpleCache();
        playingIndex = -1;
    }

    @Override
    protected void convert(BaseViewHolder helper, Audio item) {
        // data
        String avatar = UserHelper.getAvatar(couple, item.getUserId());
        int playIcon = (playingIndex == helper.getLayoutPosition()) ? R.mipmap.ic_pause_circle_outline_white_48dp : R.mipmap.ic_play_circle_outline_white_48dp;
        String duration = CountHelper.getDurationShow(item.getDuration());
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
        if (player == null) {
            player = PlayerHelper.getAudioPlayer(mActivity);
        }
        if (simpleCache == null) {
            simpleCache = PlayerHelper.getSimpleCache();
        }
        if (playingIndex >= 0) {
            // 播放
            String contentAudio = getItem(playingIndex).getContentAudio();
            PlayerHelper.release(null, null, mediaSource);
            mediaSource = PlayerHelper.getDataSource(mActivity, simpleCache, contentAudio);
            PlayerHelper.play(player, mediaSource);
        } else {
            // 暂停
            player.stop();
        }
    }

    public void stopPlay() {
        int oldPlayingIndex = playingIndex;
        playingIndex = -1;
        if (oldPlayingIndex >= 0) {
            notifyItemChanged(oldPlayingIndex);
        }
        if (player != null) {
            player.stop();
        }
    }

    public void releasePlay() {
        PlayerHelper.release(player, simpleCache, mediaSource);
    }

    public void showDeleteDialog(final int position) {
        Audio item = getItem(position);
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
