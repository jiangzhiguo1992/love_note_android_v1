package com.jiangzg.mianmian.helper;

import android.content.SharedPreferences;

import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.system.SPUtils;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Limit;
import com.jiangzg.mianmian.domain.OssInfo;
import com.jiangzg.mianmian.domain.SuggestInfo;
import com.jiangzg.mianmian.domain.User;
import com.jiangzg.mianmian.domain.VipLimit;

import java.util.ArrayList;

/**
 * Created by Fan on 2017/3/2.
 * Preferences 帮助类
 */
public class SPHelper {

    private static final String LOG_TAG = "SPHelper";

    private static final String SHARE_USER = "shareUser";
    private static final String SHARE_COUPLE = "shareCouple";
    private static final String SHARE_LIMIT = "shareLimit";
    private static final String SHARE_SUGGEST_INFO = "shareSuggestInfo";
    private static final String SHARE_OSS_INFO = "shareOssInfo";
    private static final String SHARE_SETTINGS = "shareSettings";
    private static final String SHARE_VIP_LIMIT = "shareVipLimit";

    /* 储存字段 */
    // user
    private static final String FIELD_USER_ID = "id";
    private static final String FIELD_USER_PHONE = "phone";
    private static final String FIELD_USER_SEX = "sex";
    private static final String FIELD_USER_BIRTHDAY = "birthday";
    private static final String FIELD_USER_TOKEN = "userToken";
    // couple
    private static final String FIELD_CP_ID = "coupleId";
    private static final String FIELD_CP_STATUS = "status";
    private static final String FIELD_CP_UPDATE_AT = "updateAt";
    private static final String FIELD_CP_CREATOR_ID = "creatorId";
    private static final String FIELD_CP_CREATOR_NAME = "creatorName";
    private static final String FIELD_CP_CREATOR_AVATAR = "creatorAvatar";
    private static final String FIELD_CP_INVITEE_ID = "inviteeId";
    private static final String FIELD_CP_INVITEE_NAME = "inviteeName";
    private static final String FIELD_CP_INVITEE_AVATAR = "inviteeAvatar";
    // suggest
    private static final String FIELD_SUGGEST_INFO = "suggestInfo";
    // ossInfo
    private static final String FIELD_OSS_SECURITY_TOKEN = "securityToken";
    private static final String FIELD_OSS_KEY_ID = "accessKeyId";
    private static final String FIELD_OSS_KEY_SECRET = "accessKeySecret";
    private static final String FIELD_OSS_REGION = "region";
    private static final String FIELD_OSS_ENDPOINT = "endpoint";
    private static final String FIELD_OSS_DOMAIN = "domain";
    private static final String FIELD_OSS_BUCKET = "bucket";
    private static final String FIELD_OSS_EXPIRATION = "expiration";
    private static final String FIELD_OSS_INTERVAL = "interval";
    private static final String FIELD_OSS_PATH_VERSION = "pathVersion";
    private static final String FIELD_OSS_PATH_SUGGEST = "pathSuggest";
    private static final String FIELD_OSS_PATH_COUPLE_AVATAR = "pathCoupleAvatar";
    private static final String FIELD_OSS_PATH_COUPLE_WALL = "pathCoupleWall";
    private static final String FIELD_OSS_PATH_BOOK_ALBUM = "pathBookAlbum";
    private static final String FIELD_OSS_PATH_BOOK_PICTURE = "pathBookPicture";
    private static final String FIELD_OSS_PATH_BOOK_DIARY = "pathBookDiary";
    private static final String FIELD_OSS_PATH_BOOK_GIFT = "pathBookGift";
    private static final String FIELD_OSS_PATH_BOOK_AUDIO = "pathBookAudio";
    private static final String FIELD_OSS_PATH_BOOK_VIDEO = "pathBookVideo";
    private static final String FIELD_OSS_PATH_BOOK_THUMB = "pathBookThumb";
    // limit
    private static final String FIELD_LIMIT_SMS_EFFECT_SEC = "smsLimitEffectSec";
    private static final String FIELD_LIMIT_SMS_BETWEEN = "smsLimitBetween";
    private static final String FIELD_LIMIT_SMS_MAX_TIME = "smsLimitMaxTime";
    private static final String FIELD_LIMIT_SMS_MAX_COUNT = "smsLimitMaxCount";
    private static final String FIELD_LIMIT_SUGGEST_TITLE = "suggestLimitTitle";
    private static final String FIELD_LIMIT_SUGGEST_CONTENT_TEXT = "suggestLimitContentText";
    private static final String FIELD_LIMIT_SUGGEST_PAGE_SIZE = "suggestLimitPageSize";
    private static final String FIELD_LIMIT_SUGGEST_COMMENT_CONTENT = "suggestCommentLimitContent";
    private static final String FIELD_LIMIT_SUGGEST_COMMENT_PAGE_SIZE = "suggestCommentLimitPageSize";
    private static final String FIELD_LIMIT_COUPLE_NAME = "coupleLimitName";
    private static final String FIELD_LIMIT_VIP_EXPIRE_DAYS = "vipLimitExpireDays";
    private static final String FIELD_LIMIT_SOUVENIR_TITLE = "souvenirLimitTitle";
    private static final String FIELD_LIMIT_WHISPER_CONTENT = "whisperLimitContent";
    private static final String FIELD_LIMIT_WORD_CONTENT = "wordLimitContent";
    private static final String FIELD_LIMIT_DIARY_CONTENT = "diaryLimitContent";
    private static final String FIELD_LIMIT_DIARY_IMAGES = "diaryLimitImages";
    private static final String FIELD_LIMIT_TRACK_IMAGES = "trackLimitImages";
    private static final String FIELD_LIMIT_TRACK_TITLE = "trackLimitTitle";
    private static final String FIELD_LIMIT_TRACK_PROCESS = "trackLimitProcess";
    private static final String FIELD_LIMIT_ALBUM_TITLE = "albumLimitTitle";
    private static final String FIELD_LIMIT_PROMISE_CONTENT = "promiseLimitContent";
    private static final String FIELD_LIMIT_AUDIO_TITLE = "audioLimitTitle";
    private static final String FIELD_LIMIT_VIDEO_TITLE = "videoLimitTitle";
    private static final String FIELD_LIMIT_GIFT_NAME = "giftLimitName";
    private static final String FIELD_LIMIT_GIFT_IMAGES = "giftLimitImages";
    private static final String FIELD_LIMIT_DREAM_CONTENT = "dreamLimitContent";
    private static final String FIELD_LIMIT_ANGRY_REASON = "angryLimitReason";
    private static final String FIELD_LIMIT_AWARD_REASON = "awardLimitReason";
    // vipLimit
    private static final String FIELD_VIP_LIMIT_EXPIRE_AT = "expireAt";
    private static final String FIELD_VIP_LIMIT_CP_WALL_PAGER = "coupleWallPaperCount";
    private static final String FIELD_VIP_LIMIT_CP_ENTRY_TRACE = "coupleEntryTraceEnable";
    private static final String FIELD_VIP_LIMIT_CP_TOPIC_TRACE = "coupleTopicTraceEnable";
    private static final String FIELD_VIP_LIMIT_BK_PICTURE = "bookPictureEnable";
    private static final String FIELD_VIP_LIMIT_BK_DIARY_IMG = "bookDiaryImageEnable";
    private static final String FIELD_VIP_LIMIT_BK_GIFT_IMG = "bookGiftImageEnable";
    private static final String FIELD_VIP_LIMIT_TRACK_IMG = "bookTrackImageEnable";
    private static final String FIELD_VIP_LIMIT_AUDIO_ENABLE = "bookAudioEnable";
    private static final String FIELD_VIP_LIMIT_VIDEO_ENABLE = "bookVideoEnable";
    private static final String FIELD_VIP_LIMIT_AUDIO_SIZE = "bookAudioTotalSize";
    private static final String FIELD_VIP_LIMIT_VIDEO_SIZE = "bookVideoTotalSize";
    private static final String FIELD_VIP_LIMIT_TOPIC_IMG = "topicImageEnable";
    // settings
    private static final String FIELD_SET_THEME = "theme";
    private static final String FIELD_SET_NOTICE_SYSTEM = "noticeSystem";
    private static final String FIELD_SET_NOTICE_SOCIAL = "noticeSocial";

    public static void setUser(User user) {
        clearUser();
        clearCouple();
        if (user == null) {
            LogUtils.w(LOG_TAG, "user == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setUser: " + GsonUtils.get().toJson(user));
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
        if (CheckHelper.isNullCouple(couple)) {
            LogUtils.w(LOG_TAG, "couple == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setCouple: " + GsonUtils.get().toJson(couple));
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COUPLE).edit();
        editor.putLong(FIELD_CP_ID, couple.getId());
        editor.putInt(FIELD_CP_STATUS, couple.getStatus());
        editor.putLong(FIELD_CP_UPDATE_AT, couple.getUpdateAt());
        editor.putLong(FIELD_CP_CREATOR_ID, couple.getCreatorId());
        editor.putString(FIELD_CP_CREATOR_NAME, couple.getCreatorName());
        editor.putString(FIELD_CP_CREATOR_AVATAR, couple.getCreatorAvatar());
        editor.putLong(FIELD_CP_INVITEE_ID, couple.getInviteeId());
        editor.putString(FIELD_CP_INVITEE_NAME, couple.getInviteeName());
        editor.putString(FIELD_CP_INVITEE_AVATAR, couple.getInviteeAvatar());
        editor.apply();
    }

    public static Couple getCouple() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COUPLE);
        Couple couple = new Couple();
        couple.setId(sp.getLong(FIELD_CP_ID, 0));
        couple.setStatus(sp.getInt(FIELD_CP_STATUS, Couple.CoupleStatusTogether));
        couple.setUpdateAt(sp.getLong(FIELD_CP_UPDATE_AT, 0));
        couple.setCreatorId(sp.getLong(FIELD_CP_CREATOR_ID, 0));
        couple.setCreatorName(sp.getString(FIELD_CP_CREATOR_NAME, ""));
        couple.setCreatorAvatar(sp.getString(FIELD_CP_CREATOR_AVATAR, ""));
        couple.setInviteeId(sp.getLong(FIELD_CP_INVITEE_ID, 0));
        couple.setInviteeName(sp.getString(FIELD_CP_INVITEE_NAME, ""));
        couple.setInviteeAvatar(sp.getString(FIELD_CP_INVITEE_AVATAR, ""));
        return couple;
    }

    public static void setLimit(Limit limit) {
        if (limit == null) {
            LogUtils.w(LOG_TAG, "setLimit == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_LIMIT).edit();
        editor.putInt(FIELD_LIMIT_SMS_EFFECT_SEC, limit.getSmsLimitEffectSec());
        editor.putInt(FIELD_LIMIT_SMS_BETWEEN, limit.getSmsLimitBetween());
        editor.putInt(FIELD_LIMIT_SMS_MAX_TIME, limit.getSmsLimitMaxTime());
        editor.putInt(FIELD_LIMIT_SMS_MAX_COUNT, limit.getSmsLimitMaxCount());
        editor.putInt(FIELD_LIMIT_SUGGEST_TITLE, limit.getSuggestLimitTitle());
        editor.putInt(FIELD_LIMIT_SUGGEST_CONTENT_TEXT, limit.getSuggestLimitContentText());
        editor.putInt(FIELD_LIMIT_SUGGEST_PAGE_SIZE, limit.getSuggestLimitPageSize());
        editor.putInt(FIELD_LIMIT_SUGGEST_COMMENT_CONTENT, limit.getSuggestCommentLimitContent());
        editor.putInt(FIELD_LIMIT_SUGGEST_COMMENT_PAGE_SIZE, limit.getSuggestCommentLimitPageSize());
        editor.putInt(FIELD_LIMIT_COUPLE_NAME, limit.getCoupleLimitName());
        editor.putInt(FIELD_LIMIT_VIP_EXPIRE_DAYS, limit.getVipLimitExpireDays());
        editor.putInt(FIELD_LIMIT_SOUVENIR_TITLE, limit.getSouvenirLimitTitle());
        editor.putInt(FIELD_LIMIT_WHISPER_CONTENT, limit.getWhisperLimitContent());
        editor.putInt(FIELD_LIMIT_WORD_CONTENT, limit.getWordLimitContent());
        editor.putInt(FIELD_LIMIT_DIARY_CONTENT, limit.getDiaryLimitContent());
        editor.putInt(FIELD_LIMIT_DIARY_IMAGES, limit.getDiaryLimitImages());
        editor.putInt(FIELD_LIMIT_TRACK_IMAGES, limit.getTrackLimitImages());
        editor.putInt(FIELD_LIMIT_TRACK_TITLE, limit.getTrackLimitTitle());
        editor.putInt(FIELD_LIMIT_TRACK_PROCESS, limit.getTrackLimitProcess());
        editor.putInt(FIELD_LIMIT_ALBUM_TITLE, limit.getAlbumLimitTitle());
        editor.putInt(FIELD_LIMIT_PROMISE_CONTENT, limit.getPromiseLimitContent());
        editor.putInt(FIELD_LIMIT_AUDIO_TITLE, limit.getAudioLimitTitle());
        editor.putInt(FIELD_LIMIT_VIDEO_TITLE, limit.getVideoLimitTitle());
        editor.putInt(FIELD_LIMIT_GIFT_NAME, limit.getGiftLimitName());
        editor.putInt(FIELD_LIMIT_GIFT_IMAGES, limit.getGiftLimitImages());
        editor.putInt(FIELD_LIMIT_DREAM_CONTENT, limit.getDreamLimitContent());
        editor.putInt(FIELD_LIMIT_ANGRY_REASON, limit.getAngryLimitReason());
        editor.putInt(FIELD_LIMIT_AWARD_REASON, limit.getAwardLimitReason());
        editor.apply();
    }

    public static Limit getLimit() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_LIMIT);
        Limit limit = new Limit();
        limit.setSmsLimitEffectSec(sp.getInt(FIELD_LIMIT_SMS_EFFECT_SEC, 60 * 5));
        limit.setSmsLimitBetween(sp.getInt(FIELD_LIMIT_SMS_BETWEEN, 60 * 2));
        limit.setSmsLimitMaxTime(sp.getInt(FIELD_LIMIT_SMS_MAX_TIME, 60 * 60));
        limit.setSmsLimitMaxCount(sp.getInt(FIELD_LIMIT_SMS_MAX_COUNT, 5));
        limit.setSuggestLimitTitle(sp.getInt(FIELD_LIMIT_SUGGEST_TITLE, 20));
        limit.setSuggestLimitContentText(sp.getInt(FIELD_LIMIT_SUGGEST_CONTENT_TEXT, 200));
        limit.setSuggestLimitPageSize(sp.getInt(FIELD_LIMIT_SUGGEST_PAGE_SIZE, 30));
        limit.setSuggestCommentLimitContent(sp.getInt(FIELD_LIMIT_SUGGEST_COMMENT_CONTENT, 200));
        limit.setSuggestCommentLimitPageSize(sp.getInt(FIELD_LIMIT_SUGGEST_COMMENT_PAGE_SIZE, 30));
        limit.setCoupleLimitName(sp.getInt(FIELD_LIMIT_COUPLE_NAME, 6));
        limit.setVipLimitExpireDays(sp.getInt(FIELD_LIMIT_VIP_EXPIRE_DAYS, 30));
        limit.setSouvenirLimitTitle(sp.getInt(FIELD_LIMIT_SOUVENIR_TITLE, 20));
        limit.setWhisperLimitContent(sp.getInt(FIELD_LIMIT_WHISPER_CONTENT, 100));
        limit.setWordLimitContent(sp.getInt(FIELD_LIMIT_WORD_CONTENT, 100));
        limit.setDiaryLimitContent(sp.getInt(FIELD_LIMIT_DIARY_CONTENT, 2000));
        limit.setDiaryLimitImages(sp.getInt(FIELD_LIMIT_DIARY_IMAGES, 3));
        limit.setTrackLimitImages(sp.getInt(FIELD_LIMIT_TRACK_IMAGES, 9));
        limit.setTrackLimitTitle(sp.getInt(FIELD_LIMIT_TRACK_TITLE, 20));
        limit.setTrackLimitProcess(sp.getInt(FIELD_LIMIT_TRACK_PROCESS, 200));
        limit.setAlbumLimitTitle(sp.getInt(FIELD_LIMIT_ALBUM_TITLE, 10));
        limit.setPromiseLimitContent(sp.getInt(FIELD_LIMIT_PROMISE_CONTENT, 200));
        limit.setAudioLimitTitle(sp.getInt(FIELD_LIMIT_AUDIO_TITLE, 20));
        limit.setVideoLimitTitle(sp.getInt(FIELD_LIMIT_VIDEO_TITLE, 20));
        limit.setGiftLimitName(sp.getInt(FIELD_LIMIT_GIFT_NAME, 20));
        limit.setGiftLimitImages(sp.getInt(FIELD_LIMIT_GIFT_IMAGES, 3));
        limit.setDreamLimitContent(sp.getInt(FIELD_LIMIT_DREAM_CONTENT, 1000));
        limit.setAngryLimitReason(sp.getInt(FIELD_LIMIT_ANGRY_REASON, 200));
        limit.setAwardLimitReason(sp.getInt(FIELD_LIMIT_AWARD_REASON, 10));
        return limit;
    }

    public static void setSuggestInfo(SuggestInfo suggestInfo) {
        if (suggestInfo == null) {
            LogUtils.w(LOG_TAG, "setSuggestInfo == null");
            return;
        }
        String suggestInfoJson = GsonUtils.get().toJson(suggestInfo);
        LogUtils.d(LOG_TAG, "setSuggestInfo: " + suggestInfoJson);

        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SUGGEST_INFO).edit();
        editor.putString(FIELD_SUGGEST_INFO, suggestInfoJson);
        editor.apply();
    }

    public static SuggestInfo getSuggestInfo() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_SUGGEST_INFO);
        String json = sp.getString(FIELD_SUGGEST_INFO, "{}");
        SuggestInfo suggestInfo = GsonUtils.get().fromJson(json, SuggestInfo.class);
        if (suggestInfo == null) {
            suggestInfo = new SuggestInfo();
        }
        if (suggestInfo.getSuggestStatusList() == null) {
            suggestInfo.setSuggestStatusList(new ArrayList<SuggestInfo.SuggestStatus>());
        }
        if (suggestInfo.getSuggestContentTypeList() == null) {
            suggestInfo.setSuggestContentTypeList(new ArrayList<SuggestInfo.SuggestContentType>());
        }
        return suggestInfo;
    }

    public static void setOssInfo(OssInfo ossInfo) {
        clearOssInfo();
        if (ossInfo == null) {
            LogUtils.w(LOG_TAG, "ossInfo == null");
            return;
        } else {
            LogUtils.d(LOG_TAG, "setOssInfo: " + GsonUtils.get().toJson(ossInfo));
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_OSS_INFO).edit();
        editor.putString(FIELD_OSS_SECURITY_TOKEN, ossInfo.getSecurityToken());
        editor.putString(FIELD_OSS_KEY_ID, ossInfo.getAccessKeyId());
        editor.putString(FIELD_OSS_KEY_SECRET, ossInfo.getAccessKeySecret());
        //editor.putString(FIELD_OSS_REGION, ossInfo.getRegion());
        //editor.putString(FIELD_OSS_ENDPOINT, ossInfo.getEndpoint());
        editor.putString(FIELD_OSS_DOMAIN, ossInfo.getDomain());
        editor.putString(FIELD_OSS_BUCKET, ossInfo.getBucket());
        editor.putLong(FIELD_OSS_EXPIRATION, ossInfo.getExpiration());
        editor.putLong(FIELD_OSS_INTERVAL, ossInfo.getInterval());
        editor.putString(FIELD_OSS_PATH_VERSION, ossInfo.getPathVersion());
        editor.putString(FIELD_OSS_PATH_SUGGEST, ossInfo.getPathSuggest());
        editor.putString(FIELD_OSS_PATH_COUPLE_AVATAR, ossInfo.getPathCoupleAvatar());
        editor.putString(FIELD_OSS_PATH_COUPLE_WALL, ossInfo.getPathCoupleWall());
        editor.putString(FIELD_OSS_PATH_BOOK_ALBUM, ossInfo.getPathBookAlbum());
        editor.putString(FIELD_OSS_PATH_BOOK_PICTURE, ossInfo.getPathBookPicture());
        editor.putString(FIELD_OSS_PATH_BOOK_DIARY, ossInfo.getPathBookDiary());
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
        ossInfo.setExpiration(sp.getLong(FIELD_OSS_EXPIRATION, 0));
        ossInfo.setInterval(sp.getLong(FIELD_OSS_INTERVAL, 0));
        ossInfo.setPathVersion(sp.getString(FIELD_OSS_PATH_VERSION, ""));
        ossInfo.setPathSuggest(sp.getString(FIELD_OSS_PATH_SUGGEST, ""));
        ossInfo.setPathCoupleAvatar(sp.getString(FIELD_OSS_PATH_COUPLE_AVATAR, ""));
        ossInfo.setPathCoupleWall(sp.getString(FIELD_OSS_PATH_COUPLE_WALL, ""));
        ossInfo.setPathBookAlbum(sp.getString(FIELD_OSS_PATH_BOOK_ALBUM, ""));
        ossInfo.setPathBookPicture(sp.getString(FIELD_OSS_PATH_BOOK_PICTURE, ""));
        ossInfo.setPathBookDiary(sp.getString(FIELD_OSS_PATH_BOOK_DIARY, ""));
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
        editor.putLong(FIELD_VIP_LIMIT_EXPIRE_AT, vipLimit.getExpireAt());
        editor.putInt(FIELD_VIP_LIMIT_CP_WALL_PAGER, vipLimit.getCoupleWallPaperCount());
        editor.putBoolean(FIELD_VIP_LIMIT_CP_ENTRY_TRACE, vipLimit.isCoupleEntryTraceEnable());
        editor.putBoolean(FIELD_VIP_LIMIT_CP_TOPIC_TRACE, vipLimit.isCoupleTopicTraceEnable());
        editor.putBoolean(FIELD_VIP_LIMIT_BK_PICTURE, vipLimit.isBookPictureEnable());
        editor.putBoolean(FIELD_VIP_LIMIT_BK_DIARY_IMG, vipLimit.isBookDiaryImageEnable());
        editor.putBoolean(FIELD_VIP_LIMIT_BK_GIFT_IMG, vipLimit.isBookGiftImageEnable());
        editor.putBoolean(FIELD_VIP_LIMIT_TRACK_IMG, vipLimit.isBookTrackImageEnable());
        editor.putBoolean(FIELD_VIP_LIMIT_AUDIO_ENABLE, vipLimit.isBookAudioEnable());
        editor.putBoolean(FIELD_VIP_LIMIT_VIDEO_ENABLE, vipLimit.isBookVideoEnable());
        editor.putLong(FIELD_VIP_LIMIT_AUDIO_SIZE, vipLimit.getBookAudioTotalSize());
        editor.putLong(FIELD_VIP_LIMIT_VIDEO_SIZE, vipLimit.getBookVideoTotalSize());
        editor.putBoolean(FIELD_VIP_LIMIT_TOPIC_IMG, vipLimit.isTopicImageEnable());
        editor.apply();
    }

    public static VipLimit getVipLimit() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_VIP_LIMIT);
        VipLimit vipLimit = new VipLimit();
        vipLimit.setExpireAt(sp.getLong(FIELD_VIP_LIMIT_EXPIRE_AT, 0));
        vipLimit.setCoupleWallPaperCount(sp.getInt(FIELD_VIP_LIMIT_CP_WALL_PAGER, 1));
        vipLimit.setCoupleEntryTraceEnable(sp.getBoolean(FIELD_VIP_LIMIT_CP_ENTRY_TRACE, false));
        vipLimit.setCoupleTopicTraceEnable(sp.getBoolean(FIELD_VIP_LIMIT_CP_TOPIC_TRACE, false));
        vipLimit.setBookPictureEnable(sp.getBoolean(FIELD_VIP_LIMIT_BK_PICTURE, false));
        vipLimit.setBookDiaryImageEnable(sp.getBoolean(FIELD_VIP_LIMIT_BK_DIARY_IMG, false));
        vipLimit.setBookGiftImageEnable(sp.getBoolean(FIELD_VIP_LIMIT_BK_GIFT_IMG, false));
        vipLimit.setBookTrackImageEnable(sp.getBoolean(FIELD_VIP_LIMIT_TRACK_IMG, false));
        vipLimit.setBookAudioEnable(sp.getBoolean(FIELD_VIP_LIMIT_AUDIO_ENABLE, false));
        vipLimit.setBookVideoEnable(sp.getBoolean(FIELD_VIP_LIMIT_VIDEO_ENABLE, false));
        vipLimit.setBookAudioTotalSize(sp.getLong(FIELD_VIP_LIMIT_AUDIO_SIZE, 0));
        vipLimit.setBookVideoTotalSize(sp.getLong(FIELD_VIP_LIMIT_VIDEO_SIZE, 0));
        vipLimit.setTopicImageEnable(sp.getBoolean(FIELD_VIP_LIMIT_TOPIC_IMG, false));
        return vipLimit;
    }

    public static void clearUser() {
        SPUtils.clear(SHARE_USER);
    }

    public static void clearCouple() {
        SPUtils.clear(SHARE_COUPLE);
    }

    public static void clearOssInfo() {
        SPUtils.clear(SHARE_OSS_INFO);
    }

    public static void clearVipLimit() {
        SPUtils.clear(SHARE_VIP_LIMIT);
    }

    public static void setSettingsTheme(int themeId) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SETTINGS).edit();
        editor.putInt(FIELD_SET_THEME, themeId);
        editor.apply();
    }

    public static int getSettingsTheme() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_SETTINGS);
        return sp.getInt(FIELD_SET_THEME, ThemeHelper.THEME_PINK);
    }

    public static void setSettingsNoticeSystem(boolean system) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SETTINGS).edit();
        editor.putBoolean(FIELD_SET_NOTICE_SYSTEM, system);
        editor.apply();
    }

    public static boolean getSettingsNoticeSystem() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_SETTINGS);
        return sp.getBoolean(FIELD_SET_NOTICE_SYSTEM, true);
    }

    public static void setSettingsNoticeSocial(boolean ta) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_SETTINGS).edit();
        editor.putBoolean(FIELD_SET_NOTICE_SOCIAL, ta);
        editor.apply();
    }

    public static boolean getSettingsNoticeSocial() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_SETTINGS);
        return sp.getBoolean(FIELD_SET_NOTICE_SOCIAL, true);
    }

}
