package com.jiangzg.lovenote.controller.activity.note;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jiangzg.base.component.ActivityTrans;
import com.jiangzg.base.view.ViewUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.base.BaseActivity;
import com.jiangzg.lovenote.helper.ConsHelper;
import com.jiangzg.lovenote.helper.RxBus;
import com.jiangzg.lovenote.helper.SPHelper;
import com.jiangzg.lovenote.helper.ViewHelper;
import com.jiangzg.lovenote.model.engine.NoteCustom;

import butterknife.BindView;
import butterknife.OnClick;

public class NoteCustomActivity extends BaseActivity<NoteCustomActivity> {

    @BindView(R.id.tb)
    Toolbar tb;
    @BindView(R.id.tvSouvenir)
    TextView tvSouvenir;
    @BindView(R.id.ivSouvenir)
    ImageView ivSouvenir;
    @BindView(R.id.cvSouvenir)
    CardView cvSouvenir;
    @BindView(R.id.tvShy)
    TextView tvShy;
    @BindView(R.id.ivShy)
    ImageView ivShy;
    @BindView(R.id.cvShy)
    CardView cvShy;
    @BindView(R.id.tvMenses)
    TextView tvMenses;
    @BindView(R.id.ivMenses)
    ImageView ivMenses;
    @BindView(R.id.cvMenses)
    CardView cvMenses;
    @BindView(R.id.tvSleep)
    TextView tvSleep;
    @BindView(R.id.ivSleep)
    ImageView ivSleep;
    @BindView(R.id.cvSleep)
    CardView cvSleep;
    @BindView(R.id.tvWord)
    TextView tvWord;
    @BindView(R.id.ivWord)
    ImageView ivWord;
    @BindView(R.id.cvWord)
    CardView cvWord;
    @BindView(R.id.tvWhisper)
    TextView tvWhisper;
    @BindView(R.id.ivWhisper)
    ImageView ivWhisper;
    @BindView(R.id.cvWhisper)
    CardView cvWhisper;
    @BindView(R.id.tvDiary)
    TextView tvDiary;
    @BindView(R.id.ivDiary)
    ImageView ivDiary;
    @BindView(R.id.cvDiary)
    CardView cvDiary;
    @BindView(R.id.tvAward)
    TextView tvAward;
    @BindView(R.id.ivAward)
    ImageView ivAward;
    @BindView(R.id.cvAward)
    CardView cvAward;
    @BindView(R.id.tvDream)
    TextView tvDream;
    @BindView(R.id.ivDream)
    ImageView ivDream;
    @BindView(R.id.cvDream)
    CardView cvDream;
    @BindView(R.id.tvMovie)
    TextView tvMovie;
    @BindView(R.id.ivMovie)
    ImageView ivMovie;
    @BindView(R.id.cvMovie)
    CardView cvMovie;
    @BindView(R.id.tvFood)
    TextView tvFood;
    @BindView(R.id.ivFood)
    ImageView ivFood;
    @BindView(R.id.cvFood)
    CardView cvFood;
    @BindView(R.id.tvTravel)
    TextView tvTravel;
    @BindView(R.id.ivTravel)
    ImageView ivTravel;
    @BindView(R.id.cvTravel)
    CardView cvTravel;
    @BindView(R.id.tvAngry)
    TextView tvAngry;
    @BindView(R.id.ivAngry)
    ImageView ivAngry;
    @BindView(R.id.cvAngry)
    CardView cvAngry;
    @BindView(R.id.tvGift)
    TextView tvGift;
    @BindView(R.id.ivGift)
    ImageView ivGift;
    @BindView(R.id.cvGift)
    CardView cvGift;
    @BindView(R.id.tvPromise)
    TextView tvPromise;
    @BindView(R.id.ivPromise)
    ImageView ivPromise;
    @BindView(R.id.cvPromise)
    CardView cvPromise;
    @BindView(R.id.tvAudio)
    TextView tvAudio;
    @BindView(R.id.ivAudio)
    ImageView ivAudio;
    @BindView(R.id.cvAudio)
    CardView cvAudio;
    @BindView(R.id.tvVideo)
    TextView tvVideo;
    @BindView(R.id.ivVideo)
    ImageView ivVideo;
    @BindView(R.id.cvVideo)
    CardView cvVideo;
    @BindView(R.id.tvAlbum)
    TextView tvAlbum;
    @BindView(R.id.ivAlbum)
    ImageView ivAlbum;
    @BindView(R.id.cvAlbum)
    CardView cvAlbum;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    @BindView(R.id.ivTotal)
    ImageView ivTotal;
    @BindView(R.id.cvTotal)
    CardView cvTotal;
    @BindView(R.id.tvTrends)
    TextView tvTrends;
    @BindView(R.id.ivTrends)
    ImageView ivTrends;
    @BindView(R.id.cvTrends)
    CardView cvTrends;

    private NoteCustom custom;
    private int colorPrimary;
    private int colorGrey;
    private ColorStateList colorPrimaryStateList;
    private ColorStateList colorGreyStateList;

    public static void goActivity(Fragment from) {
        Intent intent = new Intent(from.getActivity(), NoteCustomActivity.class);
        // intent.putExtra();
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        ActivityTrans.start(from, intent);
    }

    @Override
    protected int getView(Intent intent) {
        return R.layout.activity_note_custom;
    }

    @Override
    protected void initView(Intent intent, Bundle state) {
        ViewHelper.initTopBar(mActivity, tb, getString(R.string.func_custom), true);
        // custom
        custom = SPHelper.getNoteCustom();
        colorPrimary = ContextCompat.getColor(mActivity, ViewUtils.getColorPrimary(mActivity));
        colorGrey = ContextCompat.getColor(mActivity, R.color.font_grey);
        colorPrimaryStateList = ColorStateList.valueOf(colorPrimary);
        colorGreyStateList = ColorStateList.valueOf(colorGrey);
        updateView();
    }

    @Override
    protected void initData(Intent intent, Bundle state) {
    }

    @Override
    protected void onFinish(Bundle state) {
    }

    @OnClick({R.id.cvSouvenir, R.id.cvShy, R.id.cvMenses, R.id.cvSleep,
            R.id.cvWord, R.id.cvWhisper, R.id.cvDiary, R.id.cvAward, R.id.cvDream, R.id.cvMovie,
            R.id.cvFood, R.id.cvTravel, R.id.cvAngry, R.id.cvGift, R.id.cvPromise,
            R.id.cvAudio, R.id.cvVideo, R.id.cvAlbum, R.id.cvTotal, R.id.cvTrends})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cvSouvenir:
                custom.setSouvenir(!custom.isSouvenir());
                break;
            case R.id.cvShy:
                custom.setShy(!custom.isShy());
                break;
            case R.id.cvMenses:
                custom.setMenses(!custom.isMenses());
                break;
            case R.id.cvSleep:
                custom.setSleep(!custom.isSleep());
                break;
            case R.id.cvWord:
                custom.setWord(!custom.isWord());
                break;
            case R.id.cvWhisper:
                custom.setWhisper(!custom.isWhisper());
                break;
            case R.id.cvDiary:
                custom.setDiary(!custom.isDiary());
                break;
            case R.id.cvAward:
                custom.setAward(!custom.isAward());
                break;
            case R.id.cvDream:
                custom.setDream(!custom.isDream());
                break;
            case R.id.cvMovie:
                custom.setMovie(!custom.isMovie());
                break;
            case R.id.cvFood:
                custom.setFood(!custom.isFood());
                break;
            case R.id.cvTravel:
                custom.setTravel(!custom.isTravel());
                break;
            case R.id.cvAngry:
                custom.setAngry(!custom.isAngry());
                break;
            case R.id.cvGift:
                custom.setGift(!custom.isGift());
                break;
            case R.id.cvPromise:
                custom.setPromise(!custom.isPromise());
                break;
            case R.id.cvAudio:
                custom.setAudio(!custom.isAudio());
                break;
            case R.id.cvVideo:
                custom.setVideo(!custom.isVideo());
                break;
            case R.id.cvAlbum:
                custom.setAlbum(!custom.isAlbum());
                break;
            case R.id.cvTotal:
                custom.setTotal(!custom.isTotal());
                break;
            case R.id.cvTrends:
                custom.setTrends(!custom.isTrends());
                break;
        }
        SPHelper.setNoteCustom(custom);
        updateView();
        // event
        RxBus.post(new RxBus.Event<>(ConsHelper.EVENT_CUSTOM_REFRESH, custom));
    }

    private void updateView() {
        // textView
        tvSouvenir.setTextColor(custom.isSouvenir() ? colorPrimary : colorGrey);
        tvShy.setTextColor(custom.isShy() ? colorPrimary : colorGrey);
        tvMenses.setTextColor(custom.isMenses() ? colorPrimary : colorGrey);
        tvSleep.setTextColor(custom.isSleep() ? colorPrimary : colorGrey);
        tvWord.setTextColor(custom.isWord() ? colorPrimary : colorGrey);
        tvWhisper.setTextColor(custom.isWhisper() ? colorPrimary : colorGrey);
        tvDiary.setTextColor(custom.isDiary() ? colorPrimary : colorGrey);
        tvAward.setTextColor(custom.isAward() ? colorPrimary : colorGrey);
        tvDream.setTextColor(custom.isDream() ? colorPrimary : colorGrey);
        tvMovie.setTextColor(custom.isMovie() ? colorPrimary : colorGrey);
        tvFood.setTextColor(custom.isFood() ? colorPrimary : colorGrey);
        tvTravel.setTextColor(custom.isTravel() ? colorPrimary : colorGrey);
        tvAngry.setTextColor(custom.isAngry() ? colorPrimary : colorGrey);
        tvGift.setTextColor(custom.isGift() ? colorPrimary : colorGrey);
        tvPromise.setTextColor(custom.isPromise() ? colorPrimary : colorGrey);
        tvAudio.setTextColor(custom.isAudio() ? colorPrimary : colorGrey);
        tvVideo.setTextColor(custom.isVideo() ? colorPrimary : colorGrey);
        tvAlbum.setTextColor(custom.isAlbum() ? colorPrimary : colorGrey);
        tvTotal.setTextColor(custom.isTotal() ? colorPrimary : colorGrey);
        tvTrends.setTextColor(custom.isTrends() ? colorPrimary : colorGrey);
        // imageView
        ivSouvenir.setImageResource(custom.isSouvenir() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivSouvenir.setImageTintList(custom.isSouvenir() ? colorPrimaryStateList : colorGreyStateList);
        ivShy.setImageResource(custom.isShy() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivShy.setImageTintList(custom.isShy() ? colorPrimaryStateList : colorGreyStateList);
        ivMenses.setImageResource(custom.isMenses() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivMenses.setImageTintList(custom.isMenses() ? colorPrimaryStateList : colorGreyStateList);
        ivSleep.setImageResource(custom.isSleep() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivSleep.setImageTintList(custom.isSleep() ? colorPrimaryStateList : colorGreyStateList);
        ivWord.setImageResource(custom.isWord() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivWord.setImageTintList(custom.isWord() ? colorPrimaryStateList : colorGreyStateList);
        ivWhisper.setImageResource(custom.isWhisper() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivWhisper.setImageTintList(custom.isWhisper() ? colorPrimaryStateList : colorGreyStateList);
        ivDiary.setImageResource(custom.isDiary() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivDiary.setImageTintList(custom.isDiary() ? colorPrimaryStateList : colorGreyStateList);
        ivAward.setImageResource(custom.isAward() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivAward.setImageTintList(custom.isAward() ? colorPrimaryStateList : colorGreyStateList);
        ivDream.setImageResource(custom.isDream() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivDream.setImageTintList(custom.isDream() ? colorPrimaryStateList : colorGreyStateList);
        ivMovie.setImageResource(custom.isMovie() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivMovie.setImageTintList(custom.isMovie() ? colorPrimaryStateList : colorGreyStateList);
        ivFood.setImageResource(custom.isFood() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivFood.setImageTintList(custom.isFood() ? colorPrimaryStateList : colorGreyStateList);
        ivTravel.setImageResource(custom.isTravel() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivTravel.setImageTintList(custom.isTravel() ? colorPrimaryStateList : colorGreyStateList);
        ivAngry.setImageResource(custom.isAngry() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivAngry.setImageTintList(custom.isAngry() ? colorPrimaryStateList : colorGreyStateList);
        ivGift.setImageResource(custom.isGift() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivGift.setImageTintList(custom.isGift() ? colorPrimaryStateList : colorGreyStateList);
        ivPromise.setImageResource(custom.isPromise() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivPromise.setImageTintList(custom.isPromise() ? colorPrimaryStateList : colorGreyStateList);
        ivAudio.setImageResource(custom.isAudio() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivAudio.setImageTintList(custom.isAudio() ? colorPrimaryStateList : colorGreyStateList);
        ivVideo.setImageResource(custom.isVideo() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivVideo.setImageTintList(custom.isVideo() ? colorPrimaryStateList : colorGreyStateList);
        ivAlbum.setImageResource(custom.isAlbum() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivAlbum.setImageTintList(custom.isAlbum() ? colorPrimaryStateList : colorGreyStateList);
        ivTotal.setImageResource(custom.isTotal() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivTotal.setImageTintList(custom.isTotal() ? colorPrimaryStateList : colorGreyStateList);
        ivTrends.setImageResource(custom.isTrends() ? R.mipmap.ic_check_circle_grey_24dp : R.mipmap.ic_brightness_1_grey_24dp);
        ivTrends.setImageTintList(custom.isTrends() ? colorPrimaryStateList : colorGreyStateList);
    }
}
