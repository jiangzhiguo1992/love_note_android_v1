package com.jiangzg.ita.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by JZG on 2018/3/12.
 * 帮助文档
 */

public class Help extends BaseObj implements Parcelable {

    public static final int TYPE_ALL = 0;
    public static final int TYPE_USER_INFO_SET = 1;
    public static final int TYPE_COUPLE_PAIR = 2;
    public static final int TYPE_COUPLE_HOME = 3;
    public static final int TYPE_SUGGEST_HOME = 4;
    public static final int TYPE_SUGGEST_DETAIL = 5;
    public static final int TYPE_SUGGEST_ADD = 6;
    public static final int TYPE_SUGGEST_MINE = 7;
    public static final int TYPE_SUGGEST_FOLLOW = 8;
    public static final int TYPE_WALL_PAPER_ADD = 9;
    public static final int TYPE_COUPLE_INFO = 10;

    private int contentType;
    private String title;
    private String desc;
    private List<Content> contentList;
    private List<Help> subList;

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
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

    public List<Content> getContentList() {
        return contentList;
    }

    public void setContentList(List<Content> contentList) {
        this.contentList = contentList;
    }

    public List<Help> getSubList() {
        return subList;
    }

    public void setSubList(List<Help> subList) {
        this.subList = subList;
    }

    public static class Content implements Parcelable {
        private String question;
        private String answer;

        public Content(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }

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

        public Content() {
        }

        protected Content(Parcel in) {
            question = in.readString();
            answer = in.readString();
        }

        public static final Creator<Content> CREATOR = new Creator<Content>() {
            @Override
            public Content createFromParcel(Parcel in) {
                return new Content(in);
            }

            @Override
            public Content[] newArray(int size) {
                return new Content[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(question);
            dest.writeString(answer);
        }
    }

    public Help() {
    }

    protected Help(Parcel in) {
        super(in);
        contentType = in.readInt();
        title = in.readString();
        desc = in.readString();
        subList = in.createTypedArrayList(Help.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(contentType);
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
