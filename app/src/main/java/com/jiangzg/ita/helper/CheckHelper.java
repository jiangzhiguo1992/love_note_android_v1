package com.jiangzg.ita.helper;

import com.jiangzg.base.common.StringUtils;
import com.jiangzg.ita.domain.Couple;

/**
 * Created by JZG on 2018/4/3.
 * 检查类
 */
public class CheckHelper {

    public static boolean noLogin() {
        String userToken = SPHelper.getUser().getUserToken();
        return StringUtils.isEmpty(userToken);
    }

    public static boolean isNullCouple() {
        Couple couple = SPHelper.getCouple();
        return isNullCouple(couple);
    }

    public static boolean isNullCouple(Couple couple) {
        return couple == null || couple.getId() <= 0 || couple.getCreatorId() <= 0 || couple.getInviteeId() <= 0;
    }

    // todo
    public static boolean isCoupleBreaking() {
        //Couple couple = SPHelper.getCouple();
        //if (isNullCouple(couple)) return false;
        //if (couple.getStatus() == )
        return false;
    }

}
