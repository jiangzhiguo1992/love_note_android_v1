package com.jiangzg.lovenote.helper;

import com.jiangzg.lovenote.R;
import com.jiangzg.lovenote.base.MyApp;

import java.util.Locale;

/**
 * Created by JZG on 2018/8/6.
 * CountHelper
 */
public class CountHelper {

    // getShowCount
    public static String getShowCount2Thousand(int count) {
        int unit = 10000; // 万
        if (count < unit) {
            return String.valueOf(count);
        }
        return String.format(Locale.getDefault(), MyApp.get().getString(R.string.holder_thousand), count / unit);
    }

}
