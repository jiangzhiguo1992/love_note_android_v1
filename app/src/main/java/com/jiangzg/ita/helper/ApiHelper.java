package com.jiangzg.ita.helper;

import android.app.Activity;

import com.jiangzg.ita.activity.HomeActivity;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.OssInfo;
import com.jiangzg.ita.domain.Result;
import com.jiangzg.ita.domain.User;
import com.jiangzg.ita.domain.Version;
import com.jiangzg.ita.domain.VipPower;
import com.jiangzg.ita.service.UpdateService;

import java.util.List;

/**
 * Created by JZG on 2018/3/27.
 * api辅助类
 */
public class ApiHelper {

    public static void onEntryFinish(Activity mActivity, int code, Result.Data data) {
        // user
        User user = data.getUser();
        Couple couple = data.getCouple();
        PrefHelper.setUser(user);
        PrefHelper.setCouple(couple);
        // version
        List<Version> versionList = data.getVersionList();
        UpdateService.showUpdateDialog(versionList);
        // todo oss
        OssInfo ossInfo = data.getOssInfo();
        // todo notice
        int noticeNoRead = data.getNoticeNoRead();
        // todo vip
        VipPower vipPower = data.getVipPower();

        HomeActivity.goActivity(mActivity);
    }
}
