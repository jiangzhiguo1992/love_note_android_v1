package com.jiangzg.lovenote.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 帮助文档
 */
public class Help extends BaseObj implements Parcelable {

    public static final int INDEX_ALL = 0;
    // couple
    public static final int INDEX_COUPLE_HOME = 100;
    public static final int INDEX_COUPLE_PAIR = 110;
    public static final int INDEX_COUPLE_INFO = 120;
    // note
    public static final int INDEX_NOTE_HOME = 200;
    public static final int INDEX_NOTE_LOCK = 210;
    public static final int INDEX_NOTE_SOUVENIR = 220;
    public static final int INDEX_NOTE_WORD = 230;
    public static final int INDEX_NOTE_WHISPER = 231;
    public static final int INDEX_NOTE_AWARD = 232;
    public static final int INDEX_NOTE_DREAM = 233;
    public static final int INDEX_NOTE_TRAVEL = 234;
    public static final int INDEX_NOTE_PROMISE = 235;
    // topic
    public static final int INDEX_TOPIC_HOME = 300;
    public static final int INDEX_TOPIC_POST = 310;
    // more TODO
    public static final int INDEX_MORE_HOME = 400;
    public static final int INDEX_MORE_BILL = 410;
    public static final int INDEX_MORE_VIP = 420;
    public static final int INDEX_MORE_COIN = 430;
    public static final int INDEX_MORE_SIGN = 440;
    public static final int INDEX_MORE_MATCH = 440;
    // other TODO
    public static final int INDEX_OTHER = 500;
    public static final int INDEX_USER_SUGGEST = 510;

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
