package com.jiangzg.ita.helper;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.ita.domain.Couple;
import com.jiangzg.ita.domain.User;

/**
 * Created by JZG on 2018/4/3.
 * 检查类
 */
public class CheckHelper {

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

    public static boolean isNullCouple() {
        Couple couple = SPHelper.getCouple();
        return isNullCouple(couple);
    }

    public static boolean isCoupleBreak() {
        Couple couple = SPHelper.getCouple();
        return isCoupleBreak(couple);
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

    public static boolean isCoupleBreaking() {
        Couple couple = SPHelper.getCouple();
        return isCoupleBreaking(couple);
    }

}
