package com.jiangzg.ita.helper;

import android.app.Activity;

import com.jiangzg.base.time.DateUtils;
import com.jiangzg.ita.activity.HomeActivity;
import com.jiangzg.ita.base.MyApp;
import com.jiangzg.ita.broadcast.OssReceiver;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.OssInfo;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.domain.Version;
import com.jiangzg.ita.domain.VipPower;
import com.jiangzg.ita.third.OssHelper;

import java.util.ArrayList;

/**
 * Created by JZG on 2018/3/27.
 * api辅助类
 */
public class ApiHelper {

    public static void toRefreshOss() {

        // todo api
        OssHelper.refreshOssClient();
    }

    public static void onEntryFinish(Activity mActivity, int code, Result.Data data) {
        onEntryFinish(0, 0, mActivity, code, data);
    }

    public static void onEntryFinish(long startTime, long totalWait, final Activity mActivity, int code, Result.Data data) {
        // user
        User user = data.getUser();
        Couple couple = data.getCouple();
        SPHelper.setUser(user);
        SPHelper.setCouple(couple);
        // version
        final ArrayList<Version> versionList = (ArrayList<Version>) data.getVersionList();
        // oss
        OssInfo ossInfo = data.getOssInfo();
        SPHelper.setOssInfo(ossInfo);
        OssHelper.refreshOssClient();
        OssReceiver.startAlarm();
        // todo notice
        int noticeNoRead = data.getNoticeNoRead();
        // todo vip
        VipPower vipPower = data.getVipPower();

        long endTime = DateUtils.getCurrentLong();
        long between = endTime - startTime;
        if (between >= totalWait) {
            // 间隔时间太大
            HomeActivity.goActivity(mActivity, versionList);
        } else {
            // 间隔时间太小
            MyApp.get().getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    HomeActivity.goActivity(mActivity, versionList);
                }
            }, totalWait - between);
        }
    }
}
