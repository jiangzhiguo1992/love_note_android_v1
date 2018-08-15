package com.jiangzg.lovenote.activity.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.ConvertUtils;
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.base.view.ToastUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.domain.Result;
import com.jiangzg.lovenote.domain.User;
import com.jiangzg.lovenote.domain.Vip;
import com.jiangzg.lovenote.domain.VipLimit;
import com.jiangzg.lovenote.helper.API;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RetrofitHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.TimeHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.view.FrescoAvatarView;
import com.jiangzg.lovenote.view.GSwipeRefreshLayout;

import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import rx.Observable;
import rx.functions.Action1;

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
    //@BindView(R.id.llLimitWallSize)
    //LinearLayout llLimitWallSize;
    @BindView(R.id.tvLimitLeftWallCount)
    TextView tvLimitLeftWallCount;
    @BindView(R.id.tvLimitRightWallCount)
    TextView tvLimitRightWallCount;
    //@BindView(R.id.llLimitWallCount)
    //LinearLayout llLimitWallCount;
    @BindView(R.id.tvLimitLeftTotal)
    TextView tvLimitLeftTotal;
    @BindView(R.id.tvLimitRightTotal)
    TextView tvLimitRightTotal;
    //@BindView(R.id.llLimitTotal)
    //LinearLayout llLimitTotal;
    @BindView(R.id.tvLimitLeftSouvenir)
    TextView tvLimitLeftSouvenir;
    @BindView(R.id.tvLimitRightSouvenir)
    TextView tvLimitRightSouvenir;
    //@BindView(R.id.llLimitSouvenir)
    //LinearLayout llLimitSouvenir;
    @BindView(R.id.tvLimitLeftVideoSize)
    TextView tvLimitLeftVideoSize;
    @BindView(R.id.tvLimitRightVideoSize)
    TextView tvLimitRightVideoSize;
    //@BindView(R.id.llLimitVideoSize)
    //LinearLayout llLimitVideoSize;
    @BindView(R.id.tvLimitLeftVideoCount)
    TextView tvLimitLeftVideoCount;
    @BindView(R.id.tvLimitRightVideoCount)
    TextView tvLimitRightVideoCount;
    //@BindView(R.id.llLimitVideoCount)
    //LinearLayout llLimitVideoCount;
    @BindView(R.id.tvLimitLeftAudioSize)
    TextView tvLimitLeftAudioSize;
    @BindView(R.id.tvLimitRightAudioSize)
    TextView tvLimitRightAudioSize;
    //@BindView(R.id.llLimitAudioSize)
    //LinearLayout llLimitAudioSize;
    @BindView(R.id.tvLimitLeftAudioCount)
    TextView tvLimitLeftAudioCount;
    @BindView(R.id.tvLimitRightAudioCount)
    TextView tvLimitRightAudioCount;
    //@BindView(R.id.llLimitAudioCount)
    //LinearLayout llLimitAudioCount;
    @BindView(R.id.tvLimitLeftPictureSize)
    TextView tvLimitLeftPictureSize;
    @BindView(R.id.tvLimitRightPictureSize)
    TextView tvLimitRightPictureSize;
    //@BindView(R.id.llLimitPictureSize)
    //LinearLayout llLimitPictureSize;
    @BindView(R.id.tvLimitLeftPictureCount)
    TextView tvLimitLeftPictureCount;
    @BindView(R.id.tvLimitRightPictureCount)
    TextView tvLimitRightPictureCount;
    //@BindView(R.id.llLimitPictureCount)
    //LinearLayout llLimitPictureCount;
    @BindView(R.id.tvLimitLeftDiaryImageSize)
    TextView tvLimitLeftDiaryImageSize;
    @BindView(R.id.tvLimitRightDiaryImageSize)
    TextView tvLimitRightDiaryImageSize;
    //@BindView(R.id.llLimitDiaryImageSize)
    //LinearLayout llLimitDiaryImageSize;
    @BindView(R.id.tvLimitLeftDiaryImageCount)
    TextView tvLimitLeftDiaryImageCount;
    @BindView(R.id.tvLimitRightDiaryImageCount)
    TextView tvLimitRightDiaryImageCount;
    //@BindView(R.id.llLimitDiaryImageCount)
    //LinearLayout llLimitDiaryImageCount;
    @BindView(R.id.tvLimitLeftWhisperImage)
    TextView tvLimitLeftWhisperImage;
    @BindView(R.id.tvLimitRightWhisperImage)
    TextView tvLimitRightWhisperImage;
    //@BindView(R.id.llLimitWhisperImage)
    //LinearLayout llLimitWhisperImage;
    @BindView(R.id.tvLimitLeftGiftImageCount)
    TextView tvLimitLeftGiftImageCount;
    @BindView(R.id.tvLimitRightGiftImageCount)
    TextView tvLimitRightGiftImageCount;
    //@BindView(R.id.llLimitGiftImageCount)
    //LinearLayout llLimitGiftImageCount;
    @BindView(R.id.tvLimitLeftFoodImageCount)
    TextView tvLimitLeftFoodImageCount;
    @BindView(R.id.tvLimitRightFoodImageCount)
    TextView tvLimitRightFoodImageCount;
    //@BindView(R.id.llLimitFoodImageCount)
    //LinearLayout llLimitFoodImageCount;
    @BindView(R.id.tvLimitLeftPostImageCount)
    TextView tvLimitLeftPostImageCount;
    @BindView(R.id.tvLimitRightPostImageCount)
    TextView tvLimitRightPostImageCount;
    //@BindView(R.id.llLimitPostImageCount)
    //LinearLayout llLimitPostImageCount;

    private Vip vip;
    private VipLimit vipYesLimit, vipNoLimit;
    private Call<Result> callGet;
    private Observable<Vip> obRefresh;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), VipActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    public static void goActivity(Activity from) {
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
        obRefresh = RxBus.register(ConsHelper.EVENT_VIP_INFO_REFRESH, new Action1<Vip>() {
            @Override
            public void call(Vip vip) {
                refreshData();
            }
        });
    }

    @Override
    protected void onFinish(Bundle state) {
        RetrofitHelper.cancel(callGet);
        RxBus.unregister(ConsHelper.EVENT_VIP_INFO_REFRESH, obRefresh);
    }

    @OnClick({R.id.btnHistory, R.id.btnBuy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnHistory: // 历史信息
                VipListActivity.goActivity(mActivity);
                break;
            case R.id.btnBuy: // 前往购买
                ToastUtils.show("敬请期待");
                //VipBuyActivity.goActivity(mActivity);
                break;
        }
    }

    private void refreshView() {
        // avatar
        User me = SPHelper.getMe();
        String myAvatar = me.getMyAvatarInCp();
        String taAvatar = me.getTaAvatarInCp();
        ivAvatarRight.setData(myAvatar);
        ivAvatarLeft.setData(taAvatar);
        // vip
        String vipInfo;
        if (vip != null) {
            String time = DateUtils.getString(TimeHelper.getJavaTimeByGo(vip.getExpireAt()), ConstantUtils.FORMAT_LINE_Y_M_D_H_M);
            vipInfo = String.format(Locale.getDefault(), getString(R.string.on_space_holder_space_over__due), time);
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
