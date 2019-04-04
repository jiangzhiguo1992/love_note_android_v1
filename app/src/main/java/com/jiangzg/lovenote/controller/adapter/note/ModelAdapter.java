package com.jiangzg.lovenote.controller.adapter.note;

import android.support.v4.app.Fragment;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.controller.activity.note.AlbumListActivity;
import com.jiangzg.lovenote.controller.activity.note.AngryListActivity;
import com.jiangzg.lovenote.controller.activity.note.AudioListActivity;
import com.jiangzg.lovenote.controller.activity.note.AwardListActivity;
import com.jiangzg.lovenote.controller.activity.note.DiaryListActivity;
import com.jiangzg.lovenote.controller.activity.note.DreamListActivity;
import com.jiangzg.lovenote.controller.activity.note.FoodListActivity;
import com.jiangzg.lovenote.controller.activity.note.GiftListActivity;
import com.jiangzg.lovenote.controller.activity.note.MensesActivity;
import com.jiangzg.lovenote.controller.activity.note.MovieListActivity;
import com.jiangzg.lovenote.controller.activity.note.NoteCustomActivity;
import com.jiangzg.lovenote.controller.activity.note.NoteTotalActivity;
import com.jiangzg.lovenote.controller.activity.note.PromiseListActivity;
import com.jiangzg.lovenote.controller.activity.note.ShyActivity;
import com.jiangzg.lovenote.controller.activity.note.SleepActivity;
import com.jiangzg.lovenote.controller.activity.note.TravelListActivity;
import com.jiangzg.lovenote.controller.activity.note.TrendsListActivity;
import com.jiangzg.lovenote.controller.activity.note.VideoListActivity;
import com.jiangzg.lovenote.controller.activity.note.WhisperListActivity;
import com.jiangzg.lovenote.controller.activity.note.WordListActivity;
import com.jiangzg.lovenote.main.MyApp;

/**
 * Created by JZG on 2018/3/12.
 * 小本本模块适配器
 */
public class ModelAdapter extends BaseQuickAdapter<Integer, BaseViewHolder> {

    private final Fragment mFragment;

    //public static final int SOUVENIR = 1;
    public static final int SHY = 2;
    public static final int MENSES = 3;
    public static final int SLEEP = 4;
    public static final int WORD = 5;
    public static final int WHISPER = 6;
    public static final int DIARY = 7;
    public static final int AWARD = 8;
    public static final int DREAM = 9;
    public static final int MOVIE = 10;
    public static final int FOOD = 11;
    public static final int TRAVEL = 12;
    public static final int ANGRY = 13;
    public static final int GIFT = 14;
    public static final int PROMISE = 15;
    public static final int AUDIO = 16;
    public static final int VIDEO = 17;
    public static final int ALBUM = 18;
    public static final int TOTAL = 19;
    public static final int TRENDS = 20;
    public static final int CUSTOM = 21;

    public ModelAdapter(Fragment fragment) {
        super(R.layout.list_item_note_model);
        mFragment = fragment;
    }

    @Override
    protected void convert(final BaseViewHolder helper, Integer item) {
        switch (item) {
            case SHY:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_shy_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.shy));
                break;
            case MENSES:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_menses_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.menses));
                break;
            case SLEEP:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_sleep_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.sleep));
                break;
            case WORD:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_word_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.word));
                break;
            case WHISPER:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_whisper_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.whisper));
                break;
            case DIARY:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_diary_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.diary));
                break;
            case AWARD:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_award_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.award_rule));
                break;
            case DREAM:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_dream_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.dream));
                break;
            case MOVIE:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_movie_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.movie));
                break;
            case FOOD:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_food_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.food));
                break;
            case TRAVEL:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_travel_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.travel));
                break;
            case ANGRY:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_angry_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.angry));
                break;
            case GIFT:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_gift_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.gift));
                break;
            case PROMISE:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_promise_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.promise));
                break;
            case AUDIO:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_audio_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.audio));
                break;
            case VIDEO:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_video_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.video));
                break;
            case ALBUM:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_album_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.album));
                break;
            case TOTAL:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_total_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.statistics));
                break;
            case TRENDS:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_trends_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.trends));
                break;
            case CUSTOM:
                helper.setImageResource(R.id.ivItem, R.mipmap.ic_note_custom_24dp);
                helper.setText(R.id.tvItem, MyApp.get().getString(R.string.func_custom));
                break;
        }
    }

    public void goActivity(int position) {
        Integer item = getItem(position);
        switch (item) {
            case SHY:
                ShyActivity.goActivity(mFragment);
                break;
            case MENSES:
                MensesActivity.goActivity(mFragment);
                break;
            case SLEEP:
                SleepActivity.goActivity(mFragment);
                break;
            case WORD:
                WordListActivity.goActivity(mFragment);
                break;
            case WHISPER:
                WhisperListActivity.goActivity(mFragment);
                break;
            case DIARY:
                DiaryListActivity.goActivity(mFragment);
                break;
            case AWARD:
                AwardListActivity.goActivity(mFragment);
                break;
            case DREAM:
                DreamListActivity.goActivity(mFragment);
                break;
            case MOVIE:
                MovieListActivity.goActivity(mFragment);
                break;
            case FOOD:
                FoodListActivity.goActivity(mFragment);
                break;
            case TRAVEL:
                TravelListActivity.goActivity(mFragment);
                break;
            case ANGRY:
                AngryListActivity.goActivity(mFragment);
                break;
            case GIFT:
                GiftListActivity.goActivity(mFragment);
                break;
            case PROMISE:
                PromiseListActivity.goActivity(mFragment);
                break;
            case AUDIO:
                AudioListActivity.goActivity(mFragment);
                break;
            case VIDEO:
                VideoListActivity.goActivity(mFragment);
                break;
            case ALBUM:
                AlbumListActivity.goActivity(mFragment);
                break;
            case TOTAL:
                NoteTotalActivity.goActivity(mFragment);
                break;
            case TRENDS:
                TrendsListActivity.goActivity(mFragment);
                break;
            case CUSTOM:
                NoteCustomActivity.goActivity(mFragment);
                break;
        }
    }

}
