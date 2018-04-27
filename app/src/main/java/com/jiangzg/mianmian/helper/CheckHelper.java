package com.jiangzg.mianmian.helper;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.LogUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.mianmian.domain.Couple;
import com.jiangzg.mianmian.domain.User;

import java.io.File;
import java.util.List;

/**
 * Created by JZG on 2018/4/3.
 * 检查类
 */
public class CheckHelper {

    private static final String LOG_TAG = "CheckHelper";

    public static boolean noLogin() {
        String userToken = SPHelper.getUser().getUserToken();
        return StringUtils.isEmpty(userToken);
    }

    public static boolean canUserInfo() {
        User user = SPHelper.getUser();
        int sex = user.getSex();
        long birthday = user.getBirthday();
        boolean noSex = sex != User.SEX_BOY && sex != User.SEX_GIRL;
        boolean noBirth = birthday == 0;
        return noSex || noBirth;
    }

    public static boolean isNullCouple(Couple couple) {
        return couple == null || couple.getId() <= 0 || couple.getCreatorId() <= 0 || couple.getInviteeId() <= 0;
    }

    public static boolean isCoupleBreak(Couple couple) {
        if (isNullCouple(couple)) return true;
        int status = couple.getStatus();
        if (status == Couple.CoupleStatusInviteeReject ||
                status == Couple.CoupleStatusComplexReject ||
                status == Couple.CoupleStatusInvitee ||
                status == Couple.CoupleStatusComplex) {
            return true;
        } else if (status == Couple.CoupleStatusTogether) {
            return false;
        } else if (status == Couple.CoupleStatusBreak) {
            long updatedAt = couple.getUpdateAt();
            long currentLong = DateUtils.getCurrentLong() / ConstantUtils.SEC;
            return updatedAt + Couple.BreakCountDown <= currentLong;
        }
        return true;
    }

    public static boolean isCoupleBreaking(Couple couple) {
        if (isNullCouple(couple)) return false;
        if (couple.getStatus() == Couple.CoupleStatusBreak) {
            long updatedAt = couple.getUpdateAt();
            long currentLong = DateUtils.getCurrentLong() / ConstantUtils.SEC;
            return updatedAt + Couple.BreakCountDown > currentLong;
        }
        return false;
    }

    public static boolean isCoupleCreator(Couple couple, long uid) {
        return !CheckHelper.isNullCouple(couple) && couple.getCreatorId() == uid;
    }

    // 检查wp是否已下载
    public static boolean isWallPaperExists(String ossKey) {
        // 获取wp目录里的所有文件
        List<File> fileList = ResHelper.getWallPaperDirFiles();
        for (File file : fileList) {
            // 文件不存在则直接检查下一个文件
            if (file == null) {
                LogUtils.i(LOG_TAG, "isWallPaperExists: file == null");
                continue;
            }
            // 检查本地是不是已下载
            String name = ConvertHelper.getNameByOssPath(ossKey);
            if (file.getName().trim().equals(name)) {
                return true;
            }
        }
        return false;
    }

    // 检查avatar是否已下载
    public static boolean isAvatarExists(String ossKey) {
        // 获取avatar目录里的所有文件
        List<File> fileList = ResHelper.getAvatarDirFiles();
        for (File file : fileList) {
            // 文件不存在则直接检查下一个文件
            if (file == null) {
                LogUtils.i(LOG_TAG, "isAvatarExists: file == null");
                continue;
            }
            // 检查本地是不是已下载
            String name = ConvertHelper.getNameByOssPath(ossKey);
            if (file.getName().trim().equals(name)) {
                return true;
            }
        }
        return false;
    }

}
