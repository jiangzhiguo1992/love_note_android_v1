package com.jiangzg.mianmian.helper;

import android.content.SharedPreferences;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.SPUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.mianmian.domain.CommonConst;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.Diary;
import com.jiangzg.mianmian.domain.Dream;
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

    // 存储集合
    private static final String SHARE_COMMON = "share_common";
    private static final String SHARE_OSS_INFO = "share_oss_info";
    private static final String SHARE_LIMIT = "share_limit";
    private static final String SHARE_VIP_LIMIT = "share_vip_limit";
    private static final String SHARE_COMMON_CONST = "share_common_const";
    private static final String SHARE_VERSION = "share_version";
    private static final String SHARE_ME = "share_me";
    private static final String SHARE_TA = "share_ta";
    private static final String SHARE_COUPLE = "share_couple";
    private static final String SHARE_COUPLE_TOGETHER = "share_couple_together";
    private static final String SHARE_WALL_PAPER = "share_wall_paper";
    private static final String SHARE_DRAFT = "share_draft";

    // common
    private static final String FIELD_COMMON_THEME = "theme";
    private static final String FIELD_COMMON_NOTICE_SYSTEM = "notice_system";
    private static final String FIELD_COMMON_NOTICE_SOCIAL = "notice_social";
    private static final String FIELD_COMMON_NOTICE_NO_READ_COUNT = "notice_no_read_count";
    // ossInfo
    private static final String FIELD_OSS_SECURITY_TOKEN = "security_token";
    private static final String FIELD_OSS_KEY_ID = "access_key_id";
    private static final String FIELD_OSS_KEY_SECRET = "access_key_secret";
    //private static final String FIELD_OSS_REGION = "region";
    //private static final String FIELD_OSS_ENDPOINT = "endpoint";
    private static final String FIELD_OSS_DOMAIN = "domain";
    private static final String FIELD_OSS_BUCKET = "bucket";
    private static final String FIELD_OSS_EXPIRE_TIME = "expire_time";
    private static final String FIELD_OSS_INTERVAL_SEC = "interval_sec";
    private static final String FIELD_OSS_PATH_LOG = "path_log";
    private static final String FIELD_OSS_PATH_SUGGEST = "path_suggest";
    private static final String FIELD_OSS_PATH_COUPLE_AVATAR = "path_couple_avatar";
    private static final String FIELD_OSS_PATH_COUPLE_WALL = "path_couple_wall";
    private static final String FIELD_OSS_PATH_BOOK_WHISPER = "path_book_whisper";
    private static final String FIELD_OSS_PATH_BOOK_DIARY = "path_book_diary";
    private static final String FIELD_OSS_PATH_BOOK_ALBUM = "path_book_album";
    private static final String FIELD_OSS_PATH_BOOK_PICTURE = "path_book_picture";
    private static final String FIELD_OSS_PATH_BOOK_AUDIO = "path_book_audio";
    private static final String FIELD_OSS_PATH_BOOK_VIDEO = "path_book_video";
    private static final String FIELD_OSS_PATH_BOOK_VIDEO_THUMB = "path_book_video_thumb";
    private static final String FIELD_OSS_PATH_BOOK_GIFT = "path_book_gift";
    // limit
    private static final String FIELD_LIMIT_SMS_CODE_LENGTH = "sms_code_length";
    private static final String FIELD_LIMIT_SMS_EFFECT_SEC = "sms_effect_sec";
    private static final String FIELD_LIMIT_SMS_BETWEEN = "sms_between";
    private static final String FIELD_LIMIT_SMS_MAX_TIME = "sms_max_time";
    private static final String FIELD_LIMIT_SMS_MAX_COUNT = "sms_max_count";
    private static final String FIELD_LIMIT_SUGGEST_TITLE_LENGTH = "suggest_title_length";
    private static final String FIELD_LIMIT_SUGGEST_CONTENT_TEXT_LENGTH = "suggest_content_text_length";
    private static final String FIELD_LIMIT_SUGGEST_COMMENT_CONTENT_LENGTH = "suggest_comment_content_length";
    private static final String FIELD_LIMIT_COUPLE_BREAK_SEC = "couple_break_sec";
    private static final String FIELD_LIMIT_COUPLE_NAME_LENGTH = "couple_name_length";
    private static final String FIELD_LIMIT_BOOK_OSS_EXPIRE_SECONDS = "book_oss_expire_seconds";
    private static final String FIELD_LIMIT_SOUVENIR_TITLE_LENGTH = "souvenir_title_length";
    private static final String FIELD_LIMIT_WHISPER_CONTENT_LENGTH = "whisper_content_length";
    private static final String FIELD_LIMIT_WHISPER_CHANNEL_LENGTH = "whisper_channel_length";
    private static final String FIELD_LIMIT_WORD_CONTENT_LENGTH = "word_content_length";
    private static final String FIELD_LIMIT_DIARY_CONTENT_LENGTH = "diary_content_length";
    private static final String FIELD_LIMIT_ALBUM_TITLE_LENGTH = "album_title_length";
    private static final String FIELD_LIMIT_PICTURE_COUNT = "picture_count";
    private static final String FIELD_LIMIT_AUDIO_TITLE_LENGTH = "audio_title_length";
    private static final String FIELD_LIMIT_VIDEO_TITLE_LENGTH = "video_title_length";
    private static final String FIELD_LIMIT_MEET_TITLE_LENGTH = "meet_title_length";
    private static final String FIELD_LIMIT_MEET_PROCESS_LENGTH = "meet_process_length";
    private static final String FIELD_LIMIT_GIFT_TITLE_LENGTH = "gift_title_length";
    private static final String FIELD_LIMIT_PROMISE_CONTENT_LENGTH = "promise_content_length";
    private static final String FIELD_LIMIT_PROMISE_BREAK_CONTENT_LENGTH = "promise_break_content_length";
    private static final String FIELD_LIMIT_ANGRY_CONTENT_LENGTH = "angry_content_length";
    private static final String FIELD_LIMIT_DREAM_CONTENT_LENGTH = "dream_content_length";
    private static final String FIELD_LIMIT_AWARD_REASON_LENGTH = "award_reason_length";
    private static final String FIELD_LIMIT_VIP_EXPIRE_DAYS = "vip_expire_days";
    // vipLimit
    private static final String FIELD_VIP_LIMIT_VIP_EXPIRE_AT = "vip_expire_at";
    private static final String FIELD_VIP_LIMIT_WALL_PAPER_COUNT = "wall_paper_count";
    private static final String FIELD_VIP_LIMIT_WHISPER_IMG_COUNT = "whisper_image_count";
    private static final String FIELD_VIP_LIMIT_DIARY_IMG_SIZE = "diary_image_size";
    private static final String FIELD_VIP_LIMIT_DIARY_IMG_COUNT = "diary_image_count";
    private static final String FIELD_VIP_LIMIT_ALBUM_TOTAL_COUNT = "album_total_count";
    private static final String FIELD_VIP_LIMIT_PICTURE_SIZE = "picture_size";
    private static final String FIELD_VIP_LIMIT_PICTURE_TOTAL_COUNT = "picture_total_count";
    private static final String FIELD_VIP_LIMIT_MEET_IMG_COUNT = "meet_image_count";
    private static final String FIELD_VIP_LIMIT_GIFT_IMG_COUNT = "gift_image_count";
    private static final String FIELD_VIP_LIMIT_AUDIO_SIZE = "audio_size";
    private static final String FIELD_VIP_LIMIT_AUDIO_TOTAL_COUNT = "audio_total_count";
    private static final String FIELD_VIP_LIMIT_VIDEO_SIZE = "video_size";
    private static final String FIELD_VIP_LIMIT_VIDEO_TOTAL_COUNT = "video_total_count";
    private static final String FIELD_VIP_LIMIT_TOPIC_IMG = "topic_image_enable";
    // commonConst
    private static final String FIELD_COMMON_CONST_COMPANY_NAME = "company_name";
    private static final String FIELD_COMMON_CONST_USER_PROTOCOL_URL = "user_protocol_url";
    private static final String FIELD_COMMON_CONST_ABOUT_US_URL = "about_us_url";
    // version
    private static final String FIELD_VERSION_CREATE_AT = "create_at";
    private static final String FIELD_VERSION_VERSION_CODE = "version_code";
    private static final String FIELD_VERSION_VERSION_NAME = "version_name";
    private static final String FIELD_VERSION_UPDATE_LOG = "update_log";
    private static final String FIELD_VERSION_UPDATE_URL = "update_url";
    // user
    private static final String FIELD_USER_ID = "id";
    private static final String FIELD_USER_PHONE = "phone";
    private static final String FIELD_USER_SEX = "sex";
    private static final String FIELD_USER_BIRTHDAY = "birthday";
    private static final String FIELD_USER_TOKEN = "userToken";
    // couple
    private static final String FIELD_CP_ID = "id";
    private static final String FIELD_CP_CREATE_AT = "create_at";
    private static final String FIELD_CP_UPDATE_AT = "update_at";
    private static final String FIELD_CP_CREATOR_ID = "creator_id";
    private static final String FIELD_CP_CREATOR_NAME = "creator_name";
    private static final String FIELD_CP_CREATOR_AVATAR = "creator_avatar";
    private static final String FIELD_CP_INVITEE_ID = "invitee_id";
    private static final String FIELD_CP_INVITEE_NAME = "invitee_name";
    private static final String FIELD_CP_INVITEE_AVATAR = "invitee_avatar";
    private static final String FIELD_CP_STATE_ID = "state_id";
    private static final String FIELD_CP_STATE_CREATE_AT = "state_create_at";
    private static final String FIELD_CP_STATE_UPDATE_AT = "state_update_at";
    private static final String FIELD_CP_STATE_USER_ID = "state_user_id";
    private static final String FIELD_CP_STATE_STATE = "state_state";
    // togetherDay
    private static final String FIELD_COUPLE_TOGETHER_DAY = "day";
    // wallPaper
    private static final String FIELD_WALL_PAPER_JSON = "json";
    // draft
    private static final String FIELD_DRAFT_DIARY_HAPPEN = "diary_happen";
    private static final String FIELD_DRAFT_DIARY_CONTENT_TEXT = "diary_content_text";
    private static final String FIELD_DRAFT_DREAM_HAPPEN = "dream_happen";
    private static final String FIELD_DRAFT_DREAM_CONTENT_TEXT = "dream_content_text";

    /**
     * ***********************************清除***********************************
     */
    public static void clearOssInfo() {
        SPUtils.clear(SHARE_OSS_INFO);
    }

    public static void clearVipLimit() {
        SPUtils.clear(SHARE_VIP_LIMIT);
    }

    public static void clearVersion() {
        SPUtils.clear(SHARE_VERSION);
    }

    public static void clearMe() {
        SPUtils.clear(SHARE_ME);
    }

    public static void clearTa() {
        SPUtils.clear(SHARE_TA);
    }

    public static void clearCouple() {
        SPUtils.clear(SHARE_COUPLE);
    }

    public static void clearWallPaper() {
        SPUtils.clear(SHARE_WALL_PAPER);
    }

    public static void clearDraft() {
        SPUtils.clear(SHARE_DRAFT);
    }

    /**
     * ***********************************Common***********************************
     */
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

    public static void setNoticeNoReadCount(int count) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COMMON).edit();
        editor.putInt(FIELD_COMMON_NOTICE_NO_READ_COUNT, count);
        editor.apply();
    }

    public static int getNoticeNoReadCount() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON);
        return sp.getInt(FIELD_COMMON_NOTICE_NO_READ_COUNT, 0);
    }

    /**
     * ***********************************OssInfo***********************************
     */
    public static void setOssInfo(OssInfo ossInfo) {
        clearOssInfo();
        if (ossInfo == null) {
            LogUtils.i(SPHelper.class, "setOssInfo", "ossInfo == null");
            return;
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
        editor.putString(FIELD_OSS_PATH_LOG, ossInfo.getPathLog());
        editor.putString(FIELD_OSS_PATH_SUGGEST, ossInfo.getPathSuggest());
        editor.putString(FIELD_OSS_PATH_COUPLE_AVATAR, ossInfo.getPathCoupleAvatar());
        editor.putString(FIELD_OSS_PATH_COUPLE_WALL, ossInfo.getPathCoupleWall());
        editor.putString(FIELD_OSS_PATH_BOOK_WHISPER, ossInfo.getPathBookWhisper());
        editor.putString(FIELD_OSS_PATH_BOOK_DIARY, ossInfo.getPathBookDiary());
        editor.putString(FIELD_OSS_PATH_BOOK_ALBUM, ossInfo.getPathBookAlbum());
        editor.putString(FIELD_OSS_PATH_BOOK_PICTURE, ossInfo.getPathBookPicture());
        editor.putString(FIELD_OSS_PATH_BOOK_AUDIO, ossInfo.getPathBookAudio());
        editor.putString(FIELD_OSS_PATH_BOOK_VIDEO, ossInfo.getPathBookVideo());
        editor.putString(FIELD_OSS_PATH_BOOK_VIDEO_THUMB, ossInfo.getPathBookVideoThumb());
        editor.putString(FIELD_OSS_PATH_BOOK_GIFT, ossInfo.getPathBookGift());
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
        ossInfo.setExpireTime(sp.getLong(FIELD_OSS_EXPIRE_TIME, TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()) + 60 * 10));
        ossInfo.setIntervalSec(sp.getLong(FIELD_OSS_INTERVAL_SEC, 60 * 10));
        ossInfo.setPathLog(sp.getString(FIELD_OSS_PATH_LOG, ""));
        ossInfo.setPathSuggest(sp.getString(FIELD_OSS_PATH_SUGGEST, ""));
        ossInfo.setPathCoupleAvatar(sp.getString(FIELD_OSS_PATH_COUPLE_AVATAR, ""));
        ossInfo.setPathCoupleWall(sp.getString(FIELD_OSS_PATH_COUPLE_WALL, ""));
        ossInfo.setPathBookWhisper(sp.getString(FIELD_OSS_PATH_BOOK_WHISPER, ""));
        ossInfo.setPathBookDiary(sp.getString(FIELD_OSS_PATH_BOOK_DIARY, ""));
        ossInfo.setPathBookAlbum(sp.getString(FIELD_OSS_PATH_BOOK_ALBUM, ""));
        ossInfo.setPathBookPicture(sp.getString(FIELD_OSS_PATH_BOOK_PICTURE, ""));
        ossInfo.setPathBookAudio(sp.getString(FIELD_OSS_PATH_BOOK_AUDIO, ""));
        ossInfo.setPathBookVideo(sp.getString(FIELD_OSS_PATH_BOOK_VIDEO, ""));
        ossInfo.setPathBookVideoThumb(sp.getString(FIELD_OSS_PATH_BOOK_VIDEO_THUMB, ""));
        ossInfo.setPathBookGift(sp.getString(FIELD_OSS_PATH_BOOK_GIFT, ""));
        return ossInfo;
    }

    /**
     * ***********************************Limit***********************************
     */
    public static void setLimit(Limit limit) {
        if (limit == null) {
            LogUtils.i(SPHelper.class, "setLimit", "limit == null");
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
        editor.putInt(FIELD_LIMIT_ALBUM_TITLE_LENGTH, limit.getAlbumTitleLength());
        editor.putInt(FIELD_LIMIT_PICTURE_COUNT, limit.getPictureCount());
        editor.putInt(FIELD_LIMIT_AUDIO_TITLE_LENGTH, limit.getAudioTitleLength());
        editor.putInt(FIELD_LIMIT_VIDEO_TITLE_LENGTH, limit.getVideoTitleLength());
        editor.putInt(FIELD_LIMIT_MEET_TITLE_LENGTH, limit.getMeetTitleLength());
        editor.putInt(FIELD_LIMIT_MEET_PROCESS_LENGTH, limit.getMeetProcessLength());
        editor.putInt(FIELD_LIMIT_GIFT_TITLE_LENGTH, limit.getGiftTitleLength());
        editor.putInt(FIELD_LIMIT_PROMISE_CONTENT_LENGTH, limit.getPromiseContentLength());
        editor.putInt(FIELD_LIMIT_PROMISE_BREAK_CONTENT_LENGTH, limit.getPromiseBreakContentLength());
        editor.putInt(FIELD_LIMIT_ANGRY_CONTENT_LENGTH, limit.getAngryContentLength());
        editor.putInt(FIELD_LIMIT_DREAM_CONTENT_LENGTH, limit.getDreamContentLength());
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
        limit.setAudioTitleLength(sp.getInt(FIELD_LIMIT_AUDIO_TITLE_LENGTH, 20));
        limit.setVideoTitleLength(sp.getInt(FIELD_LIMIT_VIDEO_TITLE_LENGTH, 20));
        limit.setMeetTitleLength(sp.getInt(FIELD_LIMIT_MEET_TITLE_LENGTH, 20));
        limit.setMeetProcessLength(sp.getInt(FIELD_LIMIT_MEET_PROCESS_LENGTH, 200));
        limit.setGiftTitleLength(sp.getInt(FIELD_LIMIT_GIFT_TITLE_LENGTH, 20));
        limit.setPromiseContentLength(sp.getInt(FIELD_LIMIT_PROMISE_CONTENT_LENGTH, 200));
        limit.setPromiseBreakContentLength(sp.getInt(FIELD_LIMIT_PROMISE_BREAK_CONTENT_LENGTH, 100));
        limit.setAngryContentLength(sp.getInt(FIELD_LIMIT_ANGRY_CONTENT_LENGTH, 200));
        limit.setDreamContentLength(sp.getInt(FIELD_LIMIT_DREAM_CONTENT_LENGTH, 1000));
        limit.setAwardReasonLength(sp.getInt(FIELD_LIMIT_AWARD_REASON_LENGTH, 10));
        limit.setVipExpireDay(sp.getInt(FIELD_LIMIT_VIP_EXPIRE_DAYS, 30));
        return limit;
    }

    /**
     * ***********************************VipLimit***********************************
     */
    public static void setVipLimit(VipLimit vipLimit) {
        clearVipLimit();
        if (vipLimit == null) {
            LogUtils.i(SPHelper.class, "setVipLimit", "vipLimit == null");
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
        editor.putInt(FIELD_VIP_LIMIT_AUDIO_TOTAL_COUNT, vipLimit.getAudioTotalCount());
        editor.putLong(FIELD_VIP_LIMIT_VIDEO_SIZE, vipLimit.getVideoSize());
        editor.putInt(FIELD_VIP_LIMIT_VIDEO_TOTAL_COUNT, vipLimit.getVideoTotalCount());
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
        vipLimit.setAudioTotalCount(sp.getInt(FIELD_VIP_LIMIT_AUDIO_TOTAL_COUNT, 0));
        vipLimit.setVideoSize(sp.getLong(FIELD_VIP_LIMIT_VIDEO_SIZE, 0));
        vipLimit.setVideoTotalCount(sp.getInt(FIELD_VIP_LIMIT_VIDEO_TOTAL_COUNT, 0));
        vipLimit.setTopicImageEnable(sp.getBoolean(FIELD_VIP_LIMIT_TOPIC_IMG, false));
        return vipLimit;
    }

    /**
     * ***********************************CommonConst***********************************
     */
    public static void setCommonConst(CommonConst commonConst) {
        if (commonConst == null) {
            LogUtils.i(SPHelper.class, "setCommonConst", "commonConst == null");
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

    /**
     * ***********************************Version***********************************
     */
    public static void setVersion(Version version) {
        clearVersion();
        if (version == null) {
            LogUtils.i(SPHelper.class, "setVersion", "version == null");
            return;
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

    /**
     * ***********************************Me***********************************
     */
    public static void setMe(User user) {
        clearMe();
        clearCouple();
        if (user == null) {
            LogUtils.i(SPHelper.class, "setMe", "user == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_ME).edit();
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

    public static User getMe() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_ME);
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

    /**
     * ***********************************Ta***********************************
     */
    public static void setTa(User ta) {
        clearTa();
        if (ta == null) {
            LogUtils.i(SPHelper.class, "setTa", "ta == null");
            return;
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

    /**
     * ***********************************Couple***********************************
     */
    public static void setCouple(Couple couple) {
        clearCouple();
        if (Couple.isEmpty(couple)) {
            LogUtils.i(SPHelper.class, "setCouple", "couple == null");
            return;
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

    public static void setTogetherDay(int days) {
        LogUtils.d(SPHelper.class, "setTogetherDay", String.valueOf(days));
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COUPLE_TOGETHER).edit();
        editor.putInt(FIELD_COUPLE_TOGETHER_DAY, days);
        editor.apply();
    }

    public static int getTogetherDay() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COUPLE_TOGETHER);
        int togetherDay = sp.getInt(FIELD_COUPLE_TOGETHER_DAY, 1);
        if (togetherDay <= 0) {
            togetherDay = 1;
        }
        return togetherDay;
    }

    /**
     * ***********************************WallPaper***********************************
     */
    public static void setWallPaper(WallPaper wallPaper) {
        clearWallPaper();
        if (wallPaper == null) {
            LogUtils.i(SPHelper.class, "setWallPaper", "wallPaper == null");
            return;
        }
        String json = GsonHelper.get().toJson(wallPaper);

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

    /**
     * ***********************************Draft***********************************
     */
    public static void setDraftDiary(Diary diary) {
        if (diary == null) {
            diary = new Diary();
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_DRAFT).edit();
        editor.putLong(FIELD_DRAFT_DIARY_HAPPEN, diary.getHappenAt());
        editor.putString(FIELD_DRAFT_DIARY_CONTENT_TEXT, diary.getContentText());
        editor.apply();
    }

    public static Diary getDraftDiary() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_DRAFT);
        Diary diary = new Diary();
        diary.setHappenAt(sp.getLong(FIELD_DRAFT_DIARY_HAPPEN, 0));
        diary.setContentText(sp.getString(FIELD_DRAFT_DIARY_CONTENT_TEXT, ""));
        if (diary.getHappenAt() == 0 && StringUtils.isEmpty(diary.getContentText())) {
            return null;
        }
        return diary;
    }

    public static void setDraftDream(Dream dream) {
        if (dream == null) {
            dream = new Dream();
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_DRAFT).edit();
        editor.putLong(FIELD_DRAFT_DREAM_HAPPEN, dream.getHappenAt());
        editor.putString(FIELD_DRAFT_DREAM_CONTENT_TEXT, dream.getContentText());
        editor.apply();
    }

    public static Dream getDraftDream() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_DRAFT);
        Dream dream = new Dream();
        dream.setHappenAt(sp.getLong(FIELD_DRAFT_DREAM_HAPPEN, 0));
        dream.setContentText(sp.getString(FIELD_DRAFT_DREAM_CONTENT_TEXT, ""));
        if (dream.getHappenAt() == 0 && StringUtils.isEmpty(dream.getContentText())) {
            return null;
        }
        return dream;
    }

}
