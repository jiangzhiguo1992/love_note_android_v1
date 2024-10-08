package com.jiangzg.lovenote.helper.common;

import com.jiangzg.base.common.TimeUnit;
import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.main.MyApp;
import com.jiangzg.lovenote.model.entity.Post;
import com.jiangzg.lovenote.model.entity.PostKindInfo;
import com.jiangzg.lovenote.model.entity.PostSubKindInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by JZG on 2018/8/6.
 * ShowHelper
 */
public class ShowHelper {

    public static String getShowCount2Thousand(int count) {
        int unit = 10000; // 万
        if (Math.abs(count) < unit) {
            return String.valueOf(count);
        }
        return String.format(Locale.getDefault(), MyApp.get().getString(R.string.holder_thousand_point1), (double) count / (double) unit);
    }

    public static String getShowDistance(float distance) {
        String distanceShow = "-m";
        if (distance >= 1000 * 100) {
            float km = distance / 1000;
            distanceShow = String.format(Locale.getDefault(), "%.0fkm", km);
        } else if (distance >= 1000) {
            float km = distance / 1000;
            distanceShow = String.format(Locale.getDefault(), "%.1fkm", km);
        } else if (distance >= 0) {
            distanceShow = String.format(Locale.getDefault(), "%.0fm", distance);
        }
        return distanceShow;
    }

    public static String getDurationShow(int duration) {
        String secShow;
        long sec = duration % (TimeUnit.MIN / TimeUnit.SEC);
        if (sec < 1) {
            secShow = "00";
        } else if (sec < 10) {
            secShow = "0" + sec;
        } else {
            secShow = String.valueOf(sec);
        }
        String minShow;
        long min = duration / (TimeUnit.MIN / TimeUnit.SEC);
        if (min < 1) {
            minShow = "00";
        } else if (min < 10) {
            minShow = "0" + min;
        } else {
            minShow = String.valueOf(min);
        }
        return minShow + ":" + secShow;
    }

    public static String getBetweenTimeGoneShow(long between) {
        String time = MyApp.get().getString(R.string.just_now);
        if (between >= TimeUnit.YEAR) {
            time = (between / TimeUnit.YEAR) + MyApp.get().getString(R.string.year) + MyApp.get().getString(R.string.before);
        } else if (between >= TimeUnit.MONTH) {
            time = (between / TimeUnit.MONTH) + MyApp.get().getString(R.string.month) + MyApp.get().getString(R.string.before);
        } else if (between >= TimeUnit.DAY) {
            time = (between / TimeUnit.DAY) + MyApp.get().getString(R.string.dayT) + MyApp.get().getString(R.string.before);
        } else if (between >= TimeUnit.HOUR) {
            time = (between / TimeUnit.HOUR) + MyApp.get().getString(R.string.hour) + MyApp.get().getString(R.string.before);
        } else if (between >= TimeUnit.MIN * 5) {
            time = (between / TimeUnit.MIN) + MyApp.get().getString(R.string.minute) + MyApp.get().getString(R.string.before);
        }
        return time;
    }

    public static List<String> getPostTagListShow(Post post, boolean kind, boolean subKind) {
        List<String> showList = new ArrayList<>();
        if (post == null) return showList;
        if (post.isTop()) showList.add(MyApp.get().getString(R.string.top));
        if (kind || subKind) {
            PostKindInfo kindInfo = ListHelper.getPostKindInfo(post.getKind());
            PostSubKindInfo subKindInfo = ListHelper.getPostSubKindInfo(kindInfo, post.getSubKind());
            if (kind && kindInfo != null) {
                showList.add(kindInfo.getName());
            }
            if (subKind && subKindInfo != null) {
                showList.add(subKindInfo.getName());
            }
        }
        if (post.isOfficial()) showList.add(MyApp.get().getString(R.string.administrators));
        if (post.isWell()) showList.add(MyApp.get().getString(R.string.well));
        if (post.isHot()) showList.add(MyApp.get().getString(R.string.hot));
        if (post.isMine()) showList.add(MyApp.get().getString(R.string.me_de));
        if (post.isOur() && !post.isMine()) showList.add(MyApp.get().getString(R.string.ta_de));
        if (post.isReport()) showList.add(MyApp.get().getString(R.string.already_report));
        return showList;
    }
}
