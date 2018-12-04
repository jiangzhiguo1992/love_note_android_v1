package com.jiangzg.lovenote.helper;

import com.jiangzg.base.common.ConstantUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.time.DateUtils;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.User;

/**
 * Created by JZG on 2018/12/4.
 * UserHelper
 */
public class UserHelper {

    public static boolean isEmpty(User user) {
        return (user == null || user.getId() == 0);
    }

    public static boolean isEmpty(Couple couple) {
        return (couple == null || couple.getId() == 0 || couple.getCreatorId() == 0 || couple.getInviteeId() == 0);
    }

    // ID
    public long getTaId(User user) {
        if (isEmpty(user)) return 0;
        Couple couple = user.getCouple();
        return UserHelper.getTaId(couple, user.getId());
    }

    public static long getTaId(Couple couple, long mid) {
        if (isEmpty(couple)) return 0;
        if (mid == couple.getCreatorId()) {
            return couple.getInviteeId();
        } else {
            return couple.getCreatorId();
        }
    }

    // sex
    public static int getSexResCircleSmall(User user) {
        if (isEmpty(user)) return 0;
        return getSexResCircleSmall(user.getSex());
    }

    public static int getSexResCircleSmall(int sex) {
        if (sex == User.SEX_BOY) {
            return R.mipmap.ic_sex_boy_circle;
        } else if (sex == User.SEX_GIRL) {
            return R.mipmap.ic_sex_girl_circle;
        }
        return 0;
    }

    // 昵称
    public String getMyName(User user) {
        if (isEmpty(user)) return "";
        return getName(user, user.getId());
    }

    public String getTaName(User user) {
        if (isEmpty(user)) return "";
        return getName(user, getTaId(user));
    }

    public String getName(User user, long uid) {
        if (isEmpty(user)) return "";
        return UserHelper.getName(user.getCouple(), uid);
    }

    public static String getName(Couple couple, long uid) {
        if (isEmpty(couple)) return "";
        if (uid == couple.getCreatorId()) {
            String creatorName = couple.getCreatorName();
            return StringUtils.isEmpty(creatorName) ? MyApp.get().getString(R.string.now_null_nickname) : creatorName;
        } else {
            String inviteeName = couple.getInviteeName();
            return StringUtils.isEmpty(inviteeName) ? MyApp.get().getString(R.string.now_null_nickname) : inviteeName;
        }
    }

    // 头像
    public String getMyAvatar(User user) {
        if (isEmpty(user)) return "";
        return getAvatar(user, user.getId());
    }

    public String getTaAvatar(User user) {
        if (isEmpty(user)) return "";
        return getAvatar(user, getTaId(user));
    }

    public String getAvatar(User user, long uid) {
        if (isEmpty(user)) return "";
        return UserHelper.getAvatar(user.getCouple(), uid);
    }

    public static String getAvatar(Couple couple, long uid) {
        if (isEmpty(couple)) return "";
        if (uid == couple.getCreatorId()) {
            return couple.getCreatorAvatar();
        } else {
            return couple.getInviteeAvatar();
        }
    }

    // couple
    public static boolean isCoupleBreak(Couple couple) {
        if (isEmpty(couple)) return true;
        Couple.State coupleState = couple.getState();
        if (coupleState == null) return true;
        int state = coupleState.getState();
        if (state == Couple.State.STATUS_INVITE || state == Couple.State.STATUS_INVITE_CANCEL ||
                state == Couple.State.STATUS_INVITE_REJECT || state == Couple.State.STATUS_BREAK_ACCEPT) {
            return true;
        } else if (state == Couple.State.STATUS_BREAK) {
            return !isCoupleBreaking(couple);
        } else return state != Couple.State.STATUS_TOGETHER;
    }

    public static boolean isCoupleBreaking(Couple couple) {
        if (isEmpty(couple)) return false;
        Couple.State coupleState = couple.getState();
        if (coupleState == null) return false;
        int state = coupleState.getState();
        if (state == Couple.State.STATUS_BREAK) {
            return getCoupleBreakCountDown(couple) > 0;
        }
        return false;
    }

    public static long getCoupleBreakCountDown(Couple couple) {
        if (isEmpty(couple)) return -1;
        Couple.State state = couple.getState();
        if (state == null) return -1;
        long breakAt = state.getCreateAt() + SPHelper.getLimit().getCoupleBreakSec();
        long currentAt = DateUtils.getCurrentLong() / ConstantUtils.SEC;
        long countDown = breakAt - currentAt;
        return countDown > 0 ? countDown : -1;
    }

}
