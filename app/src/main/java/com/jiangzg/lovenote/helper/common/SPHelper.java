package com.jiangzg.lovenote.helper.common;

import android.content.SharedPreferences;

import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.SPUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.lovenote.helper.view.ThemeHelper;
import com.jiangzg.lovenote.model.engine.NoteCustom;
import com.jiangzg.lovenote.model.entity.AdInfo;
import com.jiangzg.lovenote.model.entity.CommonConst;
import com.jiangzg.lovenote.model.entity.CommonCount;
import com.jiangzg.lovenote.model.entity.Cooperation;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.CoupleState;
import com.jiangzg.lovenote.model.entity.Diary;
import com.jiangzg.lovenote.model.entity.Dream;
import com.jiangzg.lovenote.model.entity.Limit;
import com.jiangzg.lovenote.model.entity.ModelShow;
import com.jiangzg.lovenote.model.entity.OssInfo;
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.model.entity.PushInfo;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.model.entity.Version;
import com.jiangzg.lovenote.model.entity.VipLimit;
import com.jiangzg.lovenote.model.entity.WallPaper;

/**
 * Created by Fan on 2017/3/2.
 * Preferences 帮助类
 */
public class SPHelper {

    // 存储集合
    private static final String SHARE_COMMON = "share_common";
    private static final String SHARE_NOTE_CUSTOM = "share_note_custom";
    private static final String SHARE_OSS_INFO = "share_oss_info";
    private static final String SHARE_PUSH_INFO = "share_push_info";
    private static final String SHARE_AD_INFO = "share_ad_info";
    private static final String SHARE_MODEL_SHOW = "share_model_show";
    private static final String SHARE_COOPERATION = "share_cooperation";
    private static final String SHARE_LIMIT = "share_limit";
    private static final String SHARE_VIP_LIMIT = "share_vip_limit";
    private static final String SHARE_COMMON_CONST = "share_common_const";
    private static final String SHARE_COMMON_COUNT = "share_common_count";
    private static final String SHARE_VERSION = "share_version";
    private static final String SHARE_ME = "share_me";
    private static final String SHARE_TA = "share_ta";
    private static final String SHARE_COUPLE = "share_couple";
    private static final String SHARE_WALL_PAPER = "share_wall_paper";
    private static final String SHARE_DRAFT = "share_draft";

    // common
    private static final String FIELD_COMMON_THEME = "theme";
    private static final String FIELD_COMMON_TOPIC_MODEL = "topic_model";
    private static final String FIELD_COMMON_NOTICE_SYSTEM = "notice_system";
    private static final String FIELD_COMMON_NOTICE_SOCIAL = "notice_social";
    private static final String FIELD_COMMON_NOTICE_DISTURB = "notice_disturb";
    // modelShow
    private static final String FIELD_MODEL_SHOW_MARKET_PAY = "market_pay";
    private static final String FIELD_MODEL_SHOW_MARKET_COIN_AD = "market_coin_ad";
    private static final String FIELD_MODEL_SHOW_COUPLE = "couple";
    private static final String FIELD_MODEL_SHOW_COUPLE_PLACE = "couple_place";
    private static final String FIELD_MODEL_SHOW_COUPLE_WEATHER = "couple_weather";
    private static final String FIELD_MODEL_SHOW_NOTE = "note";
    private static final String FIELD_MODEL_SHOW_TOPIC = "topic";
    private static final String FIELD_MODEL_SHOW_MORE = "more";
    private static final String FIELD_MODEL_SHOW_MORE_VIP = "more_vip";
    private static final String FIELD_MODEL_SHOW_MORE_COIN = "more_coin";
    private static final String FIELD_MODEL_SHOW_MORE_MATCH = "more_match";
    private static final String FIELD_MODEL_SHOW_MORE_FEATURE = "more_feature";
    // cooperation
    private static final String FIELD_COOPERATION_CATCH_BABY_ENABLE = "catch_baby_enable";
    private static final String FIELD_COOPERATION_CATCH_BABY_URL = "catch_baby_url";
    private static final String FIELD_COOPERATION_CATCH_BABY_DESC = "catch_baby_desc";
    // noteCustom
    private static final String FIELD_NOTE_CUSTOM_SOUVENIR = "souvenir";
    private static final String FIELD_NOTE_CUSTOM_SHY = "shy";
    private static final String FIELD_NOTE_CUSTOM_MENSES = "menses";
    private static final String FIELD_NOTE_CUSTOM_SLEEP = "sleep";
    private static final String FIELD_NOTE_CUSTOM_WORD = "word";
    private static final String FIELD_NOTE_CUSTOM_WHISPER = "whisper";
    private static final String FIELD_NOTE_CUSTOM_DIARY = "diary";
    private static final String FIELD_NOTE_CUSTOM_AWARD = "award";
    private static final String FIELD_NOTE_CUSTOM_DREAM = "dream";
    private static final String FIELD_NOTE_CUSTOM_MOVIE = "movie";
    private static final String FIELD_NOTE_CUSTOM_FOOD = "food";
    private static final String FIELD_NOTE_CUSTOM_TRAVEL = "travel";
    private static final String FIELD_NOTE_CUSTOM_ANGRY = "angry";
    private static final String FIELD_NOTE_CUSTOM_GIFT = "gift";
    private static final String FIELD_NOTE_CUSTOM_PROMISE = "promise";
    private static final String FIELD_NOTE_CUSTOM_AUDIO = "audio";
    private static final String FIELD_NOTE_CUSTOM_VIDEO = "video";
    private static final String FIELD_NOTE_CUSTOM_ALBUM = "album";
    private static final String FIELD_NOTE_CUSTOM_TOTAL = "total";
    private static final String FIELD_NOTE_CUSTOM_TRENDS = "trends";
    private static final String FIELD_NOTE_CUSTOM_CUSTOM = "custom";
    // commonConst
    private static final String FIELD_COMMON_CONST_COMPANY_NAME = "company_name";
    private static final String FIELD_COMMON_CONST_CUSTOMER_QQ = "customer_qq";
    private static final String FIELD_COMMON_CONST_OFFICIAL_GROUP = "official_group";
    private static final String FIELD_COMMON_CONST_OFFICIAL_WEIBO = "official_weibo";
    private static final String FIELD_COMMON_CONST_OFFICIAL_WEB = "official_web";
    private static final String FIELD_COMMON_CONST_CONTACT_EMAIL = "contact_email";
    // commonCount
    private static final String FIELD_COMMON_COUNT_NOTICE_NEW_COUNT = "notice_new_count";
    private static final String FIELD_COMMON_COUNT_VERSION_NEW_COUNT = "version_new_count";
    private static final String FIELD_COMMON_COUNT_NOTE_TRENDS_NEW_COUNT = "note_trends_new_count";
    private static final String FIELD_COMMON_COUNT_TOPIC_MSG_NEW_COUNT = "topic_msg_new_count";
    // ossInfo
    private static final String FIELD_OSS_SECURITY_TOKEN = "security_token";
    private static final String FIELD_OSS_KEY_ID = "access_key_id";
    private static final String FIELD_OSS_KEY_SECRET = "access_key_secret";
    private static final String FIELD_OSS_REGION = "region";
    private static final String FIELD_OSS_DOMAIN = "domain";
    private static final String FIELD_OSS_BUCKET = "bucket";
    private static final String FIELD_OSS_STS_EXPIRE_TIME = "sts_expire_time";
    private static final String FIELD_OSS_OSS_REFRESH_SEC = "oss_refresh_sec";
    private static final String FIELD_OSS_URL_EXPIRE_SEC = "url_expire_sec";
    private static final String FIELD_OSS_PATH_LOG = "path_log";
    private static final String FIELD_OSS_PATH_SUGGEST = "path_suggest";
    private static final String FIELD_OSS_PATH_COUPLE_AVATAR = "path_couple_avatar";
    private static final String FIELD_OSS_PATH_COUPLE_WALL = "path_couple_wall";
    private static final String FIELD_OSS_PATH_NOTE_WHISPER = "path_note_whisper";
    private static final String FIELD_OSS_PATH_NOTE_DIARY = "path_note_diary";
    private static final String FIELD_OSS_PATH_NOTE_ALBUM = "path_note_album";
    private static final String FIELD_OSS_PATH_NOTE_PICTURE = "path_note_picture";
    private static final String FIELD_OSS_PATH_NOTE_AUDIO = "path_note_audio";
    private static final String FIELD_OSS_PATH_NOTE_VIDEO = "path_note_video";
    private static final String FIELD_OSS_PATH_NOTE_VIDEO_THUMB = "path_note_video_thumb";
    private static final String FIELD_OSS_PATH_NOTE_FOOD = "path_note_food";
    private static final String FIELD_OSS_PATH_NOTE_GIFT = "path_note_gift";
    private static final String FIELD_OSS_PATH_NOTE_MOVIE = "path_note_movie";
    private static final String FIELD_OSS_PATH_TOPIC_POST = "path_topic_post";
    private static final String FIELD_OSS_PATH_MORE_MATCH = "path_more_match";
    // pushInfo
    private static final String FIELD_PUSH_ALI_APP_KEY = "ali_app_key";
    private static final String FIELD_PUSH_ALI_APP_SECRET = "ali_app_secret";
    private static final String FIELD_PUSH_MI_APP_ID = "mi_app_id";
    private static final String FIELD_PUSH_MI_APP_KEY = "mi_app_key";
    private static final String FIELD_PUSH_OPPO_APP_KEY = "oppo_app_key";
    private static final String FIELD_PUSH_OPPO_APP_SECRET = "oppo_app_secret";
    private static final String FIELD_PUSH_CHANNEL_ID = "channel_id";
    private static final String FIELD_PUSH_NOTICE_LIGHT = "notice_light";
    private static final String FIELD_PUSH_NOTICE_SOUND = "notice_sound";
    private static final String FIELD_PUSH_NOTICE_VIBRATE = "notice_vibrate";
    private static final String FIELD_PUSH_NO_START_HOUR = "no_start_hour";
    private static final String FIELD_PUSH_NO_END_HOUR = "no_end_hour";
    // adInfo
    private static final String FIELD_AD_APP_ID = "app_id";
    private static final String FIELD_AD_TOPIC_POST_POS_ID = "topic_post_pos_id";
    private static final String FIELD_AD_TOPIC_POST_JUMP = "topic_post_jump";
    private static final String FIELD_AD_TOPIC_POST_START = "topic_post_start";
    private static final String FIELD_AD_COIN_FREE_POS_ID = "coin_free_pos_id";
    private static final String FIELD_AD_COIN_FREE_TICK_SEC = "coin_free_tick_sec";
    // limit
    private static final String FIELD_LIMIT_SMS_CODE_LENGTH = "sms_code_length";
    private static final String FIELD_LIMIT_SMS_BETWEEN = "sms_between";
    private static final String FIELD_LIMIT_SUGGEST_TITLE_LENGTH = "suggest_title_length";
    private static final String FIELD_LIMIT_SUGGEST_CONTENT_LENGTH = "suggest_content_length";
    private static final String FIELD_LIMIT_SUGGEST_COMMENT_CONTENT_LENGTH = "suggest_comment_content_length";
    private static final String FIELD_LIMIT_COUPLE_INVITE_INTERVAL_SEC = "couple_invite_interval_sec";
    private static final String FIELD_LIMIT_COUPLE_BREAK_NEED_SEC = "couple_break_need_sec";
    private static final String FIELD_LIMIT_COUPLE_BREAK_SEC = "couple_break_sec";
    private static final String FIELD_LIMIT_COUPLE_NAME_LENGTH = "couple_name_length";
    private static final String FIELD_LIMIT_NOTE_OSS_EXPIRE_SECONDS = "note_oss_expire_seconds";
    private static final String FIELD_LIMIT_MENSES_MAX_PER_MONTH = "menses_max_per_month";
    private static final String FIELD_LIMIT_MENSES_MAX_CYCLE_DAY = "menses_max_cycle_day";
    private static final String FIELD_LIMIT_MENSES_MAX_DURATION_DAY = "menses_max_duration_day";
    private static final String FIELD_LIMIT_MENSES_DEFAULT_CYCLE_DAY = "menses_default_cycle_day";
    private static final String FIELD_LIMIT_MENSES_DEFAULT_DURATION_DAY = "menses_default_duration_day";
    private static final String FIELD_LIMIT_SHY_MAX_PER_DAY = "shy_max_per_day";
    private static final String FIELD_LIMIT_SHY_SAFE_LENGTH = "shy_safe_length";
    private static final String FIELD_LIMIT_SHY_DESC_LENGTH = "shy_desc_length";
    private static final String FIELD_LIMIT_SLEEP_MAX_PER_DAY = "sleep_max_per_day";
    private static final String FIELD_LIMIT_NOTE_SLEEP_SUCCESS_MIN_SEC = "note_sleep_success_min_sec";
    private static final String FIELD_LIMIT_NOTE_SLEEP_SUCCESS_MAX_SEC = "note_sleep_success_max_sec";
    private static final String FIELD_LIMIT_NOTE_LOCK_LENGTH = "note_lock_length";
    private static final String FIELD_LIMIT_SOUVENIR_TITLE_LENGTH = "souvenir_title_length";
    private static final String FIELD_LIMIT_SOUVENIR_FOREIGN_YEAR_COUNT = "souvenir_foreign_year_count";
    private static final String FIELD_LIMIT_TRAVEL_PLACE_COUNT = "travel_place_count";
    private static final String FIELD_LIMIT_TRAVEL_VIDEO_COUNT = "travel_video_count";
    private static final String FIELD_LIMIT_TRAVEL_FOOD_COUNT = "travel_food_count";
    private static final String FIELD_LIMIT_TRAVEL_MOVIE_COUNT = "travel_movie_count";
    private static final String FIELD_LIMIT_TRAVEL_ALBUM_COUNT = "travel_album_count";
    private static final String FIELD_LIMIT_TRAVEL_DIARY_COUNT = "travel_diary_count";
    private static final String FIELD_LIMIT_WHISPER_CONTENT_LENGTH = "whisper_content_length";
    private static final String FIELD_LIMIT_WHISPER_CHANNEL_LENGTH = "whisper_channel_length";
    private static final String FIELD_LIMIT_WORD_CONTENT_LENGTH = "word_content_length";
    private static final String FIELD_LIMIT_DIARY_CONTENT_LENGTH = "diary_content_length";
    private static final String FIELD_LIMIT_ALBUM_TITLE_LENGTH = "album_title_length";
    private static final String FIELD_LIMIT_PICTURE_PUSH_COUNT = "picture_push_count";
    private static final String FIELD_LIMIT_AUDIO_TITLE_LENGTH = "audio_title_length";
    private static final String FIELD_LIMIT_VIDEO_TITLE_LENGTH = "video_title_length";
    private static final String FIELD_LIMIT_FOOD_TITLE_LENGTH = "food_title_length";
    private static final String FIELD_LIMIT_FOOD_CONTENT_LENGTH = "food_content_length";
    private static final String FIELD_LIMIT_TRAVEL_TITLE_LENGTH = "travel_title_length";
    private static final String FIELD_LIMIT_TRAVEL_PLACE_CONTENT_LENGTH = "travel_place_content_length";
    private static final String FIELD_LIMIT_GIFT_TITLE_LENGTH = "gift_title_length";
    private static final String FIELD_LIMIT_PROMISE_CONTENT_LENGTH = "promise_content_length";
    private static final String FIELD_LIMIT_PROMISE_BREAK_CONTENT_LENGTH = "promise_break_content_length";
    private static final String FIELD_LIMIT_ANGRY_CONTENT_LENGTH = "angry_content_length";
    private static final String FIELD_LIMIT_DREAM_CONTENT_LENGTH = "dream_content_length";
    private static final String FIELD_LIMIT_AWARD_CONTENT_LENGTH = "award_content_length";
    private static final String FIELD_LIMIT_AWARD_RULE_TITLE_LENGTH = "award_rule_title_length";
    private static final String FIELD_LIMIT_AWARD_RULE_SCORE_MAX = "award_rule_score_max";
    private static final String FIELD_LIMIT_MOVIE_TITLE_LENGTH = "movie_title_length";
    private static final String FIELD_LIMIT_MOVIE_CONTENT_LENGTH = "movie_content_length";
    private static final String FIELD_LIMIT_POST_TITLE_LENGTH = "post_title_length";
    private static final String FIELD_LIMIT_POST_CONTENT_LENGTH = "post_content_length";
    private static final String FIELD_LIMIT_POST_SCREEN_REPORT_COUNT = "post_screen_report_count";
    private static final String FIELD_LIMIT_POST_COMMENT_CONTENT_LENGTH = "post_comment_content_length";
    private static final String FIELD_LIMIT_POST_COMMENT_SCREEN_REPORT_COUNT = "post_comment_screen_report_count";
    private static final String FIELD_LIMIT_PAY_VIP_GOODS_1_TITLE = "pay_vip_goods_1_title";
    private static final String FIELD_LIMIT_PAY_VIP_GOODS_1_DAYS = "pay_vip_goods_1_days";
    private static final String FIELD_LIMIT_PAY_VIP_GOODS_1_AMOUNT = "pay_vip_goods_1_amount";
    private static final String FIELD_LIMIT_PAY_VIP_GOODS_2_TITLE = "pay_vip_goods_2_title";
    private static final String FIELD_LIMIT_PAY_VIP_GOODS_2_DAYS = "pay_vip_goods_2_days";
    private static final String FIELD_LIMIT_PAY_VIP_GOODS_2_AMOUNT = "pay_vip_goods_2_amount";
    private static final String FIELD_LIMIT_PAY_VIP_GOODS_3_TITLE = "pay_vip_goods_3_title";
    private static final String FIELD_LIMIT_PAY_VIP_GOODS_3_DAYS = "pay_vip_goods_3_days";
    private static final String FIELD_LIMIT_PAY_VIP_GOODS_3_AMOUNT = "pay_vip_goods_3_amount";
    private static final String FIELD_LIMIT_PAY_COIN_GOODS_1_TITLE = "pay_coin_goods_1_title";
    private static final String FIELD_LIMIT_PAY_COIN_GOODS_1_COUNT = "pay_coin_goods_1_count";
    private static final String FIELD_LIMIT_PAY_COIN_GOODS_1_AMOUNT = "pay_coin_goods_1_amount";
    private static final String FIELD_LIMIT_PAY_COIN_GOODS_2_TITLE = "pay_coin_goods_2_title";
    private static final String FIELD_LIMIT_PAY_COIN_GOODS_2_COUNT = "pay_coin_goods_2_count";
    private static final String FIELD_LIMIT_PAY_COIN_GOODS_2_AMOUNT = "pay_coin_goods_2_amount";
    private static final String FIELD_LIMIT_PAY_COIN_GOODS_3_TITLE = "pay_coin_goods_3_title";
    private static final String FIELD_LIMIT_PAY_COIN_GOODS_3_COUNT = "pay_coin_goods_3_count";
    private static final String FIELD_LIMIT_PAY_COIN_GOODS_3_AMOUNT = "pay_coin_goods_3_amount";
    private static final String FIELD_LIMIT_COIN_SIGN_MIN_COUNT = "coin_sign_min_count";
    private static final String FIELD_LIMIT_COIN_SIGN_MAX_COUNT = "coin_sign_max_count";
    private static final String FIELD_LIMIT_COIN_SIGN_INCREASE_COUNT = "coin_sign_increase_count";
    private static final String FIELD_LIMIT_COIN_AD_BETWEEN_SEC = "coin_ad_between_sec";
    private static final String FIELD_LIMIT_COIN_AD_WATCH_COUNT = "coin_ad_watch_count";
    private static final String FIELD_LIMIT_COIN_AD_CLICK_COUNT = "coin_ad_click_count";
    private static final String FIELD_LIMIT_COIN_AD_MAX_PER_DAY_COUNT = "coin_ad_max_per_day_count";
    private static final String FIELD_LIMIT_MATCH_WORK_SCREEN_REPORT_COUNT = "match_work_screen_report_count";
    private static final String FIELD_LIMIT_MATCH_WORK_TITLE_LENGTH = "match_work_title_length";
    private static final String FIELD_LIMIT_MATCH_WORK_CONTENT_LENGTH = "match_work_content_length";
    // vipLimit
    private static final String FIELD_VIP_LIMIT_ADVERTISE_HIDE = "advertise_hide";
    private static final String FIELD_VIP_LIMIT_WALL_PAPER_SIZE = "wall_paper_size";
    private static final String FIELD_VIP_LIMIT_WALL_PAPER_COUNT = "wall_paper_count";
    private static final String FIELD_VIP_LIMIT_TRENDS_TOTAL_ENABLE = "note_total_enable";
    private static final String FIELD_VIP_LIMIT_SOUVENIR_COUNT = "souvenir_count";
    private static final String FIELD_VIP_LIMIT_WHISPER_IMG_ENABLE = "whisper_image_enable";
    private static final String FIELD_VIP_LIMIT_MOVIE_IMG_COUNT = "movie_image_count";
    private static final String FIELD_VIP_LIMIT_FOOD_IMG_COUNT = "food_image_count";
    private static final String FIELD_VIP_LIMIT_GIFT_IMG_COUNT = "gift_image_count";
    private static final String FIELD_VIP_LIMIT_DIARY_IMG_SIZE = "diary_image_size";
    private static final String FIELD_VIP_LIMIT_DIARY_IMG_COUNT = "diary_image_count";
    private static final String FIELD_VIP_LIMIT_AUDIO_SIZE = "audio_size";
    private static final String FIELD_VIP_LIMIT_VIDEO_SIZE = "video_size";
    private static final String FIELD_VIP_LIMIT_PICTURE_SIZE = "picture_size";
    private static final String FIELD_VIP_LIMIT_PICTURE_ORIGINAL = "picture_original";
    private static final String FIELD_VIP_LIMIT_PICTURE_TOTAL_COUNT = "picture_total_count";
    private static final String FIELD_VIP_LIMIT_TOPIC_POST_IMAGE_COUNT = "topic_post_image_count";
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
    private static final String FIELD_USER_TOKEN = "user_token";
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
    private static final String FIELD_CP_TOGETHER_AT = "together_at";
    private static final String FIELD_CP_STATE_ID = "state_id";
    private static final String FIELD_CP_STATE_CREATE_AT = "state_create_at";
    private static final String FIELD_CP_STATE_UPDATE_AT = "state_update_at";
    private static final String FIELD_CP_STATE_USER_ID = "state_user_id";
    private static final String FIELD_CP_STATE_STATE = "state_state";
    // wallPaper
    private static final String FIELD_WALL_PAPER_JSON = "json";
    // draft
    private static final String FIELD_DRAFT_DIARY_CONTENT_TEXT = "diary_content_text";
    private static final String FIELD_DRAFT_DREAM_CONTENT_TEXT = "dream_content_text";
    private static final String FIELD_DRAFT_POST_TITLE = "post_title";
    private static final String FIELD_DRAFT_POST_CONTENT_TEXT = "post_content_text";

    /**
     * ***********************************清除***********************************
     */
    public static void clearAll() {
        clearCommonCount();
        clearOssInfo();
        clearVipLimit();
        clearVersion();
        clearMe();
        clearTa();
        clearCouple();
        clearWallPaper();
        clearDraft();
    }

    public static void clearCommonCount() {
        SPUtils.clear(SHARE_COMMON_COUNT);
    }

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

    public static void setSettingsTopicModel(boolean open) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COMMON).edit();
        editor.putBoolean(FIELD_COMMON_TOPIC_MODEL, open);
        editor.apply();
    }

    public static boolean getSettingsTopicModel() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON);
        return sp.getBoolean(FIELD_COMMON_TOPIC_MODEL, true);
    }

    public static void setSettingsNoticeSystem(boolean open) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COMMON).edit();
        editor.putBoolean(FIELD_COMMON_NOTICE_SYSTEM, open);
        editor.apply();
    }

    public static boolean getSettingsNoticeSystem() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON);
        return sp.getBoolean(FIELD_COMMON_NOTICE_SYSTEM, true);
    }

    public static void setSettingsNoticeSocial(boolean open) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COMMON).edit();
        editor.putBoolean(FIELD_COMMON_NOTICE_SOCIAL, open);
        editor.apply();
    }

    public static boolean getSettingsNoticeSocial() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON);
        return sp.getBoolean(FIELD_COMMON_NOTICE_SOCIAL, true);
    }

    public static void setSettingsNoticeDisturb(boolean open) {
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COMMON).edit();
        editor.putBoolean(FIELD_COMMON_NOTICE_DISTURB, open);
        editor.apply();
    }

    public static boolean getSettingsNoticeDisturb() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON);
        return sp.getBoolean(FIELD_COMMON_NOTICE_DISTURB, true);
    }

    /**
     * ***********************************ModelShow***********************************
     */
    public static void setModelShow(ModelShow modelShow) {
        if (modelShow == null) {
            LogUtils.i(SPHelper.class, "setModelShow", "modelShow == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_MODEL_SHOW).edit();
        editor.putBoolean(FIELD_MODEL_SHOW_MARKET_PAY, modelShow.isMarketPay());
        editor.putBoolean(FIELD_MODEL_SHOW_MARKET_COIN_AD, modelShow.isMarketCoinAd());
        editor.putBoolean(FIELD_MODEL_SHOW_COUPLE, modelShow.isCouple());
        editor.putBoolean(FIELD_MODEL_SHOW_COUPLE_PLACE, modelShow.isCouplePlace());
        editor.putBoolean(FIELD_MODEL_SHOW_COUPLE_WEATHER, modelShow.isCoupleWeather());
        editor.putBoolean(FIELD_MODEL_SHOW_NOTE, modelShow.isNote());
        editor.putBoolean(FIELD_MODEL_SHOW_TOPIC, modelShow.isTopic());
        editor.putBoolean(FIELD_MODEL_SHOW_MORE, modelShow.isMore());
        editor.putBoolean(FIELD_MODEL_SHOW_MORE_VIP, modelShow.isMoreVip());
        editor.putBoolean(FIELD_MODEL_SHOW_MORE_COIN, modelShow.isMoreCoin());
        editor.putBoolean(FIELD_MODEL_SHOW_MORE_MATCH, modelShow.isMoreMatch());
        editor.putBoolean(FIELD_MODEL_SHOW_MORE_FEATURE, modelShow.isMoreFeature());
        editor.apply();
    }

    public static ModelShow getModelShow() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_MODEL_SHOW);
        ModelShow modelShow = new ModelShow();
        modelShow.setMarketPay(sp.getBoolean(FIELD_MODEL_SHOW_MARKET_PAY, true));
        modelShow.setMarketCoinAd(sp.getBoolean(FIELD_MODEL_SHOW_MARKET_COIN_AD, true));
        modelShow.setCouple(sp.getBoolean(FIELD_MODEL_SHOW_COUPLE, true));
        modelShow.setCouplePlace(sp.getBoolean(FIELD_MODEL_SHOW_COUPLE_PLACE, true));
        modelShow.setCoupleWeather(sp.getBoolean(FIELD_MODEL_SHOW_COUPLE_WEATHER, true));
        modelShow.setNote(sp.getBoolean(FIELD_MODEL_SHOW_NOTE, true));
        modelShow.setTopic(sp.getBoolean(FIELD_MODEL_SHOW_TOPIC, true));
        modelShow.setMore(sp.getBoolean(FIELD_MODEL_SHOW_MORE, true));
        modelShow.setMoreVip(sp.getBoolean(FIELD_MODEL_SHOW_MORE_VIP, true));
        modelShow.setMoreCoin(sp.getBoolean(FIELD_MODEL_SHOW_MORE_COIN, true));
        modelShow.setMoreMatch(sp.getBoolean(FIELD_MODEL_SHOW_MORE_MATCH, true));
        modelShow.setMoreFeature(sp.getBoolean(FIELD_MODEL_SHOW_MORE_FEATURE, true));
        return modelShow;
    }

    /**
     * ***********************************Cooperation***********************************
     */
    public static void setCooperation(Cooperation cooperation) {
        if (cooperation == null) {
            LogUtils.i(SPHelper.class, "setCooperation", "cooperation == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COOPERATION).edit();
        editor.putBoolean(FIELD_COOPERATION_CATCH_BABY_ENABLE, cooperation.isCatchBabyEnable());
        editor.putString(FIELD_COOPERATION_CATCH_BABY_URL, cooperation.getCatchBabyUrl());
        editor.putString(FIELD_COOPERATION_CATCH_BABY_DESC, cooperation.getCatchBabyDesc());
        editor.apply();
    }

    public static Cooperation getCooperation() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COOPERATION);
        Cooperation cooperation = new Cooperation();
        cooperation.setCatchBabyEnable(sp.getBoolean(FIELD_COOPERATION_CATCH_BABY_ENABLE, true));
        cooperation.setCatchBabyUrl(sp.getString(FIELD_COOPERATION_CATCH_BABY_URL, ""));
        cooperation.setCatchBabyDesc(sp.getString(FIELD_COOPERATION_CATCH_BABY_DESC, ""));
        return cooperation;
    }

    /**
     * ***********************************NoteCustom***********************************
     */
    public static void setNoteCustom(NoteCustom noteCustom) {
        if (noteCustom == null) {
            LogUtils.i(SPHelper.class, "setNoteCustom", "noteCustom == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_NOTE_CUSTOM).edit();
        editor.putBoolean(FIELD_NOTE_CUSTOM_SOUVENIR, noteCustom.isSouvenir());
        editor.putBoolean(FIELD_NOTE_CUSTOM_SHY, noteCustom.isShy());
        editor.putBoolean(FIELD_NOTE_CUSTOM_MENSES, noteCustom.isMenses());
        editor.putBoolean(FIELD_NOTE_CUSTOM_SLEEP, noteCustom.isSleep());
        editor.putBoolean(FIELD_NOTE_CUSTOM_WORD, noteCustom.isWord());
        editor.putBoolean(FIELD_NOTE_CUSTOM_WHISPER, noteCustom.isWhisper());
        editor.putBoolean(FIELD_NOTE_CUSTOM_DIARY, noteCustom.isDiary());
        editor.putBoolean(FIELD_NOTE_CUSTOM_AWARD, noteCustom.isAward());
        editor.putBoolean(FIELD_NOTE_CUSTOM_DREAM, noteCustom.isDream());
        editor.putBoolean(FIELD_NOTE_CUSTOM_MOVIE, noteCustom.isMovie());
        editor.putBoolean(FIELD_NOTE_CUSTOM_FOOD, noteCustom.isFood());
        editor.putBoolean(FIELD_NOTE_CUSTOM_TRAVEL, noteCustom.isTravel());
        editor.putBoolean(FIELD_NOTE_CUSTOM_ANGRY, noteCustom.isAngry());
        editor.putBoolean(FIELD_NOTE_CUSTOM_GIFT, noteCustom.isGift());
        editor.putBoolean(FIELD_NOTE_CUSTOM_PROMISE, noteCustom.isPromise());
        editor.putBoolean(FIELD_NOTE_CUSTOM_AUDIO, noteCustom.isAudio());
        editor.putBoolean(FIELD_NOTE_CUSTOM_VIDEO, noteCustom.isVideo());
        editor.putBoolean(FIELD_NOTE_CUSTOM_ALBUM, noteCustom.isAlbum());
        editor.putBoolean(FIELD_NOTE_CUSTOM_TOTAL, noteCustom.isTotal());
        editor.putBoolean(FIELD_NOTE_CUSTOM_TRENDS, noteCustom.isTrends());
        editor.putBoolean(FIELD_NOTE_CUSTOM_CUSTOM, noteCustom.isCustom());
        editor.apply();
    }

    public static NoteCustom getNoteCustom() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_NOTE_CUSTOM);
        NoteCustom noteCustom = new NoteCustom();
        noteCustom.setSouvenir(sp.getBoolean(FIELD_NOTE_CUSTOM_SOUVENIR, true));
        noteCustom.setShy(sp.getBoolean(FIELD_NOTE_CUSTOM_SHY, true));
        noteCustom.setMenses(sp.getBoolean(FIELD_NOTE_CUSTOM_MENSES, true));
        noteCustom.setSleep(sp.getBoolean(FIELD_NOTE_CUSTOM_SLEEP, true));
        noteCustom.setWord(sp.getBoolean(FIELD_NOTE_CUSTOM_WORD, true));
        noteCustom.setWhisper(sp.getBoolean(FIELD_NOTE_CUSTOM_WHISPER, true));
        noteCustom.setDiary(sp.getBoolean(FIELD_NOTE_CUSTOM_DIARY, true));
        noteCustom.setAward(sp.getBoolean(FIELD_NOTE_CUSTOM_AWARD, true));
        noteCustom.setDream(sp.getBoolean(FIELD_NOTE_CUSTOM_DREAM, true));
        noteCustom.setMovie(sp.getBoolean(FIELD_NOTE_CUSTOM_MOVIE, true));
        noteCustom.setFood(sp.getBoolean(FIELD_NOTE_CUSTOM_FOOD, true));
        noteCustom.setTravel(sp.getBoolean(FIELD_NOTE_CUSTOM_TRAVEL, true));
        noteCustom.setAngry(sp.getBoolean(FIELD_NOTE_CUSTOM_ANGRY, true));
        noteCustom.setGift(sp.getBoolean(FIELD_NOTE_CUSTOM_GIFT, true));
        noteCustom.setPromise(sp.getBoolean(FIELD_NOTE_CUSTOM_PROMISE, true));
        noteCustom.setAudio(sp.getBoolean(FIELD_NOTE_CUSTOM_AUDIO, true));
        noteCustom.setVideo(sp.getBoolean(FIELD_NOTE_CUSTOM_VIDEO, true));
        noteCustom.setAlbum(sp.getBoolean(FIELD_NOTE_CUSTOM_ALBUM, true));
        noteCustom.setTotal(sp.getBoolean(FIELD_NOTE_CUSTOM_TOTAL, true));
        noteCustom.setTrends(sp.getBoolean(FIELD_NOTE_CUSTOM_TRENDS, true));
        noteCustom.setCustom(sp.getBoolean(FIELD_NOTE_CUSTOM_CUSTOM, true));
        return noteCustom;
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
        editor.putString(FIELD_COMMON_CONST_CUSTOMER_QQ, commonConst.getCustomerQQ());
        editor.putString(FIELD_COMMON_CONST_OFFICIAL_GROUP, commonConst.getOfficialGroup());
        editor.putString(FIELD_COMMON_CONST_OFFICIAL_WEIBO, commonConst.getOfficialWeibo());
        editor.putString(FIELD_COMMON_CONST_OFFICIAL_WEB, commonConst.getOfficialWeb());
        editor.putString(FIELD_COMMON_CONST_CONTACT_EMAIL, commonConst.getContactEmail());
        editor.apply();
    }

    public static CommonConst getCommonConst() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON_CONST);
        CommonConst commonConst = new CommonConst();
        commonConst.setCompanyName(sp.getString(FIELD_COMMON_CONST_COMPANY_NAME, ""));
        commonConst.setCustomerQQ(sp.getString(FIELD_COMMON_CONST_CUSTOMER_QQ, ""));
        commonConst.setOfficialGroup(sp.getString(FIELD_COMMON_CONST_OFFICIAL_GROUP, ""));
        commonConst.setOfficialWeibo(sp.getString(FIELD_COMMON_CONST_OFFICIAL_WEIBO, ""));
        commonConst.setOfficialWeb(sp.getString(FIELD_COMMON_CONST_OFFICIAL_WEB, ""));
        commonConst.setContactEmail(sp.getString(FIELD_COMMON_CONST_CONTACT_EMAIL, ""));
        return commonConst;
    }

    /**
     * ***********************************CommonCount***********************************
     */
    public static void setCommonCount(CommonCount commonCount) {
        if (commonCount == null) {
            LogUtils.i(SPHelper.class, "setCommonCount", "commonCount == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COMMON_COUNT).edit();
        editor.putInt(FIELD_COMMON_COUNT_NOTICE_NEW_COUNT, commonCount.getNoticeNewCount());
        editor.putInt(FIELD_COMMON_COUNT_VERSION_NEW_COUNT, commonCount.getVersionNewCount());
        editor.putInt(FIELD_COMMON_COUNT_NOTE_TRENDS_NEW_COUNT, commonCount.getNoteTrendsNewCount());
        editor.putInt(FIELD_COMMON_COUNT_TOPIC_MSG_NEW_COUNT, commonCount.getTopicMsgNewCount());
        editor.apply();
    }

    public static CommonCount getCommonCount() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_COMMON_COUNT);
        CommonCount commonCount = new CommonCount();
        commonCount.setNoticeNewCount(sp.getInt(FIELD_COMMON_COUNT_NOTICE_NEW_COUNT, 0));
        commonCount.setVersionNewCount(sp.getInt(FIELD_COMMON_COUNT_VERSION_NEW_COUNT, 0));
        commonCount.setNoteTrendsNewCount(sp.getInt(FIELD_COMMON_COUNT_NOTE_TRENDS_NEW_COUNT, 0));
        commonCount.setTopicMsgNewCount(sp.getInt(FIELD_COMMON_COUNT_TOPIC_MSG_NEW_COUNT, 0));
        return commonCount;
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
        editor.putString(FIELD_OSS_REGION, ossInfo.getRegion());
        editor.putString(FIELD_OSS_DOMAIN, ossInfo.getDomain());
        editor.putString(FIELD_OSS_BUCKET, ossInfo.getBucket());
        editor.putLong(FIELD_OSS_STS_EXPIRE_TIME, ossInfo.getStsExpireTime());
        editor.putLong(FIELD_OSS_OSS_REFRESH_SEC, ossInfo.getOssRefreshSec());
        editor.putLong(FIELD_OSS_URL_EXPIRE_SEC, ossInfo.getUrlExpireSec());
        editor.putString(FIELD_OSS_PATH_LOG, ossInfo.getPathLog());
        editor.putString(FIELD_OSS_PATH_SUGGEST, ossInfo.getPathSuggest());
        editor.putString(FIELD_OSS_PATH_COUPLE_AVATAR, ossInfo.getPathCoupleAvatar());
        editor.putString(FIELD_OSS_PATH_COUPLE_WALL, ossInfo.getPathCoupleWall());
        editor.putString(FIELD_OSS_PATH_NOTE_WHISPER, ossInfo.getPathNoteWhisper());
        editor.putString(FIELD_OSS_PATH_NOTE_DIARY, ossInfo.getPathNoteDiary());
        editor.putString(FIELD_OSS_PATH_NOTE_ALBUM, ossInfo.getPathNoteAlbum());
        editor.putString(FIELD_OSS_PATH_NOTE_PICTURE, ossInfo.getPathNotePicture());
        editor.putString(FIELD_OSS_PATH_NOTE_AUDIO, ossInfo.getPathNoteAudio());
        editor.putString(FIELD_OSS_PATH_NOTE_VIDEO, ossInfo.getPathNoteVideo());
        editor.putString(FIELD_OSS_PATH_NOTE_VIDEO_THUMB, ossInfo.getPathNoteVideoThumb());
        editor.putString(FIELD_OSS_PATH_NOTE_FOOD, ossInfo.getPathNoteFood());
        editor.putString(FIELD_OSS_PATH_NOTE_GIFT, ossInfo.getPathNoteGift());
        editor.putString(FIELD_OSS_PATH_NOTE_MOVIE, ossInfo.getPathNoteMovie());
        editor.putString(FIELD_OSS_PATH_TOPIC_POST, ossInfo.getPathTopicPost());
        editor.putString(FIELD_OSS_PATH_MORE_MATCH, ossInfo.getPathMoreMatch());
        editor.apply();
    }

    public static OssInfo getOssInfo() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_OSS_INFO);
        OssInfo ossInfo = new OssInfo();
        ossInfo.setSecurityToken(sp.getString(FIELD_OSS_SECURITY_TOKEN, ""));
        ossInfo.setAccessKeyId(sp.getString(FIELD_OSS_KEY_ID, ""));
        ossInfo.setAccessKeySecret(sp.getString(FIELD_OSS_KEY_SECRET, ""));
        ossInfo.setRegion(sp.getString(FIELD_OSS_REGION, ""));
        ossInfo.setDomain(sp.getString(FIELD_OSS_DOMAIN, ""));
        ossInfo.setBucket(sp.getString(FIELD_OSS_BUCKET, ""));
        ossInfo.setStsExpireTime(sp.getLong(FIELD_OSS_STS_EXPIRE_TIME, TimeHelper.getGoTimeByJava(DateUtils.getCurrentLong()) + 60 * 30));
        ossInfo.setOssRefreshSec(sp.getLong(FIELD_OSS_OSS_REFRESH_SEC, 60 * 30));
        ossInfo.setUrlExpireSec(sp.getLong(FIELD_OSS_URL_EXPIRE_SEC, 60 * 10));
        ossInfo.setPathLog(sp.getString(FIELD_OSS_PATH_LOG, ""));
        ossInfo.setPathSuggest(sp.getString(FIELD_OSS_PATH_SUGGEST, ""));
        ossInfo.setPathCoupleAvatar(sp.getString(FIELD_OSS_PATH_COUPLE_AVATAR, ""));
        ossInfo.setPathCoupleWall(sp.getString(FIELD_OSS_PATH_COUPLE_WALL, ""));
        ossInfo.setPathNoteWhisper(sp.getString(FIELD_OSS_PATH_NOTE_WHISPER, ""));
        ossInfo.setPathNoteDiary(sp.getString(FIELD_OSS_PATH_NOTE_DIARY, ""));
        ossInfo.setPathNoteAlbum(sp.getString(FIELD_OSS_PATH_NOTE_ALBUM, ""));
        ossInfo.setPathNotePicture(sp.getString(FIELD_OSS_PATH_NOTE_PICTURE, ""));
        ossInfo.setPathNoteAudio(sp.getString(FIELD_OSS_PATH_NOTE_AUDIO, ""));
        ossInfo.setPathNoteVideo(sp.getString(FIELD_OSS_PATH_NOTE_VIDEO, ""));
        ossInfo.setPathNoteVideoThumb(sp.getString(FIELD_OSS_PATH_NOTE_VIDEO_THUMB, ""));
        ossInfo.setPathNoteFood(sp.getString(FIELD_OSS_PATH_NOTE_FOOD, ""));
        ossInfo.setPathNoteGift(sp.getString(FIELD_OSS_PATH_NOTE_GIFT, ""));
        ossInfo.setPathNoteMovie(sp.getString(FIELD_OSS_PATH_NOTE_MOVIE, ""));
        ossInfo.setPathTopicPost(sp.getString(FIELD_OSS_PATH_TOPIC_POST, ""));
        ossInfo.setPathMoreMatch(sp.getString(FIELD_OSS_PATH_MORE_MATCH, ""));
        return ossInfo;
    }

    /**
     * ***********************************PushInfo***********************************
     */
    public static void setPushInfo(PushInfo pushInfo) {
        if (pushInfo == null) {
            LogUtils.i(SPHelper.class, "setPushInfo", "pushInfo == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_PUSH_INFO).edit();
        editor.putString(FIELD_PUSH_ALI_APP_KEY, pushInfo.getAliAppKey());
        editor.putString(FIELD_PUSH_ALI_APP_SECRET, pushInfo.getAliAppSecret());
        editor.putString(FIELD_PUSH_MI_APP_ID, pushInfo.getMiAppId());
        editor.putString(FIELD_PUSH_MI_APP_KEY, pushInfo.getMiAppKey());
        editor.putString(FIELD_PUSH_OPPO_APP_KEY, pushInfo.getOppoAppKey());
        editor.putString(FIELD_PUSH_OPPO_APP_SECRET, pushInfo.getOppoAppSecret());
        editor.putString(FIELD_PUSH_CHANNEL_ID, pushInfo.getChannelId());
        editor.putBoolean(FIELD_PUSH_NOTICE_LIGHT, pushInfo.isNoticeLight());
        editor.putBoolean(FIELD_PUSH_NOTICE_SOUND, pushInfo.isNoticeSound());
        editor.putBoolean(FIELD_PUSH_NOTICE_VIBRATE, pushInfo.isNoticeVibrate());
        editor.putInt(FIELD_PUSH_NO_START_HOUR, pushInfo.getNoStartHour());
        editor.putInt(FIELD_PUSH_NO_END_HOUR, pushInfo.getNoEndHour());
        editor.apply();
    }

    public static PushInfo getPushInfo() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_PUSH_INFO);
        PushInfo pushInfo = new PushInfo();
        pushInfo.setAliAppKey(sp.getString(FIELD_PUSH_ALI_APP_KEY, ""));
        pushInfo.setAliAppSecret(sp.getString(FIELD_PUSH_ALI_APP_SECRET, ""));
        pushInfo.setMiAppId(sp.getString(FIELD_PUSH_MI_APP_ID, ""));
        pushInfo.setMiAppKey(sp.getString(FIELD_PUSH_MI_APP_KEY, ""));
        pushInfo.setOppoAppKey(sp.getString(FIELD_PUSH_OPPO_APP_KEY, ""));
        pushInfo.setOppoAppSecret(sp.getString(FIELD_PUSH_OPPO_APP_SECRET, ""));
        pushInfo.setChannelId(sp.getString(FIELD_PUSH_CHANNEL_ID, ""));
        pushInfo.setNoticeLight(sp.getBoolean(FIELD_PUSH_NOTICE_LIGHT, true));
        pushInfo.setNoticeSound(sp.getBoolean(FIELD_PUSH_NOTICE_SOUND, true));
        pushInfo.setNoticeVibrate(sp.getBoolean(FIELD_PUSH_NOTICE_VIBRATE, true));
        pushInfo.setNoStartHour(sp.getInt(FIELD_PUSH_NO_START_HOUR, 22));
        pushInfo.setNoEndHour(sp.getInt(FIELD_PUSH_NO_END_HOUR, 8));
        return pushInfo;
    }

    /**
     * ***********************************AdInfo***********************************
     */
    public static void setAdInfo(AdInfo adInfo) {
        if (adInfo == null) {
            LogUtils.i(SPHelper.class, "setAdInfo", "adInfo == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_AD_INFO).edit();
        editor.putString(FIELD_AD_APP_ID, adInfo.getAppId());
        editor.putString(FIELD_AD_TOPIC_POST_POS_ID, adInfo.getTopicPostPosId());
        editor.putInt(FIELD_AD_TOPIC_POST_JUMP, adInfo.getTopicPostJump());
        editor.putInt(FIELD_AD_TOPIC_POST_START, adInfo.getTopicPostStart());
        editor.putString(FIELD_AD_COIN_FREE_POS_ID, adInfo.getCoinFreePosId());
        editor.putInt(FIELD_AD_COIN_FREE_TICK_SEC, adInfo.getCoinFreeTickSec());
        editor.apply();
    }

    public static AdInfo getAdInfo() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_AD_INFO);
        AdInfo adInfo = new AdInfo();
        adInfo.setAppId(sp.getString(FIELD_AD_APP_ID, ""));
        adInfo.setTopicPostPosId(sp.getString(FIELD_AD_TOPIC_POST_POS_ID, ""));
        adInfo.setTopicPostJump(sp.getInt(FIELD_AD_TOPIC_POST_JUMP, 20));
        adInfo.setTopicPostStart(sp.getInt(FIELD_AD_TOPIC_POST_START, 10));
        adInfo.setCoinFreePosId(sp.getString(FIELD_AD_COIN_FREE_POS_ID, ""));
        adInfo.setCoinFreeTickSec(sp.getInt(FIELD_AD_COIN_FREE_TICK_SEC, 5));
        return adInfo;
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
        editor.putInt(FIELD_LIMIT_SMS_BETWEEN, limit.getSmsBetweenSec());
        editor.putInt(FIELD_LIMIT_SUGGEST_TITLE_LENGTH, limit.getSuggestTitleLength());
        editor.putInt(FIELD_LIMIT_SUGGEST_CONTENT_LENGTH, limit.getSuggestContentLength());
        editor.putInt(FIELD_LIMIT_SUGGEST_COMMENT_CONTENT_LENGTH, limit.getSuggestCommentContentLength());
        editor.putLong(FIELD_LIMIT_COUPLE_INVITE_INTERVAL_SEC, limit.getCoupleInviteIntervalSec());
        editor.putLong(FIELD_LIMIT_COUPLE_BREAK_NEED_SEC, limit.getCoupleBreakNeedSec());
        editor.putLong(FIELD_LIMIT_COUPLE_BREAK_SEC, limit.getCoupleBreakSec());
        editor.putInt(FIELD_LIMIT_COUPLE_NAME_LENGTH, limit.getCoupleNameLength());
        editor.putLong(FIELD_LIMIT_NOTE_OSS_EXPIRE_SECONDS, limit.getNoteResExpireSec());
        editor.putInt(FIELD_LIMIT_MENSES_MAX_PER_MONTH, limit.getMensesMaxPerMonth());
        editor.putInt(FIELD_LIMIT_MENSES_MAX_CYCLE_DAY, limit.getMensesMaxCycleDay());
        editor.putInt(FIELD_LIMIT_MENSES_MAX_DURATION_DAY, limit.getMensesMaxDurationDay());
        editor.putInt(FIELD_LIMIT_MENSES_DEFAULT_CYCLE_DAY, limit.getMensesDefaultCycleDay());
        editor.putInt(FIELD_LIMIT_MENSES_DEFAULT_DURATION_DAY, limit.getMensesDefaultDurationDay());
        editor.putInt(FIELD_LIMIT_SHY_MAX_PER_DAY, limit.getShyMaxPerDay());
        editor.putInt(FIELD_LIMIT_SHY_SAFE_LENGTH, limit.getShySafeLength());
        editor.putInt(FIELD_LIMIT_SHY_DESC_LENGTH, limit.getShyDescLength());
        editor.putInt(FIELD_LIMIT_SLEEP_MAX_PER_DAY, limit.getSleepMaxPerDay());
        editor.putLong(FIELD_LIMIT_NOTE_SLEEP_SUCCESS_MIN_SEC, limit.getNoteSleepSuccessMinSec());
        editor.putLong(FIELD_LIMIT_NOTE_SLEEP_SUCCESS_MAX_SEC, limit.getNoteSleepSuccessMaxSec());
        editor.putInt(FIELD_LIMIT_NOTE_LOCK_LENGTH, limit.getNoteLockLength());
        editor.putInt(FIELD_LIMIT_SOUVENIR_TITLE_LENGTH, limit.getSouvenirTitleLength());
        editor.putInt(FIELD_LIMIT_SOUVENIR_FOREIGN_YEAR_COUNT, limit.getSouvenirForeignYearCount());
        editor.putInt(FIELD_LIMIT_TRAVEL_PLACE_COUNT, limit.getTravelPlaceCount());
        editor.putInt(FIELD_LIMIT_TRAVEL_VIDEO_COUNT, limit.getTravelVideoCount());
        editor.putInt(FIELD_LIMIT_TRAVEL_FOOD_COUNT, limit.getTravelFoodCount());
        editor.putInt(FIELD_LIMIT_TRAVEL_MOVIE_COUNT, limit.getTravelMovieCount());
        editor.putInt(FIELD_LIMIT_TRAVEL_ALBUM_COUNT, limit.getTravelAlbumCount());
        editor.putInt(FIELD_LIMIT_TRAVEL_DIARY_COUNT, limit.getTravelDiaryCount());
        editor.putInt(FIELD_LIMIT_WHISPER_CONTENT_LENGTH, limit.getWhisperContentLength());
        editor.putInt(FIELD_LIMIT_WHISPER_CHANNEL_LENGTH, limit.getWhisperChannelLength());
        editor.putInt(FIELD_LIMIT_WORD_CONTENT_LENGTH, limit.getWordContentLength());
        editor.putInt(FIELD_LIMIT_DIARY_CONTENT_LENGTH, limit.getDiaryContentLength());
        editor.putInt(FIELD_LIMIT_ALBUM_TITLE_LENGTH, limit.getAlbumTitleLength());
        editor.putInt(FIELD_LIMIT_PICTURE_PUSH_COUNT, limit.getPicturePushCount());
        editor.putInt(FIELD_LIMIT_AUDIO_TITLE_LENGTH, limit.getAudioTitleLength());
        editor.putInt(FIELD_LIMIT_VIDEO_TITLE_LENGTH, limit.getVideoTitleLength());
        editor.putInt(FIELD_LIMIT_FOOD_TITLE_LENGTH, limit.getFoodTitleLength());
        editor.putInt(FIELD_LIMIT_FOOD_CONTENT_LENGTH, limit.getFoodContentLength());
        editor.putInt(FIELD_LIMIT_TRAVEL_TITLE_LENGTH, limit.getTravelTitleLength());
        editor.putInt(FIELD_LIMIT_TRAVEL_PLACE_CONTENT_LENGTH, limit.getTravelPlaceContentLength());
        editor.putInt(FIELD_LIMIT_GIFT_TITLE_LENGTH, limit.getGiftTitleLength());
        editor.putInt(FIELD_LIMIT_PROMISE_CONTENT_LENGTH, limit.getPromiseContentLength());
        editor.putInt(FIELD_LIMIT_PROMISE_BREAK_CONTENT_LENGTH, limit.getPromiseBreakContentLength());
        editor.putInt(FIELD_LIMIT_ANGRY_CONTENT_LENGTH, limit.getAngryContentLength());
        editor.putInt(FIELD_LIMIT_DREAM_CONTENT_LENGTH, limit.getDreamContentLength());
        editor.putInt(FIELD_LIMIT_AWARD_CONTENT_LENGTH, limit.getAwardContentLength());
        editor.putInt(FIELD_LIMIT_AWARD_RULE_TITLE_LENGTH, limit.getAwardRuleTitleLength());
        editor.putInt(FIELD_LIMIT_AWARD_RULE_SCORE_MAX, limit.getAwardRuleScoreMax());
        editor.putInt(FIELD_LIMIT_MOVIE_TITLE_LENGTH, limit.getMovieTitleLength());
        editor.putInt(FIELD_LIMIT_MOVIE_CONTENT_LENGTH, limit.getMovieContentLength());
        editor.putInt(FIELD_LIMIT_POST_TITLE_LENGTH, limit.getPostTitleLength());
        editor.putInt(FIELD_LIMIT_POST_CONTENT_LENGTH, limit.getPostContentLength());
        editor.putInt(FIELD_LIMIT_POST_SCREEN_REPORT_COUNT, limit.getPostScreenReportCount());
        editor.putInt(FIELD_LIMIT_POST_COMMENT_CONTENT_LENGTH, limit.getPostCommentContentLength());
        editor.putInt(FIELD_LIMIT_POST_COMMENT_SCREEN_REPORT_COUNT, limit.getPostCommentScreenReportCount());
        editor.putString(FIELD_LIMIT_PAY_VIP_GOODS_1_TITLE, limit.getPayVipGoods1Title());
        editor.putInt(FIELD_LIMIT_PAY_VIP_GOODS_1_DAYS, limit.getPayVipGoods1Days());
        editor.putFloat(FIELD_LIMIT_PAY_VIP_GOODS_1_AMOUNT, limit.getPayVipGoods1Amount());
        editor.putString(FIELD_LIMIT_PAY_VIP_GOODS_2_TITLE, limit.getPayVipGoods2Title());
        editor.putInt(FIELD_LIMIT_PAY_VIP_GOODS_2_DAYS, limit.getPayVipGoods2Days());
        editor.putFloat(FIELD_LIMIT_PAY_VIP_GOODS_2_AMOUNT, limit.getPayVipGoods2Amount());
        editor.putString(FIELD_LIMIT_PAY_VIP_GOODS_3_TITLE, limit.getPayVipGoods3Title());
        editor.putInt(FIELD_LIMIT_PAY_VIP_GOODS_3_DAYS, limit.getPayVipGoods3Days());
        editor.putFloat(FIELD_LIMIT_PAY_VIP_GOODS_3_AMOUNT, limit.getPayVipGoods3Amount());
        editor.putString(FIELD_LIMIT_PAY_COIN_GOODS_1_TITLE, limit.getPayCoinGoods1Title());
        editor.putInt(FIELD_LIMIT_PAY_COIN_GOODS_1_COUNT, limit.getPayCoinGoods1Count());
        editor.putFloat(FIELD_LIMIT_PAY_COIN_GOODS_1_AMOUNT, limit.getPayCoinGoods1Amount());
        editor.putString(FIELD_LIMIT_PAY_COIN_GOODS_2_TITLE, limit.getPayCoinGoods2Title());
        editor.putInt(FIELD_LIMIT_PAY_COIN_GOODS_2_COUNT, limit.getPayCoinGoods2Count());
        editor.putFloat(FIELD_LIMIT_PAY_COIN_GOODS_2_AMOUNT, limit.getPayCoinGoods2Amount());
        editor.putString(FIELD_LIMIT_PAY_COIN_GOODS_3_TITLE, limit.getPayCoinGoods3Title());
        editor.putInt(FIELD_LIMIT_PAY_COIN_GOODS_3_COUNT, limit.getPayCoinGoods3Count());
        editor.putFloat(FIELD_LIMIT_PAY_COIN_GOODS_3_AMOUNT, limit.getPayCoinGoods3Amount());
        editor.putInt(FIELD_LIMIT_COIN_SIGN_MIN_COUNT, limit.getCoinSignMinCount());
        editor.putInt(FIELD_LIMIT_COIN_SIGN_MAX_COUNT, limit.getCoinSignMaxCount());
        editor.putInt(FIELD_LIMIT_COIN_SIGN_INCREASE_COUNT, limit.getCoinSignIncreaseCount());
        editor.putInt(FIELD_LIMIT_COIN_AD_BETWEEN_SEC, limit.getCoinAdBetweenSec());
        editor.putInt(FIELD_LIMIT_COIN_AD_WATCH_COUNT, limit.getCoinAdWatchCount());
        editor.putInt(FIELD_LIMIT_COIN_AD_CLICK_COUNT, limit.getCoinAdClickCount());
        editor.putInt(FIELD_LIMIT_COIN_AD_MAX_PER_DAY_COUNT, limit.getCoinAdMaxPerDayCount());
        editor.putInt(FIELD_LIMIT_MATCH_WORK_SCREEN_REPORT_COUNT, limit.getMatchWorkScreenReportCount());
        editor.putInt(FIELD_LIMIT_MATCH_WORK_TITLE_LENGTH, limit.getMatchWorkTitleLength());
        editor.putInt(FIELD_LIMIT_MATCH_WORK_CONTENT_LENGTH, limit.getMatchWorkContentLength());
        editor.apply();
    }

    public static Limit getLimit() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_LIMIT);
        Limit limit = new Limit();
        limit.setSmsCodeLength(sp.getInt(FIELD_LIMIT_SMS_CODE_LENGTH, 6));
        limit.setSmsBetweenSec(sp.getInt(FIELD_LIMIT_SMS_BETWEEN, 60 * 2));
        limit.setSuggestTitleLength(sp.getInt(FIELD_LIMIT_SUGGEST_TITLE_LENGTH, 20));
        limit.setSuggestContentLength(sp.getInt(FIELD_LIMIT_SUGGEST_CONTENT_LENGTH, 200));
        limit.setSuggestCommentContentLength(sp.getInt(FIELD_LIMIT_SUGGEST_COMMENT_CONTENT_LENGTH, 200));
        limit.setCoupleInviteIntervalSec(sp.getLong(FIELD_LIMIT_COUPLE_INVITE_INTERVAL_SEC, 3 * 60));
        limit.setCoupleBreakNeedSec(sp.getLong(FIELD_LIMIT_COUPLE_BREAK_NEED_SEC, 60 * 60 * 24 * 20));
        limit.setCoupleBreakSec(sp.getLong(FIELD_LIMIT_COUPLE_BREAK_SEC, 60 * 60 * 24));
        limit.setCoupleNameLength(sp.getInt(FIELD_LIMIT_COUPLE_NAME_LENGTH, 6));
        limit.setNoteResExpireSec(sp.getLong(FIELD_LIMIT_NOTE_OSS_EXPIRE_SECONDS, 60 * 60 * 24 * 30 * 3));
        limit.setMensesMaxPerMonth(sp.getInt(FIELD_LIMIT_MENSES_MAX_PER_MONTH, 2));
        limit.setMensesMaxCycleDay(sp.getInt(FIELD_LIMIT_MENSES_MAX_CYCLE_DAY, 60));
        limit.setMensesMaxDurationDay(sp.getInt(FIELD_LIMIT_MENSES_MAX_DURATION_DAY, 15));
        limit.setMensesDefaultCycleDay(sp.getInt(FIELD_LIMIT_MENSES_DEFAULT_CYCLE_DAY, 30));
        limit.setMensesDefaultDurationDay(sp.getInt(FIELD_LIMIT_MENSES_DEFAULT_DURATION_DAY, 7));
        limit.setShyMaxPerDay(sp.getInt(FIELD_LIMIT_SHY_MAX_PER_DAY, 5));
        limit.setShySafeLength(sp.getInt(FIELD_LIMIT_SHY_SAFE_LENGTH, 10));
        limit.setShyDescLength(sp.getInt(FIELD_LIMIT_SHY_DESC_LENGTH, 50));
        limit.setSleepMaxPerDay(sp.getInt(FIELD_LIMIT_SLEEP_MAX_PER_DAY, 10));
        limit.setNoteSleepSuccessMinSec(sp.getLong(FIELD_LIMIT_NOTE_SLEEP_SUCCESS_MIN_SEC, 60 * 30));
        limit.setNoteSleepSuccessMaxSec(sp.getLong(FIELD_LIMIT_NOTE_SLEEP_SUCCESS_MAX_SEC, 60 * 60 * 24));
        limit.setNoteLockLength(sp.getInt(FIELD_LIMIT_NOTE_LOCK_LENGTH, 6));
        limit.setSouvenirTitleLength(sp.getInt(FIELD_LIMIT_SOUVENIR_TITLE_LENGTH, 20));
        limit.setSouvenirForeignYearCount(sp.getInt(FIELD_LIMIT_SOUVENIR_FOREIGN_YEAR_COUNT, 1));
        limit.setTravelPlaceCount(sp.getInt(FIELD_LIMIT_TRAVEL_PLACE_COUNT, 1));
        limit.setTravelVideoCount(sp.getInt(FIELD_LIMIT_TRAVEL_VIDEO_COUNT, 1));
        limit.setTravelFoodCount(sp.getInt(FIELD_LIMIT_TRAVEL_FOOD_COUNT, 1));
        limit.setTravelMovieCount(sp.getInt(FIELD_LIMIT_TRAVEL_MOVIE_COUNT, 1));
        limit.setTravelAlbumCount(sp.getInt(FIELD_LIMIT_TRAVEL_ALBUM_COUNT, 1));
        limit.setTravelDiaryCount(sp.getInt(FIELD_LIMIT_TRAVEL_DIARY_COUNT, 1));
        limit.setWhisperContentLength(sp.getInt(FIELD_LIMIT_WHISPER_CONTENT_LENGTH, 100));
        limit.setWhisperChannelLength(sp.getInt(FIELD_LIMIT_WHISPER_CHANNEL_LENGTH, 10));
        limit.setWordContentLength(sp.getInt(FIELD_LIMIT_WORD_CONTENT_LENGTH, 100));
        limit.setDiaryContentLength(sp.getInt(FIELD_LIMIT_DIARY_CONTENT_LENGTH, 2000));
        limit.setAlbumTitleLength(sp.getInt(FIELD_LIMIT_ALBUM_TITLE_LENGTH, 10));
        limit.setPicturePushCount(sp.getInt(FIELD_LIMIT_PICTURE_PUSH_COUNT, 1));
        limit.setAudioTitleLength(sp.getInt(FIELD_LIMIT_AUDIO_TITLE_LENGTH, 20));
        limit.setVideoTitleLength(sp.getInt(FIELD_LIMIT_VIDEO_TITLE_LENGTH, 20));
        limit.setFoodTitleLength(sp.getInt(FIELD_LIMIT_FOOD_TITLE_LENGTH, 20));
        limit.setFoodContentLength(sp.getInt(FIELD_LIMIT_FOOD_CONTENT_LENGTH, 200));
        limit.setTravelTitleLength(sp.getInt(FIELD_LIMIT_TRAVEL_TITLE_LENGTH, 20));
        limit.setTravelPlaceContentLength(sp.getInt(FIELD_LIMIT_TRAVEL_PLACE_CONTENT_LENGTH, 200));
        limit.setGiftTitleLength(sp.getInt(FIELD_LIMIT_GIFT_TITLE_LENGTH, 20));
        limit.setPromiseContentLength(sp.getInt(FIELD_LIMIT_PROMISE_CONTENT_LENGTH, 200));
        limit.setPromiseBreakContentLength(sp.getInt(FIELD_LIMIT_PROMISE_BREAK_CONTENT_LENGTH, 100));
        limit.setAngryContentLength(sp.getInt(FIELD_LIMIT_ANGRY_CONTENT_LENGTH, 200));
        limit.setDreamContentLength(sp.getInt(FIELD_LIMIT_DREAM_CONTENT_LENGTH, 1000));
        limit.setAwardContentLength(sp.getInt(FIELD_LIMIT_AWARD_CONTENT_LENGTH, 100));
        limit.setAwardRuleTitleLength(sp.getInt(FIELD_LIMIT_AWARD_RULE_TITLE_LENGTH, 30));
        limit.setAwardRuleScoreMax(sp.getInt(FIELD_LIMIT_AWARD_RULE_SCORE_MAX, 100));
        limit.setMovieTitleLength(sp.getInt(FIELD_LIMIT_MOVIE_TITLE_LENGTH, 20));
        limit.setMovieContentLength(sp.getInt(FIELD_LIMIT_MOVIE_CONTENT_LENGTH, 200));
        limit.setPostTitleLength(sp.getInt(FIELD_LIMIT_POST_TITLE_LENGTH, 20));
        limit.setPostContentLength(sp.getInt(FIELD_LIMIT_POST_CONTENT_LENGTH, 100));
        limit.setPostScreenReportCount(sp.getInt(FIELD_LIMIT_POST_SCREEN_REPORT_COUNT, 10));
        limit.setPostCommentContentLength(sp.getInt(FIELD_LIMIT_POST_COMMENT_CONTENT_LENGTH, 100));
        limit.setPostCommentScreenReportCount(sp.getInt(FIELD_LIMIT_POST_COMMENT_SCREEN_REPORT_COUNT, 20));
        limit.setPayVipGoods1Title(sp.getString(FIELD_LIMIT_PAY_VIP_GOODS_1_TITLE, ""));
        limit.setPayVipGoods1Days(sp.getInt(FIELD_LIMIT_PAY_VIP_GOODS_1_DAYS, 0));
        limit.setPayVipGoods1Amount(sp.getFloat(FIELD_LIMIT_PAY_VIP_GOODS_1_AMOUNT, 0));
        limit.setPayVipGoods2Title(sp.getString(FIELD_LIMIT_PAY_VIP_GOODS_2_TITLE, ""));
        limit.setPayVipGoods2Days(sp.getInt(FIELD_LIMIT_PAY_VIP_GOODS_2_DAYS, 0));
        limit.setPayVipGoods2Amount(sp.getFloat(FIELD_LIMIT_PAY_VIP_GOODS_2_AMOUNT, 0));
        limit.setPayVipGoods3Title(sp.getString(FIELD_LIMIT_PAY_VIP_GOODS_3_TITLE, ""));
        limit.setPayVipGoods3Days(sp.getInt(FIELD_LIMIT_PAY_VIP_GOODS_3_DAYS, 0));
        limit.setPayVipGoods3Amount(sp.getFloat(FIELD_LIMIT_PAY_VIP_GOODS_3_AMOUNT, 0));
        limit.setPayCoinGoods1Title(sp.getString(FIELD_LIMIT_PAY_COIN_GOODS_1_TITLE, ""));
        limit.setPayCoinGoods1Count(sp.getInt(FIELD_LIMIT_PAY_COIN_GOODS_1_COUNT, 0));
        limit.setPayCoinGoods1Amount(sp.getFloat(FIELD_LIMIT_PAY_COIN_GOODS_1_AMOUNT, 0));
        limit.setPayCoinGoods2Title(sp.getString(FIELD_LIMIT_PAY_COIN_GOODS_2_TITLE, ""));
        limit.setPayCoinGoods2Count(sp.getInt(FIELD_LIMIT_PAY_COIN_GOODS_2_COUNT, 0));
        limit.setPayCoinGoods2Amount(sp.getFloat(FIELD_LIMIT_PAY_COIN_GOODS_2_AMOUNT, 0));
        limit.setPayCoinGoods3Title(sp.getString(FIELD_LIMIT_PAY_COIN_GOODS_3_TITLE, ""));
        limit.setPayCoinGoods3Count(sp.getInt(FIELD_LIMIT_PAY_COIN_GOODS_3_COUNT, 0));
        limit.setPayCoinGoods3Amount(sp.getFloat(FIELD_LIMIT_PAY_COIN_GOODS_3_AMOUNT, 0));
        limit.setCoinSignMinCount(sp.getInt(FIELD_LIMIT_COIN_SIGN_MIN_COUNT, 1));
        limit.setCoinSignMaxCount(sp.getInt(FIELD_LIMIT_COIN_SIGN_MAX_COUNT, 10));
        limit.setCoinSignIncreaseCount(sp.getInt(FIELD_LIMIT_COIN_SIGN_INCREASE_COUNT, 1));
        limit.setCoinAdBetweenSec(sp.getInt(FIELD_LIMIT_COIN_AD_BETWEEN_SEC, 60 * 10));
        limit.setCoinAdWatchCount(sp.getInt(FIELD_LIMIT_COIN_AD_WATCH_COUNT, 1));
        limit.setCoinAdClickCount(sp.getInt(FIELD_LIMIT_COIN_AD_CLICK_COUNT, 2));
        limit.setCoinAdMaxPerDayCount(sp.getInt(FIELD_LIMIT_COIN_AD_MAX_PER_DAY_COUNT, 10));
        limit.setMatchWorkScreenReportCount(sp.getInt(FIELD_LIMIT_MATCH_WORK_SCREEN_REPORT_COUNT, 10));
        limit.setMatchWorkTitleLength(sp.getInt(FIELD_LIMIT_MATCH_WORK_TITLE_LENGTH, 100));
        limit.setMatchWorkContentLength(sp.getInt(FIELD_LIMIT_MATCH_WORK_CONTENT_LENGTH, 200));
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
        editor.putBoolean(FIELD_VIP_LIMIT_ADVERTISE_HIDE, vipLimit.isAdvertiseHide());
        editor.putLong(FIELD_VIP_LIMIT_WALL_PAPER_SIZE, vipLimit.getWallPaperSize());
        editor.putInt(FIELD_VIP_LIMIT_WALL_PAPER_COUNT, vipLimit.getWallPaperCount());
        editor.putBoolean(FIELD_VIP_LIMIT_TRENDS_TOTAL_ENABLE, vipLimit.isNoteTotalEnable());
        editor.putInt(FIELD_VIP_LIMIT_SOUVENIR_COUNT, vipLimit.getSouvenirCount());
        editor.putBoolean(FIELD_VIP_LIMIT_WHISPER_IMG_ENABLE, vipLimit.isWhisperImageEnable());
        editor.putInt(FIELD_VIP_LIMIT_MOVIE_IMG_COUNT, vipLimit.getMovieImageCount());
        editor.putInt(FIELD_VIP_LIMIT_FOOD_IMG_COUNT, vipLimit.getFoodImageCount());
        editor.putInt(FIELD_VIP_LIMIT_GIFT_IMG_COUNT, vipLimit.getGiftImageCount());
        editor.putInt(FIELD_VIP_LIMIT_DIARY_IMG_COUNT, vipLimit.getDiaryImageCount());
        editor.putLong(FIELD_VIP_LIMIT_DIARY_IMG_SIZE, vipLimit.getDiaryImageSize());
        editor.putLong(FIELD_VIP_LIMIT_AUDIO_SIZE, vipLimit.getAudioSize());
        editor.putLong(FIELD_VIP_LIMIT_VIDEO_SIZE, vipLimit.getVideoSize());
        editor.putLong(FIELD_VIP_LIMIT_PICTURE_SIZE, vipLimit.getPictureSize());
        editor.putBoolean(FIELD_VIP_LIMIT_PICTURE_ORIGINAL, vipLimit.isPictureOriginal());
        editor.putInt(FIELD_VIP_LIMIT_PICTURE_TOTAL_COUNT, vipLimit.getPictureTotalCount());
        editor.putInt(FIELD_VIP_LIMIT_TOPIC_POST_IMAGE_COUNT, vipLimit.getTopicPostImageCount());
        editor.apply();
    }

    public static VipLimit getVipLimit() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_VIP_LIMIT);
        VipLimit vipLimit = new VipLimit();
        vipLimit.setAdvertiseHide(sp.getBoolean(FIELD_VIP_LIMIT_ADVERTISE_HIDE, false));
        vipLimit.setWallPaperSize(sp.getLong(FIELD_VIP_LIMIT_WALL_PAPER_SIZE, 0));
        vipLimit.setWallPaperCount(sp.getInt(FIELD_VIP_LIMIT_WALL_PAPER_COUNT, 1));
        vipLimit.setNoteTotalEnable(sp.getBoolean(FIELD_VIP_LIMIT_TRENDS_TOTAL_ENABLE, false));
        vipLimit.setSouvenirCount(sp.getInt(FIELD_VIP_LIMIT_SOUVENIR_COUNT, 1));
        vipLimit.setWhisperImageEnable(sp.getBoolean(FIELD_VIP_LIMIT_WHISPER_IMG_ENABLE, false));
        vipLimit.setMovieImageCount(sp.getInt(FIELD_VIP_LIMIT_MOVIE_IMG_COUNT, 0));
        vipLimit.setFoodImageCount(sp.getInt(FIELD_VIP_LIMIT_FOOD_IMG_COUNT, 0));
        vipLimit.setGiftImageCount(sp.getInt(FIELD_VIP_LIMIT_GIFT_IMG_COUNT, 0));
        vipLimit.setDiaryImageCount(sp.getInt(FIELD_VIP_LIMIT_DIARY_IMG_COUNT, 0));
        vipLimit.setDiaryImageSize(sp.getLong(FIELD_VIP_LIMIT_DIARY_IMG_SIZE, 0));
        vipLimit.setAudioSize(sp.getLong(FIELD_VIP_LIMIT_AUDIO_SIZE, 0));
        vipLimit.setVideoSize(sp.getLong(FIELD_VIP_LIMIT_VIDEO_SIZE, 0));
        vipLimit.setPictureSize(sp.getLong(FIELD_VIP_LIMIT_PICTURE_SIZE, 0));
        vipLimit.setPictureOriginal(sp.getBoolean(FIELD_VIP_LIMIT_PICTURE_ORIGINAL, false));
        vipLimit.setPictureTotalCount(sp.getInt(FIELD_VIP_LIMIT_PICTURE_TOTAL_COUNT, 100));
        vipLimit.setTopicPostImageCount(sp.getInt(FIELD_VIP_LIMIT_TOPIC_POST_IMAGE_COUNT, 0));
        return vipLimit;
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
        if (UserHelper.isEmpty(user)) {
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
        if (user.getId() == 0 || StringUtils.isEmpty(user.getPhone()) || StringUtils.isEmpty(user.getUserToken())) {
            return null;
        }
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
        if (UserHelper.isEmpty(couple)) {
            LogUtils.i(SPHelper.class, "setCouple", "couple == null");
            return;
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_COUPLE).edit();
        editor.putLong(FIELD_CP_ID, couple.getId());
        editor.putLong(FIELD_CP_CREATE_AT, couple.getCreateAt());
        editor.putLong(FIELD_CP_UPDATE_AT, couple.getUpdateAt());
        editor.putLong(FIELD_CP_TOGETHER_AT, couple.getTogetherAt());
        editor.putLong(FIELD_CP_CREATOR_ID, couple.getCreatorId());
        editor.putString(FIELD_CP_CREATOR_NAME, couple.getCreatorName());
        editor.putString(FIELD_CP_CREATOR_AVATAR, couple.getCreatorAvatar());
        editor.putLong(FIELD_CP_INVITEE_ID, couple.getInviteeId());
        editor.putString(FIELD_CP_INVITEE_NAME, couple.getInviteeName());
        editor.putString(FIELD_CP_INVITEE_AVATAR, couple.getInviteeAvatar());
        CoupleState state = couple.getState();
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
        couple.setTogetherAt(sp.getLong(FIELD_CP_TOGETHER_AT, 0));
        couple.setCreatorId(sp.getLong(FIELD_CP_CREATOR_ID, 0));
        couple.setCreatorName(sp.getString(FIELD_CP_CREATOR_NAME, ""));
        couple.setCreatorAvatar(sp.getString(FIELD_CP_CREATOR_AVATAR, ""));
        couple.setInviteeId(sp.getLong(FIELD_CP_INVITEE_ID, 0));
        couple.setInviteeName(sp.getString(FIELD_CP_INVITEE_NAME, ""));
        couple.setInviteeAvatar(sp.getString(FIELD_CP_INVITEE_AVATAR, ""));
        if (couple.getId() == 0 || couple.getCreatorId() == 0 || couple.getInviteeId() == 0) {
            return null;
        }
        CoupleState state = new CoupleState();
        state.setId(sp.getLong(FIELD_CP_STATE_ID, 0));
        state.setCreateAt(sp.getLong(FIELD_CP_STATE_CREATE_AT, 0));
        state.setUpdateAt(sp.getLong(FIELD_CP_STATE_UPDATE_AT, 0));
        state.setUserId(sp.getLong(FIELD_CP_STATE_USER_ID, 0));
        state.setState(sp.getInt(FIELD_CP_STATE_STATE, 0));
        if (state.getId() != 0) {
            couple.setState(state);
        }
        return couple;
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
        if (wallPaper == null) wallPaper = new WallPaper();
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
        editor.putString(FIELD_DRAFT_DIARY_CONTENT_TEXT, diary.getContentText());
        editor.apply();
    }

    public static Diary getDraftDiary() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_DRAFT);
        Diary diary = new Diary();
        diary.setContentText(sp.getString(FIELD_DRAFT_DIARY_CONTENT_TEXT, ""));
        return diary;
    }

    public static void setDraftDream(Dream dream) {
        if (dream == null) {
            dream = new Dream();
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_DRAFT).edit();
        editor.putString(FIELD_DRAFT_DREAM_CONTENT_TEXT, dream.getContentText());
        editor.apply();
    }

    public static Dream getDraftDream() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_DRAFT);
        Dream dream = new Dream();
        dream.setContentText(sp.getString(FIELD_DRAFT_DREAM_CONTENT_TEXT, ""));
        return dream;
    }

    public static void setDraftPost(Post post) {
        if (post == null) {
            post = new Post();
        }
        SharedPreferences.Editor editor = SPUtils.getSharedPreferences(SHARE_DRAFT).edit();
        editor.putString(FIELD_DRAFT_POST_TITLE, post.getTitle());
        editor.putString(FIELD_DRAFT_POST_CONTENT_TEXT, post.getContentText());
        editor.apply();
    }

    public static Post getDraftPost() {
        SharedPreferences sp = SPUtils.getSharedPreferences(SHARE_DRAFT);
        Post post = new Post();
        post.setTitle(sp.getString(FIELD_DRAFT_POST_TITLE, ""));
        post.setContentText(sp.getString(FIELD_DRAFT_POST_CONTENT_TEXT, ""));
        return post;
    }

}
