package com.jiangzg.lovenote.controller.activity.more;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.UserHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.api.Result;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.model.entity.Vip;
import com.jiangzg.lovenote.model.entity.VipLimit;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;

public class VipActivity extends BaseActivity<VipActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.srl)
    GSwipeRefreshLayout srl;

    @BindView(R.id.ivAvatarLeft)
    FrescoAvatarView ivAvatarLeft;
    @BindView(R.id.ivAvatarRight)
    FrescoAvatarView ivAvatarRight;
    @BindView(R.id.tvVipInfo)
    TextView tvVipInfo;

    @BindView(R.id.btnHistory)
    Button btnHistory;
    @BindView(R.id.btnBuy)
    Button btnBuy;

    @BindView(R.id.llLimit)
    LinearLayout llLimit;
    @BindView(R.id.tvLimitLeftWallSize)
    TextView tvLimitLeftWallSize;
    @BindView(R.id.tvLimitRightWallSize)
    TextView tvLimitRightWallSize;
    @BindView(R.id.tvLimitLeftWallCount)
    TextView tvLimitLeftWallCount;
    @BindView(R.id.tvLimitRightWallCount)
    TextView tvLimitRightWallCount;
    @BindView(R.id.tvLimitLeftTotal)
    TextView tvLimitLeftTotal;
    @BindView(R.id.tvLimitRightTotal)
    TextView tvLimitRightTotal;
    @BindView(R.id.tvLimitLeftSouvenir)
    TextView tvLimitLeftSouvenir;
    @BindView(R.id.tvLimitRightSouvenir)
    TextView tvLimitRightSouvenir;
    @BindView(R.id.tvLimitLeftVideoSize)
    TextView tvLimitLeftVideoSize;
    @BindView(R.id.tvLimitRightVideoSize)
    TextView tvLimitRightVideoSize;
    @BindView(R.id.tvLimitLeftVideoCount)
    TextView tvLimitLeftVideoCount;
    @BindView(R.id.tvLimitRightVideoCount)
    TextView tvLimitRightVideoCount;
    @BindView(R.id.tvLimitLeftAudioSize)
    TextView tvLimitLeftAudioSize;
    @BindView(R.id.tvLimitRightAudioSize)
    TextView tvLimitRightAudioSize;
    @BindView(R.id.tvLimitLeftAudioCount)
    TextView tvLimitLeftAudioCount;
    @BindView(R.id.tvLimitRightAudioCount)
    TextView tvLimitRightAudioCount;
    @BindView(R.id.tvLimitLeftPictureSize)
    TextView tvLimitLeftPictureSize;
    @BindView(R.id.tvLimitRightPictureSize)
    TextView tvLimitRightPictureSize;
    @BindView(R.id.tvLimitLeftPictureCount)
    TextView tvLimitLeftPictureCount;
    @BindView(R.id.tvLimitRightPictureCount)
    TextView tvLimitRightPictureCount;
    @BindView(R.id.tvLimitLeftDiaryImageSize)
    TextView tvLimitLeftDiaryImageSize;
    @BindView(R.id.tvLimitRightDiaryImageSize)
    TextView tvLimitRightDiaryImageSize;
    @BindView(R.id.tvLimitLeftDiaryImageCount)
    TextView tvLimitLeftDiaryImageCount;
    @BindView(R.id.tvLimitRightDiaryImageCount)
    TextView tvLimitRightDiaryImageCount;
    @BindView(R.id.tvLimitLeftWhisperImage)
    TextView tvLimitLeftWhisperImage;
    @BindView(R.id.tvLimitRightWhisperImage)
    TextView tvLimitRightWhisperImage;
    @BindView(R.id.tvLimitLeftGiftImageCount)
    TextView tvLimitLeftGiftImageCount;
    @BindView(R.id.tvLimitRightGiftImageCount)
    TextView tvLimitRightGiftImageCount;
    @BindView(R.id.tvLimitLeftFoodImageCount)
    TextView tvLimitLeftFoodImageCount;
    @BindView(R.id.tvLimitRightFoodImageCount)
    TextView tvLimitRightFoodImageCount;
    @BindView(R.id.tvLimitLeftMovieImageCount)
    TextView tvLimitLeftMovieImageCount;
    @BindView(R.id.tvLimitRightMovieImageCount)
    TextView tvLimitRightMovieImageCount;
    @BindView(R.id.tvLimitLeftPostImageCount)
    TextView tvLimitLeftPostImageCount;
    @BindView(R.id.tvLimitRightPostImageCount)
    TextView tvLimitRightPostImageCount;

    private Vip vip;
    private VipLimit vipYesLimit, vipNoLimit;
    private Observable<Vip> obRefresh;
    private Call<Result> callGet;

    public static void goActivity(Fragment from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from.getActivity(), VipActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from, VipActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Context from) {
        if (UserHelper.isCoupleBreak(SPHelper.getCouple())) {
            CouplePairActivity.goActivity(from);
            return;
        }
        Intent intent = new Intent(from, VipActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_vip;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.vip), true);
        srl.setEnabled(false);
        // view
        refreshView();
        // data
        refreshData();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        obRefresh = RxBus.register(ConsHelper.EVENT_VIP_INFO_REFRESH, vip -> refreshData());
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RxBus.unregister(ConsHelper.EVENT_VIP_INFO_REFRESH, obRefresh);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuHelp: // 帮助
                HelpActivity.goActivity(mActivity, HelpActivity.INDEX_MORE_VIP);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.btnHistory, R.id.btnBuy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnHistory: // 历史信息
                VipListActivity.goActivity(mActivity);
                break;
            case R.id.btnBuy: // 前往购买
                VipBuyActivity.goActivity(mActivity);
                break;
        }
    }

    private void refreshView() {
        // avatar
        User me = SPHelper.getMe();
        String myAvatar = UserHelper.getMyAvatar(me);
        String taAvatar = UserHelper.getTaAvatar(me);
        ivAvatarRight.setData(myAvatar);
        ivAvatarLeft.setData(taAvatar);
        // vip
        String vipInfo;
        if (vip != null) {
            String time = DateUtils.getStr(TimeHelper.getJavaTimeByGo(vip.getExpireAt()), DateUtils.FORMAT_LINE_Y_M_D_H_M);
            vipInfo = String.format(Locale.getDefault(), getString(R.string.vip_over_due_next_holder), time);
        } else {
            vipInfo = getString(R.string.no_handle);
        }
        tvVipInfo.setText(vipInfo);
        // limit
        llLimit.setVisibility(View.GONE);
        if (vipNoLimit != null) {
            // data
            String wallSize = String.format(Locale.getDefault(), getString(R.string.single_paper_max_holder), ConvertUtils.byte2FitSize(vipNoLimit.getWallPaperSize()));
            String wallCount = String.format(Locale.getDefault(), getString(R.string.can_upload_holder_paper), vipNoLimit.getWallPaperCount());
            String noteTotal = vipNoLimit.isNoteTotalEnable() ? getString(R.string.open) : getString(R.string.refuse);
            String souvenir = String.format(Locale.getDefault(), getString(R.string.can_add_holder), vipNoLimit.getSouvenirCount());
            String videoSize = String.format(Locale.getDefault(), getString(R.string.single_max_holder), ConvertUtils.byte2FitSize(vipNoLimit.getVideoSize()));
            String videoCount = String.format(Locale.getDefault(), getString(R.string.can_upload_holder), vipNoLimit.getVideoTotalCount());
            String audioSize = String.format(Locale.getDefault(), getString(R.string.single_max_holder), ConvertUtils.byte2FitSize(vipNoLimit.getAudioSize()));
            String audioCount = String.format(Locale.getDefault(), getString(R.string.can_upload_holder), vipNoLimit.getAudioTotalCount());
            String pictureSize = String.format(Locale.getDefault(), getString(R.string.single_paper_max_holder), ConvertUtils.byte2FitSize(vipNoLimit.getPictureSize()));
            String pictureCount = String.format(Locale.getDefault(), getString(R.string.can_upload_holder_paper), vipNoLimit.getPictureTotalCount());
            String diaryImageSize = String.format(Locale.getDefault(), getString(R.string.single_max_holder), ConvertUtils.byte2FitSize(vipNoLimit.getDiaryImageSize()));
            String diaryImageCount = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipNoLimit.getDiaryImageCount());
            String whisperImage = vipNoLimit.isWhisperImageEnable() ? getString(R.string.open_image_upload) : getString(R.string.refuse_image_upload);
            String giftImageCount = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipNoLimit.getGiftImageCount());
            String foodImageCount = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipNoLimit.getFoodImageCount());
            String movieImageCount = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipNoLimit.getMovieImageCount());
            String postImageCount = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipNoLimit.getTopicPostImageCount());
            // view
            llLimit.setVisibility(View.VISIBLE);
            tvLimitLeftWallSize.setText(wallSize);
            tvLimitLeftWallCount.setText(wallCount);
            tvLimitLeftTotal.setText(noteTotal);
            tvLimitLeftSouvenir.setText(souvenir);
            tvLimitLeftVideoSize.setText(videoSize);
            tvLimitLeftVideoCount.setText(videoCount);
            tvLimitLeftAudioSize.setText(audioSize);
            tvLimitLeftAudioCount.setText(audioCount);
            tvLimitLeftPictureSize.setText(pictureSize);
            tvLimitLeftPictureCount.setText(pictureCount);
            tvLimitLeftDiaryImageSize.setText(diaryImageSize);
            tvLimitLeftDiaryImageCount.setText(diaryImageCount);
            tvLimitLeftWhisperImage.setText(whisperImage);
            tvLimitLeftGiftImageCount.setText(giftImageCount);
            tvLimitLeftFoodImageCount.setText(foodImageCount);
            tvLimitLeftMovieImageCount.setText(movieImageCount);
            tvLimitLeftPostImageCount.setText(postImageCount);
        }
        if (vipYesLimit != null) {
            // data
            String wallSize = String.format(Locale.getDefault(), getString(R.string.single_paper_max_holder), ConvertUtils.byte2FitSize(vipYesLimit.getWallPaperSize()));
            String wallCount = String.format(Locale.getDefault(), getString(R.string.can_upload_holder_paper), vipYesLimit.getWallPaperCount());
            String noteTotal = vipYesLimit.isNoteTotalEnable() ? getString(R.string.open) : getString(R.string.refuse);
            String souvenir = String.format(Locale.getDefault(), getString(R.string.can_add_holder), vipYesLimit.getSouvenirCount());
            String videoSize = String.format(Locale.getDefault(), getString(R.string.single_max_holder), ConvertUtils.byte2FitSize(vipYesLimit.getVideoSize()));
            String videoCount = String.format(Locale.getDefault(), getString(R.string.can_upload_holder), vipYesLimit.getVideoTotalCount());
            String audioSize = String.format(Locale.getDefault(), getString(R.string.single_max_holder), ConvertUtils.byte2FitSize(vipYesLimit.getAudioSize()));
            String audioCount = String.format(Locale.getDefault(), getString(R.string.can_upload_holder), vipYesLimit.getAudioTotalCount());
            String pictureSize = String.format(Locale.getDefault(), getString(R.string.single_paper_max_holder), ConvertUtils.byte2FitSize(vipYesLimit.getPictureSize()));
            String pictureCount = String.format(Locale.getDefault(), getString(R.string.can_upload_holder_paper), vipYesLimit.getPictureTotalCount());
            String diaryImageSize = String.format(Locale.getDefault(), getString(R.string.single_max_holder), ConvertUtils.byte2FitSize(vipYesLimit.getDiaryImageSize()));
            String diaryImageCount = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipYesLimit.getDiaryImageCount());
            String whisperImage = vipYesLimit.isWhisperImageEnable() ? getString(R.string.open_image_upload) : getString(R.string.refuse_image_upload);
            String giftImageCount = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipYesLimit.getGiftImageCount());
            String foodImageCount = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipYesLimit.getFoodImageCount());
            String movieImageCount = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipYesLimit.getMovieImageCount());
            String postImageCount = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipYesLimit.getTopicPostImageCount());
            // view
            llLimit.setVisibility(View.VISIBLE);
            tvLimitRightWallSize.setText(wallSize);
            tvLimitRightWallCount.setText(wallCount);
            tvLimitRightTotal.setText(noteTotal);
            tvLimitRightSouvenir.setText(souvenir);
            tvLimitRightVideoSize.setText(videoSize);
            tvLimitRightVideoCount.setText(videoCount);
            tvLimitRightAudioSize.setText(audioSize);
            tvLimitRightAudioCount.setText(audioCount);
            tvLimitRightPictureSize.setText(pictureSize);
            tvLimitRightPictureCount.setText(pictureCount);
            tvLimitRightDiaryImageSize.setText(diaryImageSize);
            tvLimitRightDiaryImageCount.setText(diaryImageCount);
            tvLimitRightWhisperImage.setText(whisperImage);
            tvLimitRightGiftImageCount.setText(giftImageCount);
            tvLimitRightFoodImageCount.setText(foodImageCount);
            tvLimitRightMovieImageCount.setText(movieImageCount);
            tvLimitRightPostImageCount.setText(postImageCount);
        }
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        callGet = new RetrofitHelper().call(API.class).moreVipHomeGet();
        RetrofitHelper.enqueue(callGet, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                vip = data.getVip();
                VipLimit vipLimit = data.getVipLimit();
                SPHelper.setVipLimit(vipLimit);
                vipYesLimit = data.getVipYesLimit();
                vipNoLimit = data.getVipNoLimit();
                refreshView();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
    }

}
