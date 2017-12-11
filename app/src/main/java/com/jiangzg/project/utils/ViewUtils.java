package com.jiangzg.project.utils;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.jiangzg.project.R;

/**
 * Created by JiangZhiGuo on 2016-10-31.
 * describe 符合项目样式的View管理类
 */
public class ViewUtils {

    public static void initTop(Activity activity, String title) {
        TextView tvCenter = (TextView) activity.findViewById(R.id.tvCenter);
        tvCenter.setVisibility(View.VISIBLE);
        tvCenter.setText(title);
    }

}