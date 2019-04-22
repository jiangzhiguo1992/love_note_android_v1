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
import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.controller.activity.couple.CouplePairActivity;
import com.jiangzg.lovenote.controller.activity.settings.HelpActivity;
import com.jiangzg.lovenote.helper.common.RxBus;
import com.jiangzg.lovenote.helper.common.SPHelper;
import com.jiangzg.lovenote.helper.common.UserHelper;
import com.jiangzg.lovenote.helper.system.RetrofitHelper;
import com.jiangzg.lovenote.helper.view.ViewHelper;
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
    @BindView(R.id.btnHistory)
    Button btnHistory;
    @BindView(R.id.btnBuy)
    Button btnBuy;

    @BindView(R.id.llLimit)
    LinearLayout llLimit;
    @BindView(R.id.tvAdVipYes)
    TextView tvAdVipYes;
    @BindView(R.id.tvAdVipNo)
    TextView tvAdVipNo;
    @BindView(R.id.tvWallVipYes)
    TextView tvWallVipYes;
    @BindView(R.id.tvWallVipNo)
    TextView tvWallVipNo;
    @BindView(R.id.tvTotalVipYes)
    TextView tvTotalVipYes;
    @BindView(R.id.tvTotalVipNo)
    TextView tvTotalVipNo;
    @BindView(R.id.tvSouvenirVipYes)
    TextView tvSouvenirVipYes;
    @BindView(R.id.tvSouvenirVipNo)
    TextView tvSouvenirVipNo;
    @BindView(R.id.tvAudioVipYes)
    TextView tvAudioVipYes;
    @BindView(R.id.tvAudioVipNo)
    TextView tvAudioVipNo;
    @BindView(R.id.tvVideoVipYes)
    TextView tvVideoVipYes;
    @BindView(R.id.tvVideoVipNo)
    TextView tvVideoVipNo;
    @BindView(R.id.tvAlbumVipYes)
    TextView tvAlbumVipYes;
    @BindView(R.id.tvAlbumVipNo)
    TextView tvAlbumVipNo;
    @BindView(R.id.tvDiaryVipYes)
    TextView tvDiaryVipYes;
    @BindView(R.id.tvDiaryVipNo)
    TextView tvDiaryVipNo;
    @BindView(R.id.tvWhisperVipYes)
    TextView tvWhisperVipYes;
    @BindView(R.id.tvWhisperVipNo)
    TextView tvWhisperVipNo;
    @BindView(R.id.tvGiftVipYes)
    TextView tvGiftVipYes;
    @BindView(R.id.tvGiftVipNo)
    TextView tvGiftVipNo;
    @BindView(R.id.tvFoodVipYes)
    TextView tvFoodVipYes;
    @BindView(R.id.tvFoodVipNo)
    TextView tvFoodVipNo;
    @BindView(R.id.tvMovieVipYes)
    TextView tvMovieVipYes;
    @BindView(R.id.tvMovieVipNo)
    TextView tvMovieVipNo;

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
        refreshView(null, null);
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
        // event
        Observable<Vip> bus = RxBus.register(RxBus.EVENT_VIP_INFO_REFRESH, vip -> refreshData());
        pushBus(RxBus.EVENT_VIP_INFO_REFRESH, bus);
        // avatar
        User me = SPHelper.getMe();
        User ta = SPHelper.getTa();
        String myAvatar = UserHelper.getMyAvatar(me);
        String taAvatar = UserHelper.getTaAvatar(me);
        ivAvatarRight.setData(myAvatar, me);
        ivAvatarLeft.setData(taAvatar, ta);
        // data
        refreshData();
    }

    @Override
    protected void onFinish(Bundle state) {
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
            case R.id.btnHistory: // 购买历史
                VipListActivity.goActivity(mActivity);
                break;
            case R.id.btnBuy: // 前往购买
                VipBuyActivity.goActivity(mActivity);
                break;
        }
    }

    private void refreshData() {
        if (!srl.isRefreshing()) {
            srl.setRefreshing(true);
        }
        Call<Result> api = new RetrofitHelper().call(API.class).moreVipHomeGet();
        RetrofitHelper.enqueue(api, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
                SPHelper.setVipLimit(data.getVipLimit());
                refreshView(data.getVipYesLimit(), data.getVipNoLimit());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                srl.setRefreshing(false);
            }
        });
        pushApi(api);
    }

    private void refreshView(VipLimit vipYesLimit, VipLimit vipNoLimit) {
        if (vipYesLimit == null && vipNoLimit == null) {
            llLimit.setVisibility(View.GONE);
            return;
        }
        llLimit.setVisibility(View.VISIBLE);
        if (vipYesLimit != null) {
            // data
            String ad = getString(vipYesLimit.isAdvertiseHide() ? R.string.no_ad : R.string.yes_ad_more_less);
            String wall = String.format(Locale.getDefault(), getString(R.string.holder_paper_single_limit_holder), vipYesLimit.getWallPaperCount(), ConvertUtils.byte2FitSize(vipYesLimit.getWallPaperSize()));
            String total = getString(vipYesLimit.isNoteTotalEnable() ? R.string.open : R.string.refuse);
            String souvenir = String.format(Locale.getDefault(), getString(R.string.can_add_holder), vipYesLimit.getSouvenirCount());
            String audio = String.format(Locale.getDefault(), getString(R.string.single_max_holder), ConvertUtils.byte2FitSize(vipYesLimit.getAudioSize()));
            String video = String.format(Locale.getDefault(), getString(R.string.single_max_holder), ConvertUtils.byte2FitSize(vipYesLimit.getVideoSize()));
            String album = vipYesLimit.isPictureOriginal() ? String.format(Locale.getDefault(), getString(R.string.original_single_limit_holder), ConvertUtils.byte2FitSize(vipYesLimit.getPictureSize())) : getString(R.string.compress_loss_less);
            String diary = (vipYesLimit.getDiaryImageCount() > 0) ? String.format(Locale.getDefault(), getString(R.string.holder_paper_single_limit_holder), vipYesLimit.getDiaryImageCount(), ConvertUtils.byte2FitSize(vipYesLimit.getDiaryImageSize())) : getString(R.string.refuse_image_upload);
            String whisper = vipYesLimit.isWhisperImageEnable() ? getString(R.string.open_image_upload) : getString(R.string.refuse_image_upload);
            String gift = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipYesLimit.getGiftImageCount());
            String food = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipYesLimit.getFoodImageCount());
            String movie = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipYesLimit.getMovieImageCount());
            // view
            tvAdVipYes.setText(ad);
            tvWallVipYes.setText(wall);
            tvTotalVipYes.setText(total);
            tvSouvenirVipYes.setText(souvenir);
            tvAudioVipYes.setText(audio);
            tvVideoVipYes.setText(video);
            tvAlbumVipYes.setText(album);
            tvDiaryVipYes.setText(diary);
            tvWhisperVipYes.setText(whisper);
            tvGiftVipYes.setText(gift);
            tvFoodVipYes.setText(food);
            tvMovieVipYes.setText(movie);
        }
        if (vipNoLimit != null) {
            // data
            String ad = getString(vipNoLimit.isAdvertiseHide() ? R.string.no_ad : R.string.yes_ad_more_less);
            String wall = String.format(Locale.getDefault(), getString(R.string.holder_paper_single_limit_holder), vipNoLimit.getWallPaperCount(), ConvertUtils.byte2FitSize(vipNoLimit.getWallPaperSize()));
            String total = getString(vipNoLimit.isNoteTotalEnable() ? R.string.now_stage_open : R.string.refuse);
            String souvenir = String.format(Locale.getDefault(), getString(R.string.can_add_holder), vipNoLimit.getSouvenirCount());
            String audio = String.format(Locale.getDefault(), getString(R.string.single_max_holder), ConvertUtils.byte2FitSize(vipNoLimit.getAudioSize()));
            String video = String.format(Locale.getDefault(), getString(R.string.single_max_holder), ConvertUtils.byte2FitSize(vipNoLimit.getVideoSize()));
            String album = vipNoLimit.isPictureOriginal() ? String.format(Locale.getDefault(), getString(R.string.original_single_limit_holder), ConvertUtils.byte2FitSize(vipNoLimit.getPictureSize())) : getString(R.string.compress_loss_less);
            String diary = (vipNoLimit.getDiaryImageCount() > 0) ? String.format(Locale.getDefault(), getString(R.string.holder_paper_single_limit_holder), vipNoLimit.getDiaryImageCount(), ConvertUtils.byte2FitSize(vipNoLimit.getDiaryImageSize())) : getString(R.string.refuse_image_upload);
            String whisper = vipNoLimit.isWhisperImageEnable() ? getString(R.string.open_image_upload) : getString(R.string.refuse_image_upload);
            String gift = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipNoLimit.getGiftImageCount());
            String food = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipNoLimit.getFoodImageCount());
            String movie = String.format(Locale.getDefault(), getString(R.string.every_can_upload_holder_paper), vipNoLimit.getMovieImageCount());
            // view
            tvAdVipNo.setText(ad);
            tvWallVipNo.setText(wall);
            tvTotalVipNo.setText(total);
            tvSouvenirVipNo.setText(souvenir);
            tvAudioVipNo.setText(audio);
            tvVideoVipNo.setText(video);
            tvAlbumVipNo.setText(album);
            tvDiaryVipNo.setText(diary);
            tvWhisperVipNo.setText(whisper);
            tvGiftVipNo.setText(gift);
            tvFoodVipNo.setText(food);
            tvMovieVipNo.setText(movie);
        }
    }

}
