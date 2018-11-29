package com.jiangzg.lovenote.helper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.jiangzg.base.application.AppInfo;
import com.jiangzg.base.application.AppUtils;
import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.EncryptUtils;
import com.jiangzg.base.common.FileUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.system.DeviceInfo;
import com.jiangzg.base.system.LanguageUtils;
import com.jiangzg.base.system.LocationInfo;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.activity.HomeActivity;
import com.jiangzg.lovenote.activity.couple.CoupleInfoActivity;
import com.jiangzg.lovenote.base.BaseActivity;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.api.API;
import com.jiangzg.lovenote.model.entity.CommonConst;
import com.jiangzg.lovenote.model.entity.CommonCount;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.Entry;
import com.jiangzg.lovenote.model.entity.Limit;
import com.jiangzg.lovenote.model.entity.Lock;
import com.jiangzg.lovenote.model.entity.MatchCoin;
import com.jiangzg.lovenote.model.entity.MatchPoint;
import com.jiangzg.lovenote.model.entity.MatchReport;
import com.jiangzg.lovenote.model.entity.MatchWork;
import com.jiangzg.lovenote.model.entity.ModelShow;
import com.jiangzg.lovenote.model.entity.OssInfo;
import com.jiangzg.lovenote.model.entity.Picture;
import com.jiangzg.lovenote.model.entity.Place;
import com.jiangzg.lovenote.model.entity.PostComment;
import com.jiangzg.lovenote.model.entity.PromiseBreak;
import com.jiangzg.lovenote.model.entity.PushInfo;
import com.jiangzg.lovenote.model.entity.Result;
import com.jiangzg.lovenote.model.entity.Sms;
import com.jiangzg.lovenote.model.entity.Suggest;
import com.jiangzg.lovenote.model.entity.SuggestComment;
import com.jiangzg.lovenote.model.entity.User;
import com.jiangzg.lovenote.model.entity.Version;
import com.jiangzg.lovenote.model.entity.VipLimit;
import com.jiangzg.lovenote.model.entity.WallPaper;
import com.jiangzg.lovenote.model.entity.Whisper;
import com.jiangzg.lovenote.model.entity.Word;
import com.jiangzg.lovenote.receiver.OssRefreshReceiver;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

/**
 * Created by JZG on 2018/3/27.
 * api辅助类
 */
public class ApiHelper {

    // user登录类型
    public static final int LOG_PWD = 1;
    public static final int LOG_VER = 2;
    // user修改类型
    public static final int MODIFY_FORGET = 11;
    public static final int MODIFY_PASSWORD = 12;
    public static final int MODIFY_PHONE = 13;
    public static final int MODIFY_INFO = 14;
    // cp修改类型
    public static final int COUPLE_UPDATE_GOOD = 1; // 更好
    public static final int COUPLE_UPDATE_BAD = 2; // 更坏
    public static final int COUPLE_UPDATE_INFO = 3;// 信息
    // comment order类型
    private static final int LIST_COMMENT_ORDER_POINT = 0;
    private static final int LIST_COMMENT_ORDER_TIME = 1;
    public static final int[] LIST_COMMENT_ORDER_TYPE = new int[]{
            LIST_COMMENT_ORDER_POINT,
            LIST_COMMENT_ORDER_TIME
    };
    public static final String[] LIST_COMMENT_ORDER_SHOW = new String[]{
            MyApp.get().getString(R.string.point),
            MyApp.get().getString(R.string.time)
    };
    // note search类型
    private static final int LIST_NOTE_CP = 0;
    public static final int LIST_NOTE_MY = 1;
    public static final int LIST_NOTE_TA = 2;
    public static final int[] LIST_NOTE_TYPE = new int[]{
            LIST_NOTE_CP,
            LIST_NOTE_MY,
            LIST_NOTE_TA
    };
    public static final String[] LIST_NOTE_SHOW = new String[]{
            MyApp.get().getString(R.string.we_de),
            MyApp.get().getString(R.string.me_de),
            MyApp.get().getString(R.string.ta_de)
    };
    // topic search类型
    public static final int LIST_TOPIC_ALL = 0;
    public static final int LIST_TOPIC_OFFICIAL = 1;
    public static final int LIST_TOPIC_WELL = 2;
    public static final int[] LIST_TOPIC_TYPE = new int[]{
            LIST_TOPIC_ALL,
            LIST_TOPIC_OFFICIAL,
            LIST_TOPIC_WELL
    };
    public static final String[] LIST_TOPIC_SHOW = new String[]{
            MyApp.get().getString(R.string.all),
            MyApp.get().getString(R.string.official),
            MyApp.get().getString(R.string.well)
    };
    // match search类型
    private static final int LIST_MATCH_ORDER_COIN = 0;
    private static final int LIST_MATCH_ORDER_POINT = 1;
    private static final int LIST_MATCH_ORDER_NEW = 2;
    public static final int[] LIST_MATCH_ORDER_TYPE = new int[]{
            LIST_MATCH_ORDER_COIN,
            LIST_MATCH_ORDER_POINT,
            LIST_MATCH_ORDER_NEW
    };
    public static final String[] LIST_MATCH_ORDER_SHOW = new String[]{
            MyApp.get().getString(R.string.coin_board),
            MyApp.get().getString(R.string.point_board),
            MyApp.get().getString(R.string.new_board)
    };

    @SuppressLint("HardwareIds")
    public static Entry getEntryBody() {
        Entry entry = new Entry();
        // deviceId 最好是不需要权限就能获取
        ContentResolver contentResolver = MyApp.get().getContentResolver();
        String deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
        if (StringUtils.isEmpty(deviceId)) {
            deviceId = DeviceInfo.get().getMacAddress();
        }
        entry.setDeviceId(deviceId);
        // 手机介绍
        String manufacturer = DeviceInfo.get().getManufacturer();
        String model = DeviceInfo.get().getModel();
        entry.setDeviceName(manufacturer + " : " + model);
        // 下载渠道
        Bundle appMetaData = AppUtils.getAppMetaData(MyApp.get());
        if (appMetaData != null) {
            String marketChannel = appMetaData.getString("market_channel");
            entry.setMarket(marketChannel);
        }
        // 语言
        entry.setLanguage(LanguageUtils.getLocale().getLanguage());
        // 手机平台
        entry.setPlatform(DeviceInfo.get().getPlatform());
        // 手机版本
        entry.setOsVersion(DeviceInfo.get().getOsVersion());
        // 软件版本
        entry.setAppVersion(AppInfo.get().getVersionCode());
        return entry;
    }

    public static void postEntry(final BaseActivity mActivity) {
        Entry entry = getEntryBody();
        Call<Result> call = new RetrofitHelper().call(API.class).entryPush(entry);
        MaterialDialog loading = mActivity.getLoading(true);
        RetrofitHelper.enqueue(call, loading, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                onEntryFinish(0, 0, mActivity, code, data);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    public static void onEntryFinish(long startTime, long totalWait, final Activity mActivity, int code, Result.Data data) {
        if (data == null) return;
        // user
        User user = data.getUser();
        SPHelper.setMe(user);
        // commonConst
        CommonConst commonConst = data.getCommonConst();
        SPHelper.setCommonConst(commonConst);
        // modelShow
        ModelShow modelShow = data.getModelShow();
        SPHelper.setModelShow(modelShow);
        // limit
        Limit limit = data.getLimit();
        SPHelper.setLimit(limit);
        // vipLimit
        VipLimit vipLimit = data.getVipLimit();
        SPHelper.setVipLimit(vipLimit);
        // ossInfo
        OssInfo ossInfo = data.getOssInfo();
        SPHelper.setOssInfo(ossInfo);
        OssHelper.refreshOssClient();
        OssRefreshReceiver.startAlarm();
        // pushInfo
        PushInfo pushInfo = data.getPushInfo();
        SPHelper.setPushInfo(pushInfo);
        // version
        final ArrayList<Version> versionList = (ArrayList<Version>) data.getVersionList();
        if (versionList == null || versionList.size() <= 0) {
            SPHelper.clearVersion();
            File apkDir = ResHelper.newApkDir();
            List<File> fileList = FileUtils.listFilesAndDirInDir(apkDir, true);
            ResHelper.deleteFileListInBackground(fileList);
            // 日志上传(只限最新版本)
            OssHelper.uploadLog();
        } else {
            SPHelper.setVersion(versionList.get(0));
        }
        // commonCount
        CommonCount commonCount = data.getCommonCount();
        SPHelper.setCommonCount(commonCount);
        // delay
        long endTime = DateUtils.getCurrentLong();
        long between = endTime - startTime;
        if (between >= totalWait) {
            // 间隔时间太大
            HomeActivity.goActivity(mActivity);
        } else {
            // 间隔时间太小
            MyApp.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    HomeActivity.goActivity(mActivity);
                }
            }, totalWait - between);
        }
    }

    // 更新oss信息
    public static void ossInfoUpdate() {
        Call<Result> call = new RetrofitHelper().call(API.class).ossGet();
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                LogUtils.i(ApiHelper.class, "ossInfoUpdate", "oss更新成功");
                OssInfo ossInfo = data.getOssInfo();
                // 刷新ossInfo
                SPHelper.setOssInfo(ossInfo);
                // 刷新ossClient
                OssHelper.refreshOssClient();
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                MyApp.get().getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.w(ApiHelper.class, "ossInfoUpdate", "oss更新失败");
                        ossInfoUpdate(); // 重复更新
                    }
                }, ConstantUtils.MIN);
            }
        });
    }

    public static void showMatchWorksDeleteDialog(Activity activity, final BaseQuickAdapter adapter, final int position) {
        MatchWork item = (MatchWork) adapter.getItem(position);
        if (!item.isMine()) return;
        MaterialDialog dialog = DialogHelper.getBuild(activity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .content(R.string.confirm_del_this_work)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        delMatchWorks(adapter, position);
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialog);
    }

    private static void delMatchWorks(final BaseQuickAdapter adapter, final int position) {
        MatchWork item = (MatchWork) adapter.getItem(position);
        Call<Result> call = new RetrofitHelper().call(API.class).moreMatchWorkDel(item.getId());
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                adapter.remove(position);
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    public static void matchReportAdd(final BaseQuickAdapter adapter, final int position, boolean api) {
        final MatchWork item = (MatchWork) adapter.getItem(position);
        if (item.isReport()) return;
        item.setReport(true);
        adapter.notifyItemChanged(position + adapter.getHeaderLayoutCount());
        if (!api) return;
        MatchReport body = new MatchReport();
        body.setMatchWorkId(item.getId());
        Call<Result> call = new RetrofitHelper().call(API.class).moreMatchReportAdd(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                matchReportAdd(adapter, position, false);
            }
        });
    }

    public static void matchPointToggle(final BaseQuickAdapter adapter, final int position, boolean api) {
        final MatchWork item = (MatchWork) adapter.getItem(position);
        boolean newPoint = !item.isPoint();
        int newPointCount = newPoint ? item.getPointCount() + 1 : item.getPointCount() - 1;
        if (newPointCount < 0) {
            newPointCount = 0;
        }
        item.setPoint(newPoint);
        item.setPointCount(newPointCount);
        adapter.notifyItemChanged(position + adapter.getHeaderLayoutCount());
        if (!api) return;
        MatchPoint body = new MatchPoint();
        body.setMatchWorkId(item.getId());
        Call<Result> call = new RetrofitHelper().call(API.class).moreMatchPointAdd(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
                matchPointToggle(adapter, position, false);
            }
        });
    }

    public static void matchCoinAdd(Activity activity, final BaseQuickAdapter adapter, final int position) {
        String hint = activity.getString(R.string.input_coin_count);
        MaterialDialog dialogName = DialogHelper.getBuild(activity)
                .cancelable(true)
                .canceledOnTouchOutside(true)
                .autoDismiss(true)
                .inputType(InputType.TYPE_CLASS_NUMBER)
                .input(hint, "", false, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        LogUtils.i(CoupleInfoActivity.class, "onInput", input.toString());
                    }
                })
                .inputRange(1, 10)
                .positiveText(R.string.confirm_no_wrong)
                .negativeText(R.string.i_think_again)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        // api
                        EditText editText = dialog.getInputEditText();
                        if (editText != null) {
                            String input = editText.getText().toString();
                            matchCoinApi(adapter, position, input);
                        }
                    }
                })
                .build();
        DialogHelper.showWithAnim(dialogName);
    }

    private static void matchCoinApi(final BaseQuickAdapter adapter, final int position, String input) {
        final MatchWork item = (MatchWork) adapter.getItem(position);
        if (!StringUtils.isNumber(input)) return;
        final int coinCount = Integer.parseInt(input);
        MatchCoin body = new MatchCoin();
        body.setMatchWorkId(item.getId());
        body.setCoinCount(coinCount);
        Call<Result> call = new RetrofitHelper().call(API.class).moreMatchCoinAdd(body);
        RetrofitHelper.enqueue(call, null, new RetrofitHelper.CallBack() {
            @Override
            public void onResponse(int code, String message, Result.Data data) {
                item.setCoinCount(item.getCoinCount() + coinCount);
                adapter.notifyItemChanged(position + adapter.getHeaderLayoutCount());
            }

            @Override
            public void onFailure(int code, String message, Result.Data data) {
            }
        });
    }

    public static Sms getSmsLoginBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(Sms.TYPE_LOGIN);
        sms.setPhone(phone);
        return sms;
    }

    public static Sms getSmsRegisterBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(Sms.TYPE_REGISTER);
        sms.setPhone(phone);
        return sms;
    }

    public static Sms getSmsForgetBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(Sms.TYPE_FORGET);
        sms.setPhone(phone);
        return sms;
    }

    public static Sms getSmsPhoneBody(String phone) {
        Sms sms = new Sms();
        sms.setSendType(Sms.TYPE_PHONE);
        sms.setPhone(phone);
        return sms;
    }

    public static Sms getSmsLockBody(String taPhone) {
        Sms sms = new Sms();
        sms.setSendType(Sms.TYPE_LOCK);
        sms.setPhone(taPhone);
        return sms;
    }

    public static User getUserBody(String phone, String pwd) {
        User user = new User();
        if (!StringUtils.isEmpty(phone)) {
            user.setPhone(phone);
        }
        if (!StringUtils.isEmpty(pwd)) {
            String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
            user.setPassword(md5Pwd);
        }
        return user;
    }

    public static User getUserInfoBody(int sex, long birthday) {
        User user = new User();
        user.setSex(sex);
        user.setBirthday(birthday);
        return user;
    }

    public static Suggest getSuggestAddBody(String title, int kind, String contentText, String contentImg) {
        Suggest suggest = new Suggest();
        suggest.setTitle(title);
        suggest.setKind(kind);
        suggest.setContentText(contentText);
        suggest.setContentImage(contentImg);
        return suggest;
    }

    public static SuggestComment getSuggestCommentAddBody(long suggestId, String content) {
        SuggestComment suggestComment = new SuggestComment();
        suggestComment.setSuggestId(suggestId);
        suggestComment.setContentText(content);
        return suggestComment;
    }

    public static Couple getCoupleUpdateInfo(String avatar, String name) {
        User user = SPHelper.getMe();
        Couple couple = user.getCouple();
        if (couple.getCreatorId() == user.getId()) {
            couple.setInviteeAvatar(avatar);
            couple.setInviteeName(name);
        } else {
            couple.setCreatorAvatar(avatar);
            couple.setCreatorName(name);
        }
        return couple;
    }

    public static WallPaper getWallPaperUpdateBody(List<String> imgList) {
        WallPaper wallPaper = new WallPaper();
        wallPaper.setContentImageList(imgList);
        return wallPaper;
    }

    public static Lock getLockBody(String pwd) {
        Lock lock = new Lock();
        if (!StringUtils.isEmpty(pwd)) {
            String md5Pwd = EncryptUtils.encryptMD5ToString(pwd);
            lock.setPassword(md5Pwd);
        }
        return lock;
    }

    public static Place getPlaceBody(LocationInfo info) {
        if (info == null || (info.getLongitude() == 0 && info.getLatitude() == 0)) {
            info = LocationInfo.getInfo();
        }
        Place place = new Place();
        place.setLongitude(info.getLongitude());
        place.setLatitude(info.getLatitude());
        place.setAddress(info.getAddress());
        place.setCountry(info.getCountry());
        place.setProvince(info.getProvince());
        place.setCity(info.getCity());
        place.setDistrict(info.getDistrict());
        place.setStreet(info.getStreet());
        place.setCityId(info.getCityId());
        return place;
    }

    public static Word getWordBody(String content) {
        Word word = new Word();
        word.setContentText(content);
        return word;
    }

    public static Whisper getWhisperBody(String channel, boolean isImage, String content) {
        Whisper whisper = new Whisper();
        whisper.setChannel(channel);
        whisper.setImage(isImage);
        whisper.setContent(content);
        return whisper;
    }

    public static Picture getPictureBody(long aid, long happenAt, String content, double longitude, double latitude, String address, String cityId) {
        Picture picture = new Picture();
        picture.setAlbumId(aid);
        picture.setHappenAt(happenAt);
        picture.setContentImage(content);
        picture.setLongitude(longitude);
        picture.setLatitude(latitude);
        picture.setAddress(address);
        picture.setCityId(cityId);
        return picture;
    }

    public static PromiseBreak getPromiseBreakBody(long pid, long happenAt, String content) {
        PromiseBreak promiseBreak = new PromiseBreak();
        promiseBreak.setPromiseId(pid);
        promiseBreak.setHappenAt(happenAt);
        promiseBreak.setContentText(content);
        return promiseBreak;
    }

    public static PostComment getPostCommentTextBody(long pid, long tcid, String content) {
        PostComment postComment = new PostComment();
        postComment.setPostId(pid);
        postComment.setToCommentId(tcid);
        postComment.setKind(PostComment.KIND_TEXT);
        postComment.setContentText(content);
        return postComment;
    }

    public static PostComment getPostCommentJabBody(long pid, long tcid) {
        PostComment postComment = new PostComment();
        postComment.setPostId(pid);
        postComment.setToCommentId(tcid);
        postComment.setKind(PostComment.KIND_JAB);
        postComment.setContentText("");
        return postComment;
    }

    public static MatchWork getMatchWifeBody(long mpid, String image) {
        MatchWork work = new MatchWork();
        work.setMatchPeriodId(mpid);
        work.setContentImage(image);
        return work;
    }

    public static MatchWork getMatchLetterBody(long mpid, String title) {
        MatchWork work = new MatchWork();
        work.setMatchPeriodId(mpid);
        work.setTitle(title);
        return work;
    }

    public static MatchWork getMatchDiscussBody(long mpid, String content) {
        MatchWork work = new MatchWork();
        work.setMatchPeriodId(mpid);
        work.setContentText(content);
        return work;
    }

}
