package com.jiangzg.mianmian.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 帮助文档
 */
public class Help extends BaseObj implements Parcelable {

    public static final int INDEX_ALL = 0;
    // user
    public static final int INDEX_USER_SUGGEST_HOME = 120;
    // couple
    public static final int INDEX_COUPLE_HOME = 200;
    public static final int INDEX_COUPLE_PAIR = 210;
    public static final int INDEX_COUPLE_INFO = 220;
    // note
    public static final int INDEX_NOTE_HOME = 300;
    public static final int INDEX_NOTE_TRENDS_LIST = 310;
    public static final int INDEX_NOTE_TRENDS_TOTAL = 311;
    public static final int INDEX_NOTE_LOCK = 320;
    public static final int INDEX_NOTE_SOUVENIR_LIST = 330;
    public static final int INDEX_NOTE_SOUVENIR_BODY_EDIT = 331;
    public static final int INDEX_NOTE_SOUVENIR_FOREIGN_EDIT = 332;
    public static final int INDEX_NOTE_SOUVENIR_DETAIL_DONE = 333;
    public static final int INDEX_NOTE_SOUVENIR_DETAIL_WISH = 334;
    public static final int INDEX_NOTE_MENSES = 340;
    public static final int INDEX_NOTE_SHY = 350;
    public static final int INDEX_NOTE_SLEEP = 360;
    public static final int INDEX_NOTE_WORD_LIST = 370;
    public static final int INDEX_NOTE_WHISPER_LIST = 380;
    public static final int INDEX_NOTE_DIARY_LIST = 390;
    public static final int INDEX_NOTE_DIARY_EDIT = 391;
    public static final int INDEX_NOTE_DIARY_DETAIL = 392;
    public static final int INDEX_NOTE_ALBUM_LIST = 400;
    public static final int INDEX_NOTE_ALBUM_EDIT = 401;
    public static final int INDEX_NOTE_PICTURE_EDIT = 410;
    public static final int INDEX_NOTE_PICTURE_LIST = 411;
    public static final int INDEX_NOTE_AUDIO_LIST = 420;
    public static final int INDEX_NOTE_AUDIO_EDIT = 421;
    public static final int INDEX_NOTE_VIDEO_LIST = 430;
    public static final int INDEX_NOTE_VIDEO_EDIT = 431;
    public static final int INDEX_NOTE_FOOD_LIST = 440;
    public static final int INDEX_NOTE_FOOD_EDIT = 441;
    public static final int INDEX_NOTE_TRAVEL_LIST = 450;
    public static final int INDEX_NOTE_TRAVEL_DETAIL = 451;
    public static final int INDEX_NOTE_TRAVEL_EDIT = 452;
    public static final int INDEX_NOTE_TRAVEL_PLACE_EDIT = 453;
    public static final int INDEX_NOTE_GIFT_LIST = 460;
    public static final int INDEX_NOTE_GIFT_EDIT = 461;
    public static final int INDEX_NOTE_PROMISE_LIST = 470;
    public static final int INDEX_NOTE_PROMISE_DETAIL = 471;
    public static final int INDEX_NOTE_PROMISE_EDIT = 472;
    public static final int INDEX_NOTE_ANGRY_LIST = 480;
    public static final int INDEX_NOTE_ANGRY_EDIT = 481;
    public static final int INDEX_NOTE_ANGRY_DETAIL = 482;
    public static final int INDEX_NOTE_DREAM_LIST = 490;
    public static final int INDEX_NOTE_DREAM_DETAIL = 491;
    public static final int INDEX_NOTE_DREAM_EDIT = 492;
    public static final int INDEX_NOTE_AWARD_LIST = 500;
    public static final int INDEX_NOTE_AWARD_EDIT = 501;
    public static final int INDEX_NOTE_AWARD_RULE_LIST = 502;
    public static final int INDEX_NOTE_AWARD_RULE_EDIT = 503;
    // topic
    public static final int INDEX_TOPIC_HOME = 600;
    public static final int INDEX_TOPIC_MESSAGE = 610;
    public static final int INDEX_POST_MINE = 620;
    public static final int INDEX_POST_COLLECT = 630;
    public static final int INDEX_POST_LIST = 640;
    public static final int INDEX_POST_ADD = 641;
    public static final int INDEX_POST_DETAIL = 642;
    public static final int INDEX_POST_COMMENT_DETAIL = 643;
    // more
    public static final int INDEX_MORE_HOME = 700;

    private int index;
    private String title;
    private String desc;
    private List<HelpContent> contentList;
    private List<Help> subList;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<HelpContent> getContentList() {
        return contentList;
    }

    public void setContentList(List<HelpContent> contentList) {
        this.contentList = contentList;
    }

    public List<Help> getSubList() {
        return subList;
    }

    public void setSubList(List<Help> subList) {
        this.subList = subList;
    }

    public static class HelpContent {

        private String question;
        private String answer;

        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public HelpContent() {
        }
    }

    public Help() {
    }

    protected Help(Parcel in) {
        super(in);
        index = in.readInt();
        title = in.readString();
        desc = in.readString();
        subList = in.createTypedArrayList(Help.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(index);
        dest.writeString(title);
        dest.writeString(desc);
        dest.writeTypedList(subList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Help> CREATOR = new Creator<Help>() {
        @Override
        public Help createFromParcel(Parcel in) {
            return new Help(in);
        }

        @Override
        public Help[] newArray(int size) {
            return new Help[size];
        }
    };

}
