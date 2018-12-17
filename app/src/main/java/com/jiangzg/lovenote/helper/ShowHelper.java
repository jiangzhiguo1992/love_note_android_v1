package com.jiangzg.lovenote.helper;

import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.Coin;
import com.jiangzg.lovenote.model.entity.Trends;

/**
 * Created by JZG on 2018/12/4.
 */
public class ShowHelper {

    public static String getKindShow(int form) {
        switch (form) {
            case Coin.KIND_ADD_BY_SYS:
                return MyApp.get().getString(R.string.sys_change);
            case Coin.KIND_ADD_BY_PLAY_PAY:
                return MyApp.get().getString(R.string.pay);
            case Coin.KIND_ADD_BY_SIGN_DAY:
                return MyApp.get().getString(R.string.sign);
            case Coin.KIND_ADD_BY_MATCH_POST:
                return MyApp.get().getString(R.string.nav_match);
            case Coin.KIND_SUB_BY_MATCH_UP:
                return MyApp.get().getString(R.string.nav_match);
            case Coin.KIND_SUB_BY_WISH_UP:
                return MyApp.get().getString(R.string.nav_wish);
            case Coin.KIND_SUB_BY_CARD_UP:
                return MyApp.get().getString(R.string.nav_postcard);
        }
        return MyApp.get().getString(R.string.unknown_kind);
    }

    public static String getActShow(int act, long conId) {
        switch (act) {
            case Trends.TRENDS_ACT_TYPE_INSERT: // 添加
                return MyApp.get().getString(R.string.add);
            case Trends.TRENDS_ACT_TYPE_DELETE: // 删除
                return MyApp.get().getString(R.string.delete);
            case Trends.TRENDS_ACT_TYPE_UPDATE: // 修改
                return MyApp.get().getString(R.string.modify);
            case Trends.TRENDS_ACT_TYPE_QUERY: // 进入/浏览
                if (conId <= Trends.TRENDS_CON_ID_LIST) {
                    return MyApp.get().getString(R.string.go_in);
                } else {
                    return MyApp.get().getString(R.string.browse);
                }
        }
        return MyApp.get().getString(R.string.un_know);
    }
}
