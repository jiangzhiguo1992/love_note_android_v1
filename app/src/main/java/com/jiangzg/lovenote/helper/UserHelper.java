package com.jiangzg.lovenote.helper;

import com.jiangzg.base.common.DateUtils;
import com.jiangzg.base.common.StringUtils;
import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.Couple;
import com.jiangzg.lovenote.model.entity.CoupleState;
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
    public static long getMyId(User user) {
        if (isEmpty(user)) return 0;
        return user.getId();
    }

    public static long getTaId(User user) {
        if (isEmpty(user)) return 0;
        return UserHelper.getTaId(user.getCouple(), user.getId());
    }

    public static long getTaId(Couple couple, long mid) {
        if (isEmpty(couple)) return 0;
        if (mid == couple.getCreatorId()) {
            return couple.getInviteeId();
        }
        return couple.getCreatorId();
    }

    // sexAvatar
    public static int getSexAvatarResId(User user) {
        if (isEmpty(user)) return R.mipmap.ic_account_circle_grey_48dp;
        if (user.getSex() == User.SEX_BOY) {
            return R.mipmap.img_boy_circle;
        } else if (user.getSex() == User.SEX_GIRL) {
            return R.mipmap.img_girl_circle;
        }
        return R.mipmap.ic_account_circle_grey_48dp;
    }

    // 昵称
    public static String getMyName(User user) {
        if (isEmpty(user)) return MyApp.get().getString(R.string.now_null_nickname);
        return getName(user, user.getId());
    }

    public static String getTaName(User user) {
        if (isEmpty(user)) return MyApp.get().getString(R.string.now_null_nickname);
        return getName(user, getTaId(user));
    }

    public static String getName(User user, long uid) {
        if (isEmpty(user)) return MyApp.get().getString(R.string.now_null_nickname);
        return UserHelper.getName(user.getCouple(), uid);
    }

    public static String getName(Couple couple, long uid) {
        if (isEmpty(couple)) return MyApp.get().getString(R.string.now_null_nickname);
        if (uid == couple.getCreatorId()) {
            String creatorName = couple.getCreatorName();
            return StringUtils.isEmpty(creatorName) ? MyApp.get().getString(R.string.now_null_nickname) : creatorName;
        }
        String inviteeName = couple.getInviteeName();
        return StringUtils.isEmpty(inviteeName) ? MyApp.get().getString(R.string.now_null_nickname) : inviteeName;
    }

    // 头像
    public static String getMyAvatar(User user) {
        if (isEmpty(user)) return "";
        return getAvatar(user, user.getId());
    }

    public static String getTaAvatar(User user) {
        if (isEmpty(user)) return "";
        return getAvatar(user, getTaId(user));
    }

    public static String getAvatar(User user, long uid) {
        if (isEmpty(user)) return "";
        return UserHelper.getAvatar(user.getCouple(), uid);
    }

    public static String getAvatar(Couple couple, long uid) {
        if (isEmpty(couple)) return "";
        if (uid == couple.getCreatorId()) {
            return couple.getCreatorAvatar();
        }
        return couple.getInviteeAvatar();
    }

    // couple
    public static boolean isCoupleBreak(Couple couple) {
        if (isEmpty(couple)) return true;
        CoupleState coupleState = couple.getState();
        if (coupleState == null) return true;
        int state = coupleState.getState();
        if (state == CoupleState.STATUS_INVITE || state == CoupleState.STATUS_INVITE_CANCEL
                || state == CoupleState.STATUS_INVITE_REJECT || state == CoupleState.STATUS_BREAK_ACCEPT) {
            return true;
        } else if (state == CoupleState.STATUS_BREAK) {
            return !isCoupleBreaking(couple);
        }
        return state != CoupleState.STATUS_TOGETHER;
    }

    public static boolean isCoupleBreaking(Couple couple) {
        if (isEmpty(couple)) return false;
        CoupleState coupleState = couple.getState();
        if (coupleState == null) return false;
        int state = coupleState.getState();
        if (state == CoupleState.STATUS_BREAK) {
            return getCoupleBreakCountDown(couple) > 0;
        }
        return false;
    }

    public static long getCoupleBreakCountDown(Couple couple) {
        if (isEmpty(couple)) return -1;
        CoupleState state = couple.getState();
        if (state == null) return -1;
        long breakAt = state.getCreateAt() + SPHelper.getLimit().getCoupleBreakSec();
        long currentAt = DateUtils.getCurrentLong() / TimeUnit.SEC;
        long countDown = breakAt - currentAt;
        return countDown > 0 ? countDown : -1;
    }

}
