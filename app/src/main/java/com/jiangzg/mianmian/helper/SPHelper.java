package com.jiangzg.mianmian.helper;

import android.content.SharedPreferences;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.SPUtils;
import com.jiangzg.mianmian.domain.CommonConst;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Limit;
import com.jiangzg.mianmian.domain.OssInfo;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.domain.Version;
import com.jiangzg.mianmian.domain.VipLimit;
import com.jiangzg.mianmian.domain.WallPaper;

/**
 * Created by Fan on 2017/3/2.
 * Preferences 帮助类
 */
public class SPHelper {

    private static final String LOG_TAG = "SPHelper";

    private static final String SHARE_COMMON = "common";
    private static final String SHARE_USER = "shareUser";
    private static final String SHARE_COUPLE = "shareCouple";
    private static final String SHARE_TA = "shareTa";
    private static final String SHARE_VERSION = "shareVersion";
    private static final String SHARE_WALL_PAPER = "shareWallPaper";
    private static final String SHARE_LIMIT = "shareLimit";
    private static final String SHARE_OSS_INFO = "shareOssInfo";
    private static final String SHARE_COMMON_CONST = "shareCommonConst";
    private static final String SHARE_VIP_LIMIT = "shareVipLimit";
    private static final String SHARE_DIARY = "shareDiary";

    // common
    private static final String FIELD_COMMON_THEME = "theme";
    private static final String FIELD_COMMON_NOTICE_SYSTEM = "noticeSystem";
    private static final String FIELD_COMMON_NOTICE_SOCIAL = "noticeSocial";
    private static final String FIELD_COMMON_NOTICE_NO_READ_COUNT = "noticeNoReadCount";
    // user
    private static final String FIELD_USER_ID = "id";
    private static final String FIELD_USER_PHONE = "phone";
    private static final String FIELD_USER_SEX = "sex";
    private static final String FIELD_USER_BIRTHDAY = "birthday";
    private static final String FIELD_USER_TOKEN = "userToken";
    // couple
    private static final String FIELD_CP_ID = "id";
    private static final String FIELD_CP_CREATE_AT = "createAt";
    private static final String FIELD_CP_UPDATE_AT = "updateAt";
    private static final String FIELD_CP_CREATOR_ID = "creatorId";
    private static final String FIELD_CP_CREATOR_NAME = "creatorName";
    private static final String FIELD_CP_CREATOR_AVATAR = "creatorAvatar";
    private static final String FIELD_CP_INVITEE_ID = "inviteeId";
    private static final String FIELD_CP_INVITEE_NAME = "inviteeName";
    private static final String FIELD_CP_INVITEE_AVATAR = "inviteeAvatar";
    private static final String FIELD_CP_STATE_ID = "stateId";
    private static final String FIELD_CP_STATE_CREATE_AT = "stateCreateAt";
    private static final String FIELD_CP_STATE_UPDATE_AT = "stateUpdateAt";
    private static final String FIELD_CP_STATE_USER_ID = "stateUserId";
    private static final String FIELD_CP_STATE_STATE = "stateState";
    // version
    private static final String FIELD_VERSION_CREATE_AT = "createAt";
    private static final String FIELD_VERSION_VERSION_CODE = "versionCode";
    private static final String FIELD_VERSION_VERSION_NAME = "versionName";
    private static final String FIELD_VERSION_UPDATE_LOG = "updateLog";
    private static final String FIELD_VERSION_UPDATE_URL = "updateUrl";
    // ossInfo
    private static final String FIELD_OSS_SECURITY_TOKEN = "securityToken";
    private static final String FIELD_OSS_KEY_ID = "accessKeyId";
    private static final String FIELD_OSS_KEY_SECRET = "accessKeySecret";
    //private static final String FIELD_OSS_REGION = "region";
    //private static final String FIELD_OSS_ENDPOINT = "endpoint";
    private static final String FIELD_OSS_DOMAIN = "domain";
    private static final String FIELD_OSS_BUCKET = "bucket";
    private static final String FIELD_OSS_EXPIRE_TIME = "expireTime";
    private static final String FIELD_OSS_INTERVAL_SEC = "intervalSec";
    private static final String FIELD_OSS_PATH_SUGGEST = "pathSuggest";
    private static final String FIELD_OSS_PATH_COUPLE_AVATAR = "pathCoupleAvatar";
    private static final String FIELD_OSS_PATH_COUPLE_WALL = "pathCoupleWall";
    private static final String FIELD_OSS_PATH_BOOK_WHISPER = "pathBookWhisper";
    private static final String FIELD_OSS_PATH_BOOK_DIARY = "pathBookDiary";
    private static final String FIELD_OSS_PATH_BOOK_ALBUM = "pathBookAlbum";
    private static final String FIELD_OSS_PATH_BOOK_PICTURE = "pathBookPicture";
    private static final String FIELD_OSS_PATH_BOOK_GIFT = "pathBookGift";
    private static final String FIELD_OSS_PATH_BOOK_AUDIO = "pathBookAudio";
    private static final String FIELD_OSS_PATH_BOOK_VIDEO = "pathBookVideo";
    private static final String FIELD_OSS_PATH_BOOK_THUMB = "pathBookThumb";
    // limit
    private static final String FIELD_LIMIT_SMS_CODE_LENGTH = "smsCodeLength";
    private static final String FIELD_LIMIT_SMS_EFFECT_SEC = "smsEffectSec";
    private static final String FIELD_LIMIT_SMS_BETWEEN = "smsBetween";
    private static final String FIELD_LIMIT_SMS_MAX_TIME = "smsMaxTime";
    private static final String FIELD_LIMIT_SMS_MAX_COUNT = "smsMaxCount";
    private static final String FIELD_LIMIT_SUGGEST_TITLE_LENGTH = "suggestTitleLength";
    private static final String FIELD_LIMIT_SUGGEST_CONTENT_TEXT_LENGTH = "suggestContentTextLength";
    private static final String FIELD_LIMIT_SUGGEST_COMMENT_CONTENT_LENGTH = "suggestCommentContentLength";
    private static final String FIELD_LIMIT_COUPLE_BREAK_SEC = "coupleBreakSec";
    private static final String FIELD_LIMIT_COUPLE_NAME_LENGTH = "coupleNameLength";
    private static final String FIELD_LIMIT_BOOK_OSS_EXPIRE_SECONDS = "bookOssExpireSeconds";
    private static final String FIELD_LIMIT_SOUVENIR_TITLE_LENGTH = "souvenirTitleLength";
    private static final String FIELD_LIMIT_WHISPER_CONTENT_LENGTH = "whisperContentLength";
    private static final String FIELD_LIMIT_WHISPER_CHANNEL_LENGTH = "whisperChannelLength";
    private static final String FIELD_LIMIT_WORD_CONTENT_LENGTH = "wordContentLength";
    private static final String FIELD_LIMIT_DIARY_CONTENT_LENGTH = "diaryContentLength";
    private static final String FIELD_LIMIT_ALBUM_TITLE_LENGTH = "albumTitleLength";
    private static final String FIELD_LIMIT_PICTURE_COUNT = "pictureCount";
    private static final String FIELD_LIMIT_MEET_TITLE_LENGTH = "meetTitleLength";
    private static final String FIELD_LIMIT_MEET_PROCESS_LENGTH = "meetProcessLength";
    private static final String FIELD_LIMIT_PROMISE_CONTENT_LENGTH = "promiseContentLength";
    private static final String FIELD_LIMIT_AUDIO_TITLE_LENGTH = "audioTitleLength";
    private static final String FIELD_LIMIT_VIDEO_TITLE_LENGTH = "videoTitleLength";
    private static final String FIELD_LIMIT_GIFT_NAME_LENGTH = "giftNameLength";
    private static final String FIELD_LIMIT_DREAM_CONTENT_LENGTH = "dreamContentLength";
    private static final String FIELD_LIMIT_ANGRY_REASON_LENGTH = "angryReasonLength";
    private static final String FIELD_LIMIT_AWARD_REASON_LENGTH = "awardReasonLength";
    private static final String FIELD_LIMIT_VIP_EXPIRE_DAYS = "vipExpireDays";
    // vipLimit
    private static final String FIELD_VIP_LIMIT_VIP_EXPIRE_AT = "vipExpireAt";
    private static final String FIELD_VIP_LIMIT_WALL_PAPER_COUNT = "wallPaperCount";
    private static final String FIELD_VIP_LIMIT_WHISPER_IMG_COUNT = "whisperImageCount";
    private static final String FIELD_VIP_LIMIT_DIARY_IMG_SIZE = "diaryImageSize";
    private static final String FIELD_VIP_LIMIT_DIARY_IMG_COUNT = "diaryImageCount";
    private static final String FIELD_VIP_LIMIT_ALBUM_TOTAL_COUNT = "albumTotalCount";
    private static final String FIELD_VIP_LIMIT_PICTURE_SIZE = "pictureSize";
    private static final String FIELD_VIP_LIMIT_PICTURE_TOTAL_COUNT = "pictureTotalCount";
    private static final String FIELD_VIP_LIMIT_MEET_IMG_COUNT = "meetImageCount";
    private static final String FIELD_VIP_LIMIT_GIFT_IMG_COUNT = "giftImageCount";
    private static final String FIELD_VIP_LIMIT_AUDIO_SIZE = "audioSize";
    private static final String FIELD_VIP_LIMIT_AUDIO_TOTAL_SIZE = "audioTotalSize";
    private static final String FIELD_VIP_LIMIT_VIDEO_SIZE = "videoSize";
    private static final String FIELD_VIP_LIMIT_VIDEO_TOTAL_SIZE = "videoTotalSize";
    private static final String FIELD_VIP_LIMIT_TOPIC_IMG = "topicImageEnable";
    // commonConst
    private static final String FIELD_COMMON_CONST_COMPANY_NAME = "companyName";
    private static final String FIELD_COMMON_CONST_USER_PROTOCOL_URL = "userProtocolUrl";
    private static final String FIELD_COMMON_CONST_ABOUT_US_URL = "aboutUsUrl";
    // wallPaper
    private static final String FIELD_WALL_PAPER_JSON = "json";
    // diary
    private static final String FIELD_DIARY_HAPPEN = "happen";
    private static final String FIELD_DIARY_CONTENT = "content";

    public static void setUser(User user) {
        clearUser();
        clearCouple();
        if (user == null) {
            LogUtils.w(LOG_TAG, "user == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setUser: " + GsonHelper.get().toJson(user));
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_USER).edit();
        editor.putLong(FIELD_USER_ID, user.getId());
        editor.putString(FIELD_USER_PHONE, user.getPhone());
        editor.putInt(FIELD_USER_SEX, user.getSex());
        editor.putLong(FIELD_USER_BIRTHDAY, user.getBirthday());
        editor.putString(FIELD_USER_TOKEN, user.getUserToken());
        editor.apply();
        // 存cp
        Couple couple = user.getCouple();
        setCouple(couple);
    }

    public static User getUser() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_USER);
        User user = new User();
        user.setId(sp.getLong(FIELD_USER_ID, 0));
        user.setPhone(sp.getString(FIELD_USER_PHONE, ""));
        user.setSex(sp.getInt(FIELD_USER_SEX, 0));
        user.setBirthday(sp.getLong(FIELD_USER_BIRTHDAY, 0));
        user.setUserToken(sp.getString(FIELD_USER_TOKEN, ""));
        // 取cp
        Couple couple = getCouple();
        user.setCouple(couple);
        return user;
    }

    public static void setCouple(Couple couple) {
        clearCouple();
        if (Couple.isEmpty(couple)) {
            LogUtils.w(LOG_TAG, "couple == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setCouple: " + GsonHelper.get().toJson(couple));
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COUPLE).edit();
        editor.putLong(FIELD_CP_ID, couple.getId());
        editor.putLong(FIELD_CP_CREATE_AT, couple.getCreateAt());
        editor.putLong(FIELD_CP_UPDATE_AT, couple.getUpdateAt());
        editor.putLong(FIELD_CP_CREATOR_ID, couple.getCreatorId());
        editor.putString(FIELD_CP_CREATOR_NAME, couple.getCreatorName());
        editor.putString(FIELD_CP_CREATOR_AVATAR, couple.getCreatorAvatar());
        editor.putLong(FIELD_CP_INVITEE_ID, couple.getInviteeId());
        editor.putString(FIELD_CP_INVITEE_NAME, couple.getInviteeName());
        editor.putString(FIELD_CP_INVITEE_AVATAR, couple.getInviteeAvatar());
        Couple.State state = couple.getState();
        if (state != null) {
            editor.putLong(FIELD_CP_STATE_ID, state.getId());
            editor.putLong(FIELD_CP_STATE_CREATE_AT, state.getCreateAt());
            editor.putLong(FIELD_CP_STATE_UPDATE_AT, state.getUpdateAt());
            editor.putLong(FIELD_CP_STATE_USER_ID, state.getUserId());
            editor.putInt(FIELD_CP_STATE_STATE, state.getState());
        }
        editor.apply();
    }

    public static Couple getCouple() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COUPLE);
        Couple couple = new Couple();
        couple.setId(sp.getLong(FIELD_CP_ID, 0));
        couple.setCreateAt(sp.getLong(FIELD_CP_CREATE_AT, 0));
        couple.setUpdateAt(sp.getLong(FIELD_CP_UPDATE_AT, 0));
        couple.setCreatorId(sp.getLong(FIELD_CP_CREATOR_ID, 0));
        couple.setCreatorName(sp.getString(FIELD_CP_CREATOR_NAME, ""));
        couple.setCreatorAvatar(sp.getString(FIELD_CP_CREATOR_AVATAR, ""));
        couple.setInviteeId(sp.getLong(FIELD_CP_INVITEE_ID, 0));
        couple.setInviteeName(sp.getString(FIELD_CP_INVITEE_NAME, ""));
        couple.setInviteeAvatar(sp.getString(FIELD_CP_INVITEE_AVATAR, ""));
        Couple.State state = new Couple.State();
        state.setId(sp.getLong(FIELD_CP_STATE_ID, 0));
        state.setCreateAt(sp.getLong(FIELD_CP_STATE_CREATE_AT, 0));
        state.setUpdateAt(sp.getLong(FIELD_CP_STATE_UPDATE_AT, 0));
        state.setUserId(sp.getLong(FIELD_CP_STATE_USER_ID, 0));
        state.setState(sp.getInt(FIELD_CP_STATE_STATE, 0));
        couple.setState(state);
        return couple;
    }

    public static void setTa(User ta) {
        clearTa();
        if (ta == null) {
            LogUtils.w(LOG_TAG, "ta == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setTa: " + GsonHelper.get().toJson(ta));
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_TA).edit();
        editor.putLong(FIELD_USER_ID, ta.getId());
        editor.putString(FIELD_USER_PHONE, ta.getPhone());
        editor.putInt(FIELD_USER_SEX, ta.getSex());
        editor.putLong(FIELD_USER_BIRTHDAY, ta.getBirthday());
        editor.apply();
    }

    public static User getTa() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_TA);
        User user = new User();
        user.setId(sp.getLong(FIELD_USER_ID, 0));
        user.setPhone(sp.getString(FIELD_USER_PHONE, ""));
        user.setSex(sp.getInt(FIELD_USER_SEX, 0));
        user.setBirthday(sp.getLong(FIELD_USER_BIRTHDAY, 0));
        if (user.getId() == 0 || StringUtils.isEmpty(user.getPhone())) {
            return null;
        }
        return user;
    }

    public static void setVersion(Version version) {
        clearVersion();
        if (version == null) {
            LogUtils.w(LOG_TAG, "version == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setVersion: " + GsonHelper.get().toJson(version));
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_VERSION).edit();
        editor.putLong(FIELD_VERSION_CREATE_AT, version.getCreateAt());
        editor.putInt(FIELD_VERSION_VERSION_CODE, version.getVersionCode());
        editor.putString(FIELD_VERSION_VERSION_NAME, version.getVersionName());
        editor.putString(FIELD_VERSION_UPDATE_LOG, version.getUpdateLog());
        editor.putString(FIELD_VERSION_UPDATE_URL, version.getUpdateUrl());
        editor.apply();
    }

    public static Version getVersion() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_VERSION);
        Version version = new Version();
        version.setCreateAt(sp.getLong(FIELD_VERSION_CREATE_AT, 0));
        version.setVersionCode(sp.getInt(FIELD_VERSION_VERSION_CODE, 0));
        version.setVersionName(sp.getString(FIELD_VERSION_VERSION_NAME, ""));
        version.setUpdateLog(sp.getString(FIELD_VERSION_UPDATE_LOG, ""));
        version.setUpdateUrl(sp.getString(FIELD_VERSION_UPDATE_URL, ""));
        if (version.getVersionCode() <= 0 || StringUtils.isEmpty(version.getUpdateUrl())) {
            return null;
        }
        return version;
    }

    public static void setWallPaper(WallPaper wallPaper) {
        clearWallPaper();
        if (wallPaper == null) {
            LogUtils.w(LOG_TAG, "wallPaper == null");
            return;
        }
        String json = GsonHelper.get().toJson(wallPaper);
        LogUtils.d(LOG_TAG, "setWallPaper: " + json);

        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_WALL_PAPER).edit();
        editor.putString(FIELD_WALL_PAPER_JSON, json);
        editor.apply();
    }

    public static WallPaper getWallPaper() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_WALL_PAPER);
        WallPaper wallPaper = new WallPaper();
        String json = GsonHelper.get().toJson(wallPaper);
        // json解析
        String newJson = sp.getString(FIELD_WALL_PAPER_JSON, json);
        wallPaper = GsonHelper.get().fromJson(newJson, WallPaper.class);
        return wallPaper;
    }

    public static void setLimit(Limit limit) {
        if (limit == null) {
            LogUtils.w(LOG_TAG, "setLimit == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_LIMIT).edit();
        editor.putInt(FIELD_LIMIT_SMS_CODE_LENGTH, limit.getSmsCodeLength());
        editor.putInt(FIELD_LIMIT_SMS_EFFECT_SEC, limit.getSmsEffectSec());
        editor.putInt(FIELD_LIMIT_SMS_BETWEEN, limit.getSmsBetweenSec());
        editor.putInt(FIELD_LIMIT_SMS_MAX_TIME, limit.getSmsMaxSec());
        editor.putInt(FIELD_LIMIT_SMS_MAX_COUNT, limit.getSmsMaxCount());
        editor.putInt(FIELD_LIMIT_SUGGEST_TITLE_LENGTH, limit.getSuggestTitleLength());
        editor.putInt(FIELD_LIMIT_SUGGEST_CONTENT_TEXT_LENGTH, limit.getSuggestContentTextLength());
        editor.putInt(FIELD_LIMIT_SUGGEST_COMMENT_CONTENT_LENGTH, limit.getSuggestCommentContentLength());
        editor.putLong(FIELD_LIMIT_COUPLE_BREAK_SEC, limit.getCoupleBreakSec());
        editor.putInt(FIELD_LIMIT_COUPLE_NAME_LENGTH, limit.getCoupleNameLength());
        editor.putLong(FIELD_LIMIT_BOOK_OSS_EXPIRE_SECONDS, limit.getBookResExpireSec());
        editor.putInt(FIELD_LIMIT_SOUVENIR_TITLE_LENGTH, limit.getSouvenirTitleLength());
        editor.putInt(FIELD_LIMIT_WHISPER_CONTENT_LENGTH, limit.getWhisperContentLength());
        editor.putInt(FIELD_LIMIT_WHISPER_CHANNEL_LENGTH, limit.getWhisperChannelLength());
        editor.putInt(FIELD_LIMIT_WORD_CONTENT_LENGTH, limit.getWordContentLength());
        editor.putInt(FIELD_LIMIT_DIARY_CONTENT_LENGTH, limit.getDiaryContentLength());
        editor.putInt(FIELD_LIMIT_MEET_TITLE_LENGTH, limit.getMeetTitleLength());
        editor.putInt(FIELD_LIMIT_MEET_PROCESS_LENGTH, limit.getMeetProcessLength());
        editor.putInt(FIELD_LIMIT_ALBUM_TITLE_LENGTH, limit.getAlbumTitleLength());
        editor.putInt(FIELD_LIMIT_PICTURE_COUNT, limit.getPictureCount());
        editor.putInt(FIELD_LIMIT_PROMISE_CONTENT_LENGTH, limit.getPromiseContentLength());
        editor.putInt(FIELD_LIMIT_AUDIO_TITLE_LENGTH, limit.getAudioTitleLength());
        editor.putInt(FIELD_LIMIT_VIDEO_TITLE_LENGTH, limit.getVideoTitleLength());
        editor.putInt(FIELD_LIMIT_GIFT_NAME_LENGTH, limit.getGiftNameLength());
        editor.putInt(FIELD_LIMIT_DREAM_CONTENT_LENGTH, limit.getDreamContentLength());
        editor.putInt(FIELD_LIMIT_ANGRY_REASON_LENGTH, limit.getAngryReasonLength());
        editor.putInt(FIELD_LIMIT_AWARD_REASON_LENGTH, limit.getAwardReasonLength());
        editor.putInt(FIELD_LIMIT_VIP_EXPIRE_DAYS, limit.getVipExpireDay());
        editor.apply();
    }

    public static Limit getLimit() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_LIMIT);
        Limit limit = new Limit();
        limit.setSmsCodeLength(sp.getInt(FIELD_LIMIT_SMS_CODE_LENGTH, 6));
        limit.setSmsEffectSec(sp.getInt(FIELD_LIMIT_SMS_EFFECT_SEC, 60 * 5));
        limit.setSmsBetweenSec(sp.getInt(FIELD_LIMIT_SMS_BETWEEN, 60 * 2));
        limit.setSmsMaxSec(sp.getInt(FIELD_LIMIT_SMS_MAX_TIME, 60 * 60));
        limit.setSmsMaxCount(sp.getInt(FIELD_LIMIT_SMS_MAX_COUNT, 5));
        limit.setSuggestTitleLength(sp.getInt(FIELD_LIMIT_SUGGEST_TITLE_LENGTH, 20));
        limit.setSuggestContentTextLength(sp.getInt(FIELD_LIMIT_SUGGEST_CONTENT_TEXT_LENGTH, 200));
        limit.setSuggestCommentContentLength(sp.getInt(FIELD_LIMIT_SUGGEST_COMMENT_CONTENT_LENGTH, 200));
        limit.setCoupleBreakSec(sp.getLong(FIELD_LIMIT_COUPLE_BREAK_SEC, 60 * 60 * 24));
        limit.setCoupleNameLength(sp.getInt(FIELD_LIMIT_COUPLE_NAME_LENGTH, 6));
        limit.setBookResExpireSec(sp.getLong(FIELD_LIMIT_BOOK_OSS_EXPIRE_SECONDS, ConstantUtils.MONTH * 3));
        limit.setSouvenirTitleLength(sp.getInt(FIELD_LIMIT_SOUVENIR_TITLE_LENGTH, 20));
        limit.setWhisperContentLength(sp.getInt(FIELD_LIMIT_WHISPER_CONTENT_LENGTH, 100));
        limit.setWhisperChannelLength(sp.getInt(FIELD_LIMIT_WHISPER_CHANNEL_LENGTH, 10));
        limit.setWordContentLength(sp.getInt(FIELD_LIMIT_WORD_CONTENT_LENGTH, 100));
        limit.setDiaryContentLength(sp.getInt(FIELD_LIMIT_DIARY_CONTENT_LENGTH, 2000));
        limit.setAlbumTitleLength(sp.getInt(FIELD_LIMIT_ALBUM_TITLE_LENGTH, 10));
        limit.setPictureCount(sp.getInt(FIELD_LIMIT_PICTURE_COUNT, 1));
        limit.setMeetTitleLength(sp.getInt(FIELD_LIMIT_MEET_TITLE_LENGTH, 20));
        limit.setMeetProcessLength(sp.getInt(FIELD_LIMIT_MEET_PROCESS_LENGTH, 200));
        limit.setPromiseContentLength(sp.getInt(FIELD_LIMIT_PROMISE_CONTENT_LENGTH, 200));
        limit.setAudioTitleLength(sp.getInt(FIELD_LIMIT_AUDIO_TITLE_LENGTH, 20));
        limit.setVideoTitleLength(sp.getInt(FIELD_LIMIT_VIDEO_TITLE_LENGTH, 20));
        limit.setGiftNameLength(sp.getInt(FIELD_LIMIT_GIFT_NAME_LENGTH, 20));
        limit.setDreamContentLength(sp.getInt(FIELD_LIMIT_DREAM_CONTENT_LENGTH, 1000));
        limit.setAngryReasonLength(sp.getInt(FIELD_LIMIT_ANGRY_REASON_LENGTH, 200));
        limit.setAwardReasonLength(sp.getInt(FIELD_LIMIT_AWARD_REASON_LENGTH, 10));
        limit.setVipExpireDay(sp.getInt(FIELD_LIMIT_VIP_EXPIRE_DAYS, 30));
        return limit;
    }

    public static void setOssInfo(OssInfo ossInfo) {
        clearOssInfo();
        if (ossInfo == null) {
            LogUtils.w(LOG_TAG, "ossInfo == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setOssInfo: " + GsonHelper.get().toJson(ossInfo));
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_OSS_INFO).edit();
        editor.putString(FIELD_OSS_SECURITY_TOKEN, ossInfo.getSecurityToken());
        editor.putString(FIELD_OSS_KEY_ID, ossInfo.getAccessKeyId());
        editor.putString(FIELD_OSS_KEY_SECRET, ossInfo.getAccessKeySecret());
        //editor.putString(FIELD_OSS_REGION, ossInfo.getRegion());
        //editor.putString(FIELD_OSS_ENDPOINT, ossInfo.getEndpoint());
        editor.putString(FIELD_OSS_DOMAIN, ossInfo.getDomain());
        editor.putString(FIELD_OSS_BUCKET, ossInfo.getBucket());
        editor.putLong(FIELD_OSS_EXPIRE_TIME, ossInfo.getExpireTime());
        editor.putLong(FIELD_OSS_INTERVAL_SEC, ossInfo.getIntervalSec());
        editor.putString(FIELD_OSS_PATH_SUGGEST, ossInfo.getPathSuggest());
        editor.putString(FIELD_OSS_PATH_COUPLE_AVATAR, ossInfo.getPathCoupleAvatar());
        editor.putString(FIELD_OSS_PATH_COUPLE_WALL, ossInfo.getPathCoupleWall());
        editor.putString(FIELD_OSS_PATH_BOOK_WHISPER, ossInfo.getPathBookWhisper());
        editor.putString(FIELD_OSS_PATH_BOOK_DIARY, ossInfo.getPathBookDiary());
        editor.putString(FIELD_OSS_PATH_BOOK_ALBUM, ossInfo.getPathBookAlbum());
        editor.putString(FIELD_OSS_PATH_BOOK_PICTURE, ossInfo.getPathBookPicture());
        editor.putString(FIELD_OSS_PATH_BOOK_GIFT, ossInfo.getPathBookGift());
        editor.putString(FIELD_OSS_PATH_BOOK_AUDIO, ossInfo.getPathBookAudio());
        editor.putString(FIELD_OSS_PATH_BOOK_VIDEO, ossInfo.getPathBookVideo());
        editor.putString(FIELD_OSS_PATH_BOOK_THUMB, ossInfo.getPathBookThumb());
        editor.apply();
    }

    public static OssInfo getOssInfo() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_OSS_INFO);
        OssInfo ossInfo = new OssInfo();
        ossInfo.setSecurityToken(sp.getString(FIELD_OSS_SECURITY_TOKEN, ""));
        ossInfo.setAccessKeyId(sp.getString(FIELD_OSS_KEY_ID, ""));
        ossInfo.setAccessKeySecret(sp.getString(FIELD_OSS_KEY_SECRET, ""));
        //ossInfo.setRegion(sp.getString(FIELD_OSS_REGION, ""));
        //ossInfo.setEndpoint(sp.getString(FIELD_OSS_ENDPOINT, ""));
        ossInfo.setDomain(sp.getString(FIELD_OSS_DOMAIN, ""));
        ossInfo.setBucket(sp.getString(FIELD_OSS_BUCKET, ""));
        ossInfo.setExpireTime(sp.getLong(FIELD_OSS_EXPIRE_TIME, 0));
        ossInfo.setIntervalSec(sp.getLong(FIELD_OSS_INTERVAL_SEC, 0));
        ossInfo.setPathSuggest(sp.getString(FIELD_OSS_PATH_SUGGEST, ""));
        ossInfo.setPathCoupleAvatar(sp.getString(FIELD_OSS_PATH_COUPLE_AVATAR, ""));
        ossInfo.setPathCoupleWall(sp.getString(FIELD_OSS_PATH_COUPLE_WALL, ""));
        ossInfo.setPathBookWhisper(sp.getString(FIELD_OSS_PATH_BOOK_WHISPER, ""));
        ossInfo.setPathBookDiary(sp.getString(FIELD_OSS_PATH_BOOK_DIARY, ""));
        ossInfo.setPathBookAlbum(sp.getString(FIELD_OSS_PATH_BOOK_ALBUM, ""));
        ossInfo.setPathBookPicture(sp.getString(FIELD_OSS_PATH_BOOK_PICTURE, ""));
        ossInfo.setPathBookGift(sp.getString(FIELD_OSS_PATH_BOOK_GIFT, ""));
        ossInfo.setPathBookAudio(sp.getString(FIELD_OSS_PATH_BOOK_AUDIO, ""));
        ossInfo.setPathBookVideo(sp.getString(FIELD_OSS_PATH_BOOK_VIDEO, ""));
        ossInfo.setPathBookThumb(sp.getString(FIELD_OSS_PATH_BOOK_THUMB, ""));
        return ossInfo;
    }

    public static void setVipLimit(VipLimit vipLimit) {
        clearVipLimit();
        if (vipLimit == null) {
            LogUtils.w(LOG_TAG, "setVipLimit == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_VIP_LIMIT).edit();
        editor.putLong(FIELD_VIP_LIMIT_VIP_EXPIRE_AT, vipLimit.getVipExpireAt());
        editor.putInt(FIELD_VIP_LIMIT_WALL_PAPER_COUNT, vipLimit.getWallPaperCount());
        editor.putInt(FIELD_VIP_LIMIT_WHISPER_IMG_COUNT, vipLimit.getWhisperImageCount());
        editor.putInt(FIELD_VIP_LIMIT_DIARY_IMG_COUNT, vipLimit.getDiaryImageCount());
        editor.putLong(FIELD_VIP_LIMIT_DIARY_IMG_SIZE, vipLimit.getDiaryImageSize());
        editor.putInt(FIELD_VIP_LIMIT_ALBUM_TOTAL_COUNT, vipLimit.getAlbumTotalCount());
        editor.putInt(FIELD_VIP_LIMIT_PICTURE_TOTAL_COUNT, vipLimit.getPictureTotalCount());
        editor.putLong(FIELD_VIP_LIMIT_PICTURE_SIZE, vipLimit.getPictureSize());
        editor.putInt(FIELD_VIP_LIMIT_GIFT_IMG_COUNT, vipLimit.getGiftImageCount());
        editor.putInt(FIELD_VIP_LIMIT_MEET_IMG_COUNT, vipLimit.getMeetImageCount());
        editor.putLong(FIELD_VIP_LIMIT_AUDIO_SIZE, vipLimit.getAudioSize());
        editor.putLong(FIELD_VIP_LIMIT_AUDIO_TOTAL_SIZE, vipLimit.getAudioTotalSize());
        editor.putLong(FIELD_VIP_LIMIT_VIDEO_SIZE, vipLimit.getVideoSize());
        editor.putLong(FIELD_VIP_LIMIT_VIDEO_TOTAL_SIZE, vipLimit.getVideoTotalSize());
        editor.putBoolean(FIELD_VIP_LIMIT_TOPIC_IMG, vipLimit.isTopicImageEnable());
        editor.apply();
    }

    public static VipLimit getVipLimit() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_VIP_LIMIT);
        VipLimit vipLimit = new VipLimit();
        vipLimit.setVipExpireAt(sp.getLong(FIELD_VIP_LIMIT_VIP_EXPIRE_AT, 0));
        vipLimit.setWallPaperCount(sp.getInt(FIELD_VIP_LIMIT_WALL_PAPER_COUNT, 1));
        vipLimit.setWhisperImageCount(sp.getInt(FIELD_VIP_LIMIT_WHISPER_IMG_COUNT, 0));
        vipLimit.setDiaryImageCount(sp.getInt(FIELD_VIP_LIMIT_DIARY_IMG_COUNT, 0));
        vipLimit.setDiaryImageSize(sp.getLong(FIELD_VIP_LIMIT_DIARY_IMG_SIZE, 0));
        vipLimit.setAlbumTotalCount(sp.getInt(FIELD_VIP_LIMIT_ALBUM_TOTAL_COUNT, 0));
        vipLimit.setPictureTotalCount(sp.getInt(FIELD_VIP_LIMIT_PICTURE_TOTAL_COUNT, 0));
        vipLimit.setPictureSize(sp.getLong(FIELD_VIP_LIMIT_PICTURE_SIZE, 0));
        vipLimit.setGiftImageCount(sp.getInt(FIELD_VIP_LIMIT_GIFT_IMG_COUNT, 0));
        vipLimit.setMeetImageCount(sp.getInt(FIELD_VIP_LIMIT_MEET_IMG_COUNT, 0));
        vipLimit.setAudioSize(sp.getLong(FIELD_VIP_LIMIT_AUDIO_SIZE, 0));
        vipLimit.setAudioTotalSize(sp.getLong(FIELD_VIP_LIMIT_AUDIO_TOTAL_SIZE, 0));
        vipLimit.setVideoSize(sp.getLong(FIELD_VIP_LIMIT_VIDEO_SIZE, 0));
        vipLimit.setVideoTotalSize(sp.getLong(FIELD_VIP_LIMIT_VIDEO_TOTAL_SIZE, 0));
        vipLimit.setTopicImageEnable(sp.getBoolean(FIELD_VIP_LIMIT_TOPIC_IMG, false));
        return vipLimit;
    }

    public static void setCommonConst(CommonConst commonConst) {
        if (commonConst == null) {
            LogUtils.w(LOG_TAG, "commonConst == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COMMON_CONST).edit();
        editor.putString(FIELD_COMMON_CONST_COMPANY_NAME, commonConst.getCompanyName());
        editor.putString(FIELD_COMMON_CONST_USER_PROTOCOL_URL, commonConst.getUserProtocolUrl());
        editor.putString(FIELD_COMMON_CONST_ABOUT_US_URL, commonConst.getAboutUsUrl());
        editor.apply();
    }

    public static CommonConst getCommonConst() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON_CONST);
        CommonConst commonConst = new CommonConst();
        commonConst.setCompanyName(sp.getString(FIELD_COMMON_CONST_COMPANY_NAME, ""));
        commonConst.setUserProtocolUrl(sp.getString(FIELD_COMMON_CONST_USER_PROTOCOL_URL, ""));
        commonConst.setAboutUsUrl(sp.getString(FIELD_COMMON_CONST_ABOUT_US_URL, ""));
        return commonConst;
    }

    public static void setDiary(Diary diary) {
        clearDiary();
        if (diary == null) {
            LogUtils.w(LOG_TAG, "diary == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setDiary: " + GsonHelper.get().toJson(diary));
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_DIARY).edit();
        editor.putLong(FIELD_DIARY_HAPPEN, diary.getHappenAt());
        editor.putString(FIELD_DIARY_CONTENT, diary.getContent());
        editor.apply();
    }

    public static Diary getDiary() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_DIARY);
        Diary diary = new Diary();
        diary.setHappenAt(sp.getLong(FIELD_DIARY_HAPPEN, 0));
        diary.setContent(sp.getString(FIELD_DIARY_CONTENT, ""));
        return diary;
    }

    public static void clearUser() {
        SPUtils.clear(SHARE_USER);
    }

    public static void clearCouple() {
        SPUtils.clear(SHARE_COUPLE);
    }

    public static void clearTa() {
        SPUtils.clear(SHARE_TA);
    }

    public static void clearVersion() {
        SPUtils.clear(SHARE_VERSION);
    }

    public static void clearWallPaper() {
        SPUtils.clear(SHARE_WALL_PAPER);
    }

    public static void clearOssInfo() {
        SPUtils.clear(SHARE_OSS_INFO);
    }

    public static void clearVipLimit() {
        SPUtils.clear(SHARE_VIP_LIMIT);
    }

    public static void clearDiary() {
        SPUtils.clear(SHARE_DIARY);
    }

    public static void setTheme(int themeId) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COMMON).edit();
        editor.putInt(FIELD_COMMON_THEME, themeId);
        editor.apply();
    }

    public static int getTheme() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON);
        return sp.getInt(FIELD_COMMON_THEME, ThemeHelper.THEME_PINK);
    }

    public static void setSettingsNoticeSystem(boolean system) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COMMON).edit();
        editor.putBoolean(FIELD_COMMON_NOTICE_SYSTEM, system);
        editor.apply();
    }

    public static boolean getSettingsNoticeSystem() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON);
        return sp.getBoolean(FIELD_COMMON_NOTICE_SYSTEM, true);
    }

    public static void setSettingsNoticeSocial(boolean ta) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COMMON).edit();
        editor.putBoolean(FIELD_COMMON_NOTICE_SOCIAL, ta);
        editor.apply();
    }

    public static boolean getSettingsNoticeSocial() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON);
        return sp.getBoolean(FIELD_COMMON_NOTICE_SOCIAL, true);
    }

    public static void setNoticeNoReadCount(long count) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COMMON).edit();
        editor.putLong(FIELD_COMMON_NOTICE_NO_READ_COUNT, count);
        editor.apply();
    }

    public static long getNoticeNoReadCount() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON);
        return sp.getLong(FIELD_COMMON_NOTICE_NO_READ_COUNT, 0);
    }

}
